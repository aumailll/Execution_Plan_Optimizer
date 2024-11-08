/** ====================================================================================================================
 * OPTIMIZER - Rule
 * ====================================================================================================================
 *
 * A rule takes two parameters, an Execution Plan and a Catalog. It returns a new Execution Plan and Catalog transformed,
 * with potential new aliases in the catalog... It can do nothing if no change is required.
 */

package optimizer

import execution.ExecutionPlan
import optimizer.rules._
import catalog.Catalog

trait Rules {
  def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog)
}

object RuleSet {
  private val allRules: Seq[Rules] = Seq(new SimplifyRule())

  def applyRules(plan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    allRules.foldLeft((plan, catalog)) {
      case ((currentPlan, currentCatalog), rule) =>
        rule.apply(currentPlan, currentCatalog)
    }
  }
}
