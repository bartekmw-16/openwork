package com.tymerstudio.app.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class DesktopInstance(
    val name: String,
    val host: String,
    val port: Int,
    val serviceInfo: NsdServiceInfo? = null
)

class DesktopDiscovery(private val context: Context) {
    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val serviceType = "_tymerstudio._tcp"

    fun discoverDevices(): Flow<List<DesktopInstance>> = callbackFlow {
        val devices = mutableMapOf<String, DesktopInstance>()

        val discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(serviceType: String) {
                trySend(devices.values.toList())
            }

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                        // Ignore resolution failures
                    }

                    override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                        val host = serviceInfo.host?.hostAddress ?: return
                        val port = serviceInfo.port
                        val name = serviceInfo.serviceName

                        val device = DesktopInstance(
                            name = name,
                            host = host,
                            port = port,
                            serviceInfo = serviceInfo
                        )

                        devices[name] = device
                        trySend(devices.values.toList())
                    }
                })
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                devices.remove(serviceInfo.serviceName)
                trySend(devices.values.toList())
            }

            override fun onDiscoveryStopped(serviceType: String) {
                trySend(devices.values.toList())
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                close(Exception("Discovery failed with error code: $errorCode"))
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                close(Exception("Stop discovery failed with error code: $errorCode"))
            }
        }

        try {
            nsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            try {
                nsdManager.stopServiceDiscovery(discoveryListener)
            } catch (e: Exception) {
                // Ignore errors on cleanup
            }
        }
    }

    fun stopDiscovery() {
        // Discovery is stopped when the flow is cancelled
    }
}
