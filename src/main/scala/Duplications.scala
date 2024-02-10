import DuplicationsError._
import cats.effect.kernel.Async
import cats.implicits._
import fs2.io.file.{Files, Path}
import fs2.text

import java.nio.file.{NoSuchFileException, Paths}
import scala.annotation.unused

object Duplications {

  private type Word   = String
  private type Amount = Int

  private val defaultSigns = Set(',', '.', '!', '?', ';')

  def defineResultMap[F[_]: Async](filePath: String): F[Map[Word, Amount]] = for {
    byLineMaps <- getLinesMaps(filePath).adaptErr(handleErrors)
    _          <- Async[F].raiseWhen(byLineMaps.isEmpty)(EmptyFileError())
    resultMap   = composeLinesMaps(byLineMaps)
  } yield resultMap

  private def getLinesMaps[F[_]: Async](path: String): F[List[Map[Word, Amount]]] =
    Files[F]
      .readAll(Path.fromNioPath(Paths.get(path)))
      .through(text.utf8.decode)
      .through(text.lines)
      .map(defineLineWords)
      .map(defineLineMap)
      .unNone
      .compile
      .toList

  private def composeLinesMaps(maps: List[Map[Word, Amount]]): Map[Word, Amount] = {
    val keys = maps.flatMap(_.keys).distinct

    keys.foldLeft(Map.empty[Word, Amount]) { (resultMap, key) =>
      val resultCount = maps.flatMap(_.get(key)).sum
      resultMap + (key -> resultCount)
    }
  }

  private def defineLineWords(line: String): Array[Word] =
    filterSigns(line.trim)
      .split(" ")
      .filterNot(_ == "")
      .map(_.toLowerCase)

  private def defineLineMap(words: Array[Word]): Option[Map[Word, Amount]] = {
    val lineMap = words
      .groupBy(identity)
      .map { case (word, duplications) =>
        word -> duplications.length
      }

    if (lineMap.isEmpty) None
    else lineMap.some
  }

  private def filterSigns(line: String, signs: Set[Char] = defaultSigns): String =
    line.filterNot(signs.contains)

  @unused // Sure we can use scary regex
  private def filterSignsByRegex(line: String): String =
    line.replaceAll("(- )|( -)|([!$%^&*()_+|~=`{}\\[\\]:\";'<>?,.\\/])", "")

  private def handleErrors: PartialFunction[Throwable, Throwable] = {
    case _: NoSuchFileException => FileNotExistsError()
    case e                      => UnknownError(cause = e.some)
  }

}
