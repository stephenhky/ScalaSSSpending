package home.kwyho.ss.finance.wrangler

import home.kwyho.ss.finance.daoobj.SSSpendDAO

import scala.collection.JavaConverters._

import com.google.gdata.client.spreadsheet.{FeedURLFactory, SpreadsheetService}
import com.google.gdata.data.spreadsheet.{WorksheetEntry, SpreadsheetEntry, SpreadsheetFeed}

import scala.collection.mutable

/**
 * Created by hok1 on 6/18/15.
 */
class SSSpendingSpreadsheetService(val service : SpreadsheetService, val year : String) {
  val factory : FeedURLFactory = FeedURLFactory.getDefault()
  val spreadsheetFeed : SpreadsheetFeed = service.getFeed(factory.getSpreadsheetsFeedUrl(), classOf[SpreadsheetFeed])
  val spreadsheetHashMap : Map[String, SpreadsheetEntry] = computeSSSpendingSpreadsheetHashMap()
  val currentSpreadsheet : SpreadsheetEntry = spreadsheetHashMap get(SSSpendDAO.yearHash.get(year).get) get
  val worksheetHashMap : Map[String, WorksheetEntry] = computeWorksheetHashMap(currentSpreadsheet)

  def computeSSSpendingSpreadsheetHashMap() : Map[String, SpreadsheetEntry] = {
    val spreadsheets : mutable.Buffer[SpreadsheetEntry] = spreadsheetFeed.getEntries.asScala
    val spreadsheetHashMap : mutable.Map[String, SpreadsheetEntry] = mutable.Map[String, SpreadsheetEntry]()
    spreadsheets.foreach( spreadsheetEntry => spreadsheetHashMap put(spreadsheetEntry.getKey, spreadsheetEntry))
    spreadsheetHashMap.toMap
  }

  def computeWorksheetHashMap(spreadsheet : SpreadsheetEntry) : Map[String, WorksheetEntry] = {
    val worksheetMutableMap : mutable.Map[String, WorksheetEntry] = mutable.Map[String, WorksheetEntry]()
    val worksheets : mutable.Buffer[WorksheetEntry] = spreadsheet.getWorksheets.asScala
    worksheets.foreach( worksheet => worksheet.getTitle.getPlainText match {
      case "Summary" => {worksheetMutableMap put("Summary", worksheet)}
      case month : String => {if (SSSpendDAO.calendarMonths.contains(month)) worksheetMutableMap put(month, worksheet)}
    })
    worksheetMutableMap.toMap
  }

  def getWorksheet(month : String) : WorksheetEntry = worksheetHashMap get(month) get
  def getSummaryWorksheet() : WorksheetEntry = worksheetHashMap get("Summary") get
}
