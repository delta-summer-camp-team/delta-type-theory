package camp.delta.deltatypetheory.core.kernel.reduction

import camp.delta.deltatypetheory.core.kernel.elaborate.ElaborationContext
import camp.delta.deltatypetheory.core.kernel.elaborate.shift
import camp.delta.deltatypetheory.core.kernel.model.App
import camp.delta.deltatypetheory.core.kernel.model.BoundVar
import camp.delta.deltatypetheory.core.kernel.model.CoreTerm
import camp.delta.deltatypetheory.core.kernel.model.GlobalName
import camp.delta.deltatypetheory.core.kernel.model.GlobalRef
import camp.delta.deltatypetheory.core.kernel.model.Lambda
import camp.delta.deltatypetheory.core.kernel.model.Pi
import camp.delta.deltatypetheory.core.kernel.model.TypeTerm
import camp.delta.deltatypetheory.core.kernel.reduce.applyRule

/**
 * Крюк вместо ElaborationContext: по имени глобала возвращает его значение.
 * Позже обернём в ctx.lookupGlobal(name.value)?.value.
 */
typealias GlobalResolver = (GlobalName) -> CoreTerm?

/**
 * Слабая головная нормальная форма: упрощает ТОЛЬКО голову (верхний узел),
 * не залезая внутрь детей.
 */
fun whnf(
    term: CoreTerm,
    resolve: GlobalResolver,
): CoreTerm =
    when (term) {
        is TypeTerm -> {
            term
        }

        is BoundVar -> {
            term
        }

        is GlobalRef -> {
            val value = resolve(term.name)
            if (value != null) whnf(value, resolve) else term
        }

        is Lambda -> {
            term
        }

        is Pi -> {
            term
        }

        is App -> {
            when (val f = whnf(term.function, resolve)) {
                is Lambda -> whnf(substituteTop(f.body, term.argument), resolve)
                else -> App(f, term.argument)
            }
        }

        is GlobalName -> {
            term
        }
    }

/**
 * Полная нормальная форма: whnf для головы + рекурсивная нормализация детей.
 */
fun normalize(
    term: CoreTerm,
    resolve: GlobalResolver,
): CoreTerm =
    when (val head = whnf(term, resolve)) {
        is Pi -> {
            Pi(
                normalize(head.parameterType, resolve),
                normalize(head.body, resolve),
            )
        }

        is Lambda -> {
            Lambda(
                normalize(head.parameterType, resolve),
                normalize(head.body, resolve),
            )
        }

        is App -> {
            App(
                normalize(head.function, resolve),
                normalize(head.argument, resolve),
            )
        }

        else -> {
            head
        }
    }

/**
 * Два терма конвертируются, если их нормальные формы совпадают.
 */
fun definitionallyEqual(
    left: CoreTerm,
    right: CoreTerm,
    resolve: GlobalResolver,
): Boolean = normalize(left, resolve) == normalize(right, resolve)

// ---------------------------------------------------------------------------
// Перегрузки с ElaborationContext (C2): раскрытие глобалов через контекст.
// Реализация одна и та же -- просто другой источник значений.
// ---------------------------------------------------------------------------

fun whnf(
    term: CoreTerm,
    ctx: ElaborationContext,
): CoreTerm {
    val reduced = whnf(term) { name ->
        ctx.lookupGlobal(name.value)?.value
    }

    for (rule in ctx.rules) {
        val rewritten = applyRule(rule, reduced)
        if (rewritten != null) {
            return whnf(rewritten, ctx)
        }
    }

    return reduced
}

fun normalize(
    term: CoreTerm,
    ctx: ElaborationContext,
): CoreTerm = normalize(term) { name -> ctx.lookupGlobal(name.value)?.value }

fun definitionallyEqual(
    left: CoreTerm,
    right: CoreTerm,
    ctx: ElaborationContext,
): Boolean = definitionallyEqual(left, right) { name -> ctx.lookupGlobal(name.value)?.value }

// ---------------------------------------------------------------
// Внутренние помощники C6: минимальная подстановка для бета-редукции.
// Отдельной задачи C5 пока не делаем; здесь только то, что нужно whnf.
// ---------------------------------------------------------------

/**
 * Заменяет переменную де Бройна [index] на [replacement].
 * [depth] -- сколько биндеров уже пройдено; растёт только для тела Lambda/Pi.
 */
private fun CoreTerm.substitute(
    index: Int,
    replacement: CoreTerm,
    depth: Int = 0,
): CoreTerm =
    when (this) {
        is BoundVar -> {
            when {
                this.index == index + depth -> replacement.shift(depth)
                this.index > index + depth -> BoundVar(this.index - 1)
                else -> this
            }
        }

        is App -> {
            App(
                function.substitute(index, replacement, depth),
                argument.substitute(index, replacement, depth),
            )
        }

        is Lambda -> {
            Lambda(
                parameterType.substitute(index, replacement, depth),
                body.substitute(index, replacement, depth + 1),
            )
        }

        is Pi -> {
            Pi(
                parameterType.substitute(index, replacement, depth),
                body.substitute(index, replacement, depth + 1),
            )
        }

        is TypeTerm -> {
            this
        }

        is GlobalRef -> {
            this
        }

        is GlobalName -> {
            this
        }
    }

/** Подстановка самой верхней переменной (позиция 0) в [body] -- форма результата бета-редукции. */
internal fun substituteTop(
    body: CoreTerm,
    replacement: CoreTerm,
): CoreTerm = body.substitute(index = 0, replacement = replacement)
