package utilities

import anorm._
import play.api.db.DB
import play.api.Logger
import play.api.Play.current

/**
 * A utility object for general purpose database operations
 */
object DatabaseOperations {
  /**
   * Creates a new item in the database
   *
   * @param insertSql Sql for inserting the item
   * @param obtainSql Sql for obtaining the created item back from the database
   * @param rowParser Row parser to parse the query result
   * @param itemName  Name of the item to use in logging
   *
   * @tparam T  Type of the item
   *
   * @return  An optional item
   */
  def create[T](insertSql: Sql, obtainSql: Sql, rowParser: RowParser[T], itemName: String): Option[T] = {
    try {
      DB.withConnection { implicit c =>
        if(insertSql.executeUpdate() > 0)
          obtainSql.as(rowParser *).headOption
        else {
          Logger.error(s"$itemName.create() - Couldn't create $itemName, insert failed!")
          None
        }
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"$itemName.create() - ${e.getMessage}")
        None
    }
  }

  /**
   * Reads an item from the database
   *
   * @param readSql   Sql for reading the item from the database
   * @param rowParser Row parser to parse the query result
   * @param itemName  Name of the item to use in logging
   *
   * @tparam T  Type of the item
   *
   * @return  An optional item
   */
  def read[T](readSql: Sql, rowParser: RowParser[T], itemName: String): Option[T] = {
    try {
      DB.withConnection { implicit c =>
        readSql.as(rowParser *).headOption
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"$itemName.read() - ${e.getMessage}")
        None
    }
  }

  /**
   * Deletes an item from the database
   *
   * @param deleteSql Sql for deleting the item
   * @param itemName  Name of the item to use in logging
   *
   * @tparam T  Type of the item
   *
   * @return  true if successful, false otherwise
   */
  def delete[T](deleteSql: Sql, itemName: String): Boolean = {
    try {
      DB.withConnection { implicit c =>
        deleteSql.executeUpdate() > 0
      }
    }
    catch {
      case e: Exception =>
        Logger.error(s"$itemName.delete() - ${e.getMessage}")
        false
    }
  }
}