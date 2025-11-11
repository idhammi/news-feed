package id.idham.newsfeed

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import id.idham.newsfeed.core.designsystem.theme.NewsFeedTheme
import id.idham.newsfeed.navigation.NewsFeedNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.attributes.preferredRefreshRate = 90f
        enableEdgeToEdge()
        setContent {
            NewsFeedTheme {
                NewsFeedNavHost()
            }
        }
    }
}
