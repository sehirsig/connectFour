package de.htwg.se.ConnectFour
import com.google.inject.AbstractModule
import de.htwg.se.ConnectFour.controller.Controller
import de.htwg.se.ConnectFour.controller.impl.ControllerImpl
import de.htwg.se.ConnectFour.model.grid.Grid
import de.htwg.se.ConnectFour.model.grid.impl.GridImpl
import de.htwg.se.ConnectFour.model.player.PlayerBuilder
import de.htwg.se.ConnectFour.model.player.impl.PlayerBuilderImpl
import net.codingwell.scalaguice.ScalaModule

class GameModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
      bind[Grid].to[GridImpl]
      bind[Controller].to[ControllerImpl]
  }
}
