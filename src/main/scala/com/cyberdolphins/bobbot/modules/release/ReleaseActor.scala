package com.cyberdolphins.bobbot.modules.release

import akka.actor.Actor.Receive
import akka.actor.{ActorLogging, Actor}
import com.cyberdolphins.slime.incoming._
import com.cyberdolphins.slime.outgoing._
import org.joda.time.DateTime

/**
  * Created by mwielocha on 11/01/16.
  */
class ReleaseActor extends Actor with ActorLogging {

  def isAfter5pm = DateTime.now().hourOfDay().get > 17

  override def receive: Receive = {
    case SimpleInboundMessage(text, channel, user)
      if (text.toLowerCase.startsWith("releasing") ||
        text.toLowerCase.startsWith("swarming")) && isAfter5pm  =>

        sender ! SimpleOutboundMessage("Wow there cowboy, releasing after *5 pm*, are you mad bro?", channel, user)
  }
}
