package com.musammi.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MusammiApp()
            }
        }
    }
}

@Composable
fun MusammiApp() {

    var showRecitationScreen by remember {
        mutableStateOf(false)
    }

    if (!showRecitationScreen) {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = "مُسَمِّع",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "رفيقك في حفظ ومراجعة القرآن الكريم",
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                Button(
                    onClick = {
                        showRecitationScreen = true
                    }
                ) {
                    Text("ابدأ التسميع")
                }
            }
        }

    } else {

        RecitationScreen(
            onBack = {
                showRecitationScreen = false
            }
        )
    }
}

@Composable
fun RecitationScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var resultText by remember {
        mutableStateOf("اضغط على الميكروفون وابدأ القراءة")
    }

    var isListening by remember {
        mutableStateOf(false)
    }

    val speechRecognizer
