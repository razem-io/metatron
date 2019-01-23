package metatron.model

case class RpcResponse[T](version: String, result: T, error: Option[String])