package com.github.balashov.exchange.utils

import org.testcontainers.containers.FixedHostPortGenericContainer

class ExchangeTestContainer(imageName: String) : FixedHostPortGenericContainer<ExchangeTestContainer>(imageName) {
    fun pause() {
        dockerClient.pauseContainerCmd(getContainerId()).exec()
    }

    fun unpause() {
        dockerClient.unpauseContainerCmd(getContainerId()).exec()
    }
}
