package com.rios.spize.model


data class UserData(
    val userName: String? = null,
    val userMail: String? = null,
    val userProfileImage: String? = null,
    val userGender: String? = null
) {
    constructor() : this(null, null, null,null)
}

