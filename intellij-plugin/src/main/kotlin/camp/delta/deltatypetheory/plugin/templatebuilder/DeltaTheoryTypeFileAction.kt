package camp.delta.deltatypetheory.plugin.templatebuilder

import camp.delta.deltatypetheory.plugin.language.DeltaIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
//import camp.delta.deltatypetheory.resources.*
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.util.NlsContexts
import com.intellij.psi.PsiDirectory
import org.jetbrains.annotations.NonNls

//val FILE = IconLoader.getIcon("/META-INF/pluginIconThumb.svg", DeltaIcons::class.java)
class DeltaTheoryTypeFileAction : CreateFileFromTemplateAction("Delta Type Theory File"
    ,"Create a Delta Type Theory file here", IconLoader.getIcon("/META-INF/pluginIconThumb.svg", DeltaIcons::class.java)) {
    override fun buildDialog(
        project: Project,
        dir: PsiDirectory,
        builder: CreateFileFromTemplateDialog.Builder
    ) {
        builder.setTitle("Delta Type Theory File")
        builder.addKind("Delta Type Theory File", DeltaIcons.FILE, "template.delta")
    }

    override fun getActionName(
        dir: PsiDirectory?,
        fileName: @NonNls String,
        templateName: @NonNls String?
    ): @NlsContexts.Command String? {
        return "Create a Delta Type Theory file $fileName"
    }

}