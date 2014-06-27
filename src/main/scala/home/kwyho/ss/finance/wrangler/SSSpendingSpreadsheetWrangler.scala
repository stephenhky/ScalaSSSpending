package home.kwyho.ss.finance.wrangler

import com.google.gdata.data.spreadsheet._
import java.net.URL
import com.google.gdata.client.spreadsheet.CellQuery
import scala.collection.mutable.{Buffer, Map}
import scala.collection.JavaConversions._
import home.kwyho.ss.finance.daoobj.SSSpendDAO
import home.kwyho.ss.finance.dataentry.SpendingEntry
import home.kwyho.ss.finance.analytics.SpendingAnalyzer

/**
 * Created by hok1 on 6/20/14.
 */
class SSSpendingSpreadsheetWrangler(ssEntry : SpreadsheetEntry) {
  val spreadsheetEntry = ssEntry
  val columnHashMap = SSSpendDAO.DataColumnHashMap

  def getWorksheetSpendingData(worksheet : WorksheetEntry) : List[SpendingEntry] = {
    val cellFeedUrl : URL = worksheet getCellFeedUrl
    val rowCount : Int = worksheet getRowCount

    var cellQuery : CellQuery = new CellQuery(cellFeedUrl)
    cellQuery setMinimumRow(3)
    cellQuery setMaximumRow(rowCount)
    cellQuery setMinimumCol(columnHashMap.keySet.min)
    cellQuery setMaximumCol(columnHashMap.keySet.max)
    val feed : CellFeed = ssEntry.getService().query(cellQuery, classOf[CellFeed])
    val cellEntries : Buffer[CellEntry] = feed getEntries

    val entries : IndexedSeq[SpendingEntry] = (3 to rowCount).map(idx => new SpendingEntry)
    cellEntries.foreach( cellEntry => entries(cellEntry.getCell.getRow-2).setField(cellEntry getCell))

    entries.toList
  }

  def writeSummaryToGoogleSpreadsheet(summaryWorksheet : WorksheetEntry,
                                      monthlyCategorizedSpendings : List[Map[String, Double]]) = {
    val cellFeedUrl : URL = summaryWorksheet getCellFeedUrl
    val annualCategorizedSpendings = monthlyCategorizedSpendings.reduce( (m1, m2) =>
      SpendingAnalyzer.mergeTwoCategoriesSpendingMaps(m1, m2))

    

  }
}
