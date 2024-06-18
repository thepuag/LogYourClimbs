package com.nmg.logyourclimbs.routes

class Routes {
    var idRoute = 0
    var routeName: String
    var description: String
    var grade: String
    var howWas: String
    var date: String
    var idUser: Int

    // Constructor para inicializar una ruta sin ID
    internal constructor(name: String, description: String, grade: String, howWas: String, date: String, idUser: Int) {
        // Asigno los valores de los parámetros a las propiedades de la clase
        this.routeName = name
        this.description = description
        this.grade = grade
        this.howWas = howWas
        this.date = date
        this.idUser = idUser
    }

    // Constructor para inicializar una ruta con ID
    internal constructor(id: Int, name: String, description: String, grade: String, howWas: String, date: String, idUser: Int) {
        // Asigno el ID de la ruta y los demás valores de los parámetros a las propiedades de la clase
        this.idRoute = id
        this.routeName = name
        this.description = description
        this.grade = grade
        this.howWas = howWas
        this.date = date
        this.idUser = idUser
    }
}
