package id.idham.newsfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import id.idham.newsfeed.core.designsystem.theme.NewsFeedTheme
import id.idham.newsfeed.core.ui.SecurityAlertDialog
import id.idham.newsfeed.navigation.NewsFeedNavHost
import id.idham.newsfeed.security.SecurityMonitor

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        SecurityMonitor.initialize(this)

        window.attributes.preferredRefreshRate = 90f
        enableEdgeToEdge()
        setContent {
            val detectedThreat by SecurityMonitor.threatFlow.collectAsState()

            NewsFeedTheme {
                NewsFeedNavHost()

                detectedThreat?.let { threat ->
                    SecurityAlertDialog(
                        threatMessage = threat,
                        onExitClick = { finishAffinity() }
                    )
                }
            }
        }
    }
}
