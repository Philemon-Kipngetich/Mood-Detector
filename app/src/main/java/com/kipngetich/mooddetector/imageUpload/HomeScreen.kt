package com.kipngetich.mooddetector.imageUpload

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BrightnessHigh
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kipngetich.mooddetector.R
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, isDarkTheme: Boolean, onToggleTheme: () -> Unit) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val username = sharedPreferences.getString("username", "") ?: ""
    var isProcessRunning by remember { mutableStateOf(false) }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person, contentDescription = "User Icon",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = "Hello, $username",
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp, start = 16.dp)
                        .clickable { onToggleTheme() },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { onToggleTheme() }) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Outlined.Nightlight else Icons.Outlined.BrightnessHigh,
                            contentDescription = "Toggle Theme"
                        )
                    }
                    Text(
                        text = if (isDarkTheme) "Dark Theme" else "Light Theme",
                        modifier = Modifier.padding(start = 4.dp) // Adjust padding as needed
                    )
                }
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp, start = 16.dp)
                        .clickable {
                            sharedPreferences
                                .edit()
                                .remove("username")
                                .apply()
                            navController.navigate("userScreen") {
                                popUpTo("homeScreen") { inclusive = true }
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        sharedPreferences.edit().remove("username").apply()
                        navController.navigate("userScreen") {
                            popUpTo("homeScreen") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                    Text(
                        text = "Logout",
                        modifier = Modifier.padding(start = 4.dp) // Adjust padding as needed
                    )
                }
            }
        }, drawerState = drawerState
    ) {
        Scaffold(topBar = {
            TopAppBar(title = {
                Text(
                    text = "MoodUp",
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
            }, colors = TopAppBarDefaults.topAppBarColors(
                MaterialTheme.colorScheme.primary
            ), navigationIcon = {
                IconButton(onClick = { scope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Default.Menu, contentDescription = "Menu"
                    )
                }
            })
        }) { padding ->
            Box(modifier = Modifier.padding(padding)) {
                var isImageSelected by remember { mutableStateOf(false) }
                val img: Bitmap = BitmapFactory.decodeResource(
                    Resources.getSystem(), android.R.drawable.ic_menu_report_image
                )
                val bitmap = remember {
                    mutableStateOf(img)
                }

                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.TakePicturePreview()
                ) {
                    if (it != null) {
                        bitmap.value = it
                        isImageSelected = true
                    }
                }
                var showDialog by remember {
                    mutableStateOf(false)
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 100.dp)
                ) {
                    Text(
                        text = "Image Processing",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    if (isImageSelected) {
                        Image(
                            bitmap = bitmap.value.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(250.dp)
                                .background(Color.Blue)
                                .border(
                                    width = 1.dp, color = Color.White, shape = CircleShape
                                )
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(250.dp)
                                .clip(CircleShape)
                                .background(Color.Gray)
                                .border(
                                    width = 1.dp, color = Color.White, shape = CircleShape
                                )
                                .clickable { showDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Take a photo", color = Color.White
                            )
                        }
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp)

                ) {
                    if (isImageSelected) {
                        if (isProcessRunning) {
                            Row(
                                modifier = Modifier.padding(top = 90.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(color = Color.Blue)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Processing...",
                                    fontSize = 16.sp,
                                )
                            }
                        } else {
                            Button(
                                modifier = Modifier.padding(top = 90.dp), onClick = {
                                    uploadImageToServer(
                                        bitmap.value, context,
                                        onProcessChanged = { process ->
                                            isProcessRunning = process
                                        }
                                    ) { imageUrl ->
                                        sendEmotionDetectionRequest(
                                            imageUrl, context,
                                            onProcessStateChanged = { isProcessing ->
                                                isProcessRunning = isProcessing
                                            }
                                        ) { detectedEmotion ->
                                            navController.navigate("listScreen/${detectedEmotion}")

                                        }
                                    }
                                }, colors = ButtonDefaults.buttonColors(
                                    Color(0xFF3F51B5)
                                )
                            ) {
                                Text(
                                    text = "Process", fontSize = 15.sp, color = Color.White
                                )
                            }
                        }
                    }
                }
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 10.dp)
                ) {
                    if (showDialog) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .width(150.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF3F51B5))
                        ) {
                            Column(
                                modifier = Modifier.padding(start = 45.dp)
                            ) {
                                Image(painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .clickable {
                                            launcher.launch()
                                            showDialog = false
                                        })
                                Text(
                                    text = "Camera", color = Color.White
                                )
                            }
                            Column(
                                modifier = Modifier.padding(start = 20.dp, bottom = 70.dp)
                            ) {
                                Icon(imageVector = Icons.Rounded.Close,
                                    contentDescription = "Close",
                                    tint = Color.White,
                                    modifier = Modifier.clickable { showDialog = false })
                            }
                        }
                    }
                }
            }
        }
    }
}

