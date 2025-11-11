package id.idham.newsfeed.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import id.idham.newsfeed.core.model.Article
import id.idham.newsfeed.core.model.Source
import id.idham.newsfeed.core.ui.EmptyState
import id.idham.newsfeed.core.ui.ErrorState
import id.idham.newsfeed.core.ui.LoadingState
import org.koin.androidx.compose.koinViewModel

enum class ViewMode {
    LIST, GRID
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    onItemClicked: (Article) -> Unit
) {
    val articles = viewModel.articlesFlow.collectAsLazyPagingItems()
    var viewMode by rememberSaveable { mutableStateOf(ViewMode.LIST) }

    HomeScreenContent(
        modifier = modifier,
        articles = articles,
        viewMode = viewMode,
        onViewModeChange = { viewMode = it },
        onItemClicked = onItemClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    articles: LazyPagingItems<Article>,
    viewMode: ViewMode,
    onViewModeChange: (ViewMode) -> Unit,
    onItemClicked: (Article) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("News Feed") },
                actions = {
                    IconButton(onClick = {
                        onViewModeChange(
                            when (viewMode) {
                                ViewMode.LIST -> ViewMode.GRID
                                ViewMode.GRID -> ViewMode.LIST
                            }
                        )
                    }) {
                        Icon(
                            imageVector = when (viewMode) {
                                ViewMode.LIST -> Icons.Default.GridView
                                ViewMode.GRID -> Icons.AutoMirrored.Default.ViewList
                            },
                            contentDescription = when (viewMode) {
                                ViewMode.LIST -> "Switch to grid view"
                                ViewMode.GRID -> "Switch to list view"
                            }
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (articles.loadState.refresh) {
                is LoadState.Loading -> {
                    LoadingState()
                }

                is LoadState.Error -> {
                    val error = (articles.loadState.refresh as LoadState.Error).error
                    ErrorState(error.message)
                }

                is LoadState.NotLoading -> {
                    if (articles.itemCount == 0) {
                        EmptyState()
                    } else {
                        when (viewMode) {
                            ViewMode.LIST -> {
                                LazyColumn(
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(articles.itemCount) { index ->
                                        articles[index]?.let { article ->
                                            NewsArticleListItem(article) { onItemClicked(it) }
                                        }
                                    }

                                    item {
                                        LoadingFooter(articles.loadState.append)
                                    }
                                }
                            }

                            ViewMode.GRID -> {
                                LazyVerticalGrid(
                                    columns = GridCells.Fixed(2),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(articles.itemCount) { index ->
                                        articles[index]?.let { article ->
                                            NewsArticleGridItem(article) { onItemClicked(it) }
                                        }
                                    }

                                    item {
                                        LoadingFooter(articles.loadState.append)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingFooter(appendState: LoadState) {
    when (appendState) {
        is LoadState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        is LoadState.Error -> {
            ErrorState(appendState.error.message)
        }

        else -> {}
    }
}

@Composable
private fun NewsArticleListItem(
    article: Article,
    onItemClicked: (Article) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(article) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )
                Text(
                    text = article.description.ifEmpty { "No description available" },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun NewsArticleGridItem(
    article: Article,
    onItemClicked: (Article) -> Unit
) {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClicked(article) }
    ) {
        Column {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(article.urlToImage)
                    .crossfade(true)
                    .build(),
                contentDescription = article.title,
                contentScale = ContentScale.Crop,
                placeholder = ColorPainter(Color.Gray),
                error = ColorPainter(Color.Gray),
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleSmall,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = article.description.ifEmpty { "No description available" },
                    style = MaterialTheme.typography.bodySmall,
                    minLines = 2,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NewsArticleListItem_Preview() {
    val article = Article(
        source = Source(id = "1", name = "TechCrunch"),
        author = "John Doe",
        title = "Breaking: New Technology Revolutionizes the Industry",
        description = "A groundbreaking discovery has been made that will change everything we know about technology and innovation.",
        url = "https://example.com",
        urlToImage = "https://loremflickr.com/640/480/technology",
        publishedAt = "2024-01-15T10:30:00Z",
        content = "Full content here..."
    )
    NewsArticleListItem(article = article, onItemClicked = {})
}

@Preview(showBackground = true)
@Composable
private fun NewsArticleGridItem_Preview() {
    val article = Article(
        source = Source(id = "1", name = "TechCrunch"),
        author = "John Doe",
        title = "Breaking: New Technology Revolutionizes the Industry",
        description = "A groundbreaking discovery has been made that will change everything we know about technology and innovation.",
        url = "https://example.com",
        urlToImage = "https://loremflickr.com/640/480/technology",
        publishedAt = "2024-01-15T10:30:00Z",
        content = "Full content here..."
    )
    NewsArticleGridItem(article = article, onItemClicked = {})
}
