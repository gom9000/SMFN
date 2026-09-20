# SMFN-library 
> **Type:** Java Mathematical & Scientific Library  
> **Status:** Continuous Research & Experimental Modeling

SMFN is a pure Java mathematical and scientific library for mathematical modelling, symbolic computation, and numerical analysis. Rather than optimizing for raw performance, SMFN emphasizes mathematical abstraction, explicit structure, and composability across different numeric domains, ranging from polynomial arithmetic and differential equations to quantum mechanical simulations and geometric visualization.


## Core Features

### Mathematical Foundations & Solvers
* **Abstract Algebraic Architecture:** A structure-oriented algebraic hierarchy ($Semiring → Ring → Field$) with a clear separation between mathematical elements and the structures that define their operations.
* **Symbolic Polynomial Arithmetic:** Symbolic operations over arbitrary scalar fields, supporting dynamic structure promotion to Euclidean Rings (GCD, polynomial division, Horner scheme).
* **Generic Linear Algebra:** Vector spaces and matrices over arbitrary rings and fields, with algorithms selected according to the available algebraic structure (e.g. determinant via Gauss elimination on fields or Laplace expansion on rings).
* **Numerical Solvers & Analysis:** Root-finding algorithms, numerical differentiation, ODE integration, and iterative solvers based on unified state-history models.

### Applications & Domains
* **Implicit Geometry:** Representation of geometric entities (lines, circles, ellipses, planes) as implicit functions with analytically defined gradients and numerical intersection solvers.
* **Fractals & Complex Maps:** Mandelbrot and Julia set exploration and visualization through generic complex-domain operations.
* **Quantum Mechanics Simulations:** Quantum state evolution, Schrodinger equation system integration, and measurement of physical observables.
* **Graphics Subsystem:** Decoupled 1D/2D plotting framework with separate rendering and plotting layers.


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
Real originalNorm = space.norm(point);      // 1.0
Real rotatedNorm  = space.norm(rotatedOnce); // 1.0, exactly
```


## Documentation & User Guide

### [Part 1: Foundations](docs/smfn-guide-1-foundations.md)
- Scope and Philosophy
- Core Modeling Concepts

### [Part 2: Algebra & Numeric Systems](docs/smfn-guide-2-algebra.md)
- Element Architecture
- Axiomatic Structure Hierarchy & Factories
- Polynomials
- Functional Mappings & Linear Operators

### [Part 3: Linear Algebra](docs/smfn-guide-3-linear-algebra.md)
- Spaces
- Vectors
- Matrices
- Square Matrices

### [Part 4: Numerical Analysis](docs/smfn-guide-4-numerical-analysis.md)
- The Problem-Solver Model
- Solvers

### [Part 5: Physical Modelling](docs/smfn-guide-5-physical-modelling.md)
- General Method of Physical Modelling
- Classical Mechanics
- Electromagnetism
- Quantum Mechanics

### [Part 6: Graphics Subsystem](docs/smfn-guide-6-graphics.md)
- Architecture & Decoupling Philosophy
- Core Infrastructure
- Plotting Layer
