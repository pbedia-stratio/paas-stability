package com.stratio.tests.viewer

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{http, jsonPath, responseTimeInMillis}
import io.gatling.http.request.builder.HttpRequestBuilder

trait PerformanceTest extends Simulation with Common {

  object Auth {
    val auth: HttpRequestBuilder = http("POST /login/authenticate/userpass")
      .post("/api/login/authenticate/userpass")
      .body(ElFileBody("src/test/resources/data/viewer/AUTH.txt"))
      .asJSON
  }

  object Render{

    val getRpc: HttpRequestBuilder = http("GET /rpc")
      .get("/rpc")
      .queryParam("st","-1:-1:*:*:*:0:default")
      .check(responseTimeInMillis.lessThanOrEqual(100))

    val getIframe: HttpRequestBuilder = http("GET /ifr")
      .get("")
      .check(responseTimeInMillis.lessThanOrEqual(100))

  }

  object Data {

    val getData: ChainBuilder =
      forever {
        pace(paceTime).exec {
          http("POST /data")
            .post("/api/data")
            .body(StringBody{
              """{"pageWidgetId":${PWID},
                |"filters":[],
                |"parameters":[],
                |"aggregations":[],
                |"metadata":true}""".stripMargin
            })
            .asJSON
            .check(responseTimeInMillis.lessThanOrEqual(20000))
        }
      }
  }
}

