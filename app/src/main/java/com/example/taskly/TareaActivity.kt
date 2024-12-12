package com.example.taskly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TareaActivity : AppCompatActivity() {

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var currentEditText: EditText
    private lateinit var gestureDetector: GestureDetector
    private var idUsuario: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarea)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var DB = SQLiteHelper(this, "usuarios", null, 1)

        val extras = intent.extras
        idUsuario = extras?.getInt("ID_USUARIO").toString()

        var etTitulo = findViewById<EditText>(R.id.etTitulo)
        var etDesc = findViewById<EditText>(R.id.etDescripcion)
        var sPrioridad = findViewById<Spinner>(R.id.sPrioridad)
        var btnGuardar = findViewById<Button>(R.id.btnGuardar)
        var btnAtras = findViewById<ImageView>(R.id.vBack)
        val btnGrabar = findViewById<ImageView>(R.id.btnGrabarTarea)

        var prioridades = arrayOf("baja", "media", "alta")
        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, prioridades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sPrioridad.adapter = adapter

        btnAtras.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.putExtra("ID_USUARIO", idUsuario?.toInt())
            startActivity(intent)
        }

        btnGuardar.setOnClickListener {
            val titulo = etTitulo.text.toString()
            val descripcion = etDesc.text.toString()
            val prioridad = sPrioridad.selectedItem.toString()
            // Add your save logic here
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}
            override fun onError(error: Int) {
                Toast.makeText(this@TareaActivity, "Error recognizing speech", Toast.LENGTH_SHORT).show()
            }
            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null && matches.isNotEmpty()) {
                    currentEditText.setText(matches[0])
                }
            }
            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        btnGrabar.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-ES")
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Habla ahora")
            startActivityForResult(intent, ProyetoActivity.REQUEST_CODE_SPEECH_INPUT)
        }

        etTitulo.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                currentEditText = etTitulo
            }
        }

        etDesc.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                currentEditText = etDesc
            }
        }

        // Initialize GestureDetector
        gestureDetector = GestureDetector(this, GestureListener())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ProyetoActivity.REQUEST_CODE_SPEECH_INPUT && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && result.isNotEmpty()) {
                currentEditText.setText(result[0])
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        return super.onTouchEvent(event)
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            val intent = Intent(this@TareaActivity, MenuPrincipalActivity::class.java)
            intent.putExtra("ID_USUARIO", idUsuario?.toInt())
            startActivity(intent)
            return true
        }
    }

    companion object {
        const val REQUEST_CODE_SPEECH_INPUT = 100
    }
}
