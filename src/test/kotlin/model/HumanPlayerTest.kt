package model

import assertk.assertThat
import assertk.assertions.*
import exceptions.PlayerInputException
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class HumanPlayerTest {
    @Nested
    inner class Play() {
        @Nested
        inner class Predicting() {
            @Test
            fun `throw exception when input is more than 3`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("1234")
                    humanPlayer.play(true)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should have 3 characters, e.g. OC2")

            }

            @Test
            fun `throw exception when 1st character is not O or C`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("D23")
                    humanPlayer.play(true)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2")
            }

            @Test
            fun `throw exception when 2nd character is not O or C`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("O2F")
                    humanPlayer.play(true)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2")
            }

            @Test
            fun `throw exception when 3rd character is not 0-4`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("CO5")
                    humanPlayer.play(true)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("3rd character (guess) should be 0-4, e.g. OC2")
            }

            @Test
            fun `should set hands and prediction when input is correct`() {
                val humanPlayer = HumanPlayer()
                humanPlayer.enterInput("OC2")
                humanPlayer.play(true)

                assertThat(humanPlayer.hands).isEqualTo("OC")
                assertThat(humanPlayer.prediction).isEqualTo(2)
            }
        }

        @Nested
        inner class Participating() {
            @Test
            fun `throw exception when input is more than 2`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("124")
                    humanPlayer.play(false)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should have 2 characters, e.g. CO")
            }

            @Test
            fun `throw exception when 1st character is not O or C`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("D2")
                    humanPlayer.play(false)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should 'O' (open) or 'C' (close) for first two characters, e.g. OC")
            }

            @Test
            fun `throw exception when 2nd character is not O or C`() {
                assertThat {
                    val humanPlayer = HumanPlayer()
                    humanPlayer.enterInput("O2")
                    humanPlayer.play(false)
                }.isFailure().isInstanceOf(PlayerInputException::class)
                    .hasMessage("input should 'O' (open) or 'C' (close) for first two characters, e.g. OC")
            }

            @Test
            fun `should set hands and prediction when input is correct`() {
                val humanPlayer = HumanPlayer()
                humanPlayer.enterInput("CO")
                humanPlayer.play(false)
                assertThat(humanPlayer.hands).isEqualTo("CO")
                assertThat(humanPlayer.prediction).isNull()
            }
        }
    }
}