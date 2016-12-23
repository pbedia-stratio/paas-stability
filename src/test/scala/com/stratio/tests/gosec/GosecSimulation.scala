package com.stratio.tests.gosec

import com.typesafe.scalalogging.LazyLogging
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.{ChainBuilder, ScenarioContext}

import scala.concurrent.duration._

class GosecSimulation extends Simulation with Common with LazyLogging {
  val getPluginResource = new ActionBuilder {
    val action: String = "wheel read /"
    val requestName: String = "Get plugin resource"

    override def build(ctx: ScenarioContext, next: Action): Action = {
      new ActionGenerator(next, ctx, action, requestName)
    }
  }

  val createPluginResource = new ActionBuilder {
    val action: String = "wheel create /performFolder data"
    val requestName: String = "Create plugin resource"

    override def build(ctx: ScenarioContext, next: Action): Action = {
      new ActionGenerator(next, ctx, action, requestName)
    }
  }

  val setPluginResource = new ActionBuilder {
    val action: String = "wheel alter /performFolder data2"
    val requestName: String = "Update plugin resource"

    override def build(ctx: ScenarioContext, next: Action): Action = {
      new ActionGenerator(next, ctx, action, requestName)
    }
  }


  val createPluginResourceSetDataGetDataAndRemoveResource = ChainBuilder(List(getPluginResource, createPluginResource, setPluginResource))

  val scn = scenario("Gosec plugin performance test")
    .exec(createPluginResourceSetDataGetDataAndRemoveResource)
  setUp(
    scn.inject(rampUsers(users) over injectDuration))
    .maxDuration(runDuration minutes)
    .assertions(
      global.successfulRequests.percent.greaterThan(98))

}


class ActionGenerator(val next: Action, ctx: ScenarioContext, actionType: String, requestName: String) extends ChainableAction with Common {
  override def name: String = ???

  def execute(session: Session) {
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None
    val result = session.status.name
    try {
      start = System.currentTimeMillis
      doActions.performActions("action", actionType)
      if (result.contains("KO")) {
        logger.error("GOSEC EXCEPTION", result.contains("KO"))
        status = KO
      }
      logger.info("Plugin Scope ", doActions.dummyPlugin.get.scope)
      logger.info("Plugin version ", doActions.version)

      end = System.currentTimeMillis
    } catch {
      case e: Exception =>
        errorMessage = Some(e.getMessage)
        logger.error("GOSEC EXCEPTION", e)
        status = KO
    } finally {
      val requestStartDate, requestEndDate = start
      val responseStartDate, responseEndDate = end
      val responseTime = new ResponseTimings(requestStartDate, responseStartDate)
      val errorMessage = getErrorMessage(requestName)
      val message = Some(errorMessage)
      val extraInfo = Nil
      val responseCode: Option[String] = None
      ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
      next ! session
    }
  }
}