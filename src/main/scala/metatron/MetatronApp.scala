package metatron

import com.paulgoldbaum.influxdbclient.Parameter.Precision
import com.paulgoldbaum.influxdbclient._
import metatron.model.RpcDevice
import metatron.model.request.RpcLoginRequest
import monix.execution.Scheduler.{global => scheduler}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

object MetatronApp extends App {
  import MetatronConfig._
  val influxdb = InfluxDB.connect(influxDbConfig.host, influxDbConfig.port)
  val db = influxdb.selectDatabase(influxDbConfig.databaseName)
  init()
  def init(): Unit = {
    db.exists().onComplete {
      case Success(x) if !x =>
        db.create()
        db.createRetentionPolicy("two_years", "105w", 1, true)
      case Failure(e) => println(e)
      case _ =>
    }
  }

  var id = login()

  def login(): String = {
    val loginRequest = RpcLoginRequest(username = homematicConfig.username, homematicConfig.password)
    Await.result(loginRequest.send(), 10.seconds).result
  }

  scheduler.scheduleWithFixedDelay(10.seconds, 60.seconds) {
    println("Polling Data.... ")

    val result = pollData()

    val points = result.map(e => {
      val point = Point("hm")
        .addTag("name", e._1.name)
        .addTag("type", e._1.`type`)

      e._2.foldLeft(point) { (p, m) => p.addField(m._1, m._2.toDouble) }
    }).toSeq

    db.bulkWrite(points, retentionPolicy = "two_years", precision = Precision.MILLISECONDS)
  }

  def pollData(): Map[RpcDevice, List[(String, String)]] = {
    val end = for {
      devicesResponse <- model.request.RpcDeviceListAllDetailRequest(id).send()
      result <- {
        if(devicesResponse.error.exists(_.contains("access denied"))) id = login()

        val futures: List[Future[(RpcDevice, Map[String, String])]] = devicesResponse
          .result
          .filter(device => Seq("HM-CC-RT-DN", "HM-TC-IT-WM-W-EU").contains(device.`type`))
          .flatMap(device => {
            device.channels.map(_.address).map(c => {
              val f = model.request.RpcInterfaceGetParamsetRequest(id, device.interface, c, "VALUES").send()

              f.onComplete {
                case Success(x) => println(s"${device.name} - ${device.`type`} / $c -> $x")
                case Failure(x) => println(s"${device.name} - ${device.`type`} / $c -> $x")
              }

              f.map(device -> _.result)
            })
          })

        Future.sequence(futures.map(_.transform(Success(_))))
      }
    } yield result


    val successes: Map[RpcDevice, List[(String, String)]] = Await.result(end, 40.seconds)
      .filter(_.isSuccess)
      .map(_.get)
      .groupBy(_._1)
      .mapValues(_.flatMap(_._2.toList))

    println("HERE IT GOES!")
    successes.filter(_._2.nonEmpty).foreach(e => println(e._1.name + ": " + e._2))

    successes
  }
}
