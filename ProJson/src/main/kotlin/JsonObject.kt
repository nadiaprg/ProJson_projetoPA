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
    fun getPropriedade(propriedade: String): Any? {
        return propriedades[propriedade]
    }

    // altera o valor de uma propriedade
    // caso a propriedade nao exista cria e associa o valor
    fun setProperty(propriedade: String, valor: Any? = null){
        // transforma o valor num JsonValue
        val valorJson = ProJson().toJson(valor)
        // modifica a propriedade
        propriedades[propriedade] = valorJson
    }

    // adicionar propriedade (não sei se vale apenas)
    fun addProperty(propriedade: String){
        propriedades[propriedade] = null
    }

    // eleminar propriedade
    fun removeProperty(propriedade: String){
        propriedades.remove(propriedade)
    }

    override fun toString(): String {
        if (tipo.isNullOrEmpty())
            return propriedades.toList().joinToString(",\n", "{\n", "\n}") {
                "${it.component1()}: ${it.component2()}"
            }

        return "{\n\$type: \"$tipo\",\n" + propriedades.toList().joinToString(",\n", postfix = "\n}") {
            "${it.component1()}: ${it.component2()}"
        }
    }

}