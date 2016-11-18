package com.stratio.tests.commons

import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext

case class TimeInterval(startTime: Long, endTime: Long)

class TemporizedOperation(val next: Action,
                          ctx: ScenarioContext,
                          actionName: String,
                          methodToBeExecuted: Session => Any,
                          errorMessage: Option[String] = None)
  extends ChainableAction
  with Timer {

  override def name: String = actionName
  override def execute(session: Session) {
    executeMethod(session) match {
      case Right(timer) => {
        val responseTime = new ResponseTimings(timer.startTime, timer.endTime)
        ctx.coreComponents.statsEngine.logResponse(
          session,
          name,
          responseTime,
          OK,
          None,
          errorMessage,
          Nil)
      }
      case Left(exception) => {
        ctx.coreComponents.statsEngine.logResponse(
          session,
          name,
          new ResponseTimings(0L, 0L),
          KO,
          None,
          errorMessage,
          Nil)
      }
    }
    next ! session
  }

  def executeMethod(session: Session): Either[String, TimeInterval] = {
    try {
      val times = withTimer(session) {
        methodToBeExecuted(session)
      }
      Right(TimeInterval(times._1, times._2))
    } catch {
      case e: Exception =>
        logger.error(errorMessage.getOrElse(""), e)
        Left(errorMessage.getOrElse(""))
    }
  }
}



