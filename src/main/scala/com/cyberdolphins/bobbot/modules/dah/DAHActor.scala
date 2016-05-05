package com.cyberdolphins.bobbot.modules.dah

import akka.actor.{ActorLogging, Actor}
import akka.actor.Actor.Receive
import com.cyberdolphins.slime.incoming._
import com.cyberdolphins.slime.outgoing._
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Random}
import java.util.UUID

/**
 * Created by Mikolaj Wielocha on 05/05/16
 */

case class Card(id: UUID, text: List[String]) {

  def size = math.max(0, text.size - 1)

  def compile(responses: List[String]): String = {

    val zero = List.empty[String]

    text.zip(responses :+ "").foldLeft(zero) {
      case (acc, (call, resp)) => acc :+ (call + resp)
    }.mkString
  }
}

object Card {

  implicit val reads = Json.reads[Card]

}

class DAHActor extends Actor with ActorLogging {

  def loadCards(resource: String): List[Card] = {
    Json.parse {
      Source.fromInputStream {
        Thread.currentThread()
          .getContextClassLoader
          .getResourceAsStream(resource)
      }.mkString
    }.as[List[Card]]
  }

  val calls = loadCards("dah_calls.json")
  val resps = loadCards("dah_resps.json")

  override def receive: Actor.Receive = {

    case SimpleInboundMessage("developersagainsthumanity", channel, user) =>

      val call = Random.shuffle(calls).head

      val respons = Random.shuffle(resps).take(call.size)

      sender ! SimpleOutboundMessage(
        call.compile(respons.flatMap(_.text)), channel, user
      )
  }
}
