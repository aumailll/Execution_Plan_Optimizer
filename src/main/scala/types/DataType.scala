/** ====================================================================================================================
 * DATATYPE
 * ====================================================================================================================
 */

package types

enum DataType:
  case IntType // 1, 2, -24...
  case DoubleType // 1.0, -1.0, 1.2243e24, 1.32e-48...
  case StringType // 'toto', 'hello', 'akdjakj'...
  case BooleanType // true, false
  case TimestampType
