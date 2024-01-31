package co.softov.morestuff.android.domain.service

import co.softov.morestuff.android.domain.model.Permission

interface PermissionHandler {

    fun checkPermissionGranted(permission: Permission): Boolean

    fun request(permission: Permission)

}