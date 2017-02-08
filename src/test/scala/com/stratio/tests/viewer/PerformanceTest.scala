package com.stratio.tests.viewer

import io.gatling.core.Predef._
import io.gatling.http.Predef.{http, jsonPath, responseTimeInMillis}
import io.gatling.http.request.builder.HttpRequestBuilder

trait PerformanceTest extends Simulation with Common {
  var thisco = ""

  object Auth {
    val auth: HttpRequestBuilder =
      http("POST /api/login/authenticate/userpass")
        .post("/api/login/authenticate/userpass")
        .body(ElFileBody("src/test/resources/data/viewer/AUTH.txt"))
        .asJSON
        .check(responseTimeInMillis.lessThanOrEqual(500))
  }

  object Render{
    val getRpc =
      http("POST /rpc")
        .post("/rpc")
        .queryParam("st","-1%3A-1%3A*%3A*%3A*%3A0%3Adefault")
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
            |      "language":"es",
            |      "country":"ALL",
            |      "userId":"@viewer",
            |      "groupId":"@self"
            |    }
            |  }
            |]
          """.stripMargin}).asJSON
        .check(jsonPath("$[0].result.*.iframeUrls.home").ofType[String].saveAs("ifrUrl"))


    val getIframe =
      http("GET /ifr")
          .get("${ifrUrl}")
          .check(responseTimeInMillis.lessThanOrEqual(10000))
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
        .check(responseTimeInMillis.lessThanOrEqual(10000))
  }
}
