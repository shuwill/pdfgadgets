package org.spreadme.pdfgadgets.common

interface Loadable<T> {

    /**
     * load finish callback
     */
    suspend fun load()

    fun content(): T
}