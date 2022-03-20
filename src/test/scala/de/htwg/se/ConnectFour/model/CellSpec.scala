package de.htwg.se.ConnectFour.model

import de.htwg.se.ConnectFour.model.grid.Cell
import de.htwg.se.ConnectFour.model.player.impl.{PlayerBuilderImpl, PlayerImpl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CellSpec extends AnyWordSpec with Matchers {
  "A Cell" when {
    "not set to any value " should {
      val emptyCell = Cell(None)
      "have value 0" in {
        emptyCell.piece should be(None)
      }
      "not be set" in {
        emptyCell.isSet should be(false)
      }
      "have a nice String representation" in {
        emptyCell.toString should be (Console.BLUE + "_ ")
      }
    }
    "set to a specific value" should {
      val playerBuilder = PlayerBuilderImpl()
      val player = playerBuilder.createPlayer("Your Name",1)
      val nonEmptyCell = Cell(Some(grid.Piece(player)))
      "return that value" in {
        nonEmptyCell.piece should be(Some(grid.Piece(PlayerImpl("Your Name",1))))
      }
      "be set" in {
        nonEmptyCell.isSet should be(true)
      }
      "have a nice String representation" in {
        nonEmptyCell.toString should be (Console.RED + "☻ ")
      }
    }
  }
}