import java.util.concurrent.RecursiveTask

class ForkJoinCountLetters(wordList: IndexedSeq[String], from: Int, until: Int) extends RecursiveTask[Double]:
  override def compute(): Double =
    if until - from < 1 then
      0
    else if until - from == 1 then
      wordList(from).length
    else
      val count = (until - from) / 2
      val t1: ForkJoinCountLetters = ForkJoinCountLetters(wordList, from, from + count)
      val t2: ForkJoinCountLetters = ForkJoinCountLetters(wordList, from + count, until)
      t1.fork()
      t2.fork()
      t1.join() + t2.join()

