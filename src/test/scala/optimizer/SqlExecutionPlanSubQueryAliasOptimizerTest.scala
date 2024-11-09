/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanSubQueryAliasOptimizer
 * ====================================================================================================================
 *
 * Input :
 *
 * Project(List(
 *  NamedExpression("client", ResolvedAttribute("client", "o"))),
 *  SubQueryAlias("o", TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))))
 *
 *
 * Output :
 *
 * Project(List(
 *  NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *  TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))
 */

package optimizer

import org.scalatest.funsuite.AnyFunSuite
import expression._
import execution._
import catalog._
import types._
import optimizer.rules._

class SqlExecutionPlanSubQueryAliasOptimizerTest extends AnyFunSuite {

  test("Simplify SubQueryAlias in Project") {
    val inputPlan: ExecutionPlan = Project(
      List(
        NamedExpression("client", ResolvedAttribute("client", "o"))
      ),
      SubQueryAlias("o", TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      )))
    )

    val expectedOutput: ExecutionPlan = Project(
      List(
        NamedExpression("client", ResolvedAttribute("client", "orders"))
      ),
      TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      ))
    )

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanSubQueryAliasOptimizer

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test : Simplify SubQueryAlias in Project - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
