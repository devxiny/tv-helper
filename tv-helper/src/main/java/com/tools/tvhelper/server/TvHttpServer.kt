package com.tools.tvhelper.server

import android.content.Context
import com.google.gson.Gson
import com.tools.tvhelper.TvControlConfig
import fi.iki.elonen.NanoWSD
import java.io.IOException

class TvHttpServer(
    private val context: Context,
    port: Int,
    private val config: TvControlConfig,
    private val onAction: (String, Map<String, String>?) -> Unit
) : NanoWSD(port) {

    private val gson = Gson()

    override fun openWebSocket(handshake: IHTTPSession): WebSocket {
        return TvWebSocket(handshake, onAction)
    }

    override fun serveHttp(session: IHTTPSession): Response {
        if (session.uri == "/api/config") {
            return newFixedLengthResponse(Response.Status.OK, "application/json", gson.toJson(config))
        }
        
        // Serve index.html for root
        if (session.uri == "/" || session.uri == "/index.html") {
            return try {
                val stream = context.assets.open("web/index.html")
                newChunkedResponse(Response.Status.OK, "text/html", stream)
            } catch (e: IOException) {
                newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Error loading assets")
            }
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "Not Found")
    }

    private class TvWebSocket(
        handshake: IHTTPSession,
        private val onAction: (String, Map<String, String>?) -> Unit
    ) : WebSocket(handshake) {
        
        override fun onOpen() {}
        override fun onClose(code: WebSocketFrame.CloseCode?, reason: String?, initiatedByRemote: Boolean) {}
        
        override fun onMessage(message: WebSocketFrame) {
            try {
                val payload = Gson().fromJson(message.textPayload, ActionPayload::class.java)
                if (payload.action == "ping") return // Heartbeat, ignore
                onAction(payload.action, payload.data)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        override fun onPong(pong: WebSocketFrame?) {}
        override fun onException(exception: IOException?) {}
    }

    private data class ActionPayload(val action: String, val data: Map<String, String>?)
}
