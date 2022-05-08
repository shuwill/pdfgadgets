package org.spreadme.pdfgadgets.common

abstract class LoadableAppComponent<T>(
    name: String = "Loading..."
) : AppComponent(name) {

    /**
     * load finish callback
     */
    abstract suspend fun load()

    abstract fun content(): T

}