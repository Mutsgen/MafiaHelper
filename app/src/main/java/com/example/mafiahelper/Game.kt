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
        nowDon = _players.firstOrNull { it._role.name == "Мафия" && it._isAlive } ?: return
    }

    fun closeStage() {
        setDon()
        when (currentStage) {
            Stages.NIGHT -> performNightActions()
            Stages.DAY -> performDayActions()
        }
        swapStage()
    }

    private fun performNightActions(): Int {
        return try {
            nowDon?.let { don ->
                if (don._role.actFrequency % currentDay == 0) {
                    don.doAction(don._target!!, currentDay, currentStage)
                }
            }
            _players.filter { it._role.name != "Мафия" }
                .forEach { it.doAction(it._target!!,  currentDay, currentStage) }
            1
        } catch (error: Exception) {
            0
        }
    }

    private fun performDayActions() {
        nowVoteTarget?.let { it._isAlive = false }
        nowVoteTarget = null
    }

    fun returnNowActions() {
        val actions = mutableListOf<Player>()
        if (nowDon != null) actions.add(nowDon!!)
        else {
            setDon()
            if (nowDon != null) actions.add(nowDon!!)
        }
        _players.filter { it._role.name != "Мафия" && it._isAlive &&
                it._role.canPerformAction(currentDay, currentStage) }
            .forEach {  actions.add(it) }
    }


    override fun toString(): String {
        return "Game(day=$currentDay, stage=$currentStage, teamCounts=$teamCounts)"
    }

}