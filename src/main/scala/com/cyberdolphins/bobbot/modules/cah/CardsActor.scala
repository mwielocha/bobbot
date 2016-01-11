package com.cyberdolphins.bobbot.modules.cah

import akka.actor.{ActorLogging, Actor}
import akka.actor.Actor.Receive
import com.cyberdolphins.slime.incoming._
import com.cyberdolphins.slime.outgoing._
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Random}

/**
  * Created by mwielocha on 10/01/16.
  */
class CardsActor extends Actor with ActorLogging {
  implicit class PrettyString(s: String) {

    val replacements = Map(
      ".." -> ".",
      ".?" -> "?",
      ".*."-> "*.",
      ".*,"-> "*,",
      ".*:"-> "*:",
      ".*?"-> "*?"
    )

    def prettify = {
      replacements.foldLeft(s) {
        case (o, (l, r)) => o.replaceAllLiterally(l, r)
      }.replaceAll("\\.\\*\\s([a-z])", "* $1")
    }
  }

  val cards = Json.parse {
    Source.fromInputStream {
      Thread.currentThread()
        .getContextClassLoader
        .getResourceAsStream("cah_cards.json")
    }.mkString
  }.as[List[Card]]

  val whites = cards.filter(_.color == "white")
  val blacks = cards.filter(_.color == "black")

  override def receive: Actor.Receive = {

    case SimpleInboundMessage("cardsagainsthumanity", channel, user) =>

      Random.shuffle(blacks).head match {
        case Card(_, _, text, size) =>

          val randomized = Random.shuffle(whites).take(size)

          @tailrec
          def compile(output: String, pending: List[Card]): String = {
            pending match {
              case head +: tail if output.contains("%s") =>
                compile(output.replaceFirst("%s", s"*${head.text}*"), tail)
              case _ => (output +: pending.map(_.text)).mkString("\n")
            }
          }

          val output = Try(compile(text, randomized)) match {
            case Success(out) => out
            case Failure(ex) =>
              log.error(ex, s"Me not worky: $text, $randomized")
              ":("
          }

          sender ! SimpleOutboundMessage(
            output.prettify, channel, user
          )
      }
  }
}
