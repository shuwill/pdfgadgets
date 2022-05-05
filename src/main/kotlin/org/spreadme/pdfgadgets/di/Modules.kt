package org.spreadme.pdfgadgets.di

import org.koin.dsl.module
import org.spreadme.pdfgadgets.repository.*
import org.spreadme.pdfgadgets.ui.frame.ApplicationViewModel

val appConfigLoadModule = module {
    single<AppConfigRepository> {
        DefaultAppConfigRepository()
    }
}

val fileMetadataModule = module {
    single {
        FileMetadataRepository()
    }
}

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

val viewModelModule = module {
    single {
        ApplicationViewModel(get(), get())
    }
}