package org.spreadme.pdfgadgets.di

import org.koin.dsl.module
import org.spreadme.pdfgadgets.repository.*

val pdfParseModule = module {

    single {
        FileMetadataParser()
    }

    single<SignatureParser> {
        DefaultSignatureParser()
    }

    single<PdfMetadataParser> {
        DefaultPdfMetadataParser()
    }
}