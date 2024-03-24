package com.example.mafiahelper

import android.annotation.SuppressLint


class Player(number: UInt, name: String?, role: Role) {
    val _number = number
    var _name = name
    var _isAlive: Boolean = true
    var _role: Role
    var _isSaveCurrentNight = false
    var _isDisabledCurrentDay = false
    var _target: Player? = null

    init {
        _role = role
    }

    fun updatePlayer(name: String?, role: Role): Player {
        _name = name
        _role = role
        return this
    }

    fun doAction(target: Player, day: Int, stage: Stages) {

        if (!this._isDisabledCurrentDay
            && this._role.canPerformAction(day, stage)) {
            println("action by ${this._number} with role ${this._role.name} to ${target._number}")
            this._role.performAction(target)
        }
    }
    fun checkDoAction(target: Player, day: Int, stage: Stages): Boolean {
        return (this._isAlive
                && !this._isDisabledCurrentDay
                && target._isAlive
                && this._role.canPerformAction(day, stage))
    }

    fun doDie() {
        if (this._isAlive && !this._isSaveCurrentNight && this._role.isCanDie) {
            _isAlive = false
            println("${this._number} is dead")
        }
    }
    fun doSave() {
        _isAlive = true
        _isSaveCurrentNight = true
        println("${this._number} is alive")
    }

    fun dropNightModifiers() {
        _isSaveCurrentNight = false
        _isDisabledCurrentDay = false
        _target = null
    }

    @SuppressLint("SuspiciousIndentation")
    fun selectTarget(targetPlayer: Player, day: Int, stage: Stages) {
        if (this._isAlive
            && !this._isDisabledCurrentDay
            && targetPlayer._isAlive
            && this._role.canPerformAction(day, stage))
        _target = targetPlayer
    }

}