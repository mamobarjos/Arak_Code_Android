package com.arakadds.arak.common.helper.connectivity_checker

import android.util.Log
import com.arakadds.arak.utils.Constants.GOOGLE_PORT
import com.arakadds.arak.utils.Constants.GOOGLE_SERVER
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

/**
 * Send a ping to edco primary DNS.
 * If successful, that means we have internet.
 */
object DoesNetworkHaveInternet {

    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try {
            Log.d(TAG, "PINGING edco.")
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress(GOOGLE_SERVER, GOOGLE_PORT), 60000)
            socket.close()
            Log.d(TAG, "PING success.")
            true
        } catch (e: IOException) {
            Log.e(TAG, "No internet connection. ${e}")
            false
        }
    }
}