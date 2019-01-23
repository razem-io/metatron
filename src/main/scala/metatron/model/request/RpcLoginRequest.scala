package metatron.model.request

case class RpcLoginRequest(username: String, password: String) extends RpcStringRequest {
  override val method: String = "Session.login"
  override val params: Map[String, String] = Map("username" -> username, "password" -> password)
}
