package com.github.balashov.rx

import rx.Observable
import io.reactivex.netty.protocol.http.server.HttpServerRequest

class Router(private val mongoDB: Database) {

    private fun <T> HttpServerRequest<T>.getQueryParam(paramName: String): String {
        val paramsList = this.queryParameters[paramName]?.toList() ?: error("Parameter $paramName is required")
        return paramsList[0]
    }

    fun <T> route(request: HttpServerRequest<T>): Observable<String> {
        return when (val path = request.decodedPath.substring(1)) {
            "register" -> postRegisterRoute(request)
            "users" -> getUsersRoute()
            "get_products" -> getProductsRoute(request)
            "add_products" -> postProductsRoute(request)
            else -> Observable.just("Error: Incorrect path - $path")
        }
    }

    private fun <T> postRegisterRoute(
        request: HttpServerRequest<T>
    ): Observable<String> {
        val id = request.getQueryParam("id")
        val name = request.getQueryParam("name")
        val currency = request.getQueryParam("currency")

        val user = User(id.toInt(), name, currency)
        return mongoDB.registerUser(user).map { "$it\n" }
    }

    private fun getUsersRoute(): Observable<String> {
        return mongoDB.users.map { "$it\n" }
    }

    private fun <T> getProductsRoute(
        request: HttpServerRequest<T>
    ): Observable<String> {
        val id = request.getQueryParam("id")

        return mongoDB.getProductsForUser(id.toInt()).map { "$it\n" }
    }

    private fun <T> postProductsRoute(
        request: HttpServerRequest<T>
    ): Observable<String> {
        val id = request.getQueryParam("id")
        val title = request.getQueryParam("title")
        val price = request.getQueryParam("price")

        val product = Product(id.toInt(), title, price.toDouble())
        return mongoDB.addProduct(product).map { "$it\n" }
    }
}
