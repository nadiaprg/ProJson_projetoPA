package org.example

/**
 * Representa a classe base abstrata para todos os tipos de valores JSON.
 *
 * Garante que todos os tipos possíveis de valores JSON
 * são conhecidos na compilação e restritos a esta hierarquia.
 */
sealed class JsonValue {

    /**
     * Retorna a representação textual deste valor JSON.
     *
     * @return Uma [String] contendo o valor formatado de acordo com a sintaxe padrão JSON.
     */
    abstract override fun toString(): String
}