package groceries

import akka.actor.ActorSystem
import akka.stream.{KillSwitches, SharedKillSwitch}
import com.typesafe.scalalogging.StrictLogging
import groceries.clients.DiscounterClient
import groceries.config.ConfigurationModule
import play.api.libs.ws.StandaloneWSClient
import play.api.libs.ws.ahc.StandaloneAhcWSClient

import scala.concurrent.ExecutionContextExecutor

trait GroceriesComponent { this: ConfigurationModule with StrictLogging =>

  import com.softwaremill.macwire._

  implicit val system: ActorSystem = ActorSystem(s"${BuildInfo.name}-system")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  lazy val sharedKillSwitch: SharedKillSwitch = KillSwitches.shared(s"${BuildInfo.name}-global-killswitch")

  lazy val wsClient: StandaloneWSClient = StandaloneAhcWSClient()
  lazy val discounterClient: DiscounterClient = wire[DiscounterClient]

}
