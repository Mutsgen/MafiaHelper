package com.example.mafiahelper

import android.content.Context

class Role(private val context: Context, roleDTO: RoleDTO) {
    val id: UInt
    val name: String
    val isBaseRole: Boolean
    val isDoNight: Boolean
    val isCanDie: Boolean
    val team: Short
    val actFrequency: Short
    val icon: Int?
    val isDoKill: Boolean
    val isDoSave: Boolean
    /* TODO: реализовать шерифа сканирование*/
    val code: String?
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