package models

import com.typesafe.config.Config

case class ServerConfig
(
  interface: String,
  port: Int
)

object ServerConfig {
  def apply(config: Config): ServerConfig = {
    val interface = config.getString("interface")
    val port = config.getInt("port")
    new ServerConfig(interface, port)
  }
}