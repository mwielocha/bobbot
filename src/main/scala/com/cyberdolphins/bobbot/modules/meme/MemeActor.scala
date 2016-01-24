package com.cyberdolphins.bobbot.modules.meme

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.cyberdolphins.slime.incoming._
import com.cyberdolphins.slime.outgoing._
import com.cyberdolphins.slime.common._
import com.ning.http.client.AsyncHttpClientConfig
import play.api.libs.ws.ning.NingWSClient
import play.api.libs.functional.syntax._
import play.api.libs.json._


/**
  * Created by mikwie on 12/01/16.
  */

case class Request(image: String, bottomText: String, topText: String)

object Request {

  implicit val writes = {
    ((__ \ "image").write[String] ~
      (__ \ "bottom_text").write[String] ~
      (__ \ "top_text").write[String]) (unlift(Request.unapply))
  }
}

class MemeActor extends Actor {

  import context.dispatcher

  private val httpClientBuilder = new AsyncHttpClientConfig.Builder()
  private val httpClient = new NingWSClient(httpClientBuilder.build())

  private val generatorUrl = "http://meme-generator.wmdev.org/generate.php"

  private val Command = ":([a-z]{1,})\\s(.*)".r

  import Request._

  override def receive: Receive = {

    case SimpleInboundMessage(Command(cmd, text), channel, user) =>

      val splitted = text.split("""\\n""")

      val request = Request(cmd,
        splitted.lift(1).getOrElse(""),
        splitted.headOption.getOrElse(""))

      val senderLocal = sender()

      httpClient.url(generatorUrl).post(Json.toJson(request)).map {
        response => if (response.status == 200) {
          val imageUrl = (response.json \ "image_url").as[String]
          senderLocal ! ComplexOutboundMessage(cmd, channel, user,
            Attachment("").withImageUrl(imageUrl))
        }
      }
  }
}
