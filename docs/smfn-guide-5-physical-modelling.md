# Part 5: Physical Modelling


## General Method of Physical Modelling
There is no dedicated "physical modelling" layer. A physical concept is expressed directly in terms of the library structures.

## Classical Mechanics
todo.

## Electromagnetism
todo.

## Quantum Mechanics
...
- An **observable** is a `SquareMatrix<Complex>` that is required to be Hermitian.
- A **dynamical law** is a `DifferentialEquationProblem` — the same interface any other differential equation in Part 4/5 uses.
- A **measurement** is an inner product — the same `InnerProductVectorSpace` from Part 3.


### `Observable`
```java
public final class Observable {
    public Observable(SquareMatrix<Complex> operator) {
        if (!operator.isHermitian()) {
            throw new IllegalArgumentException("An Observable must be represented by a Hermitian operator (M = M^dagger).");
        }
        this.operator = operator;
    }
    public SquareMatrix<Complex> asOperator() { return operator; }
}
```

`isHermitian()` is the method from Part 3, unmodified. The physics here is entirely in the constructor's check — a non-Hermitian matrix is rejected at the moment it would become an `Observable`, not discovered later when its eigenvalues turn out complex.

### `SchrodingerEquationSystem`
The time-dependent Schrödinger equation, `d|ψ⟩/dt = -i·H|ψ⟩` (natural units, ℏ=1), is implemented against the exact interface from Part 4/5:

```java
public final class SchrodingerEquationSystem implements DifferentialEquationProblem<Complex, Vector<Complex>> {
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

The state's dimension is read from the Hamiltonian itself (`hamiltonian.asOperator().getN()`) rather than taken as a separate constructor argument — there is no way to construct a system whose declared dimension disagrees with its own Hamiltonian's size, because there is no second number to get wrong.

Because this is an ordinary `DifferentialEquationProblem<Complex, Vector<Complex>>`, it integrates with either ODE solver from Part 4/5 without anything quantum-specific in the solver itself:

```java
Observable H = new Observable(hamiltonianMatrix);
SchrodingerEquationSystem system = new SchrodingerEquationSystem(H);

VectorSpace<Complex, ComplexField> space = new VectorSpace<>(ComplexField.INSTANCE, H.asOperator().getN());
InitialValueProblem<Complex, Vector<Complex>> ivp = new InitialValueProblem<Complex, Vector<Complex>>() {
    public Vector<Complex> derivative(Vector<Complex> state, Real time) { return system.derivative(state, time); }
    public Vector<Complex> getInitialState() { return space.of(new Complex[]{ new Complex(1,0), new Complex(0,0) }); }
    public Real getStartTime() { return new Real(0.0); }
};

RungeKutta4Solver<Complex, Vector<Complex>, ComplexField> solver = new RungeKutta4Solver<>();
Vector<Complex> psiAtT = solver.integrate(ivp, endTime, new IntegrationParameters(dt), space);
```

### `QuantumSystemSimulator`
The expectation value of an observable, `⟨ψ|H|ψ⟩`, is computed with nothing beyond `apply` and the Hermitian inner product from Part 3:

```java
public Real measure(InnerProductVectorSpace<Complex, ?> space, Observable observable, Vector<Complex> state) {
    Vector<Complex> H_psi = observable.asOperator().apply(state);
    Complex expectation = space.innerProduct(state, H_psi);
    return new Real(expectation.getRe());   // guaranteed real for a Hermitian observable
}
```

The method returns a `Real`, not a `Complex` truncated to its real part by convention — for a genuinely Hermitian operator the imaginary part of `⟨ψ|H|ψ⟩` is mathematically zero, and the Hermitian constraint enforced back in `Observable`'s constructor is precisely what makes that guarantee hold rather than merely hoped-for.
