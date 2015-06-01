package home.kwyho.ss.finance.authenticate

import java.io.{InputStreamReader, File}
import java.util.Collections

import com.google.api.client.auth.oauth2.{Credential, AuthorizationCodeFlow}
import com.google.api.client.extensions.appengine.datastore.AppEngineDataStoreFactory
import com.google.api.client.extensions.appengine.http.UrlFetchTransport
import com.google.api.client.googleapis.auth.oauth2.{GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson.JacksonFactory

import scala.collection.JavaConverters._
import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gdata.client.spreadsheet.SpreadsheetService

// New way of authentication. Refer to https://stackoverflow.com/questions/30483601/create-spreadsheet-using-google-spreadsheet-api-in-google-drive-in-java?newreg=e6435ad6891a4c1f8bbe76f4fb22fd64
// http://stackoverflow.com/questions/13257163/java-google-api-analytics-unable-to-obtain-new-access-token-using-refresh
// https://github.com/google/google-api-java-client#Authorization_Code_Flow
object GoogleSpreadsheetOAuth2Authentication {
//  def login(username : String, password : String) : SpreadsheetService = {
//    val httpTransport : HttpTransport = new NetHttpTransport()
//    val jsonFactory : JacksonFactory = new JacksonFactory()
//    val SCOPES = List("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds").asJava
//    val credential : GoogleCredential = new GoogleCredential.Builder()
//      .setTransport(httpTransport)
//      .setJsonFactory(jsonFactory)
//      .setServiceAccountId(username)
//      .setServiceAccountScopes(SCOPES)
//      .setServiceAccountPrivateKeyFromP12File(new File("ScalaSSSpending-1b7c3965aa13.p12"))
//      .build()
//    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
//    service.setOAuth2Credentials(credential)
//    service
//  }

  def login(username : String, password : String) : SpreadsheetService = {
    val SCOPES = List("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds").asJava

    val jsonFactory : JsonFactory = new JacksonFactory()
    val httpTransport : HttpTransport = new UrlFetchTransport()
    val clientsSecrets : GoogleClientSecrets = GoogleClientSecrets
      .load(jsonFactory, new InputStreamReader(GoogleSpreadsheetOAuth2Authentication
                                                .getClass
                                                .getResourceAsStream("ScalaSSSpending-b9fdae2be0c0.json")))

    val authorizationCodeFlow : AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport, jsonFactory, clientsSecrets, SCOPES
    ).setDataStoreFactory(new AppEngineDataStoreFactory()).build()

    val credential : Credential = authorizationCodeFlow.loadCredential(username)

    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
    service.setOAuth2Credentials(credential)
    service
  }

}
