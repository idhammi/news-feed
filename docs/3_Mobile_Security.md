# Mobile Security Features

This project incorporates security measures to protect application integrity and secure network communications against reverse engineering and Man-in-the-Middle (MITM) attacks.

## 1. RASP (Runtime Application Self-Protection)

The application integrates the **freeRASP SDK by Talsec** to monitor device and application state during runtime.

The SDK actively detects the following threats:
- **Rooted & Jailbroken Devices**: Restricts execution on compromised operating systems.
- **Emulators**: Prevents the application from running within simulated environments.
- **Hooking Frameworks**: Detects memory injection attempts (e.g., Frida, Xposed) designed to alter application behavior.
- **App Tampering**: Verifies the signing certificate hash. If the APK is modified and resigned, the application identifies the discrepancy.

### Code Sample
Here is the `SecurityMonitor` implementation handling the freeRASP configuration and exposing threat detection states:

```kotlin
object SecurityMonitor : ThreatListener.ThreatDetected {
    private val _threatFlow = MutableStateFlow<String?>(null)
    val threatFlow: StateFlow<String?> = _threatFlow.asStateFlow()

    fun initialize(context: Context) {
        val expectedPackageName = "id.idham.newsfeed"
        val expectedAlternativePackageNames = arrayOf("com.android.vending")
        val expectedSigningCertificateHashBase64 = arrayOf("U4FUXOMbsknBix9IY/zCX+c+wOyL7FaN81N8uUrHr6Y=")

        val config = TalsecConfig.Builder(expectedPackageName, expectedSigningCertificateHashBase64)
            .watcherMail("developer@example.com")
            .supportedAlternativeStores(expectedAlternativePackageNames)
            .prod(true)
            .build()

        ThreatListener(this).registerListener(context)
        Talsec.start(context, config)
    }

    override fun onRootDetected() { _threatFlow.value = "Root/Jailbreak detected" }
    override fun onDebuggerDetected() { _threatFlow.value = "Debugger attached" }
    // Other threat listeners...
}
```

### Configuration Details

1. **Alternative Package Names** (`expectedAlternativePackageNames`): Specifies authorized app stores (e.g., `com.android.vending` for Google Play) to prevent the app from running if installed from untrusted third-party sources.

2. **Signing Certificate Hash** (`expectedSigningCertificateHashBase64`): A Base64-encoded SHA-256 hash of the application's official public signing certificate. 
   - **Safety**: It is completely safe to hardcode because it is derived from the public key, not the private key.
   - **Purpose**: It prevents tampering. If the APK is modified and resigned by a malicious actor, the signature hash will change, and the app will instantly terminate itself.

**Threat Handling:**
When a threat is detected, the `SecurityMonitor` component triggers an un-dismissible Alert Dialog. The application then automatically terminates its process to prevent unauthorized access.

## 2. Network Security Configuration

The application enforces network security at the OS level using `network_security_config.xml`.

- **Cleartext Traffic Restriction**: All HTTP connections are explicitly disabled (`cleartextTrafficPermitted="false"`). Network requests are strictly routed over encrypted HTTPS (SSL/TLS) channels to ensure data confidentiality.

## 3. Implementation Screenshots

**Security Threat Detection Dialog:**<br>
<img src="images/6.png" width="300" alt="Security Dialog"/>
