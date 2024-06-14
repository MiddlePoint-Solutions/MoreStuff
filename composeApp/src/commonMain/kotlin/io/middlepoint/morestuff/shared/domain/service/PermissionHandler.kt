package io.middlepoint.morestuff.shared.domain.service

import io.middlepoint.morestuff.shared.domain.model.Permission

interface PermissionHandler {

    fun checkPermissionGranted(permission: Permission): Boolean

    fun request(permission: Permission)

}