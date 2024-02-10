import cats.effect.kernel.Async
import cats.effect.{IO, IOApp}
import cats.implicits._

object Main extends IOApp.Simple {

  def app[F[_]: Async]: F[Unit] = for {
    map <- Duplications.defineResultMap("your.path")
    _    = println(map)
  } yield ()

  override def run: IO[Unit] = app[IO]

}
