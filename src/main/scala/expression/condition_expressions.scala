/** ====================================================================================================================
 * EXPRESSION - ConditionExpression
 * ====================================================================================================================
 *
 * An expression is a column, a transformation over a column, or a condition over a column
 */

package expression

/**
 * Represents a logical AND operation between two expressions.
 *
 * @param left  The left operand of the AND operation, which must be an `Expression`.
 * @param right The right operand of the AND operation, which must also be an `Expression`.
 * @return An `Expression` that represents the logical "AND" gate of the left and right operands.
 */
case class And(left: Expression, right: Expression) extends Expression

/**
 * Represents a logical OR operation between two expressions.
 *
 * @param left  The left operand of the OR operation, which must be an `Expression`.
 * @param right The right operand of the OR operation, which must also be an `Expression`.
 * @return An `Expression` that represents the logical "OR" gate of the left and right operands.
 */
case class Or(left: Expression, right: Expression) extends Expression

/**
 * Represents a logical NOT operation applied to an expression.
 *
 * @param expression The expression to negate, which must conform to the `Expression` trait.
 * @return An `Expression` that represents the logical "NOT" for the given expression.
 */
case class Not(expression: Expression) extends Expression

/**
 * Represents an equality comparison between two expressions.
 *
 * @param left  The left operand of the equality comparison, which must be an `Expression`.
 * @param right The right operand of the equality comparison, which must also be an `Expression`.
 * @return An `Expression` that represents the "=" of the left and right operands.
 */
case class EqualTo(left: Expression, right: Expression) extends Expression

/**
 * Represents a less-than comparison between two expressions.
 *
 * @param left  The left operand of the comparison, which must be an `Expression`.
 * @param right The right operand of the comparison, which must also be an `Expression`.
 * @return An `Expression` that represents the "<" of the left and right operands.
 */
case class Less(left: Expression, right: Expression) extends Expression

/**
 * Represents a less-than-or-equal-to comparison between two expressions.
 *
 * @param left  The left operand of the comparison, which must be an `Expression`.
 * @param right The right operand of the comparison, which must also be an `Expression`.
 * @return An `Expression` that represents the "<=" of the left and right operands.
 */
case class LessOrEqual(left: Expression, right: Expression) extends Expression

/**
 * Represents a greater-than comparison between two expressions.
 *
 * @param left  The left operand of the comparison, which must be an `Expression`.
 * @param right The right operand of the comparison, which must also be an `Expression`.
 * @return An `Expression` that represents the ">" of the left and right operands.
 */
case class Greater(left: Expression, right: Expression) extends Expression

/**
 * Represents a greater-than-or-equal-to comparison between two expressions.
 *
 * @param left  The left operand of the comparison, which must be an `Expression`.
 * @param right The right operand of the comparison, which must also be an `Expression`.
 * @return An `Expression` that represents the ">=" of the left and right operands.
 */
case class GreaterOrEqual(left: Expression, right: Expression) extends Expression