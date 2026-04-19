package org.example

class JsonObject(
    private val tipo: String,
    private val propriedades: MutableMap<String, Any?>
) : JsonValue() {

    // getter da variavel tipo
    fun getType(): String {
        return tipo
    }

    // getter do mapa propriedades
    fun getPropriedades(): MutableMap<String, Any?>{
        return propriedades
    }

    // vai buscar 1 propriedade ao mapa
    // TODO pensar em como tirar o null
    fun getPropriedade(propriedade: String): Any? {
        return propriedades.get(propriedade)
    }

    // altera o valor de uma propriedade
    fun setProperty(propriedade: String, valor: Any){
        // TODO
        // procurar no mapa a chave
        // se existir, verificar se o tipo do valor corresponde ao tipo atual(?) ou verificar atraves de reflexao
        // se estiver tudo bem, alterar valor
    }

    // adicionar propriedade(?)
    fun addProperty(propriedade: String, valor: Any){
        propriedades.put(propriedade, valor)
    }

    // eleminar propriedade(?)
    fun removeProperty(propriedade: String){
        propriedades.remove(propriedade)
    }

}