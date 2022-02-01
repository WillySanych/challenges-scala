import cats.effect.{ExitCode, IO, IOApp, Resource}
import fs2.{Stream, io, text}

import java.io.InputStream
import cats.effect.unsafe.implicits.global

import scala.io.StdIn

object Main {
  def main (args: Array[String]) = {
    def resourceIO(name: String): IO[InputStream] =
      IO{getClass.getResourceAsStream(name)}

    val largeFile: Stream[IO, Byte] =
      fs2.io.readInputStream(resourceIO("text.txt"),
        chunkSize = 4096, closeAfterUse = true)

    val wordRegEx = raw"[a-zA-Z0-9-]+".r

    def words: fs2.Pipe[IO, String, String] =
      in => in.flatMap{ line =>
        Stream.emits(wordRegEx
          .findAllIn(line)
          .map(_.toLowerCase)
          .toList)
      }

    val largeFileWords = largeFile
      .through(text.utf8.decode)
      .through(text.lines)
      .through(words)

    println("Enter the word:")
    val word = StdIn.readLine()

    def wordFilter (word: String): fs2.Pipe[IO, String, String] =
      val lcWord = word.toLowerCase
      in => in
        .filter(_ == lcWord)

    val wordsFiltered = largeFileWords.through(wordFilter(word))

    def toIO: IO[List[String]] =
      wordsFiltered.compile.toList

    val countWord = toIO.unsafeRunSync().size
    println(s"The '$word' occurs $countWord times")

  }
}
