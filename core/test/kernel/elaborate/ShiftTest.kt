package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import kotlin.test.Test
import kotlin.test.assertEquals

class ShiftTest {

  @Test
  fun `free variable moves`() {
    assertEquals(BoundVar(3), BoundVar(1).shift(2))
  }

  @Test
  fun `bound variable does not move`() {
    val identity = Lambda(TypeTerm, BoundVar(0))
    assertEquals(identity, identity.shift(5))
  }

  @Test
  fun `boundary index is treated as free`() {
    assertEquals(BoundVar(1), BoundVar(0).shift(1, cutoff = 0))
  }

  @Test
  fun `parameter type and body use different cutoffs`() {
    assertEquals(
      Lambda(BoundVar(1), BoundVar(0)),
      Lambda(BoundVar(0), BoundVar(0)).shift(1),
    )
  }

  @Test
  fun `pi behaves like lambda`() {
    assertEquals(
      Pi(BoundVar(1), BoundVar(0)),
      Pi(BoundVar(0), BoundVar(0)).shift(1),
    )
  }

  @Test
  fun `shifting by zero is identity`() {
    val term = Lambda(TypeTerm, App(BoundVar(0), BoundVar(1)))
    assertEquals(term, term.shift(0))
  }

  @Test
  fun `nested binders accumulate cutoff`() {
    assertEquals(
      Pi(TypeTerm, Pi(TypeTerm, BoundVar(12))),
      Pi(TypeTerm, Pi(TypeTerm, BoundVar(2))).shift(10),
    )
  }

  @Test
  fun `application shifts both children at the same depth`() {
    assertEquals(
      App(BoundVar(2), BoundVar(3)),
      App(BoundVar(0), BoundVar(1)).shift(2),
    )
  }
}
