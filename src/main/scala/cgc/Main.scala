package cgc

import cuboid.{CuboidProcessor, AggregateFunction}
import java.io.IOException
import org.apache.commons.cli.Options
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.BasicParser

object Main {

  def main(args : Array[String]) {

    /**
     * Defines options for the command line
     * parser
     */
    def getOptions : Options = {
      val options = new Options()
      options.addOption("h","help",false,"Display help")
      options.addOption("inp","inputPath",true,"Descendant network input path")
      options.addOption("oup","outputPath",true,"Output Path")
      options.addOption("cu", "cuboid", true, "Cuboid function\n Ex : 0,2")
      options
    }

    /**
     * Prints a well formatted help
     */
    def printHelp() {
      val formatter = new HelpFormatter()
      formatter.printHelp("SGraphCube",getOptions,true)
    }

    val options = getOptions
    val parser = new BasicParser()
    val cmd = parser.parse(options, args)

    if(args.length == 0 || cmd.hasOption("h")){
      printHelp()
      sys.exit(0)
    }

    if(!cmd.hasOption("inp")){
      println("Input path required")
      printHelp()
      sys.exit(0)
    }

    if(!cmd.hasOption("oup")){
      println("Output path required")
      printHelp()
      sys.exit(0)
    }

    if (!cmd.hasOption("cu")){
      println("Cuboid function required")
      printHelp()
      sys.exit(0)
    }

    var cuboidFunction : AggregateFunction = null

    cuboidFromUser(cmd.getOptionValue("cu")) match {
      case Some(fun) => {
        cuboidFunction = fun
      }

      case None => {
        println("Wrongly formatted cuboid")
        printHelp()
        sys.exit(0)
      }
    }


    val startMaterialization = System.currentTimeMillis()

    CuboidProcessor.computeCuboid(cuboidFunction,cmd.getOptionValue("inp"),cmd.getOptionValue("oup"))


    println("Elapsed time : " + (System.currentTimeMillis() - startMaterialization))

  }

  /**
   * Converts a String to its function representation
   * @param userEntry  A string representing the cuboid query
   * @return  The aggregate function corresponding to the user query
   *          or None if the user input is wrongly formatted
   */
  def cuboidFromUser(userEntry : String) : Option[AggregateFunction] = {
    val regex = """\d+(,\d+)*""".r
    userEntry match{
      case regex(_)  => Some(AggregateFunction(userEntry))

      //if you want to interact directly with the input graph
      case "base" => Some(AggregateFunction(""))

      case _ => None
    }
  }


}

