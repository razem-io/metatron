package metatron.model.request

import io.circe.generic.auto._
import metatron.model.RpcResponse

import scala.util.Try

abstract class RpcStringListRequest extends RpcRequest[List[String]] {
  override def decode(jsonString: String): Try[RpcResponse[List[String]]] =
    io.circe.parser.decode[RpcResponse[List[String]]](jsonString).toTry
}
