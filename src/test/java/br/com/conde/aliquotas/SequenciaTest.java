package br.com.conde.aliquotas;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SequenciaTest {

    @Test
    @DisplayName("perfeito e o ciclo de periodo 1")
    void perfeitos() {
        for (long n : new long[] {6, 28, 496, 8128}) {
            Sequencia.Destino d = Sequencia.classificar(n);
            assertThat(d.tipo()).as("n = %d", n).isEqualTo(Sequencia.Tipo.PERFEITO);
            assertThat(d.periodo()).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("amigavel e o ciclo de periodo 2, nos dois sentidos")
    void amigaveis() {
        for (long n : new long[] {220, 284, 1184, 1210, 2620, 2924}) {
            Sequencia.Destino d = Sequencia.classificar(n);
            assertThat(d.tipo()).as("n = %d", n).isEqualTo(Sequencia.Tipo.AMIGAVEL);
            assertThat(d.periodo()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("12496 fecha em cinco passos, e 14316 em vinte e oito")
    void sociaveis() {
        Sequencia.Destino cinco = Sequencia.classificar(12496);
        assertThat(cinco.tipo()).isEqualTo(Sequencia.Tipo.SOCIAVEL);
        assertThat(cinco.periodo()).isEqualTo(5);

        Sequencia.Destino vinteOito = Sequencia.classificar(14316);
        assertThat(vinteOito.tipo()).isEqualTo(Sequencia.Tipo.SOCIAVEL);
        assertThat(vinteOito.periodo()).isEqualTo(28);
    }

    @Test
    @DisplayName("primo morre em dois passos: vai para 1 e depois para 0")
    void primoMorreRapido() {
        Sequencia.Destino d = Sequencia.classificar(97);
        assertThat(d.tipo()).isEqualTo(Sequencia.Tipo.TERMINA);
        assertThat(d.passos()).isEqualTo(2);
    }

    @Test
    @DisplayName("quem cai num ciclo sem fazer parte dele e marcado separado")
    void caiEmCicloSemFazerParte() {
        // 25 vai para 6, que e perfeito: o 25 nao e perfeito nem esta no ciclo
        Sequencia.Destino d = Sequencia.classificar(25);
        assertThat(d.tipo()).isEqualTo(Sequencia.Tipo.CAI_EM_CICLO);
        assertThat(Sequencia.passo(25)).isEqualTo(6);
    }

    @Test
    @DisplayName("o 138 resolve, mas so com teto acima do pico dele")
    void oCasoDo138() {
        Sequencia.Destino comTetoBaixo = Sequencia.classificar(138, Sequencia.PASSOS, 100_000_000_000L);
        assertThat(comTetoBaixo.tipo()).isEqualTo(Sequencia.Tipo.ABERTA);

        Sequencia.Destino comTetoAlto = Sequencia.classificar(138, Sequencia.PASSOS, 1_000_000_000_000L);
        assertThat(comTetoAlto.tipo()).isEqualTo(Sequencia.Tipo.TERMINA);
        assertThat(comTetoAlto.maior()).isGreaterThan(100_000_000_000L);
    }

    @Test
    @DisplayName("a trajetoria comeca no proprio n e segue a soma")
    void trajetoria() {
        assertThat(Sequencia.trajetoria(12, 5)).containsExactly(12L, 16L, 15L, 9L, 4L);
        assertThat(Sequencia.trajetoria(220, 3)).containsExactly(220L, 284L, 220L);
    }
}
