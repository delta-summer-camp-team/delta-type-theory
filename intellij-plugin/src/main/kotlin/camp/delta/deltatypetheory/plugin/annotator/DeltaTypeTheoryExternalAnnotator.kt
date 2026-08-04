package camp.delta.deltatypetheory.plugin.annotator

import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryElementType
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryTokenType
import camp.delta.deltatypetheory.plugin.annotator.DeltaTypeTheoryDiagnostic
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAware
import com.intellij.psi.PsiFile

class DeltaTypeTheoryExternalAnnotator : ExternalAnnotator<String, List<DeltaTypeTheoryDiagnostic>>(), DumbAware {
    override fun collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): String? {
        TODO("Pending")
        return TODO("Provide the return value")
    }

    override fun doAnnotate(collectedInfo: String?): List<DeltaTypeTheoryDiagnostic>? {
        when (collectedInfo) {
            null -> return null
        }


        return super.doAnnotate(collectedInfo)
    }
    override fun apply(
        psiFile: PsiFile,
        annotationResult: List<DeltaTypeTheoryDiagnostic>?,
        holder: AnnotationHolder
    ) {





        super.apply(psiFile, annotationResult, holder)
    }
}