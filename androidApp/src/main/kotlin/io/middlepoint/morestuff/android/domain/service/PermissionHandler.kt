package io.middlepoint.morestuff.android.domain.service

import io.middlepoint.morestuff.android.domain.model.Permission

interface PermissionHandler {

    fun checkPermissionGranted(permission: Permission): Boolean

    fun request(permission: Permission)

}