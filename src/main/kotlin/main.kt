import model.AIPlayer
import model.Game
import model.HumanPlayer
import services.GameService

fun main() {
    val openCloseGameService = GameService()
    openCloseGameService.startGame(Game(listOf(HumanPlayer(), AIPlayer())))
}