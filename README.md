# aliquotas

Pegue um número. Some os divisores dele, menos ele mesmo. Repita com o resultado.

O 12 vira 16, que vira 15, que vira 9, que vira 4, que vira 3, que vira 1. Acabou. Quase
todo número faz isso: cai, cai, cai e morre no 1.

Mas o 6 vira 6. O 220 vira 284, que vira 220 de volta. O 12496 dá uma volta de **cinco**
números e retorna ao começo. O 14316 dá uma volta de **vinte e oito**.

E o 276 sobe, e sobe, e ninguém no mundo sabe se ele volta. Este repositório varre todos
os números até cem mil, classifica cada um no que ele vira, e — a parte que me custou a
noite — mede o quanto da resposta depende do **teto que eu escolhi**, e não do problema.

```bash
mvn -B -ntp verify                              # 23 testes
mvn -B -ntp exec:java -Dexec.args="perfeitos"   # os n com s(n) = n
mvn -B -ntp exec:java -Dexec.args="amigaveis"   # os pares que se apontam
mvn -B -ntp exec:java -Dexec.args="sociaveis"   # os ciclos de três ou mais
mvn -B -ntp exec:java -Dexec.args="tipos"       # no que cada sequência termina
mvn -B -ntp exec:java -Dexec.args="tetos"       # a resposta em função do teto
mvn -B -ntp exec:java -Dexec.args="juiz"        # as conferências
mvn -B -ntp exec:java -Dexec.args="fundo"       # varredura até um milhão (job próprio)
```

Java 21, sem dependência fora de JUnit e AssertJ.

## Três caminhos para a mesma soma

A conta que o projeto inteiro repete milhões de vezes é "some os divisores próprios".
Ela é feita de três jeitos que não compartilham código:

- **direta**: testa candidato por candidato até a raiz;
- **por fatoração**: fatora e usa a fórmula multiplicativa de σ;
- **por crivo**: para cada divisor `d`, soma `d` em todos os múltiplos de `d`.

```
crivo x soma direta, número por número   : 100.000 de 100.000
soma direta x fórmula da fatoração       :  50.000 de  50.000
```

O crivo é o que torna a varredura possível — ele entrega a soma de um milhão de números
no tempo em que a fatoração entregaria alguns milhares. Os outros dois existem para provar
que ele está certo.

## O que foi encontrado

**Quatro perfeitos** até cem mil, e todos na forma que Euclides descreveu:

```
6      2^1 x 3     (p = 2)
28     2^2 x 7     (p = 3)
496    2^4 x 31    (p = 5)
8128   2^6 x 127   (p = 7)
```

**Treze pares amigáveis**, do 220/284 que Pitágoras já conhecia até 79750/88730:

```
220    284        6232   6368       66928  66992
1184   1210       10744  10856      67095  71145
2620   2924       12285  14595      69615  87633
5020   5564       17296  18416      79750  88730
                  63020  76084
```

**Dois ciclos sociáveis**, e só dois:

```
período 5    12496 → 14288 → 15472 → 14536 → 14264 → 12496
período 28   14316 ... sobe até 629.072 e volta
```

Não existe ciclo de período 3 nem de 4 começando abaixo de cem mil. O salto é de 2 direto
para 5, e depois de 5 direto para 28.

## No que cada sequência termina

```
destino          quantos   fatia
termina em 1     13142     87,619%
perfeito             4      0,027%
amigável            14      0,093%
sociável             5      0,033%
cai em ciclo       322      2,147%
não resolveu      1512     10,081%
```

De 2 a 15.000. "Cai em ciclo" é quem chega num ciclo sem fazer parte dele: o 25 vai parar
no 6 e fica preso ali, mas o 25 não é perfeito.

## A linha que não mede o problema, e sim a mim

A última linha da tabela acima — "não resolveu" — não é uma propriedade dos números. É uma
propriedade do **teto de valor** que eu escolhi antes de rodar. Quando a sequência passa do
teto, o programa desiste e chama de aberta.

```
teto    não resolvem até 1.000   os menores
10^8    30                       138, 150, 168, 222, 234, 276, 306, 312
10^9    30                       138, 150, 168, 222, 234, 276, 306, 312
10^10   30                       138, 150, 168, 222, 234, 276, 306, 312
10^11   30                       138, 150, 168, 222, 234, 276, 306, 312
10^12   19                       276, 306, 396, 552, 564, 660, 696, 702
10^13   19                       276, 306, 396, 552, 564, 660, 696, 702
```

O **138** é o retrato do problema. Com teto de 10¹¹ ele entra na lista dos que "não
resolvem". Ele resolve: a sequência dele sobe até passar de **1,7 × 10¹¹**, demora 177
passos, e aí desaba até o 1 como qualquer outra. Ele nunca esteve aberto — eu é que parei
de olhar cedo demais.

Entre 10¹¹ e 10¹² a lista cai de 30 para 19 e **não cai mais**. Os 19 que sobram são:

```
276  306  396  552  564  660  696  702  720  780
828  840  858  888  936  966  978  990  996
```

Os cinco primeiros números para os quais isso é um problema em aberto de verdade — 276,
552, 564, 660 e 966, os cinco de Lehmer — estão todos nessa lista.

## O que as medidas me corrigiram

**Escolhi um teto e ele mentiu para mim.** Com 10¹¹, a tabela de destinos acusava 1.669
sequências "não resolvidas" e colocava o 138 como a menor delas. O 138 resolve. O número
que eu ia publicar era sobre a minha implementação, não sobre o problema — foi exatamente
o erro que me quebrou o CI do `menage` ontem, e levei dois dias para aprender a cheirar
ele antes de publicar. Hoje o teto padrão é 10¹², onde a conta estabiliza, e existe uma
tabela inteira que mostra a resposta mudando com o teto.

**Achei que "não resolve" fosse só os cinco de Lehmer.** São **19** até mil, com o teto
alto. Os cinco famosos são os que resistem a buscas sérias de verdade, com milhões de
termos; os outros catorze apenas passam do meu teto. A diferença entre "aberto na
literatura" e "aberto no meu laptop" é grande e a tabela precisa dizer qual dos dois.

**Esperei ciclos de período 3 e 4.** Não existem abaixo de cem mil. O período pula de 2
para 5, e de 5 para 28. Eu tinha escrito "ciclos de tamanho crescente" no rascunho do
README antes de olhar a saída.

**Imprimi 1.669 números numa linha só.** Erro bobo, custou uma rodada: a tabela de
destinos despejava a lista inteira dos não resolvidos. Hoje mostra os doze menores e
aponta para o comando que explica o resto.

## Quatro achados

1. **Treze pares amigáveis e quatro perfeitos** até cem mil — e os quatro perfeitos saem
   todos da forma `2^(p-1) × (2^p - 1)`, com p igual a 2, 3, 5 e 7.
2. **Não existe ciclo de período 3 ou 4** começando abaixo de cem mil: o salto é de 2
   direto para **5** (em 12496) e de 5 direto para **28** (em 14316, que sobe a 629.072).
3. **A fatia de "não resolve" é do teto, não do problema:** subir o teto de 10¹¹ para 10¹²
   derruba a contagem até mil de **30 para 19**, e tira o 138 da lista — ele sobe a
   1,7 × 10¹¹ e cai, em 177 passos.
4. Dos 14.999 números varridos, **87,619% morrem no 1** e apenas **23** fazem parte de
   algum ciclo: 4 perfeitos, 14 amigáveis e 5 sociáveis.
