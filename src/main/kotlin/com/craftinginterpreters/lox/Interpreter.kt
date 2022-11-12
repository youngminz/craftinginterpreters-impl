package com.craftinginterpreters.lox

import com.craftinginterpreters.lox.TokenType.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

class Interpreter : Expr.Visitor<Any?> {
    fun interpret(expression: Expr) {
        try {
            val value = evaluate(expression)
            println(stringify(value))
        } catch (error: RuntimeError) {
            Lox.runtimeError(error)
        }
    }

    override fun visitBinaryExpr(expr: Expr.Binary): Any? {
        val left = evaluate(expr.left)
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            GREATER -> {
                checkNumberOperand(expr.operator, left, right)
                left > right
            }
            GREATER_EQUAL -> {
                checkNumberOperand(expr.operator, left, right)
                left >= right
            }
            LESS -> {
                checkNumberOperand(expr.operator, left, right)
                left < right
            }
            LESS_EQUAL -> {
                checkNumberOperand(expr.operator, left, right)
                left <= right
            }
            MINUS -> {
                checkNumberOperand(expr.operator, left, right)
                left - right
            }
            PLUS -> {
                if (left is Double && right is Double) {
                    left + right
                } else if (left is String && right is String) {
                    left + right
                } else {
                    throw RuntimeError(expr.operator, "Operands must be two numbers or two strings.")
                }
            }
            SLASH -> {
                checkNumberOperand(expr.operator, left, right)
                left / right
            }
            STAR -> {
                checkNumberOperand(expr.operator, left, right)
                left * right
            }
            BANG_EQUAL -> {
                !isEqual(left, right)
            }
            EQUAL_EQUAL -> {
                isEqual(left, right)
            }

            else -> {
                // Unreachable.
                null
            }
        }
    }

    override fun visitGroupingExpr(expr: Expr.Grouping): Any? {
        return evaluate(expr.expression)
    }

    override fun visitLiteralExpr(expr: Expr.Literal): Any? {
        return expr.value
    }

    override fun visitUnaryExpr(expr: Expr.Unary): Any? {
        val right = evaluate(expr.right)

        return when (expr.operator.type) {
            BANG -> {
                !isTruthy(right)
            }
            MINUS -> {
                checkNumberOperand(expr.operator, right)
                -right
            }

            else -> {
                // Unreachable.
                null
            }
        }
    }

    @OptIn(ExperimentalContracts::class)
    private fun checkNumberOperand(operator: Token, operand: Any?) {
        contract {
            returns() implies (operand is Double)
        }
        if (operand is Double) {
            return
        }
        throw RuntimeError(operator, "Operand must be a number.")
    }

    @OptIn(ExperimentalContracts::class)
    private fun checkNumberOperand(operator: Token, left: Any?, right: Any?) {
        contract {
            returns() implies (left is Double && right is Double)
        }
        if (left is Double && right is Double) {
            return
        }
        throw RuntimeError(operator, "Operands must be numbers.")
    }

    private fun isTruthy(any: Any?): Boolean {
        if (any == null) {
            return false
        }
        if (any is Boolean) {
            return any
        }
        return true
    }

    private fun isEqual(a: Any?, b: Any?): Boolean {
        return a == b
    }

    private fun stringify(any: Any?): String {
        if (any == null) {
            return "nil"
        }

        if (any is Double) {
            var text = any.toString()
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length - 2)
            }
            return text
        }

        return any.toString()
    }

    private fun evaluate(expr: Expr): Any? {
        return expr.accept(this)
    }
}
