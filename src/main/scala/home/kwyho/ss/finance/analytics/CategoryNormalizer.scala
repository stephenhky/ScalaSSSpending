package home.kwyho.ss.finance.analytics

import java.io.{FileReader, File}
import au.com.bytecode.opencsv.CSVReader
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions._
import scala.collection.mutable.Map
import edu.stanford.nlp.tagger.maxent.MaxentTagger
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory

/**
 * Created by hok1 on 6/23/14.
 */
class CategoryNormalizer(crosswalkFileName : String = "JSSSpendCatCrosswalk.csv") {
  val crosswalkFile : File = new File(crosswalkFileName)
  val crosswalkHashMap : Map[String, String] = Map()
  importXWalk(crosswalkFile)
  val tagger : MaxentTagger = new MaxentTagger("english-left3words-distsim.tagger")

  def importXWalk(crosswalkFile : File) = {
    val reader : CSVReader = new CSVReader(new FileReader(crosswalkFile))
    val lines : Buffer[Array[String]] = reader.readAll()
    reader close()
    lines.foreach( line => crosswalkHashMap += (line(0) -> line(1)))
  }

  def stemWords(word : String) : String = {
    val tokens : Array[String] = word.split(" ")
    val stemmedTokens = tokens.map( token => PorterStemmerTokenizerFactory.stem(token))
    stemmedTokens reduce( (s1, s2) => s1+" "+s2) trim
  }
}
