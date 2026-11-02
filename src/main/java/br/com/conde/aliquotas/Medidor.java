package br.com.conde.aliquotas;

import java.util.List;

/**
 * Roda as tabelas do README. Nenhum numero daquele arquivo foi digitado a mao.
 *
 * <pre>
 * mvn -B -ntp exec:java -Dexec.args="perfeitos"
 * mvn -B -ntp exec:java -Dexec.args="amigaveis"
 * mvn -B -ntp exec:java -Dexec.args="sociaveis"
 * mvn -B -ntp exec:java -Dexec.args="tipos"
 * mvn -B -ntp exec:java -Dexec.args="juiz"
 * mvn -B -ntp exec:java -Dexec.args="fundo"      (varredura cara, job proprio)
 * </pre>
 */
public final class Medidor {

    /** Limite do crivo: perfeitos, amigaveis e sociaveis saem daqui. */
    public static final int LIMITE = 100_000;

    /** Limite da classificacao completa, que e bem mais cara por numero. */
    public static final int LIMITE_TIPOS = 15_000;

    /** Limite da varredura de fundo, que roda num job proprio. */
    public static final int LIMITE_FUNDO = 1_000_000;

    public static void main(String[] args) {
        String comando = args.length > 0 ? args[0] : "tudo";
        switch (comando) {
            case "perfeitos" -> perfeitos();
            case "amigaveis" -> amigaveis();
            case "sociaveis" -> sociaveis();
            case "tipos" -> tipos();
            case "juiz" -> juiz();
            case "tetos" -> tetos();
            case "fundo" -> fundo();
            case "tudo" -> {
                perfeitos();
                amigaveis();
                sociaveis();
                tipos();
                juiz();
            }
            default -> {
                System.out.println("comando desconhecido: " + comando);
                System.exit(2);
            }
        }
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println(texto);
        System.out.println("-".repeat(texto.length()));
    }

    private static void perfeitos() {
        titulo("Numeros perfeitos ate " + LIMITE);
        Varredura v = new Varredura(LIMITE);
        System.out.printf("%-10s %-14s %s%n", "n", "s(n)", "forma de Euclides");
        for (int n : v.perfeitos()) {
            System.out.printf("%-10d %-14d %s%n", n, v.somaDe(n), euclides(n));
        }
    }

    /** Todo perfeito par e 2^(p-1) x (2^p - 1). Mostra o p, se a forma servir. */
    private static String euclides(int n) {
        int potencia = Integer.numberOfTrailingZeros(n);
        long parte = n >> potencia;
        long mersenne = (1L << (potencia + 1)) - 1;
        return parte == mersenne ? "2^" + potencia + " x " + mersenne + "  (p = " + (potencia + 1) + ")"
                : "nao bate";
    }

    private static void amigaveis() {
        titulo("Pares amigaveis com o menor elemento ate " + LIMITE);
        Varredura v = new Varredura(LIMITE);
        List<long[]> pares = v.paresAmigaveis();
        System.out.printf("%-10s %-10s %s%n", "a", "b", "soma do par");
        for (long[] p : pares) {
            System.out.printf("%-10d %-10d %d%n", p[0], p[1], p[0] + p[1]);
        }
        System.out.printf("%npares: %d%n", pares.size());
    }

    private static void sociaveis() {
        titulo("Ciclos de periodo 3 ou mais comecando ate " + LIMITE);
        Varredura v = new Varredura(LIMITE);
        for (List<Long> ciclo : v.ciclosSociaveis()) {
            System.out.printf("periodo %-4d menor %-10d maior %d%n",
                    ciclo.size(),
                    ciclo.stream().mapToLong(Long::longValue).min().orElseThrow(),
                    ciclo.stream().mapToLong(Long::longValue).max().orElseThrow());
            if (ciclo.size() <= 6) {
                System.out.println("   " + ciclo);
            }
        }
    }

    private static void tipos() {
        titulo("No que cada sequencia termina, de 2 a " + LIMITE_TIPOS);
        Varredura v = new Varredura(LIMITE);
        Varredura.Conta c = v.contarTipos(LIMITE_TIPOS);
        int total = c.total();
        System.out.printf("%-16s %-10s %s%n", "destino", "quantos", "fatia");
        linha("termina em 1", c.termina(), total);
        linha("perfeito", c.perfeito(), total);
        linha("amigavel", c.amigavel(), total);
        linha("sociavel", c.sociavel(), total);
        linha("cai em ciclo", c.caiEmCiclo(), total);
        linha("nao resolveu", c.aberta(), total);
        System.out.printf("%ntotal varrido: %d%n", total);
        List<Integer> presos = v.abertas(LIMITE_TIPOS);
        System.out.printf("os doze menores que nao resolveram: %s%n",
                presos.subList(0, Math.min(12, presos.size())));
        System.out.println("  (com o teto de valor atual; veja o comando \"tetos\")");
    }

    /** Os tetos de valor usados na tabela, de 10^8 a 10^13. */
    private static final long[] TETOS = {
        100_000_000L, 1_000_000_000L, 10_000_000_000L,
        100_000_000_000L, 1_000_000_000_000L, 10_000_000_000_000L
    };

    /** Ate onde varrer na tabela de tetos: o custo da fatoracao sobe junto com o teto. */
    public static final int LIMITE_TETOS = 1_000;

    private static void tetos() {
        titulo("Quantas \"nao resolvem\" ate " + LIMITE_TETOS + ", por teto de valor");
        Varredura v = new Varredura(LIMITE);
        System.out.printf("%-16s %-14s %s%n", "teto", "nao resolvem", "os menores");
        for (long teto : TETOS) {
            List<Integer> presos = v.abertasComTeto(LIMITE_TETOS, teto);
            String amostra = presos.toString();
            System.out.printf("10^%-13d %-14d %s%n",
                    (long) Math.round(Math.log10(teto)), presos.size(), amostra);
        }
        System.out.println();
        System.out.println("A coluna do meio nao mede o problema: mede o teto que eu escolhi.");
    }

    private static void linha(String nome, int quantos, int total) {
        System.out.printf("%-16s %-10d %.3f%%%n", nome, quantos, 100.0 * quantos / total);
    }

    private static void juiz() {
        titulo("As conferencias");
        Varredura v = new Varredura(LIMITE);
        System.out.printf("crivo x soma direta, numero por numero   : %d de %d%n",
                v.conferirCrivoContraDireta(LIMITE), LIMITE);
        System.out.printf("soma direta x formula da fatoracao       : %d de %d%n",
                v.conferirDiretaContraFatoracao(50_000), 50_000);
    }

    private static void fundo() {
        titulo("Varredura ate " + LIMITE_FUNDO);
        long comeco = System.nanoTime();
        Varredura v = new Varredura(LIMITE_FUNDO);
        List<Integer> perf = v.perfeitos();
        List<long[]> pares = v.paresAmigaveis();
        System.out.println("perfeitos      : " + perf);
        System.out.println("pares amigaveis: " + pares.size());
        System.out.printf("o maior par    : %d e %d%n",
                pares.get(pares.size() - 1)[0], pares.get(pares.size() - 1)[1]);
        int naoAlcancados = v.naoAlcancadosAte(10_000, LIMITE_FUNDO);
        System.out.printf("%nn ate 10.000 que nao sao s(m) para nenhum m ate %d: %d%n",
                LIMITE_FUNDO, naoAlcancados);
        System.out.println("  (cota superior: um m maior ainda pode alcancar algum deles)");
        System.out.printf("%ntempo: %.1f s%n", (System.nanoTime() - comeco) / 1e9);
    }
}
