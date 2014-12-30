package home.kwyho.ss.finance.daoobj

/**
 * Created by hok1 on 6/19/14.
 */
object SSSpendDAO {
  val yearHash = Map("2013" -> "t-cP5RjsrrdhW6qxupaT_xg", "2014" -> "tsXZvQtYkoE11jq-b1wxpYA",
    "2015" -> "tLrg3di8rs6V8W4sMSqhTaw")
  val calendarMonths = List("January", "February", "March", "April", "May", "June", "July", "August", "September",
    "October", "November", "December")
  val DataColumnHashMap : Map[Int, String] = Map(2 -> "Date", 3 -> "Place", 4 -> "Category", 5 -> "City", 6 -> "Debit",
    7 -> "Comment", 8 -> "Individual", 9 -> "PaymentMethod")
}
