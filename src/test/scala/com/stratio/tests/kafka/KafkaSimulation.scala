package com.stratio.tests.kafka

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef.{StringBody, http, responseTimeInMillis, jsonPath}
import io.gatling.http.request.ELFileBody
import org.slf4j.LoggerFactory
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class KafkaSimulation extends PerformanceTest {
  feederAssoc.records.foreach(fA => {
    if (fA.get("TOPIC").get.equals(dtopic)) {
      scns += scenario(dtopic.toUpperCase)
        .exec(flattenMapIntoAttributes(fA))
        .exec(Prod.produceData)
    }
  })

  setUp(
    scns.toList.map(_.inject(rampUsers(users) over (new DurationInt(injectDuration).seconds))))
    .maxDuration(new DurationInt(runDuration).minutes)
    .uniformPauses(5)
    .assertions(
      global.responseTime.max.lessThan(3000),
      global.successfulRequests.percent.greaterThan(95)
    )

}

trait PerformanceTest extends Simulation {
  object Prod {
    val HTTPproducer = "http://".concat(System.getProperty("REST_PROXY", "127.0.0.1:80")).concat("/topics/").concat(System.getProperty("TOPIC", "hola"))
    val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")
    val produceData =
      forever(
        pace(5 seconds, 10 seconds).exec(
          http("POST /data")
            .post(HTTPproducer)
            .headers(sentHeaders)
            .body(StringBody(
              """{
                |"records":[{
                |"value":{"foo":"amparo"}}]
                |}""".stripMargin)).asJSON
            .check(jsonPath("$")
              .saveAs("response"))
            .check(responseTimeInMillis.lessThanOrEqual(10000L))
        )
      )
  }


  val feederAssoc = csv("associationId.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

  val dtopic = this.getClass.getSimpleName.replace("Data", "").toLowerCase

  val scns = new ListBuffer[ScenarioBuilder]()

//  val HTTPproducer = "http://".concat(System.getProperty("BROKER_IP", "127.0.0.1")).concat("/topics/").concat(System.getProperty("TOPIC", "test"))
//  val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")
//
//  val producerRequest = http("Producer url")
//    .post(HTTPproducer)
//    .headers(sentHeaders)
//
//  object order{
//    val dataStart = new AtomicInteger(1)
//  }
}

