package com.nmg.logyourclimbs.aidfuns

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.nmg.logyourclimbs.R
import java.io.*

class FileDB {

    fun exportDB(uri: Uri, context: Context, databaseName: String) {
        // Obtengo la ruta absoluta de la base de datos.
        val databasePath = context.getDatabasePath(databaseName).absolutePath
        // Creo un objeto File con la ruta de la base de datos.
        val databaseFile = File(databasePath)

        try {
            // Abro el archivo de la base de datos para leer.
            databaseFile.inputStream().use { input ->
                // Abro el URI para escribir los datos.
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    // Copio los datos del archivo de la base de datos al URI.
                    input.copyTo(output)
                }
            }
            // Muestro un mensaje de éxito si la exportación fue exitosa.
            Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // En caso de error, imprimo la traza de la excepción.
            e.printStackTrace()
            // Muestro un mensaje de error si la exportación falló.
            Toast.makeText(context, context.getString(R.string.export_error), Toast.LENGTH_SHORT).show()
        }
    }

    fun importDB(uri: Uri, context: Context, databaseName: String) {
        // Obtengo la ruta absoluta de la base de datos.
        val databasePath = context.getDatabasePath(databaseName).absolutePath
        // Creo un objeto File con la ruta de la base de datos.
        val databaseFile = File(databasePath)

        try {
            // Abro el URI para leer los datos.
            context.contentResolver.openInputStream(uri)?.use { input ->
                // Abro el archivo de la base de datos para escribir.
                databaseFile.outputStream().use { output ->
                    // Copio los datos del URI al archivo de la base de datos.
                    input.copyTo(output)
                }
            }
            // Muestro un mensaje de éxito si la importación fue exitosa.
            Toast.makeText(context, context.getString(R.string.import_success), Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            // En caso de error, imprimo la traza de la excepción.
            e.printStackTrace()
            // Muestro un mensaje de error si la importación falló.
            Toast.makeText(context, context.getString(R.string.import_error), Toast.LENGTH_SHORT).show()
        }
    }

}
