package com.example.persianvoiceassistant

import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.speech.*
import android.speech.tts.TextToSpeech
import androidx.core.app.NotificationCompat
import java.net.URLEncoder
import java.util.Locale

class AssistantService : Service(), TextToSpeech.OnInitListener {
    private var recognizer: SpeechRecognizer?=null
    private lateinit var tts: TextToSpeech
    private var running=false
    private var waiting=false
    private val channel="assistant_channel"

    override fun onCreate() {
        super.onCreate()
        createChannel()
        tts=TextToSpeech(this,this)
        startForeground(20, notification("دستیار فعال است"))
    }

    override fun onStartCommand(i:Intent?, flags:Int, id:Int):Int {
        if(!running){running=true; speak("دستیار آماده است"); startRecognition()}
        return START_STICKY
    }

    override fun onInit(r:Int){ if(r==TextToSpeech.SUCCESS){tts.language=Locale("fa","IR");tts.setSpeechRate(.9f)} }

    private fun createChannel(){
        if(Build.VERSION.SDK_INT>=26){
            val c=NotificationChannel(channel,"دستیار صوتی",NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java).createNotificationChannel(c)
        }
    }

    private fun notification(text:String):Notification{
        return NotificationCompat.Builder(this,channel)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("دستیار صوتی فارسی")
            .setContentText(text)
            .setOngoing(true).build()
    }

    private fun startRecognition(){
        if(!running)return
        if(checkSelfPermission(android.Manifest.permission.RECORD_AUDIO)!=PackageManager.PERMISSION_GRANTED){stopSelf();return}
        if(!SpeechRecognizer.isRecognitionAvailable(this)){speak("تشخیص گفتار در این گوشی در دسترس نیست");return}
        recognizer?.destroy()
        recognizer=SpeechRecognizer.createSpeechRecognizer(this)
        recognizer!!.setRecognitionListener(object:RecognitionListener{
            override fun onReadyForSpeech(p:Bundle?){update("منتظر «دستیار»...")}
            override fun onBeginningOfSpeech(){update("در حال شنیدن...")}
            override fun onEndOfSpeech(){}
            override fun onError(e:Int){restart(500)}
            override fun onResults(r:Bundle?){
                val s=r?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull().orEmpty()
                if(s.isNotBlank()) handle(s)
                restart(250)
            }
            override fun onPartialResults(p:Bundle?){}
            override fun onEvent(t:Int,p:Bundle?){}
            override fun onRmsChanged(v:Float){}
            override fun onBufferReceived(b:ByteArray?){}
        })
        val i=Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply{
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE,"fa-IR")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3)
        }
        try{recognizer!!.startListening(i)}catch(_:Exception){restart(1000)}
    }

    private fun restart(ms:Long){
        if(running) Handler(mainLooper).postDelayed({if(running)startRecognition()},ms)
    }

    private fun handle(raw:String){
        var t=raw.lowercase(Locale("fa","IR")).replace("ي","ی").replace("ك","ک").trim()
        if(t.contains("دستیار")){
            waiting=true
            t=t.replaceFirst("دستیار","").trim()
            if(t.isNotEmpty()){command(t);waiting=false}else speak("بله؟")
        }else if(waiting){command(t);waiting=false}
    }

    private fun command(t:String){
        when{
            t.contains("گوگل")&&(t.contains("سرچ")||t.contains("جستجو"))->{
                val q=clean(t,"گوگل"); if(q.isBlank())speak("چی رو سرچ کنم؟") else {open("https://www.google.com/search?q="+URLEncoder.encode(q,"UTF-8"));speak("جستجو را باز کردم")}
            }
            t.contains("یوتیوب")&&(t.contains("سرچ")||t.contains("جستجو"))->{
                val q=clean(t,"یوتیوب"); if(q.isBlank())speak("چی رو سرچ کنم؟") else open("https://www.youtube.com/results?search_query="+URLEncoder.encode(q,"UTF-8"))
            }
            t.contains("گوگل")&&openWord(t)->open("https://www.google.com")
            t.contains("یوتیوب")&&openWord(t)->open("https://www.youtube.com")
            t.contains("کروم")&&openWord(t)->launch("com.android.chrome","کروم")
            t.contains("تلگرام")&&openWord(t)->launch("org.telegram.messenger","تلگرام")
            t.contains("دیسکورد")&&openWord(t)->launch("com.discord","دیسکورد")
            t.contains("دانلود")&&openWord(t)->open("content://com.android.externalstorage.documents/root/primary")
            t.contains("ماشین حساب")&&openWord(t)->calc()
            t.contains("ببند")||t.contains("بسته کن")->speak("اندروید به برنامه معمولی اجازه بستن اجباری برنامه‌های دیگر را نمی‌دهد.")
            t.contains("خاموش")||t.contains("خروج")->stopSelf()
            else->speak("این دستور را هنوز یاد نگرفته‌ام")
        }
    }

    private fun openWord(t:String)=t.contains("باز")||t.contains("اجرا")||t.contains("برو")
    private fun clean(t:String,s:String)=t.replace("برو","").replace("توی","").replace("در","").replace("به","").replace(s,"").replace("و","").replace("سرچ","").replace("جستجو","").replace("کن","").trim()
    private fun open(url:String){try{startActivity(Intent(Intent.ACTION_VIEW,android.net.Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))}catch(_:Exception){speak("برنامه لازم پیدا نشد")}}
    private fun launch(pkg:String,name:String){val i=packageManager.getLaunchIntentForPackage(pkg);if(i!=null){i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);speak("$name باز شد")}else speak("$name نصب نیست")}
    private fun calc(){for(p in listOf("com.google.android.calculator","com.android.calculator2","com.sec.android.app.popupcalculator")){val i=packageManager.getLaunchIntentForPackage(p);if(i!=null){i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);startActivity(i);speak("ماشین حساب باز شد");return}};speak("ماشین حساب پیدا نشد")}
    private fun speak(s:String){update(s);tts.speak(s,TextToSpeech.QUEUE_FLUSH,null,"assistant")}
    private fun update(s:String){val n=notification(s);getSystemService(NotificationManager::class.java).notify(20,n)}
    override fun onDestroy(){running=false;recognizer?.destroy();tts.stop();tts.shutdown();super.onDestroy()}
    override fun onBind(i:Intent?)=null
}
