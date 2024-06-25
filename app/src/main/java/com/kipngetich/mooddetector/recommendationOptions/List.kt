package com.kipngetich.mooddetector.recommendationOptions

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kipngetich.mooddetector.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendationScreen(emotion: String, navController: NavController) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Recommendations",
                        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { padding ->
            val cards = listOf(
                CardData("Recommended Music", R.drawable.music, emotion),
                CardData("Guided Meditation", R.drawable.guided, emotion),
                CardData("Positive Affirmation", R.drawable.positive, emotion)
            )
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .padding(vertical = 16.dp, horizontal = 10.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(8.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = getGreetingText(emotion, username),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    content = {
                        items(items = cards, itemContent = {
                            DisplayCard(card = it, navController)
                        })
                    }
                )
            }
        }
    )
}

@Composable
fun DisplayCard(
    card: CardData,
    navController: NavController
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
            .fillMaxWidth()
            .height(180.dp)
            .clickable {
                when (card.text) {
                    "Recommended Music" -> navController.navigate("musicScreen/${card.emotion}")
                    "Guided Meditation" -> navController.navigate("guided_meditation/${card.emotion}")
                    "Positive Affirmation" -> navController.navigate("positive_affirmation/${card.emotion}")
                }
            },
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = card.cardImage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = card.text,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 5.dp)
            )
        }
    }
}

fun getGreetingText(emotion: String, username: String): String {
    return when (emotion) {
        "happy" -> "Hey $username! Here are some recommendations to keep the good vibes going!"
        "sad" -> "Hey $username, we understand. Here are some recommendations that might cheer you up."
        "angry" -> "Hey $username, take a breather! Here are some recommendations to calm your nerves."
        "neutral" -> "Hey $username! Here are some recommendations for you."
        "disgust" -> "Hey $username, we hear you! Here are some recommendations to lift your spirits."
        "fear" -> "Hey $username, don't worry, we've got your back! Check out these recommendations."
        "surprise" -> "Hey $username, Surprise! Here are some recommendations just for you."
        else -> "Hey $username! Here are some recommendations for you."
    }
}