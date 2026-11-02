package br.com.conde.aliquotas;

/**
 * A soma dos divisores proprios de n, por dois caminhos que nao se falam.
 *
 * <p>O caminho burro soma divisor por divisor. O caminho esperto fatora e usa a
 * formula multiplicativa de sigma. Os dois tem que dar o mesmo numero para todo
 * n varrido; se discordarem em um, um dos dois esta errado.
 */
public final class SomaDivisores {

    private SomaDivisores() {
    }

    /**
     * Soma os divisores proprios testando candidato por candidato, ate a raiz.
     * E o caminho que nao precisa saber nada sobre fatoracao.
     */
    public static long direta(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("n tem que ser positivo: " + n);
        }
        if (n == 1) {
            return 0;
        }
        long soma = 1;
        for (long d = 2; d * d <= n; d++) {
            if (n % d == 0) {
                soma += d;
                long par = n / d;
                if (par != d) {
                    soma += par;
                }
            }
        }
        return soma;
    }

    /**
     * Soma os divisores proprios pela formula de sigma: para n = produto de
     * p^a, sigma(n) = produto de (p^(a+1) - 1) / (p - 1). Tira-se o proprio n
     * no fim para ficar com os divisores PROPRIOS.
     */
    public static long porFatoracao(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("n tem que ser positivo: " + n);
        }
        long sigma = 1;
        long resto = n;
        for (long p = 2; p * p <= resto; p++) {
            if (resto % p != 0) {
                continue;
            }
            long potencia = 1;
            long parcela = 1;
            while (resto % p == 0) {
                resto /= p;
                potencia *= p;
                parcela += potencia;
            }
            sigma *= parcela;
        }
        if (resto > 1) {
            sigma *= resto + 1;
        }
        return sigma - n;
    }

    /**
     * Monta uma tabela de somas de divisores proprios ate o limite, por crivo:
     * cada d soma-se nos multiplos dele. E o unico jeito de varrer milhoes sem
     * esperar o dia inteiro.
     */
    public static int[] crivo(int limite) {
        int[] soma = new int[limite + 1];
        for (int d = 1; d <= limite / 2; d++) {
            for (int m = 2 * d; m <= limite; m += d) {
                soma[m] += d;
            }
        }
        return soma;
    }
}
