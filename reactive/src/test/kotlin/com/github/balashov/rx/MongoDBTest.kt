package com.github.balashov.rx

import com.mongodb.rx.client.FindObservable
import com.mongodb.rx.client.MongoCollection
import com.mongodb.rx.client.MongoDatabase
import com.mongodb.rx.client.Success
import org.bson.Document
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import io.mockk.every
import io.mockk.mockk
import rx.Observable

@SuppressWarnings("unchecked")
class MongoDBTest {

    private lateinit var db: MongoDatabase
    private lateinit var users: MongoCollection<Document>
    private lateinit var products: MongoCollection<Document>
    private lateinit var mongoDB: Database
    private lateinit var findUserResult: FindObservable<Document>
    private lateinit var findProductResult: FindObservable<Document>

    private val pavel = User(1, "Pavel", "RUR")
    private val alex = User(2, "Alex", "USD")

    private val apple = Product(1, "Apple", 1.34)
    private val tomato = Product(2, "Tomato", 2.99)

    @Before
    fun setUp() {
        db = mockk()
        users = mockk()
        products = mockk()

        every { db.getCollection("users") }.returns(users)
        every { db.getCollection("products") }.returns(products)

        mongoDB = MongoDB(db.getCollection("users"), db.getCollection("products"))

        findUserResult = mockk()
        findProductResult = mockk()

        every { users.find() }.returns(findUserResult)
        every { products.find() }.returns(findProductResult)
    }

    @Test
    fun testRegisterUser() {
        every { findUserResult.toObservable() }.returns(Observable.empty())
        every { users.insertOne(pavel.toDocument()) }.returns(Observable.just(Success.SUCCESS))

        val actualResponse = mongoDB.registerUser(pavel).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(true)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testRegisterExistUser() {
        every { findUserResult.toObservable() }.returns(Observable.just(pavel.toDocument()))
        every { users.insertOne(pavel.toDocument()) }.returns(Observable.just(Success.SUCCESS))

        val actualResponse = mongoDB.registerUser(pavel).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(false)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testGetUsers() {
        every { findUserResult.toObservable() }.returns(Observable.just(pavel.toDocument()))

        val actualResponse = mongoDB.users.toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(pavel)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testGetEmptyUsers() {
        every { findUserResult.toObservable() }.returns(Observable.empty())

        val actualResponse = mongoDB.users.toBlocking().iterator.asSequence().toList()
        val expectedResponse = emptyList<User>()
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testAddProduct() {
        every { findProductResult.toObservable() }.returns(Observable.empty())
        every { products.insertOne(apple.toDocument()) }.returns(Observable.just(Success.SUCCESS))

        val actualResponse = mongoDB.addProduct(apple).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(true)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testAddExistProduct() {
        every { findProductResult.toObservable() }.returns(Observable.just(apple.toDocument()))
        every { products.insertOne(apple.toDocument()) }.returns(Observable.just(Success.SUCCESS))

        val actualResponse = mongoDB.addProduct(apple).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(false)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testGetProductsForUser() {
        every { findUserResult.toObservable() }.returns(Observable.just(pavel.toDocument()))
        every { findProductResult.toObservable() }.returns(Observable.just(apple.toDocument(), tomato.toDocument()))

        val actualResponse = mongoDB.getProductsForUser(pavel.id).toBlocking().iterator.asSequence().toList()
        val expectedResponse = listOf(apple, tomato)
        assertEquals(expectedResponse, actualResponse)
    }

    @Test
    fun testGetProductsForNotExistUser() {
        every { findUserResult.toObservable() }.returns(Observable.just(pavel.toDocument()))
        every { findProductResult.toObservable() }.returns(Observable.just(apple.toDocument(), tomato.toDocument()))

        val actualResponse = mongoDB.getProductsForUser(alex.id).toBlocking().iterator.asSequence().toList()
        val expectedResponse = emptyList<Product>()
        assertEquals(expectedResponse, actualResponse)
    }
}
