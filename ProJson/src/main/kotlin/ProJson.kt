package org.example

import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor


class ProJson {
    fun toJson(objeto: Any?): JsonValue {
        return when(objeto){
            null -> JsonPrimitive(null)
            is Collection<*> -> {
                val ja = createJsonArray(objeto)
                JsonArray(ja)
            }

            is Map<*, *> -> {
                val jo = createJsonObjectMap(objeto)
                JsonObject( jo)
            }

            is String, is Number, is Boolean -> JsonPrimitive(objeto)
            else -> {
                val clazz = objeto::class
                val jo = createJsonObject(objeto)
                JsonObject(jo, clazz.simpleName)
            }
        }
    }

    private fun createJsonArray(objeto: Collection<*>): MutableList<JsonValue?> {
        var array = mutableListOf<JsonValue?>()
        objeto.forEach {
            o -> array.add(toJson(o))
        }
        return array
    }

    private fun createJsonObject(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>()
        val clazz = objeto::class

            clazz.primaryConstructor?.parameters?.forEach {
                val property_name = it.name

                if (property_name != null) {
                    // Procuramos a propriedade correspondente para extrair o valor
                    val property = clazz.memberProperties.find { it.name == property_name }
                    val valorOriginal = property?.call(objeto)

                    // Chamada recursiva para converter em JsonValue
                    mapa[property_name] = toJson(valorOriginal)
                }
            }

        return mapa
    }

    private fun createJsonObjectMap(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>()
        val listaMapa = (objeto as Map<*,*>).toList()

        listaMapa.forEach {
            mapa[it.component1().toString()] = toJson(it.component2())
        }

        return mapa
    }


}