package groceries.config

trait ConfigurationModule {

  def discounterConfig: DiscounterConfig

}


trait LiveConfiguration extends ConfigurationModule {

  lazy val config: Configuration = Configuration()
  lazy val discounterConfig: DiscounterConfig = config.discounterConfig

}
