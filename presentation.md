---
title: Scala type-level operations
author: "[Matt Bovel](mailto:matthieu@bovel.net) @[LAMP](https://www.epfl.ch/labs/lamp/)/[LARA](https://lara.epfl.ch/w/), [EPFL](https://www.epfl.ch/fr/)"
---

# Literal types

Type inhabited by a single constant value known at compile-time:

```scala
val x: 3 = 3
val y: false = false
val z: "monday" | "tuesday" = "monday"
```

<small>See [SIP-23 - literal-based singleton types](https://docs.scala-lang.org/sips/42.type.html).</small>

# Term-reference types

Type inhabited by a single non-necessary-constant term:

```scala
val a: Int = ???
val b: Int = ???
val c: a.type = a
val d: Int = a    // Ok because (a: Int) <: Int
```
<div class="fragment">
```scala
val e: a.type = b // Error: found (b: Int)
                  // but required (a: Int)
```
</div>

# Dependent parameter types

```scala
def same(a: Any, b: a.type) = true
same(3, 3) // Ok
same(3, 4) // Error
```
<div class="fragment">
```scala
def same2[T](a: T, b: T) = true
same2(3, 4) // Ok
```
</div>

# Compile-time operations

Simple bounded type aliases:

```scala
infix type +[X <: Int, Y <: Int] <: Int
```

<div class="fragment">
With special compiler support for constant-folding:

```scala
import scala.compiletime.ops.int.+

val a: 2 + 2 = 4
```


<small>See [Add primitive compiletime operations on singleton types #7628](https://github.com/lampepfl/dotty/pull/7628).</small>
</div>

# Refinement types

```scala
case class Vec(size: Int)

val v: Vec {val size: 2} = ???

val size: 2 = v.size
```

# Example: Sized vectors

<examples/1_vec.scala>

# Algebraic reasoning

```scala
// Summing x n times is normalized to x * n.
summon[2L * m.type =:= m.type + m.type]
summon[2L * m.type + 2L * m.type =:= m.type + 3L * m.type]
summon[2L * m.type * m.type =:= m.type * 2L * m.type]
```

# Example: Check `Vec.map` (refinements)

Live.

# Example: Check `Vec.map` (type params)

```scala
import compiletime.ops.int.{+,-}

enum Vec[Len <: Int, +T]:
  case Nil extends Vec[0, Nothing]
  case NotNil[T]() extends Vec[Int, T]

  def ::[S >: T](x: S): Vec[Len + 1, T] = ???
  def tail: Vec[Len - 1, T] = ???
  def head: T = ???

  def map[S](f: T => S): Vec[Len, S] =
    this match
      case Vec.Nil => Vec.Nil
      case _ => f(this.head) :: this.tail.map(f)
```

<small>See [Boruch-Gruszecki, A. (2019). GADTs in Dotty. Slide 16.](https://portal.klewel.com/watch/webcast/typelevel-summit-lausanne-2019/talk/9/)</small>

# Example: tf-dotty (with abstract dimensions)

```scala
val x: Int = 2
val y: Int = 2
val tensor = tf.zeros(x #: y #: SNil)
val res = tf.reshape(tensor, y #: x #: SNil)
```

<small>See [github.com/MaximeKjaer/tf-dotty](https://github.com/MaximeKjaer/tf-dotty), in particular the [implementation of `reshape`](https://github.com/MaximeKjaer/tf-dotty/blob/45af57dd0f60cb2d2fc9cf56f963b6ca4bd32909/modules/tensorflow/src/main/scala/io/kjaer/tensorflow/core/tf.scala#L82-L97).</small>

# Open problems with normalization

- Exponential explosion and compilation time
- Sub-typing

# Match types

```scala
type IsEmpty[S <: String] <: Boolean = S match {
  case "" => true
  case _ => false
}

summon[IsEmpty[""] =:= true]
summon[IsEmpty["hello"] =:= false]
```

<small>See [Blanvillain, O., Brachthäuser, J., Kjaer, M., & Odersky, M. (2021). Type-Level Programming with Match Types. 70.](http://infoscience.epfl.ch/record/290019)</small>

# Example: strongly-typed `printf`

<examples/2_printf.scala>

<small>See also [`printf` in Zig](https://ziglang.org/documentation/0.1.1/#case-study-printf)</small>

# Example: regsafe

```scala
import regsafe.Regex

val date = Regex("""(\d{4})-(\d{2})-(\d{2})""")
"2004-01-20" match
  case date(y, m, d, a) =>
    s"$y was a good year for PLs."
```

<small>See [github.com/OlivierBlanvillain/regsafe](https://github.com/OlivierBlanvillain/regsafe) and [Blanvillain, O. (2022). Type-Safe Regular Expressions.](https://2022.ecoop.org/details/scala-2022-papers/1/Type-Safe-Regular-Expressions)</small>

# Selectable

_Not presented._

<examples/3_selectable.scala>

# Hands-on: strongly-typed CSV (macros)

_Not presented._

<examples/4_macro_refinement/1_macro.scala>

# Hands-on: implement `Read`

Live.

Code written during the live session: <https://github.com/dotty-staging/dotty/commit/7dfde36139bc380ac9c6e0c4f5ee80647006c29e>.

<small>For a real-world example of generating types at compile-time, see [FSharp.Data](https://fsprojects.github.io/FSharp.Data/library/CsvFile.html) and [F# Type Providers](https://docs.microsoft.com/en-us/dotnet/fsharp/tutorials/type-providers/).</small>

# Thanks!
