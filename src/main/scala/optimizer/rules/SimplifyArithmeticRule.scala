/** ====================================================================================================================
 * OPTIMIZER - SimplifyRule
 * ====================================================================================================================
 *
 * Input :
 *
 * Project(List(
 * NamedExpression("col1_plus_0", Add(ResolvedAttribute("col1", "orders"), Literal(0, DataType.IntType))),
 * NamedExpression("col2_times_1", Multiply(ResolvedAttribute("col2", "orders"), Literal(1.0, DataType.DoubleType))),
 * NamedExpression("col3_minus_0", Subtract(ResolvedAttribute("col3", "orders"), Literal(0, DataType.IntType)))
 * ))
 *
 * Output :
 *
 * Project(List(
 * NamedExpression("col1_plus_0", ResolvedAttribute("col1", "orders")),
 * NamedExpression("col2_times_1", ResolvedAttribute("col2", "orders")),
 * NamedExpression("col3_minus_0", ResolvedAttribute("col3", "orders"))
 * ))
 */

package optimizer.rules

import expression._
import execution._
import optimizer._
import catalog._
import types._

class SimplifyArithmeticRule extends Rules {
  /**
   * Check if the expression contains a Project(_, ArithmeticExpression)
   * @param execution_plan    Execution Plan
   * @param catalog           Catalog
   * @return                  Is there any arithmetic expression in the projection?
   */
  override def isApplicable(execution_plan: ExecutionPlan, catalog: Catalog): Boolean = {
    // Check if the Execution Plan is a Project or not (otherwise, the rule is not applicable)
    execution_plan match {
      // Check if it is a Project (otherwise, the rule is not applicable)
      case Project(expressions, _) =>
        expressions.exists {
          // Check if it's a NamedExpression (otherwise, the rule is not applicable)
          case NamedExpression(_, expr) => isArithmeticSimplifiable(expr)
          case null => false
        }
      case _ => false
    }
  }

  /**
   * Apply a simplification over Project with Arithmetic Expression
   * @param execution_plan  Exeuction Plan
   * @param catalog         Catalog
   * @return new Execution Plan and Old Catalog
   */
  override def apply(execution_plan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    // For Project(expressions, next) with Simplifiable Arithmetic Expressions (otherwise, return the same tuple)
    execution_plan match {
      case Project(expressions, next) =>
        // Simplify the expression
        val SimplifiedExpressions = expressions.map {
          // Return the simplified NamedExpression
          case NamedExpression(name, expr) =>
            val simplifiedExpr = simplifyExpression(expr)
            NamedExpression(name, simplifiedExpr)
        }
        // Return the new Execution Plan and the Previous Catalog
        (Project(SimplifiedExpressions, next), catalog)
      case _ => (execution_plan, catalog)
    }
  }

  /**
   * Simplify an Unsimplified Expression
   * @param expression Arithmetic Expression
   * @return Simplified Expression
   */
  private def simplifyExpression(expression: Expression): Expression = {
    expression match {
      // Add or Subtract by 0
      case Add(Literal(0, _), right)           => simplifyExpression(right)
      case Subtract(Literal(0, _), right)      => Negate(simplifyExpression(right))
      case Add(left, Literal(0, _))            => simplifyExpression(left)
      case Subtract(left, Literal(0, _))       => simplifyExpression(left)

      // Multiply or Divide by 1
      case Multiply(Literal(1, _), right)  => simplifyExpression(right)
      case Multiply(left, Literal(1, _))   => simplifyExpression(left)
      case Divide(Literal(1, _), right)    => simplifyExpression(right)
      case Divide(left, Literal(1, _))     => simplifyExpression(left)

      // Multiply by 0
      case Multiply(Literal(0, _), _)   => Literal(0, DataType.IntType)
      case Multiply(_, Literal(0, _))   => Literal(0, DataType.IntType)

      // Nested operations
      case Add(left, right)       => Add(simplifyExpression(left), simplifyExpression(right))
      case Subtract(left, right)  => Subtract(simplifyExpression(left), simplifyExpression(right))
      case Multiply(left, right)  => Multiply(simplifyExpression(left), simplifyExpression(right))
      case Divide(left, right)    => Divide(simplifyExpression(left), simplifyExpression(right))

      // Default case
      case other => other
    }
  }

  /**
   * Check if there is any case that could be simplified (we consider the types are fitting the expectations)
   * @param expression  Potential Arithmetic Expression in a Project
   * @return            Is there any Simplifiable Arithmetic Expression?
   */
  private def isArithmeticSimplifiable(expression: Expression): Boolean = {
    expression match {
      case Add(Literal(0, _), _) | Add(_, Literal(0, _))              => true
      case Subtract(Literal(0, _), _) | Subtract(_, Literal(0, _))    => true
      case Multiply(Literal(1, _), _) | Multiply(_, Literal(1, _))    => true
      case Multiply(Literal(0, _), _) | Multiply(_, Literal(0, _))    => true
      case Divide(_, Literal(1, _))                                   => true

      // Check for nested operations
      case Add(left, right)      => isArithmeticSimplifiable(left) || isArithmeticSimplifiable(right)
      case Subtract(left, right) => isArithmeticSimplifiable(left) || isArithmeticSimplifiable(right)
      case Multiply(left, right) => isArithmeticSimplifiable(left) || isArithmeticSimplifiable(right)
      case Divide(left, right)   => isArithmeticSimplifiable(left) || isArithmeticSimplifiable(right)

      // Otherwise, we can clearly say that there is no Simplifiable Arithmetic Expression
      case _ => false
    }
  }
}