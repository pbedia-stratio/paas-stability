package com.stratio.tests.kafka

import io.gatling.core.Predef._
import io.gatling.core.session.{Expression, SessionAttribute}
import io.gatling.core.structure.ScenarioBuilder
import io.gatling.http.Predef._
import org.slf4j.LoggerFactory

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration._

class KafkaSimulation extends PerformanceTest {
    feederAssoc.records.foreach(fA => {
    scns += scenario(fA.get("TOPIC").get)
      .exec(flattenMapIntoAttributes(fA))
        .exec(Cons.createConsumer)
      .exec(Prod.produceData)
    })

  logger.info("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp(
      scns.toList.map(_.inject(rampUsers(users) over injectDuration)))
    .maxDuration(runDuration minutes)
    .assertions(
        global.responseTime.max.lessThan(3000),
        global.successfulRequests.percent.greaterThan(95)
    )

}

trait Headers {
  val contentTypeValue: Expression[String] = "application/vnd.kafka.json.v1+json"
  val contentType = "Content-Type"
}

trait PerformanceTest extends Simulation with Headers {

  def logger = LoggerFactory.getLogger(this.getClass)


  object Prod {
    val HTTPobtainMsg = s"""http://${REST_PROXY}/consumers/$${CONSUMER}/instances/$${CONSUMER}/topics/$${TOPIC}"""
    val HTTPproducer = s"""http://${REST_PROXY}/topics/$${TOPIC}"""

    val produceData =
      forever(
        pace(1 seconds, 5 seconds).exec(
          http("POST /data")
            .post(HTTPproducer)
            .body(ElFileBody("src/test/resources/data/producerBody.txt")).asJSON
            .header(contentType, contentTypeValue)
            .check(jsonPath("$.offsets..offset"))
        )
          .pause(2)
          .exec(http("GET /data")
          .get(HTTPobtainMsg)
          .header("Accept", contentTypeValue)
          .check(regex("offset").findAll.exists)
        )
      )
  }

  object Cons {
    val HTTPcreateConsumer = s"""http://${REST_PROXY}/consumers/$${CONSUMER}"""
    val createConsumer =
      http("POST /consumer")
        .post(HTTPcreateConsumer)
        .body(ElFileBody("src/test/resources/data/createConsumer.txt")).asJSON
        .header(contentType, contentTypeValue)
  }


  val feederAssoc = csv("topicList.csv")

  val users = Integer.parseInt(System.getProperty("users", "1"))
  val injectDuration = Integer.parseInt(System.getProperty("injectD", "1"))
  val runDuration = Integer.parseInt(System.getProperty("runD", "1"))

  val REST_PROXY = System.getProperty("REST_PROXY", "127.0.0.1")
  val scns = new ListBuffer[ScenarioBuilder]()
}

