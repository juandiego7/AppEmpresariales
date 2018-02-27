package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import play.api.db._
import models.Pet

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(db: Database, cc: ControllerComponents) extends AbstractController(cc) {

  var pets = List[Pet]()

  /**
   * Create an Action to render an HTML page with a welcome message.
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def getPets = Action {
    val conn = db.getConnection()
    pets = List[Pet]()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM pet")
    
      while (rs.next()) {
        var p = Pet(
          rs.getInt("id"),
          rs.getString("name"),
          rs.getString("kind"),
          rs.getString("gender"),
          rs.getString("location"),
          rs.getString("state")
        )
        pets = pets :+ p
      }
    } finally {
      conn.close()
    }
    val json = Json.toJson(pets)
    Ok(json)
  }

  def getPet(name: String) = Action {
    val conn = db.getConnection()
    pets = List[Pet]()
    try {
      val stmt = conn.createStatement
      val rs = stmt.executeQuery("SELECT * FROM pet WHERE name like '"+name+"%'")
      while(rs.next()){
        var p = Pet(
          rs.getInt("id"),
          rs.getString("name"),
          rs.getString("kind"),
          rs.getString("gender"),
          rs.getString("location"),
          rs.getString("state")
        )
        pets = pets :+ p
      }
    } 
    finally {
      conn.close()      
    }
    val json = Json.toJson(pets)
    Ok(json)
  }

  def insertPet() = Action { implicit request =>
    val bodyAsJson = request.body.asJson.get

    bodyAsJson.validate[Pet] match {
      case success: JsSuccess[Pet] =>
        val pet = success.get

        val message: Option[String] = {
          pets.find(_.id == success.get.id) match{
            case Some(q) =>  Option("Ya existe una mascota con la misma identificaión")

            case None => val conn = db.getConnection()
                          try {
                            val stmt = conn.createStatement();
                            val rs = stmt.executeUpdate("INSERT INTO pet VALUES("+pet.id+",'"+pet.name+"','"+pet.kind+"','"+pet.gender+"','"+pet.location+"','"+pet.state+"')")
                          }finally{
                            conn.close()
                          }
                          pets = pets :+ pet
                          Option("Ingreso exitoso")
          }
        }
        
        Ok(Json.toJson(
          Map("message" -> message)
        ))

      case JsError(error) => BadRequest(Json.toJson(
        Map("error" -> "Bad Parameters", "description" -> "Missing a parameter")
      ))
    }
  }

  def deletePet(id: Int) = Action {
    val conn = db.getConnection()
    try {
      val stmt = conn.createStatement();
      val rs = stmt.executeUpdate("DELETE FROM pet WHERE id = " + id)
    }finally{
      conn.close()
    }
    //pets = pets.filter(p => p.id != id)
    Ok("Mascota borrada") 
  }

  def updatePet = Action { implicit request =>
    val bodyAsJson = request.body.asJson.get

    bodyAsJson.validate[Pet] match {
      case success: JsSuccess[Pet] =>
        var pet = Pet(success.get.id, success.get.name, success.get.kind, success.get.gender, success.get.location, success.get.state)

        val conn = db.getConnection()
        try {
          val stmt = conn.createStatement();          
          val rs = stmt.executeUpdate("UPDATE pet SET name = '"+pet.name+"', kind = '"+pet.kind+"', gender = '"+pet.gender+"', location = '"+pet.location+"',state = '"+pet.state+"' WHERE id = "+pet.id)
        }finally{
          conn.close()
        }

        //pets = pets.map(x => if (x.id == success.get.id) newPet else x)
        Ok(Json.toJson(
          Map("message" -> "Los datos de la mascota han sido actualizados")
        ))

      case e:JsError => BadRequest(Json.toJson(
        Map("error" -> "No se pudo actualizar", "description" -> "Bad parameters")))
    }
  }
}
