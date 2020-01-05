package arnfarkas.growme.weatherchecker

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer
import akka.stream.alpakka.mqtt.MqttMessage
import akka.stream.scaladsl.Source
import akka.util.ByteString
import spray.json._
import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
import com.typesafe.scalalogging.Logger


object WeatherCheckerApp extends JsonSupport {

  val LOG = Logger(WeatherCheckerApp.getClass)
  LOG.debug(s"${EnvVal.weatherUrl}")
  LOG.debug(s"${EnvVal.mqttParams}")
  LOG.debug(s"${EnvVal.initialDelay}")
  LOG.debug(s"${EnvVal.interval}")
  LOG.debug(s"${EnvVal.topics}")

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem()
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    for {
      url <- EnvVal.weatherUrl
      mqttParams <- EnvVal.mqttParams
      delay <- EnvVal.initialDelay
      interval <- EnvVal.interval
    } yield system.scheduler.schedule(delay, interval) {
      Http().singleRequest(HttpRequest(uri = url)).onComplete {

        case Success(res) =>
          Unmarshal(res).to[Observations].onComplete {
            case Success(observations) =>
              LOG.debug(s"Observations: $observations")
              observations.data.headOption.foreach { observation =>
                val payload = ByteString(observation.toJson.toString)
                val messages: List[MqttMessage] = EnvVal.topics.map(MqttMessage(_, payload))
                Source(messages).runWith(Mqtt(mqttParams).sink)
              }
            case Failure(ex) => LOG.error("Could not Unmarshal the response", ex)
          }

        case Failure(ex) => LOG.error(s"Could not reach endpoint $url", ex)
      }
    }
  }
}
