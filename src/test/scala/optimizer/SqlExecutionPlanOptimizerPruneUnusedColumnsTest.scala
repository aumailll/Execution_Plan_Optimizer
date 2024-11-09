/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizerPruneUnusedColumnsTest
 * ====================================================================================================================
 */

package optimizer

import catalog.*
import execution.*
import expression.*
import org.scalatest.funsuite.AnyFunSuite
import types.*

class SqlExecutionPlanOptimizerPruneUnusedColumnsTest extends AnyFunSuite {
  test("Simplify Prune Unused Columns") {
    val inputPlan: ExecutionPlan =
      Project(List(NamedExpression("*", Star)),
        Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
          TableScan("orders", List(
            ResolvedAttribute("id", "orders"),
            ResolvedAttribute("client", "orders"),
            ResolvedAttribute("timestamp", "orders"),
            ResolvedAttribute("product", "orders"),
            ResolvedAttribute("price", "orders")
          ))
        )
      )

    val expectedOutput: ExecutionPlan =
      Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
        Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
          TableScan("orders", List(
            ResolvedAttribute("client", "orders")
          ))
        )
      )

    val catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanPruneUnusedColumns

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test : Simplify Prune Unused Columns - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
