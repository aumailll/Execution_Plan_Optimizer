/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizerReplaceUnresolvedAttributeTest
 * ====================================================================================================================
 */

package optimizer

import org.scalatest.funsuite.AnyFunSuite
import expression.*
import execution.*
import catalog.*
import types.*

class SqlExecutionPlanOptimizerReplaceUnresolvedAttributeTest extends AnyFunSuite {
  test("Simplify UnresolvedAttribute Expression") {
    val inputPlan: ExecutionPlan = Project(List(
      NamedExpression(
        "result",
        Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType))
      )),
      Filter(Less(UnresolvedAttribute("result"), Literal(1.20, DataType.DoubleType)),
        TableScan("orders", List(
          ResolvedAttribute("id", "orders"),
          ResolvedAttribute("client", "orders"),
          ResolvedAttribute("timestamp", "orders"),
          ResolvedAttribute("product", "orders"),
          ResolvedAttribute("price", "orders")
        ))
    ))

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression(
        "result",
        Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType))
      )),
      Filter(Less(
        Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType)),
        Literal(1.20, DataType.DoubleType)),
        TableScan("orders", List(
          ResolvedAttribute("id", "orders"),
          ResolvedAttribute("client", "orders"),
          ResolvedAttribute("timestamp", "orders"),
          ResolvedAttribute("product", "orders"),
          ResolvedAttribute("price", "orders")
        ))
      ))

    val catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanReplaceUnresolvedAttributes

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test : Simplify UnresolvedAttribute Expression - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
