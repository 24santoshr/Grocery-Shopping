package groceries.clients


import com.typesafe.scalalogging.Logger
import groceries.config.DiscounterConfig
import groceries.model.{Discounts, ProductItem}
import play.api.libs.json.JsValue
import play.api.libs.ws.{JsonBodyReadables, StandaloneWSClient}

import scala.concurrent.{ExecutionContext, Future}

class DiscounterClient(
                        ws: StandaloneWSClient,
                        config: DiscounterConfig
                      )(implicit ec: ExecutionContext) {

  import JsonBodyReadables.readableAsJson;

  private val logger = Logger(getClass)

  def listProducts(): Future[Seq[ProductItem]] =
    ws
      .url(s"${config.url}/api/products")
      .get()
      .map { response => response.body[JsValue].as[Seq[ProductItem]] }

  /**
   * Fetching and storing data for discount codes
   *
   * @param name : takes discount name as parameter
   * @return
   */
  def getDiscount(name: Seq[String]): Future[Seq[Discounts]] = {

    var result = Seq[Discounts]()
    name.distinct.foreach { a =>
      ws
        .url(s"${config.url}/api/discounts/$a")
        .get()
        .map {
          case response if response.status == 200 => {
            val discountName = response.body[JsValue].\("name").as[String]
            val price = response.body[JsValue].\("discount").as[BigDecimal]
            result = result ++ Seq(Discounts(discountName, price))
          }
          case response if response.status == 404 => {
            // For calculation simplification purpose, as invalid discount code will not have discount price
            result = result ++ Seq(Discounts(a, 0))
          }
        }
    }
    println(s"")
    Future(result)
  }
}