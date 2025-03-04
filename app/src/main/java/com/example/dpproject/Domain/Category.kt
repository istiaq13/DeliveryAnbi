package com.example.dpproject.domain

class Category {
    private var id: Int = 0
    private var imagePath: String = ""
    private var name: String = ""

    constructor() {
        // Default constructor
    }

    fun getID(): Int {
        return id
    }

    fun setID(id: Int) {
        this.id = id
    }

    fun getImagePath(): String {
        return imagePath
    }

    fun setImagePath(imagePath: String) {
        this.imagePath = imagePath
    }

    fun getName(): String {
        return name
    }

    fun setName(name: String) {
        this.name = name
    }
}
