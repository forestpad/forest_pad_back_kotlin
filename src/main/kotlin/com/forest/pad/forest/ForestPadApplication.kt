package com.forest.pad.forest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class ForestPadApplication

fun main(args: Array<String>) {
	runApplication<ForestPadApplication>(*args)
}
