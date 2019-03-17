package metatron

import java.time.Instant

import com.paulgoldbaum.influxdbclient.Parameter.Precision
import com.paulgoldbaum.influxdbclient._
import metatron.model.xml
import metatron.model.xml.{HomematicXmlRpc, RpcDatapoint}
import monix.execution.Scheduler.{global => scheduler}
import wvlet.log.LogSupport

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

object MetatronApp extends App with LogSupport {
  import MetatronConfig._
  val influxdb = InfluxDB.connect(influxDbConfig.host, influxDbConfig.port)
  val db = influxdb.selectDatabase(influxDbConfig.databaseName)

  init()

  def init(): Unit = {
    import wvlet.airframe._

    log.init

    db.exists().onComplete {
      case Success(x) if !x =>
        db.create()
        db.createRetentionPolicy("two_years", "105w", 1, true)
      case Failure(e) => println(e)
      case _ =>
    }
  }

  scheduler.scheduleWithFixedDelay(10.seconds, 60.seconds) {
    info("Polling Data.... ")

    val result = HomematicXmlRpc.pollStateList


    val doubleResult: Seq[(xml.RpcDevice, Seq[(RpcDatapoint, Double)])] = result.devices.map(d => d -> d.channels.flatMap(_.datapoints.flatMap(p => Try(p.value.toDouble).toOption.map(p -> _))))

    val points = doubleResult.flatMap(t => {
      val device = t._1
      val points = t._2

      points.map(p => {
        val datapoint = p._1
        val value = p._2

        val timestamp = datapoint.timestamp.toEpochMilli
        val timestampCatched = if (timestamp == 0) Instant.now.toEpochMilli else timestamp

        Point(key = "hm", timestamp = timestampCatched)
          .addTag("name", device.name)
          .addTag("type", datapoint.datapointType)
          .addField("value", value)
      })
    })

    db.bulkWrite(points, retentionPolicy = "two_years", precision = Precision.MILLISECONDS)
  }
}
