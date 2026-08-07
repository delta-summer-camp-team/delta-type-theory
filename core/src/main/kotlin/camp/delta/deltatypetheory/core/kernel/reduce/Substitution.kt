package camp.delta.deltatypetheory.core.kernel.reduce

import camp.delta.deltatypetheory.core.kernel.elaborate.shift
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm

/** Replaces [index], dropping its binder. */
internal fun CoreTerm.substitute(index: Int, replacement: CoreTerm, depth: Int = 0): CoreTerm =
  when (this) {

    is BoundVar -> when {
      // hit: move it in
      this.index == index + depth -> replacement.shift(depth)
      // past the dropped binder
      this.index > index + depth -> BoundVar(this.index - 1)
      else -> this
    }

    // body only
    is Lambda -> Lambda(
      parameterType.substitute(index, replacement, depth),
      body.substitute(index, replacement, depth + 1),
    )
    is Pi -> Pi(
      parameterType.substitute(index, replacement, depth),
      body.substitute(index, replacement, depth + 1),
    )

    is App -> App(
      function.substitute(index, replacement, depth),
      argument.substitute(index, replacement, depth),
    )

    TypeTerm -> this
    is GlobalRef -> this

    is GlobalName -> this // TODO: not a term
  }

/** Beta step. No outer shifts: substitute already decrements. */
internal fun substituteTop(body: CoreTerm, replacement: CoreTerm): CoreTerm =
  body.substitute(0, replacement)
