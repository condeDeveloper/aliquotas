package br.com.conde.aliquotas;

import java.util.ArrayList;
import java.util.List;

/**
 * Varre todo n ate um limite e separa quem e perfeito, quem forma par
 * amigavel, quem entra em ciclo maior e quem nao resolveu.
 *
 * <p>O primeiro passo de todo mundo sai de um crivo: para cada divisor d,
 * soma-se d em todos os multiplos. Isso da a soma de divisores proprios de
 * milhoes de numeros no tempo em que a fatoracao daria a de alguns milhares.
 */
public final class Varredura {

    private final int limite;
    private final int[] soma;

    public Varredura(int limite) {
        if (limite < 2) {
            throw new IllegalArgumentException("limite pequeno demais: " + limite);
        }
        this.limite = limite;
        this.soma = SomaDivisores.crivo(limite);
    }

    public int limite() {
        return limite;
    }

    /** A soma de divisores proprios, do crivo quando cabe e da fatoracao quando nao cabe. */
    public long somaDe(long n) {
        return n <= limite ? soma[(int) n] : SomaDivisores.porFatoracao(n);
    }

    /** Os n com s(n) = n. */
    public List<Integer> perfeitos() {
        List<Integer> achados = new ArrayList<>();
        for (int n = 2; n <= limite; n++) {
            if (soma[n] == n) {
                achados.add(n);
            }
        }
        return achados;
    }

    /**
     * Os pares com s(a) = b e s(b) = a, com a menor que b. O b pode passar do
     * limite do crivo: nesse caso a soma dele sai da fatoracao.
     */
    public List<long[]> paresAmigaveis() {
        List<long[]> pares = new ArrayList<>();
        for (int a = 2; a <= limite; a++) {
            long b = soma[a];
            if (b > a && somaDe(b) == a) {
                pares.add(new long[] {a, b});
            }
        }
        return pares;
    }

    /**
     * Os ciclos de periodo 3 ou mais que comecam dentro do limite. Cada ciclo
     * aparece uma vez so, representado pelo menor elemento dele.
     */
    public List<List<Long>> ciclosSociaveis() {
        List<List<Long>> ciclos = new ArrayList<>();
        for (int n = 2; n <= limite; n++) {
            Sequencia.Destino d = Sequencia.classificar(n);
            if (d.tipo() != Sequencia.Tipo.SOCIAVEL) {
                continue;
            }
            List<Long> ciclo = cicloDe(n, d.periodo());
            long menor = ciclo.stream().mapToLong(Long::longValue).min().orElseThrow();
            if (menor == n) {
                ciclos.add(ciclo);
            }
        }
        return ciclos;
    }

    private List<Long> cicloDe(long inicio, int periodo) {
        List<Long> ciclo = new ArrayList<>(periodo);
        long atual = inicio;
        for (int i = 0; i < periodo; i++) {
            ciclo.add(atual);
            atual = Sequencia.passo(atual);
        }
        return ciclo;
    }

    /** Quantos n de cada tipo, varrendo tudo de 2 ate o limite. */
    public record Conta(int termina, int perfeito, int amigavel, int sociavel, int caiEmCiclo, int aberta) {
        public int total() {
            return termina + perfeito + amigavel + sociavel + caiEmCiclo + aberta;
        }
    }

    public Conta contarTipos(int ate) {
        int[] c = new int[Sequencia.Tipo.values().length];
        for (int n = 2; n <= ate; n++) {
            c[Sequencia.classificar(n).tipo().ordinal()]++;
        }
        return new Conta(
                c[Sequencia.Tipo.TERMINA.ordinal()],
                c[Sequencia.Tipo.PERFEITO.ordinal()],
                c[Sequencia.Tipo.AMIGAVEL.ordinal()],
                c[Sequencia.Tipo.SOCIAVEL.ordinal()],
                c[Sequencia.Tipo.CAI_EM_CICLO.ordinal()],
                c[Sequencia.Tipo.ABERTA.ordinal()]);
    }

    /** Os n que nao resolveram dentro dos tetos. */
    public List<Integer> abertas(int ate) {
        return abertasComTeto(ate, Sequencia.TETO);
    }

    /**
     * Os n que nao resolveram com ESTE teto de valor.
     *
     * <p>A lista encolhe conforme o teto sobe, e e essa a medida que interessa:
     * uma sequencia declarada "aberta" com teto baixo quase sempre so estava
     * passeando por um pico antes de desabar. O 138 sobe a mais de 1,7 x 10^11
     * e volta para 1; com teto de 10^11 ele entra na lista de abertas sem ter
     * nada de aberto.
     */
    public List<Integer> abertasComTeto(int ate, long teto) {
        List<Integer> presos = new ArrayList<>();
        for (int n = 2; n <= ate; n++) {
            if (Sequencia.classificar(n, Sequencia.PASSOS, teto).tipo() == Sequencia.Tipo.ABERTA) {
                presos.add(n);
            }
        }
        return presos;
    }

    /**
     * Quantos n ate {@code ate} NAO aparecem como s(m) para nenhum m ate
     * {@code procurarAte}.
     *
     * <p>Isto e uma COTA SUPERIOR, nao a resposta: um m maior que o teto ainda
     * pode produzir algum desses n e tirar ele da lista. O numero so encolhe
     * conforme o teto sobe, e a tabela diz com qual teto foi medido.
     */
    public int naoAlcancadosAte(int ate, int procurarAte) {
        boolean[] alcancado = new boolean[ate + 1];
        int[] grande = SomaDivisores.crivo(procurarAte);
        for (int m = 2; m <= procurarAte; m++) {
            int s = grande[m];
            if (s >= 0 && s <= ate) {
                alcancado[s] = true;
            }
        }
        int quantos = 0;
        for (int n = 1; n <= ate; n++) {
            if (!alcancado[n]) {
                quantos++;
            }
        }
        return quantos;
    }

    /** Confere o crivo contra a soma direta, numero por numero. */
    public int conferirCrivoContraDireta(int ate) {
        for (int n = 1; n <= ate; n++) {
            if (soma[n] != SomaDivisores.direta(n)) {
                throw new IllegalStateException("o crivo discorda da soma direta em n = " + n);
            }
        }
        return ate;
    }

    /** Confere a soma direta contra a formula da fatoracao, numero por numero. */
    public int conferirDiretaContraFatoracao(int ate) {
        for (int n = 1; n <= ate; n++) {
            if (SomaDivisores.direta(n) != SomaDivisores.porFatoracao(n)) {
                throw new IllegalStateException("os dois caminhos discordam em n = " + n);
            }
        }
        return ate;
    }
}
