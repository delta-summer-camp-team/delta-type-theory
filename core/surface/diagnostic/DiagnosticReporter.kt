package camp.delta.deltatypetheory.core.surface.diagnostic

import camp.delta.deltatypetheory.core.surface.model.SourceRange

class DiagnosticReporter {
    val diagnostics: List<SurfaceDiagnostic> = mutableListOf()

    fun report(diagnostic: SurfaceDiagnostic) {
        diagnostics.add(diagnostic)
    }

    fun reportError(message: String, range: SourceRange?) {
        diagnostics.add(SurfaceDiagnostic(SurfaceDiagnosticSeverity.Error, message, range))
        // System.err.println(
        //         "\u001BError in ${range.filePath} from ${range.startOffset} to
        // ${range.endOffset}: \u001B[0m ${message}"
        // )
    }

    fun all(): List<SurfaceDiagnostic> {
        return diagnostics
    }

    fun hasErrors(): Boolean {
        return diagnostics.isEmpty()
    }
}
