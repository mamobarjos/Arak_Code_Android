package com.arakadds.arak.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val builder = request.newBuilder().apply {
            // Basic headers
            addHeader("Device-Type", "android")
            addHeader("Content-Type", "application/json")
            
            // Cache prevention headers
            addHeader("Cache-Control", "no-cache, no-store, must-revalidate")
            addHeader("Pragma", "no-cache")
            addHeader("Expires", "0")
        }

        // Proceed with the request
        val response = chain.proceed(builder.build())

        // Add cache prevention headers to response
        return response.newBuilder()
            .header("Cache-Control", "no-cache, no-store, must-revalidate")
            .header("Pragma", "no-cache")
            .header("Expires", "0")
            .build()
    }

}