/** ====================================================================================================================
 * OPTIMIZER - SimplifyStarProjectRule
 * ====================================================================================================================
 *
 * Input :
 *
 * Project(List(NamedExpression("*", Star)),
 *  TableScan("orders", List(
 *    ResolvedAttribute("id", "orders"),
 *    ResolvedAttribute("client", "orders"),
 *    ResolvedAttribute("timestamp", "orders"),
 *    ResolvedAttribute("product", "orders"),
 *    ResolvedAttribute("price", "orders")
 * ))
 *
 *
 * Output :
 *
 * Project(List(
 *  NamedExpression("id", ResolvedAttribute("id", "orders")),
 *  NamedExpression("client", ResolvedAttribute("client", "orders")),
 *  NamedExpression("timestamp", ResolvedAttribute("timestamp", "orders")),
 *  NamedExpression("product", ResolvedAttribute("product", "orders")),
 *  NamedExpression("price", ResolvedAttribute("price", "orders"))
 *  ),
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

class SimplifyStarProjectRule extends Rules {
  /**
   * Check if the rule is applicable or not
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return              Is there "Project(NamedExpression("*", Star) :: Nil, TableScan(tableName, attributes))" in the
   *                      Execution Plan
   */
  override def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean = {
    // Check the shape of the Execution Plan
    executionPlan match {
      // If the shape is like Project(NamedExpression("*", Star) :: Nil, TableScan(tableName, attributes), return true
      case Project(NamedExpression("*", Star) :: Nil, TableScan(tableName, attributes)) => true
      // Otherwise, return false
      case _ => false
    }
  }

  /**
   * Simplify the '*' by its content
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return              New Execution Plan with the content implied by '*' and the previous catalog
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    executionPlan match {
      // Retrieve the elements concerned by '*'
      case Project(NamedExpression("*", Star) :: Nil, TableScan(tableName, attributes)) =>
        val namedExpressions = attributes.map {
          // Retrieve the ResolvedAttribute(attributeName, tableName) 
          case ResolvedAttribute(attributeName, _) =>
            NamedExpression(attributeName, ResolvedAttribute(attributeName, tableName))
        }
        // Define the new Execution Plan 
        val optimizedPlan = Project(namedExpressions, TableScan(tableName, attributes))
        
        // Return the new Execution Plan and the Previous Catalog
        (optimizedPlan, catalog)
      // Return the old Execution Plan and Catalog
      case _ => (executionPlan, catalog)
    }
  }
}