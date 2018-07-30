package com.example.dmelnyk.ft_hangouts

import java.io.Serializable

class Contact : Serializable {
    var id: Int = 0
    var first_name: String = ""
    var last_name: String = ""
    var phone_number: String = ""
    var email: String = ""
    var photo: String = "default"

    constructor()

    constructor(first_name: String, last_name: String, phone_number: String, email: String) {
        this.first_name = first_name
        this.last_name = last_name
        this.phone_number = phone_number
        this.email = email
    }
}