package metatron.model.request

import fr.hmil.roshttp.HttpRequest
import fr.hmil.roshttp.body.ByteBufferBody
import io.circe.generic.auto._
import io.circe.syntax._
import io.circe.{Encoder, Json}
import metatron.HomematicConfig
import metatron.model.RpcResponse
import monix.execution.Scheduler.Implicits.global

import scala.concurrent.Future
import scala.util.Try

trait RpcRequest[T] {
  implicit val rpcRequestEncoder: Encoder[RpcRequest[T]] = (r: RpcRequest[T]) => Json.obj(
    ("method", Json.fromString(r.method)),
    ("params", Json.obj(r.params.map(e => e._1 -> Json.fromString(e._2)).toSeq: _*))
  )

  val method: String
  val params: Map[String, String]

  def send()(implicit homematicConfig: HomematicConfig): Future[RpcResponse[T]] = {
    send(this.asJson, homematicConfig.url)
  }

  def send(json: Json, homematicUrl: String): Future[RpcResponse[T]] = {
    println(json)
    val jsonBytes = json.noSpaces.getBytes

    HttpRequest(s"$homematicUrl/api/homematic.cgi")
      .withHeaders(
        "Content-Length" → jsonBytes.length.toString,
        "Optional" → "additional",
        "Http" → "headers"
      )
      .post(
        ByteBufferBody(
          data = java.nio.ByteBuffer.wrap(jsonBytes),
          contentType = "application/json; charset=utf-8"
        )
      )
      .flatMap(response ⇒ {
        println(method + ": " + response.body)
        Future.fromTry(decode(response.body))
      })
  }

  def decode(jsonString: String): Try[RpcResponse[T]]
}
