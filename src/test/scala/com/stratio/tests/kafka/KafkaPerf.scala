package com.stratio.tests.kafka

import io.gatling.core.Predef._
import io.gatling.http.action.sse.EventStreamParser.Data
import scala.concurrent.duration.DurationInt

class KafkaPerf extends PerformanceTest {

  feederAssoc.records.foreach(fA => {
    scns += scenario(fA.get("DS").get)
      .exec(flattenMapIntoAttributes(fA))
      .exec(Auth.auth)
      .exec(Data.getData)
  })

  logger.error("Scenarios size: {}",scns.size )
  if (scns.size < 1) {
      throw new AssertionError("No scenarios")
    }

  setUp(
    scns.toList.map(_.inject(rampUsersPerSec(5) to (200) during (5 minutes)))
    )
    .maxDuration(30 minutes)
    .uniformPauses(2)
    .protocols(httpConf)
    .assertions(
        global.responseTime.max.lessThan(50),
        global.successfulRequests.percent.greaterThan(95)
        )
}