package org.proJson

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
                        val objString = plugin.transform(objet)

                        return JsonPrimitive(objString)
                    }

                }
                val jo = createJsonObject(objet)

                if (!clazz.isData){
                    if (IDs.containsKey(objet))
                        JsonObject(jo, clazz.simpleName, IDs[objet])
                    else {
                        val id = createID()
                        IDs[objet] = id
                        JsonObject(jo, clazz.simpleName, id)
                    }
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
     * @param objet coleção a ser iterada
     * @return [MutableList] de [JsonValue] resultante da conversão de cada elemento
     */
    private fun createJsonArray(objet: Collection<*>): MutableList<JsonValue?> {
        var array = mutableListOf<JsonValue?>()
        objet.forEach {
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
     * @param objet instancia do objeto cujas propriedades vão ser iteradas
     * @return [MutableMap] cuja chave é uma [String] do nome da propriedade e o valor é o seu [JsonValue] serializado
     */
    private fun createJsonObject(objet: Any): MutableMap<String, JsonValue> {
        var map = mutableMapOf<String, JsonValue>()
        val clazz = objet::class

        // Percorremos cada propriedade da classe
        clazz.memberProperties.forEach {
            // verifica se a propriedade tem a anotação JsonIgnore
            if (it.hasAnnotation<JsonIgnore>()) return@forEach

            var originalValue = it.call(objet)

            if (it.hasAnnotation<Reference>()){
                originalValue = createReferences(originalValue as Collection<Any>)
            }

            // verifica se a propriedade tem a anotacao JsonProperty
            if (it.hasAnnotation<JsonProperty>()){
                // se tiver, o nome da propriedade vai ser o nome dado na anotacao
                val name = it.findAnnotation<JsonProperty>()?.name ?: ""
                // Chamada recursiva para converter em JsonValue
                map[name] = toJson(originalValue)
            }
            else{
                // Chamada recursiva para converter em JsonValue
                map[it.name] = toJson(originalValue)
            }
        }
        return map
    }

    /**
     * Converte um [Map] num mapa de propriedades compativel com [JsonObject]
     * @param objet mapa original
     * @return [MutableMap] com chaves [String] e valores [JsonValue]
     */
    private fun createJsonObjectMap(objet: Map<*, *>): MutableMap<String, JsonValue> {
        var map = mutableMapOf<String, JsonValue>()
        val mapList = objet.toList()

        mapList.forEach {
            map[it.component1().toString()] = toJson(it.component2())
        }

        return map
    }

    /**
     * Processa uma coleção de objetos, e se estes ainda não possuírem um UUID gerado ele cria, caso contratrio
     * recupera o identificador existente
     *
     * @param list coleção de objetos referencia a processar
     * @return uma lista de [String] com [MutableMap] com os UUIDs correspondentes aos objetos, onde as chaves e valores [String]
     */
    private fun createReferences(list: Collection<Any>): List<Map<String, String>>{
        val listIDs = mutableListOf<Map<String, String>>()
        list.forEach {
            // verifica se o objeto ja tinha sido criado
            if (!IDs.containsKey(it)){
                // se nao, cria o objeto
                toJson(it)
            }

            // vai buscar o id
            val id = IDs.getValue(it)

            // faz da referencia um mapa para
            listIDs.add(mapOf("\$ref" to id))
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

    /**
     * Devolve o objeto associado ao id dado
     *
     * @return o objeto; se o id não existir, devolve null
     */
    fun getObject(id: String): Any? {
        //return IDs.filter { key -> IDs[key] == id }.keys
        return IDs.entries.find { it.value == id }?.key
    }

}