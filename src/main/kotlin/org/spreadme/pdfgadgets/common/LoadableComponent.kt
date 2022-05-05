package org.spreadme.pdfgadgets.common

abstract class LoadableComponent<T>(
    name: String = "Loading..."
) : AbstractComponent(name), Loadable<T>