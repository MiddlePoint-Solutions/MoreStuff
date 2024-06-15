interface PDFHandler {
    suspend fun savePDF(uri: String): String?
    fun sharePDF(pdfPath: String)
    fun openPDF(pdfPath: String)
}

expect class PDFHandlerImpl: PDFHandler
