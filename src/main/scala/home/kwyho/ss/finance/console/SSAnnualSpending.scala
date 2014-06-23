package home.kwyho.ss.finance.console

import home.kwyho.ss.finance.daoobj.SSSpendDAO
import home.kwyho.google.ss.finance.SpreadsheetSSSpending
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import home.kwyho.ss.finance.wrangler.SSSpendingSpreadsheetWrangler
import home.kwyho.ss.finance.analytics.SpendingAnalyzer

/**
 * Created by hok1 on 6/20/14.
 */
object SSAnnualSpending {
  def main(args : Array[String]) {
    var gmailAddress : String = readLine("GMail address = ? ")
    var password : String = readLine("Password = ? ")
    var year : String = readLine("Year = ? ")
    if (!SSSpendDAO.yearHash.contains(year)) {
      System.exit(1)
    }
    val ssSpendServiceWrapper : SpreadsheetSSSpending = new SpreadsheetSSSpending(gmailAddress, password, year)
    val spreadSheet : SpreadsheetEntry = ssSpendServiceWrapper.retrieveSSSpendingSpreadsheet()
    val wrangler : SSSpendingSpreadsheetWrangler = new SSSpendingSpreadsheetWrangler(spreadSheet)
    val monthlyEntries = SSSpendDAO.calendarMonths.map(
      month => wrangler getWorksheetSpendingData(ssSpendServiceWrapper getWorksheet(month)))

    (0 to SSSpendDAO.calendarMonths.size-1).foreach( monthIdx => {
      println(SSSpendDAO.calendarMonths(monthIdx))
      val entries = monthlyEntries(monthIdx)
      val categorizedSpendings = SpendingAnalyzer.analyzeCategorizedSpending(entries)
      categorizedSpendings.keySet.foreach( category => {
        println("\t"+category+" : "+categorizedSpendings(category))
      })
    })
  }
}
