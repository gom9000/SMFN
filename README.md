# SMFN eXPerience
------------------------------------------------------------------------    

## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- tensors/                (TensorElement)
|   |   |   |   \-- capabilities/           (Absolutable,Exponentiable,Normable,Sqrtable,Orderable,Differentiable,Integrable)
|   |   |   |-- structures/                 (AdditiveMonoid,MultiplicativeMonoid,ComutativeMultiplicativeMonoid,Semiring,Ring,CommutativeRing,Group, AbelianGroup,Field)
|   |   |   |   \-- capabilities/           (ApproximateStructure,ExactStructure,NumericFactory)
|   |   |-- numerics/                       (Natural, Signedint, ZnElement, Rational, Real, Complex)
|   |   |-- structures/                     (NaturalSemiring, IntegerRing, ZnRing, RationalField, RealField, ComplexField)
|   |   \-- polynomial/
|   |-- linearalgebra                       # Vettori, Matrici e Spazi
|   |   |-- core                   		  # Interfacce per Vettori, Matrici, Spazi
|   |   |   |-- elements/                   (...)
|   |   |   \-- structures/                 (...)
|   |-- geometry/								  # (GeometryEntity, Point, Circle, Ellipse)
|   |-- analysis/                           # Calcolo (Funzioni, Derivate, Integrali, Risolutori Numerici)
|   |   |-- core								  # core interface per functionals, operators, problems, solvers
|   |   |-- functions/						  # LinearFunction ...
|   |   |-- fractals/                       # Contiene implementazioni specifiche di frattali (Mandelbrot, Julia, ...)
|   |   |-- numerical/                      # Contiene implementazioni di metodi numerici
|   |   |   |-- functionals/                # Contiene le implementazioni per il calcolo differenziale/integrale
|   |   |   |-- solvers.ode/                # Contiene le implementazioni per la risoluzione delle ODE
|   |   |   |-- solvers.root/               # Contiene le implementazioni per la ricerca delle roots
|   |   |   \-- ode/                        # Contiene le implementazioni per la risoluzione delle ODE
|   |   \-- integral/                       # (es. Metodi di quadratura numerica)
|   |-- utils                               # Utility e Costanti (MathConstants, MathUtils)
|-- graphics/                               # Logica specifica per la viewport e il rendering
|   |-- core/                               # Interfacce grafiche (Renderer, Viewport, ColorMapper, VieportController)
|   |-- swing/                              # impl (SwingRenderer1D, Swingrnderer2D)
|   \-- plotting/                           # (FunctionPlotter, CartesianAxisPlotter, ScatterPlotter)
|-- physics                                 # Package per le applicazioni fisiche (Elettromagnetismo, MQ, RG)
|   |-- core/                               (Interfacce fisiche base: particella, forza...)
|   |-- mechanics/                          (Dinamica, gravità , cinematica)
|   |-- em                                  # Classi per campi E e B
|   |-- mq                                  # Classi per funzioni d'onda, operatori (HamiltonianOperator, Observable, ...)
|   |   |-- core								 # Observable
|   |   |-- operators/                      # Implementazioni di Posizione, Momento, Momento Angolare
|   |   |-- states/                         # StateVector (normalizzato), QuantumSystem
|   |   |-- dynamics/                       # TimeEvolutionOperator, SchrodingerSolver
|   |   |-- problems/                       # Esempi: ParticleInABox, HarmonicOscillator
|   |   \-- utils/                          # PhysicalConstants, Units
|   |-- relativity                          # Classi per metriche tensoriali
</pre>


# net.gommagomma.smfn.math
## net.gommagomma.smfn.math.algebra
-----------------------------------
### net.gommagomma.smfn.math.algebra.core:
- interface Mapping<I, O> { O apply(I input); default <V> Mapping<V, O> compose(Mapping<? super V, ? extends I> before) {  Objects.requireNonNull(before); return (V v) -> apply(before.apply(v)); } static <T> Mapping<T, T> identity() { return (T t) -> t; } }
- interface Morphism<I, O> extends Mapping<I, O> { O evaluate(I input); @Override default O apply(I input) {  return evaluate(input); }}
- interface Operator<T> extends Mapping<T, T>{    static <T> Operator<T> identity() {   return t -> t;  }   default Operator<T> then(Operator<T> next) { return (T t) -> next.apply(this.apply(t));  }  default Operator<T> power(int n) {        if (n < 0) throw new IllegalArgumentException("Negative power not supported for general operators."); if (n == 0) return identity(); return (T t) -> { T result = t; for (int i = 0; i < n; i++) result = this.apply(result);            return result;   };  }}
- interface LinearOperator<T> extends Operator<T> {}

### net.gommagomma.smfn.math.algebra.core.elements:
- interface AlgebraicElement<E extends AlgebraicElement<E>>{E copy();  }
- interface ScalarElement<K extends ScalarElement<K>> extends AlgebraicElement<K> { ScalarStructure<K> getStructure(); }
- interface ExactElement<K extends ExactElement<K>> extends ScalarElement<K> {}
- interface ApproximateElement<K extends ApproximateElement<K>> extends ScalarElement<K> {}
- interface CompositeElement<K extends ScalarElement<K>, E extends CompositeElement<K, E>> extends AlgebraicElement<E> { ScalarStructure<K> getScalarStructure();}
- interface LinearElement<V extends LinearElement<V, K>, K extends ScalarElement<K>> extends CompositeElement<K, V> {}

### net.gommagomma.smfn.math.algebra.core.elements.tensors:
- interface TensorElement<T extends TensorElement<T, K>, K extends ScalarElement<K>> extends AlgebraicElement<T> {int rank(); int[] getShape(); long size(); K get(int... indices);}

### net.gommagomma.smfn.math.algebra.core.elements.capabilities:
- interface Orderable<E extends Orderable<E>> extends AlgebraicElement<E>, Comparable<E>{ default boolean isLessThan(E other) { return compareTo(other) < 0; }default boolean isGreaterThan(E other) {return compareTo(other) > 0;} }
- interface Sqrtable<E extends AlgebraicElement<E>> { E sqrt(); }
- interface Exponentiable<E extends AlgebraicElement<E>> { E power(int exponent);}
- interface Normable<K extends ScalarElement<K>> {K norm();}
- interface Absolutable<E extends AlgebraicElement<E>> { E abs(); int signum(); }

### net.gommagomma.smfn.math.algebra.core.elements.factories:
- interface NumericFactory<K extends ScalarElement<K>> {K zero(); K one(); K of(double value); K of(long value); K of(int value);}
- interface CompositeElementFactory<E, D> { E of(D data); }

### net.gommagomma.smfn.math.algebra.core.structures:
- interface AlgebraicStructure<E extends AlgebraicElement<E>>{ String getName(); boolean contains(E e); boolean areEqual(E a, E b);}
- interface AdditiveMonoid<E extends AlgebraicElement<E>> extends AlgebraicStructure<E>{E zero();	E add(E a, E b); default boolean isZero(E e) { return areEqual(e, zero()); }}
- interface AdditiveGroup<E extends AlgebraicElement<E>> extends AdditiveMonoid<E> { E negate(E e);	default E subtract(E a, E b) {  return add(a, negate(b));  }}
- interface AbelianGroup<E extends AlgebraicElement<E>> extends AdditiveGroup<E> {}
- interface MultiplicativeMonoid<E extends AlgebraicElement<E>> extends AlgebraicStructure<E> {	E one();  E multiply(E a, E b);  boolean isOne(E element);}
- interface CommutativeMultiplicativeMonoid<E extends AlgebraicElement<E>> extends MultiplicativeMonoid<E> {}
- interface MultiplicativeGroup<E extends AlgebraicElement<E>> extends CommutativeMultiplicativeMonoid<E> {	E inverse(E e);	default E divide(E a, E b) { return multiply(a, inverse(b)); }}
- interface Semiring<E extends AlgebraicElement<E>> extends AdditiveMonoid<E>, MultiplicativeMonoid<E> {}
- interface Ring<E extends AlgebraicElement<E>> extends Semiring<E>, AbelianGroup<E> {}
- interface CommutativeRing<E extends AlgebraicElement<E>> extends Ring<E>, CommutativeMultiplicativeMonoid<E> {}
- interface Field<E extends AlgebraicElement<E>> extends CommutativeRing<E>, MultiplicativeGroup<E> {}
- interface EuclideanDomain<E extends AlgebraicElement<E>, N extends AlgebraicElement<N>> extends CommutativeRing<E> {E quotient(E a, E b); E remainder(E a, E b);	N degree(E e); default E gcd(E a, E b) {} default E lcm(E a, E b) {} default E normalize(E element) {}}

### net.gommagomma.smfn.math.algebra.core.structures.composite:
- interface ScalarStructure<K extends ScalarElement<K>> extends Semiring<K> {Real magnitude(K element); boolean isExact();   }}
- interface CompositeStructure<K extends ScalarElement<K>, E extends CompositeElement<K, E>, S extends ScalarStructure<K>> { S getScalarStructure(); }}
- interface LinearStructure<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends ScalarStructure<K>> extends CompositeStructure<K, V, S> {  V scale(K scalar, V vector);}
- interface Semimodule<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> extends LinearStructure<V, K, S>, AdditiveMonoid<V> {}
- interface Module<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> extends Semimodule<V, K, S>, AbelianGroup<V> {}
- interface LinearSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends Module<V, K, S> {}

### net.gommagomma.smfn.math.algebra.core.structures.metric:
- interface MetricSpace<E>{  Real distance(E a, E b);
- interface NormedSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends LinearSpace<V, K, S>, MetricSpace<V>{Real norm(V v);	@Override  default Real distance(V a, V b) {  V difference = subtract(a, b);  return norm(difference);   }}}
- interface InnerProductSpace<V extends LinearElement<V, K>, K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends NormedSpace<V, K, S>{	K innerProduct(V a, V b);}

### net.gommagomma.smfn.math.algebra.core.structures.capabilities:
- interface ExactStructure<K extends ExactElement<K>> extends ScalarStructure<K> {	@Override default boolean isExact() { return true; } @Override  default boolean areEqual(K a, K b) { if (a == b) return true; if (a == null || b == null) return false;return a.equals(b); }	}
- interface ApproximateStructure<K extends ApproximateElement<K>> extends ScalarStructure<K> {  @Override default boolean isExact() { return false; }  double epsilon();  }}

- interface SymbolicDifferentiationProvider<E extends AlgebraicElement<E>>{  E derivative(E element);}
- interface SymbolicIntegrationProvider<E extends AlgebraicElement<E>, K extends ScalarElement<K>>{ E integrate(E element, K constant);}
- interface EvaluationProvider<E, I, O>{ O evaluate(E element, I input);}

### net.gommagomma.smfn.math.algebra.numerics:
- final class Natural implements ExactElement<Natural>, Orderable<Natural>, Exponentiable<Natural> {}
- final class SignedInt implements ExactElement<SignedInt>, Orderable<SignedInt>, Absolutable<SignedInt>, Exponentiable<SignedInt> {}
- final class Rational implements ExactElement<Rational>, Normable<Real>, Orderable<Rational>, Absolutable<Rational>, Exponentiable<Rational> {}
- final class Real implements ApproximateElement<Real>, Normable<Real>, Orderable<Real>, Absolutable<Real>, Exponentiable<Real>, Sqrtable<Real> {}
- final class Complex implements ApproximateElement<Complex>, Normable<Real>, Exponentiable<Complex>, Sqrtable<Complex> {}
- final class ZnElement implements ExactElement<ZnElement>, Exponentiable<ZnElement> {}

### net.gommagomma.smfn.math.algebra.structures:
- final class NaturalSemiring implements Semiring<Natural>, ExactStructure<Natural>, NumericFactory<Natural> {}
- final class IntegerRing implements EuclideanDomain<SignedInt, Natural>, ExactStructure<SignedInt>, NumericFactory<SignedInt> {}
- final class RationalField implements Field<Rational>, ExactStructure<Rational>, NumericFactory<Rational> {}
- final class RealField implements Field<Real>, ApproximateStructure<Real>, NumericFactory<Real> {}
- final class ComplexField implements Field<Complex>, ApproximateStructure<Complex>, NumericFactory<Complex> {}
- final class ZnRing implements CommutativeRing<ZnElement>, ExactStructure<ZnElement>, NumericFactory<ZnElement> {}

### net.gommagomma.smfn.math.algebra.polynomial:
- final class Polynomial<K extends ScalarElement<K>> implements CompositeElement<K, Polynomial<K>>, ScalarElement<Polynomial<K>> {}
- class PolynomialSemiring<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> implements Semiring<Polynomial<K>>, CompositeStructure<K, Polynomial<K>, S>, ScalarStructure<Polynomial<K>>, CompositeElementFactory<Polynomial<K>, List<K>> {}
- class PolynomialRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> extends PolynomialSemiring<K, S> implements Ring<Polynomial<K>>, SymbolicDifferentiationProvider<Polynomial<K>> {}
- class CommutativePolynomialRing<K extends ScalarElement<K>, S extends CommutativeRing<K> & ScalarStructure<K>> extends PolynomialRing<K, S> implements CommutativeRing<Polynomial<K>> {}
- class EuclideanPolynomialRing<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends CommutativePolynomialRing<K, S> implements EuclideanDomain<Polynomial<K>, Natural>, SymbolicIntegrationProvider<Polynomial<K>, K> {}
- final class PolynomialDivisionResult<K extends ScalarElement<K>> {}
- final class Polynomials {}

## net.gommagomma.smfn.math.linearalgebra
-----------------------------------------
### net.gommagomma.smfn.math.linearalgebra.vectors:
- final class Vector<K extends ScalarElement<K>> implements LinearElement<Vector<K>, K>, TensorElement<Vector<K>, K> { private final ScalarElement<?>[] data; private final LinearStructure<Vector<K>, K, ?> vectorStructure; private final ScalarStructure<K> scalarStructure; private final int size;}
- class VectorSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> implements Semimodule<Vector<K>, K, S>, CompositeElementFactory<Vector<K>, K[]> {}
- class VectorModule<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> extends VectorSemimodule<K, S> implements Module<Vector<K>, K, S> {}
- class VectorSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends VectorModule<K, S> implements LinearSpace<Vector<K>, K, S> {}
- class InnerProductVectorSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends VectorSpace<K, S> implements InnerProductSpace<Vector<K>, K, S> {}

### net.gommagomma.smfn.math.linearalgebra.matrices:
- final class Matrix<K extends ScalarElement<K>> implements LinearElement<Matrix<K>, K>, TensorElement<Matrix<K>, K>{ private final K[] data; private final int rows, cols; protected final LinearStructure<Matrix<K>, K, ?> matrixStructure;   protected final ScalarStructure<K> scalarStructure; }
- class MatrixSemimodule<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> implements Semimodule<Matrix<K>, K, S>, CompositeElementFactory<Vector<K>, K[]> {}
- class MatrixModule<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> extends MatrixSemimodule<K, S> implements Module<Matrix<K>, K, S> {}
- class MatrixSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends MatrixModule<K, S> implements LinearSpace<Matrix<K>, K, S> {}
- class InnerProductMatrixSpace<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends MatrixSpace<K, S> implements InnerProductSpace<Matrix<K>, K, S> {}

### net.gommagomma.smfn.math.linearalgebra.matrices.square:
- final class SquareMatrix<K extends ScalarElement<K>> implements LinearElement<SquareMatrix<K>, K>, ScalarElement<SquareMatrix<K>>, TensorElement<SquareMatrix<K>, K> { private final Matrix<K> internalMatrix; private final ScalarStructure<SquareMatrix<K>> structure;}
- class SquareMatrixSemiring<K extends ScalarElement<K>, S extends Semiring<K> & ScalarStructure<K>> implements Semiring<SquareMatrix<K>>, Semimodule<SquareMatrix<K>, K, S>, ScalarStructure<SquareMatrix<K>>, CompositeElementFactory<SquareMatrix<K>, K[]> {}
- class SquareMatrixRing<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> extends SquareMatrixSemiring<K, S> implements Ring<SquareMatrix<K>>, Module<SquareMatrix<K>, K, S> {}
- class SquareMatrixField<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>> extends SquareMatrixRing<K, S> implements Field<SquareMatrix<K>>, LinearSpace<SquareMatrix<K>, K, S> {}
- final class SquareMatrices {}

### net.gommagomma.smfn.math.linearalgebra.operators:
- class CharacteristicPolynomialMorphism<K extends ScalarElement<K>, S extends Ring<K> & ScalarStructure<K>> implements Morphism<SquareMatrix<K>, Polynomial<K>> {}

// ### net.gommagomma.smfn.math.linearalgebra.core.factories:
//- interface VectorElementFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>{ V createVector(K[] data);  V createVector(double[] data);  V createVector(long[] data);  V createVector(int[] data);  V createZeroVector(int dimension);}
//- interface MatrixElementFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>{ M createMatrix(K[][] data); 	M createMatrix(double[][] data);M createMatrix(long[][] data);	M createMatrix(int[][] data);	M createZeroMatrix(int rows, int cols);}

// ### net.gommagomma.smfn.math.linearalgebra.core.operators:
// - interface LinearMapping<K, V, M extends LinearMapping<K, V, M>> extends Scalable<K, M>, Morphism<V, V> {	default V transform(V vector) {	return apply(vector);	}}
//- interface LinearMorphism<K extends SemiringElement<K>, V extends Scalable<K, V> & CommutativeMonoidElement<V>> extends Morphism<V, V>, CommutativeMonoidElement<LinearMorphism<K, V>>, Scalable<K, LinearMorphism<K, V>> {  }
//- interface LinearOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends LinearOperator<K, V, O>> extends LinearMapping<K, V, O>, LinearCombinable<K, O> {@Override   default V evaluate(V vector) {  return apply(vector); }}
//- interface HermitianMapping<K extends FieldElement<K>, V extends LinearCombinable<K, V>, H extends HermitianMapping<K, V, H>> extends LinearMapping<K, V, H> {Real expectationValue(V state);}
//- interface HermitianOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends HermitianOperator<K, V, O>> extends LinearOperator<K, V, O>, HermitianMapping<K, V, O> {}

## net.gommagomma.smfn.math.geometry
-------------------------------------
- interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> extends Morphism<D, C> {int getAmbientDimension();}
- class Point implements AlgebraicElement<Point> {}
- class Circle implements GeometryEntity<RealVector, Real> {}
- class Ellipse implements GeometryEntity<RealVector, Real> {}

## net.gommagomma.smfn.math.analysis
------------------------------------
### net.gommagomma.smfn.math.analysis.core.functionals:
- interface Functional<K extends FieldElement<K>, D extends AlgebraicElement<D>, C> { K evaluate(Mapping<D, K> f, C context); }

### net.gommagomma.smfn.math.analysis.core.operators:
- interface SymbolicOperator<F extends Mapping<?, ?>, R extends Mapping<?, ?>> extends Operator<F, R> {}
- interface DifferentialOperator<F extends Mapping<?, ?>, R extends Mapping<?, ?>> extends SymbolicOperator<F, R> {}
- interface IntegralOperator<F extends Mapping<?, ?>, R extends Mapping<?, ?>> extends SymbolicOperator<F, R> {}

### net.gommagomma.smfn.math.analysis.core.problems:
- interface AnalysisProblem<P extends AlgebraicElement<P>> {}
- interface DifferentialEquationProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> extends AnalysisProblem<V> { V derivative(V currentState, Real currentTime); }
- interface InitialValueProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> extends DifferentialEquationProblem<K, V> { V getInitialState(); Real getStartTime(); }
- interface BoundaryValueProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> extends DifferentialEquationProblem<K, V> { K getEndTime(); V getBoundaryConditionAtStart(); V getBoundaryConditionAtEnd(); }
- interface FixedPointProblem<T extends AlgebraicElement<T>> extends AnalysisProblem<T> {  T nextIteration(T current); }
- interface ScalarRootFindingProblem<T extends FieldElement<T>> extends AnalysisProblem<T> { Mapping<T, T> getFunction(); }
- interface RootFindingProblem<K extends FieldElement<K>, V extends VectorElement<K, V>> extends AnalysisProblem<V> { Mapping<V, V> getFunction(); }

### net.gommagomma.smfn.math.analysis.core.solvers:
- interface Solver<P, R> {}
- interface IterativeSolver<P, S extends AlgebraicElement<S>, R extends AlgebraicElement<R>> extends Solver<P, R> { R solve(P problem, S initialState, ConvergenceCriteria criteria, ConvergenceParameters params, MetricSpace<S> space); }
- interface IntervalSolver<K extends FieldElement<K>, R extends VectorElement<K, R>> extends Solver<InitialValueProblem<K, R>, R> { R integrate(InitialValueProblem<K, R> problem, Real endTime, IntegrationParameters params); }
- interface IntervalODEStepSolver<K extends FieldElement<K>, T extends VectorElement<K, T>> extends IntervalSolver<K, T> { T step(DifferentialEquationProblem<K, T> system, T currentState, Real currentTime, Real deltaTime); }
- interface ConvergenceCriteria { boolean isConverged(Real distance, ConvergenceParameters params, int iteration); }
- final class ConvergenceParameters { public final Real tolerance;  public final int maxIterations; }
- final class IntegrationParameters { public final Real fixedStepSize; public final Real tolerance; public final Real maxStepSize; public final Real minStepSize; }

### net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation:
- class CentralDifferenceDifferentiator<K extends FieldElement<K>> implements Functional<K, K, K> {}
- class ForwardDifferenceDifferentiator<R extends FieldElement<R>> implements Functional<R, R, R> {}

### net.gommagomma.smfn.math.analysis.numerical.solvers.ode:
- class RungeKutta4Solver<K extends FieldElement<K>, T extends VectorElement<K, T>> implements IntervalODEStepSolver<K, T> {}
- class EmbeddedRK23Solver<K extends FieldElement<K>, T extends VectorElement<K, T> & Normable<Real, T>> implements IntervalODEStepSolver<K, T> {}

### net.gommagomma.smfn.math.analysis.numerical.solvers.roots:
- class NewtonRaphsonSolver<R extends FieldElement<R>> implements IterativeSolver<ScalarRootFindingProblem<R>, R, R> {}

### net.gommagomma.smfn.math.analysis.functions:
- final class LinearFunction<K extends FieldElement<K>> implements CommutativeRingElement<LinearFunction<K>>, Mapping<K, K>  {}

### net.gommagomma.smfn.math.analysis.fractals;
- class MandelbrotSolver implements IterativeSolver<FixedPointProblem<Complex>, Complex, Natural> {}
- class MandelbrotFunction implements Mapping<Complex, Natural> {}
- class JuliaSolver IterativeSolver<FixedPointProblem<Complex>, Complex, Natural> {}
- class JuliaFunction implements Mapping<Complex, Natural> {}

## net.gommagomma.smfn.graphics
-------------------------------
### net.gommagomma.smfn.graphics.core:
- class Viewport {}
- class ViewportController {}
- interface Renderer {}
- interface Renderer1D extends Renderer {}
- interface Renderer2D extends Renderer {}
- interface ColorMapper {Color map(E value);}

### net.gommagomma.smfn.graphics.plotting:
- class FunctionPlotter1D
- class FunctionPlotter2D
- class CartesianAxisPlotter
- class ScatterPlotter

### net.gommagomma.smfn.graphics.drivers.swing:
- class SwingRenderer1D extends Canvas implements Renderer1D
- class SwingRenderer2D extends Canvas implements Renderer2D

## net.gommagomma.smfn.physics
------------------------------
### net.gommagomma.smfn.physics.mq:
- interface Observable<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends Observable<K, V, O>> extends HermitianOperator<K, V, O>{}
- final class HamiltonianOperator implements Observable<Complex, ComplexVector, HamiltonianOperator> {}
- class SchrodingerEquationSystem implements DifferentialSystem<Complex, ComplexVector>{}



# TODO:

- pulizia degli operatori/capabilities sugli operatori fuori da analysis;
- Demo di linearalgebra con elementi ZnRing;
- factory of() per complex/rational;
- raggruppare per interfacce i metodi @Override;

- public final class DerivativeOperator<T extends Differentiable<T>> 
    implements Operator<T> {

    @Override
    public T apply(T input) {
        // Non mi serve sapere se 'input' è un Polinomio o una Matrice.
        // Mi basta sapere che è 'Differentiable'.
        return input.derive();
    }
}

- Per robustezza assoluta in librerie matematiche generiche, si preferisce un "epsilon relativo" (ulps - units in the last place), che adatta la tolleranza alla grandezza dei numeri confrontati.

- Polinomi: Evaluatable<X, X>  - interfaccia chiave che definisca il concetto di "radice" (valutazione)

- ComplexVector: Dot Product
Stai calcolando <v,w>=SOMMA(v(i) x w(i)\). Questa è la convenzione standard dei Matematici (lineare nel primo argomento, antilineare nel secondo).
Attenzione per il package mq (Quantum Mechanics): Nella notazione di Dirac (Fisica), il prodotto scalare (bra-ket <phi|psi> è, per convenzione, antilineare nel primo argomento (bra) e lineare nel secondo (ket): <phi|psi>=SOMMA(phi(i)\ x psi(i))
Se userai questa classe ComplexVector per i tuoi StateVector quantistici, dovrai ricordarti che v.dotProduct(w) calcolerà matematicamente <w|v> (o invertire la logica nella classe HilbertSpace specifica per la MQ).


- Soluzione Architetturale: Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).

- Suggerimento: Vincola l'interfaccia HermitianOperator in modo più stretto, non solo a VectorElement, ma a InnerProductSpaceElement.
// Vincolo più stretto per i problemi di MQ:
public interface HermitianOperator<K extends FieldElement<K, ?> & Normable<Real, K>, 
                                  V extends InnerProductSpaceElement<K, V>, 
                                  O extends HermitianOperator<K, V, O>> 
    extends LinearOperator<K, V, O> {
    Real expectationValue(V state); // Funziona solo se il prodotto scalare è definito
}



- Solvers per mq:
math.analysis.solvers.integral: metodi di quadratura numerici (Regola di Simpson, Regola del Trapezio o Gauss-Legendre)
net.gommagomma.smfn.math.linearalgebra.solvers o estendono AbstractMatrix: 
	Decomposizione LU, Decomposizione QR
	QR Algorithm
math.analysis.solvers.ode: EmbeddedRK23Solver / RKF45Solver / Crank-Nicolson
physics.mq.solvers: Metodo agli Elementi Finiti (FEM) o alle Differenze Finite (FDM)
math.linearalgebra.solvers: Algoritmo di Lanczos o Arnoldi

- Per la Fisica (Simulazione e Animazione)
Avrai bisogno di:
    SimulationPanel: Un pannello che esegue un loop di aggiornamento a tempo fisso (es. 60 FPS).
    PhysicsRenderer: Logica per disegnare gli oggetti fisici (es. la classe Particle dal package smfn.physics.core). DisegnerÃ  cerchi per i corpi, frecce per le forze o i campi elettrici.
    Camera: Logica per gestire la vista, permettendo all'utente di muovere la visuale nello spazio simulato.


public interface AnalysisFunction<I, O> extends Mapping<I, O> {
}

// Specializzazione K -> K
public interface ScalarFunction<K> extends AnalysisFunction<K, K> {
}
public class NewtonRaphsonSolver<K extends ScalarElement<K>> {

    private final Functional<ScalarFunction<K>, K, K> numericalDifferentiator;

    public NewtonRaphsonSolver(Functional<ScalarFunction<K>, K, K> numericalDifferentiator) {
        this.numericalDifferentiator = numericalDifferentiator;
    }

    public K solve(ScalarRootFindingProblem<K> problem, K initialGuess, ConvergenceParameters<K> params) {
        K x = initialGuess;
        ScalarFunction<K> f = problem.getFunction();
        ScalarFunction<K> dfSymbolic = problem.getDerivative();

        for (int i = 0; i < params.getMaxIterations(); i++) {
            K fx = f.apply(x);
            
            // Se fx è abbastanza vicino a zero, abbiamo finito
            if (params.isConverged(fx)) return x;

            // Calcolo della derivata f'(x)
            K dfx;
            if (dfSymbolic != null) {
                // Via Simbolica: valutiamo la funzione derivata nel punto
                dfx = dfSymbolic.apply(x);
            } else {
                // Via Numerica: usiamo il funzionale di fallback (es. Forward Difference)
                dfx = numericalDifferentiator.evaluate(f, x);
            }

            // Newton step: x_{n+1} = x_n - f(x_n) / f'(x_n)
            // Nota: qui serve che K sia in un Field per la divisione
            K step = fx.divide(dfx);
            x = x.subtract(step);
        }
        
        throw new RuntimeException("Raggiunto il numero massimo di iterazioni senza convergenza.");
    }
}
public class DifferentialSymbolicOperator<K, S extends Ring<K>> 
      implements SymbolicOperator<K, K, PolynomialFunction<K, S>> {
    @Override
    public PolynomialFunction<K, S> transform(PolynomialFunction<K, S> f) {
        // Usa la struttura per creare un nuovo elemento algebrico (Polinomio derivato)
        Polynomial<K> derivedPoly = f.getStructure().derivative(f.getPolynomial());
        
        // Restituisce una nuova proiezione analitica con lo stesso evaluator
        return new PolynomialFunction<>(derivedPoly, f.getStructure(), null); 
        // Nota: Qui andrebbe passata la logica per rigenerare l'evaluator corretto
    }
}

- Mapping<I, O>
	|- Morphism<I, O>
	|- Operator<>
    |	|- SymbolicOperator<I, O, M extends Morphism<I, O>
   	|	|	|- DifferentialSymbolicOperator<I, O, M>
   	|	|	|- IntegralSymbolicOperator<I, O, M>
	|	|- Functional<I, K, M>
	|	|	|- EvaluateOperator<I, K, M>
	|	|	|- IntegralOperator<I, K, M>

	