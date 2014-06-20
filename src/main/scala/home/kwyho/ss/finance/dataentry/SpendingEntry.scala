package home.kwyho.ss.finance.dataentry

import com.google.gdata.data.dublincore.Date

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
}
