package home.kwyho.ss.finance.analytics

import java.io.{FileReader, File}
import au.com.bytecode.opencsv.CSVReader
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
      val stemmedCategory : String = stemWords(category).toLowerCase()
      if (stemmedCategoriesHashMap.contains(stemmedCategory)) {
        val categories : List[String] = stemmedCategoriesHashMap(stemmedCategory)
        stemmedCategoriesHashMap(stemmedCategory) = categories ::: List(category)
      } else {
        stemmedCategoriesHashMap += (stemmedCategory -> List(category))
      }
    })
  }

  def chooseBestWord(words : List[String]) : String = {
    def getTagLabels(word : String) : List[String] =
      word.split(" ").toList.map( token => {
        val taggedTokens : String = tagger tagString(token)
        taggedTokens substring(taggedTokens.lastIndexOf('_')+1)
      })

    def isCapitalized(word : String) : Boolean = if (word.length > 0) {
      word.split(" ").map( token => Character.isUpperCase(token charAt(0))).reduce( (b1, b2) => b1 & b2)
    } else {false}


    def score(word : String) : Int = {
      val tagLabels : List[String] = getTagLabels(word)
      var score : Int = 0
      if (tagLabels contains("VBG")) score += 1  // Rule 1: prefer '-ing' ending
      if (isCapitalized(word)) score += 1  // Rule 2: prefer capitalized start
      if ((tagLabels contains("NN")) | (tagLabels contains("NNP"))) score += 1  // Rule 3: prefer singular
      score
    }

    words maxBy( score)
  }

  def normalize(category : String) : String = {
    var stemmedCategory : String = stemWords(category toLowerCase)
    if (crosswalkHashMap contains(stemmedCategory)) stemmedCategory = crosswalkHashMap(stemmedCategory)
    if (stemmedCategoriesHashMap contains(stemmedCategory)) {
      chooseBestWord(stemmedCategoriesHashMap(stemmedCategory))
    } else {
      category
    }
  }
}
