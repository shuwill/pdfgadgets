package org.spreadme.pdfgadgets

import org.koin.core.context.startKoin
import org.spreadme.pdfgadgets.common.Application
import org.spreadme.pdfgadgets.di.appConfigLoadModule
import org.spreadme.pdfgadgets.di.fileMetadataModule
import org.spreadme.pdfgadgets.di.pdfParseModule

class PDFGadgetsApp : Application() {

    override fun create() {
        val intent = MainActivity.getStartIntent()
        initKoin()
        startActivity(intent)
    }

    private fun initKoin() {
        startKoin {
            modules(
                appConfigLoadModule,
                fileMetadataModule,
                pdfParseModule
            )
        }
    }
}

fun main() {
    PDFGadgetsApp().create()
}
