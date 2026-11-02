package br.com.conde.aliquotas;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Os numeros do README, presos. Todos sairam do {@link Medidor} depois de a
 * varredura terminar: nenhum foi digitado de cabeca.
 */
class MedidasTest {

    private static final Varredura V = new Varredura(Medidor.LIMITE);

    @Test
    @DisplayName("quatro perfeitos ate cem mil, e todos na forma de Euclides")
    void perfeitos() {
        assertThat(V.perfeitos()).containsExactly(6, 28, 496, 8128);
    }

    @Test
    @DisplayName("treze pares amigaveis ate cem mil, comecando em 220 e 284")
    void paresAmigaveis() {
        List<long[]> pares = V.paresAmigaveis();
        assertThat(pares).hasSize(13);
        assertThat(pares.get(0)).containsExactly(220L, 284L);
        assertThat(pares.get(1)).containsExactly(1184L, 1210L);
        assertThat(pares.get(12)).containsExactly(79750L, 88730L);
    }

    @Test
    @DisplayName("dois ciclos sociaveis ate cem mil: periodo 5 e periodo 28")
    void ciclosSociaveis() {
        List<List<Long>> ciclos = V.ciclosSociaveis();
        assertThat(ciclos).hasSize(2);

        List<Long> cinco = ciclos.get(0);
        assertThat(cinco).hasSize(5);
        assertThat(cinco).containsExactly(12496L, 14288L, 15472L, 14536L, 14264L);

        assertThat(ciclos.get(1)).hasSize(28);
        assertThat(ciclos.get(1).get(0)).isEqualTo(14316L);
        assertThat(ciclos.get(1).stream().mapToLong(Long::longValue).max().orElseThrow())
                .isEqualTo(629072L);
    }

    @Test
    @DisplayName("a conta por destino, de 2 a 15.000")
    void contaPorDestino() {
        Varredura.Conta c = V.contarTipos(Medidor.LIMITE_TIPOS);
        assertThat(c.total()).isEqualTo(14_999);
        assertThat(c.termina()).isEqualTo(13_142);
        assertThat(c.perfeito()).isEqualTo(4);
        assertThat(c.amigavel()).isEqualTo(14);
        assertThat(c.sociavel()).isEqualTo(5);
        assertThat(c.caiEmCiclo()).isEqualTo(322);
        assertThat(c.aberta()).isEqualTo(1_512);
    }

    @Test
    @DisplayName("os 14 amigaveis ate 15.000 sao sete pares inteiros")
    void amigaveisSaoParesInteiros() {
        Varredura.Conta c = V.contarTipos(Medidor.LIMITE_TIPOS);
        assertThat(c.amigavel() % 2).isZero();
        assertThat(c.amigavel() / 2).isEqualTo(7);
    }

    @Test
    @DisplayName("o teto de valor muda a resposta: 30 com 10^11, 19 com 10^12")
    void aRespostaDependeDoTeto() {
        assertThat(V.abertasComTeto(Medidor.LIMITE_TETOS, 100_000_000_000L)).hasSize(30);
        assertThat(V.abertasComTeto(Medidor.LIMITE_TETOS, 1_000_000_000_000L)).hasSize(19);
        assertThat(V.abertasComTeto(Medidor.LIMITE_TETOS, 10_000_000_000_000L)).hasSize(19);
    }

    @Test
    @DisplayName("os cinco de Lehmer estao entre os 19 que nao cedem")
    void osCincoDeLehmer() {
        List<Integer> presos = V.abertasComTeto(Medidor.LIMITE_TETOS, 10_000_000_000_000L);
        assertThat(presos).hasSize(19);
        assertThat(presos).contains(276, 552, 564, 660, 966);
        assertThat(presos.get(0)).isEqualTo(276);
        // e o 138, que o teto baixo prendia, nao esta mais aqui
        assertThat(presos).doesNotContain(138);
    }

    @Test
    @DisplayName("as duas conferencias fecham")
    void conferencias() {
        assertThat(V.conferirCrivoContraDireta(Medidor.LIMITE)).isEqualTo(Medidor.LIMITE);
        assertThat(V.conferirDiretaContraFatoracao(50_000)).isEqualTo(50_000);
    }

    @Test
    @DisplayName("todo perfeito par encontrado tem a forma 2^(p-1) x (2^p - 1)")
    void formaDeEuclides() {
        for (int n : V.perfeitos()) {
            int potencia = Integer.numberOfTrailingZeros(n);
            long parte = n >> potencia;
            long mersenne = (1L << (potencia + 1)) - 1;
            assertThat(parte).as("n = %d", n).isEqualTo(mersenne);
        }
    }
}
