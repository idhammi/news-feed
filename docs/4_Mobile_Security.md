# Mobile Security Features

This project incorporates security measures to protect application integrity and secure network communications against reverse engineering and Man-in-the-Middle (MITM) attacks.

## 1. RASP (Runtime Application Self-Protection)

The application integrates the **freeRASP SDK by Talsec** to monitor device and application state during runtime.

The SDK actively detects the following threats:
- **Rooted & Jailbroken Devices**: Restricts execution on compromised operating systems.
- **Emulators**: Prevents the application from running within simulated environments.
- **Hooking Frameworks**: Detects memory injection attempts (e.g., Frida, Xposed) designed to alter application behavior.
- **App Tampering**: Verifies the signing certificate hash. If the APK is modified and resigned, the application identifies the discrepancy.

**Threat Handling:**
When a threat is detected, the `SecurityMonitor` component triggers an un-dismissible Alert Dialog. The application then automatically terminates its process to prevent unauthorized access.

## 2. Network Security Configuration

The application enforces network security at the OS level using `network_security_config.xml`.

- **Cleartext Traffic Restriction**: All HTTP connections are explicitly disabled (`cleartextTrafficPermitted="false"`). Network requests are strictly routed over encrypted HTTPS (SSL/TLS) channels to ensure data confidentiality.

## 3. Implementation Screenshots

**Security Threat Detection Dialog:**
![Security Dialog](images/6.png)
