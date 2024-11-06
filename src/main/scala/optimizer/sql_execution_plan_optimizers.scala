  /** ====================================================================================================================
 * OPTIMIZER - SqlExecutionPlanOptimizer
 * ====================================================================================================================
 */

package optimizer

import catalog.TableCatalog
import execution.ExecutionPlan
import execution.Project

trait sql_execution_plan_optimizers {
  def optimize(execution_plan: ExecutionPlan, table_catalog: TableCatalog): ExecutionPlan = {
    val (optimizedPlan, _) = RuleSet.applyRules(execution_plan, table_catalog)
    optimizedPlan
  }
}