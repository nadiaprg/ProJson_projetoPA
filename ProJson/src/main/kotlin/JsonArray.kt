package org.example

class JsonArray(
    private var lista: MutableList<JsonValue?>
) : JsonValue() {

    //adicionar JsonValue à lista
    fun add(value: JsonValue) {
        lista.add(value)
    }

    //Remover um Jsonvalue da lista
    fun remove(index: Int) {
        if (index >= 0 && index < size()) lista.removeAt(index)
    }

    //ir buscar um certo JsonValue à lista
    fun get(index: Int): JsonValue?{
        return lista[index]
    }

    //tamanho da lista
    fun size(): Int {
        return lista.size
    }

    override fun toString(): String {
        return lista.joinToString(",", "[", "]"){
            it?.toString() ?: "null"
        }
    }


}