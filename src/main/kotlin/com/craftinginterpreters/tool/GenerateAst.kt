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
                "Literal  : Object value",
                "Unary    : Token operator, Expr right",
            )
        )
    }

    companion object {
        private fun defineAst(outputDir: String, baseName: String, types: List<String>) {
            val path = "$outputDir/$baseName.java"
            val writer = PrintWriter(path, "UTF-8")

            writer.println("package com.craftinginterpreters.lox;")
            writer.println()
            writer.println("import java.util.List;")
            writer.println()
            writer.println("abstract public class $baseName {")

            defineVisitors(writer, baseName, types)

            // The AST classes.
            types.map { type ->
                val className = type.split(":")[0].trim()
                val fields = type.split(":")[1].trim()
                defineType(writer, baseName, className, fields)
            }

            // The base accept() method.
            writer.println()
            writer.println("  abstract <R> R accept(Visitor<R> visitor);")

            writer.println("}")

            writer.close()
        }

        private fun defineVisitors(writer: PrintWriter, baseName: String, types: List<String>) {
            writer.println("  interface Visitor<R> {")

            types.map { type ->
                val typeName = type.split(":")[0].trim()
                writer.println("    R visit$typeName$baseName($typeName ${baseName.lowercase()});")
            }

            writer.println("  }")
        }

        private fun defineType(writer: PrintWriter, baseName: String, className: String, fieldList: String) {
            writer.println("  static public class $className extends $baseName {")

            // Constructor.
            writer.println("    $className($fieldList) {")

            // Store parameters in fields.
            val fields = fieldList.split(", ")
            fields.map { field ->
                val name = field.split(" ")[1]
                writer.println("      this.$name = $name;")
            }

            writer.println("    }")

            // Visitor pattern.
            writer.println()
            writer.println("    @Override")
            writer.println("    <R> R accept(Visitor<R> visitor) {")
            writer.println("      return visitor.visit$className$baseName(this);")
            writer.println("    }")

            // Fields.
            writer.println()
            fields.map { field ->
                writer.println("    final $field;")
            }

            writer.println("  }")
        }
    }
}

fun main(args: Array<String>) {
    GenerateAst().main(args)
}
