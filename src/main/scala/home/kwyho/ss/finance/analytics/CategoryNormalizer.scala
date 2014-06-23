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
  var crosswalkHashMap : Map[String, String] = Map()
  var stemmedCategoriesHashMap : Map[String, List[String]] = Map()
  importXWalk(crosswalkFile)
  val tagger : MaxentTagger = new MaxentTagger("english-left3words-distsim.tagger")


  def importXWalk(crosswalkFile : File) = {
    val reader : CSVReader = new CSVReader(new FileReader(crosswalkFile))
    reader.readAll().foreach( line => crosswalkHashMap += (line(0) -> line(1)))
    reader close()
  }

  def stemWords(word : String) : String =
    word split(" ") map( token => PorterStemmerTokenizerFactory.stem(token)) reduce( (s1, s2) => s1+" "+s2) trim

  def importAllCategories(categories : List[String]) = {
    categories.foreach( category => {
      val stemmedCategory = stemWords(category) toLowerCase
      if (stemmedCategoriesHashMap contains(stemmedCategory)) {
        stemmedCategoriesHashMap(stemmedCategory) += category
      } else {
        stemmedCategoriesHashMap += (stemmedCategory -> List(category))
      }
    })
  }
}
