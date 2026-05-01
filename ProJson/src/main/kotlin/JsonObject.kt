package org.example

/**
 * Representa um mapa que associa uma String a um [JsonValue]
 *
 * Premite a manipulação dinâmica de elementos, incluindo adicionar novas propriedade,
 * remover ou modificar propriedades já existentes e consultar valores
 *
 * Extende a classe abstrata [JsonValue]
 *
 * @property propriedades é o mapa mutável interno que guarda os valores da classe
 * @property tipo é a variável que guarda o nome da classe original do objeto
 */
class JsonObject(
    private var propriedades: MutableMap<String, JsonValue?>,
    private val tipo: String? = null
) : JsonValue() {

    /**
     * Retorna o valor da variável tipo
     *
     * @return a String correspondente ao valor da variável tipo, sendo que pode ser null
     */
    fun getType(): String? {
        return tipo
    }

    /**
     * Retorna a variável propriedades
     *
     * @return o mapa associado à variável propriedades
     */
    fun getPropriedades(): MutableMap<String, JsonValue?>{
        return propriedades
    }

    /**
     * Retorna o valor associado a uma chave
     *
     * @param propriedade é a string correspondente à chave do valor a devolver
     * @return o [JsonValue] correspondente à chave ou null se o elemento for nulo
     */
    fun getPropriedade(propriedade: String): Any? {
        return propriedades[propriedade]
    }

    /**
     * Modifica ou cria uma propriedade da variável propriedades
     *
     * Se a propriedade já existir, atualiza o valor da mesma
     * Se a propriedade não exitir, cria a mesma e associa o valor dado
     *
     * @param propriedade é a string correspondente à chave
     * @param valor é o valor a associar à chave
     */
    fun setProperty(propriedade: String, valor: Any? = null){
        // transforma o valor num JsonValue
        val valorJson = ProJson().toJson(valor)
        // modifica a propriedade
        propriedades[propriedade] = valorJson
    }

    /**
     * Remove uma propriedade e o valor associado à mesma do mapa
     *
     * @param propriedade é a string correspondente à chave
     */
    fun removeProperty(propriedade: String){
        propriedades.remove(propriedade)
    }

    /**
     * Serializa o mapa para o formato textual do Json padrão
     *
     * @return uma [String] formatada representando um [JsonObject]
     */
    override fun toString(): String {
        if (tipo.isNullOrEmpty())
            return propriedades.toList().joinToString(",\n", "{\n", "\n}") {
                "${it.component1()}: ${it.component2()}"
            }

        return "{\n\$type: \"$tipo\",\n" + propriedades.toList().joinToString(",\n", postfix = "\n}") {
            "${it.component1()}: ${it.component2()}"
        }
    }

}