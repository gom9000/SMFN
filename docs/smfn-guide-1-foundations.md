# Part 1: Foundations

## Scope and Philosophy
**SMFN** (`net.gommagomma.smfn`) is a Java library created to model algebraic structures, vector spaces, polynomials, and numerical algorithms, and to apply these abstractions to solve simple physics problems.

The library focuses on structural clarity and representing abstract algebraic concepts directly within Java's type system, prioritizing mathematical expressiveness and design readability over computational performance.


## Core Modeling Concepts

### Elements vs Structures
The library separates data entities from the algebraic contexts that govern their behavior:

* **Element (`Element`)**: Represents a single mathematical value or state (e.g., a scalar, a vector, a matrix, or a polynomial). An element is an immutable container and does not perform complex operations on itself in isolation.
* **Structure (`Structure`)**: Represents the algebraic environment or mathematical space (e.g., `Field<T>`, `VectorSpace<V, S>`, `PolynomialRing<T>`). The structure encodes axioms, defines identity elements (zero, unity), and provides the operations to manipulate its elements.

```java
RealField R = RealField.INSTANCE;
Real a = R.of(3.0);
Real b = R.of(4.0);

Real sum = R.add(a, b);
Real prod = R.multiply(a, b);
```

### Values vs. Operations
The separation between values and operations serves to define all and only the operations that are mathematically compatible within a specific domain.

* **Value Objects**: Represent inert mathematical state (scalars, vectors, matrices) detached from operational rules.
* **Domain Operations**: Reside exclusively within governing structures (`Field`, `Ring`, `VectorSpace`) or dedicated operators, guaranteeing that operations strictly adhere to the axioms and constraints of that specific mathematical domain.

### Exact vs. Approximate Domains
The library explicitly categorizes mathematical domains by their computational nature:

| Domain Type | Characteristics | Examples |
| :--- | :--- | :--- |
| **Exact** | Zero truncation error, exact algebraic equality (`==` or `.equals()`), symbolic or exact rational arithmetic. | Natural numbers ($\mathbb{N}$), Integers ($\mathbb{Z}$), Rationals ($\mathbb{Q}$), Modular Arithmetic ($\mathbb{Z}_n$). |
| **Approximate** | IEEE 754 floating-point arithmetic (`double`), subject to rounding errors, equality governed by tolerance $\varepsilon$. | Reals ($\mathbb{R}$ / `Real`), Complex ($\mathbb{C}$ / `Complex`), Numerical Solvers. |

### Capability Modeling
Mathematical structures and elements possess independent, orthogonal properties (e.g., being ordered, having a norm, admitting an inverse, being differentiable). Forcing these into a class hierarchy leads to duplication or fragile abstractions.

Capabilities are implemented as **independent interface traits** applied to elements and structures:

* `Orderable<T>`: Declares ordering relations ($\le, \ge$) and sign checks.
* `Absolutable<T>`: Declares absolute value or magnitude extraction.
* `Normable<T, R>`: Declares norm computation $\Vert{}x\Vert{}$.
* `Sqrtable<T>`: Declares square root operation.
* `Conjugable<T>`: Declares complex conjugation.

Algorithms do not query concrete types; they check for required capability interfaces.

```java
RealField R = RealField.INSTANCE;
ComplexField C = ComplexField.INSTANCE;

Real x = R.of(2.5);
Complex z = C.of(3.0, 4.0);

x.isLessThan(new Real(3.0)); // Orderable
x.abs();                     // Absolutable
x.sqrt();                    // Sqrtable
z.conjugate();               // Conjugable
z.norm();                    // Normable (|z|)
```

### Modeling Mathematical Axioms
The package structure directly reflects the formal hierarchy of abstract algebra:

```
Semiring
  └── Ring
        └──  CommutativeRing
               └── Field
               └── EuclideanDomain
```

1. **Semiring**: Addition and multiplication with associative/distributive laws and identity elements ($0, 1$). No requirement for additive inverses (e.g., Natural numbers $\mathbb{N}$).
2. **Ring**: Adds additive inverses, forming an Abelian group under addition (e.g., Integers $\mathbb{Z}$, Polynomial rings).
3. **Field**: Adds multiplicative inverses for all non-zero elements, forming a commutative division algebra (e.g., Rationals $\mathbb{Q}$, Reals $\mathbb{R}$, Complex numbers $\mathbb{C}$).

### Linear Algebra over Rings vs. Fields
The operational capabilities of matrices and vectors adjust dynamically according to the underlying scalar structure:

* **Matrices over a Semiring** (`SquareMatrixSemiring`, e.g. `Natural`): addition, multiplication, transpose.
* **Matrices over a Ring** (`SquareMatrixRing`, e.g. `SignedInt`, `Polynomial<K>`): adds negation, subtraction, and a determinant via Laplace cofactor expansion.
* **Matrices over a Field** (`SquareMatrixAlgebra`, e.g. `Real`, `Complex`): adds `InvertibleElements` and a faster Gaussian-elimination determinant.


### Mapping/Operator Philosophy & Chaining
The library uses a functional approach for element transformations and domain decoupling:

* **Mapping**: Mapping applies arbitrary transformations $f: T \to U$ across components, structures, or data types. Because Mapping is completely domain-agnostic, it acts as the universal bridge between pure mathematical constructs and downstream consumers, such as projecting mathematical states into screen coordinates for the graphics engine, serialization, or signal processing adapters.
* **Operator Chaining**: Linear operators and algebraic transformations are composable ($O_3 \circ O_2 \circ O_1$), allowing execution pipelines to be assembled before evaluation.


### Computational Workflow: Problem & Solver Model
The library decouples the mathematical formulation of a task from its numerical execution:

* **Problem**: A static definition containing initial conditions, parameters, boundary constraints, or differential equations (e.g., `RootFindingProblem`, `DifferentialEquationProblem`).
* **Solver**: The execution engine (e.g., `RungeKutta4Solver`, `NewtonRaphsonSolver`). A solver accepts a `Problem` and runtime settings (tolerances, max iterations, time step $\Delta t$) to compute the solution.


### Execution Flow
The standard lifecycle in SMFN follows four steps:

1. **Instantiation**: Construct immutable elements or physical initial conditions.
2. **Contextualization**: Bind elements to their governing algebraic structure or vector space.
3. **Execution**: Pass the problem definition to a solver or chain algebraic operators.
4. **Inspection / Visualization**: Extract numerical results, project states, or render outputs.

### Physics Modules as Consumers
Domain-specific packages (es. physics.mq) do not re-implement linear algebra or numerical methods. They act as **clients/consumers** of the core architecture:
* Physical positions and velocities are instances of `Vector<K>` inside a `LinearSpace`.
* Physical forces and field transformations are modeled as `Operator` instances.
* Time evolution relies on generic ODE numerical `Solver` engines.

### Integrated Light-Weight Graphics Engine
The library includes a minimal, zero-dependency visual rendering engine for rapid representation:

* **1D/2D Function Plotting**: Real-time rendering of scalar functions $f(x)$, parametric curves, and spectral signals.
* **Field & Particle Rendering**: Immediate visual feedback for trajectories, vector fields, and implicit geometric functions.

### Immutable Value Objects
Elements types (es. `Complex`, `Polynomial`, `Matrix`) are immutable, ensuring thread safety and mathematical predictability.

### Numerical Tolerance ($\varepsilon$) & `MathConstants`
In approximate domains ($\mathbb{R}$, $\mathbb{C}$), exact floating-point equality (`a == b`) is unreliable due to machine precision limitations.

* **Contextual Tolerance**: All equality checks and singularity detections accept a tolerance parameter $\varepsilon$.
* **`MathConstants`**: Provides system-wide default thresholds ($\varepsilon \approx 10^{-12}$ for double-precision calculations), which can be overridden locally per solver or algorithm instance.

### Domain Invariants & Fail-Fast Policy
SMFN enforces strict **Fail-Fast** behavior for mathematical boundaries:

* **Dimension Mismatch**: Operations between vectors or matrices with incompatible dimensions immediately throw an exception before executing partial computations.
* **Algebraic Singularities**: Inversion of singular matrices or division by zero in non-field structures throws exceptions, preventing the silent propagation of `NaN` or `Infinity`.