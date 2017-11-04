package utils

import play.api.libs.json.{JsValue, Json}

object Implicits {
  implicit class JsValueEx(val jsValue: JsValue) {
    def prettify: String = Json.prettyPrint(jsValue)
  }
}
