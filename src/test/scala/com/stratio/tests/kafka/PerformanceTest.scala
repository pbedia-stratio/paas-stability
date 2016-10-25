package com.stratio.tests.kafka

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{StringBody, http, responseTimeInMillis, jsonPath}
import io.gatling.http.request.ELFileBody
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

trait PerformanceTest extends Simulation with Common {

  object Auth {

    val auth = http("POST /login/authenticate/userpass")
      .post("/login/authenticate/userpass")
      .body(ELFileBody("AUTH.txt")).asJSON
  }

  object Data {

    val getData =
      forever(
        pace(5 seconds, 10 seconds).exec(
          http("POST /data")
          .post("/data")
          .body(StringBody(
          """{"pageWidgetId": ${PWID}
            ,"filters":[],"aggregations":[]
            |,"parameters": []
            |,"metadata":true
            |}""".stripMargin)).asJSON
          .check(jsonPath("$")
          .saveAs("response"))
          .check(responseTimeInMillis.lessThanOrEqual(10000L))
        )
  )}

  val feederAssoc = csv("associationId.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

  val dtype = this.getClass.getSimpleName.replace("Data", "").toLowerCase

  val scns = new ListBuffer[ScenarioBuilder]()
}

