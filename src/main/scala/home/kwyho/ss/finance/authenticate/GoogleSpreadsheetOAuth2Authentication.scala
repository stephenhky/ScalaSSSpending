package home.kwyho.ss.finance.authenticate

import java.io.{InputStream, InputStreamReader}
import java.util

import com.google.api.client.auth.oauth2.{TokenResponse, Credential, AuthorizationCodeFlow}
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.{GoogleTokenResponse, GoogleAuthorizationCodeFlow, GoogleClientSecrets}
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson.JacksonFactory

import com.google.api.client.http.HttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.gdata.client.authn.oauth.{OAuthSigner, OAuthHmacSha1Signer, GoogleOAuthParameters}
import com.google.gdata.client.spreadsheet.SpreadsheetService

// New way of authentication. Refer to https://stackoverflow.com/questions/30483601/create-spreadsheet-using-google-spreadsheet-api-in-google-drive-in-java?newreg=e6435ad6891a4c1f8bbe76f4fb22fd64
// http://stackoverflow.com/questions/13257163/java-google-api-analytics-unable-to-obtain-new-access-token-using-refresh
// https://github.com/google/google-api-java-client#Authorization_Code_Flow
// Available scopes: https://developers.google.com/gmail/api/auth/scopes
// Access token request: https://developers.google.com/identity/protocols/OAuth2InstalledApp
// Answer at bottom: http://stackoverflow.com/questions/12521385/how-to-authenticate-google-drive-without-requiring-the-user-to-copy-paste-auth-c
object GoogleSpreadsheetOAuth2Authentication {

  def login(username : String) : SpreadsheetService = {
    val SCOPES = util.Arrays.asList("https://spreadsheets.google.com/feeds", "https://docs.google.com/feeds")
    val REDIRECT_URI : String = "http://localhost"
//    val REDIRECT_URI : String = "urn:ietf:wg:oauth:2.0:oob"

    val jsonFactory : JsonFactory = new JacksonFactory()
    val httpTransport : HttpTransport = new NetHttpTransport()

    val jsonStream : InputStream = GoogleSpreadsheetOAuth2Authentication.getClass.getResourceAsStream("ScalaSSSpending-b9fdae2be0c0.json")
    val clientsSecrets : GoogleClientSecrets = GoogleClientSecrets.load(jsonFactory, new InputStreamReader(jsonStream))

    val authorizationCodeFlow : AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow.Builder(
      httpTransport, jsonFactory, clientsSecrets, SCOPES
    ).setAccessType("offline").setApprovalPrompt("auto").build()
//    val url : String = authorizationCodeFlow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build()
//    println(url)
//    var code: String = readLine("code = ? ")
//    val response : TokenResponse = authorizationCodeFlow.newTokenRequest(code).setRedirectUri(REDIRECT_URI).execute()
//    val credential : Credential = authorizationCodeFlow.loadCredential(username).setFromTokenResponse(response)
    val app : AuthorizationCodeInstalledApp = new AuthorizationCodeInstalledApp(authorizationCodeFlow, new LocalServerReceiver())
    val credential : Credential = app.authorize(username)

    val service : SpreadsheetService = new SpreadsheetService("ScalaSSSpend")
    service.setOAuth2Credentials(credential)
    service
  }

}
