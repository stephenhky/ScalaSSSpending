package home.kwyho.ss.finance.daoobj

/**
 * Created by hok1 on 6/19/14.
 */
object SSSpendDAO {
  val yearHash = Map("2013" -> "1Sr_3CBxyc8NGCdPOXCqMVxeFe5zj1LDXUWdrCeMP6WE",
    "2014" -> "1EyG00bkrAuursG43Fo_KIsM3sPg3Lg4E0SI4AzkQOow",
    "2015" -> "1yKTRenG_CmCxmV4hHRwE-RXeC55RH7ahFpOjQ_rON6E")
  val calendarMonths = List("January", "February", "March", "April", "May", "June", "July", "August", "September",
    "October", "November", "December")
  val DataColumnHashMap : Map[Int, String] = Map(2 -> "Date", 3 -> "Place", 4 -> "Category", 5 -> "City", 6 -> "Debit",
    7 -> "Comment", 8 -> "Individual", 9 -> "PaymentMethod")
}
