/** ====================================================================================================================
 * EXECUTION PLAN - BinaryNodes
 * ====================================================================================================================
 *
 * An Execution Plan represents the operations that must be executed from the database. The architecture is based on
 * several nodes levels.
 */

package execution

/**
 * Represents a JOIN operation that combines two execution plans based on a specified condition. This corresponds to the
 * SQL JOIN operation, which merges rows from two tables based on a related column between them.
 *
 * @param left  The left execution plan, representing the first input to the join operation. This is typically the left
 *              table or dataset being joined.
 * @param right The right execution plan, representing the second input to the join operation. This is usually the right
 *              table or dataset that is being joined with the left.
 * @return An `ExecutionPlan` that represents the result of the join operation between the two input plans.
 */
case class Join(left: ExecutionPlan, right: ExecutionPlan) extends BinaryNode(left, right)

/**
 * Represents a UNION operation that combines the results of multiple subqueries. This corresponds to the SQL UNION
 * operation, which merges the result sets of two or more SELECT queries into a single result set, removing duplicates.
 *
 * @param subquery A list of `ExecutionPlan` instances, each representing a subquery whose results will be combined. All
 *                 subqueries must have the same number of columns and compatible data types for the UNION operation to
 *                 be valid.
 * @return An `ExecutionPlan` that represents the combined result of the subqueries.
 */
case class Union(subquery: List[ExecutionPlan]) extends ExecutionPlan