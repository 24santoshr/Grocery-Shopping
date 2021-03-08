package groceries.config

import com.typesafe.config.Config

object DiscounterConfig {
  def apply(config: Config): DiscounterConfig =
    DiscounterConfig(
      url = config.getString("url")
    )
}

case class DiscounterConfig(url: String)
