# Numerical Analysis

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
 ├── DifferentialEquationProblem<K, V>
 │    ├── InitialValueProblem<K, V>
 │    └── BoundaryValueProblem<K, V>
 └── LinearSystemProblem<K>
```

* **`IterationProblem<P>`**: It serves as a structural base for any sequence where an element is mapped to another of the same type.
* **`RootFindingProblem<P>`**: Interprets `apply(P)` as a residual $F(P)$ that must be driven to zero. Specialized into `ScalarRootFindingProblem<T>` ($T \to T$) and `VectorRootFindingProblem<K>` ($\text{Vector}<K> \to \text{Vector}<K>$).
* **`FixedPointProblem<T>`**: Interprets `apply(T)` as the next step $T_{k+1} = G(T_k)$. It provides a default method `nextIteration(T current)` as a semantic alias for `apply`.
* **`DifferentialEquationProblem<K, V>`**: Models dynamics $V' = F(t, V)$ via its binary method `derivative(V currentState, Real currentTime)`. The state type $V$ is bound to `LinearElement<V, K>`, allowing vector or matrix differential equations.
* **`InitialValueProblem<K, V>` & `BoundaryValueProblem<K, V>`**:supplying initial conditions ($V(t_0) = V_0$) or boundary conditions ($V(t_0), V(t_f)$) respectively.
* **`LinearSystemProblem<K>`**: A concrete, immutable value holding the two components of a linear system $Ax = b$: a coefficient matrix `getMatrix(): SquareMatrix<K>` and a right-hand side vector `getRhs(): Vector<K>`. It carries no method that a caller supplies a formula for — every other branch of this hierarchy bundles at least one such method (`apply`, `derivative`) — so it is a plain data class rather than an interface for implementers to specialize.

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
    SolverResult<R> solve(P problem, S initialState, StoppingCriteria criteria, StoppingParameters params, MetricSpace<S> space);
}
```

* **`IterativeSolver`**: Used for root-finding and fixed-point algorithms. Distance measurements (`MetricSpace<S>`) and stopping rules (`StoppingCriteria`, `StoppingParameters`) are supplied explicitly by the caller.
* **`IntervalSolver` & `IntervalODEStepSolver`**: Designed for differential equations over time intervals. `IntervalSolver` integrates an `InitialValueProblem` over $[t_0, t_f]$, using a `Module<V, K, S>` provided by the caller. `IntervalODEStepSolver` extends this contract to provide discrete time-stepping via `step(...)`.

#### `SolverResult<R>`: The Outcome of a Solver's Process
A `SolverResult<R>` is the full outcome produced by a numerical process: the computed value together with metadata describing how the process terminated. It carries three fields, common to every solver in the library:

* **`getValue(): R`**: The value produced by the process — the exact solution when convergence was reached, otherwise the best approximation available at the point of termination.
* **`getStatus(): TerminationStatus`**: The termination outcome, as a value of the `TerminationStatus` enum: `CONVERGED`, `MAX_ITERATIONS_REACHED`, `DIVERGED`, `NUMERICAL_ERROR`. Each value is a neutral description of what happened during the process; it carries no built-in judgment of success or failure. A root-finding process treats `MAX_ITERATIONS_REACHED` as a failed run, while a fixed-point process tracking the escape of an orbit under iteration treats it as its normal, expected outcome for an orbit that never escapes.
* **`getIterationsExecuted(): int`**: The number of iterations the process actually performed before terminating.

Diagnostics that are not common to every solver are exposed as optional capability interfaces, queried at runtime via `instanceof`, following the same pattern used for problem capabilities (`DifferentiableScalarProblem`, `DifferentiableVectorProblem`):

* **`StepDistanceAware`**: Adds `getFinalStepDistance(): Real`, the distance between the last two iterates produced by the process. It is implemented by a `SolverResult` when the process received an external `MetricSpace` over its iteration space, as `IterativeSolver.solve` does.
* **`ResidualAware`**: Adds `getFinalResidual(): Real`, the norm of the equation's residual $\Vert F(x)\Vert$ at the returned value. It is implemented by a `SolverResult` produced for a root-finding process, one whose problem defines an equation to be driven to zero.

Three concrete implementations cover the combinations of these capabilities actually needed by the library's solvers:

* **`BasicSolverResult<R>`**: Implements only the three universal fields. Used by a solver whose process has no external metric space and no equation, such as an eigenvalue solver, whose stopping rule is internal to the algorithm.
* **`IterativeSolverResult<R>`**: Extends `BasicSolverResult<R>` and implements `StepDistanceAware`. Used by a solver whose process receives an external metric space but drives no equation to zero, such as `EscapeTimeSolver`.
* **`RootFindingSolverResult<R>`**: Extends `IterativeSolverResult<R>` and implements `ResidualAware`. Used by a solver for a root-finding process, one that has both a metric space and an equation, such as `NewtonRaphsonSolver` and `VectorNewtonRaphsonSolver`.


## Solvers

### Direct Linear System Solvers (`numerical.solvers.linear`)
Direct solvers compute exact solutions for linear algebraic systems $Ax = b$ in a single deterministic pass without residual convergence loops or metric space evaluations.

* **`LinearSystemSolver<K>`**: Defines the contract `Vector<K> solve(LinearSystemProblem<K> problem)`, satisfying `Solver<LinearSystemProblem<K>, Vector<K>>` with the problem as a single argument. A default method `solve(SquareMatrix<K> matrix, Vector<K> rhs)` wraps the two components into a `LinearSystemProblem<K>` and delegates, for callers that already have the matrix and right-hand side separately.
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

  // equivalently, through the problem type directly
  LinearSystemProblem<Real> problem = new LinearSystemProblem<>(A, b);
  Vector<Real> sameX = solver.solve(problem);
  ```

### Root-Finding Solvers (`numerical.solvers.roots`)
Root-finding implementations drive residuals to zero over scalar, vector, or polynomial domains:

* **`NewtonRaphsonSolver`**: Solves scalar problems ($T \to T$). It inspects the problem via `instanceof DifferentiableScalarProblem`; if absent, it falls back to a constructor-supplied `CentralDifferenceDifferentiator`. Its result is a `RootFindingSolverResult<T>`: a zero derivative encountered mid-process is reported as `TerminationStatus.NUMERICAL_ERROR`, and an exhausted iteration budget as `TerminationStatus.MAX_ITERATIONS_REACHED`, each carrying the last computed approximation as its value.
  ```java
  // One-variable Newton-Raphson
  NewtonRaphsonSolver<Real> solver = new NewtonRaphsonSolver<>(R, numericDifferentiator);
  SolverResult<Real> result = solver.solve(problem, initialGuess, criteria, params, metricSpace);
  Real root = result.getValue();
  Real residual = ((ResidualAware) result).getFinalResidual();
  ```

* **`VectorNewtonRaphsonSolver`**: solves $n \times n$ non-linear systems by solving the local Jacobian system directly at each iteration (via `GaussianEliminationSolver`). Same optional-capability pattern as the scalar case: uses `getJacobian()` if the problem implements `DifferentiableVectorProblem<K>`, otherwise falls back to `CentralDifferenceJacobianEstimator` (numeric, one column per extra evaluation of `F`). Its result is likewise a `RootFindingSolverResult<Vector<K>>`: a singular Jacobian is reported as `TerminationStatus.NUMERICAL_ERROR`, carrying the last iterate reached before the singularity as its value.
  ```java
  // Multi-variable Newton-Raphson
  GaussianEliminationSolver<Real, RealField> linearSolver = new GaussianEliminationSolver<>(R, dimensione);
  VectorSpace<Real, RealField> vectorSpace = new VectorSpace<>(R, dimensione);
  CentralDifferenceJacobianEstimator<Real, RealField> jacobianFallback = new CentralDifferenceJacobianEstimator<>(R, R.of(1e-6));
  VectorNewtonRaphsonSolver<Real, RealField> vectorSolver = new VectorNewtonRaphsonSolver<>(linearSolver, vectorSpace, jacobianFallback);
  SolverResult<Vector<Real>> result = vectorSolver.solve(multivariateProblem, initialGuess, criteria, params, vectorMetricSpace);
  Vector<Real> solution = result.getValue();
  ```

* **`PolynomialRootSolver`**: Computes *all* roots of a polynomial via iterative root finding followed by exact synthetic division (deflation). Full factorizability is guaranteed when operating over algebraically closed fields (e.g., `Complex`). `findAllRoots` checks each intermediate `SolverResult`'s `getStatus()` and throws `IllegalStateException` for any root that did not reach `TerminationStatus.CONVERGED`, so a returned list of roots is always a complete factorization.
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

### Eigenvalue Solvers (`numerical.solvers.eigen`)
Spectral decomposition algorithms compute the eigenvalues and eigenvectors of square matrices $A \in \mathbb{K}^{n \times n}$. 

* **`EigenvalueSolver<K>`**: Defines the base contract `SolverResult<EigenDecomposition> solve(SquareMatrix<K> matrix, StoppingParameters params)`. Every concrete solver implements this contract and carries the `EigenvalueSolver` suffix. Its stopping rule is internal to the algorithm (for the Jacobi family, the norm of the matrix's off-diagonal part), so the contract takes no `MetricSpace`; correspondingly, the `SolverResult<EigenDecomposition>` it returns is a `BasicSolverResult`, implementing neither `StepDistanceAware` nor `ResidualAware` — a spectral decomposition has no equation to drive to zero, and the process defines no external notion of distance between iterates.
* **`EigenDecomposition`**: Represents the spectral result. Eigenvalues and eigenvectors are modeled over `Complex` regardless of the source field $K$, as `Complex` is algebraically closed and handles complex conjugate pairs arising from real non-symmetric matrices. Provides `toRealDecomposition(Real tolerance)` to validate and extract a `RealEigenDecomposition` when imaginary parts fall within tolerance, throwing an exception if non-negligible imaginary components are present.
* **`JacobiEigenvalueSolver`**: Solves real symmetric matrices ($A = A^T$) via classical Jacobi rotations. Uses a numerically stable trigonometric-free formulation (relying solely on square roots and basic arithmetic) to iteratively zero out off-diagonal elements, yielding real eigenvalues and orthogonal eigenvectors simultaneously. An iteration budget exhausted before the off-diagonal norm falls under tolerance is reported as `TerminationStatus.MAX_ITERATIONS_REACHED`, carrying the decomposition assembled from the last completed rotation as its value.
* **`HermitianEigenvalueSolver`**: Extends Jacobi's approach to complex Hermitian matrices ($A = A^\dagger$). Each rotation applies a diagonal unitary phase-absorption step to make the target off-diagonal entry real, followed by a standard Jacobi real rotation. Guarantees real eigenvalues and complex eigenvectors, and reports an exhausted iteration budget the same way `JacobiEigenvalueSolver` does.
* **`GeneralEigenvalueSolver<K>`**: Acts as a smart dispatching wrapper (Facade) implementing `EigenvalueSolver<K>`. It inspects matrix properties at runtime (`isSymmetric()`, `isHermitian()`) to delegate execution to the optimal underlying solver. Throws an `UnsupportedOperationException` for general non-Hermitian matrices until `QREigenvalueSolver` is integrated.

  ```java
  // Spectral decomposition of a real symmetric matrix
  RealField R = RealField.INSTANCE;
  SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, 2.0, 1.0, 1.0, 2.0);
  StoppingParameters params = new StoppingParameters(R.of(1e-12), 100);

  GeneralEigenvalueSolver<Real> solver = new GeneralEigenvalueSolver<>();
  SolverResult<EigenDecomposition> result = solver.solve(A, params);
  EigenDecomposition decomposition = result.getValue();

  RealEigenDecomposition realSpectral = decomposition.toRealDecomposition(R.of(1e-9));
  List<Real> eigenvalues = realSpectral.getEigenvalues();
  List<Vector<Real>> eigenvectors = realSpectral.getEigenvectors();
  ```
  ```java
  // Spectral decomposition of a hermitian matrix
  RealField R = RealField.INSTANCE;
  ComplexField C = ComplexField.INSTANCE;
  SquareMatrix<Complex> H = SquareMatrixElementFactory.of(C, C.of(2, 0), C.of(1, 1), C.of(1, -1), C.of(3, 0));
  StoppingParameters params = new StoppingParameters(R.of(1e-12), 100);

  GeneralEigenvalueSolver<Complex> solver = new GeneralEigenvalueSolver<>();
  SolverResult<EigenDecomposition> result = solver.solve(H, params);
  EigenDecomposition decomposition = result.getValue();

  // eigenvalues are real by construction, eigenvectors are in general genuinely complex
  List<Real> eigenvalues = decomposition.getRealEigenvalues();
  List<Vector<Complex>> eigenvectors = decomposition.getEigenvectors();
  ```


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