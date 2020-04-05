package com.github.balashov.exchange.account

import com.github.balashov.exchange.exchange.ExchangeClientImpl
import com.github.balashov.exchange.model.Share
import com.github.balashov.exchange.utils.ExchangeTestContainer
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AccountImplTest {
    private val imageName = "exchange:v0.0.1"
    private val hostPort = 13338
    private val containerPort = 8080
    private val host = "http://localhost:$hostPort"
    private val company = "YaCo"

    private val exchangeClient = ExchangeClientImpl(hostPort)
    private lateinit var account: Account
    private lateinit var exchangeContainer: ExchangeTestContainer
    private val client = HttpClient { expectSuccess = false }

    @Before
    fun setUp() {
        account = AccountImpl(exchangeClient)

        exchangeContainer = ExchangeTestContainer(imageName)
            .withFixedExposedPort(hostPort, containerPort)
            .withExposedPorts(containerPort)

        exchangeContainer.start()
    }

    @After
    fun stop() {
        exchangeContainer.stop()
    }

    @Test
    fun testRegisterUserId() {
        val id = account.registerUser("Pavel")
        val expectedId = 0
        assertEquals(expectedId, id)
    }

    @Test
    fun testAddGetMoney() {
        val id = account.registerUser("Pavel")
        val addMoney = 200
        val addMoneySnd = 1500

        val expectedStartMoney = 0
        var money = account.getMoney(id)
        assertEquals(expectedStartMoney, money)

        var result = account.addMoney(id, addMoney)
        assertTrue(result)
        money = account.getMoney(id)
        assertEquals(addMoney, money)

        result = account.addMoney(id, addMoneySnd)
        assertTrue(result)
        money = account.getMoney(id)
        assertEquals(addMoney + addMoneySnd, money)
    }

    @Test
    fun testBuyShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val request = "$host/register?name=$company&count=5&price=10"
        val response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val buyResult = account.buyShares(id, company, 3)
        assertNotNull(buyResult)
        assertEquals(10, buyResult.second)
        assertEquals(3, buyResult.first)
    }

    @Test
    fun testBuyOverShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val request = "$host/register?name=$company&count=5&price=10"
        val response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        var buyResult = account.buyShares(id, company, 12)
        assertNotNull(buyResult)
        assertEquals(10, buyResult.second)
        assertEquals(5, buyResult.first)

        buyResult = account.buyShares(id, company, 5)
        assertNotNull(buyResult)
        assertEquals(10, buyResult.second)
        assertEquals(0, buyResult.first)
    }

    @Test
    fun testBuyNotExistShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val buyResult = account.buyShares(id, company, 12)
        assertNull(buyResult)
    }

    @Test
    fun testSellShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val request = "$host/register?name=$company&count=5&price=10"
        val response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val buyResult = account.buyShares(id, company, 3)
        assertNotNull(buyResult)

        var curMoney = account.sellShares(id, company, 2)
        assertEquals(190, curMoney)

        curMoney = account.sellShares(id, company, 1)
        assertEquals(200, curMoney)
    }

    @Test
    fun testSellOverShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val request = "$host/register?name=$company&count=5&price=10"
        val response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val buyResult = account.buyShares(id, company, 3)
        assertNotNull(buyResult)

        val curMoney = account.sellShares(id, company, 5)
        assertNull(curMoney)
    }

    @Test
    fun testSellNoShares() = runBlocking {
        val id = account.registerUser("Pavel")

        val curMoney = account.sellShares(id, company, 5)
        assertNull(curMoney)
    }

    @Test
    fun testGetShares() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        val request = "$host/register?name=$company&count=5&price=10"
        val response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val buyResult = account.buyShares(id, company, 3)
        assertNotNull(buyResult)

        val shares = account.getShares(id)
        val expectedShares = HashSet<Share>()
        expectedShares.add(Share(company, 10, 3))
        assertEquals(expectedShares, shares)
    }

    @Test
    fun testGetMoneyWithShare() = runBlocking {
        val id = account.registerUser("Pavel")
        val addMoney = 200

        val result = account.addMoney(id, addMoney)
        assertTrue(result)

        var request = "$host/register?name=$company&count=5&price=10"
        var response = client.get<HttpResponse>(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val buyResult = account.buyShares(id, company, 3)
        assertNotNull(buyResult)

        request = "$host/change_share_price?name=$company&price=50"
        response = client.get(request)
        assertEquals(HttpStatusCode.OK, response.status)

        val money = account.getMoneyWithShare(id)
        assertEquals(addMoney - 3 * 10 + 50 * 3, money)
    }
}
