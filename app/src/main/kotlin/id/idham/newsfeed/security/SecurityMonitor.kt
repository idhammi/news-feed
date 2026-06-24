package id.idham.newsfeed.security

import android.content.Context
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.ThreatListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object SecurityMonitor : ThreatListener.ThreatDetected {

    private val _threatFlow = MutableStateFlow<String?>(null)
    val threatFlow: StateFlow<String?> = _threatFlow.asStateFlow()

    fun initialize(context: Context) {
        val expectedPackageName = "id.idham.newsfeed"
        val expectedAlternativePackageNames = arrayOf("com.android.vending")
        val expectedSigningCertificateHashBase64 = arrayOf("U4FUXOMbsknBix9IY/zCX+c+wOyL7FaN81N8uUrHr6Y=")

        val config = TalsecConfig.Builder(expectedPackageName, expectedSigningCertificateHashBase64)
            .watcherMail("idham@example.com")
            .supportedAlternativeStores(expectedAlternativePackageNames)
            .prod(true)
            .build()

        ThreatListener(this).registerListener(context)
        Talsec.start(context, config)
    }

    override fun onRootDetected() { _threatFlow.value = "Root/Jailbreak detected" }
    override fun onDebuggerDetected() { _threatFlow.value = "Debugger attached" }
    override fun onEmulatorDetected() { _threatFlow.value = "Emulator detected" }
    override fun onTamperDetected() { _threatFlow.value = "App tampering detected" }
    override fun onUntrustedInstallationSourceDetected() { /* Ignore */ }
    override fun onHookDetected() { _threatFlow.value = "Hooking framework detected (e.g., Frida)" }
    override fun onDeviceBindingDetected() { _threatFlow.value = "Device binding issue" }
    override fun onObfuscationIssuesDetected() { /* Ignore */ }
    override fun onMalwareDetected(p0: MutableList<SuspiciousAppInfo>?) { _threatFlow.value = "Malware detected" }
}
