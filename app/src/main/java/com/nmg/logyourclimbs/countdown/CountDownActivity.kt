package com.nmg.logyourclimbs.countdown

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.nmg.logyourclimbs.R
import com.nmg.logyourclimbs.aidfuns.AidFun
import com.nmg.logyourclimbs.database.SqliteDatabase
import com.nmg.logyourclimbs.databinding.ActivityCountDownBinding
import com.nmg.logyourclimbs.databinding.DialogAddWorkBinding

class CountDownActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCountDownBinding
    private lateinit var dataBase: SqliteDatabase
    private var isPaused: Boolean = false
    private var workTime: Long = 0
    private var restTime: Long = 0
    private var numRounds: Int = 0
    private var currentRound: Int = 0
    private var isWorking: Boolean = true
    private var millisLeft: Long = 0
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var prepCountDownTimer: CountDownTimer
    private var userID = 0
    private var selectedTitle: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountDownBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obteniendo el userID del intent
        userID = intent.getIntExtra("userID", 0)
        // Inicializando los componentes
        initComponents()
    }

    override fun onResume() {
        super.onResume()
        if (!::dataBase.isInitialized) {
            dataBase = SqliteDatabase(this)
        }
        // Actualizando los títulos de trabajo
        sWorksTitle()
    }

    private fun initComponents() {
        // Inicializando la base de datos
        dataBase = SqliteDatabase(this)
        // Configurando los listeners de los botones y switches
        initListener()
    }

    private fun initListener() {
        // Mantener la pantalla encendida si se activa el switch
        binding.swScreenOn.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
        }

        // Reiniciar el temporizador cuando se presiona el botón reset
        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        // Iniciar el temporizador cuando se presiona el botón start
        binding.btnStart.setOnClickListener {
            if (this::countDownTimer.isInitialized) {
                countDownTimer.cancel()
            }
            val workData = dataBase.getWorkData(selectedTitle)
            if (workData != null) {
                workTime = (workData[dataBase.COLUMN_WORK_TIME]?.toLong() ?: 0) * 1000
                restTime = (workData[dataBase.COLUMN_REST_TIME]?.toLong() ?: 0) * 1000
                numRounds = workData[dataBase.COLUMN_ROUNDS] ?: 0
            }
            currentRound = 0
            isWorking = true
            // Iniciar el temporizador de preparación
            startPrepTimer()
        }

        // Pausar o reanudar el temporizador cuando se presiona el botón pause
        binding.btnPause.setOnClickListener {
            if (isPaused) {
                isPaused = false
                startTimer(millisLeft)
            } else {
                isPaused = true
                countDownTimer.cancel()
            }
        }

        // Abrir el diálogo para eliminar trabajos
        binding.btnRemoveWork.setOnClickListener { removeWorksDialog() }
        // Abrir el diálogo para agregar trabajos
        binding.btnAddWork.setOnClickListener { addWorkDialog() }
    }

    private fun startPrepTimer() {
        // Configurando el temporizador de preparación a 5 segundos
        binding.tvTimer.text = AidFun(this).formatTime(5000)
        binding.tvPrep.text = getString(R.string.ready)
        window.decorView.setBackgroundColor(Color.BLUE)
        prepCountDownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.tvTimer.text = AidFun(this@CountDownActivity).formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                binding.tvPrep.text = ""
                window.decorView.setBackgroundColor(Color.GREEN)  // Cambiar a verde al iniciar la primera ronda de trabajo
                startTimer(workTime)
            }
        }
        prepCountDownTimer.start()
    }

    private fun startTimer(time: Long) {
        if (isPaused) return
        millisLeft = time
        countDownTimer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                millisLeft = millisUntilFinished
                binding.tvTimer.text = AidFun(this@CountDownActivity).formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                if (isWorking) {
                    currentRound++
                    if (currentRound < numRounds) {
                        isWorking = false
                        window.decorView.setBackgroundColor(Color.RED)
                        startTimer(restTime)
                    } else {
                        binding.tvTimer.text = getString(R.string.finished)
                        window.decorView.setBackgroundColor(Color.WHITE)
                    }
                } else {
                    isWorking = true
                    window.decorView.setBackgroundColor(Color.GREEN)
                    startTimer(workTime)
                }
            }
        }
        countDownTimer.start()
    }

    private fun resetTimer() {
        // Cancelando los temporizadores si están inicializados
        if (this::countDownTimer.isInitialized) {
            countDownTimer.cancel()
        }
        if (this::prepCountDownTimer.isInitialized) {
            prepCountDownTimer.cancel()
        }
        // Reiniciando los valores de las variables y actualizando la interfaz
        currentRound = 0
        isWorking = true
        isPaused = false
        millisLeft = 0
        binding.tvTimer.text = "00:00"
        binding.tvShowWork.text = "0"
        binding.tvShowRest.text = "0"
        binding.tvShowRound.text = "0"
        binding.tvWorkName.text = selectedTitle
        window.decorView.setBackgroundColor(Color.WHITE)
    }

    private fun sWorksTitle() {
        // Obteniendo los títulos de los trabajos del usuario
        val titles = dataBase.getTitles(userID)
        val sWorksTitles: Spinner = findViewById(R.id.sWorksTitles)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, titles)
        sWorksTitles.adapter = adapter
        sWorksTitles.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedTitle = parent.getItemAtPosition(position).toString()
                val workData = dataBase.getWorkData(selectedTitle)
                if (workData != null) {
                    binding.tvShowWork.text = workData[dataBase.COLUMN_WORK_TIME]?.toString()
                    binding.tvShowRest.text = workData[dataBase.COLUMN_REST_TIME]?.toString()
                    binding.tvShowRound.text = workData[dataBase.COLUMN_ROUNDS]?.toString()
                    binding.tvWorkName.text = selectedTitle
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No se hace nada en esta situación
            }
        }
    }

    private fun addWorkDialog() {
        val dialogAddWork = Dialog(this)
        val dialogBinding: DialogAddWorkBinding = DialogAddWorkBinding.inflate(layoutInflater)

        dialogAddWork.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddWork.setContentView(dialogBinding.root)
        dialogAddWork.show()

        dialogBinding.btnAddWork.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString()
            val workTime = dialogBinding.etWorkTime.text.toString()
            val restTime = dialogBinding.etRestTime.text.toString()
            val rounds = dialogBinding.etRounds.text.toString()

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(workTime) || TextUtils.isEmpty(restTime) || TextUtils.isEmpty(rounds)) {
                Toast.makeText(
                    this@CountDownActivity,
                    R.string.error_addRoute,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                val newWork = Works(userID, title, workTime.toInt(), restTime.toInt(), rounds.toInt())
                dataBase.addWork(newWork)
                dialogAddWork.hide()
                sWorksTitle()
            }
        }
    }

    private fun removeWorksDialog() {
        val deleteDialog = AlertDialog.Builder(this)
            .setTitle(this.getString(R.string.delete_confirm))
            .setMessage(this.getString(R.string.cant_rollback))
            .setIcon(R.drawable.delete)
            .setPositiveButton(this.getString(R.string.accept)) { _, _ ->
                dataBase.deleteWorks(selectedTitle)
                (this as Activity).finish()
                this.startActivity(this.intent)
            }
        deleteDialog.setNegativeButton(this.getString(R.string.cancel)) { _, _ -> }
        deleteDialog.show()
    }
}
