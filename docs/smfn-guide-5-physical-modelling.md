# Part 5: Physical Modelling


## General Method of Physical Modelling
Physical systems are expressed directly through the algebraic and numerical abstractions of the library. A physical state is a point in a vector space, a physical law is a differential equation, and a physical observable is an operator with specific algebraic symmetries.

```text
Physical Domain             Library Abstraction                      
---------------------------------------------------------------------
State Space                 Vector Space / Module                    
Physical Dynamics           DifferentialEquationProblem<K, V>        
Conservative Constraint     Algebraic Symmetries (Hermitian, Unitary)
Evolution Engine            IntervalODEStepSolver<K, V, S>          
Physical Measurement        Inner Product / Functional Evaluation    
```

## Classical Mechanics
todo.

## Electromagnetism
todo.

## Quantum Mechanics
This section presents the formulation of non-relativistic finite-dimensional quantum mechanics, mapping its foundational principles (state spaces, operators, time evolution, and physical measurements) directly onto the core mathematical structures of the library:

- An **observable** is a `SquareMatrix<Complex>` constrained to be Hermitian ($A = A^\dagger$), ensuring real expectation values.
- A **dynamical law** is a `DifferentialEquationProblem<Complex, Vector<Complex>>` representing the time-dependent Schrödinger equation.
- A **measurement** is an inner product operation evaluated on an `InnerProductVectorSpace<Complex, ?>`.


### `Observable`
Physical observables must yield real eigenvalues. Rather than checking for real spectra at measurement time, `Observable` enforces the Hermitian symmetry constraint ($A = A^\dagger$) at construction time using the matrix properties.

```java
public final class Observable {
    private final SquareMatrix<Complex> operator;
    public Observable(SquareMatrix<Complex> operator) {
        if (!operator.isHermitian()) {
            throw new IllegalArgumentException("An Observable must be represented by a Hermitian operator (M = M^dagger).");
        }
        this.operator = operator;
    }
    public SquareMatrix<Complex> asOperator() { return operator; }
}
```

By asserting `isHermitian()` upfront, any `Observable` instance structurally guarantees real expectation values across all future calculations.


### Time-Dependent Schrödinger Dynamics
The time-dependent Schrödinger equation:

$$\frac{d\vert{}\psi\rangle}{dt} = -i H \vert{}\psi\rangle \quad (\text{with } \hbar = 1)$$

is implemented as a standard `DifferentialEquationProblem<Complex, Vector<Complex>>`. The spatial dimension of the state vector is inferred directly from the Hamiltonian's degree $n$, eliminating any possibility of dimension mismatch.

```java
public final class SchrodingerEquationSystem
implements DifferentialEquationProblem<Complex, Vector<Complex>> {
    private static final Complex MINUS_I = new Complex(0.0, -1.0);
    private final Observable hamiltonian;
    private final VectorSpace<Complex, ComplexField> space;

    public SchrodingerEquationSystem(Observable hamiltonian) {
        this.hamiltonian = hamiltonian;
        this.space = new VectorSpace<>(ComplexField.INSTANCE, hamiltonian.asOperator().getN());
    }

    @Override
    public Vector<Complex> derivative(Vector<Complex> state, Real time) {
        Vector<Complex> H_psi = hamiltonian.asOperator().apply(state);
        return space.scale(MINUS_I, H_psi);
    }
}
```

### Execution via Generic ODE Solvers
Because `SchrodingerEquationSystem` implements `DifferentialEquationProblem`, it integrates seamlessly with the numerical solvers:

```java
// Define Hamiltonian and system
Observable H = new Observable(hamiltonianMatrix);
SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

// Wrap in Initial Value Problem
VectorSpace<Complex, ComplexField> space = new VectorSpace<>(ComplexField.INSTANCE, H.asOperator().getN());
InitialValueProblem<Complex, Vector<Complex>> ivp = new InitialValueProblem<Complex, Vector<Complex>>() {
    public Vector<Complex> derivative(Vector<Complex> state, Real time) { return system.derivative(state, time); }
    public Vector<Complex> getInitialState() { return space.of(new Complex[]{ new Complex(1,0), new Complex(0,0) }); }
    public Real getStartTime() { return new Real(0.0); }
};

// Integrate using standard RK4 solver
RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> solver = new RungeKutta4Solver<>();
Vector<Complex> psiAtT = solver.integrate(ivp, endTime, new IntegrationParameters(dt), space);
```

### Quantum Measurements and Expectation Values
The expectation value of an observable $A$ in state $\vert{}\psi\rangle$ is given by:

$$\langle A \rangle = \langle \psi \vert{} A \vert{} \psi \rangle$$

The measurement engine uses the `InnerProductVectorSpace` to compute this inner product.

```java
public class QuantumSystemSimulator {
    public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
        Vector<Complex> A_psi = observable.asOperator().apply(state);
        Complex expectation = space.innerProduct(state, A_psi);
        
        // Guaranteed real for a Hermitian observable
        return new Real(expectation.getRe());
    }
}
```

Because `Observable` guarantees $A = A^\dagger$, the imaginary component of $\langle \psi | A | \psi \rangle$ is identically zero. Returning a strict `Real` type (rather than a `Complex` with zero imaginary part) enforces this mathematical invariant directly within Java's type system.
