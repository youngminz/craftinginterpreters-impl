package com.craftinginterpreters.lox

class RuntimeError(
    val token: Token,
    override val message: String,
) : RuntimeException(message)
