package vn.vietvu.arango

import com.arangodb.{ArangoDB, Protocol}

class Traverse {
  def main(args: Array[String]): Unit = {
    // get some nodes

    // buid it's neighbors within
  }

  def getArango: ArangoDB = {
    new ArangoDB.Builder()
      .host("127.0.0.1", 8529)
      .useProtocol(Protocol.VST)
      .build()
  }
}
