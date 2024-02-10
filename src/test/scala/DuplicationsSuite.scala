import DuplicationsError._
import cats.effect.IO
import weaver._

object DuplicationsSuite extends SimpleIOSuite {

  test("should return correct duplications count") {
    for {
      fileUrl <- IO.delay(getClass.getClassLoader.getResource("successfulTest.txt"))
      success <- Duplications.defineResultMap(fileUrl.getPath)
    } yield expect(success == Map("файл" -> 1, "это" -> 2, "текстовый" -> 1, "предложение" -> 1, "второе" -> 1))
  }

  test("should fail with FileNotExistsError") {
    for {
      failure <- Duplications.defineResultMap("notExists.txt").attempt
    } yield expect(failure == Left(FileNotExistsError()))
  }

  test("should fail with EmptyFileError") {
    for {
      fileUrl <- IO.delay(getClass.getClassLoader.getResource("empty.txt"))
      failure <- Duplications.defineResultMap(fileUrl.getPath).attempt
    } yield expect(failure == Left(EmptyFileError()))
  }
}
