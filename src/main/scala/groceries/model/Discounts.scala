package groceries.model

import play.api.libs.json.{Json, OFormat}

/**
 * @author Santosh
 */
// Creating structure for Discount data
object Discounts {

  implicit val jsonFormat: OFormat[Discounts] = Json.format[Discounts]

}

case class Discounts(name: String, discount: BigDecimal)
