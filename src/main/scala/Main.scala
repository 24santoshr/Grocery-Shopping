
import com.typesafe.scalalogging.StrictLogging
import groceries.GroceriesComponent
import groceries.config.LiveConfiguration
import groceries.services.GroceriesService

import scala.util.{Failure, Success}

/**
 * @author Santosh
 */
object Main extends App
  with StrictLogging
  with LiveConfiguration
  with GroceriesComponent {


  logger.info(s"Going Shopping")


  discounterClient
    .listProducts()
    .flatMap { products =>
      discounterClient.getDiscount(products.flatMap(x => x.discountCode)).map(discounts => (discounts, products))
    }.map { case (discounts, products) =>

    products.foreach {
      product =>
        logger.info(s"${product.name} costs ${product.price}€")
    }

    discounts.foreach {
      discount =>
        logger.info(s"${discount.name} and its discount percentage is ${discount.discount}")
    }

    val groceries = new GroceriesService(products, discounts)

    val categoryKeys = products.groupBy(_.category).keys
    categoryKeys.foreach { category =>
      for (
        getPrices <- groceries.actualPrices(category)
      ) yield getPrices.foreach { discountedPrice =>
        logger.info(s" In Category $category, the Product item ${discountedPrice._1}'s actual price after discount is ${discountedPrice._2}€")
      }
    }

    for {
      getValidDiscountProducts <- groceries.availableDiscounted()
      getOnlyDiscounted <- groceries.buyOnlyDiscounted()
      getAll <- groceries.buyAll()
    } yield logger.info(s" Products having valid discount codes are\n $getValidDiscountProducts. \n" +
      s"Discounted prices for a particular category are \n ${getOnlyDiscounted._1}.\n" +
      s"Non discounted prices for a particular category are \n ${getAll._1}. \n" +
      s"Total price for all discounted/non discounted products is ${getOnlyDiscounted._2 + getAll._2}.")

  }
    .andThen {
      case Success(_) =>
        wsClient.close()
        system.terminate()
      case Failure(ex) =>
        logger.error(s"Exception happened", ex)
        wsClient.close()
        system.terminate()
    }
}