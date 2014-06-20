package home.kwyho.google.ss.finance.authenticate;

import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.util.AuthenticationException;

public class GoogleSpreadsheetAuthentication {
	public static SpreadsheetService login(String username, String password) throws AuthenticationException {
		SpreadsheetService service = new SpreadsheetService("SSSpend");
		service.setUserCredentials(username, password);
		return service;
	}
}
