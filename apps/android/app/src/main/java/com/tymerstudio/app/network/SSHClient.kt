package com.tymerstudio.app.network

import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class SSHClient {
    private var session: Session? = null
    private val jsch = JSch()

    suspend fun connect(
        host: String,
        port: Int,
        username: String = "tymer",
        privateKeyPath: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Add private key if provided
            privateKeyPath?.let {
                if (File(it).exists()) {
                    jsch.addIdentity(it)
                }
            }

            // Create session
            session = jsch.getSession(username, host, port).apply {
                // Skip host key checking for local network
                // In production, should verify host keys
                setConfig("StrictHostKeyChecking", "no")
                connect(10000) // 10 second timeout
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createTunnel(
        localPort: Int,
        remoteHost: String,
        remotePort: Int
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val assignedPort = session?.setPortForwardingL(
                localPort,
                remoteHost,
                remotePort
            ) ?: throw IllegalStateException("Not connected")

            Result.success(assignedPort)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun disconnect() {
        session?.disconnect()
        session = null
    }

    fun isConnected(): Boolean {
        return session?.isConnected == true
    }

    companion object {
        fun generateKeyPair(
            privateKeyPath: String,
            publicKeyPath: String,
            passphrase: String? = null
        ): Result<Unit> {
            return try {
                val jsch = JSch()
                val keyPair = com.jcraft.jsch.KeyPair.genKeyPair(jsch, com.jcraft.jsch.KeyPair.RSA, 2048)

                keyPair.writePrivateKey(privateKeyPath, passphrase?.toByteArray())
                keyPair.writePublicKey(publicKeyPath, "tymer@android")
                keyPair.dispose()

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
