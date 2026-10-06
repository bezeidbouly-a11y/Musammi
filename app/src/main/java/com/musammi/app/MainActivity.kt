package com.musammi.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

/* ---------------- البيانات ---------------- */

data class SurahInfo(
    val number: Int,
    val name: String,
    val ayahCount: Int
)

data class QuranAyah(
    val numberInSurah: Int,
    val globalNumber: Int,
    val text: String,
    val audio: String = ""
)

val surahs = listOf(
    SurahInfo(1, "الفاتحة", 7),
    SurahInfo(2, "البقرة", 286),
    SurahInfo(3, "آل عمران", 200),
    SurahInfo(4, "النساء", 176),
    SurahInfo(5, "المائدة", 120),
    SurahInfo(6, "الأنعام", 165),
    SurahInfo(7, "الأعراف", 206),
    SurahInfo(8, "الأنفال", 75),
    SurahInfo(9, "التوبة", 129),
    SurahInfo(10, "يونس", 109),
    SurahInfo(11, "هود", 123),
    SurahInfo(12, "يوسف", 111),
    SurahInfo(13, "الرعد", 43),
    SurahInfo(14, "إبراهيم", 52),
    SurahInfo(15, "الحجر", 99),
    SurahInfo(16, "النحل", 128),
    SurahInfo(17, "الإسراء", 111),
    SurahInfo(18, "الكهف", 110),
    SurahInfo(19, "مريم", 98),
    SurahInfo(20, "طه", 135),
    SurahInfo(21, "الأنبياء", 112),
    SurahInfo(22, "الحج", 78),
    SurahInfo(23, "المؤمنون", 118),
    SurahInfo(24, "النور", 64),
    SurahInfo(25, "الفرقان", 77),
    SurahInfo(26, "الشعراء", 227),
    SurahInfo(27, "النمل", 93),
    SurahInfo(28, "القصص", 88),
    SurahInfo(29, "العنكبوت", 69),
    SurahInfo(30, "الروم", 60),
    SurahInfo(31, "لقمان", 34),
    SurahInfo(32, "السجدة", 30),
    SurahInfo(33, "الأحزاب", 73),
    SurahInfo(34, "سبأ", 54),
    SurahInfo(35, "فاطر", 45),
    SurahInfo(36, "يس", 83),
    SurahInfo(37, "الصافات", 182),
    SurahInfo(38, "ص", 88),
    SurahInfo(39, "الزمر", 75),
    SurahInfo(40, "غافر", 85),
    SurahInfo(41, "فصلت", 54),
    SurahInfo(42, "الشورى", 53),
    SurahInfo(43, "الزخرف", 89),
    SurahInfo(44, "الدخان", 59),
    SurahInfo(45, "الجاثية", 37),
    SurahInfo(46, "الأحقاف", 35),
    SurahInfo(47, "محمد", 38),
    SurahInfo(48, "الفتح", 29),
    SurahInfo(49, "الحجرات", 18),
    SurahInfo(50, "ق", 45),
    SurahInfo(51, "الذاريات", 60),
    SurahInfo(52, "الطور", 49),
    SurahInfo(53, "النجم", 62),
    SurahInfo(54, "القمر", 55),
    SurahInfo(55, "الرحمن", 78),
    SurahInfo(56, "الواقعة", 96),
    SurahInfo(57, "الحديد", 29),
    SurahInfo(58, "المجادلة", 22),
    SurahInfo(59, "الحشر", 24),
    SurahInfo(60, "الممتحنة", 13),
    SurahInfo(61, "الصف", 14),
    SurahInfo(62, "الجمعة", 11),
    SurahInfo(63, "المنافقون", 11),
    SurahInfo(64, "التغابن", 18),
    SurahInfo(65, "الطلاق", 12),
    SurahInfo(66, "التحريم", 12),
    SurahInfo(67, "الملك", 30),
    SurahInfo(68, "القلم", 52),
    SurahInfo(69, "الحاقة", 52),
    SurahInfo(70, "المعارج", 44),
    SurahInfo(71, "نوح", 28),
    SurahInfo(72, "الجن", 28),
    SurahInfo(73, "المزمل", 20),
    SurahInfo(74, "المدثر", 56),
    SurahInfo(75, "القيامة", 40),
    SurahInfo(76, "الإنسان", 31),
    SurahInfo(77, "المرسلات", 50),
    SurahInfo(78, "النبأ", 40),
    SurahInfo(79, "النازعات", 46),
    SurahInfo(80, "عبس", 42),
    SurahInfo(81, "التكوير", 29),
    SurahInfo(82, "الانفطار", 19),
    SurahInfo(83, "المطففين", 36),
    SurahInfo(84, "الانشقاق", 25),
    SurahInfo(85, "البروج", 22),
    SurahInfo(86, "الطارق", 17),
    SurahInfo(87, "الأعلى", 19),
    SurahInfo(88, "الغاشية", 26),
    SurahInfo(89, "الفجر", 30),
    SurahInfo(90, "البلد", 20),
    SurahInfo(91, "الشمس", 15),
    SurahInfo(92, "الليل", 21),
    SurahInfo(93, "الضحى", 11),
    SurahInfo(94, "الشرح", 8),
    SurahInfo(95, "التين", 8),
    SurahInfo(96, "العلق", 19),
    SurahInfo(97, "القدر", 5),
    SurahInfo(98, "البينة", 8),
    SurahInfo(99, "الزلزلة", 8),
    SurahInfo(100, "العاديات", 11),
    SurahInfo(101, "القارعة", 11),
    SurahInfo(102, "التكاثر", 8),
    SurahInfo(103, "العصر", 3),
    SurahInfo(104, "الهمزة", 9),
    SurahInfo(105, "الفيل", 5),
    SurahInfo(106, "قريش", 4),
    SurahInfo(107, "الماعون", 7),
    SurahInfo(108, "الكوثر", 3),
    SurahInfo(109, "الكافرون", 6),
    SurahInfo(110, "النصر", 3),
    SurahInfo(111, "المسد", 5),
    SurahInfo(112, "الإخلاص", 4),
    SurahInfo(113, "الفلق", 5),
    SurahInfo(114, "الناس", 6)
)

/* ---------------- Activity ---------------- */

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

/* ---------------- التطبيق ---------------- */

@Composable
fun MusammiApp() {

    var screen by remember { mutableStateOf("home") }

    var selectedSurah by remember {
        mutableStateOf(surahs[0])
    }

    var selectedAyah by remember {
        mutableStateOf<QuranAyah?>(null)
    }

    when (screen) {

        "home" -> HomeScreen(
            onMushaf = {
                screen = "surahs"
            },
            onRecitation = {
                screen = "recitationSurahs"
            }
        )

        "surahs" -> SurahListScreen(
            title = "المصحف الكريم",
            onSurah = {
                selectedSurah = it
                screen = "mushaf"
            },
            onBack = {
                screen = "home"
            }
        )

        "mushaf" -> MushafScreen(
            surah = selectedSurah,
            onBack = {
                screen = "surahs"
            }
        )

        "recitationSurahs" -> SurahListScreen(
            title = "اختر سورة للتسميع",
            onSurah = {
                selectedSurah = it
                screen = "recitationAyahs"
            },
            onBack = {
                screen = "home"
            }
        )

        "recitationAyahs" -> AyahSelectionScreen(
            surah = selectedSurah,
            onAyah = {
                selectedAyah = it
                screen = "recitation"
            },
            onBack = {
                screen = "recitationSurahs"
            }
        )

        "recitation" -> {
            selectedAyah?.let {
                RecitationScreen(
                    surah = selectedSurah,
                    ayah = it,
                    onBack = {
                        screen = "recitationAyahs"
                    }
                )
            }
        }
    }
}

/* ---------------- الرئيسية ---------------- */

@Composable
fun HomeScreen(
    onMushaf: () -> Unit,
    onRecitation: () -> Unit
) {

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
                "مُسَمِّع",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Text(
                "رفيقك في حفظ ومراجعة القرآن الكريم",
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onMushaf,
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text("📖 المصحف الكريم")
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onRecitation,
                modifier = Modifier.fillMaxWidth(0.75f)
            ) {
                Text("🎙️ اختبار الحفظ")
            }
        }
    }
}

/* ---------------- قائمة السور ---------------- */

@Composable
fun SurahListScreen(
    title: String,
    onSurah: (SurahInfo) -> Unit,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack
        ) {
            Text("رجوع")
        }

        Spacer(Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            items(surahs) { surah ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clickable {
                            onSurah(surah)
                        }
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Text(
                            "${surah.number}"
                        )

                        Column(
                            horizontalAlignment = Alignment.End
                        ) {

                            Text(
                                "سورة ${surah.name}",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                "${surah.ayahCount} آية",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- تحميل السورة ---------------- */

suspend fun loadSurah(
    surahNumber: Int
): List<QuranAyah> {

    return kotlinx.coroutines.Dispatchers.IO.let { dispatcher ->

        kotlinx.coroutines.withContext(dispatcher) {

            val result = mutableListOf<QuranAyah>()

            try {

                val apiUrl =
                    "https://api.alquran.cloud/v1/surah/$surahNumber/ar.alafasy"

                val connection =
                    URL(apiUrl).openConnection() as HttpsURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                val jsonText =
                    connection.inputStream
                        .bufferedReader()
                        .use { it.readText() }

                connection.disconnect()

                val root = JSONObject(jsonText)

                val data =
                    root.getJSONObject("data")

                val ayahs =
                    data.getJSONArray("ayahs")

                for (i in 0 until ayahs.length()) {

                    val item =
                        ayahs.getJSONObject(i)

                    result.add(
                        QuranAyah(
                            numberInSurah =
                                item.getInt("numberInSurah"),

                            globalNumber =
                                item.getInt("number"),

                            text =
                                item.getString("text"),

                            audio =
                                item.optString("audio", "")
                        )
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

            result
        }
    }
}

/* ---------------- المصحف ---------------- */

@Composable
fun MushafScreen(
    surah: SurahInfo,
    onBack: () -> Unit
) {

    var ayahs by remember {
        mutableStateOf<List<QuranAyah>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var error by remember {
        mutableStateOf(false)
    }

    var playingNumber by remember {
        mutableStateOf<Int?>(null)
    }

    var mediaPlayer by remember {
        mutableStateOf<MediaPlayer?>(null)
    }

    LaunchedEffect(surah.number) {

        loading = true
        error = false

        ayahs = loadSurah(surah.number)

        loading = false

        if (ayahs.isEmpty()) {
            error = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedButton(
                onClick = onBack
            ) {
                Text("رجوع")
            }

            Text(
                "سورة ${surah.name}",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        Spacer(Modifier.height(16.dp))

        when {

            loading -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error -> {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        "تعذر تحميل السورة.\nتأكد من اتصال الإنترنت.",
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {

                LazyColumn {

                    items(ayahs) { ayah ->

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                        ) {

                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {

                                Text(
                                    text =
                                        "${ayah.text}  ﴿${ayah.numberInSurah}﴾",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Right,
                                    style =
                                        MaterialTheme.typography.titleMedium
                                )

                                Spacer(Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = {

                                        mediaPlayer?.release()
                                        mediaPlayer = null

                                        if (ayah.audio.isNotBlank()) {

                                            try {

                                                playingNumber =
                                                    ayah.numberInSurah

                                                val player =
                                                    MediaPlayer()

                                                player.setDataSource(
                                                    ayah.audio
                                                )

                                                player.setOnPreparedListener {
                                                    it.start()
                                                }

                                                player.setOnCompletionListener {
                                                    playingNumber = null
                                                    it.release()

                                                    if (mediaPlayer === it) {
                                                        mediaPlayer = null
                                                    }
                                                }

                                                player.setOnErrorListener {
                                                        mp, _, _ ->

                                                    playingNumber = null
                                                    mp.release()

                                                    if (mediaPlayer === mp) {
                                                        mediaPlayer = null
                                                    }

                                                    true
                                                }

                                                mediaPlayer = player
                                                player.prepareAsync()

                                            } catch (
                                                e: Exception
                                            ) {
                                                playingNumber = null
                                            }
                                        }
                                    }
                                ) {

                                    Text(
                                        if (
                                            playingNumber ==
                                            ayah.numberInSurah
                                        ) {
                                            "🔊 جارٍ التشغيل"
                                        } else {
                                            "▶️ استمع"
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- اختيار آية للتسميع ---------------- */

@Composable
fun AyahSelectionScreen(
    surah: SurahInfo,
    onAyah: (QuranAyah) -> Unit,
    onBack: () -> Unit
) {

    var ayahs by remember {
        mutableStateOf<List<QuranAyah>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(surah.number) {

        loading = true
        ayahs = loadSurah(surah.number)
        loading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "تسميع سورة ${surah.name}",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(Modifier.height(12.dp))

        OutlinedButton(
            onClick = onBack
        ) {
            Text("رجوع")
        }

        Spacer(Modifier.height(12.dp))

        if (loading) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        } else if (ayahs.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("تعذر تحميل الآيات. تحقق من الإنترنت.")
            }

        } else {

            LazyColumn {

                items(ayahs) { ayah ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable {
                                onAyah(ayah)
                            }
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {

                            Text(
                                "الآية ${ayah.numberInSurah}",
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                ayah.text,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Right
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ---------------- التسميع ---------------- */

@Composable
fun RecitationScreen(
    surah: SurahInfo,
    ayah: QuranAyah,
    onBack: () -> Unit
) {

    val context = LocalContext.current

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
        SpeechRecognizer.createSpeechRecognizer(context)
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
                    resultText = "أنا أستمع الآن..."
                    comparisonResult = ""
                }

                override fun onBeginningOfSpeech() {
                    isListening = true
                }

                override fun onRmsChanged(rmsdB: Float) {}

                override fun onBufferReceived(
                    buffer: ByteArray?
                ) {}

                override fun onEndOfSpeech() {
                    isListening = false
                }

                override fun onError(error: Int) {

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

                        resultText = matches[0]

                        comparisonResult =
                            compareRecitation(
                                ayah.text,
                                matches[0]
                            )
                    }
                }

                override fun onPartialResults(
                    partialResults: Bundle?
                ) {

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

        speechRecognizer.setRecognitionListener(
            listener
        )

        onDispose {
            speechRecognizer.destroy()
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
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
                "التسميع",
                style =
                    MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "سورة ${surah.name} • الآية ${ayah.numberInSurah}"
            )

            Spacer(Modifier.height(24.dp))

            Text(
                resultText,
                textAlign = TextAlign.Center,
                style =
                    MaterialTheme.typography.bodyLarge
            )

            if (comparisonResult.isNotEmpty()) {

                Spacer(Modifier.height(24.dp))

                Text(
                    comparisonResult,
                    textAlign = TextAlign.Center,
                    style =
                        MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(32.dp))

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
                    if (isListening) {
                        "إيقاف الاستماع"
                    } else {
                        "ابدأ القراءة 🎤"
                    }
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBack
            ) {
                Text("رجوع")
            }
        }
    }
}

/* ---------------- مقارنة القراءة ---------------- */

fun normalizeArabic(text: String): String {

    return text
        .replace(
            Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06EDـ]"),
            ""
        )
        .replace("أ", "ا")
        .replace("إ", "ا")
        .replace("آ", "ا")
        .replace("ٱ", "ا")
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

    if (correctNormalized == spokenNormalized) {
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
                    correctWords.size *
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
