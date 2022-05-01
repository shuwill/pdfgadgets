package org.spreadme.pdfgadgets.common

interface Loadable {

    /**
     * load finish callback
     */
    suspend fun load()
}