<h1 align="center"> 
    ProJson_projetoPA 

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-indigo.svg?logo=kotlin)
![Versão](https://img.shields.io/badge/Vers%C3%A3o-1.0.0-green.svg)
</h1>


O **ProJson** é uma biblioteca de serialização 
JSON desenvolvida para Kotlin. Utilizando reflexão, converte instâncias de 
objetos para valores JSON de forma automática. 
Destaca-se por **gerir referências** entre objetos e por 
oferecer um sistema de **plugins para personalização** total.


##    Indice


* [Funcionalidades](#funcionalidades)
* [Pré-Requisitos](#pré-requisitos)
* [Instalação](#instalação)
* [Tutorial](#tutorial)
* [Autores](#autores)


##    Funcionalidades

A biblioteca ProJson foi desenvolvida em Kotlin, 
sem dependências externas, e oferece um conjunto robusto de 
ferramentas para conversão e manipulação de JSON:

- **Geração de JSON Standard:** Converte automáticamente objetos 
Kotlin em memória para uma estrutura JSON e lida com tipos primitivos, 
coleções (transformadas em $JsonArray$) e mapas ou objetos complexos 
(transformados em $JsonObject$).

- **Gestão de Referencias:** Soluciona o problema da serialização de 
estruturas de dados complexas onde os nós se referenciam entre si. 
O gerador atribui automaticamente UUIDs ($\$id$) aos objetos na 
primeira vez que os encontra e, em instâncias futuras, utiliza 
apontadores ($\$ref$).

- **Manipulação Dinâmica em Memória:** Após ser instanciado, 
o modelo JSON pode ser manipulado e percorrido, sendo possível adicionar, 
modificar ou remover elementos tanto em objetos (através de propriedades) 
como em arrays (por índice).

- **Customização via Anotações:**
   * $@JsonIgnore :$ Omite propriedades específicas para que não sejam 
  serializadas na representação textual do JSON.
   * $@JsonProperty(String) :$ Permite personalizar o identificador 
  (chave) de uma propriedade no JSON resultante.
   * $@Reference :$ Instrui o gerador a utilizar uma referência em vez 
  de serializar o objeto ou coleção de forma aninhada.
   * $@JsonString(Plugin::class) :$ Permite definir uma serialização 
  textual customizada para uma classe, recorrendo à implementação 
  da interface JsonPlugin para gerar a representação final do objeto.

- **Geração de Texto JSON Válido:** Converte objetos internos 
(JsonObject, JsonArray, etc.) numa String de texto final, garantindo que 
a formatação respeita as regras do JSON.



##    Pré-requisitos

Para integrar e utilizar a biblioteca ProJson e necessário que o 
ambiente de desenvolvimento cumpra os seguintes requisitos:

- **Kotlin SDK:** A biblioteca foi desenvolvida em Kotlin, 
exigindo assim um ambiente compatível para compilação e execução.
- **Kotlin Reflection:** O mapeamento dinâmico de propriedades e anotações
requer o uso de reflexão, sendo necessário garantir que o pacote 
$kotlin-reflect$ está configurado no ambiente de desenvolvimento.

A biblioteca foi construída de forma totalmente autónoma, não sendo 
necessária a instalação de bibliotecas de terceiros.


##    Instalação

Para a integração da biblioteca ProJson no projeto é necessário:

1. **Download da Release:** Aceder à secção de releases do repositório 
no GitHub e descarregar a versão mais recente do ficheiro $.jar$. 
2. **Importação para o Projeto:** Criar uma pasta chamada $libs$ na raiz 
do projeto e colocar lá o ficheiro $.jar$ descarregado.
3. **Configuração do Gradle:** No ficheiro $build.gradle.kts$, adicionar a 
referência local do $.jar$ e assegurar a inclusão do pacote $kotlin-reflect$:

```
dependencies {
    implementation(files("libs/ProJson-1.0-SNAPSHOT.jar"))
    implementation(kotlin("reflect"))
}
```

4. **Sincronização:** Sincronizar o projeto Gradle para aplicar as alterações.

##    Tutorial

Aqui estão os guias passo-a-passo para tirares o máximo partido do ProJson.

1. **Serialização Básica**
   
    Para converter um objeto normal, basta instanciar o motor ProJson e chamar o método toJson.

    _JsonObject_
    ```
    Kotlin
    
    data class Utilizador(val nome: String, val idade: Int)
    
    fun main() {
    val user = Utilizador("Maria", 25)
    val motor = ProJson()
    
        val json = motor.toJson(user)
        println(json.toString())
    }
    // output esperado: { "$type": "Utilizador", "nome": "Maria", "idade": 25 }
    ```

   _JsonArray_
    ```
    Kotlin
    
    fun main() {
    val user = Utilizador("Maria", 25)
    val lista = ["string", null, user]
    val motor = ProJson()
    
        val json = motor.toJson(lista)
        println(json.toString())
    }
    
    // output esperado: ["string", null, { "$type": "Utilizador", "nome": "Maria", "idade": 25 }]
    ```

   _JsonPrimitive_
    ```
    Kotlin
    
    fun main() {
        val string = "string"
        val motor = ProJson()
    
        val json = motor.toJson(string)
        println(json.toString())
    }
    
    // output esperado: "string"
    ```
   

2. **Omitir Propriedades**

   Usa o @JsonIgnore para esconder dados sensíveis

    ```
    Kotlin
    
    class Produto(
        val id: Int,
        
        val titulo: String,
        
        @JsonIgnore
        val custoProducao: Double
    )
    
    fun main(){
        val produto = Produto(1, "Cadeira", 0.5)
        val motor = ProJson()
    
        val json = motor.toJson(produto)
        print(json.toString())
        
    }
    
    // Output esperado: { "$id": "11fb194e-b75c-4f73-9c10-65df91b81352", $type": "Produto", "id": 1, "titulo": "Cadeira" }
    ```

3. **Renomear Propriedades**

   Usa o @JsonProperty para adaptares o nome da chave no JSON final.

    ```
    Kotlin
   
    class Produto(
        @JsonProperty("identificador")
        val id: Int,
    
        val titulo: String,
    
        val custoProducao: Double
    )
    
    fun main(){
        val produto = Produto(1, "Cadeira", 0.5)
        val motor = ProJson()
        
        val json = motor.toJson(produto)
        print(json.toString()) 
    }
    
    // Output esperado: { "$id": "11fb194e-b75c-4f73-9c10-65df91b81352", "$type": "Produto", "identificador": 1, "titulo": "Cadeira", "custoProducao": 0.5 }
    ```

4. **Referências**

   Se queres fazer uma referência a um JsonObject (que não seja uma Data Class), usa a anotação @Reference. 
   O ProJson vai gerar um UUID para o objeto na primeira vez que o encontrar e, das próximas vezes, vai usar um ponteiro {"$ref": "uuid"}.

    ```
    Kotlin
    
    class Task(
       val descricao: String,
    
       @Reference 
       val dependencias: List<Task>
    )
    
    fun main() {
        val t1 = Task("Comprar cimento", emptyList())
        val t2 = Task("Fazer fundação", listOf(t1))
        val t3 = Task("Levantar paredes", listOf(t1, t2))
    
        val motor = ProJson()
        
        val json_t2 = motor.toJson(t2)
        val json_t3 = motor.toJson(t3)
        println(json_t2.toString())
        println(json_t3.toString())
    }
    
    // Output esperado t2: { "$id": "9e2e6c64-3236-45b7-8b8a-11271c69e4df", "$type": "Task", "descricao": "Comprar cimento", "dependencias": [{ "$ref": "15fb134e-b75c-4f73-9c60-65df91b81352" }] }
    // Output esperado t3: { "$id": "d388f116-826f-4751-bdad-fb8cc152b968", "$type": "Task", "descricao": "Levantar paredes", "dependencias": [{ "$ref": "15fb134e-b75c-4f73-9c60-65df91b81352" }, { "$ref": "9e2e6c64-3236-45b7-8b8a-11271c69e4df" }] }
    ```

5. **Usar Plugins Customizados (@JsonString)**

   Se quiseres que um objeto inteiro seja representado como uma simples String formatada (como uma Data), cria um plugin implementando a interface JsonPlugin.

    
Passo A: Criar a classe do Plugin (Tem de ter um construtor vazio)

```
    Kotlin 
    
    class FormatarDataPlugin : JsonPlugin {
        override fun transform(obj: Any): String {
            if (obj !is DataCustom) 
                return obj.toString()
            val d = obj.dia.toString().padStart(2, '0')
            val m = obj.mes.toString().padStart(2, '0')
            return "$d/$m/${obj.ano}"
        }
    }
```

Passo B: Anotar a classe de dados

```
    Kotlin
    
    @JsonString(FormatarDataPlugin::class)
    data class DataCustom(val dia: Int, val mes: Int, val ano: Int)
    
    fun main() {
    val data = DataCustom(5, 9, 2026)
    val json = ProJson().toJson(data)
    
        println(json.toString()) 
        // Output esperado: "05/09/2026"
    }
```

## Autores

<div align="center">
    <ul style="list-style-type: none; padding: 0;">
        <li><b>Nádia Gavancha</b> - <a href="https://github.com/nadiaprg">GitHub</a></li>
        <li><b>Matilde Gloria</b> - <a href="https://github.com/matildeggloria4">GitHub</a></li>
    </ul>
</div>


