package com.kipngetich.mooddetector

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kipngetich.mooddetector.affirmation.PositiveAffirmationScreen
import com.kipngetich.mooddetector.imageUpload.HomeScreen
import com.kipngetich.mooddetector.meditation.GuidedMeditation
import com.kipngetich.mooddetector.musicRecommendation.MusicScreen
import com.kipngetich.mooddetector.recommendationOptions.RecommendationScreen
import com.kipngetich.mooddetector.ui.theme.MoodDetectorTheme
import com.kipngetich.mooddetector.username.UsernameScreen

class MainActivity : ComponentActivity() {

    private lateinit var themeSharedPreferences: SharedPreferences
    private lateinit var userSharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        themeSharedPreferences = getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val isDarkTheme = themeSharedPreferences.getBoolean("is_dark_theme", false)

        userSharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val username = userSharedPreferences.getString("username", null)
        setContent {
            MoodDetectorTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(isDarkTheme, username)
                }
            }
        }
    }

    private fun toggleTheme() {
        val currentTheme = themeSharedPreferences.getBoolean("is_dark_theme", false)
        val newTheme = !currentTheme
        themeSharedPreferences.edit().putBoolean("is_dark_theme", newTheme).apply()
        recreate()
    }

    @Composable
    fun MyApp(isDarkTheme: Boolean, username: String?) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = if (username == null) "userScreen" else "homeScreen"
        ) {
            composable("userScreen") {
                UsernameScreen(navController = navController, userSharedPreferences = userSharedPreferences)
            }
            composable("homeScreen") {
                HomeScreen(navController, isDarkTheme) { toggleTheme() }
            }
            composable(
                route = "listScreen/{emotion}",
                arguments = listOf(navArgument("emotion") { type = NavType.StringType })
            ) { backStackEntry ->
                RecommendationScreen(
                    emotion = backStackEntry.arguments?.getString("emotion") ?: "",
                    navController
                )
            }
            composable(
                "musicScreen/{emotion}",
                arguments = listOf(navArgument("emotion") { type = NavType.StringType })
            ) { backStackEntry ->
                MusicScreen(
                    emotion = backStackEntry.arguments?.getString("emotion") ?: ""
                )
            }
            composable(
                "guided_meditation/{emotion}",
                arguments = listOf(navArgument("emotion") { type = NavType.StringType })
            ) { backStackEntry ->
                GuidedMeditation(
                    emotion = backStackEntry.arguments?.getString("emotion") ?: ""
                )
            }
            composable(
                "positive_affirmation/{emotion}",
                arguments = listOf(navArgument("emotion") { type = NavType.StringType })
            ) { backStackEntry ->
                PositiveAffirmationScreen(
                    emotion = backStackEntry.arguments?.getString("emotion") ?: ""
                )
            }

        }

    }
}
