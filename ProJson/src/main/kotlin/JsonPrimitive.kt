package org.example

import kotlin.reflect.KClass

class JsonPrimitive(
    private var valor: Any?
) : JsonValue() {

    init {
        require(valor == null || valor is String || valor is Boolean || valor is Number){
            "Tipo não suportado pelo JSON: ${valor?.javaClass?.simpleName}"
        }
    }


    override fun toString(): String {
        return when(valor){
            is String -> "\"$valor\""
            is Boolean, is Number -> "$valor"
            else -> "null"
        }
    }

}