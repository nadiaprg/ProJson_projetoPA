package org.example

class JsonPrimitive(
    private var valor: Any?
) : JsonValue() {
    override fun toString(): String {
        TODO("Not yet implemented")
    }

    init {
        require(valor == null || valor is String || valor is Boolean || valor is Number){
            "Tipo não suportado pelo JSON: ${valor?.javaClass?.simpleName}"
        }
    }


}