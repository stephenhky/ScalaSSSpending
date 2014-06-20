package home.kwyho.ss.finance.wrangler

import com.google.gdata.data.spreadsheet.{CellEntry, CellFeed, WorksheetEntry, SpreadsheetEntry}
import java.net.URL
import com.google.gdata.client.spreadsheet.CellQuery
import scala.collection.mutable.Buffer
import scala.collection.JavaConversions._
import home.kwyho.ss.finance.daoobj.SSSpendDAO

/**
 * Created by hok1 on 6/20/14.
 */
class SSSpendingSpreadsheetWrangler(ssEntry : SpreadsheetEntry) {
  val spreadsheetEntry = ssEntry
  val columnHashMap : Map[String, Int] = SSSpendDAO.DataColumnHashMap

  def getWorksheetSpendingData(worksheet : WorksheetEntry) : Any = {
    val cellFeedUrl : URL = worksheet getCellFeedUrl
    val rowCount : Int = worksheet getRowCount

    var cellQuery : CellQuery = new CellQuery(cellFeedUrl)
    cellQuery setMinimumRow(2)
    cellQuery setMaximumRow(rowCount)
    cellQuery setMinimumCol(columnHashMap("Date"))
    cellQuery setMaximumCol(columnHashMap("PaymentMethod"))
    val feed : CellFeed = ssEntry.getService().query(cellQuery, classOf[CellFeed])
    val cellEntries : Buffer[CellEntry] = feed getEntries
  }
}
