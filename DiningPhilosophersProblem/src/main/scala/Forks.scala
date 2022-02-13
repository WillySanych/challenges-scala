object Forks {
  private val forks: Array[Boolean] = Array.fill(5)(true)
 def getFork(index: Int): Boolean = forks(index)
 def setFork(index: Int, state: Boolean): Unit = {
   this.synchronized {
     forks(index) = state
   }
  }
}
