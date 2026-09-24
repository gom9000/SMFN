# SMFN-library 
> **Type:** Java Mathematical & Scientific Library | **Status:**  Continuous Research (Sawdust alert!)

SMFN is an experimental library and a personal exploration of mathematical and scientific modelling. It explores how algebraic structures, generic algorithms, and composable abstractions can be used to represent mathematical concepts and build scientific models in pure Java.

Rather than optimizing for raw performance, SMFN emphasizes mathematical abstraction, explicit structure, and composability across different numeric domains, from polynomial arithmetic and linear algebra to differential equations, quantum mechanics  and geometric visualization.


## Overview
| Domain | Key Capabilities |
| :--- | :--- |
| **Algebra & Arithmetic** | $Semiring \rightarrow Ring \rightarrow Field$ hierarchy, symbolic polynomials, Euclidean division |
| **Linear Algebra** | Generic matrices/vectors over arbitrary rings/fields, structure-based algorithms |
| **Numerical Analysis** | Root-finding, numerical differentiation, ODE integration (RK4), iterative solvers |
| **Applications** | Implicit geometry, Mandelbrot/Julia maps, quantum measurement & dynamics |
| **Graphics** | Decoupled 1D/2D rendering and plotting framework |


## Technical Specifications & Requirements
* **JDK Version:** Java 11+
* **Dependencies:** Zero external dependencies (Pure Java SE)
* **Build System:** Maven


## Quick Start Example

#### Closure under composition:
```java
// determinant() only requires the coefficient domain to provide a Ring.
// Therefore the same matrix algorithm works for Rational, Complex, Polynomial<Complex>, etc.
ComplexField C = ComplexField.INSTANCE;
EuclideanPolynomialRing<Complex, ComplexField> p = new EuclideanPolynomialRing<>(C);
SquareMatrixRing<Polynomial<Complex>, EuclideanPolynomialRing<Complex, ComplexField>> M2 = new SquareMatrixRing<>(p, 2);

// M(x) = [[x+i, 1], [2x, x^2-i]] -- entries are themselves polynomials
SquareMatrix<Polynomial<Complex>> M = SquareMatrixElementFactory.of(p,
    PolynomialElementFactory.of(C, C.of(0, 1), C.of(1, 0)),           // x + i
    PolynomialElementFactory.of(C, C.of(1, 0)),                       // 1
    PolynomialElementFactory.of(C, C.of(0), C.of(2)),                 // 2x
    PolynomialElementFactory.of(C, C.of(0, -1), C.of(0), C.of(1))     // x^2 -i
);

Polynomial<Complex> det = M2.determinant(M);  // x^3 + ix^2 - (2 + i)x + 1
```

#### Operators, not just values:
```java
// A SquareMatrix is also a linear operator: apply(), compose() and power()
// are provided through the Mapping -> Operator -> LinearOperator hierarchy
RealField R = RealField.INSTANCE;
SquareMatrixRing<Real, RealField> M2 = new SquareMatrixRing<>(R, 2);
InnerProductVectorSpace<Real, RealField> space = new InnerProductVectorSpace<>(R, 2);

// A 90-degree rotation matrix
SquareMatrix<Real> rotate90 = SquareMatrixElementFactory.of(R, R.of(0), R.of(-1), R.of(1), R.of(0));
Vector<Real> point = space.of(new Real[]{ R.of(1.0), R.of(0.0) });

Vector<Real> rotatedOnce  = rotate90.apply(point);                   // (0, 1)
Vector<Real> rotatedTwice = rotate90.compose(rotate90).apply(point); // (-1, 0) — same as (rotate90 * rotate90)
Vector<Real> fullCircle   = rotate90.power(4).apply(point);          // (1, 0) — back to start

// Rotation preserves length: the norm is unchanged
Real originalNorm = space.norm(point);       // 1.0
Real rotatedNorm  = space.norm(rotatedOnce); // 1.0
```

#### ... And Solvers, not only symbols:
```java
// Numerical ODE integration: solving differential equation systems over time
RealField R = RealField.INSTANCE;
VectorSpace<Real, RealField> space = new VectorSpace<>(R, 2);

// Harmonic Oscillator: d²/dt² x = -x  ==>  d/dt [x, v] = [v, -x]
InitialValueProblem<Real, Vector<Real>> oscillator = new InitialValueProblem<>() {
    @Override public Vector<Real> derivative(Vector<Real> state, Real time) {
        return space.of(new Real[]{ state.get(1), R.negate(state.get(0)) });
    }
    @Override public Vector<Real> getInitialState() { return space.of(new Real[]{ R.of(1.0), R.of(0.0) }); }
    @Override public Real getStartTime() { return R.of(0.0); }
};

RungeKutta4Solver<Real, Vector<Real>, RealField> solver = new RungeKutta4Solver<>();
Vector<Real> stateAtHalfPi = solver.integrate(oscillator, R.of(Math.PI / 2), new IntegrationParameters(R.of(0.01)), space);

Real position = stateAtHalfPi.get(0); // ~ 0.0 (cos(pi/2))
Real velocity = stateAtHalfPi.get(1); // ~ -1.0 (-sin(pi/2))
```

#### And then:
```java
// Physical simulation: define two observables (2x2 Pauli matrices)
Observable<Complex> sigmaZ = new Observable<>(Pauli.SIGMA_Z);
Observable<Complex> sigmaX = new Observable<>(Pauli.SIGMA_X);

// Prepare the balanced superposition state |+> = (|0> + |1>) / sqrt(2)
double invSqrt2 = 1.0 / Math.sqrt(2);
QuantumState psi = QuantumState.of(new Complex(invSqrt2, 0), new Complex(invSqrt2, 0));

QuantumSystemSimulator simulator = new QuantumSystemSimulator();

// Expectation values: deterministic, no collapse
Real expZ = simulator.expectationValue(sigmaZ, psi);  // 0.0: equal probabilities for +1 and -1 in Z
Real expX = simulator.expectationValue(sigmaX, psi);  // 1.0: |+> is an eigenstate of sigma_x with eigenvalue +1

// Projective measurement: samples an eigenvalue per Born's rule, collapses the state
ConvergenceParameters params = new ConvergenceParameters(new Real(1e-12), 100);
MeasurementOutcome outcome = simulator.performMeasurement(sigmaZ, psi, params, new Random());
Real value = outcome.getValue();                         // +1 or -1, sampled per Born's rule
QuantumState collapsed = outcome.getCollapsedState();    // now an eigenstate of sigma_z

// Measuring again on the collapsed state always gives the same value,
// the state is no longer in a superposition.
Real repeat = simulator.expectationValue(sigmaZ, collapsed);
```

## Documentation & User Guide

### [Foundations](docs/smfn-guide-foundations.md)
- Scope and Philosophy
- Core Modeling Concepts

### [Algebra & Numeric Systems](docs/smfn-guide-algebra.md)
- Element Architecture
- Axiomatic Structure Hierarchy & Factories
- Polynomials
- Functional Mappings & Linear Operators

### [Linear Algebra](docs/smfn-guide-linear-algebra.md)
- Spaces
- Vectors
- Matrices
- Square Matrices

### [Numerical Analysis](docs/smfn-guide-numerical-analysis.md)
- The Problem-Solver Model
- Solvers

### [Graphics Subsystem](docs/smfn-guide-graphics.md)
- Architecture & Decoupling Philosophy
- Core Infrastructure
- Plotting Layer

### [Physical Modelling](docs/smfn-guide-physical-modelling.md)
- General Method of Physical Modelling
- Quantum Mechanics
