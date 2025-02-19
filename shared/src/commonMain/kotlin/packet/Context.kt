package packet

import io.github.nomisrev.JsonPath
import io.github.nomisrev.path
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class Context {
    val variables = mutableMapOf<String, JsonElement>()
    val responses = mutableMapOf<String, JsonObject>()

    fun resolve(expression: String): JsonElement {
        val regex = """\$\{(?:([^.]+)\.)?([^}]+)}""".toRegex()
        return regex.matchEntire(expression)?.let { match ->
            val (scope, path) = match.destructured
            when {
                scope.isEmpty() -> variables[path]
                else -> responses[scope]?.let { JsonPath.path(path).getOrNull(it) }
            } ?: JsonPrimitive("__MISSING_${expression}__")
        } ?: JsonPrimitive(expression)
    }
}

fun String.ext(prefix: CharSequence = "\${", suffix: CharSequence = "}") = "$prefix$this$suffix"