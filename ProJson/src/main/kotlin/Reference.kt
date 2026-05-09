package org.example

/**
 * Classe de anotação utilizada para indicar que uma propriedade deve ser serializada como uma referência JSON
 *
 * Em vez de serializar o objeto completo como um objeto JSON, a propriedade anotada
 * manda o gerador a utilizar um identificador UUID que aponta para o objeto real
 */

@Target(AnnotationTarget.PROPERTY)
annotation class Reference()
