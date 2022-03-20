package de.htwg.se.ConnectFour

import com.google.inject.{Guice, Injector}
import de.htwg.se.ConnectFour.aUI.UIFactory
import de.htwg.se.ConnectFour.controller.Controller

import scala.util.{Failure, Success, Try}

case object Game {
  def main(args: Array[String]): Unit = {
    val injector: Injector = Guice.createInjector(new GameModule())
    val controller = injector.getInstance(classOf[Controller])
    val uiType = "gui"

    Try(UIFactory(uiType,controller)) match {
      case Success(v) => println("See you next time! Bye.")
      case Failure(v) => println("Could not create UI" + v.getMessage + v.getCause)
    }
  }
}