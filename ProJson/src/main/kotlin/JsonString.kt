package org.example

import kotlin.reflect.KClass

/**
 * Anotação utilizada para definir uma serialização customizada de uma classe para uma string no formato JSON
 *
 * Quando uma classe tem a anotação [JsonString], a classe ProJson ignora a sua serialização padrão como um objeto JSON
 * e utiliza o plugin fornecido para transformar a instância numa representação textual
 *
 * @property plugin é uma [KClass] do plugin responsável pela formatação
 */

@Target(AnnotationTarget.CLASS)
annotation class JsonString(val plugin: KClass<*>)
