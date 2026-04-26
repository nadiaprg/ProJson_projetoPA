import org.example.JsonArray
import org.example.JsonObject
import org.example.JsonPrimitive
import org.example.ProJson
import org.junit.jupiter.api.Test
import kotlin.collections.listOf
import kotlin.test.assertEquals
import kotlin.test.assertNull

class Tests {

    // classes dadas no enunciado para fazer os testes
    class Task(
        val description: String,
        val deadline: Date?,
        val dependencies: List<Task>
    )
    data class Date(
        val day: Int,
        val month: Int,
        val year: Int
    )

    // JsonObject
    fun criarJsonObject(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        val jsonClass = json::class.toString()

        assertEquals(" COMPLETAR ", json.toString(), "Nao esta a criar um JsonObject corretamente")
        assertEquals("JsonObject", jsonClass, "Criou uma instancia da classe $jsonClass e nao JsonObject")
    }

    fun criarJsonObjectComMap(){
        val map = mapOf("K1" to "V1", "K2" to "V2", "K3" to "V3")
        val json = ProJson().toJson(map) as JsonObject
        val jsonClass = json::class.toString()

        assertEquals(" COMPLETAR ", json.toString(), "Nao esta a criar um JsonObject corretamente")
        assertEquals("JsonObject", jsonClass, "Criou uma instancia da classe $jsonClass e nao JsonObject")
        assertNull(json.getType(), "Tipo de um Mapa tem de ser null")
    }

    // TODO
    fun adicionarPropriedade(){

    }

    fun removerPropriedade(){

    }

    fun alterarPropriedade(){

    }

    //Testes para o JsonPrimitive
    @Test
    fun stringPrimitive(){
        assertEquals("\"a\"", JsonPrimitive("a").toString())
    }

    @Test
    fun booleanPrimitive(){
        assertEquals("true", JsonPrimitive(true).toString())
    }

    @Test
    fun numeroPrimitive(){
        assertEquals("24", JsonPrimitive(24).toString())
    }


    //Testes para o JsonArray
    @Test
    fun criarJsonArray(){
        val list = listOf("a", null, "b")
        val json = ProJson().toJson(list) as JsonArray

        assertEquals("[\"a\",null,\"b\"]", json.toString())
    }

    @Test
    fun addJsonArray(){
        val list = listOf("a", null, "b")
        val json = ProJson().toJson(list) as JsonArray
        json.add(JsonPrimitive("c"))

        assertEquals("[\"a\",null,\"b\",\"c\"]", json.toString())
    }

    @Test
    fun removeJsonArray(){
        val list = listOf("a", null, "b", "c")
        val json = ProJson().toJson(list) as JsonArray
        json.remove(2)

        assertEquals("[\"a\",null,\"c\"]", json.toString())
    }

    @Test
    fun getJsonArray(){
        val list = listOf("a", null, "b", "c")
        val json = ProJson().toJson(list) as JsonArray

        assertEquals("\"b\"", json.get(2).toString())
    }
}