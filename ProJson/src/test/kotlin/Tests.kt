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
    @Test
    fun criarJsonObject(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        val jsonClass = json::class.simpleName

        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026\n}", json.toString(), "Nao esta a criar um JsonObject corretamente")
        assertEquals("JsonObject", jsonClass, "Criou uma instancia da classe $jsonClass e nao JsonObject")
        assertEquals("Date", json.getType(), "Tipo tinha de ser Date e nao ${json.getType()}")
    }
    @Test
    fun criarJsonObjectComMap(){
        val d = Date(31, 4, 2026)
        val map = mapOf("K1" to "V1", "K2" to null, "K3" to d)
        val json = ProJson().toJson(map) as JsonObject
        val jsonClass = json::class.simpleName

        assertEquals("{\nK1: \"V1\",\nK2: null,\nK3: {\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026\n}\n}", json.toString(), "Nao esta a criar um JsonObject corretamente")
        assertEquals("JsonObject", jsonClass, "Criou uma instancia da classe $jsonClass e nao JsonObject")
        assertNull(json.getType(), "Tipo de um Mapa tem de ser null")
    }

    @Test
    fun adicionarPropriedadePrimitiva(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("jaPassou", true)

        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026,\njaPassou: true\n}", json.toString(), "Nao adicionou a propriedade corretamente")
    }

    @Test
    fun adicionarPropriedadeArray(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("tarefas", listOf("tarefa1", "tarefa2"))

        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026,\ntarefas: [\"tarefa1\",\"tarefa2\"]\n}", json.toString(), "Nao adicionou a propriedade corretamente")
    }

    @Test
    fun adicionarPropriedadeArrayMapa(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("alturaDoDia", mapOf("madrugada" to "00 as 6", "manha" to "6 ao 12", "tarde" to "12 as 18", "noite" to "18 a 00"))
        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026,\nalturaDoDia: {\nmadrugada: \"00 as 6\",\nmanha: \"6 ao 12\",\ntarde: \"12 as 18\",\nnoite: \"18 a 00\"\n}\n}", json.toString(), "Nao adicionou a propriedade corretamente")
    }

    @Test
    fun removerPropriedadeQueExiste(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.removeProperty("day")

        assertEquals("{\n\$type: \"Date\",\nmonth: 4,\nyear: 2026\n}", json.toString(), "Nao removeu a propriedade")
    }

    @Test
    fun removerPropriedadeQueNaoExiste(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.removeProperty("jaPassou")

        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 4,\nyear: 2026\n}", json.toString(), "Nao conseguiu lidar com um user tentar remover uma propriedade que nao existe")
    }

    @Test
    fun alterarPropriedade(){
        val d = Date(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("month", 8)

        assertEquals("{\n\$type: \"Date\",\nday: 31,\nmonth: 8,\nyear: 2026\n}", json.toString(), "Nao conseguiu alterar a propriedade")
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