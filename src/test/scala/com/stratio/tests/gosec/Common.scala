package com.stratio.tests.gosec

import com.stratio.gosec.dyplon.plugins.dummy.DummyPlugin

trait Common {

  val users = Integer.parseInt(System.getProperty("users", "20"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "5"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "4"))
  val userName = System.getProperty("userName", "jmgomez")
  val instanceArg = System.getProperty("instance", "DummyInstance")
  val manifestPath = System.getProperty("manifest", System.getProperty("user.home") + "/.m2/repository/com/stratio/gosec/dyplon/plugins/dummy/0.6.0-SNAPSHOT/dummy-0.6.0-SNAPSHOT.jar")
  val version: Option[String] = Some(System.getProperty("users", "0.7.0-SNAPSHOT"))
  val genericErrorMessage = "Error performing the action: "
  val doActions = new DummyPlugin(userName, instanceArg, version, manifestPath)

  def getErrorMessage(requestName: String) = s"""$genericErrorMessage $requestName"""

}
