package br.com.conde.aliquotas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SomaDivisoresTest {

    @Test
    @DisplayName("os casos de cabeceira, contados na mao")
    void casosDeCabeceira() {
        assertThat(SomaDivisores.direta(1)).isZero();
        assertThat(SomaDivisores.direta(2)).isEqualTo(1);
        assertThat(SomaDivisores.direta(6)).isEqualTo(6);
        assertThat(SomaDivisores.direta(12)).isEqualTo(16);
        assertThat(SomaDivisores.direta(28)).isEqualTo(28);
        assertThat(SomaDivisores.direta(220)).isEqualTo(284);
        assertThat(SomaDivisores.direta(284)).isEqualTo(220);
    }

    @Test
    @DisplayName("para p primo a soma dos divisores proprios e 1")
    void primoSomaUm() {
        for (long p : new long[] {2, 3, 5, 7, 11, 13, 97, 101, 7919}) {
            assertThat(SomaDivisores.direta(p)).as("p = %d", p).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("quadrado perfeito nao conta a raiz duas vezes")
    void quadradoPerfeito() {
        // 36: 1+2+3+4+6+9+12+18 = 55, com o 6 aparecendo UMA vez
        assertThat(SomaDivisores.direta(36)).isEqualTo(55);
        assertThat(SomaDivisores.direta(49)).isEqualTo(8);
        assertThat(SomaDivisores.direta(10000)).isEqualTo(SomaDivisores.porFatoracao(10000));
    }

    @Test
    @DisplayName("os dois caminhos concordam em todo n ate 20.000")
    void diretaConcordaComFatoracao() {
        for (long n = 1; n <= 20_000; n++) {
            assertThat(SomaDivisores.porFatoracao(n))
                    .as("n = %d", n)
                    .isEqualTo(SomaDivisores.direta(n));
        }
    }

    @Test
    @DisplayName("o crivo concorda com a soma direta em todo n ate 20.000")
    void crivoConcordaComDireta() {
        int[] crivo = SomaDivisores.crivo(20_000);
        for (int n = 1; n <= 20_000; n++) {
            assertThat((long) crivo[n]).as("n = %d", n).isEqualTo(SomaDivisores.direta(n));
        }
    }

    @Test
    @DisplayName("os tres caminhos valem para numeros grandes tambem")
    void numerosGrandes() {
        for (long n : new long[] {999_983L, 1_000_000L, 123_456_789L, 999_999_937L}) {
            assertThat(SomaDivisores.porFatoracao(n))
                    .as("n = %d", n)
                    .isEqualTo(SomaDivisores.direta(n));
        }
    }

    @Test
    @DisplayName("zero e negativo sao recusados na porta")
    void recusaForaDoDominio() {
        assertThatThrownBy(() -> SomaDivisores.direta(0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> SomaDivisores.porFatoracao(-5)).isInstanceOf(IllegalArgumentException.class);
    }
}
