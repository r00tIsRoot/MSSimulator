package com.mssimulator.data

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

private val client = HttpClient()

actual suspend fun fetchText(url: String): String {
    val response: HttpResponse = client.get(url)
    return response.bodyAsText()
}
