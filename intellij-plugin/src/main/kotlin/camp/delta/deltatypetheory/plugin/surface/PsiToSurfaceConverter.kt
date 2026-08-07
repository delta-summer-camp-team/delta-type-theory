package camp.delta.deltatypetheory.plugin.surface

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryType
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryAxiomDecl
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryDefDecl
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryExpr
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryApplication
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryAtom
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryLambdaExpr
import camp.delta.deltatypetheory.plugin.psi.DeltaTypeTheoryPiExpr
import camp.delta.deltatypetheory.core.surface.model.SurfaceProgram
import camp.delta.deltatypetheory.core.surface.model.SurfaceDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceName
import camp.delta.deltatypetheory.core.surface.model.SurfaceAxiomDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceDefDecl
import camp.delta.deltatypetheory.core.surface.model.SurfaceTerm
import camp.delta.deltatypetheory.core.surface.model.SurfaceTypeTerm
import camp.delta.deltatypetheory.core.surface.model.SurfaceNameRef
import camp.delta.deltatypetheory.core.surface.model.SurfaceApp
import camp.delta.deltatypetheory.core.surface.model.SurfaceBinder
import camp.delta.deltatypetheory.core.surface.model.SurfaceLambda
import camp.delta.deltatypetheory.core.surface.model.SurfacePi

class PsiToSurfaceConverter {

    fun convert(file: PsiFile): SurfaceProgram {
        val declarations = collectTopLevelDeclarations(file)

        return SurfaceProgram(
            declarations = declarations.map { convertDecl(it) },
            fileName = file.name
        )
    }


    private fun collectTopLevelDeclarations(file: PsiFile): List<PsiElement> {
        return file.children.filter { element ->
            when (element.node?.elementType) {
                DeltaTypeTheoryType.AXIOM_DECL,
                DeltaTypeTheoryType.DEF_DECL -> true

                else -> false
            }
        }
    }

    private fun convertDecl(element: PsiElement): SurfaceDecl {
        return when (element.node?.elementType) {
            DeltaTypeTheoryType.AXIOM_DECL -> {
                convertAxiom(element)
            }

            DeltaTypeTheoryType.DEF_DECL -> {
                convertDef(element)
            }

            else -> {
                error("Unknown declaration: ${element.node?.elementType}")
            }
        }
    }

    private fun convertAxiom(element: PsiElement): SurfaceDecl {
        val axiom = element as DeltaTypeTheoryAxiomDecl

        val name = SurfaceName(axiom.identifier.text)
        val type = convertExpr(axiom.expr)

        return SurfaceAxiomDecl(
            name = name,
            type = type,
            range = null
        )
    }


    private fun convertDef(element: PsiElement): SurfaceDecl {
        val def = element as DeltaTypeTheoryDefDecl
        val expressions = def.exprList

        require(expressions.size == 2) {
            "Expected 2 expressions in def, but got ${expressions.size}"
        }

        val name = SurfaceName(def.identifier.text)
        val type = convertExpr(expressions[0])
        val value = convertExpr(expressions[1])

        return SurfaceDefDecl(
            name = name,
            type = type,
            value = value,
            range = null
        )
    }


    private fun convertExpr(element: PsiElement): SurfaceTerm {
        val expr = element as DeltaTypeTheoryExpr

        expr.application?.let {
            return convertApplication(it)
        }

        expr.atom?.let {
            return convertAtom(it)
        }

        expr.lambdaExpr?.let {
            return convertLambda(it)
        }

        expr.piExpr?.let {
            return convertPi(it)
        }

        error("Unknown expression: ${element.text}")
    }

    private fun convertApplication(
        element: DeltaTypeTheoryApplication
    ): SurfaceTerm {
        var result = convertAtom(element.atom)

        for (argument in element.argumentList) {
            result = SurfaceApp(
                result,
                convertExpr(argument.expr)
            )
        }

        return result
    }

    private fun convertAtom(
        element: DeltaTypeTheoryAtom
    ): SurfaceTerm {

        element.identifier?.let { identifier ->
            val name = identifier.text

            return if (name == "Type") {
                SurfaceTypeTerm
            } else {
                SurfaceNameRef(
                    SurfaceName(name)
                )
            }
        }

        element.expr?.let { expr ->
            return convertExpr(expr)
        }

        error("Unknown atom: ${element.text}")
    }

    private fun convertLambda(
        element: DeltaTypeTheoryLambdaExpr
    ): SurfaceTerm {

        val expressions = element.exprList

        require(expressions.size == 2) {
            "Expected 2 expressions in lambda, but got ${expressions.size}"
        }

        val binder = SurfaceBinder(
            name = SurfaceName(element.identifier.text),
            type = convertExpr(expressions[0])
        )

        return SurfaceLambda(
            binder = binder,
            body = convertExpr(expressions[1])
        )
    }

    private fun convertPi(
        element: DeltaTypeTheoryPiExpr
    ): SurfaceTerm {

        val expressions = element.exprList

        require(expressions.size == 2) {
            "Expected 2 expressions in pi, but got ${expressions.size}"
        }

        val binder = SurfaceBinder(
            name = SurfaceName(element.identifier.text),
            type = convertExpr(expressions[0])
        )

        return SurfacePi(
            binder = binder,
            body = convertExpr(expressions[1])
        )
    }

}
