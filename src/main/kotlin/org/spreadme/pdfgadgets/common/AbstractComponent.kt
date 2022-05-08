package org.spreadme.pdfgadgets.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import java.io.Closeable
import java.util.*
import kotlin.reflect.full.primaryConstructor

abstract class AbstractComponent(
    val uid: String = UUID.randomUUID().toString()
) : Component, Closeable {

    val viewModels: MutableMap<String, ViewModel> = mutableMapOf()

    @Composable
    override fun render() {
        key(this) {
            onRender()
        }
    }

    @Composable
    abstract fun onRender()

    override fun close() {
        viewModels.values.forEach {
            it.clear()
        }
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

inline fun <reified T : ViewModel> AbstractComponent.getViewModel(
    vararg params: Any?,
    init: (T) -> Unit = {}
): T {
    val key = T::class.java.name
    synchronized(viewModels) {
        val previous = viewModels[key]
        if (previous == null) {
            val kClass = T::class
            val primaryConstructor = kClass.primaryConstructor ?: throw Exception("no primary constructor found on ${kClass.simpleName}")
            val viewModel = primaryConstructor.call(*params)
            init(viewModel)
            viewModels[key] = viewModel
            return viewModel
        }
        return previous as T
    }
}