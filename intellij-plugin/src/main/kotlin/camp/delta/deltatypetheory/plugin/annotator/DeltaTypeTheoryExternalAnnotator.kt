package camp.delta.deltatypetheory.plugin.annotator

import camp.delta.deltatypetheory.core.surface.check.SurfaceTypeCheckerImpl
import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnostic
import camp.delta.deltatypetheory.core.surface.diagnostic.SurfaceDiagnosticSeverity
import camp.delta.deltatypetheory.core.surface.model.SourceRange
import camp.delta.deltatypetheory.plugin.annotator.diagnostic_classes.DeltaTypeTheoryCollectedInfo
import camp.delta.deltatypetheory.plugin.surface.PsiToSurfaceConverter
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile

class DeltaTypeTheoryExternalAnnotator :
    ExternalAnnotator<DeltaTypeTheoryCollectedInfo, List<SurfaceDiagnostic>>(),
    DumbAware {
    private val converter = PsiToSurfaceConverter()
    private val typechecker = SurfaceTypeCheckerImpl

    override fun collectInformation(
        file: PsiFile,
        editor: Editor,
        hasErrors: Boolean,
    ): DeltaTypeTheoryCollectedInfo =
        try {
            val program = converter.convert(file)

            DeltaTypeTheoryCollectedInfo(
                program = program,
            )
        } catch (e: Exception) {
            DeltaTypeTheoryCollectedInfo(
                program = null,
                diagnostics =
                    listOf(
                        SurfaceDiagnostic(
                            severity = SurfaceDiagnosticSeverity.Error,
                            message = e.message ?: "Failed to convert file",
                            range =
                                SourceRange(
                                    filePath = file.name,
                                    startOffset = 0,
                                    endOffset = file.textLength,
                                ),
                        ),
                    ),
            )
        }

    override fun doAnnotate(collectedInfo: DeltaTypeTheoryCollectedInfo): List<SurfaceDiagnostic> {
        if (collectedInfo.program == null) {
            return collectedInfo.diagnostics
        }

        return try {
            typechecker.check(collectedInfo.program).diagnostics
        } catch (e: Exception) {
            listOf(
                SurfaceDiagnostic(
                    severity = SurfaceDiagnosticSeverity.Error,
                    message = e.message ?: "Type checking failed",
                    range = null,
                ),
            )
        }
    }

    override fun apply(
        file: PsiFile,
        annotationResult: List<SurfaceDiagnostic>?,
        holder: AnnotationHolder,
    ) {
        if (annotationResult == null) {
            return
        }

        for (diagnostic in annotationResult) {
            val severity =
                when (diagnostic.severity) {
                    SurfaceDiagnosticSeverity.Error -> {
                        HighlightSeverity.ERROR
                    }

                    SurfaceDiagnosticSeverity.Warning -> {
                        HighlightSeverity.WARNING
                    }

                    SurfaceDiagnosticSeverity.Info -> {
                        HighlightSeverity.WEAK_WARNING
                    }
                }

            val textRange =
                diagnostic.range?.toTextRange(file.textLength)
                    ?: if (file.textLength > 0) {
                        TextRange(0, 1)
                    } else {
                        TextRange(0, 0)
                    }

            holder
                .newAnnotation(severity, diagnostic.message)
                .range(textRange)
                .create()
        }
    }

    private fun SourceRange.toTextRange(textLength: Int): TextRange {
        val start = startOffset.coerceIn(0, textLength)
        val end = endOffset.coerceIn(start, textLength)

        return TextRange(start, end)
    }
}
