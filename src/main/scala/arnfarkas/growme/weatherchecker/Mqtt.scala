package arnfarkas.growme.weatherchecker

import akka.Done
import akka.stream.alpakka.mqtt.{MqttConnectionSettings, MqttMessage, MqttQoS}
import akka.stream.alpakka.mqtt.scaladsl.MqttSink
import akka.stream.scaladsl.Sink
import javax.net.ssl.SSLContext
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

import scala.concurrent.Future
import scala.util.Try

case class Mqtt(mqtt: MqttParams) {
  private val mqttConnection = MqttConnectionSettings(
    s"ssl://${mqtt.host}:${mqtt.port}",
    "ssl-client",
    new MemoryPersistence
  ).withAuth(mqtt.user, mqtt.password)
    .withSocketFactory(SSLContext.getDefault.getSocketFactory)

  val sink: Sink[MqttMessage, Future[Done]] = MqttSink(mqttConnection, MqttQoS.AtLeastOnce)
}

object MqttParams {
  def byEnv(envKey: String): Option[MqttParams] = for {
    mqtt <- sys.env.get(envKey)
    listAttributes <- Try(mqtt.split("@").toList.flatMap(_.split(":"))).toOption

    user = listAttributes(0)
    password = listAttributes(1)
    host = listAttributes(2)
    port <- Try(listAttributes(3).toInt).toOption
  } yield MqttParams(user, password, host, port)
}
case class MqttParams(user: String, password: String, host: String, port: Int)


