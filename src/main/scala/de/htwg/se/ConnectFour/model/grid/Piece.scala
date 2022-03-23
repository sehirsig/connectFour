package de.htwg.se.ConnectFour.model.grid

import de.htwg.se.ConnectFour.model.player.Player

/**
 * Piece case class
 */
case class Piece(player: Player):
  override def toString: String =
    player.playerNumber match
      case 1 => Console.RED + "☻ "
      case 2 => Console.YELLOW + "☻ "
