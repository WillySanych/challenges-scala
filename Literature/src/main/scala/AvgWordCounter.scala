import java.util.concurrent.ForkJoinPool
import scala.io.Source
import scala.util.matching.Regex

object AvgWordCounter{
  @main def main () = {
  val source = Source.fromURL("https://raw.githubusercontent.com/benschw/shakespeare-txt/master/shakespeare-hamlet-25.txt")
  val Word = """\b([\w+ '])+\b""".r

  val wordList: IndexedSeq[String] = source
    .getLines()
    .flatMap(x => Word.findAllIn(x.toLowerCase()))
    .toIndexedSeq
    .flatMap(line => line.split(" "))

  source.close()

  val pool = new ForkJoinPool()
  val task1 = new ForkJoinCountWords(wordList, 0, wordList.size)
  val task2 = new ForkJoinCountLetters(wordList, 0, wordList.size)
  val countWords: Double = pool.invoke(task1)
  val countLetters: Double = pool.invoke(task2)
  pool.shutdown()

  val avgWordLength: Double = (countLetters / countWords)
  println(avgWordLength)
  }
}