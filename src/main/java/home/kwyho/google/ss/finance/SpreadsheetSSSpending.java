package home.kwyho.google.ss.finance;

import home.kwyho.google.ss.finance.authenticate.GoogleSpreadsheetAuthentication;
import home.kwyho.ss.finance.daoobj.DataObjects;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gdata.client.spreadsheet.FeedURLFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.SpreadsheetEntry;
import com.google.gdata.data.spreadsheet.SpreadsheetFeed;
import com.google.gdata.data.spreadsheet.WorksheetEntry;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import scala.collection.JavaConversions;

public class SpreadsheetSSSpending {
	private FeedURLFactory factory;
	private SpreadsheetFeed spreadsheetFeed;
	private SpreadsheetService service;
	private SpreadsheetEntry ssSpendingSpreadsheet;
	private HashMap<String, WorksheetEntry> hashWorksheets;
	private WorksheetEntry summaryWorksheet;
	private String year;
    private List<String> calendarMonths = JavaConversions.asJavaList(DataObjects.calendarMonths());
    private Map<String, String> spreadsheetIDHash = JavaConversions.asJavaMap(DataObjects.yearHash());
	
	public SpreadsheetSSSpending(FeedURLFactory factory, SpreadsheetFeed feed,
			SpreadsheetService service, String year) {
		super();
		this.factory = factory;
		this.spreadsheetFeed = feed;
		this.service = service;
		this.year = year;
		ssSpendingSpreadsheet = retrieveSSSpendingSpreadsheet();
		computeHashmap();
	}

	public SpreadsheetSSSpending(SpreadsheetService service, String year) throws IOException, ServiceException {
		super();
		this.service = service;
		this.year = year;
		factory = FeedURLFactory.getDefault();
		spreadsheetFeed = this.service.getFeed(factory.getSpreadsheetsFeedUrl(), SpreadsheetFeed.class);
		ssSpendingSpreadsheet = retrieveSSSpendingSpreadsheet();
		computeHashmap();
	}
	
	public SpreadsheetSSSpending(String username, String password, String year) throws AuthenticationException, IOException, ServiceException {
		this(GoogleSpreadsheetAuthentication.login(username, password), year);
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
				if (Arrays.asList(calendarMonths).contains(sheetName)) {
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
