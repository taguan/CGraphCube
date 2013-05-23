package cgc.cuboid

import java.io._
//import collection._
import io.Source
import cgc.graph.VertexIDParser


object CuboidProcessor {

  def computeCuboid(fun : AggregateFunction, inputPath : String, outputPath : String){

    /**
     * Parses a key value Pair from a line around a tab character
     */
    def parseLine(line : String) = {
      val regex = """([^\t]+)\t([^\t]+)""".r

      line match {
        case regex(key,value) => Pair(key,value.toInt)
        case _ => throw new IOException("Wrongly formatted line, line : " + line)
      }
    }

    val table = new java.util.HashMap[String,Int]

    new java.io.File(inputPath).listFiles().filterNot(_.getName.startsWith(".")).foreach(
      file => { println(file)

      Source.fromFile(file).getLines().map(parseLine(_)).foreach(
      entry => {
        val aggregatedKey = fun.aggregate(VertexIDParser.parseID(entry._1))
        var previousWeight = table.get(aggregatedKey)

        if (previousWeight == null) previousWeight = 0

        table.put(aggregatedKey,previousWeight + entry._2)
        //val previousWeight = table.getOrElse(aggregatedKey,0)
        //table += (aggregatedKey -> (previousWeight + entry._2))
      }

    )} )

    val writer = new PrintWriter(new BufferedWriter(new FileWriter(outputPath)))

    var it = table.entrySet().iterator()

    while(it.hasNext){

      val elem = it.next
      writer.println(elem.getKey + "\t" + elem.getValue)
    }

    /*
    for((key,value) <- table.iterator){
      writer.println(key + "\t" + value)
    } */
    writer.close()


  }

}

