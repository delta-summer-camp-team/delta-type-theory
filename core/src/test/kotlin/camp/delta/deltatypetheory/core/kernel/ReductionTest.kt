package camp.delta.deltatypetheory.core.kernel

import camp.delta.deltatypetheory.core.kernel.load.GlobalBinding
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.reduction.GlobalResolver
import camp.delta.deltatypetheory.core.kernel.reduction.definitionallyEqual
import camp.delta.deltatypetheory.core.kernel.reduction.normalize
import camp.delta.deltatypetheory.core.kernel.reduction.whnf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReductionTest {

  private val zero = GlobalRef(GlobalName("zero"))
  private fun succ(t: CoreTerm) = App(GlobalRef(GlobalName("succ")), t)
  private val id = Lambda(TypeTerm, BoundVar(0))

  private fun resolver(vararg globals: Pair<String, CoreTerm>): GlobalResolver {
    val table = globals.toMap()
    return { name -> table[name.value] }
  }

  @Test
  fun `whnf on leaves returns them unchanged`() {
    assertEquals(TypeTerm, whnf(TypeTerm, resolver()))
    assertEquals(BoundVar(3), whnf(BoundVar(3), resolver()))
    assertEquals(zero, whnf(zero, resolver()))
  }

  @Test
  fun `whnf reveals a global value`() {
    assertEquals(
      succ(zero),
      whnf(GlobalRef(GlobalName("x")), resolver("x" to succ(zero)))
    )
  }

  @Test
  fun `whnf reduces beta redex at the head`() {
    assertEquals(
      TypeTerm,
      whnf(App(GlobalRef(GlobalName("id")), TypeTerm), resolver("id" to id))
    )
  }

  @Test
  fun `whnf stops at a stuck application`() {
    val stuck = App(zero, TypeTerm)
    assertEquals(stuck, whnf(stuck, resolver()))
  }

  @Test
  fun `whnf does not look inside lambda body`() {
    // Внутри лямбды -- редекс, но whnf его не трогает (слабая форма).
    val term = Lambda(TypeTerm, App(Lambda(TypeTerm, BoundVar(0)), TypeTerm))
    assertEquals(term, whnf(term, resolver()))
  }

  @Test
  fun `normalize does look inside lambda body`() {
    val term = Lambda(TypeTerm, App(Lambda(TypeTerm, BoundVar(0)), TypeTerm))
    assertEquals(Lambda(TypeTerm, TypeTerm), normalize(term, resolver()))
  }

  @Test
  fun `normalize reduces chains of globals`() {
    // x := y, y := zero
    val term = GlobalRef(GlobalName("x"))
    assertEquals(zero, normalize(term, resolver("x" to GlobalRef(GlobalName("y")), "y" to zero)))
  }

  @Test
  fun `normalize inside application children`() {
    // id(inner), где inner -- редекс, сворачивается до Type целиком.
    val term = App(GlobalRef(GlobalName("id")), App(Lambda(TypeTerm, BoundVar(0)), TypeTerm))
    assertEquals(TypeTerm, normalize(term, resolver("id" to id)))
  }

  @Test
  fun `definitionally equal when x is defined as succ zero`() {
    // AC из плана: definitionallyEqual(x, succ(zero)) == true, если x := succ(zero).
    val x = GlobalRef(GlobalName("x"))
    val ctx = resolver("x" to succ(zero))
    assertTrue(definitionallyEqual(x, succ(zero), ctx))
  }

  @Test
  fun `definitionally equal via ElaborationContext`() {
    // C6 теперь умеет ходить за значениями в настоящий C2-контекст.
    val ctx = ElaborationContext()
    ctx.addGlobal(GlobalBinding(GlobalName("x"), TypeTerm, succ(zero)))
    assertTrue(definitionallyEqual(GlobalRef(GlobalName("x")), succ(zero), ctx))
  }

  @Test
  fun `definitionally equal via beta reduction`() {
    // id(Type) и Type -- одно и то же после упрощения.
    val ctx = resolver("id" to id)
    assertTrue(definitionallyEqual(App(GlobalRef(GlobalName("id")), TypeTerm), TypeTerm, ctx))
  }

  @Test
  fun `structurally different convertible terms are equal`() {
    // x -- ссылка на zero, y -- применение id к zero: структурно разные, но обе формы -- zero.
    val ctx = resolver(
      "x" to zero,
      "y" to App(GlobalRef(GlobalName("id")), zero),
      "id" to id
    )
    assertTrue(definitionallyEqual(GlobalRef(GlobalName("x")), GlobalRef(GlobalName("y")), ctx))
  }

  @Test
  fun `non convertible terms are not equal`() {
    val ctx = resolver("x" to zero)
    assertFalse(definitionallyEqual(GlobalRef(GlobalName("x")), succ(zero), ctx))
  }

  @Test
  fun `definitionally equal on pi types`() {
    val pi1 = Pi(TypeTerm, TypeTerm)
    val pi2 = Pi(TypeTerm, TypeTerm)
    assertTrue(definitionallyEqual(pi1, pi2, resolver()))
  }

  @Test
  fun `normalize resolves globals inside pi body`() {
    // (t : Type) -> x, где x := Type
    val term = Pi(TypeTerm, GlobalRef(GlobalName("x")))
    assertEquals(Pi(TypeTerm, TypeTerm), normalize(term, resolver("x" to TypeTerm)))
  }
}
