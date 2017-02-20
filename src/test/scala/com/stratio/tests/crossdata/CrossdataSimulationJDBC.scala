/*
package com.stratio.tests.crossdata

import java.util.Calendar
import java.sql.{Connection, DriverManager, Statement}

import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.protocol.Protocol
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.action.builder.ActionBuilder
import io.gatling.commons.stats.{KO, OK}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CrossdataBuilder(protocol:CrossdataProtocol) extends ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    new CrossdataAction(ctx,next, protocol)
  }
}


class CrossdataSimulationHugo extends Simulation {
  val crossdataProtocol = CrossdataProtocol()
  val crossdataScn = scenario("Crossdata call").exec(CrossdataBuilder(crossdataProtocol))

  setUp(
    crossdataScn.inject(constantUsersPerSec(5) during (15))
  ).protocols(crossdataProtocol)
}

case class CrossdataProtocol()
  extends Protocol with Common{

  val driver = "com.stratio.jdbc.core.jdbc4.StratioDriver"
  val url = "jdbc:crossdata://Server="+ crossdataHost +":"+ crossdataPort+";UID=crossdata;SSL=true"
  val username = "crossdata"
  val password = "crossdata"
  var connection:Connection = null
  try {
    // make the connection
    Class.forName(driver)
    connection = DriverManager.getConnection(url, username, password)
  }catch{
    case e => e.printStackTrace
  }

  def call(query: String): Boolean = {
    val statement = connection.createStatement()
    try{
      val resultSet = statement.executeQuery("SELECT host, user FROM user")
    }catch{
      case e => e.printStackTrace
      return false
    }
    return true
  }

}


class CrossdataAction(ctx: ScenarioContext, val next: Action, val protocol:CrossdataProtocol) extends ChainableAction {

  override def execute(session: Session): Unit = {
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = OK
    var errorMessage: Option[String] = None
    val result: Future[Boolean] = protocol.call("SELECT count(*) FROM store_sales")
    result.foreach(b => if(b) {
      println(b)
      status = KO}
    else
    {status=OK})
    val requestStartDate, requestEndDate = start
    val responseStartDate, responseEndDate = end
    val responseTime = new ResponseTimings(requestStartDate, responseStartDate)
    val requestName = "Crossdata Scenario"
    val message = errorMessage
    val extraInfo = Nil
    val responseCode: Option[String] = None
    ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
    next ! session
  }

  override def name: String = "Crossdata Simulation"

}*/
