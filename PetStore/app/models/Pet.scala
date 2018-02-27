package models

import com.sun.org.apache.xpath.internal.operations.Or
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.functional.syntax._

case class Pet(id: Int, name: String, tipo: String, )

object Pet {

  implicit val placeWrite = Json.writes[Pet]
  implicit val placeRead = Json.reads[Pet]

}