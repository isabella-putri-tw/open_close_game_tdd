package model

import assertk.assertThat
import assertk.assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AIPLayerTest {
    @Nested
    inner class play() {
        @Test
        fun `AI as participant should generate OO or OC or CO CC`() {
            val aiPlayer = AIPlayer()
            aiPlayer.play(false)
            assertThat(aiPlayer.hands).isNotNull().hasLength(2)
            assertThat(aiPlayer.hands!![0]).isIn('O', 'C')
            assertThat(aiPlayer.hands!![1]).isIn('O', 'C')
            assertThat(aiPlayer.prediction).isNull()
        }

        @Test
        fun `AI as predictor as should generate OCX format`() {
            val aiPlayer = AIPlayer()
            aiPlayer.play(true)
            assertThat(aiPlayer.hands).isNotNull().hasLength(2)
            assertThat(aiPlayer.hands!![0]).isIn('O', 'C')
            assertThat(aiPlayer.hands!![1]).isIn('O', 'C')
            assertThat(aiPlayer.prediction).isNotNull().isGreaterThanOrEqualTo(0)
            assertThat(aiPlayer.prediction).isNotNull().isLessThanOrEqualTo(4)
        }
    }
}