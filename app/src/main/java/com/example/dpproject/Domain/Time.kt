package com.example.dpproject.domain

class Time {
    private var id: Int = 0
    private var value: String = ""

    constructor() {
        // Default constructor
    }

    override fun toString(): String {
        return value
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getValue(): String {
        return value
    }

    fun setValue(value: String) {
        this.value = value
    }
}