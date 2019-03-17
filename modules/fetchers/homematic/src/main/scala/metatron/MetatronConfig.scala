package metatron

import com.typesafe.config.{Config, ConfigFactory}

case class InfluxDbConfig(host: String, port: Int, databaseName: String)
case class HomematicConfig(url: String, username: String, password: String)

object MetatronConfig {
  val config: Config = ConfigFactory.load()

  implicit val influxDbConfig = InfluxDbConfig(
    host = config.getString("influxdb.host"),
    port = config.getInt("influxdb.port"),
    databaseName = config.getString("influxdb.database.name")
  )

  implicit val homematicConfig = HomematicConfig(
    url = config.getString("homematic.url"),
    username = config.getString("homematic.username"),
    password = config.getString("homematic.password")
  )
}
