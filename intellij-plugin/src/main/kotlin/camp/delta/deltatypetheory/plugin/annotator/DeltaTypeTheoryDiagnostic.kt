package camp.delta.deltatypetheory.plugin.annotator

import andel.text.TextRange
import com.intellij.lang.annotation.HighlightSeverity

class DeltaTypeTheoryDiagnostic (val range: TextRange, val message: String, val severity: HighlightSeverity) {

}