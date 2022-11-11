package com.craftinginterpreters.tool

import java.io.PrintWriter
import kotlin.system.exitProcess

class GenerateAst {
    fun main(args: Array<String>) {
        if (args.size != 1) {
            System.err.println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]
        defineAst(
            outputDir,
            "Expr",
            listOf(
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Any? value",
                "Unary    : Token operator, Expr right",
            )
        )
    }

    private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
        val path = "$outputDir/$baseName.kt"
        val writer = PrintWriter(path, "UTF-8")

        writer.println("package com.craftinginterpreters.lox")
        writer.println()
        writer.println("sealed class $baseName {")

        defineVisitors(writer, baseName, types)

        // The AST classes.
        types.map { type ->
            val className = type.split(":")[0].trim()
            val fields = type.split(":")[1].trim()
            defineType(writer, baseName, className, fields)
        }

        // The base accept() method.
        writer.println("    abstract fun <R> accept(visitor: Visitor<R>): R")

        writer.println("}")

        writer.close()
    }

    private fun defineVisitors(writer: PrintWriter, baseName: String, types: List<String>) {
        writer.println("    interface Visitor<R> {")

        types.map { type ->
            val typeName = type.split(":")[0].trim()
            writer.println("        fun visit$typeName$baseName(${baseName.lowercase()}: $typeName): R")
        }

        writer.println("  }")
        writer.println()
    }

    private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
        writer.println("    data class $className(")

        // Constructor.
        val fields = fieldList.split(", ")
        fields.map { field ->
            val type = field.split(" ")[0]
            val name = field.split(" ")[1]
            writer.println("        val $name: $type,")
        }
        writer.println("    ) : $baseName() {")

        // Visitor pattern.
        writer.println("        override fun <R> accept(visitor: Visitor<R>): R {")
        writer.println("            return visitor.visit$className$baseName(this)")
        writer.println("        }")

        writer.println("    }")
        writer.println()
    }
}

fun main(args: Array<String>) {
    GenerateAst().main(args)
}
