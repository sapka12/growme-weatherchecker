package arnfarkas.growme.weatherchecker

import scala.concurrent.duration._
import scala.util.Try


object Env {
  val weatherUrl = "growme_weatherchecker_weatherurl"
  val mqtt = "growme_weatherchecker_mqtt"
  val initialdelaySec = "growme_weatherchecker_initialdelay_sec"
  val intervalSec = "growme_weatherchecker_interval_sec"
  val topics = "growme_weatherchecker_topics"
}


object EnvVal {
  val weatherUrl = sys.env.get(Env.weatherUrl).map(_.replaceAll("\\\\", ""))
  val mqttParams = MqttParams.byEnv(Env.mqtt)
  val initialDelay = Try(sys.env(Env.initialdelaySec).toInt.seconds).toOption
  val interval = Try(sys.env(Env.intervalSec).toInt.seconds).toOption
  val topics = Try(sys.env(Env.topics).split(",")).toOption.toList.flatten
}