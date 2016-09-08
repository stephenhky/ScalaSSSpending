package home.kwyho.ss.finance.console


import com.google.gdata.client.spreadsheet.SpreadsheetService
import home.kwyho.ss.finance.authenticate.GoogleSpreadsheetOAuth2Authentication
import home.kwyho.ss.finance.daoobj.SSSpendDAO
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import home.kwyho.ss.finance.wrangler.{SSSpendingSpreadsheetService, SSSpendingSpreadsheetWrangler}
import home.kwyho.ss.finance.analytics.CategoryNormalizer
import java.io.{File, FileWriter}

import com.opencsv.CSVWriter

/**
 * Created by hok1 on 7/7/14.
 */
object SSSpendingDataFileRetrieval {
  def main(args : Array[String]) {
    // Args
    if (!(args.length<1)) {
      System.out.println("Need client secret file.")
    }
    val clientSecretFile : File = new File(args(0))

    // User's input
    val gmailAddress : String = scala.io.StdIn.readLine("GMail address = ? ")
    val year : String = scala.io.StdIn.readLine("Year = ? ")
    if (!SSSpendDAO.yearHash.contains(year)) {
      System.exit(1)
    }
    var outputfilename : String = scala.io.StdIn.readLine("Output filename = ? ")

    // Connecting to Google
    println("Connecting to Google...")
    val authService : SpreadsheetService = GoogleSpreadsheetOAuth2Authentication.login(gmailAddress, clientSecretFile)
    val ssSpendService : SSSpendingSpreadsheetService = new SSSpendingSpreadsheetService(authService, year)

    // Retrieving data and wrangling
    println("Retrieving data...")
    val spreadsheet : SpreadsheetEntry = ssSpendService currentSpreadsheet
    val wrangler : SSSpendingSpreadsheetWrangler = new SSSpendingSpreadsheetWrangler(spreadsheet)
    val monthlyEntries = SSSpendDAO.calendarMonths.map(month => wrangler getWorksheetSpendingData( ssSpendService getWorksheet(month)))

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
