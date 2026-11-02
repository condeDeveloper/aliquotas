package br.com.conde.aliquotas;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A sequencia aliquota de n: some os divisores proprios, repita com o
 * resultado, repita de novo. Quatro coisas podem acontecer, e so quatro:
 *
 * <ul>
 *   <li>cair em 1 e morrer (o caminho de quase todo mundo);
 *   <li>voltar para o proprio n em um passo: n e PERFEITO;
 *   <li>voltar em dois passos: n e metade de um par AMIGAVEL;
 *   <li>voltar em tres ou mais: n faz parte de um ciclo SOCIAVEL.
 * </ul>
 *
 * <p>Fora disso sobra o caso que ninguem sabe resolver: a sequencia cresce e
 * nao da sinal de parar. Aqui isso tem nome proprio (ABERTA) e nunca e
 * confundido com "termina".
 */
public final class Sequencia {

    public enum Tipo {
        /** Chegou a 1 e acabou. */
        TERMINA,
        /** s(n) = n. */
        PERFEITO,
        /** Ciclo de periodo 2. */
        AMIGAVEL,
        /** Ciclo de periodo 3 ou mais. */
        SOCIAVEL,
        /** Caiu num ciclo que nao contem o proprio n. */
        CAI_EM_CICLO,
        /** Passou do teto de valor ou do teto de passos sem resolver. */
        ABERTA
    }

    /**
     * @param tipo o que aconteceu
     * @param periodo tamanho do ciclo, ou 0 se nao houve ciclo
     * @param passos quantas vezes a soma foi aplicada
     * @param maior o maior valor que a sequencia alcancou
     */
    public record Destino(Tipo tipo, int periodo, int passos, long maior) {
    }

    /** Teto de valor: acima disso a fatoracao fica cara e a sequencia e declarada aberta. */
    public static final long TETO = 1_000_000_000_000L;

    /** Teto de passos. */
    public static final int PASSOS = 1000;

    private Sequencia() {
    }

    /** O proximo valor da sequencia. */
    public static long passo(long n) {
        return SomaDivisores.porFatoracao(n);
    }

    public static Destino classificar(long n) {
        return classificar(n, PASSOS, TETO);
    }

    public static Destino classificar(long n, int tetoPassos, long tetoValor) {
        Map<Long, Integer> quando = new LinkedHashMap<>();
        long atual = n;
        long maior = n;
        for (int i = 0; i < tetoPassos; i++) {
            if (atual == 0) {
                return new Destino(Tipo.TERMINA, 0, i, maior);
            }
            Integer visto = quando.get(atual);
            if (visto != null) {
                int periodo = i - visto;
                Tipo tipo;
                if (visto != 0) {
                    tipo = Tipo.CAI_EM_CICLO;
                } else if (periodo == 1) {
                    tipo = Tipo.PERFEITO;
                } else if (periodo == 2) {
                    tipo = Tipo.AMIGAVEL;
                } else {
                    tipo = Tipo.SOCIAVEL;
                }
                return new Destino(tipo, periodo, i, maior);
            }
            quando.put(atual, i);
            atual = passo(atual);
            maior = Math.max(maior, atual);
            if (atual > tetoValor) {
                return new Destino(Tipo.ABERTA, 0, i + 1, maior);
            }
        }
        return new Destino(Tipo.ABERTA, 0, tetoPassos, maior);
    }

    /** A trajetoria inteira, do n ate o fim ou ate o teto de passos. */
    public static List<Long> trajetoria(long n, int tetoPassos) {
        List<Long> caminho = new ArrayList<>();
        long atual = n;
        for (int i = 0; i < tetoPassos && atual != 0; i++) {
            caminho.add(atual);
            if (caminho.size() > 1 && caminho.subList(0, caminho.size() - 1).contains(atual)) {
                break;
            }
            atual = passo(atual);
        }
        return caminho;
    }
}
