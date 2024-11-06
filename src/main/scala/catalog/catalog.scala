/** ====================================================================================================================
 * CATALOG - Catalog
 * ====================================================================================================================
 *
 * Catalog is a top-level structure that organizes and manages table and subquery alias catalogs.
 */

package catalog

import execution.ExecutionPlan
import types.DataType

/**
 * @param tables          The primary catalog of tables, where each entry maps a table name to its corresponding
 *                        table definition.
 * @param subqueryAliases A catalog that manages subquery aliases, mapping alias names to their respective subquery
 *                        definitions.
 */
case class Catalog(tables: TableCatalog, subqueryAliases: SubQueryAliasCatalog)

/**
 * @param aliases A map where each key is an alias name (String) and each value is an `ExecutionPlan`, representing the
 *                execution details of the subquery associated with the alias.
 */
case class SubQueryAliasCatalog(aliases: Map[String, ExecutionPlan])

/**
 * @param tables A map where each key is a table name (String) and each value is a `TableStructure`, representing the
 *               structure and metadata of the table.
 */
case class TableCatalog(tables: Map[String, TableStructure])

/**
 * @param fields A list of `TableField` objects, each representing a field in the table with details such as name, type,
 *               and constraints.
 */
case class TableStructure(fields: List[TableField])

/**
 * @param name     The name of the field (or column) as a string.
 * @param dataType The data type of the field, represented by a `datatype.DataType`, which specifies the kind of values the field
 *                 can hold.
 */
case class TableField(name: String, dataType: DataType)