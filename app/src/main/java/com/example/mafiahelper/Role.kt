package com.example.mafiahelper

import android.content.Context

class Role(private val context: Context, roleId: UInt) {
    var id: UInt
    var name: String
    var isBaseRole: Boolean
    var isDoNight: Boolean
    var isCanDie: Boolean
    var team: Short
    var actFrequency: Short
    var icon: Int?

    init {
        val db = DbHelper(context = context, factory = null)
        val roleDTO = db.getCurentRole(roleId)
        if (roleDTO != null) {
            id = roleDTO.id
            name = roleDTO.name
            isBaseRole = roleDTO.isBaseRole
            isDoNight = roleDTO.isDoNight
            isCanDie = roleDTO.isCanDie
            team = roleDTO.team
            actFrequency = roleDTO.actFrequency
            icon = roleDTO.icon
        } else {
            throw IllegalArgumentException("Role with id $roleId not found")
        }
    }

    // Метод для выполнения действия роли
    fun performAction(target: Player): String {
        // Здесь можно добавить логику в зависимости от роли
        return "Action performed on ${target}"
    }

    // Метод для проверки, может ли роль выполнять действие
    fun canPerformAction(): Boolean {
        // Здесь можно добавить логику в зависимости от роли
        return true
    }
}