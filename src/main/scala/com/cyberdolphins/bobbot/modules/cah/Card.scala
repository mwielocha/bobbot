package com.cyberdolphins.bobbot.modules.cah

import play.api.libs.json.{JsResult, JsSuccess, JsValue, Reads}

/**
  * Created by mwielocha on 09/01/16.
  */


/*
  "expansion":"Original",
	"color":"black",
	"text":"Who stole the cookies from the cookie jar?"
 */

case class Card(expansion: String, color: String, text: String, size: Int = 1)

object Card {

  implicit val reads = new Reads[Card] {
    override def reads(json: JsValue): JsResult[Card] = {
      JsSuccess(
        Card(
          (json \ "expansion").as[String],
          (json \ "color").as[String],
          (json \ "text").as[String],
          (json \ "size").asOpt[Int].getOrElse(1)
        )
      )
    }
  }
}
