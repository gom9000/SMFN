# Physical Modelling


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

- An **observable** is a `SquareMatrix<K>` constrained to be Hermitian ($A = A^\dagger$), ensuring real expectation values.
- A **dynamical law** is a `DifferentialEquationProblem<Complex, Vector<Complex>>` representing the time-dependent Schrödinger equation.
- A **measurement** is an inner product evaluated between a `QuantumState` and the observable's action on it.

The Pauli matrices ($\sigma_x, \sigma_y, \sigma_z$) and the identity are available as ready-to-use constants in `Pauli`, for building simple two-level Hamiltonians and observables without constructing the matrices by hand.


### `QuantumState`
A quantum state carries its own inner-product space fixed once, from the amplitudes' dimension.

```java
QuantumState psi = QuantumState.of(new Complex(1/Math.sqrt(2), 0), new Complex(1/Math.sqrt(2), 0));

Real n = psi.norm();
Complex overlap = psi.innerProduct(other);
QuantumState combined = zero.plus(one).normalize();  // (|0>+|1>)/sqrt(2)
Vector<Complex> raw = psi.asVector();  // escape hatch to raw linear algebra, never mandatory
```


### `Observable`
Physical observables must yield real eigenvalues. Rather than checking for real spectra at measurement time, `Observable` enforces the Hermitian symmetry constraint ($A = A^\dagger$) at construction time using the matrix properties.

```java
public class Observable<K extends ScalarElement<K>> {
    private final SquareMatrix<K> operator;
    public Observable(SquareMatrix<K> operator) {
        if (!operator.isHermitian()) {
            throw new IllegalArgumentException("An Observable must be represented by a Hermitian operator (M = M^dagger).");
        }
        this.operator = operator;
    }
    public SquareMatrix<K> asOperator() { return operator; }

    public static Observable<Complex> toComplex(Observable<Real> observable) { ... }
}
```

By asserting `isHermitian()` upfront, any `Observable` instance structurally guarantees real expectation values across all future calculations.


### `Hamiltonian`
The Hamiltonian is the specific `Observable` representing total energy and generating time evolution.

Finding the stationary states of a system, solving $H\vert\psi\rangle = E\vert\psi\rangle$, is directly an eigenvalue problem: the eigenvalues are the energy levels, the eigenvectors the corresponding stationary states.

```java
public final class Hamiltonian<K extends ScalarElement<K>> extends Observable<K> {
    public Hamiltonian(SquareMatrix<K> operator) { super(operator); }

    public StationaryStates findStationaryStates(ConvergenceParameters params) {
        SolverResult<EigenDecomposition> result = new GeneralEigenvalueSolver<K>().solve(asOperator(), params);
        if (result.getStatus() != ConvergenceStatus.CONVERGED) { throw new IllegalStateException(...); }
        EigenDecomposition decomposition = result.getValue();
        List<Real> energyLevels = decomposition.getRealEigenvalues(params.tolerance);

        List<QuantumState> states = new ArrayList<>(decomposition.getEigenvectors().size());
        for (Vector<Complex> eigenvector : decomposition.getEigenvectors()) {
            states.add(QuantumState.from(eigenvector));
        }

        return new StationaryStates(energyLevels, states);
    }
}
```

```java
Hamiltonian<Complex> H = new Hamiltonian<>(hamiltonianMatrix);
StationaryStates stationaryStates = H.findStationaryStates(params);
List<Real> energyLevels = stationaryStates.getEnergyLevels();
List<QuantumState> states = stationaryStates.getStates();
```

### Dynamics and Time Evolution
The time-dependent Schrödinger equation:

$$\frac{d\vert{}\psi\rangle}{dt} = -i H(t) \vert{}\psi\rangle \quad (\text{with } \hbar = 1)$$

is implemented as a standard `DifferentialEquationProblem<Complex, Vector<Complex>>`. The Hamiltonian is represented as `Mapping<Real, Observable<Complex>>`: a function from time to the operator valid at that instant. A time-independent Hamiltonian is just the special case "$t \to H$, always the same", offered as a separate convenience constructor rather than a distinct class.

```java
public final class SchrodingerEquationSystem
implements DifferentialEquationProblem<Complex, Vector<Complex>> {
    private static final Complex MINUS_I = new Complex(0.0, -1.0);
    private final Mapping<Real, Observable<Complex>> hamiltonian;
    private final VectorSpace<Complex, ComplexField> space;

    /** Time-independent Hamiltonian -- H(t) = hamiltonian for every t. */
    public SchrodingerEquationSystem(Observable<Complex> hamiltonian) {
        this.hamiltonian = time -> hamiltonian;
        this.space = new VectorSpace<>(ComplexField.INSTANCE, hamiltonian.asOperator().getN());
    }

    /** Time-dependent Hamiltonian. Dimension is inferred by evaluating hamiltonian at t=0. */
    public SchrodingerEquationSystem(Mapping<Real, Observable<Complex>> hamiltonian) {
        this.hamiltonian = hamiltonian;
        this.space = new VectorSpace<>(ComplexField.INSTANCE, hamiltonian.apply(new Real(0.0)).asOperator().getN());
    }

    @Override
    public Vector<Complex> derivative(Vector<Complex> state, Real time) {
        Vector<Complex> H_psi = hamiltonian.apply(time).asOperator().apply(state);
        return space.scale(MINUS_I, H_psi);
    }

    public QuantumState evolve(QuantumState initialState, Real endTime, Real dt) { ... }
}
```

Simulating time evolution requires coupling the system with an initial quantum state and propagating it through step-by-step numerical integration:

```java
Hamiltonian<Complex> H = new Hamiltonian<>(hamiltonianMatrix);
SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

QuantumState psi0 = QuantumState.of(new Complex(1, 0), new Complex(0, 0));
QuantumState psiAtT = system.evolve(psi0, endTime, dt);
```


### Measurement Engine (`QuantumSystemSimulator`)

#### Expectation Values
The expectation value of an observable $A$ in state $\vert{}\psi\rangle$ is given by:

$$\langle A \rangle = \langle \psi \vert{} A \vert{} \psi \rangle$$

The measurement engine uses `QuantumState`'s own inner product to compute this.

```java
public final class QuantumSystemSimulator {
    public Real measure(Observable<Complex> observable, QuantumState state) {
        Vector<Complex> A_psi = observable.asOperator().apply(state.asVector());
        Complex expectation = state.innerProduct(QuantumState.from(A_psi));

        // Guaranteed real for a Hermitian observable
        return new Real(expectation.getRe());
    }
}
```

Because `Observable` guarantees $A = A^\dagger$, the imaginary component of $\langle \psi | A | \psi \rangle$ is identically zero, returning a `Real`.


#### Projective Measurement (State Collapse)
`performMeasurement()` simulates projective measurement: it samples a single eigenvalue according to Born's rule $P(\lambda_i) = \vert{}\langle\lambda_i\vert\psi\rangle\vert{}^2$ and collapses the state onto the corresponding eigenvector.

```java
// Projects state onto a random eigenstate based on Born's probability distribution
MeasurementOutcome outcome = simulator.performMeasurement(observable, state, params, random);

Real value = outcome.getValue();                        // Sampled eigenvalue
QuantumState collapsed = outcome.getCollapsedState();   // Post-measurement state |lambda_i>
```


#### Measurement Probabilities
`measurementProbabilities()` returns the full Born-rule theoretical distribution:

```java
List<MeasurementProbability> probabilities = simulator.measurementProbabilities(observable, state, eigenParams);
for (MeasurementProbability p : probabilities) {
    System.out.println("P(" + p.getValue() + ") = " + p.getProbability());
}
```