package home.kwyho.ss.finance.authenticate

import java.io.File

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.json.jackson.JacksonFactory

import scala.collection.JavaConverters._
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gdata.client.spreadsheet.SpreadsheetService

// New way of authentication. Refer to https://stackoverflow.com/questions/30483601/create-spreadsheet-using-google-spreadsheet-api-in-google-drive-in-java?newreg=e6435ad6891a4c1f8bbe76f4fb22fd64
// http://stackoverflow.com/questions/13257163/java-google-api-analytics-unable-to-obtain-new-access-token-using-refresh
// https://github.com/google/google-api-java-client#Authorization_Code_Flow
object GoogleSpreadsheetOAuth2Authentication {
  def login(username : String, password : String) : SpreadsheetService = {
    val httpTransport : HttpTransport = new NetHttpTransport()
    val jsonFactory : JacksonFactory = new JacksonFactory()
    val SCOPES = List("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds").asJava
    val credential : GoogleCredential = new GoogleCredential.Builder()
      .setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setServiceAccountId(username)
      .setServiceAccountScopes(SCOPES)
      .setServiceAccountPrivateKeyFromP12File(new File("ScalaSSSpending-1b7c3965aa13.p12"))
      .build()
    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
    service.setOAuth2Credentials(credential)
    service
  }

  def login(username : String, password : String, p12file : File) : SpreadsheetService = {
    null
  }
}
