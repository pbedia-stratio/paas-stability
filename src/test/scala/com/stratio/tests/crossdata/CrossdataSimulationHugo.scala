package com.stratio.tests.crossdata

import com.stratio.crossdata.common.result.{ErrorSQLResult,SuccessfulSQLResult}
import com.stratio.crossdata.driver.Driver
import com.stratio.crossdata.driver.config.DriverConf
import io.gatling.commons.stats.{KO, OK}
import io.gatling.core.Predef._
import io.gatling.core.action.{Action, ChainableAction}
import io.gatling.core.protocol.Protocol
import io.gatling.core.stats.message.ResponseTimings
import io.gatling.core.structure.ScenarioContext
import io.gatling.core.action.builder.{ActionBuilder}
import scala.collection.mutable.{ListBuffer}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

case class CrossdataBuilder(protocol:CrossdataProtocol) extends ActionBuilder {
  override def build(ctx: ScenarioContext, next: Action): Action = {
    new CrossdataAction(ctx,next, protocol)
  }
}

object ListFutures {
  val  listfutures: ListBuffer[Future[Boolean]] = ListBuffer.empty
  var  num_a = 0
  val expected = 1
}

class CrossdataSimulationHugo extends Simulation {

  val crossdataProtocol = CrossdataProtocol()
  val crossdataScn = scenario("Crossdata call").exec(CrossdataBuilder(crossdataProtocol))

  setUp{
    crossdataScn.inject(constantUsersPerSec(1) during (1)).protocols(crossdataProtocol)
  }

}

case class CrossdataProtocol()
  extends Protocol with Common{
  var driverConf = new DriverConf()
  driverConf.setHttpHostAndPort(crossdataHost, crossdataPort)
  driverConf.setEnableSsl(true)
  driverConf.setKeyStorePath(keysotrePath)
  driverConf.setKeyStorePwd(keystorePassword)
  driverConf.setTrustStorePath(trustedstorePath)
  driverConf.setTrustStorePwd(trustedstorePassword)
  driverConf.setHttpClientIdleTimeout(30000);
  driverConf.setHttpHostConnectionPoolClientIdleTimeout(30);
  var driver: Driver = null

  def call(query: String, ctx: ScenarioContext, session : Session): Future[Boolean] = {
    driver = Driver.http.newSession("crossdata-1", "crossdata", driverConf)
    var start: Long = 0L
    var end: Long = 0L
    var status: Status = KO
    var errorMessage: Option[String] = None
    start = System.currentTimeMillis
    driver.sql(query).sqlResult.map {
      case _: SuccessfulSQLResult =>
        status = OK
        end = System.currentTimeMillis
        val requestStartDate = start
        val responseEndDate = end
        val responseTime = new ResponseTimings(requestStartDate, responseEndDate)
        val requestName = "Crossdata Scenario"
        val message = errorMessage
        val extraInfo:List[Any] = List(query)
        val responseCode: Option[String] = None
        ctx.coreComponents.configuration.config.get
        ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
        driver.closeSession()
        true
      case ErrorSQLResult(message, cause) =>
        println(cause.get.getCause.getMessage)
        println(message)
        errorMessage ++ message
        status = KO
        end = System.currentTimeMillis
        val requestStartDate = start
        val responseEndDate = end
        val responseTime = new ResponseTimings(requestStartDate, responseEndDate)
        val requestName = "Crossdata Scenario"
        val messageReport = errorMessage
        val extraInfo:List[Any] = List(query)
        val responseCode: Option[String] = None
        ctx.coreComponents.configuration.config.get
        ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, messageReport, extraInfo)
        driver.closeSession()
        false
      case _ =>
        status = KO
        end = System.currentTimeMillis
        val requestStartDate = start
        val responseEndDate = end
        val responseTime = new ResponseTimings(requestStartDate, responseEndDate)
        val requestName = "Crossdata Scenario"
        val message = errorMessage
        val extraInfo:List[Any] = List(query)
        val responseCode: Option[String] = None
        ctx.coreComponents.configuration.config.get
        ctx.coreComponents.statsEngine.logResponse(session, requestName, responseTime, status, responseCode, message, extraInfo)
        driver.closeSession()
        false
    }
    }
}


class CrossdataAction(ctx: ScenarioContext, val next: Action, protocol:CrossdataProtocol)  extends ChainableAction {

  import scala.concurrent.duration._
  override def execute(session: Session): Unit = {
    val query = "select i_item_id ,i_item_desc ,s_store_id ,s_store_name ,sum(ss_quantity)        as store_sales_quantity ,sum(sr_return_quantity) as store_returns_quantity ,sum(cs_quantity)        as catalog_sales_quantity from store_sales, store_returns, catalog_sales, date_dim d1, date_dim d2, date_dim d3, store, item where d1.d_moy               = 9 and d1.d_year              = 1999 and d1.d_date_sk           = ss_sold_date_sk and i_item_sk              = ss_item_sk and s_store_sk             = ss_store_sk and ss_customer_sk         = sr_customer_sk and ss_item_sk             = sr_item_sk and ss_ticket_number       = sr_ticket_number and sr_returned_date_sk    = d2.d_date_sk and d2.d_moy               between 9 and  9 + 3 and d2.d_year              = 1999 and sr_customer_sk         = cs_bill_customer_sk and sr_item_sk             = cs_item_sk and cs_sold_date_sk        = d3.d_date_sk and d3.d_year              in (1999,1999+1,1999+2) group by i_item_id, i_item_desc, s_store_id, s_store_name order by i_item_id, i_item_desc, s_store_id, s_store_name limit 100"
    val result: Future[Boolean] = protocol.call(query, ctx, session)
    ListFutures.listfutures += result
    if(ListFutures.num_a == ListFutures.expected-1){
      ListFutures.listfutures.foreach{ future =>
        Try(Await.result(future, 5 hours)) match {
          case Success(_) =>
          case Failure(exception) =>
            println(exception)
        }
      }
    }else{
      ListFutures.num_a+=1
    }

    next ! session
  }

  override def name: String = "Crossdata Simulation"

}