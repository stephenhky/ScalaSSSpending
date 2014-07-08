package home.kwyho.ss.finance.console

import home.kwyho.ss.finance.daoobj.SSSpendDAO
import home.kwyho.google.ss.finance.SpreadsheetSSSpending
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import home.kwyho.ss.finance.wrangler.SSSpendingSpreadsheetWrangler
import home.kwyho.ss.finance.analytics.CategoryNormalizer
import au.com.bytecode.opencsv.CSVWriter
import java.io.{File, FileWriter}

/**
 * Created by hok1 on 7/7/14.
 */
object SSSpendingDataFileRetrieval {
  def main(args : Array[String]) {
    // User's input
    val console = System console()
    var gmailAddress : String = readLine("GMail address = ? ")
//    var password : String = readLine("Password = ? ")
    print("Password = ? ")
    var password : String = new String(console readPassword())
    var year : String = readLine("Year = ? ")
    if (!SSSpendDAO.yearHash.contains(year)) {
      System.exit(1)
    }
    var outputfilename : String = readLine("Output filename = ? ")

    // Connecting to Google
    println("Connecting to Google...")
    val ssSpendServiceWrapper : SpreadsheetSSSpending = new SpreadsheetSSSpending(gmailAddress, password, year)

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
    println("Writing...")
    val writer : CSVWriter = new CSVWriter(new FileWriter(new File(outputfilename)))
    val headers : Array[String] = Array("Date", "Place", "Category", "City", "Debit", "Comment", "Individual", "PaymentMethod")
    writer writeNext(headers)
    monthlyEntries.foreach( entries => entries.foreach( entry => {
      val rowToWrite : Array[String] = Array(entry.date.getValue, entry.place, entry.category, entry.city,
        entry.debit.toString, entry.comment, entry.individual, entry.paymentMethod)
      writer writeNext(rowToWrite)
    }))
    writer close()

    println("Done.")
  }
}
