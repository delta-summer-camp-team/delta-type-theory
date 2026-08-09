package camp.delta.deltatypetheory.plugin.annotator

import camp.delta.deltatypetheory.plugin.annotator.diagnostic_classes.DeltaTypeTheoryDiagnostic
import camp.delta.deltatypetheory.plugin.annotator.diagnostic_classes.CollectedInfoClass
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil

class DeltaTypeTheoryExternalAnnotator : ExternalAnnotator<CollectedInfoClass, List<DeltaTypeTheoryDiagnostic>>(), DumbAware {
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): CollectedInfoClass {
        val errors = PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
            .map{ DeltaTypeTheoryDiagnostic(it.textRange, it.errorDescription, HighlightSeverity.ERROR) }
        return CollectedInfoClass(file.text, errors)
    }

    override fun doAnnotate(collectedInfo: CollectedInfoClass): List<DeltaTypeTheoryDiagnostic> = collectedInfo.diagnostics

    override fun apply(
        psiFile: PsiFile,
        annotationResult: List<DeltaTypeTheoryDiagnostic>?,
        holder: AnnotationHolder
    ) {
        if (annotationResult == null) {
            return
        }; for (error in annotationResult) {
            holder.newAnnotation(error.severity, error.message)
                .range(error.range)
                .create()
        }
    }
}