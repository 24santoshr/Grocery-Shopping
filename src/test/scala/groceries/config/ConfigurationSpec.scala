package groceries.config

import groceries.Specification

class ConfigurationSpec extends Specification {

  "Configuration" should {
    "load without exceptions" in {
      val config: Configuration = Configuration()
    }
  }

}
