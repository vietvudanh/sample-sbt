package vn.vietvu.arango

import java.util

import com.arangodb.entity.{BaseDocument, BaseEdgeDocument, CollectionType, EdgeDefinition}
import com.arangodb.model.{CollectionCreateOptions, DocumentCreateOptions, GraphCreateOptions}
import com.arangodb.{ArangoDB, Protocol}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import scala.jdk.CollectionConverters._

object ImportGraphData {

  val batchSize = 10000

  def main(args: Array[String]): Unit = {
    //    if (args.length == 0) {
    //      throw new Exception("Missing filename")
    //    }
    //    val fileName = args(0)

    val fileName = "/home/vietvd5/works/data/facebook_clean_data/artist_edges.csv"

    val src = Source.fromFile(fileName)
    val iter = src.getLines().drop(1).map(_.split(","))
    val vertices = new mutable.HashSet[String]()
    val edges = new ArrayBuffer[Array[String]]()
    println("reading files")
    for (line <- iter) {
      val Array(n1, n2) = line
      vertices.add(n1)
      vertices.add(n2)
      edges.addOne(Array(n1, n2))
    }

    println("Vertices::" + vertices.size)
    println("edge::" + edges.length)

    // now insert the fucking thing
    val dbName = "facebook"
    val vertexName = "artist"
    val edgeName = "artist_edge"
    val graphName = "graph_artist"
    initCollections(dbName, vertexName, edgeName, graphName)

    val arangoDB = getArango
    val db = arangoDB.db(dbName)
    val vertexCollection = db.collection(vertexName)
//
    val edgeCollection = db.collection(edgeName)

    // insert
    vertexCollection.truncate()
    val vertexData = vertices.map(v => new BaseDocument(v))
    val chunks = vertexData.grouped(batchSize)
    println("start inserting")
    for (chunk <- chunks) {
      println("inserting " + chunk.size)
      vertexCollection.insertDocuments(chunk.asJava, new DocumentCreateOptions().waitForSync(true))
    }

    edgeCollection.truncate()
    val edgeData = edges.map(v => {
      val Array(f, t) = v
      new BaseEdgeDocument(s"${vertexName}/${f}", s"${vertexName}/${t}")
    })
    val chunkEdges = edgeData.grouped(batchSize)
    println("start inserting " + edgeData.length)

    for (edgeChunk <- chunkEdges) {
      val res = edgeCollection.insertDocuments(edgeChunk.asJava,
        new DocumentCreateOptions().waitForSync(true))

      if (res.getErrors.size() > 0) {
        res.getDocumentsAndErrors.forEach(e => {
          println(e.getClass())
        })
      }
    }

    println("insert done")
    arangoDB.shutdown()
    System.exit(0)
  }

  def getArango: ArangoDB = {
    new ArangoDB.Builder()
      .host("127.0.0.1", 8529)
      .useProtocol(Protocol.VST)
      .build()
  }

  def initCollections(
                       dbName: String,
                       vertexCollectionName: String,
                       edgeCollectionName: String,
                       graphName: String): Unit = {
    val arangoDB = getArango
    val db = arangoDB.db(dbName)
    if (!db.collection(vertexCollectionName).exists) {
      db.createCollection(
        vertexCollectionName,
        new CollectionCreateOptions().`type`(CollectionType.DOCUMENT)
      )
    }

    if (!db.collection(edgeCollectionName).exists) {
      db.createCollection(
        edgeCollectionName,
        new CollectionCreateOptions().`type`(CollectionType.EDGES)
      )
    }

    val graph = db.graph(graphName)
    if (!graph.exists) {
      val edgeDefinition = new EdgeDefinition()
        .collection(edgeCollectionName)
        .from(vertexCollectionName)
        .to(vertexCollectionName)
      graph.create(util.Arrays.asList(edgeDefinition), new GraphCreateOptions)
    }
  }
}