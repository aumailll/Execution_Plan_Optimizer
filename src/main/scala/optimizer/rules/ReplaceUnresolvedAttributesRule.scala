/** ====================================================================================================================
 * RULE - ReplaceUnresolvedAttributeInFilterRule
 * ====================================================================================================================
 *
 * Work only with Project first, it can be done for others by adding case in 'isApplicable', but the objective is to
 * build an optimizer and not the ultimate one then some savings have been done
 *
 * Input :
 *
 * // SELECT (orders.price * 1.20) AS result FROM orders WHERE result < 2.0
 * Project(List(NamedExpression(
 *  "result",
 *  Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType)))),
 *  Filter(Less(
 *    UnresolvedAttribute("result"),
 *    Literal(1.20, DataType.DoubleType)),
 *    TableScan("orders", ...)))
 *
 * Output :
 *
 * Project(List(NamedExpression(
 *  "result",
 *  Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType)))),
 *  Filter(Less(
 *    Multiply(ResolvedAttribute("client", "orders"), Literal(1.20, DataType.DoubleType)),
 *    Literal(1.20, DataType.DoubleType)),
 *    TableScan("orders", ...)))
 */

package optimizer.rules

import scala.annotation.tailrec
import catalog.*
import execution.*
import expression.*
import optimizer.*
import types.*

class ReplaceUnresolvedAttributesRule extends Rules {
  /**
   * Check if the rule is applicable anywhere within the Execution Plan.
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return              True if there is any UnresolvedAttribute that matches a named expression.
   */
  override def isApplicable(executionPlan: ExecutionPlan, catalog: Catalog): Boolean = {
    // Look for the Execution Plan that requires a Project to have UnresolvedAttribute
    executionPlan match {
      // If the plan is a Project, extract NamedExpressions to get the aliases
      case Project(namedExpressions, child) =>
        // Define a set that contains the aliases in the Execution Plan
        val currentAliases = namedExpressions.collect {
          case NamedExpression(name, _) => name
        }.toSet

        // Recursively check the child for matching UnresolvedAttributes
        containsMatchingUnresolvedAttribute(child, currentAliases)
      case _ => false
    }
  }

  /**
   * Apply the rule by replacing UnresolvedAttributes with expressions from NamedExpressions that match the alias name.
   * @param executionPlan Execution Plan
   * @param catalog       Catalog
   * @return Optimized Execution Plan with UnresolvedAttributes replaced by corresponding NamedExpression expressions.
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    executionPlan match {
      case Project(namedExpressions, child) =>
        // Map of alias -> expression
        val aliasMap = namedExpressions.collect {
          case NamedExpression(name, expr) => name -> expr
        }.toMap

        // Recursively replace UnresolvedAttributes in the child execution plan
        val newChild = replaceUnresolvedAttributes(child, aliasMap)
        (Project(namedExpressions, newChild), catalog)
      case _ => (executionPlan, catalog)
    }
  }

  /**
   * Recursively replaces UnresolvedAttributes in expressions with corresponding expressions from the alias map.
   * @param executionPlan The Execution Plan (could be any part of the tree like Filter, Project, etc.)
   * @param aliasMap      A map of alias to expressions (from NamedExpression)
   * @return              Execution Plan with UnresolvedAttributes replaced.
   */
  private def replaceUnresolvedAttributes(executionPlan: ExecutionPlan, aliasMap: Map[String, Expression]):
  ExecutionPlan = {
    executionPlan match {
      case Project(namedExpressions, child) =>
        // Replace UnresolvedAttributes in the child node (could be a Filter, TableScan, etc.)
        val newChild = replaceUnresolvedAttributes(child, aliasMap)
        Project(namedExpressions, newChild)

      case Filter(condition, child) =>
        // Replace UnresolvedAttributes in the condition of the Filter expression
        val newCondition = replaceUnresolvedAttributesInExpression(condition, aliasMap)
        // Recursively replace UnresolvedAttributes in the child (e.g., TableScan)
        val newChild = replaceUnresolvedAttributes(child, aliasMap)
        Filter(newCondition, newChild)

      case Range(start, end, child) =>
        // Recursively replace UnresolvedAttributes in the child of Range
        val newChild = replaceUnresolvedAttributes(child, aliasMap)
        Range(start, end, newChild)

      case _ => executionPlan // Base case: return the plan as it is if no further replacement needed
    }
  }

  /**
   * Replaces UnresolvedAttributes in expressions with their corresponding expressions from the alias map.
   * @param expression  The expression that might contain UnresolvedAttributes.
   * @param aliasMap    The map of alias to expressions.
   * @return            The expression with UnresolvedAttributes replaced.
   */
  private def replaceUnresolvedAttributesInExpression(expression: Expression, aliasMap: Map[String, Expression]): Expression = {
    expression match {
      case UnresolvedAttribute(attr) if aliasMap.contains(attr) =>
        // Replace UnresolvedAttribute with the corresponding expression from the aliasMap
        aliasMap(attr)

      // Recursively replace in sub-expressions
      case And(left, right) =>
        And(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      case Or(left, right) =>
        Or(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      case Not(inner) =>
        Not(replaceUnresolvedAttributesInExpression(inner, aliasMap))

      case Less(left, right) =>
        Less(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      case Greater(left, right) =>
        Greater(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      case EqualTo(left, right) =>
        EqualTo(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      case LessOrEqual(left, right) =>
        LessOrEqual(replaceUnresolvedAttributesInExpression(left, aliasMap), replaceUnresolvedAttributesInExpression(right, aliasMap))

      // Otherwise, it means it's a leaf node not fitting any condition, so we return it as is
      case _ => expression
    }
  }




  /**
   * Recursive function to provide information about potential alias within UnresolvedAttribute(attr)
   * @param expression  Expression (Less, LessOrEqual...)
   * @param aliases     Set of aliases
   * @return            Is there any 'attr' such that 'UnresolvedAttribute(attr)' and 'attr' is withing 'aliases'
   */
  private def containsMatchingUnresolvedAttributeExpression(expression: Expression, aliases: Set[String]): Boolean = {
    expression match {
      case UnresolvedAttribute(attr) if aliases.contains(attr) => true

      // Recursively check the expression's children
      case And(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)
      case Or(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)
      case Not(inner) =>
        containsMatchingUnresolvedAttributeExpression(inner, aliases)
      case Less(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)
      case Greater(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)
      case EqualTo(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)
      case LessOrEqual(left, right) =>
        containsMatchingUnresolvedAttributeExpression(left, aliases) ||
          containsMatchingUnresolvedAttributeExpression(right, aliases)

      // Otherwise, it means it's a leaf node not fitting any condition
      case _ => false
    }
  }

  /**
   * Recursive function that provides the truth about the presence of an UnresolvedAttribute(alias)
   * @param executionPlan Execution Plan
   * @param aliases       Set of aliases in the Execution Plan for expressions
   * @return              Is there any 'attr' such that 'UnresolvedAttribute(attr)' and 'attr' is withing 'aliases'
   */
  @tailrec
  private def containsMatchingUnresolvedAttribute(executionPlan: ExecutionPlan, aliases: Set[String]): Boolean = {
    executionPlan match {
      case Project(_, child) =>
        containsMatchingUnresolvedAttribute(child, aliases)

      case Filter(condition, child) =>
        containsMatchingUnresolvedAttributeExpression(condition, aliases) || containsMatchingUnresolvedAttribute(child, aliases)

      case Range(_, _, child) =>
        containsMatchingUnresolvedAttribute(child, aliases)

      case _ => false
    }
  }
}