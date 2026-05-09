package org.example

import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.createInstance
import java.util.UUID;

/**
 * Classe principal responsável por gerar a estrutura JSON a partir de objetos Kotlin
 *
 * Converte instâncias de objetos em memória para a hierarquia de [JsonValue],
 * gerindo de forma transparente as referências entre objetos através da geração e rastreamento de UUIDs
 *
 * Suporta anotações de personalização como [JsonIgnore], [JsonProperty], [Reference] e [JsonString].
 */

class ProJson {

    /**
     * Mapa que liga os objetos instaciados aos seus respetivos UUIDs de forma a garantir que os objetos que já
     * foram processados usem o mesmo identificador e para não serem criados identificadores iguais
     */
    private var IDs = mutableMapOf<Any, String>()

    /**
     * Converte um objeto Kotlin arbitrário na sua representação [JsonValue] correspondente
     *
     * - Valores nulos, [String], [Number] e [Boolean] são convertidos em [JsonPrimitive]
     * - [Collection] são convertidas em [JsonArray]
     * - Mapas e objetos complexos são convertidos em [JsonObject]
     * - Se a classe possuir a anotação [JsonString] delega a transformação para o plugin
     *
     * @param objet é o objeto em memória que vai ser convertido
     * @return de uma instancia de [JsonValue] que corresponde a um [JsonPrimitive], [JsonObject] ou [JsonArray]
     */
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

    /**
     * Converte uma coleção numa lista de instâncias [JsonValue]
     *
     * @param objeto coleção a ser iterada
     * @return [MutableList] de [JsonValue] resultante da conversão de cada elemento
     */
    private fun createJsonArray(objeto: Collection<*>): MutableList<JsonValue?> {
        var array = mutableListOf<JsonValue?>()
        objeto.forEach {
            o -> array.add(toJson(o))
        }
        return array
    }

    /**
     * Converte um objeto complexo num mapa de propriedades do tipo [JsonValue], utilizando a reflexão
     *
     * Processa dinamicamente as anotações [JsonIgnore] para omitir propriedades, [Reference] para lidar
     * com dependências e [JsonProperty] para costumizar os nomes das chaves
     *
     * @param objeto instancia do objeto cujas propriedades vão ser iteradas
     * @return [MutableMap] cuja chave é uma [String] do nome da propriedade e o valor é o seu [JsonValue] serializado
     */
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

    /**
     * Converte um [Map] num mapa de propriedades compativel com [JsonObject]
     * @param objeto mapa original
     * @return [MutableMap] com chaves [String] e valores [JsonValue]
     */
    private fun createJsonObjectMap(objeto: Any): MutableMap<String, JsonValue?> {
        var mapa = mutableMapOf<String, JsonValue?>()
        val listaMapa = (objeto as Map<*,*>).toList()

        listaMapa.forEach {
            mapa[it.component1().toString()] = toJson(it.component2())
        }

        return mapa
    }

    /**
     * Processa uma coleção de objetos, e se estes ainda não possuírem um UUID gerado ele cria, caso contratrio
     * recupera o identificador existente
     *
     * @param list coleção de objetos referencia a processar
     * @return uma lista de [String] com os UUIDs correspondentes aos objetos
     */
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

    /**
     * Cria um UUIDs de um [JsonObject] que ainda não exista
     *
     * @return uma [String] do UUID gerado
     */
    private fun createID(): String{
        var id = UUID.randomUUID().toString()
        while (IDs.containsValue(id)){
            id = UUID.randomUUID().toString()
        }
        return id
    }

}