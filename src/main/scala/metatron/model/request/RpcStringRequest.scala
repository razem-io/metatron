package metatron.model.request

import io.circe.generic.auto._
import metatron.model.RpcResponse

import scala.util.Try

abstract class RpcStringRequest extends RpcRequest[String] {
  override def decode(jsonString: String): Try[RpcResponse[String]] =
    io.circe.parser.decode[RpcResponse[String]](jsonString).toTry
}
