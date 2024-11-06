/** ====================================================================================================================
 * OPTIMIZER - Rule
 * ====================================================================================================================
 *
 * A rule takes two parameters, an Execution Plan and a Catalog. It returns a new Execution Plan and Catalog transformed,
 * with potential new aliases in the catalog... It can do nothing if no change is required.
 */

package optimizer

import execution.{ExecutionPlan, Project}
import expression.{Literal, Add, Substract, Multiply, Divide, Expression, NamedExpression}
import catalog.Catalog
import types.DataType

trait Rules {
  def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog)
}

object RuleSet {
  private val allRules: Seq[Rules] = Seq(
    new SimplifyRule()
  )

  def applyRules(plan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    allRules.foldLeft((plan, catalog)) {
      case ((currentPlan, currentCatalog), rule) =>
        rule.apply(currentPlan, currentCatalog)
    }
  }
}

class SimplifyRule extends Rules {
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

  override def apply(executionPlan: ExecutionPlan, catalog: Catalog): (ExecutionPlan, Catalog) = {
    executionPlan match {
      case p: Project =>
        val simplifiedExpressions = p.expression.map { ne =>
          ne.copy(expression = simplifyExpression(ne.expression))
        }

        val updatedPlan = p.copy(expression = simplifiedExpressions)
        (updatedPlan, catalog)

      case other => (executionPlan, catalog)
    }
  }
}

