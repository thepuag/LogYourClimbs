package com.nmg.logyourclimbs.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nmg.logyourclimbs.R
import com.nmg.logyourclimbs.aidfuns.AidFun
import com.nmg.logyourclimbs.database.SqliteDatabase
import com.nmg.logyourclimbs.databinding.ActivityLogbookBinding
import com.nmg.logyourclimbs.databinding.ActivitySignupBinding
import com.nmg.logyourclimbs.routes.LogbookActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private lateinit var sqliteDB: SqliteDatabase
    private lateinit var aidFun: AidFun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializo la base de datos SQLite
        sqliteDB = SqliteDatabase(this)
        // Inicializo la función de ayuda
        aidFun = AidFun(this)

        // Manejo el evento de clic del botón de registro
        binding.btnSignup.setOnClickListener {
            val signupUserName = binding.etSignupName.text.toString()
            val signupPassword = binding.etSignupPassword.text.toString()
            val signupCPassword = binding.etConfirmPassword.text.toString()
            // Verifico si el usuario ya existe
            val checkUser = sqliteDB.checkUser(signupUserName)

            // Verifico si los campos están vacíos
            if (TextUtils.isEmpty(signupUserName) || TextUtils.isEmpty(signupPassword) || TextUtils.isEmpty(signupCPassword)) {
                aidFun.showToast("Rellene los campos correctamente.")
            } else {
                // Verifico si la contraseña cumple con los requisitos
                if (!isValidPassword(signupPassword)) {
                    aidFun.showToast("La contraseña debe tener al menos 6 caracteres y contener letras y números.")
                } else {
                    // Si el usuario no existe
                    if (!checkUser) {
                        // Verifico si las contraseñas coinciden
                        if (signupPassword == signupCPassword) {
                            // Registro el nuevo usuario
                            sqliteDB.addUser(signupUserName, signupPassword)
                            aidFun.showToast("Registrado correctamente")
                            // Navego a la actividad de inicio de sesión
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            aidFun.showToast("Las contraseñas no coinciden")
                        }
                    } else {
                        aidFun.showToast("El usuario ya existe.")
                    }
                }
            }
        }

        // Manejo el evento de clic del texto de ya registrado
        binding.tvAlreadyRegistered.setOnClickListener {
            // Navego a la actividad de inicio de sesión
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    /**
     * Valida que la contraseña cumpla con los requisitos de seguridad:
     * - Mínimo 6 caracteres
     * - Contiene al menos una letra
     * - Contiene al menos un número
     */
    private fun isValidPassword(password: String): Boolean {
        if (password.length < 6) {
            return false
        }
        
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        
        return hasLetter && hasDigit
    }
}
