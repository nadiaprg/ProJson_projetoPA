package org.example

/**
 * Representa um array Json que funciona como uma lista ordenada de [JsonValue]
 *
 * Premite a manipulação dinâmica de elementos, incluindo adição, remoção e consulta de valores
 * garantindo conformidade com o modelo Json
 *
 * @property lista é a lista mutável interna que armazena os elementos do array
 */

class JsonArray(
    private var lista: MutableList<JsonValue?>
) : JsonValue() {

    /**
     * Adiciona um novo elemento [JsonValue] ao final do array
     *
     * @param value é o valor Json (objeto, array, primitivo ou referencia) a ser inserido no array
     */
    fun add(value: JsonValue) {
        lista.add(value)
    }

    /**
     * Remover o elemento de uma certa posição
     *
     * @param index é o indice do elemento a remover
     */
    fun remove(index: Int) {
        if (index >= 0 && index < size()) lista.removeAt(index)
    }

    /**
     * Retorna o elemento de uma certa posição
     *
     * @param index é o indice do elemento a devolver
     * @return o [JsonValue] correspondente à posição ou null se o elemento for nulo
     */
    fun get(index: Int): JsonValue?{
        return lista[index]
    }

    /**
     * Retorna o numero total de elementos do array
     *
     * @return o tamanho da lista
     */
    fun size(): Int {
        return lista.size
    }


    /**
     * Serializa o array para o formato textual do Json padrão
     *
     * @return uma [String] formatada representando o array Json
     */
    override fun toString(): String {
        return lista.joinToString(",", "[", "]"){
            it?.toString() ?: "null"
        }
    }


}