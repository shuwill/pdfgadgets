package org.spreadme.pdfgadgets.ui

import org.spreadme.pdfgadgets.utils.OS
import org.spreadme.pdfgadgets.utils.Platform
import javax.swing.JRootPane

class PlatformUI(
    private val rootPane: JRootPane = JRootPane(),
    private val isDark: Boolean = false
) {

    companion object {
        val properties = mutableMapOf<String, String>()
    }

    private val platform: Platform = Platform()

    fun isSupportCustomWindowDecoration(): Boolean =
        platform.os == OS.MacOs

    fun customWindowDecoration() {
        if (isSupportCustomWindowDecoration()) {
            System.setProperty("apple.awt.textantialiasing", "true")
            rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            rootPane.putClientProperty("apple.awt.windowTitleVisible", false)
            rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            if (isDark) {
                rootPane.putClientProperty("apple.awt.windowAppearance", "NSAppearanceNameVibrantDark")
            } else {
                rootPane.putClientProperty("apple.awt.windowAppearance", "NSAppearanceNameVibrantLight")
            }
            properties["CUSTOM_DECORATION"] = true.toString()
        }
    }

    fun isCustomWindowDecoration() =
        properties["CUSTOM_DECORATION"] == true.toString()

}