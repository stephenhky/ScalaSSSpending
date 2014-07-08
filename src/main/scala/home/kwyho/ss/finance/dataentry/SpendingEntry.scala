package home.kwyho.ss.finance.dataentry

import com.google.gdata.data.dublincore.Date
import com.google.gdata.data.spreadsheet.Cell
import home.kwyho.ss.finance.daoobj.SSSpendDAO

/**
 * Created by hok1 on 6/20/14.
 */
class SpendingEntry {
  var date : Date = new Date()
  var place : String = ""
  var category : String = ""
  var city : String = ""
  var debit : Double = 0
  var comment : String = ""
  var individual : String = ""
  var paymentMethod : String = ""

  def isEmpty : Boolean = {
    place=="" & category=="" & city=="" & debit==0 & comment=="" & individual=="" & paymentMethod==""
  }

  def setField(cell : Cell) = {
    val attribute : String = SSSpendDAO DataColumnHashMap(cell getCol)
    attribute match {
      case "Date" => date = new Date(cell getValue)
      case "Place" => place = cell getValue
      case "Category" => category = cell getValue
      case "City" => city = cell getValue
      case "Debit" => debit = cell getDoubleValue
      case "Comment" => comment = cell getValue
      case "Individual" => individual = cell getValue
      case "PaymentMethod" => paymentMethod = cell getValue
    }
  }
}
