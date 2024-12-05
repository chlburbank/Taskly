package com.example.taskly

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var DB = SQLiteHelper(this, "usuarios", null, 1);

        var etUsuario = findViewById<EditText>(R.id.etUsuario);
        var etContrasenya = findViewById<EditText>(R.id.etContrasenya);
        var btnRegistro = findViewById<Button>(R.id.btnRegistro);
        var btnLogin = findViewById<Button>(R.id.btnLogin);

        btnRegistro.setOnClickListener {
            var usuario = etUsuario.text.toString();
            var contrasenya = etContrasenya.text.toString();

            if (usuario.isNotEmpty() && contrasenya.isNotEmpty()) {
                val resultado = DB.insertarUsuario(usuario, contrasenya)
                if (resultado != -1L) {
                    Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        btnLogin.setOnClickListener {
            val usuario = etUsuario.text.toString()
            val contrasenya = etContrasenya.text.toString()
            if (usuario.isNotEmpty() && contrasenya.isNotEmpty()) {
                val cursor = DB.recogerUsuarioLogin(usuario, contrasenya)
                if (cursor != null && cursor.moveToFirst()) {
                    Toast.makeText(this, "Login exitoso", Toast.LENGTH_SHORT).show()
                    var intent = Intent(this, MenuPrincipalActivity::class.java);
                    intent.putExtra("ID_USUARIO", cursor.getInt(0))
                    startActivity(intent)
                    cursor.close();
                } else {
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

    }
}