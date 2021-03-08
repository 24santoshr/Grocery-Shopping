package groceries.services

import groceries.model.{Discounts, ProductItem, Receipt, ValidDiscountedProducts}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class GroceriesService(val productItem: Seq[ProductItem],
                       val discountItem: Seq[Discounts]) {


  /**
   * Returns the actual prices per productItem in the supplied category after application of discount
   *
   * @param category takes category as input
   * @return
   */
  def actualPrices(category: ProductItem.Category): Future[Map[String, BigDecimal]] = {

    var resultantSet = Seq[(String, BigDecimal, ProductItem.Category, Option[String], BigDecimal)]()
    val filteredSet = productItem.filter(_.category == category)
    val nonDiscountSet = filteredSet.filter(x => x.discountCode.isEmpty)

    // Calculating prices for Non Discounted products
    resultantSet = resultantSet ++ nonDiscountSet.map(x => (x.name, x.price, x.category, x.discountCode, x.price)).filter(_._4.isEmpty)

    //Calculating prices for Discounted products
    discountItem.foreach { a =>
      if (a.discount > 0) {
        resultantSet = resultantSet ++ filteredSet.map(x => (x.name, x.price, x.category, x.discountCode, x.price - (x.price * (a.discount / 100)))).filter(_._4.contains(a.name))
      }
      else {
        resultantSet = resultantSet ++ filteredSet.map(x => (x.name, x.price, x.category, x.discountCode, x.price)).filter(_._4.contains(a.name))
      }
    }
    val finalResult = resultantSet.map(x => (x._1, x._5)).toMap
    Future(finalResult)
  }

  /**
   * Returns all products grouped by Category that have a valid discount code
   */
  def availableDiscounted(): Future[Seq[ValidDiscountedProducts]] = {

    var resultantSet = Seq[ProductItem]()
    var groupedProducts = Seq[ValidDiscountedProducts]()

    discountItem.foreach { a =>
      if (a.discount > 0)
        resultantSet = resultantSet ++ productItem.filter(_.discountCode.contains(a.name))
    }
    val finalResult = resultantSet.groupBy(_.category).map(x => (x._1, x._2.map(_.name)))


    finalResult.foreach {
      getReceipt =>
        groupedProducts = groupedProducts ++ Seq(ValidDiscountedProducts(getReceipt._1, getReceipt._2))
    }
    Future(groupedProducts)
  }

  /**
   * Returns a receipt with the lineitems and the total
   */
  def buyAll(): Future[(Seq[Receipt], BigDecimal)] = {

    var combinedSet = Seq[ProductItem]()
    var receipt = Seq[Receipt]()

    // Getting price data for only discounted products
    combinedSet = productItem.filter(x => x.discountCode.isEmpty)
    discountItem.distinct.foreach { a =>
      if (a.discount == 0)
        combinedSet = combinedSet ++ productItem.filter(x => x.discountCode.contains(a.name))
    }

    val resultantSet = combinedSet.groupBy(_.category).map(x => (x._1, x._2.map(_.name), x._2.map(_.price).sum))
    val finalNonDiscountedSet = resultantSet.groupBy(_._1).map(x => (x._1, x._2.map(_._3).sum + "€"))
    val nonDiscountedPrice = resultantSet.map(x => x._3).sum

    finalNonDiscountedSet.foreach {
      getReceipt =>
        receipt = receipt ++ Seq(Receipt(getReceipt._1, getReceipt._2))
    }
    Future(receipt, nonDiscountedPrice)
  }

  /**
   * Returns a receipt with the lineitems and the total
   */
  def buyOnlyDiscounted(): Future[(Seq[Receipt], BigDecimal)] = {

    var resultantSet = Seq[(String, BigDecimal, ProductItem.Category, Option[String], BigDecimal)]()
    var receipt = Seq[Receipt]()

    //Searching for products having valid discount codes
    discountItem.foreach { a =>
      if (a.discount > 0)
        resultantSet = resultantSet ++ productItem.map(x => (x.name, x.price, x.category, x.discountCode, x.price - (x.price * (a.discount / 100)))).filter(_._4.contains(a.name))
    }
    val finalDiscountedSet = resultantSet.groupBy(_._3).map(x => (x._1, x._2.map(_._5).sum + "€"))
    val discountedProductsPrice = resultantSet.map(x => x._5).sum

    finalDiscountedSet.foreach {
      getReceipt =>
        receipt = receipt ++ Seq(Receipt(getReceipt._1, getReceipt._2))
    }
    Future(receipt, discountedProductsPrice)
  }
}
