package models

import com.sun.org.apache.xpath.internal.operations.Or
import play.api.libs.json.{JsPath, Json, Reads}
import play.api.libs.functional.syntax._

case class Pet(id: Int, name: String, kind: String, gender: String, location: String, state: String)

object Pet {

  implicit val petWrite = Json.writes[Pet]
  implicit val petRead = Json.reads[Pet]

}