package com.example.dpproject.domain

class Location {
    private var id: Int = 0
    private var loc: String = ""

    constructor() {
        // Default constructor
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getLoc(): String {
        return loc
    }

    fun setLoc(loc: String) {
        this.loc = loc
    }

    override fun toString(): String {
        return loc  // Fixed 'Loc' typo to 'loc'
    }
}
