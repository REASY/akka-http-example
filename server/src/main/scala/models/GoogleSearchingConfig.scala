package models

import com.typesafe.config.Config

case class GoogleSearchingConfig
(
  apiUrl: String,
  key: String,
  cx: String
)

object GoogleSearchingConfig {
  def apply(config: Config): GoogleSearchingConfig = {
    val apiUrl: String = config.getString("api-url")
    val key:String = config.getString("key")
    val cx:String = config.getString("cx")
    GoogleSearchingConfig(apiUrl, key, cx)
  }
}