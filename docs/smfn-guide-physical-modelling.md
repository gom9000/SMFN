# PPhysical Modelling


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


## Quantum Mechanics
This section presents the formulation of non-relativistic finite-dimensional quantum mechanics, mapping its foundational principles (state spaces, operators, time evolution, and physical measurements) directly onto the core mathematical structures of the library:

- An **observable** is a `SquareMatrix<Complex>` constrained to be Hermitian ($A = A^\dagger$), ensuring real expectation values.
- A **dynamical law** is a `DifferentialEquationProblem<Complex, Vector<Complex>>` representing the time-dependent Schrödinger equation.
- A **measurement** is an inner product operation evaluated on an `InnerProductVectorSpace<Complex, ?>`.

The Pauli matrices ($\sigma_x, \sigma_y, \sigma_z$) and the identity are available as ready-to-use constants in `Pauli`, for building simple two-level Hamiltonians and observables without constructing the matrices by hand.

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


### `QuantumState`
A quantum state carries its own inner-product space fixed once, from the amplitudes' dimension.

```java
QuantumState psi = QuantumState.of(new Complex(1/Math.sqrt(2), 0), new Complex(1/Math.sqrt(2), 0));

Real n = psi.norm();
Complex overlap = psi.innerProduct(other);
QuantumState combined = zero.plus(one).normalize();  // (|0>+|1>)/sqrt(2)
Vector<Complex> raw = psi.asVector();  // escape hatch to raw linear algebra, never mandatory
```

### `Hamiltonian`
The Hamiltonian is the specific `Observable` representing total energy and generating time evolution.

Finding the stationary states of a system, solving $H\vert\psi\rangle = E\vert\psi\rangle$, is directly an eigenvalue problem: the eigenvalues are the energy levels, the eigenvectors the corresponding stationary states.

```java
public final class Hamiltonian extends Observable {
    public Hamiltonian(SquareMatrix<Complex> operator) { super(operator); }

    public EigenDecomposition findStationaryStates(ConvergenceParameters params) {
        return new HermitianEigenvalueSolver().solve(asOperator(), params);
    }
}
```

```java
Hamiltonian H = new Hamiltonian(hamiltonianMatrix);
StationaryStates stationaryStates = H.findStationaryStates(params);
List<Real> energyLevels = stationaryStates.getEnergyLevels();
List<QuantumState> states = stationaryStates.getStates();
```

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

### Time Evolution
bla bla

```java
// Define Hamiltonian and system
Hamiltonian H = new Hamiltonian(hamiltonianMatrix);
SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

QuantumState psi0 = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
QuantumState psiAtT = system.evolve(psi0, endTime, dt);
```

### Quantum Measurements and Expectation Values
The expectation value of an observable $A$ in state $\vert{}\psi\rangle$ is given by:

$$\langle A \rangle = \langle \psi \vert{} A \vert{} \psi \rangle$$

The measurement engine uses the `InnerProductVectorSpace` to compute this inner product.

```java
public final class QuantumSystemSimulator {
    public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
        Vector<Complex> A_psi = observable.asOperator().apply(state);
        Complex expectation = space.innerProduct(state, A_psi);
        
        // Guaranteed real for a Hermitian observable
        return new Real(expectation.getRe());
    }
}
```

Because `Observable` guarantees $A = A^\dagger$, the imaginary component of $\langle \psi | A | \psi \rangle$ is identically zero. Returning a strict `Real` type (rather than a `Complex` with zero imaginary part) enforces this mathematical invariant directly within Java's type system.


### True Quantum Measurement
Unlike `measure()`, which calculates the static expectation value $\langle A \rangle$, `performMeasurement()` simulates projective measurement: it samples a single eigenvalue according to Born's rule $P(\lambda_i) = \vert{}\langle\lambda_i\vert\psi\rangle\vert{}^2$ and collapses the state onto the corresponding eigenvector.

```java
// Projects state onto a random eigenstate based on Born's probability distribution
MeasurementOutcome outcome = simulator.performMeasurement(space, observable, state, params, random);

Real value = outcome.getValue();                // Sampled eigenvalue
Vector<Complex> collapsed = outcome.getState(); // Post-measurement state |lambda_i>
```


### Measurement Probabilities
`performMeasurement()` samples one outcome. `measurementProbabilities()` returns the full Born-rule distribution instead, with no sampling noise:

```java
List<MeasurementProbability> probabilities = simulator.measurementProbabilities(space, observable, state, eigenParams);
for (MeasurementProbability p : probabilities) {
    System.out.println("P(" + p.getValue() + ") = " + p.getProbability());
}
```