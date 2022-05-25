package de.htwg.se.ConnectFour.util

/**
 * Observer Trait
 * for the observer pattern
 */
trait Observer:
  def update: Boolean

class Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers=subscribers:+s
  def remove(s: Observer) = subscribers = subscribers.filterNot(_==s)
  def notifyObservers = subscribers.map(_.update)
