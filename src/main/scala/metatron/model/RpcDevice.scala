package metatron.model

case class RpcDevice(
                    id: String,
                    name: String,
                    address: String,
                    interface: String,
                    `type`: String,
                    operateGroupOnly: String,
                    channels: List[RpcChannel]
                    )
