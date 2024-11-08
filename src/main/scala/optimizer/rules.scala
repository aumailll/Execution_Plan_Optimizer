/** ====================================================================================================================
 * OPTIMIZER - Rules
 * ====================================================================================================================
 *
 * A rule takes two parameters, an Execution Plan and a Catalog. It returns a new Execution Plan and Catalog transformed,
 * with potential new aliases in the catalog... It can do nothing if no change is required.
 */

package optimizer

import execution.ExecutionPlan
import optimizer.rules._
import catalog.Catalog

/**
 * Trait that allows to apply an optimization rule only when it is applicable
 */
trait Rules {
  def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog)
  def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean
}


object RuleSet {
  private val allRules: Seq[Rules] = Seq(new SimplifyRule())

  def applyRules(plan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    allRules.foldLeft((plan, catalog)) {
      case ((currentPlan, currentCatalog), rule) =>
        if (rule.isApplicable(currentPlan, currentCatalog)) {
          rule.apply(currentPlan, currentCatalog)
        } else {
          (currentPlan, currentCatalog)
        }
    }
  }
}
