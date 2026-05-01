package org.example

import kotlin.reflect.KClass


/**
 * Representa o valor primitivo do modelo Json
 *
 * Esta classe encapsula valores simples como numeros, strings, booleanos e nulos
 * Garante, na inicialização, que apenas tipos compativeis com a especificação Json sejam armazenados
 */

class JsonPrimitive(
    private var valor: Any?
) : JsonValue() {

    /**
     * Valida se o [valor] fornecido é compativél com os tipos de Json primitivos
     *
     * @throws IllegalArgumentException caso o tipo do [valor] não seja [String], [Boolean], [Number] ou nulo
     */
    init {
        require(valor == null || valor is String || valor is Boolean || valor is Number){
            "Tipo não suportado pelo JSON: ${valor?.javaClass?.simpleName}"
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
        return when(valor){
            is String -> "\"$valor\""
            is Boolean, is Number -> "$valor"
            else -> "null"
        }
    }

}