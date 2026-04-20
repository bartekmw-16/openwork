package com.tymerstudio.app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.tymerstudio.app.network.DesktopDiscovery
import com.tymerstudio.app.network.DesktopInstance
import com.tymerstudio.app.network.SSHClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ConnectionViewModel(application: Application) : AndroidViewModel(application) {
    private val discovery = DesktopDiscovery(application)
    private val sshClient = SSHClient()

    private val _discoveredDevices = MutableStateFlow<List<DesktopInstance>>(emptyList())
    val discoveredDevices: StateFlow<List<DesktopInstance>> = _discoveredDevices

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning

    private val _connectionStatus = MutableStateFlow("")
    val connectionStatus: StateFlow<String> = _connectionStatus

    fun startDiscovery() {
        viewModelScope.launch {
            _isScanning.value = true
            _connectionStatus.value = ""

            try {
                discovery.discoverDevices().collect { devices ->
                    _discoveredDevices.value = devices
                    if (devices.isNotEmpty()) {
                        _isScanning.value = false
                    }
                }
            } catch (e: Exception) {
                _connectionStatus.value = "Discovery failed: ${e.message}"
                _isScanning.value = false
            }
        }
    }

    fun connectToDevice(device: DesktopInstance, onConnected: () -> Unit) {
        viewModelScope.launch {
            _connectionStatus.value = "Connecting to ${device.name}..."

            // For now, skip SSH and connect directly
            // In production, establish SSH tunnel first
            val result = sshClient.connect(
                host = device.host,
                port = 22, // SSH port
                username = "tymer"
            )

            result.fold(
                onSuccess = {
                    _connectionStatus.value = "Connected successfully"
                    // Store connection details
                    // TODO: Save to preferences
                    onConnected()
                },
                onFailure = { error ->
                    _connectionStatus.value = "Connection failed: ${error.message}"
                }
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        discovery.stopDiscovery()
        sshClient.disconnect()
    }
}
