package org.spreadme.pdfgadgets.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import java.io.Closeable
import kotlin.coroutines.CoroutineContext

abstract class ViewModel {

    companion object {
        fun closeWithRumtimeException(any: Any) {
            if (any is Closeable) {
                any.close()
            }
        }
    }

    val tags: MutableMap<String, Any> = mutableMapOf()

    @Volatile
    var cleared: Boolean = false

    open fun onCleared() {

    }

    fun clear() {
        cleared = true
        if (tags.isNotEmpty()) {
            synchronized(tags) {
                tags.forEach { (_, u) ->
                    closeWithRumtimeException(u)
                }
            }
        }
        onCleared()
    }

    inline fun <reified T : Any> setTagIfAbsent(key: String, newValue: T): T {
        synchronized(tags) {
            val previous = tags[key]
            val result = if (previous == null) {
                tags[key] = newValue
                newValue
            } else {
                previous
            }
            if (cleared) {
                closeWithRumtimeException(result)
            }
            return result as T
        }
    }

    inline fun <reified T> getTag(key: String): T? {
        if (tags.isEmpty()) {
            return null
        }
        synchronized(tags) {
            return tags[key] as T
        }
    }
}

private const val JOB_KEY = "org.spreadme.pdfgadgets.common.ViewModelCoroutineScope.JOB_KEY"

val ViewModel.viewModelScope: CoroutineScope
    get() {
        val scope: CoroutineScope? = this.getTag(JOB_KEY)
        if (scope != null) {
            return scope
        }
        return setTagIfAbsent(
            JOB_KEY,
            CloseableCoroutineScope(SupervisorJob() + Dispatchers.Default)
        )
    }

internal class CloseableCoroutineScope(context: CoroutineContext) : Closeable, CoroutineScope {

    override val coroutineContext: CoroutineContext = context

    override fun close() {
        coroutineContext.cancel()
    }

}