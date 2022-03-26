package de.htwg.se.ConnectFour.aUI
import de.htwg.se.ConnectFour.aUI.*
import de.htwg.se.ConnectFour.controller.Controller
import javafx.embed.swing.JFXPanel

object UIFactory:
  def apply(uiType:String, controller:Controller) =
    uiType.toLowerCase() match
      case "gui" =>
        JFXPanel()
        GUI(controller).run()
      case "tui" => TUI(controller).run()