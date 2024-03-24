package com.example.mafiahelper

enum class Stages {
    NIGHT,
    DAY
}

class Game(players: List<Player> = listOf()) {
    var currentDay: Int = 1
    var currentStage: Stages = Stages.DAY
    private val teamCounts = mutableMapOf<Short, Int>()
    var nowDon: Player? = null
    var nowVoteTarget: Player? = null
    val _players = players


    init {
        players.groupBy { it._role.team }.forEach { (team, players) ->
            teamCounts[team] = players.size
        }
        setDon()
    }

    private fun swapStage() {
        currentStage = when (currentStage) {
            Stages.DAY -> Stages.NIGHT
            Stages.NIGHT -> {
                currentDay++
                Stages.DAY
            }
        }
    }

    private fun setDon() {
        nowDon = _players.firstOrNull { it._role.name == "Мафия" && it._isAlive }
    }

    fun closeStage(): Game {
        setDon()
        when (currentStage) {
            Stages.NIGHT -> performNightActions()
            Stages.DAY -> performDayActions()
        }
        swapStage()

        return Game(_players).apply {
            currentDay = this@Game.currentDay
            currentStage = this@Game.currentStage
            nowVoteTarget = null
        }
    }

    private fun performNightActions() {
        try {
            nowDon?.let { don ->
                if (currentDay % don._role.actFrequency == 0 && don._target != null) {
                    println("${don._target!!._number} by don")
                    don.doAction(don._target!!, currentDay, currentStage)
                }
            }
            _players.filter { it._role.name != "Мафия" && it._target != null}
                .forEach {
                    println("${it._target!!._number} by smth")
                    it.doAction(it._target!!,  currentDay, currentStage)
                }
            for (player in _players) {
                player.dropNightModifiers()
            }

        } catch (error: Exception) {
            println(error)
        }
    }

    private fun performDayActions() {
        nowVoteTarget?.let { it.doDie() }
        nowVoteTarget = null
        println(this.toString())
    }

    fun returnNowActions(): MutableList<Player> {
        val actions = mutableListOf<Player>()
        if (nowDon != null && currentStage == Stages.NIGHT) actions.add(nowDon!!)
        else {
            setDon()
            if (nowDon != null && currentStage == Stages.NIGHT) actions.add(nowDon!!)
        }
        _players.filter { it._role.name != "Мафия"
                && it._isAlive
                && !it._isDisabledCurrentDay
                && it._role.canPerformAction(currentDay, currentStage) }
            .forEach {  actions.add(it) }

        return actions
    }


    override fun toString(): String {
        return "Game(day=$currentDay, stage=$currentStage, teamCounts=$teamCounts)"
    }

}