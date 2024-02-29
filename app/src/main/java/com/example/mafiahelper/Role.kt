package com.example.mafiahelper

import android.content.Context

class Role(private val context: Context, roleDTO: RoleDTO) {
    var id: UInt
    var name: String
    var isBaseRole: Boolean
    var isDoNight: Boolean
    var isCanDie: Boolean
    var team: Short
    var actFrequency: Short
    var icon: Int?

    var isDoKill: Boolean
    var isDoSave: Boolean

    var code: String?
    init {
        id = roleDTO.id
        name = roleDTO.name
        isBaseRole = roleDTO.isBaseRole
        isDoNight = roleDTO.isDoNight
        isCanDie = roleDTO.isCanDie
        team = roleDTO.team
        actFrequency = roleDTO.actFrequency
        icon = roleDTO.icon
        isDoKill = roleDTO.isDoKill
        isDoSave = roleDTO.isDoSave
        code = roleDTO.code
    }

    // Метод для выполнения действия роли
    fun performAction(target: Player) {
        // Здесь можно добавить логику в зависимости от роли
         when {
            isDoKill -> target.doDie()
            isDoSave -> target.doSave()
        }
    }

    // Метод для проверки, может ли роль выполнять действие
    fun canPerformAction(day: Int, stage: Stages): Boolean {
        return day % this.actFrequency  == 0 && (stage == Stages.NIGHT && this.isDoNight)
    }
}