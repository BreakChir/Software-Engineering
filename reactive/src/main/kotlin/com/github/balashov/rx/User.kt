package com.github.balashov.rx

import org.bson.Document

class User(
    val id: Int,
    private val name: String,
    val currency: String
) {

    constructor(doc: Document) : this(
        doc.getInteger("id"),
        doc.getString("name"),
        doc.getString("currency")
    )

    fun toDocument(): Document {
        val document = Document()
        document.append("id", id)
            .append("name", name)
            .append("currency", currency)
        return document
    }

    override fun toString(): String {
        return "User{id='$id', name='$name', currency='$currency'}"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is User) return false
        return this.id == other.id
    }
}