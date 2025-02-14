package de.htwg.se.ConnectFour.model.gridComponent.gridBaseImpl

import com.google.inject.Inject
import de.htwg.se.ConnectFour.model.gridComponent.gridBaseImpl.Prototype.GridDefaultPrototype
import de.htwg.se.ConnectFour.model.gridComponent.{Cell, GridInterface, Piece}
import de.htwg.se.ConnectFour.model.playerComponent.PlayerInterface
import netscape.javascript.JSObject
import play.api.libs.json.{JsArray, JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.io.Source
import scala.util.{Failure, Success, Try}

/**
 * Game grid implementation
 */
case class Grid(rows: Vector[Vector[Cell]]) extends GridInterface :

  override val colCount = 7
  override val rowCount = 6

  @Inject()
  def this() = this(Vector.tabulate(6, 7) { (rowCount, col) => Cell(None) })

  /** Drop a Piece into the grid */
  override def drop(column: Int, piece: Piece): Grid =
    val idx = this.rows.indexWhere(row => !row(column).isSet)
    if idx > -1 then
      return this.replaceCell(idx, column, Cell(Some(piece)))
    this

  override def replaceCell(row: Int, col: Int, cell: Cell): Grid = copy(rows.updated(row, rows(row).updated(col, cell)))

  override def reset(): Grid =
    defaultGrid.cloneGrid()

  /** Performance Boost, copy, instead of new */
  def defaultGrid: GridPrototype = GridDefaultPrototype()

  /** Check if someone has won. */
  override def checkWin(currentPlayer: PlayerInterface): Boolean =
    Try(checkWinTry(currentPlayer)) match
      case Success(v) => v
      case Failure(_) => false

  /** CheckWin which gets called by a "Try" to prevent exceptions.
   * Future to allow asynchronous pattern matching, enhancing performance.
   * */
  def checkWinTry(currentPlayer: PlayerInterface): Boolean =
    val playerPiece = Some(Piece(currentPlayer))

    val horizontal = winPattern(playerPiece)(rowCount - 1, colCount - 4, (0, 1))
    val vertical = winPattern(playerPiece)(rowCount - 4, colCount - 1, (1, 0))
    val ascendingDiagonal = winPattern(playerPiece)(rowCount - 4, colCount - 4, (1, 1))
    val descendingDiagonal = winPattern(playerPiece)(rowCount - 1, colCount - 4, (-1, 1), 3)

    val result: Future[(Option[Boolean], Option[Boolean], Option[Boolean], Option[Boolean])] = for {
      hor <- horizontal
      vert <- vertical
      asc <- ascendingDiagonal
      desc <- descendingDiagonal
    } yield (hor, vert, asc, desc)

    val x = Await.result(result, Duration.Inf)
    val checkList: List[Option[Boolean]] = List(x._1, x._2, x._3, x._4)

    //Check ob es einen Win gab
    checkList.filterNot(_.isEmpty).contains(Some(true))

  /** winPattern starts a recursive Method, to check if someone won.
   * As Future, so multiple winPattern can run at once.
   * */
  def winPattern(currentPiece: Option[Piece])(rowMax: Int, colMax: Int, chipSet: (Int, Int), rowMin: Int = 0, colMin: Int = 0): Future[Option[Boolean]] =
  //idx = rowMin für descendingDiagonal, damit idx bei rowMin anfängt!
    Future {
      if goThroughRow(currentPiece)(rowMin, rowMax, colMin, colMax, rowMin, chipSet) == Some(true) then
        Some(true)
      else
        None
    }

  /** goThroughRow recursively visits every row and calls every column. */
  def goThroughRow(currentPiece: Option[Piece])(rowMin: Int, rowMax: Int, colMin: Int, colMax: Int, idx: Int, chipSet: (Int, Int)): Option[Boolean] =
    if rowMax < idx || rowMin > idx then
      Some(false)
    else if goThroughCol(currentPiece)(colMin, colMax, 0, idx, chipSet) == Some(true) then
      Some(true)
    else if goThroughRow(currentPiece)(rowMin, rowMax, colMin, colMax, idx + 1, chipSet) == Some(true) then
      Some(true)
    else
      Some(false)

  /** goThroughCol recursively goes through every column and checks for a win with checkP */
  def goThroughCol(currentPiece: Option[Piece])(min: Int, max: Int, idx: Int, rowIdx: Int, chipSet: (Int, Int)): Option[Boolean] =
    if (max < idx) || (min > idx) then
      Some(false)
    else if checkP(currentPiece)((rowIdx, idx), chipSet) then
      Some(true)
    else
      goThroughCol(currentPiece)(min, max, idx + 1, rowIdx, chipSet)

  /** checkP looks if 4 Pieces are from the same person, if so returns true. */
  def checkP(currentPiece: Option[Piece])(firstChip: (Int, Int), chipSet: (Int, Int)): Boolean =
    (0 to 3).map(x => {
      val c = firstChip.add(chipSet.multi(x))
      this.cell(c._1, c._2).piece == currentPiece
    }).forall(_ == true)

  /** Extensions for tuples, for the checkP method. */
  extension (b: (Int, Int))
    def add(c: (Int, Int)): (Int, Int) =
      (b._1 + c._1, b._2 + c._2)
    def multi(d: Int): (Int, Int) =
      (b._1 * d, b._2 * d)

  override def toJsonString(moveCount: Int, curPlayerName: String, player1Name: String, player2Name: String): String =
    Json.prettyPrint(gameToJson(moveCount, curPlayerName, player1Name, player2Name))

  def gameToJson(moveCount: Int, curPlayerName: String, player1Name: String, player2Name: String): JsValue =
    Json.obj(
      "player" -> Json.obj(
        "moveCount" -> Json.obj(
          "value" -> moveCount
        ),
        "currentPlayer" -> Json.obj(
          "name" -> curPlayerName
        ),
        "player1" -> Json.obj(
          "name" -> player1Name
        ),
        "player2" -> Json.obj(
          "name" -> player2Name
        )
      ),
      "grid" -> Json.obj(
        "cells" -> Json.toJson(
          (0 to this.colCount - 1).flatMap(col =>
            (0 to this.rowCount - 1).reverse.map(row => {
              val player = this.cell(row, col).piece match
                case Some(s) => s.player.playerNumber
                case None => -1
              Json.obj(
                "row" -> row,
                "col" -> col,
                "value" -> player
              )
            }))
        )
      )
    )

  override def cell(row: Int, col: Int): Cell = rows(row)(col)

  override def toJson(moveCount: Int, curPlayerName: String, player1Name: String, player2Name: String): JsValue =
    gameToJson(moveCount, curPlayerName, player1Name, player2Name)

  override def jsonToGrid(player1: PlayerInterface, player2: PlayerInterface, par_grid: GridInterface, source: String): GridInterface =
    val gameJson: JsValue = Json.parse(source)
    val grid = (gameJson \ "grid")

    val cells = (grid \ "cells").as[JsArray]
    recursiveSetGrid(player1, player2, cells, 0, par_grid)

  def recursiveSetGrid(player1: PlayerInterface, player2: PlayerInterface, cells: JsArray, idx: Int, grid: GridInterface): GridInterface =
    if cells.value.length == idx then
      return grid

    val cell = cells.value(idx)

    val row = (cell \ "row").get.as[Int]
    val col = (cell \ "col").get.as[Int]
    val value = (cell \ "value").get.as[Int]
    val optPiece = value match
      case 1 => Some(Piece(player1))
      case 2 => Some(Piece(player2))
      case _ => None
    recursiveSetGrid(player1, player2, cells, idx + 1, grid.replaceCell(row, col, Cell(optPiece)))

  /** Plain String for representation in the TUI. */
  override def toPlainString: String =
    print(drawPlainString);
    this.rows.map(row => row.map(col => if col.isSet then return drawPlainString))
    ""

  /** Plain String for representation in the TUI. */
  def drawPlainString: String =
    val builder = new StringBuilder
    this.rows.reverse.map(row => {
      row.map(col => builder.append(col.toPlainString)); builder.append("\n")
    })
    builder.toString()

  override def toString: String =
    this.rows.map(row => row.map(col => if col.isSet then return drawString))
    ""

  def drawString: String =
    val builder = new StringBuilder
    this.rows.reverse.map(row => {
      row.map(col => builder.append(col)); builder.append("\n")
    })
    builder.toString()
