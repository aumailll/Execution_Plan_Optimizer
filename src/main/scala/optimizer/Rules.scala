/** ====================================================================================================================
 * OPTIMIZER - Rules
 * ====================================================================================================================
 *
 * A rule takes two parameters, an Execution Plan and a Catalog. It returns a new Execution Plan and Catalog transformed,
 * with potential new aliases in the catalog... It can do nothing if no change is required.
 */

package optimizer

import execution.ExecutionPlan
import catalog.Catalog

/**
 * Trait that allows to apply an optimization rule only when it is applicable
 */
trait Rules {
  def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog)
  def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean
}
