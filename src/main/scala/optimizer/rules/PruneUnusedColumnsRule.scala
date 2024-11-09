/** ====================================================================================================================
 * RULE - PruneUnusedColumnsRule
 * ====================================================================================================================
 *
 * Realize pruning only with this query like this 'SELECT * FROM (SELECT orders.client FROM orders)' and does not take
 * care about 'Filter(SELECT * FROM (SELECT orders.client FROM orders))'. Once again, we are building an optimizer not
 * the ultimate one. But the structure provide a scalable way to improve and keep the code as it is.
 *
 * Input :
 *
 * // SELECT * FROM (SELECT orders.client FROM orders)
 *
 * Project(List(NamedExpression("*", Star)),
 *  Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *    TableScan("orders", List(
 *      ResolvedAttribute("id", "orders"),
 *      ResolvedAttribute("client", "orders"),
 *      ResolvedAttribute("timestamp", "orders"),
 *      ResolvedAttribute("product", "orders"),
 *      ResolvedAttribute("price", "orders")
 * )))
 *
 * Output :
 *
 * Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *  Project(List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *   TableScan("orders", List(
 *    ResolvedAttribute("client", "orders")
 * )))
 */

package optimizer.rules

import expression._
import execution._
import optimizer._
import catalog._

class PruneUnusedColumnsRule extends Rules {
  /**
   * Check if the rule is applicable anywhere within the Execution Plan.
   *
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return True if there is a Project with SELECT * and a child with specific columns
   */
  override def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean = {
    executionPlan match {
      case Project(List(NamedExpression("*", Star)), child) =>
        // If the outer project uses SELECT *, check the child
        child match {
          // If the child contains an inner Project with a TableScan, we can apply pruning
          case Project(innerExpressions, TableScan(tableName, attributes)) =>
            true
          case _ => false
        }
      case _ => false
    }
  }

  /**
   * Apply the rule by pruning unnecessary columns in the execution plan.
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return Optimized Execution Plan with unnecessary columns pruned and old Catalog
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    executionPlan match {
      // Retrieve the child
      case Project(List(NamedExpression("*", Star)), child) =>
        child match {
          // Retrieve the innerExpressions
          case Project(innerExpressions, grandChild) =>
            // Retrieve a set of columns to keep according to the inner expressions
            val neededColumns = innerExpressions.collect {
              case NamedExpression(name, _) => name
            }.toSet

            // Ensure both inner and outer project reflect the same pruned columns
            val prunedNamedExpressions = innerExpressions.filter {
              case NamedExpression(name, _) => neededColumns.contains(name)
            }

            // Replace the inner child with pruned columns
            val prunedChild = replaceWithPrunedColumns(grandChild, neededColumns)

            // Create a new Project with only the needed columns
            val prunedProject = Project(prunedNamedExpressions,
              Project(prunedNamedExpressions, prunedChild)
            )

            (prunedProject, catalog)

          case _ => (executionPlan, catalog)
        }

      // If the outer project is not using "*", return it unchanged
      case _ => (executionPlan, catalog)
    }
  }

  /**
   * Replace unused columns in the child execution plan.
   * @param child         Child execution plan
   * @param needed_columns Set of columns that should be kept
   * @return Pruned execution plan
   */
  private def replaceWithPrunedColumns(child: ExecutionPlan, needed_columns: Set[String]): ExecutionPlan = {
    child match {
      case TableScan(name, columns) =>
        // Keep only the needed columns from the table scan
        val prunedColumns = columns.filter(c => needed_columns.contains(c.name))
        TableScan(name, prunedColumns)

      case Project(namedExpressions, grandChild) =>
        // Prune the columns in the project node
        val prunedNamedExpressions = namedExpressions.filter {
          case NamedExpression(name, _) => needed_columns.contains(name)
        }
        Project(prunedNamedExpressions, replaceWithPrunedColumns(grandChild, needed_columns))

      // If the child is not a TableScan or Project, return it unchanged
      case _ => child
    }
  }
}

