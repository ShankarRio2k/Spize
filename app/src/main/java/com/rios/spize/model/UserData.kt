package com.rios.spize.model


data class UserData(
    val id: String,
    var userName: String? = null,
    val userMail: String? = null,
    var userProfileImage: String? = null,
    val userGender: String? = null,
    var userAge: Int = 26
) {
    constructor() : this("", null, null, null, null, 26)
}

