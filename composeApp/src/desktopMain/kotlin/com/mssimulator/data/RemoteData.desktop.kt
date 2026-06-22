package com.mssimulator.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI

actual suspend fun fetchText(url: String): String = withContext(Dispatchers.IO) {
    URI(url).toURL().openStream().bufferedReader().use { it.readText() }
}
