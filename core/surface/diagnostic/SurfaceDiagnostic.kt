package camp.delta.deltatypetheory.core.surface.diagnostic

import camp.delta.deltatypetheory.core.surface.model.SourceRange

enum class SurfaceDiagnosticSeverity {
    Error,
    Warning,
    Info
}

data class SurfaceDiagnostic(
        val severity: SurfaceDiagnosticSeverity,
        val message: String,
        val range: SourceRange?
)
