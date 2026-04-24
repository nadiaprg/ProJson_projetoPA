package org.example

class JsonObject(
    private var propriedades: MutableMap<String, JsonValue?>,
    private val tipo: String? = null
) : JsonValue() {

    // getter da variavel tipo
    fun getType(): String? {
        return tipo
    }

    // getter do mapa propriedades
    fun getPropriedades(): MutableMap<String, JsonValue?>{
        return propriedades
    }

    // vai buscar 1 propriedade ao mapa
    // TODO pensar em como tirar o null
    fun getPropriedade(propriedade: String): Any? {
        return propriedades.get(propriedade)
    }

    // altera o valor de uma propriedade
    // tenho de transformar valor em JsonValue
    fun setProperty(propriedade: String, valor: Any){
        // TODO
        // procurar no mapa a chave
        // se existir, verificar se o tipo do valor corresponde ao tipo atual(?) ou verificar atraves de reflexao
        // se estiver tudo bem, alterar valor
    }

    // adicionar propriedade(?)
    // tenho de transformar valor em JsonValue
    fun addProperty(propriedade: String, valor: JsonValue){
        propriedades.put(propriedade, valor)
    }

    // eleminar propriedade(?)
    fun removeProperty(propriedade: String){
        propriedades.remove(propriedade)
    }

    override fun toString(): String {
        TODO("Not yet implemented")
    }

}