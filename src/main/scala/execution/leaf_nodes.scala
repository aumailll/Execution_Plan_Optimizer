/** ====================================================================================================================
 * EXECUTION PLAN - LeafNodes
 * ====================================================================================================================
 *
 * An Execution Plan represents the operations that must be executed from the database. The architecture is based on
 * several nodes levels.
 */

package execution

import types.ResolvedAttribute

/**
 * Represents the FROM clause of a SELECT query. This corresponds to the SQL FROM statement, which specifies the table 
 * from which to retrieve data.
 *
 * @param name   The name of the table being scanned. This is typically the name of the database table that contains the
 *               data to be retrieved.
 * @param output A list of `ResolvedAttribute` objects representing the attributes (columns) that will be outputted from
 *               the table scan. Each `ResolvedAttribute` defines a column's name and its associated data type.
 * @return A `TableScan` object that represents the operation of scanning the specified table and returning its 
 *         attributes for further processing in the query execution.
 */
case class TableScan(name: String, output: List[ResolvedAttribute]) extends LeafNode

/**
 * Represents an alias for a table or subquery in a SQL query. This corresponds to the SQL AS clause, allowing a user to
 * assign a temporary name to a table or subquery for ease of reference in other parts of the query.
 *
 * @param name The alias name to be assigned to the table or subquery. This name can be used to refer to the table or 
 *             subquery in other clauses of the query (e.g., WHERE, JOIN).
 * @param next The next execution plan that this alias applies to, represented by an `ExecutionPlan`. This indicates the
 *             original table or subquery being aliased.
 * @return A `SubQueryAlias` object that encapsulates the aliasing of the specified table or subquery.
 */
case class SubQueryAlias(name: String, next: ExecutionPlan) extends UnaryNode(next)