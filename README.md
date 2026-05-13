<h1 align="center"> 
    ProJson_projetoPA 

![Kotlin](https://img.shields.io/badge/Kotlin-2.2-indigo.svg?logo=kotlin)
![Versão](https://img.shields.io/badge/Vers%C3%A3o-1.0.0-success.svg)
</h1>


O ProJson é uma biblioteca de serialização 
JSON para Kotlin. Utilizando reflexão, converte instâncias de 
objetos para valores JSON. 
Destaca-se por gerir referências entre objetos, 
prevenindo ciclos infinitos e reduzindo a duplicação de dados, 
além de oferecer um sistema de plugins para personalização total.


##    Indice


* [Funcionalidades](#funcionalidades)
* [Pré-Requisitos](#pré-requisitos)
* [Instalação](#instalação)
* [Tutorial](#tutorial)
* [Autores](#autores)


##    Funcionalidades


- **Serialização Automática**: Converte primitivos, coleções, mapas e objetos complexos 
(Data Classes e Classes normais) de forma transparente.

- **Gestão de Referências** (@Reference): Suporta grafos de objetos complexos gerando 
identificadores únicos ($\$id$) e referências ($\$ref$), garantindo a integridade dos dados na memória.

- **Personalização Rápida** (@JsonProperty, @JsonIgnore): Permite renomear chaves ou omitir propriedades facilmente.

- **Mecanismo de Plugins** (@JsonString): Permite formatar classes específicas como Strings usando classes 
de conversão externas, promovendo código limpo e modular.


##    Pré-requisitos




##    Instalação


Como o projeto está distribuído via ficheiro .jar, a instalação é simples:

1. Vai à página de Releases deste repositório no GitHub.

2. Faz o download do ficheiro ProJson-1.0-SNAPSHOT.jar.

3. No IntelliJ IDEA / Gradle: Cria uma pasta chamada libs na raiz do teu projeto e coloca lá o .jar.

    - Adiciona a seguinte linha ao teu build.gradle.kts:
         ```
        implementation(files("libs/ProJson-1.0-SNAPSHOT.jar"))
         ```
    - Sincroniza o Gradle.


##    Tutorial


Aqui estão os guias passo-a-passo para tirares o máximo partido do ProJson.

1. **Serialização Básica**
   
    Para converter um objeto normal, basta instanciar o motor ProJson e chamar o método toJson.

    ```
    data class Utilizador(val nome: String, val idade: Int)
    
    fun main() {
    val user = Utilizador("Maria", 25)
    val motor = ProJson()
    
        val json = motor.toJson(user)
        println(json.toString())
    }
    ```

2. **Omitir e Renomear Propriedades**

   Usa o @JsonIgnore para esconder dados sensíveis e o @JsonProperty para adaptares o nome da chave no JSON final.

    ```
    class Produto(
    @JsonProperty("identificador")
    val id: Int,
    
        val titulo: String,
        
        @JsonIgnore
        val custoProducao: Double
    )
    
    // Output esperado: { "$type": "Produto", "identificador": 1, "titulo": "Cadeira" }
    ```

3. **Trabalhar com Referências (Evitar Duplicados e Ciclos)**

   Se tens objetos que partilham a mesma dependência, usa a anotação @Reference. O ProJson vai gerar um UUID para o objeto na primeira vez que o encontrar e, das próximas vezes, vai usar um ponteiro {"$ref": "uuid"}.

    ```
    class Task(
    val descricao: String,
    @Reference val dependencias: List<Task>
    )
    
    fun main() {
    val t1 = Task("Comprar cimento", emptyList())
    val t2 = Task("Fazer fundação", listOf(t1))
    val t3 = Task("Levantar paredes", listOf(t1)) // t1 é referenciado novamente
    
        val json = ProJson().toJson(listOf(t2, t3))
        println(json.toString())
    }
    ```

4. **Usar Plugins Customizados (@JsonString)**

   Se quiseres que um objeto inteiro seja representado como uma simples String formatada (como uma Data), cria um plugin implementando a interface JsonPlugin.

    
Passo A: Criar a classe do Plugin (Tem de ter um construtor vazio)

```
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


