# SMFN eXPerience
------------------------------------------------------------------------    

## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/                           (AlgebraicElement, AlgebraicStructure, Commutative, NumericFactory, MathFunction, Mapping, FunctionElement)
|   |   |   |-- algorithms                  (AlgebraicAlgorithms)
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- additive/               (...)
|   |   |   |   |-- multiplicative/         (...)
|   |   |   |   |-- tensors/                (TensorElement)
|   |   |   |   \-- capabilities/           (Absolutable,Exponentiable,Normable,Sqrtable,Orderable)
|   |   |   |-- structures/                 (AdditiveMonoid,MultiplicativeMonoid,ComutativeMultiplicativeMonoid,Semiring,Ring,CommutativeRing,Group, AbelianGroup,Field)
|   |   |-- numeric/                        (Natural, Signedint, ZnElement, Rational, Real, Complex)
|   |   |-- polynomial/
|   |   \-- structures/                     (NaturalSemiring, IntegerRing, ZnRing, RationalField, RealField, ComplexField, PolynomialRing)
|   |-- linearalgebra                       # Vettori, Matrici e Spazi
|   |   |-- core                   		  # Interfacce per Vettori, Matrici, Spazi
|   |   |   |-- elements/                   (...)
|   |   |   \-- structures/                 (...)
|   |   |-- complex                         # Implementazioni per C (ComplexVector, ComplexMatrix, relativi spaces)
|   |   |-- natural                         # Implementazioni per N (NaturalVector, NaturalMatrix, relativi spaces)
|   |   |-- rational                        # Implementazioni per Q (RationalVector, RationalMatrix, relativi spaces)
|   |   |-- real                            # Implementazioni per R (RealVector, RealMatrix, relativi spaces)
|   |   \-- signedint                       # Implementazioni per Z (SignedIntVector, SignedIntMatrix, relativi spaces)
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
- interface Commutative {}
- interface AlgebraicElement<E extends AlgebraicElement<E>> { boolean isMathematicallyEqualTo(E other); E copy();}
- interface AlgebraicStructure<E extends AlgebraicElement<E>> { String getName(); boolean contains(E e); }
- interface NumericFactory<E extends SemiringElement<E>> {E zero(); E one(); E of(double value); E of(long value); E of(int value);}
- interface Mapping<I, O> { O apply(I input); default <V> Mapping<V, O> compose(Mapping<? super V, ? extends I> before) {  Objects.requireNonNull(before); return (V v) -> apply(before.apply(v)); } static <T> Mapping<T, T> identity() { return (T t) -> t; } }
- interface Morphism<I, O> extends Mapping<I, O> { O evaluate(I input); }

### net.gommagomma.smfn.math.algebra.core.algorithms:
- AlgebraicAlgorithms { public static <E extends EuclideanDomainElement<E, N>, N extends ComparableElement<N>> E gcd(E a, E b){} public static <E extends EuclideanDomainElement<E, N>, N extends ComparableElement<N>> E lcm(E a, E b) {} }

### net.gommagomma.smfn.math.algebra.core.elements.additive:
- interface AdditiveMonoidElement<E extends AdditiveMonoidElement<E>> extends AlgebraicElement<E> { E add(E other); E getZero(); default boolean isZero() { return isMathematicallyEqualTo(getZero()); }}
- interface CommutativeMonoidElement<E extends CommutativeMonoidElement<E>> extends AdditiveMonoidElement<E>, Commutative {}
- interface GroupElement<E extends GroupElement<E>> extends AdditiveMonoidElement<E> {E negate(); default E subtract(E other) {return add(other.negate());}}
- interface AbelianGroupElement<E extends AbelianGroupElement<E>> extends GroupElement<E>, CommutativeMonoidElement<E> {}

### net.gommagomma.smfn.math.algebra.core.elements.multiplicative:
- interface MultiplicativeMonoidElement<E extends MultiplicativeMonoidElement<E>> extends AlgebraicElement<E> { E multiply(E other); E getOne(); default boolean isOne() { return isMathematicallyEqualTo(getOne()); }}
- interface CommutativeMultiplicativeMonoidElement<T extends CommutativeMultiplicativeMonoidElement<T>> extends MultiplicativeMonoidElement<T>, Commutative {}
- interface SemiringElement<E extends SemiringElement<E>> extends CommutativeMonoidElement<E>, MultiplicativeMonoidElement<E> {}
- interface RingElement<E extends RingElement<E>> extends SemiringElement<E>, AbelianGroupElement<E> {}
- interface CommutativeRingElement<E extends CommutativeRingElement<E>> extends RingElement<E>, CommutativeMultiplicativeMonoidElement<E> {}
- interface FieldElement<E extends FieldElement<E>> extends CommutativeRingElement<E>, Scalable<E, E> {E inverse();default E divide(E other) { return multiply(other.inverse());} @Override  default E scale(E scalar) {        return this.multiply(scalar);  }}
- interface EuclideanDomainElement<E extends EuclideanDomainElement<E, N>, N extends Orderable<N>> extends CommutativeRingElement<E> {N normValue(); E remainder(E divisor); E quotient(E divisor); default E mod(E divisor) { return remainder(divisor); }}

### net.gommagomma.smfn.math.algebra.core.elements.tensors:
- interface TensorElement<K extends SemiringElement<K>> {int rank(); int[] getShape(); long size(); K get(int... indices);}

### net.gommagomma.smfn.math.algebra.core.elements.capabilities:
- interface Orderable<E extends Orderable<E>> extends AlgebraicElement<E>, Comparable<E>{ default boolean isLessThan(E other) { return compareTo(other) < 0; }default boolean isGreaterThan(E other) {return compareTo(other) > 0;} }
- interface Sqrtable<E extends Sqrtable<E>> extends AlgebraicElement<E> { E sqrt(); }
- interface Exponentiable<E extends Exponentiable<E>> extends AlgebraicElement<E> { E power(int exponent);}
- interface Normable<N extends FieldElement<N>, E extends Normable<N, E>>  extends AlgebraicElement<E> {N norm();}
- interface Differentiable<T extends AlgebraicElement<T>> {  T derivative(); }
- interface Absolutable<E extends Absolutable<E>> extends Orderable<E>, AbelianGroupElement<E> { default E abs() { return this.isLessThan(getZero()) ? this.negate() : (E) this;}	default int signum() {	if (this.isZero()) return 0; return this.isGreaterThan(getZero()) ? 1 : -1;	}}
- interface LinearCombinable<K, E extends LinearCombinable<K, E>> extends Scalable<K, E>, AbelianGroupElement<E>{	default E linearCombine(K a, E other, K b) {  return this.scale(a).add(other.scale(b));  }}
- interface Scalable<K, E> {    E scale(K scalar);}

### net.gommagomma.smfn.math.algebra.core.structures:
- interface AdditiveMonoid<E extends AdditiveMonoidElement<E>> extends AlgebraicStructure<E>{ E additiveIdentity(); }}
- interface MultiplicativeMonoid<E extends MultiplicativeMonoidElement<E>> extends AlgebraicStructure<E> {  E multiplicativeIdentity();}
- interface CommutativeMultiplicativeMonoid<E extends CommutativeMultiplicativeMonoidElement<E>> extends MultiplicativeMonoid<E> {}
- interface Group<E extends GroupElement<E>> extends AdditiveMonoid<E> {}
- interface AbelianGroup<E extends AbelianGroupElement<E>> extends Group<E> {}
- interface Semiring<E extends SemiringElement<E>> extends AdditiveMonoid<E>, MultiplicativeMonoid<E>, NumericFactory<E>{ default E zero() { return additiveIdentity(); } default E one() { return multiplicativeIdentity(); } }
- interface Ring<E extends RingElement<E>> extends Semiring<E>, AbelianGroup<E> {}
- interface CommutativeRing<E extends CommutativeRingElement<E>> extends Ring<E>, CommutativeMultiplicativeMonoid<E> {}
- interface EuclideanDomain<E extends EuclideanDomainElement<E, N>, N extends Orderable<N>> extends CommutativeRing<E> { E quotient(E a, E b); E remainder(E a, E b);}
- interface Field<E extends FieldElement<E>> extends CommutativeRing<E> {}

### net.gommagomma.smfn.math.algebra.core.structures.capabilities:
- interface HasScalarStructure<K extends SemiringElement<K>>{ Semiring<K> getScalarStructure();}

### net.gommagomma.smfn.math.algebra.numeric:
- final class Natural implements SemiringElement<Natural>, Exponentiable<Natural>, Orderable<Natural> {}
- final class SignedInt implements CommutativeRingElement<SignedInt>, Exponentiable<SignedInt>, Absolutable<SignedInt>, EuclideanDomainElement<SignedInt, Natural> { /* ... */ }
- final class Rational implements FieldElement<Rational>, Normable<Real, Rational>, Exponentiable<Rational>, Absolutable<Rational> {}
- final class Real implements FieldElement<Real>, Normable<Real, Real>, Exponentiable<Real>, Sqrtable<Real>, Absolutable<Real> {}
- final class Complex implements FieldElement<Complex>, Normable<Real, Complex>, Exponentiable<Complex>, Sqrtable<Complex> {}
- final class ZnElement implements CommutativeRingElement<ZnElement> {}

### net.gommagomma.smfn.math.algebra.structures:
- final class NaturalSemiring implements Semiring<Natural> {}
- final class IntegerRing implements EuclideanDomain<SignedInt, Natural> {}
- final class RationalField implements Field<Rational> {}
- final class RealField implements Field<Real> {}
- final class ComplexField implements Field<Complex> {}
- final class ZnRing implements CommutativeRing<ZnElement> {}

### net.gommagomma.smfn.math.algebra.polynomial:
- abstract class AbstractPolynomial<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>> implements SemiringElement<P>, Morphism<K, K> {}
- abstract class AbstractRingPolynomial<K extends RingElement<K>, P extends AbstractRingPolynomial<K, P>> 
extends AbstractPolynomial<K, P> 
implements RingElement<P> {}
- final class SemiringPolynomial<K extends SemiringElement<K>> extends AbstractPolynomial<K, SemiringPolynomial<K>> {}
- final class GeneralPolynomial<K extends RingElement<K>> extends AbstractRingPolynomial<K, GeneralPolynomial<K>> {}
- final class CommutativePolynomial<K extends CommutativeRingElement<K>> extends AbstractRingPolynomial<K, CommutativePolynomial<K>> implements CommutativeRingElement<CommutativePolynomial<K>> {}
- final class EuclideanPolynomial<K extends FieldElement<K>> extends AbstractRingPolynomial<K, EuclideanPolynomial<K>>
implements EuclideanDomainElement<EuclideanPolynomial<K>, Natural> {}
- final class PolynomialQuotientRemainder<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>> {}
- final class Polynomials {}

- abstract class AbstractPolynomialRing<K extends SemiringElement<K>, P extends AbstractPolynomial<K, P>> implements HasScalarStructure<K> {}
- public final class SemiringPolynomialRing<K extends SemiringElement<K>> extends AbstractPolynomialRing<K, SemiringPolynomial<K>> implements Semiring<SemiringPolynomial<K>> {}
- final class GeneralPolynomialRing<K extends RingElement<K>> extends AbstractPolynomialRing<K, GeneralPolynomial<K>> implements Ring<GeneralPolynomial<K>> {}
- final class CommutativePolynomialRing<K extends CommutativeRingElement<K>> extends AbstractPolynomialRing<K, CommutativePolynomial<K>> implements CommutativeRing<CommutativePolynomial<K>> {}
- final class EuclideanPolynomialRing<K extends FieldElement<K>> extends AbstractPolynomialRing<K, EuclideanPolynomial<K>>
implements EuclideanDomain<EuclideanPolynomial<K>, Natural> {}

## net.gommagomma.smfn.math.linearalgebra
-----------------------------------------
### net.gommagomma.smfn.math.linearalgebra.core.elements.vectors:
- interface SpaceElement<V extends SpaceElement<V>> extends AlgebraicElement<V>{}
- interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends SpaceElement<V>, CommutativeMonoidElement<V>, Scalable<K, V>, TensorElement<K> {int dimension(); K get(int index); int dimension();}
- interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> extends SemimoduleElement<K, V>, LinearCombinable<K, V> {}
- interface VectorElement<K extends FieldElement<K>, V extends VectorElement<K, V>> extends ModuleElement<K, V> {}
- interface NormedVectorElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends NormedVectorElement<K, V>> extends VectorElement<K, V>, NormableElement<Real, V>{  default Real distanceTo(V other) {}}
- interface InnerProductSpaceElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends NormedVectorElement<K, V> {K dotProduct(V other);}
- public abstract class AbstractRank1Tensor<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> implements SemimoduleElement<K, V> {}

### net.gommagomma.smfn.math.linearalgebra.core.elements.matrices:
- interface SemiringMatrixElement<K extends SemiringElement<K>,V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends SemiringElement<M>, LinearMapping<K, V, M>, Scalable<K, M> { int getRows(); int getColumns(); K get(int row, int col); V getRowVector(int row); V getColumnVector(int col);  M multiply(M other); V multiply(V vector);M transpose();}
- interface RingMatrixElement<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixElement<K, V, M>, RingElement<M> {}
- interface FieldMatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>> extends RingMatrixElement<K, V, M>, LinearOperator<K, V, M> { K determinant(); M inverse(); }
- abstract class AbstractSemiringMatrix<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>, S extends SemiringMatrixSemimodule<K, V, M>> implements SemiringMatrixElement<K, V, M>, TensorElement<K> {    protected final K[][] data; protected final int rows;  protected final int cols;  protected final S structure;}
- abstract class AbstractRingMatrix<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>, S extends RingMatrixModule<K, V, M>> extends AbstractSemiringMatrix<K, V, M, S> implements RingMatrixElement<K, V, M> {}
- abstract class AbstractFieldMatrix<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>, S extends FieldMatrixSpace<K, V, M>> extends AbstractRingMatrix<K, V, M, S> implements FieldMatrixElement<K, V, M> {}

### net.gommagomma.smfn.math.linearalgebra.core.factories:
- interface VectorElementFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>>{ V createVector(K[] data);  V createVector(double[] data);  V createVector(long[] data);  V createVector(int[] data);  V createZeroVector(int dimension);}
- interface MatrixElementFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>{ M createMatrix(K[][] data); 	M createMatrix(double[][] data);M createMatrix(long[][] data);	M createMatrix(int[][] data);	M createZeroMatrix(int rows, int cols);}

### net.gommagomma.smfn.math.linearalgebra.core.structures.spaces:
- interface LinearSpace<K extends SemiringElement<K>, V extends AlgebraicElement<V>> extends AlgebraicStructure<V> { Semiring<K> getScalarStructure(); }
- interface MetricSpace<T extends AlgebraicElement<T>> extends LinearSpace<T> {Real distance(T point1, T point2);}
- interface Semimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends LinearSpace<V>, VectorElementFactory<K, V> {}
- interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> extends Semimodule<K, V> {Ring<K> getScalarStructure(); }
- interface VectorSpace<K extends FieldElement<K>, V extends VectorElement<K, V>> extends Module<K, V> {Field<K> getScalarStructure();}
- class RealMetricSpace implements MetricSpace<Real, Real> {}
- interface InnerProductSpace<K extends FieldElement<K> & Normable<Real, K>, V extends InnerProductSpaceElement<K, V>> extends VectorSpace<K, V>, MetricSpace<V> {default K innerProduct(V v1, V v2) {return v1.dotProduct(v2);} @Override   default Real distance(V point1, V point2) { return point1.distanceTo(point2); }}
- interface HilbertSpace<K extends FieldElement<K> & Normable<Real, K>, V extends InnerProductSpaceElement<K, V>> extends InnerProductSpace<K, V> {}

- interface SemiringMatrixSemimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends LinearSpace<M>, SemiringMatrixFactory<K, V, M>, DimensionalStructure<SemiringMatrixSemimodule<K, V, M>> {Semiring<K> getScalarStructure();	Semimodule<K, V> getVectorStructure(); int getMatrixRows(); int getMatrixColumns();}
- interface RingMatrixModule<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixSemimodule<K, V, M> { @Override Ring<K> getScalarStructure(); @Override Module<K, V> getVectorStructure();}
- interface FieldMatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixModule<K, V, M> {@Override Field<K> getScalarStructure(); @Override VectorSpace<K, V> getVectorStructure();}
- interface DimensionalStructure<S extends AlgebraicStructure<?>> {S getSpaceOfDimensions(int rows, int cols);}

### net.gommagomma.smfn.math.linearalgebra.core.operators:
- interface LinearMapping<K, V, M extends LinearMapping<K, V, M>> extends Scalable<K, M>, Morphism<V, V> {	default V transform(V vector) {	return apply(vector);	}}
- interface LinearMorphism<K extends SemiringElement<K>, V extends Scalable<K, V> & CommutativeMonoidElement<V>> extends Morphism<V, V>, CommutativeMonoidElement<LinearMorphism<K, V>>, Scalable<K, LinearMorphism<K, V>> {  }
- interface LinearOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends LinearOperator<K, V, O>> extends LinearMapping<K, V, O>, LinearCombinable<K, O> {@Override   default V evaluate(V vector) {  return apply(vector); }}
- interface HermitianMapping<K extends FieldElement<K>, V extends LinearCombinable<K, V>, H extends HermitianMapping<K, V, H>> extends LinearMapping<K, V, H> {Real expectationValue(V state);}
- interface HermitianOperator<K extends FieldElement<K>, V extends LinearCombinable<K, V>, O extends HermitianOperator<K, V, O>> extends LinearOperator<K, V, O>, HermitianMapping<K, V, O> {}

### net.gommagomma.smfn.math.linearalgebra.natural:
- final class NaturalVector extends AbstractRank1Tensor<Natural, NaturalVector> { //... }
- final class NaturalSemimodule implements Semimodule<Natural, NaturalVector> {}
- final class NaturalMatrix extends AbstractSemiringMatrix<Natural, NaturalVector, NaturalMatrix, NaturalMatrixSemimodule> {}
- final class NaturalMatrixSemimodule implements SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix> {}

###net.gommagomma.smfn.math.linearalgebra.signedint:
- final class SignedIntVector extends AbstractRank1Tensor<SignedInt, SignedIntVector> implements ModuleElement<SignedInt, SignedIntVector> {//...}
- final class SignedIntModule implements Module<SignedInt, SignedIntVector> {}
- final class SignedIntMatrix extends AbstractRingMatrix<SignedInt, SignedIntVector, SignedIntMatrix, SignedIntMatrixModule> {}
- final class SignedIntMatrixModule implements RingMatrixModule<SignedInt, SignedIntVector, SignedIntMatrix> {}

### net.gommagomma.smfn.math.linearalgebra.real:
- final class RealVector extends AbstractRank1Tensor<Real, RealVector> implements InnerProductSpaceElement<Real, RealVector> {//...}
- final class RealVectorSpace implements HilbertSpace<Real, RealVector> {//...}
- final class RealMatrix extends AbstractFieldMatrix<Real, RealVector, RealMatrix, RealMatrixSpace> {//...}
- final class RealMatrixSpace implements FieldMatrixSpace<Real, RealVector, RealMatrix> {//...}

### net.gommagomma.smfn.math.linearalgebra.complex:
- final class ComplexVector extends AbstractRank1Tensor<Complex, ComplexVector> implements InnerProductSpaceElement<Complex, ComplexVector> { //... }
- final class ComplexVectorSpace implements HilbertSpace<Complex, ComplexVector> {//...}
- final class ComplexMatrix extends AbstractFieldMatrix<Complex, ComplexVector, ComplexMatrix, ComplexMatrixSpace> {}
- final class ComplexMatrixSpace implements FieldMatrixSpace<Complex, ComplexVector, ComplexMatrix> {//...}

### net.gommagomma.smfn.math.linearalgebra.rational:
- final class RationalVector extends AbstractRank1Tensor<Rational, RationalVector> implements InnerProductSpaceElement<Rational, RationalVector> { /... }
- final class RationalVectorSpace implements VectorSpace<Rational, RationalVector> {//...}
- final class RationalMatrix implements AbstractFieldMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixSpace> {//...}
- final class RationalMatrixSpace implements FieldMatrixSpace<Rational, RationalVector, RationalMatrix> {//...}

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

// Marker per atomi numerici
interface ScalarElement extends AlgebraicElement {} 

// Marker per dati esatti (Natural, SignedInt)
interface ExactElement extends ScalarElement {} 

// Marker per dati approssimati (Real, Complex)
interface ApproximateElement extends ScalarElement {}

// Marker per TensorElement (che hai già)
interface StructuredElement<K extends SemiringElement<K>> extends AlgebraicElement {
    Semiring<K> getScalarStructure();
}





- introduzione delle matrici quadrate (come anello moltiplicativo);

- Per robustezza assoluta in librerie matematiche generiche, si preferisce un "epsilon relativo" (ulps - units in the last place), che adatta la tolleranza alla grandezza dei numeri confrontati.

- Polinomi: Evaluatable<X, X>  - interfaccia chiave che definisca il concetto di "radice" (valutazione)

- ComplexVector: Dot Product
Stai calcolando <v,w>=SOMMA(v(i) x w(i)\). Questa è la convenzione standard dei Matematici (lineare nel primo argomento, antilineare nel secondo).
Attenzione per il package mq (Quantum Mechanics): Nella notazione di Dirac (Fisica), il prodotto scalare (bra-ket <phi|psi> è, per convenzione, antilineare nel primo argomento (bra) e lineare nel secondo (ket): <phi|psi>=SOMMA(phi(i)\ x psi(i))
Se userai questa classe ComplexVector per i tuoi StateVector quantistici, dovrai ricordarti che v.dotProduct(w) calcolerà matematicamente <w|v> (o invertire la logica nella classe HilbertSpace specifica per la MQ).

- Complex.sqrt() : 
// Attuale: magnitude + real può andare in overflow se entrambi sono enormi
double magnitude = this.modulus(); 

// Alternativa numericamente stabile (Algorithm 312, ACM):
double t = Math.sqrt((Math.abs(real) + magnitude) / 2.0);
if (real >= 0) {
    realPart = t;
    imaginaryPart = imaginary / (2.0 * t);
} else {
    realPart = Math.abs(imaginary) / (2.0 * t);
    imaginaryPart = (imaginary >= 0) ? t : -t;
}

- Soluzione Architetturale: Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).

- Operatori lineari e trasformazioni: ereditare algebra.core.Operator
- AbstractLinearTransformation
LinearTransformation<K, V> extends MathFunction<V, V>, e la classe concreta MatrixOperator implementerebbe questa interfaccia, delegando il calcolo a Mv.
class AffineMapper
interface AffineTransform<K extends FieldElement<K, ?>, V extends VectorElement<K, V>> {
    V transform(V inputVector);
    AffineTransform<K, V> inverse();
    AffineTransform<K, V> compose(AffineTransform<K, V> other);
}
impl (in linearalgebra.real):
class RealAffineTransform implements AffineTransform<Real, RealVector> {//...}
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

