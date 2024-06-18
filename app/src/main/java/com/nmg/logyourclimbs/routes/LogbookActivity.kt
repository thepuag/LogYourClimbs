package com.nmg.logyourclimbs.routes

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nmg.logyourclimbs.R
import com.nmg.logyourclimbs.aidfuns.AidFun
import com.nmg.logyourclimbs.countdown.CountDownActivity
import com.nmg.logyourclimbs.database.SqliteDatabase
import com.nmg.logyourclimbs.databinding.ActivityLogbookBinding
import java.util.Calendar

class LogbookActivity : AppCompatActivity() {
    private lateinit var rvRoutes: RecyclerView
    private lateinit var dataBase: SqliteDatabase
    private lateinit var binding: ActivityLogbookBinding
    private var rgSelect: Int = 0
    private lateinit var etFilter: TextInputEditText
    private lateinit var selectedOption: String
    private var userID = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogbookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        userID = intent.getIntExtra("userID", 0)
        // Inicializo los componentes principales de la actividad
        initComponent()
        // Lleno la lista de rutas
        fillList()
        // Configuro los listeners para los botones y otros elementos interactivos
        initListeners()
    }

    private fun initComponent() {
        // Asigno el RecyclerView y el campo de texto del filtro
        rvRoutes = findViewById(R.id.rvRoutes)
        etFilter = findViewById(R.id.etFilter)
    }

    private fun initListeners() {
        // Configuro los eventos para los botones flotantes y otros elementos
        binding.fabAddDialog.setOnClickListener { addRouteDialog() }
        binding.ivFilterRoute.setOnClickListener { buttonFilter() }
        binding.ivSortBy.setOnClickListener { buttonSotBy() }
        binding.btnStatistics.setOnClickListener { graphDialog() }
        binding.btnWorks.setOnClickListener {
            val intent = Intent(this, CountDownActivity::class.java)
            intent.putExtra("userID", userID)
            startActivity(intent)
        }
    }

    private fun buttonSotBy() {
        // Inflo el layout personalizado
        val filterView = layoutInflater.inflate(R.layout.filter_layout, null)
        // Creo el dialogo y lo personalizo
        val filterDialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.ic_sort)
            .setView(filterView)
            .create()

        // Cambio el texto del imageview
        val tvFilterSort = filterView.findViewById<TextView>(R.id.tvFilterSort)
        tvFilterSort.text = getString(R.string.sort_by)

        // Busco el radiogroup dentro de la vista inflada
        val radioGroup = filterView.findViewById<RadioGroup>(R.id.rgFilter)

        // Defino las opciones del RadioButton
        val options = arrayOf("Nombre", "Grado", "Fecha", "Descripción")

        for (option in options) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            radioGroup.addView(radioButton)
        }

        // Muestro el dialogo
        filterDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterDialog.window!!.setGravity(Gravity.TOP)
        filterDialog.show()

        // Configuro el evento de selección del RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton? = filterView.findViewById(checkedId)
            if (radio != null) {
                selectedOption = radio.text.toString()
            }
            // Ordeno las rutas basándome en la opción seleccionada
            sortRoutes(selectedOption)
            filterDialog.dismiss()
        }
    }

    private fun sortRoutes(sortBy: String) {
        // Obtengo todas las rutas y las ordeno según la opción seleccionada
        val allRoutes = dataBase.listRoutes(userID)
        allRoutes.sortWith(compareBy {
            when (AidFun(this).removeSigns(sortBy.lowercase())) {
                "nombre" -> it.routeName
                "grado" -> it.grade
                "fecha" -> it.date
                "descripcion" -> it.description
                else -> it.date // Ordeno por fecha por defecto
            }
        })
        val mAdapter = RoutesAdapter(this, allRoutes)
        rvRoutes.adapter = mAdapter
    }

    private fun fillList() {
        // Configuro el título de la actividad
        title = "LogYourClimbs"
        // Configuro el RecyclerView
        rvRoutes.layoutManager = LinearLayoutManager(this)
        rvRoutes.setHasFixedSize(true)
        // Inicializo la base de datos
        dataBase = SqliteDatabase(this@LogbookActivity)

        // Obtengo todas las rutas de la base de datos
        val allRoutes = dataBase.listRoutes(userID)
        if (allRoutes.isNotEmpty()) {
            rvRoutes.visibility = View.VISIBLE
            val mAdapter = RoutesAdapter(this, allRoutes)
            rvRoutes.adapter = mAdapter
            // Actualizo el total de rutas
            refreshTotalRoutes()
        } else {
            rvRoutes.visibility = View.GONE
            AidFun(this).showToast(getString(R.string.listEmpty))
        }
    }

    private fun buttonFilter() {
        // Inflo el layout personalizado
        val filterView = layoutInflater.inflate(R.layout.filter_layout, null)
        // Creo el dialogo y lo personalizo
        val filterDialog = AlertDialog.Builder(this)
            .setIcon(R.drawable.filter)
            .setView(filterView)
            .create()
        // Cambio el texto del imageview
        val tvFilterSort = filterView.findViewById<TextView>(R.id.tvFilterSort)
        tvFilterSort.text = getString(R.string.filter_by)
        // Busco el radiogroup dentro de la vista inflada
        val radioGroup = filterView.findViewById<RadioGroup>(R.id.rgFilter)

        // Defino las opciones del RadioButton
        val options = arrayOf("Nombre", "Grado", "Fecha", "Descripción")

        for (option in options) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            radioGroup.addView(radioButton)
        }

        // Muestro el dialogo
        filterDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        filterDialog.window!!.setGravity(Gravity.TOP)
        filterDialog.show()

        // Configuro el evento de selección del RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton? = filterView.findViewById(checkedId)
            if (radio != null) {
                selectedOption = radio.text.toString()
            }
            // Filtro las rutas basándome en la opción seleccionada
            filterRoute(selectedOption)
            filterDialog.dismiss()
        }
    }

    private fun graphDialog() {
        // Inflo el layout personalizado para el gráfico
        val filterView = layoutInflater.inflate(R.layout.dialog_graph, null)
        val graphDialog = AlertDialog.Builder(this)
            .setView(filterView)
            .setTitle(R.string.statistics)
            .create()
        val barChart = filterView.findViewById<HorizontalBarChart>(R.id.bcGrades)

        // Obtengo el número de veces que se repite cada elemento en la columna routeGrade
        val routeGradesCount = dataBase.countRouteGrades(userID)

        // Creo las entradas para el gráfico de barras
        val entries = ArrayList<BarEntry>()
        var index = 0f
        routeGradesCount.forEach { (_, count) ->
            entries.add(BarEntry(index, count.toFloat()))
            index++
        }

        // Configuro el eje X con las etiquetas de los grados de ruta
        val labels = routeGradesCount.keys.toTypedArray()
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)

        // Creo y configuro el conjunto de datos de barras
        val barDataSet = BarDataSet(entries, "Vías encadenadas")
        val data = BarData(barDataSet)

        // Configuro el gráfico con los datos y muestro el diálogo
        barChart.data = data
        barChart.invalidate()
        graphDialog.show()
    }

    private fun filterRoute(optionFilter: String) {
        // Configuro el filtro para buscar rutas
        etFilter.addTextChangedListener {
            val text = it.toString().lowercase()
            val allRoutes = dataBase.listRoutes(userID)
            val filteredList = arrayListOf<Routes>()
            when (AidFun(this).removeSigns(optionFilter.lowercase())) {
                "nombre" -> allRoutes.forEach {
                    if (it.routeName.lowercase().contains(text)) {
                        filteredList.add(it)
                    }
                }

                "grado" -> allRoutes.forEach {
                    if (it.grade.lowercase().contains(text)) {
                        filteredList.add(it)
                    }
                }

                "fecha" -> allRoutes.forEach {
                    if (it.date.lowercase().contains(text)) {
                        filteredList.add(it)
                    }
                }

                "descripcion" -> allRoutes.forEach {
                    if (it.description.lowercase().contains(text)) {
                        filteredList.add(it)
                    }
                }

                else -> allRoutes.forEach {
                    if (it.routeName.lowercase().contains(text)) {
                        filteredList.add(it)
                    }
                }
            }

            // Actualizo el adaptador del RecyclerView con la lista filtrada
            val mAdapter = RoutesAdapter(this, filteredList)
            rvRoutes.adapter = mAdapter
        }
        // Cambio la pista del campo de texto del filtro
        binding.tilFilter.hint = optionFilter
    }

    private fun addRouteDialog() {
        // Cargamos el layout del diálogo para añadir ruta
        val dialogAddRoute = Dialog(this)
        val addRouteView = layoutInflater.inflate(R.layout.dialog_add_route, null)

        // Configuro el Spinner con los grados disponibles
        val spinnerGrades = addRouteView.findViewById<Spinner>(R.id.sGrade)
        val adapter = ArrayAdapter(
            this@LogbookActivity,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.grades_array)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerGrades.setAdapter(adapter)
        var selectedGrade = ""
        spinnerGrades.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                selectedGrade = resources.getStringArray(R.array.grades_array)[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                Toast.makeText(
                    this@LogbookActivity,
                    "No funcioona el selector de grados",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Configuro el RadioGroup para seleccionar cómo fue la ruta
        val rgHowWas = addRouteView.findViewById<RadioGroup>(R.id.rgHowWas)
        rgHowWas.setOnCheckedChangeListener { _, checkedId ->
            rgSelect = when (checkedId) {
                R.id.rbOnsight -> 0
                R.id.rbFlash -> 1
                R.id.rbRedpoint -> 2
                else -> 0
            }
        }

        // Configuro el selector de fecha
        val tvDate = addRouteView.findViewById<EditText>(R.id.etDate)
        tvDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog = DatePickerDialog(
                this,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
                    tvDate.setText(selectedDate)
                }, year, month, day
            )

            // Establezco la fecha máxima a la fecha actual
            datePickerDialog.datePicker.maxDate = calendar.timeInMillis
            datePickerDialog.show()
        }

        // Configuro el diálogo y lo muestro
        dialogAddRoute.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogAddRoute.setContentView(addRouteView)
        dialogAddRoute.show()

        // Configuro el botón para añadir la ruta a la base de datos
        val nameField: TextInputEditText = addRouteView.findViewById(R.id.etName)
        val descriptionField: TextInputEditText = addRouteView.findViewById(R.id.etDescription)
        val btnAddRoute: MaterialButton = addRouteView.findViewById(R.id.btnAddRoute)
        btnAddRoute.setOnClickListener {
            val name = nameField.text.toString()
            val description = descriptionField.text.toString()
            val grade = selectedGrade
            val date = tvDate.text.toString()
            val howWas = rgSelect.toString()

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(description)) {
                Toast.makeText(
                    this@LogbookActivity,
                    R.string.error_addRoute,
                    Toast.LENGTH_LONG
                ).show()
            } else {
                // Creo una nueva ruta y la añado a la base de datos
                val newRoute = Routes(name, description, grade, howWas, date, userID)
                dataBase.addRoutes(newRoute)
                dialogAddRoute.hide()
                // Actualizo la lista de rutas
                fillList()
            }
        }
    }

    private fun refreshTotalRoutes() {
        // Actualizo el total de rutas completadas
        binding.tvNumberOfRoutes.text =
            getString(R.string.total_routes_done, dataBase.getNumberRoutes(userID).toString())
    }
}
