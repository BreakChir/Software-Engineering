package com.github.balashov.rx

import org.bson.Document

class Product(
    val id: Int,
    private val title: String,
    private val price: Double
) {

    constructor(doc: Document) : this(
        doc.getInteger("id"),
        doc.getString("title"),
        doc.getDouble("price")
    )

    fun withScaledPrice(multiplier: Double): Product =
        Product(id, title, price * multiplier)

    fun toDocument(): Document {
        val document = Document()
        document.append("id", id)
            .append("title", title)
            .append("price", price)
        return document
    }

    override fun toString(): String {
        return "Product{id='$id', title='$title', price='$price'}"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Product) return false
        return this.id == other.id
    }
}