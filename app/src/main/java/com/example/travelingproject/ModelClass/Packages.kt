package com.example.travelingproject.ModelClass

class Packages
{
    var id = ""
    var imageUrl : ArrayList<String> = ArrayList()
    var name = ""
    var mobile = ""
    var price = 0
    var days = 0
    var notes = ""

    constructor(id: String,imageUrl : ArrayList<String> = ArrayList(),name : String,mobile : String, price : Int, days : Int, notes : String)
    {
        this.id = id
        this.imageUrl = imageUrl
        this.name = name
        this.mobile = mobile
        this.price = price
        this.days = days
        this.notes = notes
    }

    constructor()
    {

    }
}