package org.example

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties


class ProJson {

    private var IDs = mutableMapOf<Any, String>()

    fun toJson(objet: Any?): JsonValue {
        return when(objet){
            null -> JsonPrimitive(null)
            is Collection<*> -> {
                val ja = createJsonArray(objet)
                JsonArray(ja)
            }

            is Map<*, *> -> {
                val jo = createJsonObjectMap(objet)
                JsonObject( jo)
            }

            is String, is Number, is Boolean -> JsonPrimitive(objet)
            else -> {
                val clazz = objet::class

                if (clazz.hasAnnotation<JsonString>()){
                    val instance = clazz.findAnnotation<JsonString>()?.clazz
                }
                val jo = createJsonObject(objet)

                if (hasReference(objet)){
                    val id = createID()
                    IDs[objet] = id
                    JsonObject(jo, clazz.simpleName, id)
                }
                else{
                    JsonObject(jo, clazz.simpleName)
                }
            }
        }
    }

    private fun createJsonArray(objeto: Collection<*>): MutableList<JsonValue?> {
        var array = mutableListOf<JsonValue?>()
        objeto.forEach {
            o -> array.add(toJson(o))
        }
        return array
    }

    private fun createJsonObject(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>()
        val clazz = objeto::class

        // Percorremos cada propriedade da classe
        val property = clazz.memberProperties.forEach {
            // verifica se a propriedade tem a anotação JsonIgnore
            if (it.hasAnnotation<JsonIgnore>()) return@forEach

            var valorOriginal = it.call(objeto)

            if (it.hasAnnotation<Reference>()){
                valorOriginal = createReferences(valorOriginal as Collection<Any>)
            }

            // verifica se a propriedade tem a anotacao JsonProperty
            if (it.hasAnnotation<JsonProperty>()){
                // se tiver, o nome da propriedade vai ser o nome dado na anotacao
                val name = it.findAnnotation<JsonProperty>()?.name ?: ""
                // Chamada recursiva para converter em JsonValue
                mapa[name] = toJson(valorOriginal)
            }
            else{
                // Chamada recursiva para converter em JsonValue
                mapa[it.name] = toJson(valorOriginal)
            }
        }
        return mapa
    }

    private fun createJsonObjectMap(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>()
        val listaMapa = (objeto as Map<*,*>).toList()

        listaMapa.forEach {
            mapa[it.component1().toString()] = toJson(it.component2())
        }

        return mapa
    }

    // verifica se a classe do objeto tem a anotacao Reference
    private fun hasReference(objet: Any): Boolean{
        val clazz = objet::class

        clazz.memberProperties.forEach {
            if (it.hasAnnotation<Reference>())
                return true
        }

        return false
    }

    // cria a lista com as referencias
    // caso os objetos ainda nao tenham sido criado, ele cria primeiro
    private fun createReferences(list: Collection<Any>): List<String>{
        val listIDs = mutableListOf<String>()
        list.forEach {
            if (!IDs.containsKey(it)){
                toJson(it)
            }
            listIDs.add(IDs.getValue(it))
        }
        return listIDs
    }

    // cria UUID de um JsonObject
    private fun createID(): String{
        var id = ""
        while (IDs.containsValue(id) || id == ""){
            id = randomSequence(8) + "-" +
                    randomSequence(4) + "-" +
                    randomSequence(4) + "-" +
                    randomSequence(4) + "-" +
                    randomSequence(12)
        }
        return id
    }

    // cria sequencia de chars
    private fun randomSequence(length: Int): String {
        val chars = ('a'..'z') + ('0'..'9')
        val lista = mutableListOf<Char>()

        for (i in (1..length)){
            lista.add(chars.random())
        }

        return lista.joinToString("")
    }

}