package metatron.model.request

import io.circe.generic.auto._
import metatron.model.RpcResponse

import scala.util.Try

case class RpcInterfaceGetDeviceDescriptionRequest(sessionId: String, interface: String, address: String) extends RpcRequest[Map[String, String]] {
  override val method: String = "Interface.getDeviceDescription"
  override val params: Map[String, String] = Map(
    "_session_id_" -> sessionId,
    "interface" -> interface,
    "address" -> address
  )

  override def decode(jsonString: String): Try[RpcResponse[Map[String, String]]] = {
    io.circe.parser.decode[RpcResponse[Map[String, String]]](jsonString).toTry
  }
}
