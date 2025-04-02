package org.constructorexample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform