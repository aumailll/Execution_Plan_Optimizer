  /** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizer
 * ====================================================================================================================
 */

package optimizer

import optimizer.rules._
import execution._
import catalog._

trait sql_execution_plan_optimizers {
  def optimize(executionPlan: ExecutionPlan, catalog: Catalog): ExecutionPlan
}

class SqlExecutionPlanOptimizer extends sql_execution_plan_optimizers{
  override def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    // Create an instance of the ArithmeticRule
    val simplifyArithmeticRule = new SimplifyArithmeticRule

    // Return an Optimized Execution Plan or the old one is the rule cannot be applied
    if (simplifyArithmeticRule.isApplicable(execution_plan, catalog)) {
      // Apply the rule and return a new Execution Plan
      val (optimizedPlan, _) = simplifyArithmeticRule.apply(execution_plan, catalog)
      optimizedPlan
    } else {
      execution_plan
    }
  }
}