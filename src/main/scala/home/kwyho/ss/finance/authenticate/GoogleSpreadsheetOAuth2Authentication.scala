package home.kwyho.ss.finance.authenticate

import java.io._
import java.util

import com.google.api.client.auth.oauth2.{TokenResponse, AuthorizationCodeFlow}
import com.google.api.client.googleapis.auth.oauth2.{GoogleCredential, GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson.JacksonFactory

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gdata.client.spreadsheet.SpreadsheetService

import scala.io.{Codec, Source}
import scala.util.parsing.json._

// New way of authentication. Refer to https://stackoverflow.com/questions/30483601/create-spreadsheet-using-google-spreadsheet-api-in-google-drive-in-java?newreg=e6435ad6891a4c1f8bbe76f4fb22fd64
// http://stackoverflow.com/questions/13257163/java-google-api-analytics-unable-to-obtain-new-access-token-using-refresh
// https://github.com/google/google-api-java-client#Authorization_Code_Flow
// Available scopes: https://developers.google.com/gmail/api/auth/scopes
// Access token request: https://developers.google.com/identity/protocols/OAuth2InstalledApp
// Answer at bottom: http://stackoverflow.com/questions/12521385/how-to-authenticate-google-drive-without-requiring-the-user-to-copy-paste-auth-c
object GoogleSpreadsheetOAuth2Authentication {

  def login(username : String, clientSecretJsonFile : File) : SpreadsheetService = {
    val SCOPES = util.Arrays.asList("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds")
    val REDIRECT_URI : String = "http://localhost"

    // define initial object
    val jsonFactory : JsonFactory = new JacksonFactory()
    val httpTransport : HttpTransport = new NetHttpTransport()

    // dealing Google JSON file
//    val jsonStream1 : InputStream = GoogleSpreadsheetOAuth2Authentication.getClass.getResourceAsStream("client_secret_32485935939-p43vo057gp5mdp9cu1k03qcfudk7lv2g.apps.googleusercontent.com.json")
    val jsonStr : String = Source.fromFile(clientSecretJsonFile)(Codec.UTF8).getLines().reduce((s1, s2) => s1+s2)
    val jsonObj : Any = JSON.parseFull(jsonStr)
    val jsonMap : Map[String, String] = (jsonObj match {
      case Some(m: Map[String, Map[String, String]]) => m.get("web").get
      case None => Map()
    })

    // Google OAuth 2.0 authorization flow
    val jsonStream : InputStream = new FileInputStream(clientSecretJsonFile)
    val clientsSecrets : GoogleClientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(jsonStream))
    val authorizationCodeFlow : AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport, jsonFactory, clientsSecrets, SCOPES
    ).setAccessType("offline").setApprovalPrompt("auto").build()
    val url : String = authorizationCodeFlow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build()
    println(url)
    var code: String = readLine("code = ? ")
    val response : TokenResponse = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute()
    val credential : GoogleCredential = new GoogleCredential.Builder().setTransport(httpTransport)
                                          .setJsonFactory(jsonFactory)
                                          .setClientSecrets(jsonMap("client_id"), jsonMap("client_secret"))
                                          .build()
                                          .setFromTokenResponse(response)

    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
    service.setOAuth2Credentials(credential)
    service
  }

}
