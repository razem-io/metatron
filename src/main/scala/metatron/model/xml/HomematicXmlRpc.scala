package metatron.model.xml

import java.nio.charset.StandardCharsets
import java.time.Instant

import enumeratum._
import okhttp3.{OkHttpClient, Request}
import wvlet.log.LogSupport

import scala.io.{Codec, Source}
import scala.util.Try
import scala.xml.{Node, NodeSeq}

case class RpcStateList(devices: Seq[RpcDevice])

// <device name="Erdgeschoss Bad" ise_id="1521" unreach="false" sticky_unreach="true" config_pending="false">
case class RpcDevice(name: String, iseId: Int, unreach: Option[Boolean], stickyUnreach: Option[Boolean], configPending: Option[Boolean], channels: Seq[RpcChannel])

// <channel name="Erdgeschoss Bad:0" ise_id="1522" index="0" visible="" operate="">
case class RpcChannel(name: String, iseId: Int, index: Int, visible: Option[Boolean], operate: Option[Boolean], datapoints: Seq[RpcDatapoint])

// <datapoint name="BidCos-RF.NEQ0875573:0.UNREACH" type="UNREACH" ise_id="1543" value="false" valuetype="2" valueunit="" timestamp="1551740946" operations="5"/>
case class RpcDatapoint(name: String, datapointType: String, iseId: Int, value: String, valuetype: Int, valueunit: String, timestamp: Instant, operations: String)

object HomematicXmlRpc extends LogSupport {

  val client = new OkHttpClient()
  val request: Request = new Request.Builder()
    .url("http://10.10.59.8/addons/xmlapi/statelist.cgi")
    .build()

  def pollStateList: Try[RpcStateList] = Try {
    val rawXml = new String(client.newCall(request).execute().body().bytes(), StandardCharsets.ISO_8859_1)
    RpcStateList.fromXml(scala.xml.XML.loadString(rawXml))
  }
}

object RpcStateList {

  // (b) convert XML to a Stock
  def fromXml(node: scala.xml.Node): RpcStateList = {
    val devices: NodeSeq = node \\ "device"

    RpcStateList(devices.map(RpcDevice.fromXml))
  }

}

object RpcDevice {
  def fromXml(node: Node): RpcDevice = {

    val unreachNode = node \ "@unreach"
    val unreach = if(unreachNode.nonEmpty) Some(unreachNode.text.toBoolean) else None
    val stickyUnreachNode = node \ "@sticky_unreach"
    val stickyUnreach = if(stickyUnreachNode.nonEmpty) Some(stickyUnreachNode.text.toBoolean) else None
    val configPendingNode = node \ "@config_pending"
    val configPending = if(configPendingNode.nonEmpty) Some(configPendingNode.text.toBoolean) else None

    RpcDevice(
      (node \ "@name").text,
      (node \ "@ise_id").text.toInt,
      unreach,
      stickyUnreach,
      configPending,
      (node \\ "channel").map(RpcChannel.fromXml)
    )
  }
}

object RpcChannel {
  def fromXml(node: Node): RpcChannel = {

    val visibleNode = node \ "@visible"
    val visible = if(visibleNode.isEmpty || visibleNode.text.isEmpty) None else Some(visibleNode.text.toBoolean)
    val operateNode = node \ "@operate"
    val operate = if(operateNode.isEmpty || operateNode.text.isEmpty) None else Some(operateNode.text.toBoolean)

    RpcChannel(
      (node \ "@name").text,
      (node \ "@ise_id").text.toInt,
      (node \ "@index").text.toInt,
      visible,
      operate,
      (node \\ "datapoint").map(RpcDatapoint.fromXml)
    )
  }
}

// <datapoint name="BidCos-RF.NEQ0875573:0.UNREACH" type="UNREACH" ise_id="1543" value="false" valuetype="2" valueunit="" timestamp="1551740946" operations="5"/>
object RpcDatapoint {
  def fromXml(node: Node): RpcDatapoint = {

    RpcDatapoint(
      name = (node \ "@name").text,
      datapointType = (node \ "@type").text,
      iseId = (node \ "@ise_id").text.toInt,
      value = (node \ "@value").text,
      valuetype = (node \ "@valuetype").text.toInt,
      valueunit = (node \ "@valueunit").text,
      timestamp = Instant.ofEpochSecond((node \ "@timestamp").text.toLong),
      operations = (node \ "@operations").text
    )
  }
}

sealed trait RpcDatapointType extends EnumEntry
object RpcDatapointType extends Enum[RpcDatapointType] with LogSupport {

  //noinspection TypeAnnotation
  val values = findValues

  case object RPC_INTEGER   extends RpcDatapointType
  case object RPC_BOOLEAN   extends RpcDatapointType
  case object RPC_UNKNOWN   extends RpcDatapointType

  def fromNode(node: Node): RpcDatapointType = {
    val value = (node \ "@value").text
    val valueType = (node \ "@valuetype").text.toInt

    valueType match {
      case 2 => RPC_BOOLEAN
      case 8 => RPC_INTEGER
      case _ =>
        warn(s"Unkknown datapoint type: $valueType with value: $value ")
        RPC_UNKNOWN
    }
  }
}