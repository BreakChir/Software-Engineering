package com.github.balashov.rx

import rx.Observable

interface Database {
    val users: Observable<User>
    fun getProductsForUser(id: Int): Observable<Product>
    fun registerUser(user: User): Observable<Boolean>
    fun addProduct(product: Product): Observable<Boolean>
}
