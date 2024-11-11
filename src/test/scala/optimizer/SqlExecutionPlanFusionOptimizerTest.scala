/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanFusionOptimizerTest
 * ====================================================================================================================
 */

package optimizer

import catalog._
import execution._
import expression._
import org.scalatest.funsuite.AnyFunSuite
import types._

class SqlExecutionPlanFusionOptimizerTest extends AnyFunSuite {
  test("Fusion of consecutive projections") {
    // Input Execution Plan representing the SQL query:
    // SELECT client FROM (SELECT client FROM orders);
    val inputPlan: ExecutionPlan =
      Project(
        List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
        Project(
          List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
          TableScan("orders", List(
            ResolvedAttribute("client", "orders"),
            ResolvedAttribute("id", "orders"),
            ResolvedAttribute("timestamp", "orders"),
            ResolvedAttribute("product", "orders"),
            ResolvedAttribute("price", "orders")
          ))
        )
      )

    // Expected Output Execution Plan after applying the FusionRule
    val expectedOutput: ExecutionPlan =
      Project(
        List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
        TableScan("orders", List(
          ResolvedAttribute("client", "orders"),
          ResolvedAttribute("id", "orders"),
          ResolvedAttribute("timestamp", "orders"),
          ResolvedAttribute("product", "orders"),
          ResolvedAttribute("price", "orders")
        ))
      )

    val catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanFusionOptimizer

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test: Fusion of consecutive projections - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
