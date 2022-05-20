package org.spreadme.pdfgadgets.ui.common

import java.awt.FileDialog
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.filechooser.FileNameExtensionFilter

fun FileDialog(
    parent: JFrame,
    title: String,
    mode: FileDialogMode = FileDialogMode.LOAD,
    exts: ArrayList<String> = arrayListOf(),
    onFileOpen: (Path) -> Unit = {},
    onFileSave: (Path) -> Unit = {}
) {

    val chooser = JFileChooser()
    chooser.fileSelectionMode = JFileChooser.FILES_ONLY
    chooser.dialogTitle = title
    chooser.fileFilter = FileNameExtensionFilter(null, *exts.toTypedArray())
    val showDialog = chooser.showDialog(
        parent, if (mode == FileDialogMode.LOAD) {
            "选择"
        } else {
            "保存"
        }
    )
    if (showDialog == JFileChooser.APPROVE_OPTION) {
        val selectedFile = chooser.selectedFile
        if (selectedFile != null) {
            val path = selectedFile.toPath()
            println(path)
            if (mode == FileDialogMode.LOAD) {
                if (Files.exists(path)) {
                    onFileOpen(path)
                }
            } else if(mode == FileDialogMode.SAVE){
                onFileSave(path)
            }
        }
    }
}

enum class FileDialogMode(private val mode: Int) {
    LOAD(FileDialog.LOAD),
    SAVE(FileDialog.SAVE);

    fun value(): Int {
        return mode
    }
}