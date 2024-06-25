package com.kipngetich.mooddetector.musicRecommendation

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.kipngetich.mooddetector.R
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MusicScreen(emotion: String) {

    val viewModel: MusicViewModel = viewModel()

    LaunchedEffect(emotion) {
        viewModel.fetchMusicRecommendations(emotion)
    }
    val musicList by viewModel.musicList.collectAsState()
    val colors = listOf(
        Color(color = 0xFFFF5A5A),
        Color(color = 0xFF65995F),
        Color(color = 0xFF673AB7),
        Color(color = 0xFFFF9800),
        Color(color = 0xFF03A9F4),
        Color(color = 0xFFFFEB3B),
    )
    val darkColors = listOf(
        Color(color = 0xFFFF5A5A),
        Color(color = 0xFF65995F),
        Color(color = 0xFF673AB7),
        Color(color = 0xFFFF9800),
        Color(color = 0xFF03A9F4),
        Color(color = 0xFFFFEB3B),
    )
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build()
    }
    val colorIndex = remember {
        mutableIntStateOf(0)
    }
    LaunchedEffect(Unit) {
        colorIndex.intValue += 1
    }
    LaunchedEffect(colorIndex.intValue) {
        delay(2100)
        if (colorIndex.intValue < darkColors.lastIndex) {
            colorIndex.intValue += 1
        } else {
            colorIndex.intValue = 0
        }
    }
    val animatedColor by animateColorAsState(
        targetValue = colors[colorIndex.intValue],
        animationSpec = tween(2000), label = ""
    )
    val animateDarkColors by animateColorAsState(
        targetValue = darkColors[colorIndex.intValue],
        animationSpec = tween(2000), label = ""
    )
    val pagerState = if (musicList.isNotEmpty()) {
        rememberPagerState(pageCount = { musicList.size })
    } else {
        rememberPagerState(pageCount = { 0 })
    }

    val playingIndex = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(pagerState.currentPage) {
        playingIndex.intValue = pagerState.currentPage
        player.seekTo(pagerState.currentPage, 0)
    }
    LaunchedEffect(musicList) {
        musicList.forEach {
            val mediaItem = MediaItem.fromUri(Uri.parse(it.audioURL))
            player.addMediaItem(mediaItem)
        }
    }
    player.prepare()

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }

    val playing = remember {
        mutableStateOf(false)
    }

    val currentPosition = remember {
        mutableLongStateOf(0)
    }

    val totalDuration = remember {
        mutableLongStateOf(0)
    }

    val progressSize = remember {
        mutableStateOf(IntSize(width = 0, height = 0))
    }

    LaunchedEffect(player.isPlaying) {
        playing.value = player.isPlaying
    }
    LaunchedEffect(player.currentPosition) {
        currentPosition.longValue = player.currentPosition
    }

    LaunchedEffect(player.duration) {
        if (player.duration > 0) {
            totalDuration.longValue = player.duration
        }
    }
    LaunchedEffect(player.currentMediaItemIndex) {
        playingIndex.intValue = player.currentMediaItemIndex
        pagerState.animateScrollToPage(playingIndex.intValue, animationSpec = tween(500))
    }
    var percentageReached =
        currentPosition.longValue.toFloat() / (if (totalDuration.longValue > 0) totalDuration.longValue else 0).toFloat()
    if (percentageReached.isNaN()) {
        percentageReached = 0f
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recommended Music",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.primary
                )

            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            listOf(
                                animatedColor,
                                animateDarkColors
                            )
                        )
                    ), contentAlignment = Alignment.Center
            )
            {
                val configuration = LocalConfiguration.current

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val textColor by animateColorAsState(
                        targetValue = if (animatedColor.luminance() > .5f) Color(
                            0xFF414141
                        ) else Color.White,
                        animationSpec = tween(2000), label = ""
                    )
                    if (musicList.isNotEmpty()) {
                        AnimatedContent(
                            targetState = playingIndex.intValue,
                            label = "",
                            transitionSpec = {
                                (scaleIn() + fadeIn()) togetherWith (scaleOut() + fadeOut())
                            }) {
                            Text(text = musicList[it].title, fontSize = 40.sp, color = textColor)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                    HorizontalPager(
                        modifier = Modifier
                            .fillMaxWidth(),
                        state = pagerState,
                        pageSize = PageSize.Fixed((configuration.screenWidthDp / (1.7)).dp),
                        contentPadding = PaddingValues(horizontal = 85.dp)
                    ) { page ->
                        Card(
                            modifier = Modifier
                                .size((configuration.screenWidthDp / (1.7)).dp)
                                .graphicsLayer {
                                    val pageOffset = (
                                            (pagerState.currentPage - page) + pagerState
                                                .currentPageOffsetFraction
                                            ).absoluteValue

                                    val alphaLerp = lerp(
                                        start = 0.4f,
                                        stop = 1f,
                                        amount = 1f - pageOffset.coerceIn(0f, 1f)
                                    )

                                    val scaleLerp = lerp(
                                        start = 0.5f,
                                        stop = 1f,
                                        amount = 1f - pageOffset.coerceIn(0f, .5f)
                                    )
                                    alpha = alphaLerp
                                    scaleX = scaleLerp
                                    scaleY = scaleLerp
                                }
                                .border(3.dp, Color.White, CircleShape)
                                .padding(6.dp),
                            shape = CircleShape
                        ) {
                            if (musicList.isNotEmpty()) {
                                AsyncImage(
                                    model = musicList[page].imageURL,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(54.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = convertLongToText(currentPosition.longValue),
                            modifier = Modifier.width(55.dp),
                            color = textColor,
                            textAlign = TextAlign.Center,
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .height(8.dp)
                                .padding(horizontal = 8.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .onGloballyPositioned {
                                    progressSize.value = it.size
                                }
                                .pointerInput(Unit) {
                                    detectTapGestures {
                                        val xPos = it.x
                                        val whereIClicked =
                                            (xPos.toLong() * totalDuration.longValue) / progressSize.value.width.toLong()
                                        player.seekTo(whereIClicked)
                                    }
                                },
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction = if (playing.value) percentageReached else 0f)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xff414141))
                            )
                        }
                        Text(
                            text = convertLongToText(totalDuration.longValue),
                            modifier = Modifier.width(55.dp),
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Control(icon = R.drawable.baseline_fast_rewind_24, size = 60.dp, onClick = {
                            player.seekToPreviousMediaItem()
                        })
                        Control(
                            icon = if (playing.value) R.drawable.ic_pause else R.drawable.baseline_play_arrow_24,
                            size = 80.dp,
                            onClick = {
                                if (playing.value) {
                                    player.pause()
                                } else {
                                    player.play()
                                }
                            })
                        Control(
                            icon = R.drawable.baseline_fast_forward_24,
                            size = 60.dp,
                            onClick = {
                                player.seekToNextMediaItem()
                            })
                    }
                }
            }
        }
    )
}

fun lerp(start: Float, stop: Float, amount: Float): Float {
    return start + amount * (stop - start)
}


@Composable
fun Control(icon: Int, size: Dp, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color.White)
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 2),
            painter = painterResource(id = icon),
            tint = Color(color = 0xff414141),
            contentDescription = null
        )
    }
}

fun convertLongToText(long: Long): String {
    val sec = long / 1000
    val minutes = sec / 60
    val seconds = sec % 60

    val minutesString = if (minutes < 10) {
        "0$minutes"
    } else {
        minutes.toString()
    }

    val secondString = if (seconds < 10) {
        "0$seconds"
    } else {
        seconds.toString()
    }
    return "$minutesString:$secondString"
}

