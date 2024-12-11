package com.example.taskly

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(
    context: Context?,
    name: String?,
    factory: SQLiteDatabase.CursorFactory?,
    version: Int
) : SQLiteOpenHelper(context, name, factory, version) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE usuarios (" +
                    "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre_usuario TEXT NOT NULL," +
                    "contrasenya TEXT NOT NULL)"
        );

        db.execSQL(
            "CREATE TABLE proyectos (" +
                    "id_proyecto INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre_proyecto TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "id_usuario INTEGER, " +
                    "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)) "
        )
        db.execSQL(
            "CREATE TABLE tareas (" +
                    "id_tarea INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre_tarea TEXT NOT NULL," +
                    "descripcion TEXT," +
                    "prioridad TEXT," +
                    "id_proyecto INTEGER," +
                    "id_usuario INTEGER," +
                    "FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)," +
                    "FOREIGN KEY (id_proyecto) REFERENCES proyectos(id_proyecto))"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tareas")
        db.execSQL("DROP TABLE IF EXISTS proyectos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    fun insertarUsuario(nombre: String, contrasenya: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("nombre_usuario", nombre)
        contentValues.put("contrasenya", contrasenya)
        return db.insert("usuarios", null, contentValues)
    }

    fun recogerUsuarioNombre(id: Int): String? {
        val db = this.readableDatabase;
        var query = db.rawQuery("SELECT nombre_usuario FROM usuarios WHERE id_usuario = ?", arrayOf(id.toString()));
        if (query.moveToFirst()) {
             return query.getString(0);
        } else {
            query.close();
            return null;
        }
    }

    fun recogerUsuarioLogin(nombre: String, contrasenya: String): Cursor? {
        val db = this.readableDatabase;
        var query = db.rawQuery("SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasenya = ?", arrayOf(nombre, contrasenya));
        if (query.moveToFirst()) {
            return query;
        } else {
            query.close();
            return null;
        }
    }

    fun guardarTarea(titulo: String, desc: String, prioridad: String, id_usuario: Int) {
        var db = writableDatabase;
        var content = ContentValues();
        content.put("nombre_tarea", titulo);
        content.put("descripcion", desc);
        content.put("prioridad", prioridad);
    }
}