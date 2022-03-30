package de.htwg.se.ConnectFour.model.fileio.json

import com.google.inject.Inject
import de.htwg.se.ConnectFour.controller.Controller
import de.htwg.se.ConnectFour.model.fileio.FileIO
import de.htwg.se.ConnectFour.model.grid.{Cell, Grid, Piece}
import play.api.libs.json._

import java.io.{File, PrintWriter}
import scala.io.Source

/**
 * FileIO implementation
 * for exporting the game as JSON File
 */
class FileIOImpl @Inject () extends FileIO:

  override def load(controller:Controller) =
    val source: String = Source.fromFile("game.json").getLines.mkString
    val gameJson: JsValue = Json.parse(source)
    val grid = (gameJson \ "grid")
    val moveCount = (gameJson \ "player" \ "moveCount" \ "value").get.toString().toInt
    val currentPlayer = (gameJson \ "player" \ "currentPlayer").get.toString()
    val player1 = (gameJson \ "player" \ "player1").get.toString()
    val player2 = (gameJson \ "player" \ "player2").get.toString()


    var newGrid: Grid = controller.getGrid()
    controller.setMoveCount(moveCount)
    controller.addPlayer(player1)
    controller.addPlayer(player2)
    currentPlayer match
      case player1 => controller.setCurrentPlayer(controller.players(0))
      case player2 => controller.setCurrentPlayer(controller.players(1))

    val cells = (grid \ "cells").as[JsArray]
    for (cell <- cells.value) do
      val row = (cell \ "row").get.as[Int]
      val col = (cell \ "col").get.as[Int]
      val value = (cell \ "value").get.as[Int]

      val optPiece = value match
        case 1 => Some(Piece(controller.players(0)))
        case 2 => Some(Piece(controller.players(1)))
        case _ => None
      newGrid = newGrid.replaceCell(row, col, Cell(optPiece))

    controller.setGrid(newGrid)

  def gameToJson(controller: Controller): JsValue =
    Json.obj(
      "player" -> Json.obj(
        "moveCount" -> Json.obj(
          "value"  -> controller.moveCount
        ),
        "currentPlayer" -> Json.obj(
          "name" -> controller.currentPlayer.playerName
        ),
        "player1" -> Json.obj(
          "name" -> controller.players(0).playerName
        ),
        "player2" -> Json.obj(
          "name" -> controller.players(1).playerName
        )
      ),
      "grid" -> Json.obj(
        "cells" -> Json.toJson(
          for {col <- 0 to controller.getGrid().colCount - 1;
               row <- (0 to controller.getGrid().rowCount - 1).reverse
               }
          yield {

            var player = controller.getGrid().cell(row, col).piece match
                case Some(s) => s.player.playerNumber
                case None => -1
                
            Json.obj(
              "row" -> row,
              "col" -> col,
              "value" -> player
            )
          }
        )
      )
    )

  override def save(game: Controller) =
    val pw = PrintWriter(File("game.json"))
    pw.write(Json.prettyPrint(gameToJson(game)))
    pw.close