package com.example.travelingproject.ModelClass

class ModelClass {
    var id: String = ""
    var username: String = ""
    var email: String = ""
    var password: String = ""
    var address: String = ""
    var mobile: String = ""
    var selectedCategory=0

    constructor(
        id: String,
        username: String,
        email: String,
        password: String,
        address: String,
        mobile: String,
        selectedCategory: Int

    ) {
        this.id = id
        this.username = username
        this.email = email
        this.password = password
        this.address = address
        this.mobile = mobile
        this.selectedCategory = selectedCategory
    }

    constructor() {

    }
}