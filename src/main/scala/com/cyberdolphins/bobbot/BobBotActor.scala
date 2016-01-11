package com.cyberdolphins.bobbot

import akka.actor.Props
import com.cyberdolphins.bobbot.modules.cah.{CardsActor, Card}
import com.cyberdolphins.bobbot.modules.release.ReleaseActor
import com.cyberdolphins.slime.SlackBotActor
import com.cyberdolphins.slime.incoming._
import com.cyberdolphins.slime.outgoing._
import play.api.libs.json.Json

import scala.annotation.tailrec
import scala.io.Source
import scala.util.{Failure, Success, Try, Random}

/**
  * Created by mwielocha on 09/01/16.
  */
class BobBotActor extends SlackBotActor {

  val modules: Seq[Class[_]] = Seq(
    classOf[CardsActor],
    classOf[ReleaseActor]
  )

  val actors = modules.map { m =>
    context.system.actorOf(Props(m))
  }

  override def eventReceive: EventReceive = {
    case m: Message => actors.foreach(_ ! m)
  }
}
