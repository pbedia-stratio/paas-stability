package com.stratio.tests.gosec

import com.stratio.gosec.dyplon.plugins.dummy.DummyPlugin

trait Common {

  val users = Integer.parseInt(System.getProperty("users", "20"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))
  val userName = System.getProperty("userName", "${userName}")
  val instanceArg = System.getProperty("instance", "DummyInstance")
  val manifestPath = System.getProperty("manifest", "/home/dspiritu/TestAtworkspace/Dummy/pluginDummy/gosec-dyplon/plugins/dummy/target/dummy-0.5.0-SNAPSHOT.jar")
  val version: Option[String] = Some("0.5.0-SNAPSHOT")
  val genericErrorMessage = "Error performing the action: "
  val doActions = new DummyPlugin(userName, instanceArg, version, manifestPath)

  def getErrorMessage(requestName: String) = s"""$genericErrorMessage $requestName"""

}
