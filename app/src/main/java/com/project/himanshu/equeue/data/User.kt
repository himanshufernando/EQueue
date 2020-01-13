package com.project.himanshu.equeue.data

data class User(
    var userID: String?,
    var name: String?,
    var userName: String?,
    var type: String?,
    var ststus : Boolean = false,
    var error: Error
) {
}