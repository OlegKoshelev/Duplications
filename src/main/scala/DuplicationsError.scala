sealed abstract class DuplicationsError(message: String, cause: Option[Throwable])
    extends Throwable(message, cause.orNull)

object DuplicationsError {

  final case class FileNotExistsError(message: String = "File not exists", cause: Option[Throwable] = None)
      extends DuplicationsError(message, cause)

  final case class EmptyFileError(message: String = "File is empty", cause: Option[Throwable] = None)
      extends DuplicationsError(message, cause)

  final case class UnknownError(message: String = "Unknown error", cause: Option[Throwable] = None)
      extends DuplicationsError(message, cause)
}
