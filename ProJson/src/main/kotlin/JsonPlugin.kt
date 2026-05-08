package org.example

interface JsonPlugin {
    fun transform(obj: Any): String
}