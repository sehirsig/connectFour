package de.htwg.se.ConnectFour.aUI

;

import de.htwg.se.ConnectFour.aUI.states.GUI.{DropState, GameState}
import de.htwg.se.ConnectFour.controller.impl.ControllerImpl
import de.htwg.se.ConnectFour.util.{Observer, UI}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Button, TextInputDialog}
import scalafx.scene.effect.{Glow, InnerShadow}
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.layout._
import scalafx.scene.paint.Color.{Black, DarkRed, LightYellow, Red, Yellow}
import scalafx.scene.paint.{LinearGradient, Stops}
import scalafx.scene.text.Text

case class GUI(controller: ControllerImpl) extends UI with Observer with JFXApp {
  controller.add(this)
  var gameState: GameState = GameState(controller, this)

  def run(): Unit = {
    main(Array())
  }

  def typeName(): Unit = {
    val dialog = new TextInputDialog(defaultValue = "Detlef") {
      initOwner(stage)
      title = "ConnectFour Game"
      headerText = "Welcome to Connect Four!"
      var number = ""
      if (controller.players.size == 0) number = "one" else number = "two"
      contentText = "Player " + number + " please enter your name:"
    }

    val result = dialog.showAndWait()
    result match {
      case Some(name) => controller.addPlayer(name)
      case None => println("Please type a name!")
    }
  }

  def begin(): Unit = {
    do {
      val playerSize = controller.players.size
      playerSize match {
        case 0 => typeName()
        case 1 => typeName()
      }
    } while (controller.players.size < 2)
    execute("") // changing the state
  }


  def gameFieldButton(x: Int, y: Int): Button = {
    val gameFieldButton = new Button {
      style = "-fx-font: normal bold 16pt sans-serif;  -fx-border-color: lightgrey; -fx-text-fill: black; -fx-background-color: #e6f3ff;"
      onMouseClicked = _ => {
        gameState.handle(y.toString)
      }
    }
    gameFieldButton
  }

  val gameLogo: HBox = new HBox {
    style = "-fx-background-color: #b3daff;"
    padding = Insets(30, 100, 0, 100)
    children = Seq(
      new Text {
        text = "Connect "
        style = "-fx-font: normal bold 70pt sans-serif"
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(Red, DarkRed))
        effect = new InnerShadow() {
          color = Black
          radius = 5
        }
      },
      new Text {
        text = "Four"
        style = "-fx-font: normal bold 70pt sans-serif"
        fill = new LinearGradient(
          endX = 0,
          stops = Stops(Yellow, LightYellow)
        )
        effect = new InnerShadow() {
          color = Black
          radius = 5
        }
      },
    )
  }

  val gameGrid: GridPane = new GridPane {
    gridLinesVisible = false
    padding = Insets(70)
    var i = 0
    for (_ <- 0 until controller.colCount) {
      if (i < 6) {
        val row = new RowConstraints() {
          percentHeight = 100.0 / controller.rowCount
        }
        rowConstraints.add(row)
      }
      val column = new ColumnConstraints() {
        percentWidth = 100.0 / controller.colCount
      }
      columnConstraints.add(column)
      i += 1
    }
  }

  val bottombar: GridPane = new GridPane {
    padding = Insets(20)
    val row: RowConstraints = new RowConstraints() {
      percentHeight = 100
      prefHeight = 60
    }
    rowConstraints.add(row)

    for (_ <- 0 until 3) {
      val col = new ColumnConstraints() {
        percentWidth = 80
      }
      columnConstraints.add(col)
    }

    val undo = new Button("Undo") {
      padding = Insets(10)
      style = "-fx-font: normal bold 16pt sans-serif; -fx-text-fill: black; -fx-background-color: #e6f3ff; -fx-background-radius: 15px;"
      this.setMaxSize(Double.MaxValue, Double.MaxValue)
      onMouseClicked = _ => {
        execute("u")
      }
      onMouseEntered = _ => effect = new Glow(0.7)
      onMouseExited = _ => effect = null
    }

    val redo = new Button("Redo") {
      padding = Insets(10)
      style = "-fx-font: normal bold 16pt sans-serif; -fx-text-fill: black; -fx-background-color: #e6f3ff; -fx-background-radius: 15px;"
      this.setMaxSize(Double.MaxValue, Double.MaxValue)
      onMouseClicked = _ => {
        execute("r")
      }
      onMouseEntered = _ => effect = new Glow(0.7)
      onMouseExited = _ => effect = null
    }

    val newGame = new Button("New Game") {
      padding = Insets(10)
      style = "-fx-font: normal bold 16pt sans-serif; -fx-text-fill: black; -fx-background-color: #e6f3ff; -fx-background-radius: 15px;"
      this.setMaxSize(Double.MaxValue, Double.MaxValue)
      onMouseClicked = _ => {
        execute("n")
      }
      onMouseEntered = _ => effect = new Glow(0.7)
      onMouseExited = _ => effect = null
    }
    add(undo, 0, 0)
    add(redo, 1, 0)
    add(newGame, 2, 0)
  }

  stage = new PrimaryStage {
    title.value = "ConnectFour Game"
    minWidth = 650
    minHeight = 900
    resizable = true
    scene = new Scene {
      root = new BorderPane {
        style = "-fx-border-color: #353535; -fx-background-color: #b3daff;"
        top = gameLogo
        center = gameGrid
        bottom = bottombar
      }
      begin()
    }
  }

  def refreshView(): Unit = {
    try {
      for (x <- 0 to controller.colCount - 1; y <- (0 to controller.rowCount - 1).reverse) {
        var reverseY = y match {
          case 0 => 5
          case 1 => 4
          case 2 => 3
          case 3 => 2
          case 4 => 1
          case 5 => 0
        }
        val piece: Button = gameFieldButton(y, x)
        if (controller.grid.cell(y, x).isSet) {
          val img = controller.grid.cell(y, x).piece.get.player.playerNumber match {
            case 1 => new Image("/red.png")
            case 2 => new Image("/yellow.png")
          }
          val imgView = new ImageView(img)
          imgView.setFitHeight(35)
          imgView.setFitWidth(35)
          imgView.setPreserveRatio(true)
          piece.setGraphic(imgView)
          piece.setMaxSize(Double.MaxValue, Double.MaxValue)
        }
        piece.setMaxSize(Double.MaxValue, Double.MaxValue)
        gameGrid.add(piece, x, reverseY)
      }
    } catch {
      case e => print(e)
    }
  }

  override def processInput(input: String):Unit = {
    input match {
      case _ => execute(input);
    }
  }

  def execute(input:String): Unit = {
    gameState.handle(input)
  }

  override def update: Boolean = {
    refreshView()
    true
  }
}