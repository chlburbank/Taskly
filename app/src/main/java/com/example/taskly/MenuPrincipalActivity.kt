package com.example.taskly

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_principal)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var DB = SQLiteHelper(this, "usuarios", null, 1);

        val extras = intent.extras

        val idUsuario = extras?.getInt("ID_USUARIO").toString()

        var tvBienvenida = findViewById<TextView>(R.id.tvBienvenida);

        tvBienvenida.setText(
            DB.recogerUsuarioNombre(idUsuario.toInt())?.uppercase()
        )

    }
}