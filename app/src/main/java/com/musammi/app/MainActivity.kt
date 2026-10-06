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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

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

/* ---------------- القرآن ---------------- */

data class QuranAyah(
    val number: Int,
    val text: String
)

val alFatiha = listOf(
    QuranAyah(1, "بسم الله الرحمن الرحيم"),
    QuranAyah(2, "الحمد لله رب العالمين"),
    QuranAyah(3, "الرحمن الرحيم"),
    QuranAyah(4, "مالك يوم الدين"),
    QuranAyah(5, "إياك نعبد وإياك نستعين"),
    QuranAyah(6, "اهدنا الصراط المستقيم"),
    QuranAyah(7, "صراط الذين أنعمت عليهم غير المغضوب عليهم ولا الضالين")
)

/* ---------------- التطبيق ---------------- */

@Composable
fun MusammiApp() {

    var screen by remember {
        mutableStateOf("home")
    }

    var selectedAyah by remember {
        mutableStateOf(1)
    }

    when (screen) {

        "home" -> HomeScreen(
            onStart = {
                screen = "selection"
            }
        )

        "selection" -> SelectionScreen(
            selectedAyah = selectedAyah,

            onAyahSelected = {
                selectedAyah = it
            },

            onStart = {
                screen = "recitation"
            },

            onBack = {
                screen = "home"
            }
        )

        "recitation" -> RecitationScreen(
            ayahNumber = selectedAyah,

            onBack = {
                screen = "selection"
            }
        )
    }
}

/* ---------------- الرئيسية ---------------- */

@Composable
fun HomeScreen(
    onStart: () -> Unit
) {

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
                text = "مُسَمِّع",
                style =
                    MaterialTheme.typography.headlineLarge
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "رفيقك في حفظ ومراجعة القرآن الكريم",
                textAlign = TextAlign.Center,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = onStart
            ) {
                Text("ابدأ التسميع")
            }
        }
    }
}

/* ---------------- اختيار السورة والآية ---------------- */

@Composable
fun SelectionScreen(
    selectedAyah: Int,
    onAyahSelected: (Int) -> Unit,
    onStart: () -> Unit,
    onBack: () -> Unit
) {

    var expanded by remember {
        mutableStateOf(false)
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
                text = "اختيار التسميع",
                style =
                    MaterialTheme.typography.headlineMedium
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Text(
                text = "السورة"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            OutlinedButton(
                onClick = {}
            ) {
                Text("سورة الفاتحة")
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "اختر الآية"
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Box {

                OutlinedButton(
                    onClick = {
                        expanded = true
                    }
                ) {
                    Text("الآية $selectedAyah")
                }

                DropdownMenu(
                    expanded = expanded,

                    onDismissRequest = {
                        expanded = false
                    }
                ) {

                    alFatiha.forEach { ayah ->

                        DropdownMenuItem(
                            text = {
                                Text(
                                    "الآية ${ayah.number}"
                                )
                            },

                            onClick = {

                                onAyahSelected(
                                    ayah.number
                                )

                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = alFatiha[
                    selectedAyah - 1
                ].text,

                textAlign = TextAlign.Center,

                style =
                    MaterialTheme.typography.bodyLarge
            )

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = onStart
            ) {
                Text("ابدأ تسميع هذه الآية")
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

/* ---------------- التسميع ---------------- */

@Composable
fun RecitationScreen(
    ayahNumber: Int,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val correctAyah =
        alFatiha[ayahNumber - 1].text

    var resultText by remember {
        mutableStateOf(
            "اضغط على الميكروفون وابدأ القراءة"
        )
    }

    var comparisonResult by remember {
        mutableStateOf("")
    }

    var isListening by remember {
        mutableStateOf(false)
    }

    val speechRecognizer = remember {
        SpeechRecognizer.createSpeechRecognizer(
            context
        )
    }

    val speechIntent = remember {

        Intent(
            RecognizerIntent.ACTION_RECOGNIZE_SPEECH
        ).apply {

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

        val listener =
            object : RecognitionListener {

                override fun onReadyForSpeech(
                    params: Bundle?
                ) {

                    isListening = true

                    resultText =
                        "أنا أستمع الآن..."

                    comparisonResult = ""
                }

                override fun onBeginningOfSpeech() {
                    isListening = true
                }

                override fun onRmsChanged(
                    rmsdB: Float
                ) {}

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {}

                override fun onEndOfSpeech() {
                    isListening = false
                }

                override fun onError(
                    error: Int
                ) {

                    isListening = false

                    resultText =
                        when (error) {

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

                override fun onResults(
                    results: Bundle?
                ) {

                    isListening = false

                    val matches =
                        results?.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION
                        )

                    if (!matches.isNullOrEmpty()) {

                        resultText =
                            matches[0]

                        comparisonResult =
                            compareRecitation(
                                correctAyah,
                                matches[0]
                            )
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {

                    val matches =
                        partialResults
                            ?.getStringArrayList(
                                SpeechRecognizer.RESULTS_RECOGNITION
                            )

                    if (!matches.isNullOrEmpty()) {

                        resultText =
                            matches[0]
                    }
                }

                override fun onEvent(
                    eventType: Int,
                    params: Bundle?
                ) {}
            }

        speechRecognizer
            .setRecognitionListener(listener)

        onDispose {

            speechRecognizer.destroy()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts
                    .RequestPermission()
        ) { granted ->

            if (granted) {

                speechRecognizer.startListening(
                    speechIntent
                )

            } else {

                resultText =
                    "يجب السماح باستخدام الميكروفون"
            }
        }

    fun startListening() {

        comparisonResult = ""

        if (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {

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
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "سورة الفاتحة • الآية $ayahNumber"
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = resultText,

                textAlign =
                    TextAlign.Center,

                style =
                    MaterialTheme.typography.bodyLarge
            )

            if (
                comparisonResult.isNotEmpty()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(24.dp)
                )

                Text(
                    text = comparisonResult,

                    textAlign =
                        TextAlign.Center,

                    style =
                        MaterialTheme.typography
                            .titleMedium
                )
            }

            Spacer(
                modifier = Modifier.height(32.dp)
            )

            Button(
                onClick = {

                    if (isListening) {

                        speechRecognizer
                            .stopListening()

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

/* ---------------- مقارنة القراءة ---------------- */

fun normalizeArabic(
    text: String
): String {

    return text
        .replace(
            Regex("[ًٌٍَُِّْـ]"),
            ""
        )
        .replace("أ", "ا")
        .replace("إ", "ا")
        .replace("آ", "ا")
        .replace("ى", "ي")
        .replace("ة", "ه")
        .replace(
            Regex("[^\\p{L}\\p{N}\\s]"),
            ""
        )
        .replace(
            Regex("\\s+"),
            " "
        )
        .trim()
}

fun compareRecitation(
    correct: String,
    spoken: String
): String {

    val correctNormalized =
        normalizeArabic(correct)

    val spokenNormalized =
        normalizeArabic(spoken)

    if (
        correctNormalized ==
        spokenNormalized
    ) {

        return "✅ أحسنت! القراءة مطابقة للآية"
    }

    val correctWords =
        correctNormalized.split(" ")

    val spokenWords =
        spokenNormalized.split(" ")

    var correctCount = 0

    val smallest =
        minOf(
            correctWords.size,
            spokenWords.size
        )

    for (i in 0 until smallest) {

        if (
            correctWords[i] ==
            spokenWords[i]
        ) {
            correctCount++
        }
    }

    val percentage =
        if (correctWords.isNotEmpty()) {

            (
                correctCount.toDouble() /
                correctWords.size.toDouble() *
                100
            ).toInt()

        } else {
            0
        }

    return when {

        percentage >= 90 ->
            "🟢 ممتاز، القراءة قريبة جدًا من الآية الصحيحة"

        percentage >= 70 ->
            "🟡 جيد، لكن يوجد اختلاف بسيط في القراءة"

        else ->
            "🔴 يوجد اختلاف. حاول قراءة الآية مرة أخرى"
    }
}
