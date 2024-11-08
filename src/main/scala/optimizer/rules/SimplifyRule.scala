/** ====================================================================================================================
 * OPTIMIZER - SimplifyRule
 * ====================================================================================================================
 */

package optimizer.rules

import expression.{Add, Divide, Expression, Literal, Multiply, Substract}
import execution.{ExecutionPlan, Project}
import optimizer.Rules
import catalog.Catalog
import types.DataType

class SimplifyRule extends Rules {
  /**
   * Define the simplification according to the different cases
   * @param expr Expression in the Execution Plan
   * @return Simplified Expression
   */
  private def simplifyExpression(expr: Expression): Expression = {
    expr match {
      case Add(Literal(0, DataType.IntType), right) => simplifyExpression(right)
      case Add(left, Literal(0, DataType.IntType)) => simplifyExpression(left)

      case Substract(Literal(0, DataType.IntType), right) => simplifyExpression(right)
      case Substract(left, Literal(0, DataType.IntType)) => simplifyExpression(left)

      case Multiply(Literal(0, DataType.IntType), _) => Literal(0, DataType.IntType)
      case Multiply(_, Literal(0, DataType.IntType)) => Literal(0, DataType.IntType)
      case Multiply(Literal(1, DataType.IntType), right) => simplifyExpression(right)
      case Multiply(left, Literal(1, DataType.IntType)) => simplifyExpression(left)

      case Divide(Literal(1, DataType.IntType), right) => simplifyExpression(right)
      case Divide(left, Literal(1, DataType.IntType)) => simplifyExpression(left)

      case Add(left, right) => Add(simplifyExpression(left), simplifyExpression(right))
      case Substract(left, right) => Substract(simplifyExpression(left), simplifyExpression(right))
      case Multiply(left, right) => Multiply(simplifyExpression(left), simplifyExpression(right))
      case Divide(left, right) => Divide(simplifyExpression(left), simplifyExpression(right))

      case other => other
    }
  }

  /**
   * Allow to check if any expression in here is simplifiable
   * @param expr expression to analyze
   * @return expression is simplifiable or not
   */
  private def containsSimplifiableExpression(expr: Expression): Boolean = {
    expr match {
      case Add(Literal(0, DataType.IntType), _) => true
      case Add(_, Literal(0, DataType.IntType)) => true

      case Substract(Literal(0, DataType.IntType), _) => true
      case Substract(_, Literal(0, DataType.IntType)) => true

      case Multiply(Literal(0, DataType.IntType), _) => true
      case Multiply(_, Literal(0, DataType.IntType)) => true
      case Multiply(Literal(1, DataType.IntType), _) => true
      case Multiply(_, Literal(1, DataType.IntType)) => true

      case Divide(Literal(1, DataType.IntType), _) => true
      case Divide(_, Literal(1, DataType.IntType)) => true

      case Add(left, right) => containsSimplifiableExpression(left) || containsSimplifiableExpression(right)
      case Substract(left, right) => containsSimplifiableExpression(left) || containsSimplifiableExpression(right)
      case Multiply(left, right) => containsSimplifiableExpression(left) || containsSimplifiableExpression(right)
      case Divide(left, right) => containsSimplifiableExpression(left) || containsSimplifiableExpression(right)

      case _ => false
    }
  }

  /**
   * Allow to check if the expression contains a Project, if so it will check the expression, looking for simplifiable
   * ones
   * @param plan Execution Plan
   * @param catalog Catalog
   * @return Is the rule applicable ?
   */
  override def isApplicable(plan: ExecutionPlan, catalog: Catalog): Boolean = {
    plan match {
      case p: Project => p.expression.exists(ne => containsSimplifiableExpression(ne.expression))
      case _ => false
    }
  }

  /**
   * Apply the rule to simplify expressions when isApplicable is True
   * @param executionPlan Execution Plan
   * @param catalog Catalog
   * @return a Simplified Execution Plan and the same Catalog
   */
  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    val project = executionPlan.asInstanceOf[Project]
    val simplifiedExpressions = project.expression.map { ne =>
      ne.copy(expression = simplifyExpression(ne.expression))
    }

    val updatedPlan = project.copy(expression = simplifiedExpressions)
    (updatedPlan, catalog)
  }
}
