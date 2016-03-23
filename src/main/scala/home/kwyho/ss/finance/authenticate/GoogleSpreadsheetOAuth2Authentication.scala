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

// New way of authentication. Refer to
// http://stackoverflow.com/questions/13257163/java-google-api-analytics-unable-to-obtain-new-access-token-using-refresh
// https://github.com/google/google-api-java-client#Authorization_Code_Flow
// Available scopes: https://developers.google.com/gmail/api/auth/scopes
// Access token request: https://developers.google.com/identity/protocols/OAuth2InstalledApp
//
// About reusing access token:
//    http://stackoverflow.com/questions/12521385/how-to-authenticate-google-drive-without-requiring-the-user-to-copy-paste-auth-c
//
// Updated: Mar 23, 2016
// Note that OAuth 2.0 code changed on March 3. Refer to:
//   https://developers.google.com/api-client-library/java/google-api-java-client/oauth2#service_accounts
object GoogleSpreadsheetOAuth2Authentication {
  val SCOPES = util.Arrays.asList("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds")
  val REDIRECT_URI : String = "http://localhost"

  // define initial object
  val jsonFactory : JsonFactory = new JacksonFactory()
  val httpTransport : HttpTransport = new NetHttpTransport()

  def getReusableTokenJSONMap(reusableJSONFile : File) : Map[String, String] = {
    val jsonStr : String = Source.fromFile(reusableJSONFile)(Codec.UTF8).getLines().reduce((s1, s2) => s1+s2)
    val jsonObj : Any = JSON.parseFull(jsonStr)
    val tokenMap : Map[String, String] = (jsonObj match {
      case Some(m: Map[String, String]) => m
      case None => Map()
    })
    tokenMap
  }

  def getSecretFileJSONMap(clientSecretJsonFile : File) : Map[String, String] = {
    // dealing Google JSON file
    val jsonStr : String = Source.fromFile(clientSecretJsonFile)(Codec.UTF8).getLines().reduce((s1, s2) => s1+s2)
    val jsonObj : Any = JSON.parseFull(jsonStr)
    val jsonMap : Map[String, String] = (jsonObj match {
//      case Some(m: Map[String, Map[String, String]]) => m.get("web").get
      case Some(m: Map[String, String]) => m
      case None => Map()
    })
    jsonMap
  }

  def extractClientsSecrets(clientSecretJsonFile : File) : GoogleClientSecrets = {
    // Google OAuth 2.0 authorization flow
    val jsonStream : InputStream = new FileInputStream(clientSecretJsonFile)
    val clientsSecrets : GoogleClientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(jsonStream))

    clientsSecrets
  }

  def retrieveNewCredential(clientsSecrets : GoogleClientSecrets, jsonMap : Map[String, String]) : GoogleCredential = {
    // retrieve new credential
    val authorizationCodeFlow : AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport, jsonFactory, clientsSecrets, SCOPES
    ).setAccessType("offline").setApprovalPrompt("auto").build()
    val url : String = authorizationCodeFlow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build()

    // ask user to open the browser with the printed url, and get the code
    println("Open this in browser: "+url)
    var code: String = readLine("code = ? ")
    val response : TokenResponse = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute()
    val credential : GoogleCredential = new GoogleCredential.Builder().setTransport(httpTransport)
      .setJsonFactory(jsonFactory)
      .setClientSecrets(jsonMap("client_id"), jsonMap("client_secret"))
      .build()
      .setFromTokenResponse(response)
    println("Access token = "+credential.getAccessToken)
    println("Refresh token = "+credential.getRefreshToken)

    credential
  }

  def reuseCredential(clientsSecrets : GoogleClientSecrets, jsonMap : Map[String, String],
                       accessToken : String, refreshToken : String) : GoogleCredential = {
    val credential : GoogleCredential = new GoogleCredential.Builder().setJsonFactory(jsonFactory)
      .setTransport(httpTransport).setClientSecrets(jsonMap("client_id"), jsonMap("client_secret"))
      .build()
    credential.setAccessToken(accessToken)
    credential.setRefreshToken(refreshToken)

    credential
  }

  def login(username : String, clientSecretJsonFile : File,
            accessToken : String = "", refreshToken : String = "") : SpreadsheetService = {

    val clientSecrets : GoogleClientSecrets = extractClientsSecrets(clientSecretJsonFile)

    val credential : GoogleCredential = if (accessToken.length==0 & refreshToken.length==0) {
      retrieveNewCredential(clientSecrets, getSecretFileJSONMap(clientSecretJsonFile))
    } else {
      reuseCredential(clientSecrets, getSecretFileJSONMap(clientSecretJsonFile), accessToken, refreshToken)
    }

    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
    service.setOAuth2Credentials(credential)
    service
  }

}
