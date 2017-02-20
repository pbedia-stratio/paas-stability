package com.stratio.tests.crossdata

import io.gatling.core.Predef._

trait Common {

  val feederCrossdata = separatedValues(System.getProperty("SCENARIO", "crossdatafeeder.csv"),'"', '|').circular

  val crossdataHost = System.getProperty("CROSSDATA_HOST", "127.0.0.1")
  val crossdataPort = Integer.parseInt(System.getProperty("CROSSDATA_PORT", "13422"))
  val keysotrePath = System.getProperty("KEYSTORE_PATH")
  val keystorePassword = System.getProperty("KEYSTORE_PASSWORD")
  val trustedstorePath = System.getProperty("TRUSTEDSTORE_PATH")
  val trustedstorePassword = System.getProperty("TRUSTEDSTORE_PASSWORD")

  val users = Integer.parseInt(System.getProperty("users", "50"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "10"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "30"))

}
