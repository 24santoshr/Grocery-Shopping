package groceries

import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Keep, Sink, Source}
import akka.stream.testkit.TestPublisher
import akka.stream.testkit.scaladsl.{TestSink, TestSource}
import akka.stream.{Materializer, SystemMaterializer}
import akka.util.ByteString
import org.mockito.stubbing.OngoingStubbing
import org.mockito.{ArgumentMatchersSugar, IdiomaticMockito}
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.{ExecutionContext, Future}

object Specification {

  implicit class OngoingStubberExtension[T](stub: OngoingStubbing[Future[T]]) {

    def thenReturnFuture(value: T): OngoingStubbing[Future[T]] = stub.thenReturn(Future.successful(value))

    def thenReturnFuture(value: T, values: T*): OngoingStubbing[Future[T]] = stub.thenReturn(
      Future.successful(value),
      values.map(Future.successful): _*
    )

    def thenReturnFailedFuture(exception: Throwable): OngoingStubbing[Future[T]] =
      stub.thenReturn(Future.failed(exception))

  }

}

trait Specification
  extends AnyWordSpec
    with should.Matchers
    with ScalaFutures
    with GivenWhenThen
    with BeforeAndAfterEach
    with IdiomaticMockito
    with ArgumentMatchersSugar
    with ModelSupport {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds), interval = Span(300, Millis))
}

trait StreamSupport extends BeforeAndAfterAll { this: Suite =>

  implicit val actorSystem = ActorSystem(this.getClass.getSimpleName)
  implicit val materializer = SystemMaterializer(actorSystem).materializer
  implicit val ec: ExecutionContext = actorSystem.dispatcher

  override def afterAll(): Unit = {
    super.afterAll()
    materializer.shutdown()
    actorSystem.terminate()
  }

  def testSource[T](sink: Sink[T, _]): TestPublisher.Probe[T] = {
    val source = TestSource.probe[T].toMat(sink)(Keep.left).run()
    source.ensureSubscription()
    source
  }

  def withSourceAndSink[T](flow: Flow[T, T, _]) = {
    val (source, sink) = TestSource.probe[T].via(flow).toMat(TestSink.probe[T])(Keep.both).run()
    source.ensureSubscription()
    sink.ensureSubscription()
    (source, sink)
  }

  def testSink[T](source: Source[T, _]) = {
    source.toMat(TestSink.probe[T])(Keep.right).run()
  }

}

trait FileSupport {

  def contentOf(file: String)(implicit materializer: Materializer, ec: ExecutionContext): Future[String] =
    contentOf(fromClassPath(file))

  def fromClassPath(file: String): Path = Paths.get(
    this.getClass.getResource(file).toURI
  )

  def contentOf(file: Path)(implicit materializer: Materializer, ec: ExecutionContext): Future[String] = FileIO.fromPath(file).runFold(ByteString(""))((agg, el) => agg.concat(el)).map(_.utf8String)

}