package id.idham.newsfeed.core.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun SecurityAlertDialog(
    threatMessage: String,
    onExitClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onExitClick,
        confirmButton = {
            Button(onClick = onExitClick) {
                Text("Exit App")
            }
        },
        icon = {
            Icon(Icons.Default.Warning, contentDescription = "Security Threat")
        },
        title = {
            Text("Security Threat Detected")
        },
        text = {
            Text("This device or environment poses a security risk ($threatMessage). For your protection, the app will now close.")
        }
    )
}
