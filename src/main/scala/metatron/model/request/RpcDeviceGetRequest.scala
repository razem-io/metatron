package metatron.model.request

import io.circe.generic.auto._
import metatron.model.{RpcDevice, RpcResponse}

import scala.util.Try

case class RpcDeviceGetRequest(sessionId: String, id: String) extends RpcRequest[List[RpcDevice]] {
  override val method: String = "Device.get"
  override val params: Map[String, String] = Map("_session_id_" -> sessionId, "id" -> id)

  override def decode(jsonString: String): Try[RpcResponse[List[RpcDevice]]] =
    io.circe.parser.decode[RpcResponse[List[RpcDevice]]](jsonString).toTry
}