# Rules

1. Добавить поддержку синтаксиса для правил
    ```
      rule name term ↦ term
    ```
    например
    ```
      rule natRec.zero: natRec(P)(z)(s)(zero) ↦ z;
      rule natRec.succ: natRec(P)(z)(s)(succ(n)) ↦ s(n)(natRec(P)(z)(s)(n));
    ```
2. (data) Ввести отдельную коллекцию правил в SurfaceProgram и научиться их коллекционировать (surface)

3. Добавить обработку правил в `PsiToSurfaceConverter`
4. Добавить обработку правил в реальный тайпчек (core)

# Meta и неявные переменные
1. Ввести синтаксис пропущенный типов или имён переменных для Pi и λ:
   такое должно парситься (команда plugin)
    ```
   def plus : Nat → Nat → Nat :=
     λ m => λ n => natRec(λ x => Nat)(n)(λ x => λ ih succ(ih))(m);
   ```
   т.е. выражения `λ m =>` должно парcиться, `Nat → Nat → Nat`
2. Ввести новый тип surface-терма -- Meta (должна хранить номер).
3. Адаптировать Surface типы и алгоритмы конверсии из psi в surface term
4. Научиться авто-вычислять meta на основе данных


   


