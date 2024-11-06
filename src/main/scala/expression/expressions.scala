/** ====================================================================================================================
 * EXPRESSION - Expression
 * ====================================================================================================================
 *
 * An expression is a column, a transformation over a column, or a condition over a column
 */

package expression

/** Define a base trait `Expression` to represent general expressions. */
trait Expression

/** *
 * @param name       The alias assigned to the expression. This is the name that can be used to reference the result of
 *                   the expression in further operations or queries.
 * @param expression The underlying expression that is being aliased. This can be any expression that conforms to the
 *                   `Expression` trait, allowing for a wide range of mathematical or logical operations.
 *
 * @return A named expression that associates the given alias with the specified expression.
 */
case class NamedExpression(name: String, expression: Expression) extends Expression