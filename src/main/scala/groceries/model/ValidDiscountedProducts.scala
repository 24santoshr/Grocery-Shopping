package groceries.model

import groceries.model.ProductItem.Category

/**
 * @author Santosh
 */
case class ValidDiscountedProducts(category: Category, products: Seq[String])
