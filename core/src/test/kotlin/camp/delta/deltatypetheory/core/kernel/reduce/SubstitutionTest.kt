package camp.delta.deltatypetheory.core.kernel.reduce

import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class SubstitutionTest {

  private val g = GlobalRef(GlobalName("g"))

  @Test
  fun `target variable is replaced`() {
    assertEquals(g, BoundVar(0).substitute(0, g))
  }

  @Test
  fun `variable past the binder is decremented`() {
    assertEquals(BoundVar(0), BoundVar(1).substitute(0, g))
  }

  @Test
  fun `variable below the target is untouched`() {
    assertEquals(BoundVar(0), BoundVar(0).substitute(1, g))
  }

  @Test
  fun `depth rises under a binder`() {
    // inside the lambda the target is 1, not 0
    assertEquals(
      Lambda(TypeTerm, g),
      Lambda(TypeTerm, BoundVar(1)).substitute(0, g),
    )
  }

  @Test
  fun `binder's own variable is untouched`() {
    val identity = Lambda(TypeTerm, BoundVar(0))
    assertEquals(identity, identity.substitute(0, g))
  }

  @Test
  fun `replacement is shifted as it goes deeper`() {
    // the free variable in the replacement now sits under one binder
    assertEquals(
      Lambda(TypeTerm, BoundVar(1)),
      Lambda(TypeTerm, BoundVar(1)).substitute(0, BoundVar(0)),
    )
  }

  @Test
  fun `parameter type and body use different depths`() {
    // same index 1 in both slots: decremented in the parameter type,
    // but it is the target inside the body
    assertEquals(
      Pi(BoundVar(0), g),
      Pi(BoundVar(1), BoundVar(1)).substitute(0, g),
    )
  }

  @Test
  fun `application substitutes both children at the same depth`() {
    assertEquals(
      App(g, BoundVar(0)),
      App(BoundVar(0), BoundVar(1)).substitute(0, g),
    )
  }

  @Test
  fun `leaves are untouched`() {
    assertEquals(TypeTerm, TypeTerm.substitute(0, g))
    assertEquals(g, g.substitute(0, g))
  }

  @Test
  fun `substituteTop performs a beta step`() {
    // (\x. x) g  ->  g
    assertEquals(g, substituteTop(BoundVar(0), g))
  }

  @Test
  fun `substituteTop decrements outer references exactly once`() {
    // the double-shift bug turns this into BoundVar(-1)
    assertEquals(BoundVar(0), substituteTop(BoundVar(1), g))
  }

  @Test
  fun `substituteTop shifts the argument under inner binders`() {
    // (\x. \y. x) v  ->  \y. v-shifted-by-one
    assertEquals(
      Lambda(TypeTerm, BoundVar(1)),
      substituteTop(Lambda(TypeTerm, BoundVar(1)), BoundVar(0)),
    )
  }
}
