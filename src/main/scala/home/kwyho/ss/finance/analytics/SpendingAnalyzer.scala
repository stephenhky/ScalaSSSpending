package home.kwyho.ss.finance.analytics

import home.kwyho.ss.finance.dataentry.SpendingEntry
import scala.collection.mutable.Map

/**
 * Created by hok1 on 6/23/14.
 */
object SpendingAnalyzer {
  def analyzeCategorizedSpending(entries : List[SpendingEntry]) : Map[String, Double] = {
    var categorizedSpendings : Map[String, Double] = Map()
    entries.foreach( entry => {
      if (categorizedSpendings.contains(entry.category)) {
        categorizedSpendings(entry.category) += entry.debit
      } else {
        categorizedSpendings += (entry.category -> entry.debit)
      }
    })
    categorizedSpendings
  }

  def mergeTwoCategoriesSpendingMaps(catSpend1 : Map[String, Double], catSpend2 : Map[String, Double]) = {
    val allCategories : Set[String] = catSpend1.keySet.toSet | catSpend2.keySet.toSet
    var mergedCategorizedSpending : Map[String, Double] = Map()
    def getSpending(category : String, catSpend : Map[String, Double]) : Double
      = if (catSpend.contains(category)) {catSpend(category)} else {0.0}

    allCategories.foreach( category =>
      mergedCategorizedSpending(category) = getSpending(category, catSpend1)+getSpending(category, catSpend2))
    mergedCategorizedSpending
  }

  def calculateIndividualSpending(entries : List[SpendingEntry], individual : String) : Double
    = entries.filter(e => e.individual==individual).map(e => e.debit).reduce((s1,s2)=> s1+s2)
}
