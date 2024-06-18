package com.nmg.logyourclimbs.countdown

class Works {
    var workID = 0
    var userId: Int
    var title: String
    var workTime: Int
    var restTime: Int
    var rounds: Int

    // Constructor para crear un nuevo trabajo sin ID (se asignará automáticamente)
    internal constructor(userID: Int, title: String, workTime: Int, restTime: Int, rounds: Int) {
        this.userId = userID
        this.title = title
        this.workTime = workTime
        this.restTime = restTime
        this.rounds = rounds
    }

    // Constructor para cargar un trabajo existente desde la base de datos con ID
    internal constructor(workID: Int, userID: Int, title: String, workTime: Int, restTime: Int, rounds: Int) {
        this.workID = workID
        this.userId = userID
        this.title = title
        this.workTime = workTime
        this.restTime = restTime
        this.rounds = rounds
    }
}
