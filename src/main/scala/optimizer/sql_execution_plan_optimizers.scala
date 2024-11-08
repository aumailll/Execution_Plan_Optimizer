  /** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizer
 * ====================================================================================================================
 */

package optimizer

import catalog._
import execution._

trait sql_execution_plan_optimizers {
  def optimize(execution_plan: ExecutionPlan, catalog: Catalog): ExecutionPlan = {
    val (optimizedPlan, _) = RuleSet.applyRules(execution_plan, catalog)
    optimizedPlan
  }
}