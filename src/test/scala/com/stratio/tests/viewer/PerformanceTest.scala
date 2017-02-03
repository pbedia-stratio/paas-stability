package com.stratio.tests.viewer

import io.gatling.core.Predef._
import io.gatling.core.structure.ChainBuilder
import io.gatling.http.Predef.{http, jsonPath, responseTimeInMillis}
import io.gatling.http.request.builder.HttpRequestBuilder

trait PerformanceTest extends Simulation with Common {

  object Auth {
    val auth: HttpRequestBuilder =
      http("POST /api/login/authenticate/userpass")
        .post("/api/login/authenticate/userpass")
        .body(ElFileBody("src/test/resources/data/viewer/AUTH.txt"))
        .asJSON
  }

  object Render{

    val getRpc: HttpRequestBuilder =
      http("GET /rpc")
        .get("/rpc")
        .queryParam("st","-1:-1:*:*:*:0:default")
        .body(StringBody {
          """
            |[
            |  {
            |    "method":"gadgets.metadata",
            |    "id":"gadgets.metadata",
            |    "params":{
            |      "container":"default",
            |      "ids":[
            |        "${WidgetURL}"
            |      ],
            |      "fields":[
            |        "iframeUrls",
            |        "modulePrefs.*",
            |        "needsTokenRefresh",
            |        "userPrefs.*",
            |        "views.preferredHeight",
            |        "views.preferredWidth",
            |        "expireTimeMs",
            |        "responseTimeMs",
            |        "rpcServiceIds",
            |        "tokenTTL"
            |      ],
            |      "language":"en",
            |      "country":"US",
            |      "userId":"@viewer",
            |      "groupId":"@self"
            |    }
            |  }
            |]
          """.stripMargin
        })
        .asJSON
        .check(responseTimeInMillis.lessThanOrEqual(100))

    val getIframe: HttpRequestBuilder = http("GET /ifr")
      .get("/gadgets/ifr")
      .queryParamSeq(Seq(
        ("url", sut + "${WidgetID}"),
        ("container", "default"),
        ("view", "home"),
        ("lang", "en"),
        ("country", "US"),
        ("debug", 0),
        ("nocache", 0),
        ("sanitize", "%25sanitize%25"),
        ("v", "dd5a6fcffba452689a93bd263486a423"),
        ("testmode", 0),
        ("parent", sut)
      ))
      .check(responseTimeInMillis.lessThanOrEqual(100))

  }

  object Data {

    val getData =
      http("POST /api/data")
        .post("/api/data")
        .body(StringBody {
          """
            |{
            |  "pageWidgetId": ${PageWidgetID},
            |  "filters":[],
            |  "parameters":[],
            |  "aggregations":[],
            |  "metadata":true
            |}
          """.stripMargin
        })
        .asJSON
        .check(responseTimeInMillis.lessThanOrEqual(20000))
  }
}

