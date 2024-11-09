/** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanArithmeticOptimizerTest
 * ====================================================================================================================
 */

package optimizer

import org.scalatest.funsuite.AnyFunSuite
import expression._
import execution._
import catalog._
import types._

class SqlExecutionPlanResolveUnresolvedOptimizerTest extends AnyFunSuite {

  test("Resolve Unresolved Attribute") {
    val inputPlan: ExecutionPlan = Project(
      List(NamedExpression("client", UnresolvedAttribute("client"))),
      TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      ))
    )

    val expectedOutput: ExecutionPlan = Project(
      List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
      TableScan("orders", List(
        ResolvedAttribute("id", "orders"),
        ResolvedAttribute("client", "orders"),
        ResolvedAttribute("timestamp", "orders"),
        ResolvedAttribute("product", "orders"),
        ResolvedAttribute("price", "orders")
      ))
    )

    val catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanResolveUnresolvedOptimizer

    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test : Resolve Unresolved Attribute - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
