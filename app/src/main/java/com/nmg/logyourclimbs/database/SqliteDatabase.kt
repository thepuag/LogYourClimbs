package com.nmg.logyourclimbs.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import androidx.core.content.contentValuesOf
import com.nmg.logyourclimbs.countdown.Works
import com.nmg.logyourclimbs.routes.Routes

class SqliteDatabase internal constructor(context: Context?) : SQLiteOpenHelper(
    context, DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "LogYourClimb"
    }

    // Declaración de constantes para las tablas y columnas de la base de datos
    private val TABLE_ROUTES = "Routes"
    private val COLUMN_ROUTE_ID = "routeID"
    private val COLUMN_ROUTE_USER_ID = "routeUserID"
    private val COLUMN_ROUTE_NAME = "routeName"
    private val COLUMN_DESCRIPTION = "routeDescription"
    private val COLUMN_GRADE = "routeGrade"
    private val COLUMN_DATE = "routeDate"
    private val COLUMN_HOWWAS = "routeHowWas"
    private val TABLE_USERS = "Users"
    private val COLUMN_USER_NAME = "userName"
    private val COLUMN_USER_PASSWORD = "userPassword"
    private val COLUMN_USER_ID = "userID"
    private val TABLE_WORKS = "Works"
    private val COLUMN_WORK_ID= "workID"
    private val COLUMN_WORK_USER_ID= "workUserID"
    val COLUMN_TITLE = "title"
    val COLUMN_WORK_TIME = "workTime"
    val COLUMN_REST_TIME = "restTime"
    val COLUMN_ROUNDS = "rounds"

    // Método onCreate: creación de tablas
    override fun onCreate(db: SQLiteDatabase?) {
        val defineTableUsers =
            ("CREATE TABLE $TABLE_USERS (" +
                    "$COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    " $COLUMN_USER_NAME TEXT," +
                    " $COLUMN_USER_PASSWORD TEXT" +
                    ")")
        val defineRoutesTable =
            ("CREATE TABLE $TABLE_ROUTES (" +
                    "$COLUMN_ROUTE_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_ROUTE_NAME TEXT," +
                    "$COLUMN_DESCRIPTION TEXT," +
                    "$COLUMN_GRADE TEXT," +
                    "$COLUMN_DATE DATE," +
                    "$COLUMN_HOWWAS INTEGER," +
                    "$COLUMN_ROUTE_USER_ID INTEGER," +
                    "FOREIGN KEY($COLUMN_ROUTE_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID)" +
                    ")")
        val defineWorksTable =
            ("CREATE TABLE $TABLE_WORKS (" +
                    "$COLUMN_WORK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COLUMN_TITLE TEXT," +
                    "$COLUMN_WORK_TIME INTEGER," +
                    "$COLUMN_REST_TIME INTEGER," +
                    "$COLUMN_ROUNDS INTEGER," +
                    "$COLUMN_WORK_USER_ID INTEGER," +
                    "FOREIGN KEY($COLUMN_WORK_USER_ID) REFERENCES $TABLE_USERS($COLUMN_USER_ID))"
                    )
        db?.execSQL(defineRoutesTable)
        db?.execSQL(defineTableUsers)
        db?.execSQL(defineWorksTable)
    }

    // Método onUpgrade: manejo de actualizaciones de la base de datos
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ROUTES")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_WORKS")
        onCreate(db)
    }

    // Método para leer un usuario con nombre y contraseña
    fun readUser(userName: String, password: String): Boolean {
        val db = this.readableDatabase
        val sqlSelect =
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_NAME = ? AND $COLUMN_USER_PASSWORD = ?"
        val cursor = db.rawQuery(sqlSelect, arrayOf(userName.lowercase(), password))
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    // Método para verificar si un usuario existe
    fun checkUser(userName: String): Boolean {
        val db = this.readableDatabase
        val sqlSelect =
            "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_NAME = ?"
        val cursor = db.rawQuery(sqlSelect, arrayOf(userName.lowercase()))
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }
        cursor.close()
        return true
    }

    // Método para añadir un nuevo usuario
    fun addUser(userName: String, password: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(COLUMN_USER_NAME, userName.lowercase())
        values.put(COLUMN_USER_PASSWORD, password)
        val resultDB = db.insert(TABLE_USERS, null, values)
        return resultDB != (-1).toLong()
    }

    // Método para obtener el ID de un usuario por su nombre
    @SuppressLint("Range")
    fun getUserId(name: String): Int {
        val db = this.readableDatabase
        val sqlQ = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_USER_NAME = ?"
        val cursor = db.rawQuery(sqlQ, arrayOf(name.lowercase()))
        cursor.moveToFirst()
        val userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
        cursor.close()
        return userId
    }

    // Método para listar las rutas de un usuario
    @SuppressLint("Range")
    fun listRoutes(userID: Int): ArrayList<Routes> {
        Log.d("noe", "userID en listRoutes() vale $userID")
        val sqlQ = "SELECT * FROM $TABLE_ROUTES WHERE $COLUMN_ROUTE_USER_ID = ?"
        val db = this.readableDatabase
        val storeRoutes = ArrayList<Routes>()
        val cursor = db.rawQuery(sqlQ, arrayOf(userID.toString()))
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex(COLUMN_ROUTE_NAME))
                val description = cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION))
                val grade = cursor.getString(cursor.getColumnIndex(COLUMN_GRADE))
                val howWas = cursor.getString(cursor.getColumnIndex(COLUMN_HOWWAS))
                val date = cursor.getString(cursor.getColumnIndex(COLUMN_DATE))
                storeRoutes.add(Routes(name, description, grade, howWas, date, userID))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return storeRoutes
    }

    // Método para contar las rutas por grado
    fun countRouteGrades(userID: Int): Map<String, Int> {
        val sqlQ = "SELECT $COLUMN_GRADE FROM $TABLE_ROUTES WHERE $COLUMN_ROUTE_USER_ID = ?"
        val db = this.readableDatabase
        val routeGradesCount = HashMap<String, Int>()
        val cursor = db.rawQuery(sqlQ, arrayOf(userID.toString()))
        if (cursor.moveToFirst()) {
            do {
                val grade = cursor.getString(cursor.run { getColumnIndex(COLUMN_GRADE) })
                if (routeGradesCount.containsKey(grade)) {
                    // Si ya existe, incrementa el contador
                    val count = routeGradesCount[grade]!! + 1
                    routeGradesCount[grade] = count
                } else {
                    // Si no existe, inicializa el contador en 1
                    routeGradesCount[grade] = 1
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return routeGradesCount.toSortedMap()
    }

    // Método para obtener el número de rutas de un usuario
    fun getNumberRoutes(userID: Int): Int {
        val db = this.readableDatabase
        val sqlQ = "SELECT * FROM $TABLE_ROUTES WHERE $COLUMN_ROUTE_USER_ID = $userID"
        val cursor = db.rawQuery(sqlQ, null)
        val rowCount = cursor.count
        cursor.close()
        return rowCount
    }

    // Método para añadir una nueva ruta
    fun addRoutes(routes: Routes) {
        val values = contentValuesOf(
            COLUMN_ROUTE_NAME to routes.routeName,
            COLUMN_DESCRIPTION to routes.description,
            COLUMN_GRADE to routes.grade,
            COLUMN_HOWWAS to routes.howWas,
            COLUMN_DATE to routes.date,
            COLUMN_ROUTE_USER_ID to routes.idUser
        )
        val db = this.writableDatabase
        db.insert(TABLE_ROUTES, null, values)
        db.close()
    }

    /*fun updateRoutes(routes: Routes) {
        val values = contentValuesOf(
            COLUMN_ROUTE_NAME to routes.routeName,
            COLUMN_DESCRIPTION to routes.description,
            COLUMN_GRADE to routes.grade,
            COLUMN_HOWWAS to routes.howWas,
            COLUMN_DATE to routes.date
        )
        val db = this.writableDatabase
        db.update(
            TABLE_ROUTES, values, "$COLUMN_ROUTE_ID = ?",
            arrayOf(routes.idRoute.toString())
        )
    }*/

    // Método para eliminar una ruta por ID
    fun deleteRoutes(id: Int) {
        val db = writableDatabase
        Log.d("Noe", "el id en deleteRoute() pasado es $id")
        db.delete(TABLE_ROUTES, "$COLUMN_ROUTE_ID = ?", arrayOf(id.toString()))
        db.close()
    }

    // Método para añadir un nuevo trabajo
    fun addWork(works: Works) {
        val values = contentValuesOf(
            COLUMN_WORK_USER_ID to works.userId,
            COLUMN_TITLE to works.title,
            COLUMN_WORK_TIME to works.workTime,
            COLUMN_REST_TIME to works.restTime,
            COLUMN_ROUNDS to works.rounds
        )
        val db = this.writableDatabase
        db.insert(TABLE_WORKS, null, values)
        db.close()
    }

    // Método para obtener los títulos de trabajos de un usuario
    @SuppressLint("Range")
    fun getTitles(userID: Int): List<String> {
        val titles = ArrayList<String>()
        val db = this.readableDatabase
        val sqlQ = "SELECT $COLUMN_TITLE FROM $TABLE_WORKS WHERE $COLUMN_WORK_USER_ID = ?"
        val cursor = db.rawQuery(sqlQ, arrayOf(userID.toString()))
        while (cursor.moveToNext()) {
            val title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE))
            titles.add(title)
        }
        cursor.close()
        db.close()
        return titles
    }

    // Método para obtener los datos de un trabajo por título
    @SuppressLint("Range")
    fun getWorkData(title: String): Map<String, Int>? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_WORKS WHERE $COLUMN_TITLE = ?"
        val cursor = db.rawQuery(query, arrayOf(title))
        if (cursor.moveToFirst()) {
            val workTime = cursor.getInt(cursor.getColumnIndex(COLUMN_WORK_TIME))
            val restTime = cursor.getInt(cursor.getColumnIndex(COLUMN_REST_TIME))
            val rounds = cursor.getInt(cursor.getColumnIndex(COLUMN_ROUNDS))
            return mapOf(COLUMN_WORK_TIME to workTime, COLUMN_REST_TIME to restTime, COLUMN_ROUNDS to rounds)
        }
        cursor.close()
        db.close()
        return null
    }

    // Método para eliminar un trabajo por título
    fun deleteWorks(title: String) {
        val db = writableDatabase
        db.delete(TABLE_WORKS, "$COLUMN_TITLE = ?", arrayOf(title))
        db.close()
    }
}
