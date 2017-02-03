package com.stratio.tests.viewer

import io.gatling.core.Predef._
import scala.concurrent.duration.DurationInt

class ViewerSimulation extends PerformanceTest {

  feederAssoc.records.foreach(feeder => {
    scenarios += scenario(feeder("DS"))
      .exec(flattenMapIntoAttributes(feeder))
      .exec(Auth.auth)
      .exec(Data.getData)
  })

  logger.debug("Scenarios size: {}", scenarios.size )

  if (scenarios.size < 1) {
    throw new AssertionError("No scenarios")
  }

  setUp {
    scenarios
      .toList
      .map(_.inject(rampUsers(users) over injectDuration))
  }
    .maxDuration(runDuration minutes)
    .protocols(httpConf)
    .assertions {
      global.responseTime.max.lte(50)
      global.successfulRequests.percent.gte(90)
    }
}