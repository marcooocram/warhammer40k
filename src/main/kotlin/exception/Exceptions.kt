package exception

import ExcludeFromJacocoGeneratedReport

@ExcludeFromJacocoGeneratedReport
class ItemNotFoundException : Exception {
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(message: String) : super(message)
}

@ExcludeFromJacocoGeneratedReport
class ResourceException(message: String): Exception(message)

@ExcludeFromJacocoGeneratedReport
class CombatUnitMergeException(message: String): Exception(message)
