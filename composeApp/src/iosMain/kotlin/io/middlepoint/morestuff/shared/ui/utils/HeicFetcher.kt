package io.middlepoint.morestuff.shared.ui.utils

import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.Buffer
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Photos.PHAsset
import platform.Photos.PHImageManager
import platform.Photos.PHImageRequestOptions
import platform.Photos.PHImageRequestOptionsDeliveryModeHighQualityFormat
import platform.Photos.PHImageRequestOptionsResizeModeFast
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy
import kotlin.coroutines.resume

actual fun getPlatformFetcherFactory(): Fetcher.Factory<Any> = PhAssetFetcherFactory()

class PhAssetFetcherFactory : Fetcher.Factory<Any> {
  private val logger = Logger.withTag("PhAssetFetcherFactory")

  override fun create(data: Any, options: Options, imageLoader: ImageLoader): Fetcher? {
    val canFetchData = when (data) {
      is Uri -> {
        val uriString = data.toString()
        val isPhasset = uriString.startsWith("phasset://")
        val isImage = uriString.endsWith(".heic", ignoreCase = true) ||
            uriString.endsWith(".jpeg", ignoreCase = true) ||
            uriString.endsWith(".jpg", ignoreCase = true) ||
            uriString.endsWith(".png", ignoreCase = true) ||
            uriString.endsWith(".gif", ignoreCase = true) ||
            uriString.endsWith(".webp", ignoreCase = true)
        val canHandle = isPhasset || isImage
        logger.d { "Analizando Uri: isPhasset=$isPhasset, isImage=$isImage, canHandle=$canHandle" }
        canHandle
      }

      is String -> {
        val isImage = data.endsWith(".heic", ignoreCase = true) ||
            data.endsWith(".jpeg", ignoreCase = true) ||
            data.endsWith(".jpg", ignoreCase = true) ||
            data.endsWith(".png", ignoreCase = true) ||
            data.endsWith(".gif", ignoreCase = true) ||
            data.endsWith(".webp", ignoreCase = true)
        logger.d { "Analizando String: isImage=$isImage" }
        isImage
      }

      else -> {
        logger.d { "No puede manejar tipo: ${data::class.simpleName}" }
        false
      }
    }

    logger.d { "canFetchData=$canFetchData para $data" }

    return if (canFetchData) {
      PhAssetFetcher(data, options)
    } else null
  }
}

class PhAssetFetcher(data: Any, options: Options) : PlatformFetcher(data, options) {
  private val logger = Logger.withTag("PhAssetFetcher")

  companion object {
    private const val JPEG_QUALITY = 70
  }

  override suspend fun fetch(): FetchResult? {
    logger.d { "Iniciando fetch para: $data" }

    try {
      val uriString = when (data) {
        is Uri -> data.toString()
        is String -> data
        else -> {
          logger.e { "Tipo de datos no soportado: ${data::class.simpleName}" }
          throw IllegalArgumentException("Tipo de datos no soportado: ${data::class}")
        }
      }

      return if (uriString.startsWith("phasset://")) {
        logger.d { "Procesando como PHAsset: $uriString" }
        fetchFromPhotoLibrary(uriString)
      } else {
        logger.d { "Procesando como archivo: $uriString" }
        fetchFromFilesystem(uriString)
      }
    } catch (e: Exception) {
      logger.e(e) { "Error durante fetch: ${e.message}" }
      throw e
    }
  }

  private suspend fun fetchFromPhotoLibrary(uriString: String): FetchResult? {
    val localIdentifier = uriString.removePrefix("phasset://")
    logger.d { "Buscando PHAsset con identificador: $localIdentifier" }

    val asset = fetchAssetByIdentifier(localIdentifier)
    if (asset == null) {
      logger.e { "PHAsset no encontrado: $localIdentifier" }
      throw IllegalArgumentException("PHAsset no encontrado: $localIdentifier")
    }

    logger.d { "Encontró PHAsset: ${asset.localIdentifier}" }

    val imageData = fetchImageData(asset)
    if (imageData == null) {
      logger.e { "No se pudo obtener datos de la imagen desde PHAsset" }
      return null
    }

    logger.d { "Datos de imagen obtenidos, tamaño original: ${imageData.length}" }

    try {
      val originalBytes = imageData.toByteArray()
      val compressedBytes = compressImage(originalBytes, JPEG_QUALITY)
      logger.d { "Imagen comprimida, tamaño original: ${originalBytes.size}, tamaño final: ${compressedBytes.size}" }

      val source = ImageSource(
        source = Buffer().apply { write(compressedBytes) },
        fileSystem = options.fileSystem,
      )

      return SourceFetchResult(
        source = source,
        mimeType = "image/jpeg",
        dataSource = DataSource.DISK
      )
    } catch (e: Exception) {
      logger.e(e) { "Error al comprimir imagen: ${e.message}" }
      throw e
    }
  }

  private fun fetchFromFilesystem(filePath: String): FetchResult {
    logger.d { "Cargando archivo: $filePath" }

    try {
      val fileExists = NSFileManager.defaultManager().fileExistsAtPath(filePath)
      if (!fileExists) {
        logger.e { "El archivo no existe: $filePath" }
        throw IllegalArgumentException("El archivo no existe: $filePath")
      }

      val fileURL = NSURL.fileURLWithPath(filePath)
      val imageData = NSData.dataWithContentsOfURL(fileURL)
      if (imageData == null) {
        logger.e { "No se pudo cargar los datos del archivo: $filePath" }
        throw IllegalArgumentException("No se pudo cargar los datos del archivo: $filePath")
      }

      logger.d { "Datos del archivo cargados, tamaño original: ${imageData.length}" }

      val originalBytes = imageData.toByteArray()
      val compressedBytes = compressImage(originalBytes, JPEG_QUALITY)

      val compressionRatio = if (originalBytes.isNotEmpty()) {
        (100 - (compressedBytes.size.toDouble() / originalBytes.size.toDouble() * 100)).toInt()
      } else 0

      logger.d { "Imagen comprimida, reducción: $compressionRatio%, tamaño final: ${compressedBytes.size}" }

      val source = ImageSource(
        source = Buffer().apply { write(compressedBytes) },
        fileSystem = options.fileSystem,
      )

      return SourceFetchResult(
        source = source,
        mimeType = "image/jpeg",
        dataSource = DataSource.DISK
      )
    } catch (e: Exception) {
      logger.e(e) { "Error procesando archivo: ${e.message}" }
      throw e
    }
  }

  // Método de compresión simplificado basado en el ejemplo
  @OptIn(ExperimentalForeignApi::class)
  private fun compressImage(bytes: ByteArray, quality: Int): ByteArray {
    logger.d { "Iniciando compresión con quality=$quality" }

    try {
      val nsData = bytes.toNSData()

      val uiImage = UIImage(data = nsData)
      if (uiImage == null) {
        logger.w { "Falló decodificar la imagen. Devolviendo bytes originales." }
        return bytes
      }


      val compressionQuality = (quality.coerceIn(0, 100) / 100.0)
      val jpegData = UIImageJPEGRepresentation(uiImage, compressionQuality)
      if (jpegData == null) {
        logger.w { "Falló la compresión JPEG. Devolviendo bytes originales." }
        return bytes
      }

      val compressedBytes = jpegData.toByteArray()
      logger.d { "Imagen comprimida. Bytes originales: ${bytes.size}, bytes comprimidos: ${compressedBytes.size}" }
      return compressedBytes
    } catch (e: Exception) {
      logger.e(e) { "Error comprimiendo imagen: ${e.message}" }
      return bytes // Devolver bytes originales en caso de error
    }
  }

  private fun fetchAssetByIdentifier(localIdentifier: String): PHAsset? {
    val fetchResult = PHAsset.fetchAssetsWithLocalIdentifiers(listOf(localIdentifier), null)
    if (fetchResult.count().toInt() == 0) {
      logger.d { "No encontró PHAssets con identificador: $localIdentifier" }
      return null
    }
    return fetchResult.firstObject() as? PHAsset
  }

  private suspend fun fetchImageData(asset: PHAsset): NSData? =
    suspendCancellableCoroutine { continuation ->
      val options = PHImageRequestOptions().apply {
        this.networkAccessAllowed = true
        this.deliveryMode = PHImageRequestOptionsDeliveryModeHighQualityFormat
        resizeMode = PHImageRequestOptionsResizeModeFast
      }

      logger.d { "Solicitando datos de imagen para PHAsset" }
      PHImageManager.defaultManager()
        .requestImageDataForAsset(asset, options) { data, dataUTI, orientation, info ->
          logger.d { "Respuesta de PHImageManager: dataUTI=$dataUTI, orientation=$orientation" }
          if (data == null) {
            logger.e { "PHImageManager no devolvió datos. Info: ${info?.get("PHImageErrorKey")}" }
          }
          continuation.resume(data)
        }
    }

  @OptIn(ExperimentalForeignApi::class)
  fun ByteArray.toNSData(): NSData = memScoped {
    this@toNSData.usePinned { pinned ->
      NSData.create(
        bytes = pinned.addressOf(0),
        length = this@toNSData.size.toULong()
      )
    }
  }

  @OptIn(ExperimentalForeignApi::class)
  fun NSData.toByteArray(): ByteArray {
    val length = this.length.toInt()
    val result = ByteArray(length)
    result.usePinned { pinned ->
      memcpy(pinned.addressOf(0), this.bytes, length.toULong())
    }
    return result
  }
}