package com.github.balashov.rx

import com.mongodb.rx.client.MongoCollection
import org.bson.Document
import rx.Observable

open class MongoDB(
    private var usersCollection: MongoCollection<Document>,
    private var productsCollection: MongoCollection<Document>
): Database {
    private fun convert(currency: String): Double {
        return when (currency) {
            "RUB" -> 61.3
            "EUR" -> 0.93
            "USD" -> 1.00
            else -> 1.00
        }
    }

    override val users: Observable<User>
        get() {
            return usersCollection.find().toObservable().map { User(it) }
        }

    override fun getProductsForUser(id: Int): Observable<Product> {
        return usersCollection
            .find()
            .toObservable()
            .filter { it.getInteger("id") == id }
            .map { User(it).currency }
            .map { convert(it) }
            .flatMap { multiplier ->
                productsCollection
                    .find()
                    .toObservable()
                    .map { Product(it).withScaledPrice(multiplier) }
            }
    }

    private fun insertById(id: Int, document: Document, collection: MongoCollection<Document>): Observable<Boolean> {
        return collection
            .find()
            .toObservable()
            .filter { it.getInteger("id") == id }
            .singleOrDefault(null)
            .flatMap { foundDoc ->
                if (foundDoc != null) {
                    Observable.just(false)
                } else {
                    collection
                        .insertOne(document)
                        .asObservable()
                        .isEmpty
                        .map { !it }
                }
            }
    }

    override fun registerUser(user: User): Observable<Boolean> {
        return insertById(user.id, user.toDocument(), usersCollection)
    }

    override fun addProduct(product: Product): Observable<Boolean> {
        return insertById(product.id, product.toDocument(), productsCollection)
    }
}
