package services

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNull
import model.AIPlayer
import model.Game
import model.HumanPlayer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.reset
import org.mockito.kotlin.whenever
import utils.GameUtil
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.PrintStream


internal class GameServiceTest {

    @Nested
    inner class PlayOneRound() {

        @Spy
        lateinit var spyAIPlayer: AIPlayer

        @BeforeEach
        fun setUp() {
            MockitoAnnotations.openMocks(this)
        }

        @AfterEach
        fun tearDown() {
            reset(spyAIPlayer)
        }

        @Test
        fun `user lost when number of open hand is incorrect`() {
            val humanPlayer = HumanPlayer()
            val gameService = GameService()
            gameService.game = Game(listOf(humanPlayer, spyAIPlayer))

            doAnswer {
                spyAIPlayer.hands = "OC"
            }.whenever(spyAIPlayer).play(any())
            humanPlayer.enterInput("CO3")

            gameService.playOneRound()

            assertThat(gameService.game.winner).isNull()
        }

        @Test
        fun `user win when number of open hand is correct`() {
            val humanPlayer = HumanPlayer()
            val gameService = GameService()
            gameService.game = Game(listOf(humanPlayer, spyAIPlayer))

            doAnswer {
                spyAIPlayer.hands = "OO"
            }.whenever(spyAIPlayer).play(any())
            humanPlayer.enterInput("CO3")

            gameService.playOneRound()

            assertThat(gameService.game.winner).isEqualTo(humanPlayer)
        }

        @Test
        fun `no winner when not user's turn to guess and AI guess wrong`() {
            val humanPlayer = HumanPlayer()
            val gameService = GameService()
            gameService.game = Game(listOf(humanPlayer, spyAIPlayer), 1)

            doAnswer {
                spyAIPlayer.hands = "OC"
                spyAIPlayer.prediction = 2
            }.whenever(spyAIPlayer).play(any())
            humanPlayer.enterInput("CC")

            gameService.playOneRound()

            assertThat(gameService.game.winner).isNull()
        }

        @Test
        fun `AI wins when not user's turn to guess and AI guess wrong`() {
            val humanPlayer = HumanPlayer()
            val gameService = GameService()
            gameService.game = Game(listOf(humanPlayer, spyAIPlayer), 1)

            doAnswer {
                spyAIPlayer.hands = "OC"
                spyAIPlayer.prediction = 1
            }.whenever(spyAIPlayer).play(any())
            humanPlayer.enterInput("CC")

            gameService.playOneRound()

            assertThat(gameService.game.winner).isEqualTo(spyAIPlayer)
        }
    }

    @Nested
    inner class StartGame() {
        private val standardOut = System.out
        private val outputStreamCaptor = ByteArrayOutputStream()

        @Mock
        lateinit var mockReader: BufferedReader

        lateinit var gameService: GameService

        @Spy
        lateinit var spyAIPlayer: AIPlayer


        @BeforeEach
        fun setUp() {
            MockitoAnnotations.openMocks(this)
            System.setOut(PrintStream(outputStreamCaptor))
            gameService = GameService()
            gameService.reader = mockReader
            gameService.gameModerator.reader = mockReader
        }

        @AfterEach
        fun tearDown() {
            System.setOut(standardOut)
            reset(mockReader, spyAIPlayer)

        }

        @Test
        fun `should repeat ask input again when human input is incorrect`() {
            doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("O3", "OC3", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))

            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: O3\n" +
                        "Bad input: input should have 3 characters, e.g. OC2\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC3\n" +
                        "AI: OO\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "N"
            )
        }

        @Test
        fun `should ask to end game and end when user correct guess and end game`() {
            doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("OC3", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))

            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC3\n" +
                        "AI: OO\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "N"
            )
        }

        @Test
        fun `should repeat game when user wrong guess and then AI win`() {
            doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CO"
                spyAIPlayer.prediction = 1
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("OC2", "CC", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))

            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC2\n" +
                        "AI: OO\n" +
                        "No winner\n" +
                        "AI is the predictor, what is your input?\n" +
                        "You: CC\n" +
                        "AI: CO1\n" +
                        "AI WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "N"
            )
        }

        @Test
        fun `should repeat game 3 times when until user win`() {
            doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CO"
                spyAIPlayer.prediction = 2
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CC"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("OC2", "CC", "OO2", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))


            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
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
                        "N"
            )
        }

        @Test
        fun `should play thrice when user wants to play again twice`() {
            doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CC"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("OC3", "Y", "OO2", "Y", "CO2", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))

            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC3\n" +
                        "AI: OO\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "Y\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OO2\n" +
                        "AI: CC\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "Y\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: CO2\n" +
                        "AI: CO\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "N"
            )
        }

        @Test
        fun `should reset winner when after play again`() {
            doAnswer {
                spyAIPlayer.hands = "CO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "OO"
                GameUtil.printPlayerInput(spyAIPlayer)
            }.doAnswer {
                spyAIPlayer.hands = "CO"
                spyAIPlayer.prediction = 1
                GameUtil.printPlayerInput(spyAIPlayer)
            }.whenever(spyAIPlayer).play(any())

            Mockito.`when`(mockReader.readLine()).thenReturn("OC2", "Y", "OC2", "CC", "N")

            gameService.startGame(Game(listOf(HumanPlayer(), spyAIPlayer)))

            assertThat(outputStreamCaptor.toString().trim()).isEqualTo(
                "Welcome to the game!\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC2\n" +
                        "AI: CO\n" +
                        "You WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "Y\n" +
                        "You are the predictor, what is your input?\n" +
                        "You: OC2\n" +
                        "AI: OO\n" +
                        "No winner\n" +
                        "AI is the predictor, what is your input?\n" +
                        "You: CC\n" +
                        "AI: CO1\n" +
                        "AI WIN!!!\n" +
                        "Do you want to play again?\n" +
                        "N"
            )
        }
    }
}