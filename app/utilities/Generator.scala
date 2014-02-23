package utilities

import scala.util.Random

/**
 * A utility object for generating useful things like UUID and hash values
 */
object Generator {
  /**
   * Generates a UUID
   *
   * @return  Generated UUID value
   */
  def generateUUID: String = {
    java.util.UUID.randomUUID().toString.replace("-", "")
  }

  /**
   * Generates the SHA-512 hashed value of given String
   *
   * @param s A value whose hash will be generated
   *
   * @return  Generated hash value
   */
  def generateSHA512(s: String): String = {
    val messageDigest = java.security.MessageDigest.getInstance("SHA-512")
    val bytes = messageDigest.digest(s.getBytes)
    val stringBuilder: StringBuilder = new StringBuilder()
    for(byte <- bytes) stringBuilder.append(Integer.toString((byte & 0xff) + 0x100, 16).substring(1))
    stringBuilder.toString()
  }

  /**
   * Generates random text of given length consisting of alphanumerical characters
   *
   * @param length  Length of the text
   *
   * @return Random text of given length consisting of alphanumerical characters
   */
  def generateRandomText(length: Int): String = Random.alphanumeric.take(length).mkString

  /**
   * Generates random text consisting of 16 alphanumerical characters
   *
   * @return Random text consisting of 16 alphanumerical characters
   */
  def generateRandomText: String = generateRandomText(16)
}