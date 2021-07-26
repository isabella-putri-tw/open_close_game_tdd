import model.AIPlayer
import model.HumanPlayer
import services.OpenCloseGameService

fun main() {
    val openCloseGameService = OpenCloseGameService(listOf(HumanPlayer(), AIPlayer()))
    openCloseGameService.startGame()
}