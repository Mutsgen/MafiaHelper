package com.example.mafiahelper

enum class Stages {
    NIGHT,
    DAY
}
class Game(players: List<Player>) {
    var currentDay = 1
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

    fun swapStage() {
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
    }

    private fun performNightActions() {
        nowDon?.let { don ->
            if (don._role.actFrequency % currentDay == 0) {
                don.doAction(don.target!!)
            }
        }
        _players.filter { it._role.name != "Мафия" && it._role.actFrequency % currentDay == 0 }
            .forEach { it.doAction(it.target!!) }
    }

    private fun performDayActions() {
        nowVoteTarget?.let { it._isAlive = false }
        nowVoteTarget = null
    }


    override fun toString(): String {
        return "Game(day=$currentDay, stage=$currentStage, teamCounts=$teamCounts)"
    }

}