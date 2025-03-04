package com.example.dpproject.domain

class User {
    var username: String = ""
    var email: String = ""
    var password: String = ""

    constructor() {
        // Default constructor required for calls to DataSnapshot.getValue(User::class.java)
    }

    constructor(username: String, email: String, password: String) {
        this.username = username
        this.email = email
        this.password = password
    }
}