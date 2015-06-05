package home.kwyho.google.ss.finance;

import home.kwyho.ss.finance.authenticate.GoogleSpreadsheetOAuth2Authentication;
import home.kwyho.ss.finance.daoobj.SSSpendDAO;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.ServiceException;

public class SpreadsheetSSSpending {
	private FeedURLFactory factory;
	private SpreadsheetFeed spreadsheetFeed;
	private SpreadsheetService service;
	private SpreadsheetEntry ssSpendingSpreadsheet;
	private HashMap<String, WorksheetEntry> hashWorksheets;
	private WorksheetEntry summaryWorksheet;
	private String year;
    private List<String> calendarMonths = SSSpendDAO.calendarMonthsInJava();
    private Map<String, String> spreadsheetIDHash = SSSpendDAO.yearHashInJava();
	
	public SpreadsheetSSSpending(FeedURLFactory factory, SpreadsheetFeed feed,
			SpreadsheetService service, String year) {
		this.factory = factory;
		this.spreadsheetFeed = feed;
		this.service = service;
		this.year = year;
		ssSpendingSpreadsheet = retrieveSSSpendingSpreadsheet();
		computeHashmap();
	}

	public SpreadsheetSSSpending(SpreadsheetService service, String year) throws IOException, ServiceException {
		this.service = service;
		this.year = year;
		factory = FeedURLFactory.getDefault();
		spreadsheetFeed = this.service.getFeed(factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);
		ssSpendingSpreadsheet = retrieveSSSpendingSpreadsheet();
		computeHashmap();
	}
	
	public SpreadsheetSSSpending(String username, String password, String year) throws IOException, ServiceException {
		this(GoogleSpreadsheetOAuth2Authentication.login(username, password), year);
	}
	
	public SpreadsheetService getService() {
		return service;
	}

	public SpreadsheetEntry retrieveSSSpendingSpreadsheet() {
		List<SpreadsheetEntry> spreadsheets = spreadsheetFeed.getEntries();
		for (SpreadsheetEntry spreadsheet: spreadsheets) {
			if (spreadsheet.getId().indexOf(spreadsheetIDHash.get(year)) != -1) {
				return spreadsheet;
			}
		}
		return null;
	}

	public SpreadsheetEntry getSSSpendingSpreadsheet() {
		return ssSpendingSpreadsheet;
	}
	
	private void computeHashmap() {
		hashWorksheets = new HashMap<String, WorksheetEntry>();
		List<WorksheetEntry> worksheets;
		try {
			worksheets = ssSpendingSpreadsheet.getWorksheets();
			for (WorksheetEntry worksheet: worksheets) {
				String sheetName = worksheet.getTitle().getPlainText();
				if (calendarMonths.contains(sheetName)) {
					hashWorksheets.put(sheetName, worksheet);
				} else if (sheetName.equals("Summary")) {
					summaryWorksheet = worksheet;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WorksheetEntry getWorksheet(String month) {
		return hashWorksheets.get(month);
	}

	public WorksheetEntry getSummaryWorksheet() {
		return summaryWorksheet;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

}
