/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanStarOptimizerTest
 * ====================================================================================================================
 */

package optimizer

import org.scalatest.funsuite.AnyFunSuite
import expression._
import execution._
import catalog._
import types._

class SqlExecutionPlanStarOptimizerTest extends AnyFunSuite {
  test("Simplify '*' in Project") {
    val inputPlan: ExecutionPlan = Project(
      List(NamedExpression("*", Star)),
      TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      ))
    )

    val expectedOutput: ExecutionPlan = Project(
      List(
        NamedExpression("id", ResolvedAttribute("id", "orders")),
        NamedExpression("client", ResolvedAttribute("client", "orders")),
        NamedExpression("timestamp", ResolvedAttribute("timestamp", "orders")),
        NamedExpression("product", ResolvedAttribute("product", "orders")),
        NamedExpression("price", ResolvedAttribute("price", "orders"))
      ),
      TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      ))
    )

    val catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanStarOptimizer

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test : Simplify '*' in Project - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
