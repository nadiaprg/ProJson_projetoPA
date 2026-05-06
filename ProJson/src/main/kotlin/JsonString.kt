package org.example

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class JsonString(val clazz: KClass<*>)
