package id.idham.newsfeed.feature.home

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

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
    val mapLocation = viewModel.mapLocation.collectAsStateWithLifecycle()
    val countryName = viewModel.countryName.collectAsStateWithLifecycle()
    val viewModeState = rememberSaveable { mutableStateOf(ViewMode.LIST) }
    val selectedTabIndexState = rememberSaveable { mutableIntStateOf(0) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.fetchLocalNews()
        } else {
            selectedTabIndexState.intValue = 0
            viewModel.fetchGlobalNews()
        }
    }

    val context = LocalContext.current
    rememberShakeDetector {
        articles.refresh()
        Toast.makeText(context, "Refreshing...", Toast.LENGTH_SHORT).show()
    }

    HomeScreenContent(
        modifier = modifier,
        articles = articles,
        viewMode = viewModeState.value,
        selectedTabIndex = selectedTabIndexState.intValue,
        onTabSelected = { index ->
            selectedTabIndexState.intValue = index
            if (index == 1) {
                locationPermissionLauncher.launch(
                    arrayOf(
                        ACCESS_FINE_LOCATION,
                        ACCESS_COARSE_LOCATION
                    )
                )
            } else {
                viewModel.fetchGlobalNews()
            }
        },
        onViewModeChange = { viewModeState.value = it },
        onItemClicked = onItemClicked,
        mapLocation = mapLocation.value,
        countryName = countryName.value,
        onMapClick = { lat, lng -> viewModel.updateMapLocation(lat, lng) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    articles: LazyPagingItems<Article>,
    viewMode: ViewMode,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onViewModeChange: (ViewMode) -> Unit,
    onItemClicked: (Article) -> Unit,
    mapLocation: GeoPoint?,
    countryName: String?,
    onMapClick: (Double, Double) -> Unit
) {
    val listState = rememberLazyListState()
    val gridState = rememberLazyGridState()

    val isScrolledDown by remember(viewMode) {
        derivedStateOf {
            if (viewMode == ViewMode.LIST) {
                listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 20
            } else {
                gridState.firstVisibleItemIndex > 0 || gridState.firstVisibleItemScrollOffset > 20
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
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
                PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                    Tab(
                        selected = selectedTabIndex == 0,
                        onClick = { onTabSelected(0) },
                        text = { Text("General") }
                    )
                    Tab(
                        selected = selectedTabIndex == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("Local News") }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                AnimatedVisibility(
                    visible = selectedTabIndex == 1 && !isScrolledDown,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.5f)
                        .clipToBounds()) {
                        OsmMapView(
                            mapLocation = mapLocation,
                            countryName = countryName,
                            onMapClick = onMapClick,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (countryName != null) {
                            ElevatedCard(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(16.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 16.dp,
                                        vertical = 8.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = countryName,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds()) {
                    if (articles.loadState.refresh is LoadState.Loading && articles.itemCount == 0) {
                        LoadingState()
                    } else if (articles.loadState.refresh is LoadState.Error && articles.itemCount == 0) {
                        val error = (articles.loadState.refresh as LoadState.Error).error
                        ErrorState(error.message)
                    } else if (articles.loadState.refresh is LoadState.NotLoading && articles.itemCount == 0) {
                        EmptyState()
                    } else {
                        when (viewMode) {
                            ViewMode.LIST -> {
                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.fillMaxSize(),
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
                                    state = gridState,
                                    modifier = Modifier.fillMaxSize(),
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
fun OsmMapView(
    mapLocation: GeoPoint?,
    countryName: String?,
    onMapClick: (Double, Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val mapView = remember {
        Configuration.getInstance().userAgentValue = context.packageName
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(5.0)
        }
    }

    LaunchedEffect(mapLocation, countryName) {
        if (mapLocation != null) {
            mapView.overlays.removeAll { it is Marker }
            val marker = Marker(mapView)
            marker.position = mapLocation
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.add(marker)
            mapView.controller.animateTo(mapLocation)
        }
    }

    AndroidView(
        factory = {
            val mapEventsReceiver = object : MapEventsReceiver {
                override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                    onMapClick(p.latitude, p.longitude)
                    return true
                }

                override fun longPressHelper(p: GeoPoint): Boolean = false
            }
            mapView.overlays.add(0, MapEventsOverlay(mapEventsReceiver))
            mapView
        },
        modifier = modifier
    )
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
