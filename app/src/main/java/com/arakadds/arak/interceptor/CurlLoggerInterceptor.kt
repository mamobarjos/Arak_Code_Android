package com.arakadds.arak.interceptor

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
//import org.smasco.app.data.model.Failure
import java.nio.charset.Charset

class CurlLoggerInterceptor : Interceptor {
    private var curlCommandBuilder: StringBuilder? = null
    private val UTF8 = Charset.forName("UTF-8")
    private var tag: String? = null

    constructor() {}

    /**
     * Set logcat tag for curl lib to make it ease to filter curl logs only.
     * @param tag
     */
    constructor(tag: String?) {
        this.tag = tag
    }

//    @Throws(Failure::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        curlCommandBuilder = StringBuilder("")
        // add cURL command
        curlCommandBuilder!!.append("cURL ")
        curlCommandBuilder!!.append("-X ")
        // add method
        curlCommandBuilder!!.append(request.method.toUpperCase()).append(" ")
        // adding headers
        for (headerName in request.headers.names()) {
            addHeader(headerName, request.headers[headerName])
        }

        // adding request body
        val requestBody = request.body
        if (request.body != null) {
            val buffer = Buffer()
            requestBody!!.writeTo(buffer)
            var charset = UTF8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(UTF8)
                curlCommandBuilder!!.append(" -d '").append(buffer.readString(charset!!))
                    .append("'")
            }
        }

        // add request URL
        curlCommandBuilder!!.append(" \"").append(request.url.toString()).append("\"")
        curlCommandBuilder!!.append(" -L")
        CurlPrinter.print(tag, request.url.toString(), curlCommandBuilder.toString())
        return chain.proceed(request)
    }

    private fun addHeader(headerName: String, headerValue: String?) {
        curlCommandBuilder!!.append("-H " + "\"").append(headerName).append(": ")
            .append(headerValue).append("\" ")
    }
}

object CurlPrinter {
    /**
     * Drawing toolbox
     */
    private const val SINGLE_DIVIDER = "────────────────────────────────────────────"
    private var sTag = "CURL"
    fun print(tag: String?, url: String, msg: String?) {
        // setting tag if not null
        if (tag != null) sTag = tag
        val logMsg = java.lang.StringBuilder("\n")
        logMsg.append("\n")
        logMsg.append("URL: $url")
        logMsg.append("\n")
        logMsg.append(SINGLE_DIVIDER)
        logMsg.append("\n")
        logMsg.append(msg)
        logMsg.append(" ")
        logMsg.append(" \n")
        logMsg.append(SINGLE_DIVIDER)
        logMsg.append(" \n ")
        log(logMsg.toString())
    }

    private fun log(msg: String) {
        Log.d(sTag, msg)
    }
}