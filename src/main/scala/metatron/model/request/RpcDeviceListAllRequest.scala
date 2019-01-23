package metatron.model.request

case class RpcDeviceListAllRequest(sessionId: String) extends RpcStringListRequest {
  override val method: String = "Device.listAll"
  override val params: Map[String, String] = Map("_session_id_" -> sessionId)
}
