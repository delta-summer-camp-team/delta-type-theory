package camp.delta.deltatypetheory.plugin

import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryDefDecl
import com.intellij.codeInsight.completion.CompletionContributor
import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.intellij.codeInsight.completion.CompletionType

class DeltaTypeTheoryCompletionContributor : CompletionContributor() {

    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(),
            DeltaTypeTheoryCompletionProvider()
        )
    }
}

private class DeltaTypeTheoryCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet,
    ) {
        val file = parameters.originalFile

        val definitions = PsiTreeUtil.findChildrenOfType(
            file,
            DeltaTypeTheoryDefDecl::class.java
        )

        for (definition in definitions) {
            val name = definition.identifier.text

            if (name.isNotEmpty()) {
                result.addElement(
                    LookupElementBuilder.create(name)
                )
            }
        }
    }
}
