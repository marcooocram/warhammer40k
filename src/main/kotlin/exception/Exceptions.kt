package exception

import ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
class ItemNotFoundException(message: String, cause: Throwable): Exception(message, cause)

@ExcludeFromJacocoGeneratedReport
class ResourceException(message: String): Exception(message)

@ExcludeFromJacocoGeneratedReport
class CombatUnitMergeException(message: String): Exception(message)