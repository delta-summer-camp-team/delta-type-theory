package camp.delta.deltatypetheory.plugin.annotator.diagnostic_classes

import com.intellij.openapi.util.TextRange
import com.intellij.lang.annotation.HighlightSeverity

class DeltaTypeTheoryDiagnostic (
    val range: TextRange,
    val message: String,
    val severity: HighlightSeverity) { }