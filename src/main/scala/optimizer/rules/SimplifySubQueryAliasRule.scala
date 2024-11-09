/** ====================================================================================================================
 * OPTIMIZER - SimplifySubQueryAliasRule
 * ====================================================================================================================
 *
 * Input :
 *
 * Project(List(
 *  NamedExpression("client", ResolvedAttribute("client", "o"))),
 *  SubQueryAlias("o", TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))))
 *
 *
 * Output :
 *
 * Project(List(
 *  NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *  TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))
 */

package optimizer.rules

import expression._
import optimizer._
import execution._
import catalog._
import types._

class SimplifySubQueryAliasRule extends Rules {
  /**
   * Checks if the rule can be applied.
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return              Boolean indicating if there is a 'Project(_, SubQueryAlias(_, TableScan(...)))'
   */
  override def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean = {
    executionPlan match {
      // Check for the shape Project(_, SubQueryAlias(_, TableScan(...)))
      case Project(_, SubQueryAlias(_, TableScan(_, _))) => true
      case _ => false
    }
  }

  /**
   * Applies the rule to simplify the SubQueryAlias by flattening it.
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return              Simplified Execution Plan and the original catalog
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    executionPlan match {
      case Project(namedExpressions, SubQueryAlias(alias, TableScan(tableName, attributes))) =>
        // Transform ResolvedAttribute references in Project to use original table name
        val updatedExpressions = namedExpressions.map {
          case NamedExpression(name, ResolvedAttribute(attributeName, `alias`)) =>
            NamedExpression(name, ResolvedAttribute(attributeName, tableName))
          case other => other
        }

        // Create a new Project node without the SubQueryAlias and point to the original table directly
        val optimizedPlan = Project(updatedExpressions, TableScan(tableName, attributes))

        // Return the optimized plan
        (optimizedPlan, catalog)

      // Fallback to return the original plan if not matching
      case _ => (executionPlan, catalog)
    }
  }
}