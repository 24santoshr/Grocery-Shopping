package groceries

import com.typesafe.scalalogging.StrictLogging
import groceries.config.{ConfigurationModule, LiveConfiguration}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.time.{Millis, Seconds, Span}

trait TestConfiguration extends LiveConfiguration {
}

trait IntegrationTestComponents extends GroceriesComponent with StrictLogging {
  this: ConfigurationModule =>

}

trait IntegrationSpecification extends Specification with IntegrationTestComponents with TestConfiguration with BeforeAndAfterAll {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(15, Seconds), interval = Span(300, Millis))

  override def afterEach(): Unit = {
    super.afterEach()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    system.terminate().futureValue
  }

}
