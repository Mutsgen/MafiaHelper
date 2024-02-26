package com.example.mafiahelper

import android.annotation.SuppressLint


class Player(number: UInt, name: String?, role: Role) {
    val _number = number
    var _name = name
    var _isAlive: Boolean = true
    var _role: Role
    var _isSaveCurrentNight = false
    var _isDisabledCurrentDay = false
    var target: Player? = null

    init {
        _role = role
    }

    fun updatePlayer(name: String?, role: Role): Player {
        _name = name
        _role = role
        return this
    }

    fun doAction(target: Player, day: Int, stage: Stages) {
        if (this._isAlive
            && !this._isDisabledCurrentDay
            && target._isAlive
            && this._role.canPerformAction(day, stage)) {
            this._role.performAction(target)
        }
    }

    fun doDie() {
        if (this._isAlive && !this._isSaveCurrentNight && this._role.isCanDie) {
            _isAlive = false
        }
    }
    fun doSave() {
        _isAlive = true
        _isSaveCurrentNight = true
    }

    fun dropNightModifiers() {
        _isSaveCurrentNight = false
        _isDisabledCurrentDay = false
        target = null
    }

    @SuppressLint("SuspiciousIndentation")
    fun selectTarget(targetPlayer: Player, day: Int, stage: Stages) {
        if (this._isAlive
            && !this._isDisabledCurrentDay
            && targetPlayer._isAlive
            && this._role.canPerformAction(day, stage))
        target = targetPlayer
    }

}