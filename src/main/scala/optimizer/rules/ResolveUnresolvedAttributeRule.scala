/** ====================================================================================================================
 * RULE - ResolveUnresolvedAttribute
 * ====================================================================================================================
 *
 * Input :
 *
 * Project(List(NamedExpression("client", UnresolvedAttribute("client"))),
 *  TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))
 *
 * Output :
 *
 * Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
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

class ResolveUnresolvedAttributeRule extends Rules {
  /**
   * Check if the rule is applicable or not
   * @param execution_plan Execution Plan
   * @param catalog        Catalog
   * @return               Is there 'Project(NamedExpression("someColumn", UnresolvedAttribute("someColumn"))' 
   *                       in the Execution Plan?
   */
  override def isApplicable(execution_plan: ExecutionPlan, catalog: Catalog): Boolean = {
    execution_plan match {
      // Check if 'Project(NamedExpression("someColumn", UnresolvedAttribute("someColumn"))' is in the Execution Plan
      case Project(namedExpressions, TableScan(tableName, _)) =>
        // Check if any NamedExpression contains UnresolvedAttribute
        namedExpressions.exists {
          case NamedExpression(_, UnresolvedAttribute(attributeName)) => true
          case _ => false
        }
      case _ => false
    }
  }

  /**
   * Resolve UnresolvedAttribute to ResolvedAttribute
   * @param execution_plan Execution Plan
   * @param catalog        Catalog
   * @return               New Execution Plan with resolved attributes
   */
  override def apply(execution_plan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    execution_plan match {
      case Project(namedExpressions, TableScan(tableName, attributes)) =>
        // Apply transformation for all NamedExpressions containing UnresolvedAttributes
        val resolvedNamedExpressions = namedExpressions.map {
          case NamedExpression(name, UnresolvedAttribute(attributeName)) =>
            NamedExpression(name, ResolvedAttribute(attributeName, tableName))
          case other => other
        }

        // Return the new Execution Plan and the Previous Catalog
        val optimizedPlan = Project(resolvedNamedExpressions, TableScan(tableName, attributes))
        (optimizedPlan, catalog)

      case _ => (execution_plan, catalog)
    }
  }
}
