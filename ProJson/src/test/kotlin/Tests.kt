import org.example.JsonArray
import org.example.JsonObject
import org.example.JsonPrimitive
import org.example.JsonProperty
import org.example.JsonIgnore
import org.example.JsonPlugin
import org.example.JsonString
import org.example.JsonValue
import org.example.ProJson
import org.example.Reference
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Collections.emptyList
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

    data class DateAnotacoes(
        @JsonProperty("dia")
        val day: Int,
        @JsonProperty("mes")
        val month: Int,
        @JsonProperty("ano")
        val year: Int
    )

    @JsonString(DateAsText::class)
    data class DateJsonString(
        val day: Int,
        val month: Int,
        val year: Int
    )

    class TaskAnotacoes(
        @JsonProperty("desc")
        val description: String,
        @JsonIgnore
        val deadline: Date?,
        @Reference
        val dependencies: List<TaskAnotacoes>
    )

    class DateAsText : JsonPlugin {
        override fun transform(obj: Any): String {
            // 1. Verificação de segurança: garante que o objeto é uma Date
            // (No teu projeto terás de importar a classe Date correta)
            if (obj !is DateJsonString) return obj.toString()

            // 2. Formatação da String (ex: adiciona zeros à esquerda para o dia/mês)
            val diaFormatado = obj.day.toString().padStart(2, '0')
            val mesFormatado = obj.month.toString().padStart(2, '0')

            // 3. Devolve exatamente o formato pedido na imagem: "30/02/2026"
            return "$diaFormatado/$mesFormatado/${obj.year}"
        }
    }

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

    // Testes da anotacao JsonProperty
    @Test
    fun criarJsonObjectJsonProperty(){
        val d = DateAnotacoes(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject
        val jsonClass = json::class.simpleName

        assertEquals("{\n\$type: \"DateAnotacoes\",\ndia: 31,\nmes: 4,\nano: 2026\n}", json.toString(), "Nao esta a criar um JsonObject corretamente")
        assertEquals("JsonObject", jsonClass, "Criou uma instancia da classe $jsonClass e nao JsonObject")
        assertEquals("DateAnotacoes", json.getType(), "Tipo tinha de ser Date e nao ${json.getType()}")
    }

    @Test
    fun alterarPropriedadeJsonProperty(){
        val d = DateAnotacoes(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("mes", 8)

        assertEquals("{\n\$type: \"DateAnotacoes\",\ndia: 31,\nmes: 8,\nano: 2026\n}", json.toString(), "Nao conseguiu alterar a propriedade")
    }

    @Test
    fun adicionarPropriedadeArrayJsonProperty() {
        val d = DateAnotacoes(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonObject

        json.setProperty("datas", listOf(DateAnotacoes(1, 5, 2026), Date(2, 5, 2026)))

        assertEquals(
            "{\n\$type: \"DateAnotacoes\",\ndia: 31,\nmes: 4,\nano: 2026,\ndatas: [{\n\$type: \"DateAnotacoes\",\ndia: 1,\nmes: 5,\nano: 2026\n},{\n\$type: \"Date\",\nday: 2,\nmonth: 5,\nyear: 2026\n}]\n}",
            json.toString(),
            "Nao adicionou a propriedade corretamente"
        )
    }

    //Testes da anotação JsonIgnore
    @Test
    fun testarJsonIgnore() {
        val t = TaskAnotacoes("T1", Date(30,2,2026), emptyList())
        val json = ProJson().toJson(t) as JsonObject

        val esperado = JsonObject(
            mapOf(
                "deps" to JsonArray(emptyList()),
                "desc" to JsonPrimitive("T1")
            ) as MutableMap<String, JsonValue>,

            "TaskAnotacoes", // Tipo da classe principal
        )

        assertEquals(
            esperado,
            json
        )
    }

    // Testes da anotacao Reference
    @Test
    fun testarReferenceComObjetosPorCriar() {
        val t1 = TaskAnotacoes("T1"
            , Date(30,2,2026), emptyList())
        val t2 = TaskAnotacoes("T2"
            , Date(31,4,2026), emptyList())
        val t3 = TaskAnotacoes("T3"
            , Date(30,2,2026), listOf(t1, t2))

        val motor = ProJson()

        val json = motor.toJson(t3) as JsonObject

        print("ID: " + json.getID())

        // Verifica se criou o ID da task 3
        assertNotNull(json.getID(), "O objeto principal deve ter um ID")
        assertTrue(json.getID()?.length == 36, "O ID deve ter tamanho 36 e nao ${json.getID()?.length}")

        // Verifica se criou corretamente a lista de dependencias
        val deps = json.getProperties()["dependencies"] as JsonArray
        assertEquals(2, deps.getList().size, "A lista de dependências deve ter 2 elementos")

        // Verifica se as referências na lista não são nulas e têm o formato certo
        deps.getList().forEach { ref ->
            val refObj = ref as JsonObject

            val refPrimitive = refObj.getProperty("\$ref") as JsonPrimitive
            assertNotNull(refPrimitive, "A referência não pode ser nula")

            val refId = refPrimitive.getValue() as String
            assertTrue(refId.length == 36, "O ID deve ter tamanho 36 e nao ${refId.length}")

            val task = motor.getObject(refId) as TaskAnotacoes

            print("\nTarefa associada ao id: $refId -> ${task.description}")

            assertNotNull(task, "Não existe uma task associada à referencia")
        }

    }

    @Test
    fun testarReferenceComObjetosJaCriados() {
        val motor = ProJson()

        val t1 = TaskAnotacoes("T1"
            , Date(30,2,2026), emptyList())

        val json_t1 = motor.toJson(t1) as JsonObject
        val id_t1 = json_t1.getID()

        val t2 = TaskAnotacoes("T2"
            , Date(31,4,2026), emptyList())

        val json_t2 = motor.toJson(t2) as JsonObject
        val id_t2 = json_t2.getID()

        val t3 = TaskAnotacoes("T3"
            , Date(30,2,2026), listOf(t1, t2))

        val json_t3 = motor.toJson(t3) as JsonObject
        val id_t3 = json_t3.getID()

        print("ID T1: $id_t1")
        print("\nID T2: $id_t2")
        print("\nnID T3: $id_t3")

        // Verifica se criou o ID da task 3
        assertNotNull(id_t3, "O objeto principal deve ter um ID")
        assertTrue(id_t3?.length == 36, "O ID deve ter tamanho 36 e nao ${id_t3?.length}")

        // Verifica se criou corretamente a lista de dependencias
        val deps = json_t3.getProperties()["dependencies"] as JsonArray
        assertEquals(2, deps.getList().size, "A lista de dependências deve ter 2 elementos")

        // Verifica se as referências na lista não são nulas e têm o formato certo
        deps.getList().forEach { ref ->
            val refObj = ref as JsonObject

            val refPrimitive = refObj.getProperty("\$ref") as JsonPrimitive
            assertNotNull(refPrimitive, "A referência não pode ser nula")

            val refId = refPrimitive.getValue() as String
            assertTrue(refId.length == 36, "O ID deve ter tamanho 36 e nao ${refId.length}")

            assertTrue(refId == id_t1 || refId == id_t2, "O id da tarefa esta errado")

            val task = motor.getObject(refId) as TaskAnotacoes

            print("\nTarefa associada ao id: $refId -> ${task.description}")

            assertNotNull(task, "Não existe uma task associada à referencia")
        }

    }

    // Testes da anotacao JsonString

    @Test
    fun testeJsonString(){
        val d = DateJsonString(31, 4, 2026)
        val json = ProJson().toJson(d) as JsonPrimitive

        assertEquals("\"31/04/2026\"", json.toString(), "não criou corretamente a string, ${json.toString()}")
    }
}