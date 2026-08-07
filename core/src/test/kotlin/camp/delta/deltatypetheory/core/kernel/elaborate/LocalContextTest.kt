package camp.delta.deltatypetheory.core.kernel.elaborate

import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LocalContextTest {

  @Test
  fun `unknown name resolves to null`() {
    assertNull(LocalContext().resolve("nope"))
  }

  @Test
  fun `position in the list is the de Bruijn index`() {
    val ctx = LocalContext().push("a", TypeTerm).push("b", TypeTerm)
    assertEquals(0, ctx.resolve("b")?.deBruijnIndex)
    assertEquals(1, ctx.resolve("a")?.deBruijnIndex)
  }

  @Test
  fun `inner binding shadows outer`() {
    val ctx = LocalContext().push("x", TypeTerm).push("x", TypeTerm)
    assertEquals(0, ctx.resolve("x")?.deBruijnIndex)
  }

  @Test
  fun `dependent type is shifted into the current scope`() {
    // x : A, pushed one binder after A
    val ctx = LocalContext().push("A", TypeTerm).push("x", BoundVar(0))
    assertEquals(LocalResolution(0, BoundVar(1)), ctx.resolve("x"))
    assertEquals(LocalResolution(1, TypeTerm), ctx.resolve("A"))
  }

  @Test
  fun `shift accumulates across several binders`() {
    val ctx = LocalContext()
      .push("A", TypeTerm)
      .push("x", BoundVar(0))
      .push("y", BoundVar(1))
    assertEquals(LocalResolution(0, BoundVar(2)), ctx.resolve("y"))
  }

  @Test
  fun `push leaves the original context untouched`() {
    val ctx = LocalContext()
    ctx.push("x", TypeTerm)
    assertTrue(ctx.bindings.isEmpty())
  }
}
