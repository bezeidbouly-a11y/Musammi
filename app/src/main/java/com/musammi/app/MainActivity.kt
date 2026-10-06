package com.musammi.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
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

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(context)
    }

    val speechIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "ar"
            )

            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "ar"
            )

            putExtra(
                RecognizerIntent.EXTRA_PARTIAL_RESULTS,
                true
            )
        }
    }

    DisposableEffect(speechRecognizer) {

        val listener = object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
                resultText = "أنا أستمع الآن..."
            }

            override fun onBeginningOfSpeech() {
                isListening = true
            }

            override fun onRmsChanged(rmsdB: Float) {}

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) {
                isListening = false

                resultText = when (error) {

                    SpeechRecognizer.ERROR_NO_MATCH ->
                        "لم أفهم القراءة، حاول مرة أخرى"

                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT ->
                        "لم أسمع صوتًا، حاول مرة أخرى"

                    SpeechRecognizer.ERROR_AUDIO ->
                        "حدث خطأ في الميكروفون"

                    SpeechRecognizer.ERROR_NETWORK ->
                        "حدث خطأ في الاتصال"

                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS ->
                        "يجب السماح باستخدام الميكروفون"

                    else ->
                        "تعذر التعرف على الصوت، حاول مرة أخرى"
                }
            }

            override fun onResults(results: Bundle?) {

                isListening = false

                val matches =
                    results?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                if (!matches.isNullOrEmpty()) {
                    resultText = matches[0]
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {

                val matches =
                    partialResults?.getStringArrayList(
                        SpeechRecognizer.RESULTS_RECOGNITION
                    )

                if (!matches.isNullOrEmpty()) {
                    resultText = matches[0]
                }
            }

            override fun onEvent(
                eventType: Int,
                params: Bundle?
            ) {}
        }

        speechRecognizer.setRecognitionListener(listener)

        onDispose {
            speechRecognizer.destroy()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->

            if (granted) {

                resultText = "ابدأ القراءة..."

                speechRecognizer.startListening(
                    speechIntent
                )

            } else {

                resultText =
                    "يجب السماح للتطبيق باستخدام الميكروفون"
            }
        }

    fun startListening() {

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            resultText = "ابدأ القراءة..."

            speechRecognizer.startListening(
                speechIntent
            )

        } else {

            permissionLauncher.launch(
                Manifest.permission.RECORD_AUDIO
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center
        ) {

            Text(
                text = "التسميع",
                style =
                    MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = resultText,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = {

                    if (isListening) {

                        speechRecognizer.stopListening()
                        isListening = false

                    } else {

                        startListening()
                    }
                }
            ) {

                Text(
                    if (isListening)
                        "إيقاف الاستماع"
                    else
                        "ابدأ القراءة 🎤"
                )
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            OutlinedButton(
                onClick = onBack
            ) {
                Text("رجوع")
            }
        }
    }
}
