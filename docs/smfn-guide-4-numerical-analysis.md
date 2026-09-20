# Part 4: Numerical Analysis

## The Problem-Solver Model
A `Problem` describes what "solved" means (a residual to zero out, a system's dynamics, or a sequence of iterates) without specifying how to reach that solution. A `Solver` contains no problem-specific data; it consumes a `Problem` along with initial states and execution parameters to iteratively compute a solution.

### Problem Abstractions (`core.problems`)
All numerical problems inherit from `AnalysisProblem<P>`, which acts as a marker interface for the domain:

```text
AnalysisProblem<P>
 ├── IterationProblem<P>
 │    ├── RootFindingProblem<P>
 │    │    ├── ScalarRootFindingProblem<T>
 │    │    └── VectorRootFindingProblem<K>
 │    └── FixedPointProblem<T>
 └── DifferentialEquationProblem<K, V>
      ├── InitialValueProblem<K, V>
      └── BoundaryValueProblem<K, V>
```

* **`IterationProblem<P>`**: It serves as a structural base for any sequence where an element is mapped to another of the same type.
* **`RootFindingProblem<P>`**: Interprets `apply(P)` as a residual $F(P)$ that must be driven to zero. Specialized into `ScalarRootFindingProblem<T>` ($T \to T$) and `VectorRootFindingProblem<K>` ($\text{Vector}<K> \to \text{Vector}<K>$).
* **`FixedPointProblem<T>`**: Interprets `apply(T)` as the next step $T_{k+1} = G(T_k)$. It provides a default method `nextIteration(T current)` as a semantic alias for `apply`.
* **`DifferentialEquationProblem<K, V>`**: Models dynamics $V' = F(t, V)$ via its binary method `derivative(V currentState, Real currentTime)`. The state type $V$ is bound to `LinearElement<V, K>`, allowing vector or matrix differential equations.
* **`InitialValueProblem<K, V>` & `BoundaryValueProblem<K, V>`**:supplying initial conditions ($V(t_0) = V_0$) or boundary conditions ($V(t_0), V(t_f)$) respectively.

#### Optional Capability Interfaces
Solvers do not rely on default methods that throw exceptions; instead, they query capabilities at runtime via `instanceof`:
* **`DifferentiableScalarProblem<T>`**: Extends `ScalarRootFindingProblem<T>` by supplying `getDerivative(): Mapping<T, T>`.
* **`DifferentiableVectorProblem<K>`**: Extends `VectorRootFindingProblem<K>` by supplying `getJacobian(): Mapping<Vector<K>, SquareMatrix<K>>`.

#### Problem Adaptors: `MultivariateFunctionSystemProblem`
To bridge scalar multivariate functions with vector solvers, `MultivariateFunctionSystemProblem<K, S>` bundles a list of $n$ `MultivariateFunction<K>` instances into a square system $F(v) = 0$. It implements `DifferentiableVectorProblem<K>` by constructing the Jacobian row-by-row: using exact gradients where `DifferentiableMultivariateFunction` is present, and falling back to finite difference estimations otherwise.

### Solver Contracts (`core.solvers`)
The root solver contract is `Solver<P, R>`, parameterized over the problem type `P` and the result type `R`.

```java
public interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>>
extends Solver<P, R> {
    R solve(P problem, S initialState, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<S> space);
}
```

* **`IterativeSolver`**: Used for root-finding and fixed-point algorithms. Distance measurements (`MetricSpace<S>`) and stopping rules (`ConvergenceCriteria`, `ConvergenceParameters`) are supplied explicitly by the caller.
* **`IntervalSolver` & `IntervalODEStepSolver`**: Designed for differential equations over time intervals. `IntervalSolver` integrates an `InitialValueProblem` over $[t_0, t_f]$, using a `Module<V, K, S>` provided by the caller. `IntervalODEStepSolver` extends this contract to provide discrete time-stepping via `step(...)`.


## Solvers

# Part 4: Numerical Analysis

## The Problem-Solver Model
A `Problem` describes what "solved" means (a residual to zero out, a system's dynamics, or a sequence of iterates) without specifying how to reach that solution. A `Solver` contains no problem-specific data; it consumes a `Problem` along with initial states and execution parameters to iteratively compute a solution.

### Problem Abstractions (`core.problems`)
All numerical problems inherit from `AnalysisProblem<P>`, which acts as a marker interface for the domain:

```text
AnalysisProblem<P>
 ├── IterationProblem<P>
 │    ├── RootFindingProblem<P>
 │    │    ├── ScalarRootFindingProblem<T>
 │    │    └── VectorRootFindingProblem<K>
 │    └── FixedPointProblem<T>
 └── DifferentialEquationProblem<K, V>
      ├── InitialValueProblem<K, V>
      └── BoundaryValueProblem<K, V>
```

* **`IterationProblem<P>`**: It serves as a structural base for any sequence where an element is mapped to another of the same type.
* **`RootFindingProblem<P>`**: Interprets `apply(P)` as a residual $F(P)$ that must be driven to zero. Specialized into `ScalarRootFindingProblem<T>` ($T \to T$) and `VectorRootFindingProblem<K>` ($\text{Vector}<K> \to \text{Vector}<K>$).
* **`FixedPointProblem<T>`**: Interprets `apply(T)` as the next step $T_{k+1} = G(T_k)$. It provides a default method `nextIteration(T current)` as a semantic alias for `apply`.
* **`DifferentialEquationProblem<K, V>`**: Models dynamics $V' = F(t, V)$ via its binary method `derivative(V currentState, Real currentTime)`. The state type $V$ is bound to `LinearElement<V, K>`, allowing vector or matrix differential equations.
* **`InitialValueProblem<K, V>` & `BoundaryValueProblem<K, V>`**:supplying initial conditions ($V(t_0) = V_0$) or boundary conditions ($V(t_0), V(t_f)$) respectively.

#### Optional Capability Interfaces
Solvers do not rely on default methods that throw exceptions; instead, they query capabilities at runtime via `instanceof`:
* **`DifferentiableScalarProblem<T>`**: Extends `ScalarRootFindingProblem<T>` by supplying `getDerivative(): Mapping<T, T>`.
* **`DifferentiableVectorProblem<K>`**: Extends `VectorRootFindingProblem<K>` by supplying `getJacobian(): Mapping<Vector<K>, SquareMatrix<K>>`.

#### Problem Adaptors: `MultivariateFunctionSystemProblem`
To bridge scalar multivariate functions with vector solvers, `MultivariateFunctionSystemProblem<K, S>` bundles a list of $n$ `MultivariateFunction<K>` instances into a square system $F(v) = 0$. It implements `DifferentiableVectorProblem<K>` by constructing the Jacobian row-by-row: using exact gradients where `DifferentiableMultivariateFunction` is present, and falling back to finite difference estimations otherwise.

### Solver Contracts (`core.solvers`)
The root solver contract is `Solver<P, R>`, parameterized over the problem type `P` and the result type `R`.

```java
public interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>>
extends Solver<P, R> {
    R solve(P problem, S initialState, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<S> space);
}
```

* **`IterativeSolver`**: Used for root-finding and fixed-point algorithms. Distance measurements (`MetricSpace<S>`) and stopping rules (`ConvergenceCriteria`, `ConvergenceParameters`) are supplied explicitly by the caller.
* **`IntervalSolver` & `IntervalODEStepSolver`**: Designed for differential equations over time intervals. `IntervalSolver` integrates an `InitialValueProblem` over $[t_0, t_f]$, using a `Module<V, K, S>` provided by the caller. `IntervalODEStepSolver` extends this contract to provide discrete time-stepping via `step(...)`.


## Solvers

### Direct Linear System Solvers (`numerical.solvers.linear`)
Direct solvers compute exact solutions for linear algebraic systems $Ax = b$ in a single deterministic pass without residual convergence loops or metric space evaluations.

* **`GaussianEliminationSolver<K, S>`**: Solves $Ax = b$ directly via Gaussian elimination with partial pivoting followed by back substitution. The computation is exact and single-pass over a `Field`. Reuses `MatrixSpace.toRowEchelonForm()` on the augmented matrix $[A\vert{}b]$ for pivoting, implementing back substitution to extract the solution vector. Throws an `ArithmeticException` if the coefficient matrix is singular.
  ```java
  SquareMatrix<Real> A = SquareMatrixElementFactory.of(R,
      3.0,  2.0, -1.0,
      2.0, -2.0,  4.0,
     -1.0,  0.5, -1.0
  );
  Vector<Real> b = VectorElementFactory.of(V3, 1.0, -2.0, 0.0);

  GaussianEliminationSolver<Real, RealField> solver = new GaussianEliminationSolver<>(R, 3);
  Vector<Real> x = solver.solve(A, b);   // (1.0, -2.0, -2.0)
  ```

### Root-Finding Solvers (`numerical.solvers.roots`)
Root-finding implementations drive residuals to zero over scalar, vector, or polynomial domains:

* **`NewtonRaphsonSolver`**: Solves scalar problems ($T \to T$). It inspects the problem via `instanceof DifferentiableScalarProblem`; if absent, it falls back to a constructor-supplied `CentralDifferenceDifferentiator`.
  ```java
  // One-variable Newton-Raphson
  NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, numericDifferentiator);
  Real root = solver.solve(problem, initialGuess, criteria, params, metricSpace);
  ```

* **`VectorNewtonRaphsonSolver`**: solves $n \times n$ non-linear systems by solving the local Jacobian system directly at each iteration (via `GaussianEliminationSolver`). Same optional-capability pattern as the scalar case: uses `getJacobian()` if the problem implements `DifferentiableVectorProblem<K>`, otherwise falls back to `CentralDifferenceJacobianEstimator` (numeric, one column per extra evaluation of `F`).
  ```java
  // Multi-variable Newton-Raphson
  GaussianEliminationSolver<Real, RealField> linearSolver = new GaussianEliminationSolver<>(R, dimensione);
  VectorSpace<Real, RealField> vectorSpace = new VectorSpace<>(R, dimensione);
  CentralDifferenceJacobianEstimator<Real, RealField> jacobianFallback = new CentralDifferenceJacobianEstimator<>(R, R.of(1e-6));
  VectorNewtonRaphsonSolver<Real, RealField> vectorSolver = new VectorNewtonRaphsonSolver<>(linearSolver, vectorSpace, jacobianFallback);
  Vector<Real> solution = vectorSolver.solve(multivariateProblem, initialGuess, criteria, params, vectorMetricSpace);
  ```

* **`PolynomialRootSolver`**: Computes *all* roots of a polynomial via iterative root finding followed by exact synthetic division (deflation). Full factorizability is guaranteed when operating over algebraically closed fields (e.g., `Complex`).
  ```java
  // All roots of a polynomial (Newton-Raphson + deflation)
  ComplexField C = ComplexField.INSTANCE;
  PolynomialRootSolver<Complex, ComplexField> rootSolver = new PolynomialRootSolver<>(
      C, new Complex(1e-6, 0), metricSpace, new Complex(0.4, 0.9), params);
  List<Complex> roots = rootSolver.findAllRoots(polynomial);
  ```

### Ordinary Differential Equations Solvers (`numerical.solvers.ode`)
Integrators compute time evolution for systems satisfying `InitialValueProblem<K, V>`:

* **`RungeKutta4Solver`**: Fixed-step integration via the classical 4th-order stages ($k_1, k_2, k_3, k_4$). Step direction is derived from start/end times, not from the sign of the step supplied. Requires `Ring & ScalarStructure & NumericFactory`.
  ```java
  // Fixed-step integration
  RungeKutta4Solver<Real, Vector<Real>, RealField> rk4 = new RungeKutta4Solver<>();
  Vector<Real> finalState = rk4.integrate(ivp, endTime, new IntegrationParameters(R.of(0.01)), vectorModule);
  ```

* **`EmbeddedRK23Solver`**: Adaptive-step integration. Uses embedded RK2(3) pairs to estimate local truncation error at each step, rejecting and retrying with a smaller step when the estimate exceeds tolerance, and raising an exception once the required step drops below the configured minimum. Requires `Field & ScalarStructure & NumericFactory` — division is needed to rescale the step.
  ```java
  // Adaptive-step integration
  EmbeddedRK23Solver<Real, Vector<Real>, RealField> rk23 = new EmbeddedRK23Solver<>();
  IntegrationParameters params = new IntegrationParameters(null, R.of(1e-8), R.of(0.1), R.of(1e-6));
  Vector<Real> finalState = rk23.integrate(ivp, endTime, params, vectorModule);
  ```

| Solver | Step Strategy | Required Algebraic Structure | Mechanism |
| :--- | :--- | :--- | :--- |
| `RungeKutta4Solver` | Fixed Step | `Ring & ScalarStructure & NumericFactory` | Classical 4th-order evaluation stages ($k_1, k_2, k_3, k_4$). Step direction is derived from start/end times. |
| `EmbeddedRK23Solver` | Adaptive Step | `Field & ScalarStructure & NumericFactory` | Uses embedded RK2(3) pairs to estimate local truncation error and dynamically resize steps within defined limits. |


### Differentiation & Gradient Adapters (`numerical.functionals.differentiation`)
Differentiation utilities act as higher-order function transformers. They are used independently or injected into iterative solvers as fallback mechanisms.

* **`CentralDifferenceDifferentiator` & `ForwardDifferenceDifferentiator`**: Implement `Mapping<Mapping<K,K>, Mapping<K,K>>` to map a scalar function to its numerical derivative.
  ```java
  // Scalar derivative fallback
  CentralDifferenceDifferentiator<Real> diff = new CentralDifferenceDifferentiator<>(R, R.of(1e-6));
  Mapping<Real, Real> derivative = diff.apply(scalarFunction);
  ```

* **`CentralDifferenceGradientEstimator`**: Computes $\nabla f(v)$ for a `MultivariateFunction<K>` by applying central differences component-by-component across vector dimensions.
  ```java
  // Multivariate gradient estimation
  CentralDifferenceGradientEstimator<Real, RealField> gradEst = new CentralDifferenceGradientEstimator<>(R, R.of(1e-6));
  Vector<Real> gradient = gradEst.estimateAt(multivariateFunction, point);
  ```

* **`CentralDifferenceJacobianEstimator`**: Estimates the full $n \times n$ Jacobian of a `Mapping<Vector<K>, Vector<K>>` one column at a time, perturbing dimension $j$ and evaluating $F$ once yields column $j$ for every row simultaneously.
  ```java
  // Full Jacobian estimation
  CentralDifferenceJacobianEstimator<Real, RealField> jacobianEst = new CentralDifferenceJacobianEstimator<>(R, R.of(1e-6));
  SquareMatrix<Real> jacobian = jacobianEst.estimateAt(problem, point);
  ```