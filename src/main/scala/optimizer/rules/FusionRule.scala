/** ====================================================================================================================
 * RULE - FusionRule
 * ====================================================================================================================
 *
 * Merges consecutive operations in the execution plan, such as projections and filters, to optimize
 * the plan by eliminating redundant operations.
 *
 * Example:
 *
 * SELECT client FROM (SELECT client FROM orders);
 *
 * Input Execution Plan:
 *
 * Project(
 *   List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *   Project(
 *     List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *     TableScan("orders", List(
 *       ResolvedAttribute("client", "orders")
 *     ))
 *   )
 * )
 *
 * Output Execution Plan:
 *
 * Project(
 *   List(NamedExpression("client", ResolvedAttribute("client", "orders"))),
 *   TableScan("orders", List(
 *     ResolvedAttribute("client", "orders")
 *   ))
 * )
 */

package optimizer.rules

import expression._
import execution._
import optimizer._
import catalog._
import types._

class FusionRule extends Rules {
  /**
   * Checks if the rule is applicable somewhere in the execution plan.
   *
   * @param executionPlan Execution plan
   * @param catalog       Catalog
   * @return True if consecutive operations can be merged
   */
  override def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean = {
    executionPlan match {
      case Project(_, child) =>
        child.isInstanceOf[Project] || isApplicable(child, catalog)
      case Filter(_, child) =>
        child.isInstanceOf[Filter] || isApplicable(child, catalog)
      case other =>
        // If the node has a child, recursively check the child
        getChild(other) match {
          case Some(child) => isApplicable(child, catalog)
          case None => false
        }
    }
  }

  /**
   * Applies the rule by merging consecutive operations in the execution plan.
   *
   * @param executionPlan Execution plan
   * @param catalog       Catalog
   * @return Optimized execution plan with merged operations and unchanged catalog
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    val optimizedPlan = fuseOperations(executionPlan)
    (optimizedPlan, catalog)
  }

  /**
   * Recursive function to merge operations in the execution plan.
   *
   * @param plan Current execution plan
   * @return Execution plan with merged operations
   */
  private def fuseOperations(plan: ExecutionPlan): ExecutionPlan = {
    plan match {
      // Merge consecutive projections
      case Project(exprs1, child1) =>
        val newChild = fuseOperations(child1)
        newChild match {
          case Project(exprs2, child2) =>
            val composedExprs = composeExpressions(exprs1, exprs2)
            Project(composedExprs, child2)
          case _ =>
            Project(exprs1, newChild)
        }

      // Merge consecutive filters
      case Filter(cond1, child1) =>
        val newChild = fuseOperations(child1)
        newChild match {
          case Filter(cond2, child2) =>
            val mergedCondition = And(cond1, cond2)
            Filter(mergedCondition, child2)
          case _ =>
            Filter(cond1, newChild)
        }

      // For other nodes, recursively apply fuseOperations to the child if it exists
      case other =>
        getChild(other) match {
          case Some(child) =>
            val newChild = fuseOperations(child)
            replaceChild(other, newChild)
          case None => other
        }
    }
  }

  /**
   * Helper method to get the child of a node if it has one.
   *
   * @param plan Node in the execution plan
   * @return Option[ExecutionPlan]
   */
  private def getChild(plan: ExecutionPlan): Option[ExecutionPlan] = {
    plan match {
      case p: Project => Some(p.next)
      case f: Filter => Some(f.next)
      case s: Sort => Some(s.next)
      case r: Range => Some(r.next)
      case sq: SubQueryAlias => Some(sq.next)
      case _ => None
    }
  }

  /**
   * Helper method to replace the child of a node with a new child.
   *
   * @param plan     Node in the execution plan
   * @param newChild New child ExecutionPlan
   * @return Execution plan with the child replaced
   */
  private def replaceChild(plan: ExecutionPlan, newChild: ExecutionPlan): ExecutionPlan = {
    plan match {
      case p: Project => p.copy(expression = p.expression, next = newChild)
      case f: Filter => f.copy(expression = f.expression, next = newChild)
      case s: Sort => s.copy(order = s.order, next = newChild)
      case r: Range => r.copy(start = r.start, count = r.count, next = newChild)
      case sq: SubQueryAlias => sq.copy(name = sq.name, next = newChild)
      case _ => plan
    }
  }

  /**
   * Composes expressions from two projections to merge the projections.
   *
   * @param outerExprs Expressions from the outer projection
   * @param innerExprs Expressions from the inner projection
   * @return List of composed expressions
   */
  private def composeExpressions(outerExprs: List[NamedExpression], innerExprs: List[NamedExpression]): List[NamedExpression] = {
    outerExprs.map { outerExpr =>
      val substitutedExpr = substituteExpression(outerExpr.expression, innerExprs)
      NamedExpression(outerExpr.name, substitutedExpr)
    }
  }

  /**
   * Substitutes inner expressions into outer expressions.
   *
   * @param expr       Outer expression
   * @param innerExprs Inner expressions
   * @return Expression with substitutions made
   */
  private def substituteExpression(expr: Expression, innerExprs: List[NamedExpression]): Expression = {
    expr match {
      case attr: ResolvedAttribute =>
        innerExprs.find(_.name == attr.name) match {
          case Some(NamedExpression(_, innerExpr)) => innerExpr
          case None => attr
        }
      case Add(left, right) =>
        val newLeft = substituteExpression(left, innerExprs)
        val newRight = substituteExpression(right, innerExprs)
        Add(newLeft, newRight)
      case Multiply(left, right) =>
        val newLeft = substituteExpression(left, innerExprs)
        val newRight = substituteExpression(right, innerExprs)
        Multiply(newLeft, newRight)
      // Handle other types of expressions similarly
      case _ => expr
    }
  }
}