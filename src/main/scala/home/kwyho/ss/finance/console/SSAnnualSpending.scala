package home.kwyho.ss.finance.console

import home.kwyho.ss.finance.daoobj.SSSpendDAO
import home.kwyho.google.ss.finance.SpreadsheetSSSpending
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import home.kwyho.ss.finance.wrangler.SSSpendingSpreadsheetWrangler
import home.kwyho.ss.finance.analytics.{CategoryNormalizer, SpendingAnalyzer}
import scala.collection.mutable.Map

/**
 * Created by hok1 on 6/20/14.
 */
object SSAnnualSpending {
  def main(args : Array[String]) {
    // User's input
    val console = System console()
    var gmailAddress : String = readLine("GMail address = ? ")
//    var password : String = readLine("Password = ? ")
//    print("Password = ? ")
//    var password : String = new String(console readPassword())
    var year : String = readLine("Year = ? ")
    if (!SSSpendDAO.yearHash.contains(year)) {
      System.exit(1)
    }

    // Connecting to Google
    println("Connecting to Google...")
    val ssSpendServiceWrapper : SpreadsheetSSSpending = new SpreadsheetSSSpending(gmailAddress, year)

    // Retrieving data and wrangling
    println("Retrieving data...")
    val spreadSheet : SpreadsheetEntry = ssSpendServiceWrapper.retrieveSSSpendingSpreadsheet()
    val wrangler : SSSpendingSpreadsheetWrangler = new SSSpendingSpreadsheetWrangler(spreadSheet)
    val monthlyEntries = SSSpendDAO.calendarMonths.map(
      month => wrangler getWorksheetSpendingData(ssSpendServiceWrapper getWorksheet(month)))

    // Language processing
    println("Natural language processing...")
    val normalizer : CategoryNormalizer = new CategoryNormalizer()
    monthlyEntries.foreach( entries => normalizer.importAllCategories(entries.map( entry => entry.category)))
    monthlyEntries.foreach( entries => entries.foreach( entry => entry.category = normalizer.normalize(entry.category)))

    // Outputting results
    println("Calculating...")
    val monthlyCategorizedSpendings : List[Map[String, Double]] = (0 to SSSpendDAO.calendarMonths.size-1).map( monthIdx =>
      SpendingAnalyzer.analyzeCategorizedSpending(monthlyEntries(monthIdx))).toList
    (0 to SSSpendDAO.calendarMonths.size-1).foreach( monthIdx => {
      println(SSSpendDAO.calendarMonths(monthIdx))
      val categorizedSpendings = monthlyCategorizedSpendings(monthIdx)
      categorizedSpendings.keySet.foreach( category => {
        println("\t"+category+" : "+categorizedSpendings(category))
      })
    })

    // Writing summary files
    println("Updating Summary...")
    wrangler writeSummaryToGoogleSpreadsheet( ssSpendServiceWrapper getSummaryWorksheet, monthlyCategorizedSpendings)

    println("Done.")
  }
}
