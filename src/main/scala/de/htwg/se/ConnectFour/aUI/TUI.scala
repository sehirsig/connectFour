package de.htwg.se.ConnectFour.aUI

import de.htwg.se.ConnectFour._
import de.htwg.se.ConnectFour.controller.Controller
import de.htwg.se.ConnectFour.model.Piece
import de.htwg.se.ConnectFour.util.Observer
import io.AnsiColor._


class TUI(controller: Controller) extends Observer{

  def processInputLine(input: String):Unit = {
    val Pattern = "([0-6])".r
    input match {
      case "p" => print(controller.gridPrint)
      case "u" => controller.undoStep()
      case "r" => controller.redoStep()
      case Pattern(input) => controller.doStep(input)
      case _ => println("Invalid input.");
    }
  }

  def gamestart(): Unit ={

    println(Console.BLUE + s"${BOLD}◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙" + Console.RED + " WELCOME TO CONNECT FOUR " + Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println("◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙" + Console.YELLOW + " You can start playing by typing a column number 0-6 " + Console.BLUE + "◙")
    println(Console.BLUE + "◙◙◙" + Console.YELLOW + " There is an option 'u' for undo and 'r' for redo " + Console.BLUE + "◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙" + Console.YELLOW + " Your turns are always alternating " + Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙" + Console.GREEN + " With typing 'q' you can quit " + Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙" + Console.MAGENTA + " Good luck and have fun! " + Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
    println(Console.BLUE + "◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙◙")
  }
  override def update: Boolean =  { println(controller.toString);true }
}
