package model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import kotlin.test.assertContains

class PlayerTest {
    @Nested
    inner class GenerateAIHands() {
        @Test
        fun `AI as participant should generate OO or OC or CO CC`() {
            val participantPlayer = ParticipantPlayer(PlayerType.AI)
            val aiPlay = participantPlayer.generateAIHands()
            Assertions.assertEquals(aiPlay.length, 2)
            assertContains( "OC", aiPlay[0])
            assertContains( "OC", aiPlay[1])
        }

        @Test
        fun `AI as predictor as should generate OCX format`() {
            val predictorPlayer = PredictorPlayer(PlayerType.AI)
            val aiPlay = predictorPlayer.generateAIHands()

            Assertions.assertEquals(aiPlay.length, 3)
            assertContains( "OC", aiPlay[0])
            assertContains( "OC", aiPlay[1])
            Assertions.assertTrue(aiPlay[2].digitToInt() <= 4)
            Assertions.assertTrue(aiPlay[2].digitToInt() >= 0)
        }
    }
}