package org.spreadme.pdfgadgets.utils

class Platform {

     val os: OS by lazy {
        val osName = System.getProperty("os.name")
        when{
            osName.lowercase().contains("mac") -> OS.MacOs
            osName.lowercase().contains("linux") -> OS.Linux
            osName.lowercase().contains("win") -> OS.Windows
            else -> throw Error("unknown os $osName")
        }
    }

     val arch: Arch by lazy {
        when(val arch = System.getProperty("os.arch")) {
            "x86_64", "amd64" -> Arch.X64
            "aarch64" -> Arch.Arm
            else -> throw Error("unknown arch $arch")
        }
    }
}

enum class OS(val id: String, val ext: String) {
    Linux("linux", "so"),
    Windows("windows", "dll"),
    MacOs("macos", "dylib");
}

enum class Arch(val id: String) {
    X64("x64"),
    Arm("arm64")
}