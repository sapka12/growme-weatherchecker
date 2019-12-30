package arnfarkas.growme.weatherchecker

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


case class Observations(data: List[Observation])
case class Observation(clouds: Int, ob_time: String, sunrise: String, sunset: String, temp: Double, timezone: String)


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val observationFormat = jsonFormat6(Observation)
  implicit val observationsFormat = jsonFormat1(Observations)
}
