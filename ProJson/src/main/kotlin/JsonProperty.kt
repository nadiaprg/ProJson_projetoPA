package org.example

/**
 * Permite costumizar os nomes identificadores das propriedades
 *
 * @property name é a variável que guarda o nome da propridade
 */
@Target(AnnotationTarget.PROPERTY)
annotation class JsonProperty(val name: String)
