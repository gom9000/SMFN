# Part 4: Numerical Analysis

## The Problem-Solver Model
A `Problem` never contains an algorithm. It describes what "solved" means — a residual to zero out, a derivative, a system's dynamics — and nothing about how to get there. A `Solver` never contains problem-specific data; it takes a `Problem` and an initial state and iterates.

```java
public interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>> {
    R solve(P problem, S initialState, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<S> space);
}
```

Two things about this signature matter beyond the obvious: `ConvergenceCriteria` (when to stop) and `MetricSpace<S>` (how to measure distance between iterations) are supplied by the caller, not inferred by the solver — the same "structures are explicit, never guessed" principle from Part 1, applied here to algorithms instead of arithmetic. And `S`/`R` must be `AlgebraicElement`, not a specific numeric type — the same solver machinery works whether the iterating state is a `Real`, a `Vector<K>`, or something else entirely.

## Root-finding

```
IterationProblem<P>              Mapping<P,P>, no interpretation fixed yet
  RootFindingProblem<P>            apply(P) = "the residual to drive to zero"
    ScalarRootFindingProblem<T>      one variable
    VectorRootFindingProblem<K>      several variables, over Vector<K>
  FixedPointProblem<T>             apply(T) = "the next iterate, T_{k+1} = G(T_k)"
```

`RootFindingProblem` and `FixedPointProblem` are not merged into one type, even though both are ultimately `Mapping<P,P>`: a root-finding residual and a fixed-point step are read completely differently by whatever consumes them, and a fractal orbit (Part 6's cousin, the Mandelbrot/Julia iteration) is neither of those two readings — it's watched to see whether it diverges, not driven toward a fixed target. `IterationProblem` exists as the neutral base precisely so a third reading doesn't have to be forced into one of the other two.

### One variable: `NewtonRaphsonSolver`

```java
ScalarRootFindingProblem<Real> problem = x -> R.subtract(R.multiply(x, x), R.of(2)); // x^2 - 2

NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, new CentralDifferenceDifferentiator<>(R, R.of(1e-6)));
Real root = solver.solve(problem, R.of(1.0), (d, p, it) -> d.getValue() < p.getTolerance().getValue(),
                          new ConvergenceParameters(R.of(1e-10), 50), (a, b) -> R.subtract(a, b).abs());
// converges to sqrt(2)
```

The derivative is an *optional capability*, not a required argument. If `problem` also implements `DifferentiableScalarProblem<T>` (adding `getDerivative()`), the solver uses it directly; otherwise it falls back to the numeric differentiator passed to its constructor. The check is a plain `instanceof`, never a default method that throws — the same pattern `Conjugable`, `Sqrtable`, and `InvertibleElements` use elsewhere.

### Several variables: `VectorNewtonRaphsonSolver`

The multivariate case replaces "divide by the derivative" with "invert the Jacobian":

```java
public class VectorNewtonRaphsonSolver<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> {
    public Vector<K> solve(DifferentiableVectorProblem<K> problem, Vector<K> initialGuess,
                            ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<Vector<K>> space);
}
```

Unlike the scalar case, there is no numeric fallback here — estimating a full Jacobian by finite differences costs one evaluation per column, and no solver in this library does that automatically today. `DifferentiableVectorProblem` (`getJacobian(): Mapping<Vector<K>, SquareMatrix<K>>`) is required.

Building the Jacobian by hand for every problem would defeat the point, so `MultivariateFunctionSystemProblem<K,S>` assembles it for you from a list of independent scalar-valued functions of several variables:

```java
MultivariateFunctionSystemProblem<Real, RealField> problem =
    new MultivariateFunctionSystemProblem<>(List.of(circle, line), R, R.of(1e-6));
```

Each function in the list becomes one row of `F` and one row of the Jacobian. Row by row, if a function implements `DifferentiableMultivariateFunction` (the same optional-capability pattern, `getGradient()` this time), that row uses the exact gradient; otherwise it falls back to a numeric estimate (`CentralDifferenceGradientEstimator`, central differences, one row at a time). A system can mix both kinds of function freely — some rows exact, some estimated — because the choice is made independently per row.

This is a genuinely square-only tool: `n` functions, `n` unknowns, one isolated solution. Two planes meeting in a line, or a single equation in several unknowns, describe a whole set of solutions rather than a point, and belong to direct linear algebra (Part 3), not to this solver.

### All the roots of a polynomial: `PolynomialRootSolver`

Newton-Raphson only ever finds one root. `PolynomialRootSolver<K,S>` finds all of them by repeating find-and-deflate: find one root, divide the polynomial by `(x - root)` using the exact polynomial division from Part 2, repeat on the quotient.

```java
ComplexField C = ComplexField.INSTANCE;
PolynomialRootSolver<Complex, ComplexField> rootSolver = new PolynomialRootSolver<>(
    C, new Complex(1e-6, 0), (a, b) -> new Real(C.subtract(a, b).modulus()), new Complex(0.4, 0.9),
    new ConvergenceParameters(new Real(1e-10), 100));

List<Complex> roots = rootSolver.findAllRoots(polynomial);
```

Completeness — finding exactly `n` roots for a degree-`n` polynomial — is guaranteed by the fundamental theorem of algebra only when `K` is algebraically closed. That is why the example works over `Complex`, not `Real`: on `Real`, `x^2+1` simply has no roots to find, and the solver correctly stops rather than inventing one.

## Integration and Differential Equations

```
DifferentialEquationProblem<K,V>     V' = F(t, V) — binary (state, time), not a Mapping
  InitialValueProblem<K,V>             + getInitialState(), getStartTime()
  BoundaryValueProblem<K,V>            + getEndTime(), start/end boundary conditions
```

`V` is bound to `LinearElement<V,K>`, not `Vector<K>` specifically — Part 3 explains why this matters: a `SquareMatrix<K>`-valued state satisfies the same bound, so a matrix differential equation (the Schrödinger equation in Part 6, for instance) is solved by the exact same integrator as a vector-valued one.

`BoundaryValueProblem` is a defined shape with no solver built against it yet — everything below consumes `InitialValueProblem` only.

```java
public interface IntervalODEStepSolver<K extends ScalarElement<K>, V extends LinearElement<V, K>, S extends Ring<K> & ScalarStructure<K>>
extends IntervalSolver<K, V, S> {
    V step(DifferentialEquationProblem<K, V> system, V currentState, Real currentTime, Real deltaTime, Module<V, K, S> space);
}
```

Two implementations exist:

| Solver | Step size | Requires of `S` |
|---|---|---|
| `RungeKutta4Solver<K,V,S>` | fixed (`IntegrationParameters(fixedStepSize)`) | `Ring & ScalarStructure & NumericFactory` |
| `EmbeddedRK23Solver<K,V,S>` | adaptive (`IntegrationParameters(fixedStepSize?, tolerance, maxStepSize, minStepSize)`) | `Field & ScalarStructure & NumericFactory` — needs division to rescale the step |

```java
RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
Vector<Real> result = solver.integrate(problem, R.of(1.0), new IntegrationParameters(R.of(0.01)), vectorSpace);
```

The fixed-step solver derives its direction purely from `startTime` versus `endTime`; the sign given for `fixedStepSize` is not trusted, only its magnitude — passing a negative step for a forward integration does not silently reverse the direction.

The adaptive solver shrinks or grows the step based on a local error estimate (comparing a second- and third-order result at the same step), rejecting and retrying with a smaller step whenever the estimate exceeds tolerance, and giving up with a clear exception once the required step drops below `minStepSize` rather than continuing indefinitely.

## Numerical differentiation as a fallback, not a first resort

Three small utilities exist purely to stand in for a derivative or gradient that a `Problem` doesn't provide analytically:

| Class | Estimates | For |
|---|---|---|
| `CentralDifferenceDifferentiator<K>` | `f'(x) ≈ (f(x+h) - f(x-h)) / 2h` | `Mapping<K,K>` |
| `ForwardDifferenceDifferentiator<K>` | `f'(x) ≈ (f(x+h) - f(x)) / h` | `Mapping<K,K>` |
| `CentralDifferenceGradientEstimator<K,S>` | `∇f(v)`, one component at a time | `MultivariateFunction<K>` |

Each implements `Mapping<Mapping<K,K>, Mapping<K,K>>` (or the vector equivalent): "turns a function into another function" needs no interface of its own beyond `Mapping` applied to `Mapping` itself. They are consulted only when the more specific `Differentiable...` capability is absent — the analytic path is always preferred where it exists.
