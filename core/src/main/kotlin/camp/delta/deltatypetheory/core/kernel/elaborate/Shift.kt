package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm

/** Renumbers free variables. */
internal fun CoreTerm.shift(amount: Int, cutoff: Int = 0): CoreTerm = when (this) {

  // below cutoff: bound here
  is BoundVar -> if (index >= cutoff) BoundVar(index + amount) else this

  // body only
  is Lambda -> Lambda(parameterType.shift(amount, cutoff), body.shift(amount, cutoff + 1))
  is Pi -> Pi(parameterType.shift(amount, cutoff), body.shift(amount, cutoff + 1))

  is App -> App(function.shift(amount, cutoff), argument.shift(amount, cutoff))

  TypeTerm -> this
  is GlobalRef -> this

  is GlobalName -> this // TODO: not a term
}
