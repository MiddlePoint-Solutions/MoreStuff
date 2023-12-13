package co.softov.morestuff.android.domain.service

interface PdfHandler {
    suspend fun savePdf(uri: String): String?
    fun sharePdf(pdfPath: String)
}
