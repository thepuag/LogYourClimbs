package com.nmg.logyourclimbs.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.nmg.logyourclimbs.aidfuns.AidFun
import com.nmg.logyourclimbs.database.SqliteDatabase
import com.nmg.logyourclimbs.databinding.ActivityLoginBinding
import com.nmg.logyourclimbs.aidfuns.FileDB
import com.nmg.logyourclimbs.routes.LogbookActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sqliteDB: SqliteDatabase
    private val fileDB = FileDB()
    private val aidFun = AidFun(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registro una actividad para exportar la base de datos a un archivo
        val exportRequest = registerForActivityResult(ActivityResultContracts.CreateDocument()) { uri: Uri? ->
            if (uri != null) {
                fileDB.exportDB(uri, this, "LogYourClimb")
            }
        }
        binding.btnExport.setOnClickListener {
            exportRequest.launch("LogYourClimb.db")
        }

        // Registro una actividad para importar la base de datos desde un archivo
        val importRequest = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val uri = result.data?.data
                if (uri != null) {
                    fileDB.importDB(uri, this, "LogYourClimb")
                }
            }
        }
        binding.btnImport.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            importRequest.launch(intent)
        }

        // Inicializo la base de datos SQLite
        sqliteDB = SqliteDatabase(this)

        // Manejo el evento de clic del botón de inicio de sesión
        binding.btnLogin.setOnClickListener {
            val name = binding.etSignupName.text.toString()
            val password = binding.etSignupPassword.text.toString()

            // Verifico si los campos de nombre y contraseña están vacíos
            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(password)) {
                aidFun.showToast("Introduce un usuario y contraseña correctos.")
            } else {
                // Verifico las credenciales del usuario en la base de datos
                val checkName = sqliteDB.readUser(name, password)
                if (checkName) {
                    aidFun.showToast("Acceso correcto.")
                    // Obtengo el ID del usuario y abro la actividad del libro de registro
                    val userId = sqliteDB.getUserId(name)
                    val intent = Intent(this, LogbookActivity::class.java)
                    Log.d("noe", "id vale $userId")
                    intent.putExtra("userID", userId)
                    startActivity(intent)
                } else {
                    aidFun.showToast("Usuario y constraseña incorrecto.")
                }
            }
        }

        // Manejo el evento de clic del texto de registro
        binding.tvSignupHere.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }
}

