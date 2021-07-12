package services

import model.ParticipantPlayer
import model.PlayerType
import model.PredictorPlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.*
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.test.assertContains


internal class OpenCloseGameServiceServiceTest {
    @Nested
    inner class UserPlay() {
        @Test
        fun `get bad input message if input length is more than 3`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.USER, "1234"), ParticipantPlayer(PlayerType.AI))
            assertEquals("Bad input: input should have 3 characters, e.g. OC2", result)
        }

        @Test
        fun `get bad input message if 1st character is not O or C`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.USER, "D23"), ParticipantPlayer(PlayerType.AI))
            assertEquals("Bad input: input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2", result)
        }

        @Test
        fun `get bad input message if 2nd character is not O or C`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.USER, "OZ3"), ParticipantPlayer(PlayerType.AI))
            assertEquals("Bad input: input should 'O' (open) or 'C' (close) for first two characters, e.g. OC2", result)
        }

        @Test
        fun `get bad input message if 3rd character is not 0-4`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.USER, "CO5"), ParticipantPlayer(PlayerType.AI))
            assertEquals("Bad input: 3rd character (guess) should be 0-4, e.g. OC2", result)
        }

        @Test
        fun `user lost when number of open hand is incorrect`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.USER, "OC3"), ParticipantPlayer(PlayerType.AI, "CO"))
            assertEquals("No winner", result)
        }

        @Test
        fun `user win when number of open hand is correct`() {
            val openCloseGameService = OpenCloseGameService()
            val result = openCloseGameService.play(PredictorPlayer(PlayerType.USER, "CO3"), ParticipantPlayer(PlayerType.AI, "OO"))
            assertEquals("You WIN!!!", result)
            assertTrue(openCloseGameService.win)
        }

        @Test
        fun `get bad input when not user's turn to guess and still input guess`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.AI, "OO2"), ParticipantPlayer(PlayerType.USER, "OC2"))
            assertEquals("Bad input: input should have 2 characters, e.g. CO", result)
        }

        @Test
        fun `get bad input when not user's turn to guess and input not 'C' or 'O'`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.AI, "CO3"), ParticipantPlayer(PlayerType.USER, "OX"))
            assertEquals("Bad input: input should 'O' (open) or 'C' (close) for first two characters, e.g. OC", result)
        }

        @Test
        fun `no winner when not user's turn to guess and AI guess wrong`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.AI, "OC2"), ParticipantPlayer(PlayerType.USER, "CC"))
            assertEquals("No winner", result)
        }

        @Test
        fun `AI wins when not user's turn to guess and AI guess wrong`() {
            val result = OpenCloseGameService().play(PredictorPlayer(PlayerType.AI, "OC1"), ParticipantPlayer(PlayerType.USER, "CC"))
            assertEquals("AI WIN!!!", result)
        }
    }

    @Nested
    inner class StartGame() {
        private val standardOut = System.out
        private val outputStreamCaptor = ByteArrayOutputStream()

        @Mock
        lateinit var mockReader: BufferedReader

        @Spy
        lateinit var openCloseGameServiceSpy: OpenCloseGameService


        @BeforeEach
        fun setUp() {
            MockitoAnnotations.openMocks(this)
            System.setOut(PrintStream(outputStreamCaptor))
            openCloseGameServiceSpy.reader = mockReader
        }

        @AfterEach
        fun tearDown() {
            System.setOut(standardOut)
            reset(openCloseGameServiceSpy, mockReader)
        }

        @Test
        fun `should ask to end game and end when user correct guess and end game`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("OC3", "N")
            val predictorPlayer = argumentCaptor<PredictorPlayer>()
            val participantPlayer = argumentCaptor<ParticipantPlayer>()

            Mockito.doAnswer{
                println("AI: OO")
                openCloseGameServiceSpy.win = true
                "You WIN!!!"
            }.whenever(openCloseGameServiceSpy).play(predictorPlayer.capture(), participantPlayer.capture())

            openCloseGameServiceSpy.startGame()

            verify(openCloseGameServiceSpy, times(1)).play(any(), any())
            assertEquals(PlayerType.USER, predictorPlayer.firstValue.type)
            assertEquals(PlayerType.AI, participantPlayer.firstValue.type)
            assertEquals("OC3", predictorPlayer.firstValue.hands)


            assertEquals("Welcome to the game!\n" +
                    "You are the predictor, what is your input?\n" +
                    "You: OC3\n" +
                    "AI: OO\n" +
                    "You WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "N", outputStreamCaptor.toString().trim())
        }

        @Test
        fun `should repeat game when user wrong guess and then AI win`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("OC2", "CC", "N")
            val predictorPlayer = argumentCaptor<PredictorPlayer>()
            val participantPlayer = argumentCaptor<ParticipantPlayer>()

            Mockito.doAnswer{
                println("AI: OO")
                "No winner"
            }.doAnswer{
                println("AI: CO1")
                openCloseGameServiceSpy.win = true
                "AI WIN!!!"
            }.whenever(openCloseGameServiceSpy).play(predictorPlayer.capture(), participantPlayer.capture())

            openCloseGameServiceSpy.startGame()

            verify(openCloseGameServiceSpy, times(2)).play(any(), any())

            assertEquals(PlayerType.USER, predictorPlayer.firstValue.type)
            assertEquals(PlayerType.AI, participantPlayer.firstValue.type)
            assertEquals("OC2", predictorPlayer.firstValue.hands)

            assertEquals(PlayerType.AI, predictorPlayer.secondValue.type)
            assertEquals(PlayerType.USER, participantPlayer.secondValue.type)
            assertEquals("CC", participantPlayer.secondValue.hands)

            assertEquals("Welcome to the game!\n" +
                    "You are the predictor, what is your input?\n" +
                    "You: OC2\n" +
                    "AI: OO\n" +
                    "No winner\n" +
                    "AI is the predictor, what is your input?\n" +
                    "You: CC\n" +
                    "AI: CO1\n" +
                    "AI WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "N", outputStreamCaptor.toString().trim())
        }

        @Test
        fun `should repeat game 3 times when until user win`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("OC2", "CC", "OO2", "N")
            val predictorPlayer = argumentCaptor<PredictorPlayer>()
            val participantPlayer = argumentCaptor<ParticipantPlayer>()

            Mockito.doAnswer{
                println("AI: OO")
                "No winner"
            }.doAnswer{
                println("AI: CO2")
                "No winner"
            }.doAnswer{
                println("AI: CC")
                openCloseGameServiceSpy.win = true
                "You WIN!!!"
            }.whenever(openCloseGameServiceSpy).play(predictorPlayer.capture(), participantPlayer.capture())

            openCloseGameServiceSpy.startGame()

            verify(openCloseGameServiceSpy, times(3)).play(any(), any())

            assertEquals(PlayerType.USER, predictorPlayer.firstValue.type)
            assertEquals(PlayerType.AI, participantPlayer.firstValue.type)
            assertEquals("OC2", predictorPlayer.firstValue.hands)

            assertEquals(PlayerType.AI, predictorPlayer.secondValue.type)
            assertEquals(PlayerType.USER, participantPlayer.secondValue.type)
            assertEquals("CC", participantPlayer.secondValue.hands)

            assertEquals(PlayerType.USER, predictorPlayer.thirdValue.type)
            assertEquals(PlayerType.AI, participantPlayer.thirdValue.type)
            assertEquals("OO2", predictorPlayer.thirdValue.hands)

            assertEquals("Welcome to the game!\n" +
                    "You are the predictor, what is your input?\n" +
                    "You: OC2\n" +
                    "AI: OO\n" +
                    "No winner\n" +
                    "AI is the predictor, what is your input?\n" +
                    "You: CC\n" +
                    "AI: CO2\n" +
                    "No winner\n" +
                    "You are the predictor, what is your input?\n" +
                    "You: OO2\n" +
                    "AI: CC\n" +
                    "You WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "N", outputStreamCaptor.toString().trim())
        }

        @Test
        fun `should play thrice when user wants to play again twice`() {
            Mockito.`when`(mockReader.readLine()).thenReturn("OC3", "Y", "OO2", "Y", "CO2", "N")
            val predictorPlayer = argumentCaptor<PredictorPlayer>()
            val participantPlayer = argumentCaptor<ParticipantPlayer>()

            Mockito.doAnswer{
                println("AI: OO")
                openCloseGameServiceSpy.win = true
                "You WIN!!!"
            }.doAnswer{
                println("AI: CC")
                openCloseGameServiceSpy.win = true
                "You WIN!!!"
            }.doAnswer{
                println("AI: CO")
                openCloseGameServiceSpy.win = true
                "You WIN!!!"
            }.whenever(openCloseGameServiceSpy).play(predictorPlayer.capture(), participantPlayer.capture())

            openCloseGameServiceSpy.startGame()

            verify(openCloseGameServiceSpy, times(3)).play(any(), any())


            assertEquals("Welcome to the game!\n" +
                    "You are the predictor, what is your input?\n" +
                    "You: OC3\n" +
                    "AI: OO\n" +
                    "You WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "Y\n"+
                    "You are the predictor, what is your input?\n" +
                    "You: OO2\n" +
                    "AI: CC\n" +
                    "You WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "Y\n"+
                    "You are the predictor, what is your input?\n" +
                    "You: CO2\n" +
                    "AI: CO\n" +
                    "You WIN!!!\n" +
                    "Do you want to play again?\n" +
                    "N", outputStreamCaptor.toString().trim())
        }
    }
}