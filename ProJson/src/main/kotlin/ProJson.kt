package org.example

import kotlin.reflect.KClass


class ProJson {
    fun toJson(objeto: Any): JsonValue {
        return when(objeto){
            null -> JsonPrimitive(null)
            is Collection<*> -> {
                createJsonArray(objeto)
            }
            is Map<*, *> -> {
                createJsonObject(objeto)
            }
            is String, Number, Boolean -> JsonPrimitive(objeto)
        }else -> {
            createJsonObject(objeto)
        }
        //TODO
        // atraves de reflection temos de verificar a classe do objeto
        // e chamar JsonObject e JsonArray respetivamente
    }

    private fun createJsonArray(objeto: Collection): MutableList {
        var array = mutableListOf()
        objeto.forEach {
            array.add(toJson(it))
        }
        return array
    }

    private fun createJsonObject(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>
        val clazz = objeto::class
        clazz.primaryConstructor?.parameters?.forEach {
            mapa[it.toString()] = toJson(it.call(objeto))
        }
        return mapa
    }




}