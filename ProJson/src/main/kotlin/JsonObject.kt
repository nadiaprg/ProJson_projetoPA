package org.example

/**
 * Representa um mapa que associa uma String a um [JsonValue]
 *
 * Premite a manipulação dinâmica de elementos, incluindo adicionar novas propriedade,
 * remover ou modificar propriedades já existentes e consultar valores
 *
 * Extende a classe abstrata [JsonValue]
 *
 * @property properties é o mapa mutável interno que guarda os valores da classe
 * @property type é a variável que guarda o nome da classe original do objeto
 */
data class JsonObject(
    private var properties: MutableMap<String, JsonValue>,
    private val type: String? = null,
    private val id: String? = null
) : JsonValue() {

    /**
     * Retorna o valor da variável tipo
     *
     * @return a String correspondente ao valor da variável tipo, sendo que pode ser null
     */
    fun getType(): String? {
        return type
    }

    /**
     * Retorna a variável propriedades
     *
     * @return o mapa associado à variável propriedades
     */
    fun getProperties(): MutableMap<String, JsonValue>{
        return properties
    }

    /**
     * Retorna o valor da variável id
     *
     * @return a String correspondente ao valor da variável id, sendo que pode ser null
     */
    fun getID(): String?{
        return id
    }

    /**
     * Retorna o valor associado a uma chave
     *
     * @param property é a string correspondente à chave do valor a devolver
     * @return o [JsonValue] correspondente à chave ou null se o elemento for nulo
     */
    fun getProperty(property: String): Any? {
        return properties[property]
    }

    /**
     * Modifica ou cria uma propriedade da variável propriedades
     *
     * Se a propriedade já existir, atualiza o valor da mesma
     * Se a propriedade não exitir, cria a mesma e associa o valor dado
     *
     * @param property é a string correspondente à chave
     * @param valor é o valor a associar à chave
     */
    fun setProperty(property: String, valor: Any? = null){
        // transforma o valor num JsonValue
        val valorJson = ProJson().toJson(valor)
        // modifica a propriedade
        properties[property] = valorJson
    }

    /**
     * Remove uma propriedade e o valor associado à mesma do mapa
     *
     * @param property é a string correspondente à chave
     */
    fun removeProperty(property: String){
        properties.remove(property)
    }

    /**
     * Serializa o mapa para o formato textual do Json padrão
     *
     * @return uma [String] formatada representando um [JsonObject]
     */
    override fun toString(): String {
        if (type.isNullOrEmpty())
            return properties.toList().joinToString(",\n", "{\n", "\n}") {
                "${it.component1()}: ${it.component2()}"
            }
        else if (id.isNullOrEmpty())
            return "{\n\$id: \"$id\",\n\$type: \"$type\",\n" + properties.toList().joinToString(",\n", postfix = "\n}") {
                "${it.component1()}: ${it.component2()}"
            }
        return "{\n\$type: \"$type\",\n" + properties.toList().joinToString(",\n", postfix = "\n}") {
            "${it.component1()}: ${it.component2()}"
        }
    }

}