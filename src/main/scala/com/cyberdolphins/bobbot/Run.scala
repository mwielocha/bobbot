package com.cyberdolphins.bobbot

import akka.actor.{ActorSystem, Props}
import com.cyberdolphins.slime.SlackBotActor.{Close, Connect}

/**
  * Created by mwielocha on 09/01/16.
  */
object Run extends App {

  val actorSystem = ActorSystem()

  val slimeBotActor = actorSystem.actorOf(Props[BobBotActor], "cahsb")

  slimeBotActor ! Connect(System.getenv("SLACK_API_KEY"))

  sys.addShutdownHook {
    slimeBotActor ! Close
    actorSystem.shutdown()
    actorSystem.awaitTermination()
  }

  actorSystem.awaitTermination()

}
