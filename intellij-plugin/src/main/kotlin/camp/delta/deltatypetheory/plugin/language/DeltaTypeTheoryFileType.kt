package camp.delta.deltatypetheory.plugin.language
import camp.delta.deltatypetheory.plugin.language.DeltaTypeTheoryLanguage

import com.intellij.openapi.fileTypes.LanguageFileType
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.util.NlsSafe
import com.jetbrains.rd.util.Maybe
import org.jetbrains.annotations.NonNls
import javax.swing.Icon
import com.intellij.openapi.util.IconLoader

object DeltaIcons {
    @JvmField
    val FILE = IconLoader.getIcon("/META-INF/pluginIconThumb.svg", DeltaIcons::class.java)
}

object DeltaTypeTheoryFileType : LanguageFileType(DeltaTypeTheoryLanguage) {
    override fun getName(): @NonNls String {
        return "DeltaTypeTheory"
    }

    override fun getDescription(): @NlsContexts.Label String {
        return "Delta Type Theory Language"
    }

    override fun getDefaultExtension(): @NlsSafe String {
        return "delta"
    }

    override fun getIcon(): Icon? = DeltaIcons.FILE
}
