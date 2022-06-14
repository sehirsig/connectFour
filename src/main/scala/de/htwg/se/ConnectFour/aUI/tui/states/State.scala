package de.htwg.se.ConnectFour.aUI.tui.states

/**
 * State Trait
 * Implementation of State pattern in the TUI package
 */
trait State[T]:
  def handle(input: String, state: T): Unit