package groceries.clients

import play.api.libs.json.Format
import play.api.libs.ws.{BodyReadable, JsonBodyReadables}

object PlayJsonBodyReadable {

  object Implicits {

    implicit def jsonReadable[A: Format]: BodyReadable[A] = BodyReadable { response =>
      JsonBodyReadables.readableAsJson.transform(response).as[A]
    }

  }

}
