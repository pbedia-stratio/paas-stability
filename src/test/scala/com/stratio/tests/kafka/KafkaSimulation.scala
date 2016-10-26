package com.stratio.tests.kafka

import java.util.concurrent.atomic.AtomicInteger

import io.gatling.core.Predef._
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class KafkaSimulation extends PerformanceTest {
  feederAssoc.records.foreach(fA => {
//    if (fA.get("TOPIC").get.equals(dtopic)) {
//    if (true) {
    scns += scenario(fA.get("TOPIC").toString())
      .exec(flattenMapIntoAttributes(fA))
        .exec(Prod.produceData)
//    }
  }
  )

  logger.error("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
      scns.toList.map(_.inject(rampUsers(users) over (60 seconds))))
    .maxDuration(1 minutes)
    .uniformPauses(5)
    .assertions(
        global.responseTime.max.lessThan(3000),
        global.successfulRequests.percent.greaterThan(95)
    )

}

trait PerformanceTest extends Simulation {

  def logger = LoggerFactory.getLogger(this.getClass)


  object Prod {
//    val HTTPproducer = "http://".concat(System.getProperty("REST_PROXY", "127.0.0.1:80")).concat("/topics/").concat(System.getProperty("TOPIC", "hola"))
    val HTTPproducer = "http://".concat(System.getProperty("REST_PROXY", "127.0.0.1:80")).concat("/topics/").concat(System.getProperty("TOPIC", "hola"))
    val sentHeaders = Map("Content-Type" -> "application/vnd.kafka.json.v1+json")
    val produceData =
      forever(
        pace(5 seconds, 10 seconds).exec(
          http("POST /data")
            .post(HTTPproducer)
            .body(ElFileBody("src/test/resources/data/producerBody.txt")).asJSON
            .header("Content-Type","application/vnd.kafka.json.v1+json")
//            .check(jsonPath("$.offsets")
//              .saveAs("response"))
//            .check(responseTimeInMillis.lessThanOrEqual(10000L))
        )
      )
  }


  val feederAssoc = csv("topicList.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

  val scns = new ListBuffer[ScenarioBuilder]()
}

