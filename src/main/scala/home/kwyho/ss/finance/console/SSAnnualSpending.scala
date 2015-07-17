package home.kwyho.ss.finance.console

import java.io.File

import com.google.gdata.client.spreadsheet.SpreadsheetService
import home.kwyho.ss.finance.authenticate.GoogleSpreadsheetOAuth2Authentication
import home.kwyho.ss.finance.daoobj.SSSpendDAO
import com.google.gdata.data.spreadsheet.SpreadsheetEntry
import home.kwyho.ss.finance.wrangler.{SSSpendingSpreadsheetService, SSSpendingSpreadsheetWrangler}
import home.kwyho.ss.finance.analytics.{CategoryNormalizer, SpendingAnalyzer}
import scala.collection.mutable

/**
 * Created by hok1 on 6/20/14.
 */
object SSAnnualSpending {
  def main(args : Array[String]) {
    // Args
    if (args.length<1) {
      System.out.println("Need client secret file.")
      System.exit(1)
    }
    val clientSecretFile : File = new File(args(0))
    val year : String = if (args.length<2) readLine("Year = ? ") else args(1)
    if (!SSSpendDAO.yearHash.contains(year)) {
      System.out.println("Data for year "+year+" does not exist.")
      System.exit(1)
    }
    val reusableTokenFile : File = if (args.length>=3) {new File(args(2))} else null
    var gmailAddress : String = ""
    var accessToken : String = ""
    var refreshToken : String = ""
    if (reusableTokenFile==null) {
      // User's input
      gmailAddress = readLine("GMail address = ? ")
      accessToken = readLine("Access token = ? ")
      refreshToken = readLine("Refresh token = ? ")
    } else {
      val reuseTokensMap : Map[String, String] = GoogleSpreadsheetOAuth2Authentication.getReusableTokenJSONMap(reusableTokenFile)
      gmailAddress = reuseTokensMap("username")
      accessToken = reuseTokensMap("accesstoken")
      refreshToken = reuseTokensMap("refreshtoken")
    }

    // Connecting to Google
    println("Connecting to Google...")
    val authService : SpreadsheetService = GoogleSpreadsheetOAuth2Authentication.login(gmailAddress, clientSecretFile, accessToken, refreshToken)
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
    println("Calculating...")
    val monthlyCategorizedSpendings : List[mutable.Map[String, Double]] = (0 to SSSpendDAO.calendarMonths.size-1).map( monthIdx =>
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
    wrangler writeSummaryToGoogleSpreadsheet( ssSpendService getSummaryWorksheet(), monthlyCategorizedSpendings)

    println("Done.")
  }
}
