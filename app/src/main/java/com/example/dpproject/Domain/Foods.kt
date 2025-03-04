package com.example.dpproject.domain

class Foods {
    private var categoryId: Int = 0
    private var description: String = ""
    private var bestFood: Boolean = false
    private var id: Int = 0
    private var locationId: Int = 0
    private var price: Double = 0.0
    private var imagePath: String = ""
    private var priceId: Int = 0
    private var star: Double = 0.0
    private var timeId: Int = 0
    private var timeValue: Int = 0
    private var title: String = ""
    private var numberInCart: Int = 0

    constructor() {
        // Default constructor
    }

    override fun toString(): String {
        return title
    }

    fun getCategoryId(): Int {
        return categoryId
    }

    fun setCategoryId(categoryId: Int) {
        this.categoryId = categoryId
    }

    fun getDescription(): String {
        return description
    }

    fun setDescription(description: String) {
        this.description = description
    }

    fun isBestFood(): Boolean {
        return bestFood
    }

    fun setBestFood(bestFood: Boolean) {
        this.bestFood = bestFood
    }

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getLocationId(): Int {
        return locationId
    }

    fun setLocationId(locationId: Int) {
        this.locationId = locationId
    }

    fun getPrice(): Double {
        return price
    }

    fun setPrice(price: Double) {
        this.price = price
    }

    fun getImagePath(): String {
        return imagePath
    }

    fun setImagePath(imagePath: String) {
        this.imagePath = imagePath
    }

    fun getPriceId(): Int {
        return priceId
    }

    fun setPriceId(priceId: Int) {
        this.priceId = priceId
    }

    fun getStar(): Double {
        return star
    }

    fun setStar(star: Double) {
        this.star = star
    }

    fun getTimeId(): Int {
        return timeId
    }

    fun setTimeId(timeId: Int) {
        this.timeId = timeId
    }

    fun getTimeValue(): Int {
        return timeValue
    }

    fun setTimeValue(timeValue: Int) {
        this.timeValue = timeValue
    }

    fun getTitle(): String {
        return title
    }

    fun setTitle(title: String) {
        this.title = title
    }

    fun getNumberInCart(): Int {
        return numberInCart
    }

    fun setNumberInCart(numberInCart: Int) {
        this.numberInCart = numberInCart
    }
}
