# SincronizacaoReceita
Aplicação Spring Boot para leitura rápida de arquivos grandes e sincronização de receita

### Instruções de Uso

#### Iniciar Aplicação
À partir de um diretório vazio, execute o código abaixo inserindo o(s) arquivo(s) CSV a ser(em) lido(s), como no exemplo abaixo:
````
git clone https://github.com/Arionildo/SincronizacaoReceita.git
cd Votacao
mvn package
cd target
java -jar sincronizacaoreceita-1.0.0.jar <caminho-do-arquivo>
````