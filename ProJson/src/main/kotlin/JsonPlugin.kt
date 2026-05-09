package org.example

/**
 * Interface que define um mecanismo de plugin para personalização da serialização de objetos
 * Para ser utilizada, uma implementação desta interface deve ser referenciada através da
 * anotação [JsonString] na classe que se pretende formatar
 */

interface JsonPlugin {

    /**
     * Transforma um objeto numa representação customizada em formato de texto
     *
     * @param obj o obejeto instanciado que deve ser trnsformado
     * @return [String] que representa o valor do objeto formatado 
     */
    fun transform(obj: Any): String
}