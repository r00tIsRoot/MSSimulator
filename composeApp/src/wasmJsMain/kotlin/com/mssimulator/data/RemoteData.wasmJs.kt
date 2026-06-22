package com.mssimulator.data

import kotlinx.coroutines.await
import kotlin.js.Promise

/** Uses the browser Fetch API; rejects (throws) on non-2xx responses. */
@JsFun(
    "(url) => fetch(url).then(r => { " +
        "if (!r.ok) throw new Error('HTTP ' + r.status + ' for ' + url); " +
        "return r.text(); })"
)
private external fun jsFetchText(url: String): Promise<JsString>

actual suspend fun fetchText(url: String): String =
    jsFetchText(url).await<JsString>().toString()
