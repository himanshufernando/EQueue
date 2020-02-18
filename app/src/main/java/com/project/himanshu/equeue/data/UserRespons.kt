package com.project.himanshu.equeue.data

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class UserRespons(
    var device_id: String?,
    var id: String?,
    var name: String?,
    var password: String?,
    var userName: String?,
    var username_password: String?
)