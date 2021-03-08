package groceries.model

import enumeratum.EnumEntry.UpperSnakecase
import play.api.libs.json.{Json, OFormat}

object ProductItem {

  import enumeratum._

  sealed trait Category extends EnumEntry with UpperSnakecase

  object Category extends Enum[Category] with PlayJsonEnum[Category] {

    val values: IndexedSeq[Category] = findValues

    case object Vegetables extends Category

    case object Beverages extends Category

    case object Fruits extends Category

    case object Bread extends Category
  }

  implicit val jsonFormat: OFormat[ProductItem] = Json.format[ProductItem]


}

case class ProductItem(
                        id: Int,
                        name: String,
                        price: BigDecimal,
                        category: ProductItem.Category,
                        discountCode: Option[String]
                      )

