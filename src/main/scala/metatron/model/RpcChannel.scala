package metatron.model

case class RpcChannel(
                       id: String,
                       name: String,
                       address: String,
                       deviceId: String,
                       index: Int,
                       partnerId: String,
                       mode: String,
                       category: String,
                       isReady: Boolean,
                       isUsable: Boolean,
                       isVisible: Boolean,
                       isLogged: Boolean,
                       isLogable: Boolean,
                       isReadable: Boolean,
                       isEventable: Boolean,
                       isAesAvailable: Boolean,
                       isVirtual: Boolean,
                       channelType: String
                     )
