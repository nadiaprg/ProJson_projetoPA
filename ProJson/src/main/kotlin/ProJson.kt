package org.example

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.createInstance
import java.util.UUID;


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

                // verifica se a classe tem a anotacao JsonString
                if (clazz.hasAnnotation<JsonString>()){
                    // se a classe de serializacao existir, cria uma instancia da mesma
                    val plugin = clazz.findAnnotation<JsonString>()?.plugin?.createInstance()

                    if (plugin != null){
                        val objString = (plugin as JsonPlugin).transform(objet)

                        return JsonPrimitive(objString)
                    }

                }
                val jo = createJsonObject(objet)

                if (!clazz.isData){
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
        var id = UUID.randomUUID().toString()
        while (IDs.containsValue(id)){
            id = UUID.randomUUID().toString()
        }
        return id
    }

}