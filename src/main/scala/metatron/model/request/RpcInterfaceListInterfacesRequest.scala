package metatron.model.request

import io.circe.generic.auto._
import metatron.model.{RpcInterface, RpcResponse}

import scala.util.Try

case class RpcInterfaceListInterfacesRequest(sessionId: String) extends RpcRequest[List[RpcInterface]] {

  override val method: String = "Interface.listInterfaces"
  override val params: Map[String, String] = Map("_session_id_" -> sessionId)

  override def decode(jsonString: String): Try[RpcResponse[List[RpcInterface]]] =
    io.circe.parser.decode[RpcResponse[List[RpcInterface]]](jsonString).toTry
}
