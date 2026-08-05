package camp.delta.deltatypetheory.plugin.language

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object DeltaTypeTheoryFileType : LanguageFileType(DeltaTypeTheoryLanguage) {

    override fun getName(): String = "DeltaTypeTheory"

    override fun getDescription(): String = "Delta Type Theory file"

    override fun getDefaultExtension(): String = "delta"

    override fun getIcon(): Icon? = null
}
