package org.spreadme.pdfgadgets.di

import org.koin.dsl.module
import org.spreadme.pdfgadgets.repository.*

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

    single {
        PdfStreamParser()
    }

    single<PdfTextSearcher> {
        DefaultPdfTextSearcher()
    }
}

val asn1ParserMoudle = module {
    single {
        ASN1Parser()
    }
}