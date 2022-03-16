package de.htwg.se.ConnectFour.controller.impl

import com.google.inject.{Guice, Inject, Injector}
import de.htwg.se.ConnectFour.GameModule
import de.htwg.se.ConnectFour.controller.Controller
import de.htwg.se.ConnectFour.model.fileio.FileIO
import de.htwg.se.ConnectFour.model.grid.Grid
import de.htwg.se.ConnectFour.model.grid.impl.Piece
import de.htwg.se.ConnectFour.model.player.{Player, PlayerBuilder}
import de.htwg.se.ConnectFour.util.UndoManager
import net.codingwell.scalaguice.InjectorExtensions.ScalaInjector

/**
 *  Controller implementation
 */
class ControllerImpl @Inject () (var grid:Grid, val playerBuilder:PlayerBuilder) extends Controller {
  val injector: Injector = Guice.createInjector(new GameModule)
  val fileIo: FileIO = injector.instance[FileIO]
  var players: Vector[Player] = Vector.empty
  var moveCount = 0
  var currentPlayer:Player = _
  val maxPlayers = 2
  val colCount = 7
  val rowCount = 6
  override val undoManager: UndoManager = new UndoManager

  override def createGrid(): Unit = {
    reset()
    notifyObservers
  }

  override def addPlayer(name:String):Unit = {
    if (players.size == 0) {
      val player = playerBuilder.createPlayer(name,1)
      players = players.appended(player)
      println("Player 1 is called: " + name)
    }
    else {
      val player = playerBuilder.createPlayer(name,2)
      players = players.appended(player)
      println("Player 2 is called: " + name)
    }
  }

  override def whoseTurnIsIt(): Unit = {
    currentPlayer = if (moveCount % 2 == 0) players(0) else players(1)
    notifyObservers
  }

  override def checkWin():Boolean = {
    val checkList:List[Option[Boolean]] = List(winPatternHorizontal(),winPatternVertical(),winPatternAscendingDiagonal(),winPatternDescendingDiagonal())
    val win = checkList.filterNot(f => f.isEmpty).contains((Some(true)))
    if (win)
      return true
    false
  }

  override def winPatternHorizontal():Option[Boolean] = {
    try {
      val currentPiece = Some(Piece(currentPlayer))
      for (i <- 0 to rowCount - 1) {
        for (j <- 0 to colCount - 3) {
          if (grid.cell(i, j).piece == currentPiece && grid.cell(i, j + 1).piece == currentPiece && grid.cell(i, j + 2).piece == currentPiece && grid.cell(i, j + 3).piece == currentPiece)
            return Some(true)
        }
      }
      None
    } catch {
      case e: Exception => None
    }
  }


  override def winPatternVertical():Option[Boolean] = {
    try {
      val currentPiece = Some(Piece(currentPlayer : Player))
      for (i <- 0 to rowCount - 3) {
        for (j <- 0 to colCount - 1) {
          if (grid.cell(i, j).piece == currentPiece && grid.cell(i + 1, j).piece == currentPiece && grid.cell(i + 2, j).piece == currentPiece && grid.cell(i + 3, j).piece == currentPiece)
            return Some(true)
        }
      }
      None
    } catch {
      case e: Exception => None
    }
  }
  override def winPatternAscendingDiagonal():Option[Boolean] = {
    try {
      val currentPiece = Some(Piece(currentPlayer))
      for (i <- 0 to rowCount-4){
        for (j <- 0 to colCount-4){
          if (grid.cell(i,j).piece == currentPiece && grid.cell(i+1,j+1).piece == currentPiece && grid.cell(i+2,j+2).piece == currentPiece && grid.cell(i+3,j+3).piece == currentPiece)
            return Some(true)
        }
      }
      None
    } catch {
      case e: Exception => None
    }
  }
  override def winPatternDescendingDiagonal():Option[Boolean] = {
    try {
      val currentPiece = Some(Piece(currentPlayer))
      for (i <- 3 to rowCount - 1) {
        for (j <- 0 to colCount - 4) {
          if (grid.cell(i, j).piece == currentPiece && grid.cell(i - 1, j + 1).piece == currentPiece && grid.cell(i - 2, j + 2).piece == currentPiece && grid.cell(i - 3, j + 3).piece == currentPiece)
            return Some(true)
        }
      }
      None
    } catch {
      case e: Exception => None
    }
  }

  override def drop(col:String): Unit = {
    whoseTurnIsIt()
    var validCol = 0
    if (col.toInt <= 6)
      validCol = col.toInt
    undoManager.doStep(new SetCommandImpl(validCol,Piece(currentPlayer),this));
    moveCount += 1
    notifyObservers
  }

  override def undoDrop(): Unit = {
    undoManager.undoStep
    moveCount -= 1
    notifyObservers
  }

  override def redoDrop(): Unit = {
    undoManager.redoStep
    moveCount += 1
    notifyObservers
  }

  override def saveGame(): Unit = {
    fileIo.save(this)
    notifyObservers
  }

  override def loadGame(): Unit = {
    fileIo.load(this)
    notifyObservers
  }

  override def reset(): Unit = {
    grid = grid.reset()
    notifyObservers
  }

  override def gridToString(): String = this.grid.toString
  override def getGrid(): Grid = this.grid
  override def getPlayers(): Vector[Player] = this.players
  override def getCurrentPlayer(): Player = this.currentPlayer
  override def getMoveCount: Int = this.moveCount
  override def setGrid(grid: Grid): Unit = this.grid = grid;notifyObservers
  override def setCurrentPlayer(currentPlayer: Player): Unit = this.currentPlayer = currentPlayer;notifyObservers
  override def setMoveCount(moveCount:Int): Unit = this.moveCount = moveCount;notifyObservers
}

