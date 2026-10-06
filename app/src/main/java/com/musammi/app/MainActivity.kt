package com.musammi.app

import android.Manifest
import android.content.Context
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

// ---------- Models ----------
data class SurahInfo(val number:Int,val name:String,val ayahCount:Int)
data class QuranAyah(val numberInSurah:Int,val globalNumber:Int,val text:String,val audio:String="")
data class Reciter(val label:String,val edition:String)
data class DiffResult(val score:Int,val correct:Int,val missing:List<String>,val extra:List<String>,val different:List<String>)

val reciters=listOf(Reciter("مشاري العفاسي","ar.alafasy"),Reciter("عبد الباسط عبد الصمد","ar.abdulbasitmurattal"))

val surahs=listOf(
SurahInfo(1,"الفاتحة",7),SurahInfo(2,"البقرة",286),SurahInfo(3,"آل عمران",200),SurahInfo(4,"النساء",176),SurahInfo(5,"المائدة",120),SurahInfo(6,"الأنعام",165),SurahInfo(7,"الأعراف",206),SurahInfo(8,"الأنفال",75),SurahInfo(9,"التوبة",129),SurahInfo(10,"يونس",109),SurahInfo(11,"هود",123),SurahInfo(12,"يوسف",111),SurahInfo(13,"الرعد",43),SurahInfo(14,"إبراهيم",52),SurahInfo(15,"الحجر",99),SurahInfo(16,"النحل",128),SurahInfo(17,"الإسراء",111),SurahInfo(18,"الكهف",110),SurahInfo(19,"مريم",98),SurahInfo(20,"طه",135),SurahInfo(21,"الأنبياء",112),SurahInfo(22,"الحج",78),SurahInfo(23,"المؤمنون",118),SurahInfo(24,"النور",64),SurahInfo(25,"الفرقان",77),SurahInfo(26,"الشعراء",227),SurahInfo(27,"النمل",93),SurahInfo(28,"القصص",88),SurahInfo(29,"العنكبوت",69),SurahInfo(30,"الروم",60),SurahInfo(31,"لقمان",34),SurahInfo(32,"السجدة",30),SurahInfo(33,"الأحزاب",73),SurahInfo(34,"سبأ",54),SurahInfo(35,"فاطر",45),SurahInfo(36,"يس",83),SurahInfo(37,"الصافات",182),SurahInfo(38,"ص",88),SurahInfo(39,"الزمر",75),SurahInfo(40,"غافر",85),SurahInfo(41,"فصلت",54),SurahInfo(42,"الشورى",53),SurahInfo(43,"الزخرف",89),SurahInfo(44,"الدخان",59),SurahInfo(45,"الجاثية",37),SurahInfo(46,"الأحقاف",35),SurahInfo(47,"محمد",38),SurahInfo(48,"الفتح",29),SurahInfo(49,"الحجرات",18),SurahInfo(50,"ق",45),SurahInfo(51,"الذاريات",60),SurahInfo(52,"الطور",49),SurahInfo(53,"النجم",62),SurahInfo(54,"القمر",55),SurahInfo(55,"الرحمن",78),SurahInfo(56,"الواقعة",96),SurahInfo(57,"الحديد",29),SurahInfo(58,"المجادلة",22),SurahInfo(59,"الحشر",24),SurahInfo(60,"الممتحنة",13),SurahInfo(61,"الصف",14),SurahInfo(62,"الجمعة",11),SurahInfo(63,"المنافقون",11),SurahInfo(64,"التغابن",18),SurahInfo(65,"الطلاق",12),SurahInfo(66,"التحريم",12),SurahInfo(67,"الملك",30),SurahInfo(68,"القلم",52),SurahInfo(69,"الحاقة",52),SurahInfo(70,"المعارج",44),SurahInfo(71,"نوح",28),SurahInfo(72,"الجن",28),SurahInfo(73,"المزمل",20),SurahInfo(74,"المدثر",56),SurahInfo(75,"القيامة",40),SurahInfo(76,"الإنسان",31),SurahInfo(77,"المرسلات",50),SurahInfo(78,"النبأ",40),SurahInfo(79,"النازعات",46),SurahInfo(80,"عبس",42),SurahInfo(81,"التكوير",29),SurahInfo(82,"الانفطار",19),SurahInfo(83,"المطففين",36),SurahInfo(84,"الانشقاق",25),SurahInfo(85,"البروج",22),SurahInfo(86,"الطارق",17),SurahInfo(87,"الأعلى",19),SurahInfo(88,"الغاشية",26),SurahInfo(89,"الفجر",30),SurahInfo(90,"البلد",20),SurahInfo(91,"الشمس",15),SurahInfo(92,"الليل",21),SurahInfo(93,"الضحى",11),SurahInfo(94,"الشرح",8),SurahInfo(95,"التين",8),SurahInfo(96,"العلق",19),SurahInfo(97,"القدر",5),SurahInfo(98,"البينة",8),SurahInfo(99,"الزلزلة",8),SurahInfo(100,"العاديات",11),SurahInfo(101,"القارعة",11),SurahInfo(102,"التكاثر",8),SurahInfo(103,"العصر",3),SurahInfo(104,"الهمزة",9),SurahInfo(105,"الفيل",5),SurahInfo(106,"قريش",4),SurahInfo(107,"الماعون",7),SurahInfo(108,"الكوثر",3),SurahInfo(109,"الكافرون",6),SurahInfo(110,"النصر",3),SurahInfo(111,"المسد",5),SurahInfo(112,"الإخلاص",4),SurahInfo(113,"الفلق",5),SurahInfo(114,"الناس",6))

class MainActivity:ComponentActivity(){override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);setContent{MaterialTheme{MusammiApp()}}}}

@Composable fun MusammiApp(){
 val context=LocalContext.current; val prefs=remember{context.getSharedPreferences("musammi",Context.MODE_PRIVATE)}
 var screen by remember{mutableStateOf("home")}; var surah by remember{mutableStateOf(surahs[(prefs.getInt("lastSurah",1)-1).coerceIn(0,113)])}
 var reciter by remember{mutableStateOf(reciters.firstOrNull{it.edition==prefs.getString("reciter",reciters[0].edition)}?:reciters[0])}
 var showText by remember{mutableStateOf(prefs.getBoolean("showText",false))}
 var rangeStart by remember{mutableStateOf(1)};var rangeEnd by remember{mutableStateOf(1)}
 when(screen){
  "home"->HomeScreen({screen="mushafList"},{screen="listenList"},{screen="testList"},{screen="settings"})
  "mushafList"->SurahList("المصحف الكريم",{surah=it;prefs.edit().putInt("lastSurah",it.number).apply();screen="mushaf"},{screen="home"})
  "listenList"->SurahList("الاستماع",{surah=it;screen="listen"},{screen="home"})
  "testList"->SurahList("اختبار الحفظ",{surah=it;rangeStart=1;rangeEnd=it.ayahCount;screen="range"},{screen="home"})
  "mushaf"->QuranScreen(surah,reciter,false,{screen="mushafList"})
  "listen"->QuranScreen(surah,reciter,true,{screen="listenList"})
  "range"->RangeScreen(surah,rangeStart,rangeEnd,{rangeStart=it;if(rangeEnd<it)rangeEnd=it},{rangeEnd=it},{screen="test"},{screen="testList"})
  "test"->RecitationSession(surah,rangeStart,rangeEnd,showText,reciter,{screen="range"})
  "settings"->SettingsScreen(reciter,showText,{reciter=it;prefs.edit().putString("reciter",it.edition).apply()},{showText=it;prefs.edit().putBoolean("showText",it).apply()},{screen="home"})
 }
}

@Composable fun HomeScreen(mushaf:()->Unit,listen:()->Unit,test:()->Unit,settings:()->Unit){Surface(Modifier.fillMaxSize()){Column(Modifier.fillMaxSize().padding(24.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.Center){Text("مُسَمِّع",style=MaterialTheme.typography.headlineLarge,fontWeight=FontWeight.Bold);Text("رفيقك في حفظ ومراجعة القرآن الكريم",textAlign=TextAlign.Center);Spacer(Modifier.height(32.dp));listOf("📖 المصحف الكريم" to mushaf,"🎧 الاستماع" to listen,"🎙️ اختبار الحفظ" to test,"⚙️ الإعدادات" to settings).forEach{(t,a)->Button(a,Modifier.fillMaxWidth(.78f)){Text(t)};Spacer(Modifier.height(12.dp))}}}}

@Composable fun SurahList(title:String,onSurah:(SurahInfo)->Unit,onBack:()->Unit){Column(Modifier.fillMaxSize().padding(16.dp)){Header(title,onBack);LazyColumn{items(surahs){s->Card(Modifier.fillMaxWidth().padding(vertical=4.dp).clickable{onSurah(s)}){Row(Modifier.fillMaxWidth().padding(16.dp),horizontalArrangement=Arrangement.SpaceBetween){Text("${s.number}");Column(horizontalAlignment=Alignment.End){Text("سورة ${s.name}",fontWeight=FontWeight.Bold);Text("${s.ayahCount} آية")}}}}}}}
@Composable fun Header(title:String,onBack:()->Unit){Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically){OutlinedButton(onBack){Text("رجوع")};Text(title,style=MaterialTheme.typography.headlineSmall)}}

suspend fun loadSurah(n:Int,edition:String):List<QuranAyah> = withContext(Dispatchers.IO){val out=mutableListOf<QuranAyah>();try{val c=URL("https://api.alquran.cloud/v1/surah/$n/$edition").openConnection() as HttpsURLConnection;c.connectTimeout=15000;c.readTimeout=15000;val root=JSONObject(c.inputStream.bufferedReader().use{it.readText()});val a=root.getJSONObject("data").getJSONArray("ayahs");for(i in 0 until a.length()){val x=a.getJSONObject(i);out+=QuranAyah(x.getInt("numberInSurah"),x.getInt("number"),x.getString("text"),x.optString("audio",""))};c.disconnect()}catch(_:Exception){};out}

@Composable fun QuranScreen(surah:SurahInfo,reciter:Reciter,auto:Boolean,onBack:()->Unit){var ayahs by remember{mutableStateOf(emptyList<QuranAyah>())};var loading by remember{mutableStateOf(true)};var player by remember{mutableStateOf<MediaPlayer?>(null)};var playing by remember{mutableStateOf<Int?>(null)};LaunchedEffect(surah.number,reciter.edition){loading=true;ayahs=loadSurah(surah.number,reciter.edition);loading=false};DisposableEffect(Unit){onDispose{player?.release()}}
 fun playAt(index:Int,continueAll:Boolean){if(index !in ayahs.indices)return;player?.release();val a=ayahs[index];if(a.audio.isBlank())return;playing=a.numberInSurah;MediaPlayer().also{p->player=p;p.setDataSource(a.audio);p.setOnPreparedListener{it.start()};p.setOnCompletionListener{mp->mp.release();if(continueAll&&index+1<ayahs.size)playAt(index+1,true) else {playing=null;player=null}};p.prepareAsync()}}
 Column(Modifier.fillMaxSize().padding(16.dp)){Header("سورة ${surah.name}",onBack);Text("القارئ: ${reciter.label}");if(auto){Button({if(ayahs.isNotEmpty())playAt(0,true)}){Text("▶️ تشغيل السورة كاملة")}};if(loading)Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){CircularProgressIndicator()}else if(ayahs.isEmpty())Text("تعذر تحميل السورة. تحقق من الإنترنت.")else LazyColumn{items(ayahs){a->Card(Modifier.fillMaxWidth().padding(vertical=5.dp)){Column(Modifier.padding(15.dp)){Text("${a.text}  ﴿${a.numberInSurah}﴾",Modifier.fillMaxWidth(),textAlign=TextAlign.Right);OutlinedButton({playAt(ayahs.indexOf(a),false)}){Text(if(playing==a.numberInSurah)"🔊 جارٍ التشغيل" else "▶️ استمع")}}}}}}}

@Composable fun RangeScreen(s:SurahInfo,start:Int,end:Int,onStart:(Int)->Unit,onEnd:(Int)->Unit,onGo:()->Unit,onBack:()->Unit){Column(Modifier.fillMaxSize().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){Header("تسميع سورة ${s.name}",onBack);Spacer(Modifier.height(24.dp));Text("من الآية: $start");Row{Button({if(start>1)onStart(start-1)}){Text("−")};Spacer(Modifier.width(12.dp));Button({if(start<s.ayahCount)onStart(start+1)}){Text("+")}};Spacer(Modifier.height(24.dp));Text("إلى الآية: $end");Row{Button({if(end>start)onEnd(end-1)}){Text("−")};Spacer(Modifier.width(12.dp));Button({if(end<s.ayahCount)onEnd(end+1)}){Text("+")}};Spacer(Modifier.height(30.dp));Button(onGo){Text("ابدأ التسميع من $start إلى $end")}}}

@Composable fun SettingsScreen(reciter:Reciter,showText:Boolean,onReciter:(Reciter)->Unit,onShow:(Boolean)->Unit,onBack:()->Unit){var expanded by remember{mutableStateOf(false)};Column(Modifier.fillMaxSize().padding(20.dp)){Header("الإعدادات",onBack);Spacer(Modifier.height(24.dp));Text("القارئ");Box{OutlinedButton({expanded=true}){Text(reciter.label)};DropdownMenu(expanded,{expanded=false}){reciters.forEach{r->DropdownMenuItem({Text(r.label)},{onReciter(r);expanded=false})}}};Spacer(Modifier.height(24.dp));Row(verticalAlignment=Alignment.CenterVertically){Switch(showText,onShow);Spacer(Modifier.width(12.dp));Text("إظهار نص الآية أثناء التسميع")}}}

@Composable fun RecitationSession(surah:SurahInfo,start:Int,end:Int,showText:Boolean,reciter:Reciter,onBack:()->Unit){val context=LocalContext.current;var ayahs by remember{mutableStateOf(emptyList<QuranAyah>())};var idx by remember{mutableStateOf(0)};var heard by remember{mutableStateOf("")};var result by remember{mutableStateOf<DiffResult?>(null)};var total by remember{mutableStateOf(0)};var done by remember{mutableStateOf(false)};var listening by remember{mutableStateOf(false)};LaunchedEffect(surah.number){ayahs=loadSurah(surah.number,reciter.edition).filter{it.numberInSurah in start..end}}
 val recognizer=remember{SpeechRecognizer.createSpeechRecognizer(context)};val intent=remember{Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar");putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS,true)}}
 DisposableEffect(recognizer,ayahs,idx){val l=object:RecognitionListener{override fun onReadyForSpeech(p:Bundle?){listening=true;heard="أنا أستمع..."};override fun onBeginningOfSpeech(){listening=true};override fun onRmsChanged(v:Float){};override fun onBufferReceived(b:ByteArray?){};override fun onEndOfSpeech(){listening=false};override fun onError(e:Int){listening=false;heard="تعذر التعرف، حاول مرة أخرى"};override fun onResults(b:Bundle?){listening=false;val t=b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?:return;heard=t;if(idx<ayahs.size){val d=diff(ayahs[idx].text,t);result=d;total+=d.score}};override fun onPartialResults(b:Bundle?){b?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let{heard=it}};override fun onEvent(t:Int,p:Bundle?){}};recognizer.setRecognitionListener(l);onDispose{recognizer.destroy()}}
 val launcher=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){if(it)recognizer.startListening(intent)};fun startListen(){result=null;heard="";if(ContextCompat.checkSelfPermission(context,Manifest.permission.RECORD_AUDIO)==PackageManager.PERMISSION_GRANTED)recognizer.startListening(intent)else launcher.launch(Manifest.permission.RECORD_AUDIO)}
 Column(Modifier.fillMaxSize().padding(20.dp),horizontalAlignment=Alignment.CenterHorizontally){Header("اختبار الحفظ",onBack);if(ayahs.isEmpty()){CircularProgressIndicator();return@Column};if(done){val final=(total/ayahs.size).coerceIn(0,100);Spacer(Modifier.height(40.dp));Text("النتيجة النهائية",style=MaterialTheme.typography.headlineMedium);Text("$final%",style=MaterialTheme.typography.headlineLarge);Text("تم تسميع ${ayahs.size} آية");return@Column};val a=ayahs[idx];Text("سورة ${surah.name} • الآية ${a.numberInSurah}");if(showText){Spacer(Modifier.height(16.dp));Text(a.text,textAlign=TextAlign.Center)};Spacer(Modifier.height(22.dp));Text(if(heard.isBlank())"اضغط على الميكروفون وابدأ القراءة" else heard,textAlign=TextAlign.Center);result?.let{d->Spacer(Modifier.height(16.dp));Text("النتيجة: ${d.score}%");if(d.missing.isNotEmpty())Text("⚠️ ناقص: ${d.missing.joinToString("، ")}");if(d.extra.isNotEmpty())Text("➕ زائد: ${d.extra.joinToString("، ")}");if(d.different.isNotEmpty())Text("❌ مختلف: ${d.different.joinToString("، ")}")};Spacer(Modifier.height(24.dp));Button({if(listening)recognizer.stopListening()else startListen()}){Text(if(listening)"إيقاف" else "🎤 ابدأ القراءة")};if(result!=null){Spacer(Modifier.height(12.dp));Button({if(idx+1<ayahs.size){idx++;heard="";result=null}else done=true}){Text(if(idx+1<ayahs.size)"الآية التالية ←" else "إنهاء وإظهار النتيجة")}}}}

fun normalizeArabic(t:String)=t.replace(Regex("[\\u0610-\\u061A\\u064B-\\u065F\\u0670\\u06D6-\\u06EDـ]"),"").replace("أ","ا").replace("إ","ا").replace("آ","ا").replace("ٱ","ا").replace("ى","ي").replace("ة","ه").replace(Regex("[^\\p{L}\\p{N}\\s]"),"").replace(Regex("\\s+")," ").trim()
fun diff(expected:String,spoken:String):DiffResult{val a=normalizeArabic(expected).split(" ").filter{it.isNotBlank()};val b=normalizeArabic(spoken).split(" ").filter{it.isNotBlank()};val n=a.size;val m=b.size;val dp=Array(n+1){IntArray(m+1)};for(i in 1..n)for(j in 1..m)dp[i][j]=if(a[i-1]==b[j-1])dp[i-1][j-1]+1 else maxOf(dp[i-1][j],dp[i][j-1]);var i=n;var j=m;val matchedA=mutableSetOf<Int>();val matchedB=mutableSetOf<Int>();while(i>0&&j>0){if(a[i-1]==b[j-1]){matchedA+=i-1;matchedB+=j-1;i--;j--}else if(dp[i-1][j]>=dp[i][j-1])i-- else j--};val missing=a.indices.filter{it !in matchedA}.map{a[it]};val extra=b.indices.filter{it !in matchedB}.map{b[it]};val different=missing.zip(extra).map{"${it.first} ← ${it.second}"};val score=if(n==0)0 else ((matchedA.size.toDouble()/n)*100).toInt();return DiffResult(score,matchedA.size,missing.drop(different.size),extra.drop(different.size),different)}
