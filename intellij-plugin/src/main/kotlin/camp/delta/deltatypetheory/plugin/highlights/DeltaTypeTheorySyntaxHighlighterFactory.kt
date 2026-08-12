package camp.delta.deltatypetheory.plugin.highlights

import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class DeltaTypeTheorySyntaxHighlighterFactory : SyntaxHighlighterFactory() {
    override fun getSyntaxHighlighter(
        project: Project?,
        file: VirtualFile?
    ): SyntaxHighlighter {
        return DeltaTypeTheorySyntaxHighlighter()
    }
}
