package com.example.taskly

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipalActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var gestureDetector: GestureDetector
    private var idUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val DB = SQLiteHelper(this, "usuarios", null, 1)

        val extras = intent.extras
        idUsuario = extras?.getInt("ID_USUARIO").toString()

        val tvBienvenida = findViewById<TextView>(R.id.tvBienvenida)
        val vCrearTarea = findViewById<View>(R.id.vTarea)
        val vCrearProyecto = findViewById<View>(R.id.vProyecto)
        val vGrabar = findViewById<View>(R.id.vVoz)

        // Initialize GestureDetector
        gestureDetector = GestureDetector(this, GestureListener())

        // Initialize SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(SpeechRecognitionListener())

        // Request necessary permissions
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)

        // Create a local copy of idUsuario and use it to set the text
        val userId = idUsuario
        if (!userId.isNullOrEmpty()) {
            tvBienvenida.text = DB.recogerUsuarioNombre(userId.toInt())?.uppercase()
        }

        vCrearTarea.setOnClickListener {
            val intent = Intent(this, TareaActivity::class.java)
            intent.putExtra("ID_USUARIO", userId?.toInt())
            startActivity(intent)
        }

        vCrearProyecto.setOnClickListener {
            val intent = Intent(this, ProyetoActivity::class.java)
            intent.putExtra("ID_USUARIO", userId?.toInt())
            startActivity(intent)
        }

        vGrabar.setOnClickListener {
            startVoiceRecognition()
        }
    }

    private fun startVoiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES") // Set the language to Spanish
        }
        speechRecognizer.startListening(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onLongPress(e: MotionEvent) {
            startVoiceRecognition()
        }

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (velocityX > 1000) {
                logout()
                return true
            }
            return false
        }
    }

    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private inner class SpeechRecognitionListener : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle?) {}
        override fun onBeginningOfSpeech() {}
        override fun onRmsChanged(rmsdB: Float) {}
        override fun onBufferReceived(buffer: ByteArray?) {}
        override fun onEndOfSpeech() {}
        override fun onError(error: Int) {
            // Handle error
            Toast.makeText(applicationContext, "Speech recognition error: $error", Toast.LENGTH_SHORT).show()
        }

        override fun onResults(results: Bundle?) {
            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            matches?.let {
                val spokenText = it[0].lowercase()
                when (spokenText) {
                    "crear proyecto" -> {
                        val intent = Intent(applicationContext, ProyetoActivity::class.java)
                        intent.putExtra("ID_USUARIO", idUsuario?.toInt())
                        startActivity(intent)
                    }
                    "crear tarea" -> {
                        val intent = Intent(applicationContext, TareaActivity::class.java)
                        intent.putExtra("ID_USUARIO", idUsuario?.toInt())
                        startActivity(intent)
                    }
                    else -> {
                        Toast.makeText(applicationContext, "Comando no reconocido: $spokenText", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        override fun onPartialResults(partialResults: Bundle?) {}
        override fun onEvent(eventType: Int, params: Bundle?) {}
    }
}
