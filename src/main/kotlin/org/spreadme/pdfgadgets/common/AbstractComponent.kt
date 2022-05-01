package org.spreadme.pdfgadgets.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import java.util.UUID

abstract class AbstractComponent(
    open val name: String,
    val uid: String = UUID.randomUUID().toString()
): Component, AutoCloseable {

    @Composable
    override fun render() {
       key(this){
           doRender()
       }
    }

    @Composable
    abstract fun doRender()

    override fun close() {

    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractComponent

        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

}