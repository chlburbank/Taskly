package com.example.taskly

import android.content.Intent
import android.os.Bundle
import android.widget.Adapter
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class TareaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tarea)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var DB = SQLiteHelper(this, "usuarios", null, 1);

        val extras = intent.extras

        val idUsuario = extras?.getInt("ID_USUARIO").toString()

        var etTitulo = findViewById<EditText>(R.id.etTitulo);
        var etDesc = findViewById<EditText>(R.id.etDescripcion);
        var sPrioridad = findViewById<Spinner>(R.id.sPrioridad);
        var btnGuardar = findViewById<Button>(R.id.btnGuardar);
        var btnAtras = findViewById<ImageView>(R.id.vBack);

        var prioridades = arrayOf("baja", "media", "alta");

        var adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, prioridades);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sPrioridad.adapter = adapter

        btnAtras.setOnClickListener{
            var intent = Intent(this, MenuPrincipalActivity::class.java);
            intent.putExtra("ID_USUARIO", idUsuario.toInt());
            startActivity(intent);
        }

        btnGuardar.setOnClickListener{
            var titulo = etTitulo.text.toString();
            var descripcion = etDesc.text.toString();
            var prioridad = sPrioridad.selectedItem.toString();


        }
    }
}