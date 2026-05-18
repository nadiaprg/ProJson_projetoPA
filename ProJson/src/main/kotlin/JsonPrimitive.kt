package org.proJson


/**
 * Representa o valor primitivo do modelo Json
 *
 * Esta classe encapsula valores simples como numeros, strings, booleanos e nulos
 * Garante, na inicialização, que apenas tipos compativeis com a especificação Json sejam armazenados
 *
 * @property value valor real armazenado, que deve ser [String], [Number], [Boolean] ou nulo
 */

data class JsonPrimitive(
    private var value: Any?
) : JsonValue() {

    /**
     * Retorna o valor armazenado
     *
     * @return valor [Any], podendo ser nulo
     */
    fun getValue(): Any?{
        return value
    }

    /**
     * Valida se o [value] fornecido é compativél com os tipos de Json primitivos
     *
     * @throws IllegalArgumentException caso o tipo do [value] não seja [String], [Boolean], [Number] ou nulo
     */
    init {
        require(value == null || value is String || value is Boolean || value is Number){
            "Tipo não suportado pelo JSON: ${value?.javaClass?.simpleName}"
        }
    }

    /**
     * Serializa os valores para formato textual Json padrão
     *
     * -As [String] são envolvidas por aspas duplas
     * -Os [Boolean] e os [Number] são convertidos para texto, sem aspas
     * -Os valores nulos são convertidos para a string "null"
     *
     * @return uma [String] formatada de acordo com as regras de serialização JSON
     */
    override fun toString(): String {
        return when(value){
            is String -> "\"$value\""
            is Boolean, is Number -> "$value"
            else -> "null"
        }
    }

}