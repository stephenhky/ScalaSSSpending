package home.kwyho.google.ss.finance.authenticate;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.AuthenticationException;

// no longer supported by Google. Refer to https://stackoverflow.com/questions/30483601/create-spreadsheet-using-google-spreadsheet-api-in-google-drive-in-java?newreg=e6435ad6891a4c1f8bbe76f4fb22fd64
@Deprecated
public class GoogleSpreadsheetAuthentication {
	public static SpreadsheetService login(String username, String password) throws AuthenticationException {
		SpreadsheetService service = new SpreadsheetService("SSSpend");
		service.setUserCredentials(username, password);
		return service;
	}
}
