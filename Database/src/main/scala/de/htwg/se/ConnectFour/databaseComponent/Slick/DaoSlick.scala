package de.htwg.se.ConnectFour.databaseComponent.Slick

import de.htwg.se.ConnectFour.databaseComponent.DatabaseInterface
import de.htwg.se.ConnectFour.databaseComponent.Slick.tables.PlayerTable
import de.htwg.se.ConnectFour.model.playerComponent.Player
import slick.jdbc.JdbcBackend.Database
import slick.lifted.TableQuery
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.{Duration, DurationInt}
import scala.concurrent.{Await, Future}
import scala.io.StdIn
import scala.util.{Failure, Success}

object DaoSlick extends DatabaseInterface {

  val connectIP = sys.env.getOrElse("SLICK_SERVICE_HOST", "localhost").toString
  val connectPort = sys.env.getOrElse("SLICK_SERVICE_PORT", 5432).toString.toInt
  val database_user = sys.env.getOrElse("SLICK_USER", "connectfour").toString
  val database_pw = sys.env.getOrElse("SLICK_USER_PW", "connectfour").toString

  val database =
    Database.forURL(
      url = "jdbc:postgresql://localhost:5432/postgres?serverTimezone=UTC",
      user = database_user,
      password = database_pw,
      driver = "org.postgresql.Driver")

  val playerTable = TableQuery[PlayerTable]

  val setup = DBIO.seq(
    playerTable.schema.createIfNotExists
  )
  val run = Future(database.run(setup))

  run.onComplete {
    case Success(_) => print("Started successfully!")
    case _ => print("Something failed!")
  }

  override def create(): Unit = {
    val d = Future(Await.result(database.run(setup), atMost = 10.second))
    d.onComplete {
      case Success(_) => print("Created successfully!")
      case _ => print("Creating failed!")
    }
  }

  override def read(playerId: Int): Option[(Int, Int, Option[String], String)] = {
    val action = playerTable.filter(_.id === playerId).result
    val result = Await.result(database.run(action), atMost = 10.second)
    result match {
      case Seq(a) => Some((a._1, a._2, a._3, a._4))
      case _ => None
    }
  }

  override def update(id: Int, game: Future[String]) = {

  }

  override def deletePlayer(num:Int): Future[Any] = {
    val action = playerTable.filter(_.id === num ).delete
    Future(Await.result(database.run(action), atMost = 10.second))
  }


  override def create(player: Player): Int = {
    val playerIDQuery = (playerTable returning playerTable.map(_.id)) += ((player.playerNumber, player.playerNumber, player.color, player.playerName))
    val playerID = Await.result(database.run(playerIDQuery), Duration("10s"))
    player.playerNumber
  }

}