package camp.delta.deltatypetheory.plugin.psi

import camp.delta.deltatypetheory.plugin.language.DeltaTypeTheoryFileType
import camp.delta.deltatypetheory.plugin.language.DeltaTypeTheoryLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class DeltaTypeTheoryFile(viewProvider: FileViewProvider) :
    PsiFileBase(viewProvider, DeltaTypeTheoryLanguage) {

    override fun getFileType(): FileType = DeltaTypeTheoryFileType

    override fun toString(): String = "Delta Type Theory File"
}