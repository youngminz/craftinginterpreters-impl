package com.craftinginterpreters.lox

class Environment(
    private val enclosing: Environment? = null,
) {
    private val values = mutableMapOf<String, Any?>()

    fun get(name: Token): Any? {
        if (values.containsKey(name.lexeme)) {
            return values[name.lexeme]
        }

        enclosing?.let {
            return enclosing.get(name)
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun assign(name: Token, value: Any?) {
        if (values.containsKey(name.lexeme)) {
            values[name.lexeme] = value
            return
        }

        enclosing?.let {
            enclosing.assign(name, value)
            return
        }

        throw RuntimeError(name, "Undefined variable '${name.lexeme}'.")
    }

    fun define(name: String, value: Any?) {
        values[name] = value
    }
}
