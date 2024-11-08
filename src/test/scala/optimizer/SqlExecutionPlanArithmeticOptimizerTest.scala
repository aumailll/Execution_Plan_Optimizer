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

class SqlExecutionPlanArithmeticOptimizerTest extends AnyFunSuite {

  // Test Case 1: Simplify Add with Literal 0
  test("Test 1 - Add with Literal 0") {
    val inputPlan: ExecutionPlan =
      Project(List(
        NamedExpression("col1_plus_0", Add(ResolvedAttribute("col1", "orders"), Literal(0, DataType.IntType))),
        NamedExpression("0_plus_col1", Add(Literal(0, DataType.IntType), ResolvedAttribute("col1", "orders")))
      ), null)

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression("col1_plus_0", ResolvedAttribute("col1", "orders")),
      NamedExpression("0_plus_col1", ResolvedAttribute("col1", "orders")),
    ), null)

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanArithmeticOptimizer
    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test 1 : Add with Literal 0 - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }

  // Test Case 2: Simplify Multiply with Literal 1
  test("Test 2 - Multiply with Literal 1") {
    val inputPlan: ExecutionPlan =
      Project(List(
        NamedExpression("col2_times_1", Multiply(ResolvedAttribute("col2", "orders"), Literal(1.0, DataType.DoubleType))),
        NamedExpression("1_times_col2", Multiply(Literal(1.0, DataType.DoubleType), ResolvedAttribute("col2", "orders"))),
      ), null)

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression("col2_times_1", ResolvedAttribute("col2", "orders")),
      NamedExpression("1_times_col2", ResolvedAttribute("col2", "orders")),
    ), null)

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanArithmeticOptimizer
    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test 2 : Multiply with Literal 1 - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }

  // Test Case 3: Simplify Subtract with Literal 0
  test("Test 3 - Subtract with Literal 0") {
    val inputPlan: ExecutionPlan =
      Project(List(
        NamedExpression("col3_minus_0", Subtract(ResolvedAttribute("col3", "orders"), Literal(0, DataType.IntType))),
        NamedExpression("0_minus_col3", Subtract(Literal(0, DataType.IntType), ResolvedAttribute("col3", "orders"))),
      ), null)

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression("col3_minus_0", ResolvedAttribute("col3", "orders")),
      NamedExpression("0_minus_col3", Negate(ResolvedAttribute("col3", "orders"))),
    ), null)

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanArithmeticOptimizer
    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test 3 : Subtract with Literal 0 - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }

  // Test Case 4: Multiply by Literal 0
  test("Test 4 - Multiply by Literal 0") {
    val inputPlan: ExecutionPlan =
      Project(List(
        NamedExpression("col4_times_0", Multiply(ResolvedAttribute("col4", "orders"), Literal(0, DataType.IntType))),
        NamedExpression("0_times_col4", Multiply(Literal(0, DataType.IntType), ResolvedAttribute("col4", "orders"))),
      ), null)

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression("col4_times_0", Literal(0, DataType.IntType)),
      NamedExpression("0_times_col4", Literal(0, DataType.IntType))
    ), null)

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanArithmeticOptimizer
    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test 4 : Multiply by Literal 0 - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }

  // Test Case 5: Nested Operations
  test("Test 5 - Simplify Nested Arithmetic Operations") {
    val inputPlan: ExecutionPlan =
      Project(List(
        NamedExpression("nested_add_subtract", Add(Add(ResolvedAttribute("col1", "orders"), Literal(0, DataType.IntType)),
          Subtract(ResolvedAttribute("col2", "orders"), Literal(0, DataType.IntType))))
      ), null)

    val expectedOutput: ExecutionPlan = Project(List(
      NamedExpression("nested_add_subtract", Add(ResolvedAttribute("col1", "orders"),
        ResolvedAttribute("col2", "orders")))
    ), null)

    val catalog: Catalog = Catalog(TableCatalog(Map()), SubQueryAliasCatalog(Map()))
    val optimizer = new SqlExecutionPlanArithmeticOptimizer
    val optimizedPlan = optimizer.optimize(inputPlan, catalog)

    if (optimizedPlan == expectedOutput) {
      println("Test 5 : Simplify Nested Arithmetic Operations - Passed")
    } else {
      println(s"Test Failed: Expected: $expectedOutput, but got: $optimizedPlan")
    }
    assert(optimizedPlan == expectedOutput, s"Expected: $expectedOutput, but got: $optimizedPlan")
  }
}
