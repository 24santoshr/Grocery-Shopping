package groceries.config

import com.typesafe.config.ConfigFactory

object Configuration {

  def apply(): Configuration = {
    val config = ConfigFactory.load()
    Configuration(
      discounterConfig = DiscounterConfig(config.getConfig("discounter"))
    )
  }

}

case class Configuration(
  discounterConfig: DiscounterConfig
)
