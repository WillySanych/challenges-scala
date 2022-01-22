import java.util.concurrent.RecursiveTask

class ForkJoinCountWords(wordList: IndexedSeq[String], from: Int, until: Int) extends RecursiveTask[Double]:
  override def compute(): Double =
    if until - from < 1 then
      0
    else if until - from == 1 then
      1
    else
      val count = (until - from) / 2
      val t1: ForkJoinCountWords = ForkJoinCountWords(wordList, from, from + count)
      val t2: ForkJoinCountWords = ForkJoinCountWords(wordList, from + count, until)
      t1.fork()
      t2.fork()
      t1.join() + t2.join()

