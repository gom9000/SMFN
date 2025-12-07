# SMFN eXPerience
------------------------------------------------------------------------    

## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/                           (AlgebraicElement, AlgebraicStructure, Commutative, NumericFactory, MathFunction)
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- additive/               (...)
|   |   |   |   |-- multiplicative/         (...)
|   |   |   |   |-- tensors/                (TensorElement)
|   |   |   |   \-- capabilities/           (ComparableElement,ExponentiableElement,NormableElement,SqrtableElement)
|   |   |   |-- structures/                 (AdditiveMonoid,MultiplicativeMonoid,ComutativeMultiplicativeMonoid,Semiring,Ring,CommutativeRing,Group, AbelianGroup,Field)
|   |   |-- numeric/                        (Natural, NaturalFactory, Signedint, SignedintFactory, ZnElement, ZnElementFactory, Rational, RationalFactory, Real, RealFactory, Complex, ComplexFactory)
|   |   \-- structures/                     (NaturalSemiring, IntegerRing, ZnRing, RationalField, RealField, ComplexField)
|   |-- linearalgebra                       # Vettori, Matrici e Spazi
|   |   |-- core                   		  # Interfacce per Vettori, Matrici, Spazi
|   |   |   |-- elements/                   (...)
|   |   |   \-- structures/                 (...)
|   |   |-- complex                         # Implementazioni per C (ComplexVector, ComplexMatrix, spaces)
|   |   |-- rational                        # Implementazioni per Q (RationalVector, RationalMatrix, spaces)
|   |   |-- real                            # Implementazioni per R (RealVector, RealMatrix, spaces)
|   |   \-- signedint                       # Implementazioni per Z (SignedIntVector, SignedIntMatrix, spaces)
|   |-- geometry/								 # (GeometryEntity, Point, Circle, Ellipse)
|   |-- analysis/                           # Calcolo (Funzioni, Derivate, Integrali, Risolutori Numerici)
|   |   |-- functions/						 # LinearFunction, PolynomialFunction, ...
|   |   |-- fractals/                       # Contiene implementazioni specifiche di frattali
|   |   |-- models/                         # Contiene le definizioni astratte dei problemi
|   |   |-- solvers/                        # Contiene implementazioni di metodi numerici
|   |   |   |-- core/                       # Contiene le astrazioni fondamentali ed i parametri comuni
|   |   |   |-- differential/               # Contiene strumenti di differenziazione numerica 
|   |   |   |-- iterative/                  # Contiene implementazioni per i solutori iterativi
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
- interface MathFunction<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> {C evaluate(D input);default <V extends AlgebraicElement<V>> MathFunction<V, C> compose(MathFunction<V, D> before)}

### net.gommagomma.smfn.math.algebra.core.elements.additive:
- interface AdditiveMonoidElement<E extends AdditiveMonoidElement<E>> extends AlgebraicElement<E> { E add(E other); E getZero(); default boolean isZero() { return isEqual(getZero()); }}
- interface CommutativeMonoidElement<E extends CommutativeMonoidElement<E>> extends AdditiveMonoidElement<E>, Commutative {}
- interface GroupElement<E extends GroupElement<E>> extends AdditiveMonoidElement<E> {E negate(); default E subtract(E other) {return add(other.negate());}}
- interface AbelianGroupElement<E extends AbelianGroupElement<E>> extends GroupElement<E>, CommutativeMonoidElement<E> {}

### net.gommagomma.smfn.math.algebra.core.elements.multiplicative:
- interface MultiplicativeMonoidElement<E extends MultiplicativeMonoidElement<E>> extends AlgebraicElement<E> { E multiply(E other); E getOne(); default boolean isOne() { return isEqual(getOne()); }}
- interface CommutativeMultiplicativeMonoidElement<T extends CommutativeMultiplicativeMonoidElement<T>> extends MultiplicativeMonoidElement<T>, Commutative {}
- interface SemiringElement<E extends SemiringElement<E>> extends CommutativeMonoidElement<E>, MultiplicativeMonoidElement<E> {}
- interface RingElement<E extends RingElement<E>> extends SemiringElement<E>, AbelianGroupElement<E> {}
- interface CommutativeRingElement<E extends CommutativeRingElement<E>> extends RingElement<E>, CommutativeMultiplicativeMonoidElement<E> {}
- interface FieldElement<E extends FieldElement<E>> extends CommutativeRingElement<E>{E inverse();default E divide(E other) {	return multiply(other.inverse());}}

### net.gommagomma.smfn.math.algebra.core.elements.tensors:
- interface TensorElement<K extends SemiringElement<K>> {int getRank(); int[] getShape(); long size(); K get(int... indices);}

### net.gommagomma.smfn.math.algebra.core.elements.capabilities:
- interface ComparableElement<E extends ComparableElement<E>> extends AlgebraicElement<E>, Comparable<E>{ default boolean isLessThan(E other) { return compareTo(other) < 0; }default boolean isGreaterThan(E other) {return compareTo(other) > 0;} double modulus();}
- interface SqrtableElement<E extends SqrtableElement<E>> extends AlgebraicElement<E> { E sqrt(); }
- interface ExponentiableElement<E extends ExponentiableElement<E>> extends AlgebraicElement<E> { E power(int exponent);}
- interface NormableElement<N extends FieldElement<N>, E extends NormableElement<N, E>>  extends AlgebraicElement<E> {N norm();}

### net.gommagomma.smfn.math.algebra.core.structures:
- interface AdditiveMonoid<E extends AdditiveMonoidElement<E>> extends AlgebraicStructure<E>{ E additiveIdentity(); }}
- interface MultiplicativeMonoid<E extends MultiplicativeMonoidElement<E>> extends AlgebraicStructure<E> {  E multiplicativeIdentity();}
- interface CommutativeMultiplicativeMonoid<E extends CommutativeMultiplicativeMonoidElement<E>> extends MultiplicativeMonoid<E> {}
- interface Group<E extends GroupElement<E>> extends AdditiveMonoid<E> {}
- interface AbelianGroup<E extends AbelianGroupElement<E>> extends Group<E> {}
- interface Semiring<E extends SemiringElement<E>> extends AlgebraicStructure<E>{}
- interface Ring<E extends RingElement<E>> extends Semiring<E>, AbelianGroup<E> {}
- interface CommutativeRing<E extends CommutativeRingElement<E>> extends Ring<E>, CommutativeMultiplicativeMonoid<E> {}
- interface Field<E extends FieldElement<E>> extends CommutativeRing<E> {}

### net.gommagomma.smfn.math.algebra.numeric:
- final class Natural implements SemiringElement<Natural>, ExponentiableElement<Natural>, ComparableElement<Natural> { /* ... */ }
- class NaturalFactory implements NumericFactory<Natural> {}
- final class SignedInt implements CommutativeRingElement<SignedInt>, ExponentiableElement<SignedInt>, ComparableElement<SignedInt>, EuclideanDomainElement<SignedInt, SignedInt> { /* ... */ }
- class SignedIntFactory implements NumericFactory<SignedInt> {}
- final class Rational implements FieldElement<Rational>, NormableElement<Real, Rational>, ExponentiableElement<Rational>, ComparableElement<Rational> { /* ... */ }
- class RationalFactory implements NumericFactory<Rational> {}
- final class Real implements FieldElement<Real>, NormableElement<Real, Real>, ExponentiableElement<Real>, SqrtableElement<Real>, ComparableElement<Real> { /* ... */ }
- class RealFactory implements NumericFactory<Real> {}
- final class Complex implements FieldElement<Complex>, NormableElement<Real, Complex>, ExponentiableElement<Complex>, SqrtableElement<Complex> { /* ... */ }
- class ComplexFactory implements NumericFactory<Complex> {}
- final class ZnElement implements CommutativeRingElement<ZnElement> {}
- class ZnElementFactory implements NumericFactory<ZnElement> {}

### net.gommagomma.smfn.math.algebra.structures:
- final class NaturalSemiring implements Semiring<Natural> { /* ... */ }
- final class IntegerRing implements CommutativeRing<SignedInt> { /* ... */ }
- final class RationalField implements Field<Rational> { /* ... */ }
- final class RealField implements Field<Real> { /* ... */ }
- final class ComplexField implements Field<Complex> { /* ... */ }
- final class ZnRing implements CommutativeRing<ZnElement> {}

## net.gommagomma.smfn.math.linearalgebra
-----------------------------------------
### net.gommagomma.smfn.math.linearalgebra.core.elements.vectors:
- interface SpaceElement<V extends SpaceElement<V>> extends AlgebraicElement<V>{}
- interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends SpaceElement<V>, CommutativeMonoidElement<V>, TensorElement<K> {int dimension(); K get(int index); V multiplyByScalar(K scalar);}
- interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> extends SemimoduleElement<K, V>, AbelianGroupElement<V> {  V createNewInstance(@SuppressWarnings("unchecked") K... components); }
- interface VectorElement<K extends FieldElement<K>, V extends VectorElement<K, V>>
extends ModuleElement<K, V> {}
- interface NormedVectorElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends NormedVectorElement<K, V>> extends VectorElement<K, V>, NormableElement<Real, V>{  default Real distanceTo(V other) {}}
- interface InnerProductSpaceElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends NormedVectorElement<K, V> {K dotProduct(V other);}
- public abstract class AbstractRank1Tensor<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> implements SemimoduleElement<K, V> {}

### net.gommagomma.smfn.math.linearalgebra.core.elements.matrices:
- interface SemiringMatrixElement<K extends SemiringElement<K>,V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends AlgebraicElement<M>, CommutativeMonoidElement<M> { int getRows(); int getColumns(); K get(int row, int col); V getRowVector(int row); V getColumnVector(int col);  M multiply(M other);  M multiplyByScalar(K scalar); V multiply(V vector);}
- interface RingMatrixElement<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixElement<K, V, M>, AbelianGroupElement<M> {}
- interface FieldMatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>> extends RingMatrixElement<K, V, M> { K determinant(); M inverse(); M transpose(); }
- abstract class AbstractSemiringMatrix<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>, F extends SemiringMatrixFactory<K, V, M>> implements SemiringMatrixElement<K, V, M>, TensorElement<K> {    protected final K[][] data; protected final int rows;  protected final int cols;  protected final F factory;}
- abstract class AbstractRingMatrix<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>, F extends RingMatrixFactory<K, V, M>> extends AbstractSemiringMatrix<K, V, M, F> implements RingMatrixElement<K, V, M> {}
- abstract class AbstractFieldMatrix<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>, F extends FieldMatrixFactory<K, V, M>> extends AbstractRingMatrix<K, V, M, F> implements FieldMatrixElement<K, V, M> {}

### net.gommagomma.smfn.math.linearalgebra.core.structures.spaces:
- interface Space<V extends AlgebraicElement<V>> extends AlgebraicStructure<V> {}
- interface Semimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends Space<V> {Semiring<K> getScalarStructure();}
- interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> extends Semimodule<K, V> {Ring<K> getScalarStructure(); }
- interface VectorSpace<K extends FieldElement<K>, V extends VectorElement<K, V>> extends Module<K, V> {Field<K> getScalarStructure();}
- interface MetricSpace<T extends AlgebraicElement<T>> extends Space<T> {Real distance(T point1, T point2);}
- class ScalarMetricSpace<T extends AbelianGroupElement<T> & NormableElement<Real, T>> implements MetricSpace<T> {}
- interface InnerProductSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends VectorSpace<K, V>, MetricSpace<V> {default K innerProduct(V v1, V v2) {return v1.dotProduct(v2);} @Override   default Real distance(V point1, V point2) { return point1.distanceTo(point2); }}
- interface HilbertSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends InnerProductSpace<K, V> {}

- interface SemiringMatrixSemimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends Space<M> {Semiring<K> getScalarStructure();	int getMatrixRows(); int getMatrixColumns();}
- interface RingMatrixModule<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixSemimodule<K, V, M> { @Override	Ring<K> getScalarStructure();}
- interface FieldMatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>>
extends RingMatrixModule<K, V, M> {@Override Field<K> getScalarStructure();}

### net.gommagomma.smfn.math.linearalgebra.core.structures.factories:
- interface SemiringMatrixFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>{ M createMatrix(K[][] data); M createZeroMatrix(int rows, int cols); V createVector(K[] data); 	K getZeroScalar();	K getOneScalar();}
- interface RingMatrixFactory<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>>extends SemiringMatrixFactory<K, V, M> {}
- interface FieldMatrixFactory<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>> extends RingMatrixFactory<K, V, M> {}

### net.gommagomma.smfn.math.linearalgebra.core.operators:
- interface LinearOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends LinearOperator<K, V, O>> extends MathFunction<V, V> {}
- interface ProjectionOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, P extends ProjectionOperator<K, V, P>> extends LinearOperator<K, V, P> {}
- public abstract class AbstractProjectionOperator<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> 
implements ProjectionOperator<K, V, AbstractProjectionOperator<K, V>> {}
- interface HermitianOperator<K extends FieldElement<K>, V extends VectorElement<K, V>, O extends HermitianOperator<K, V, O>>  extends LinearOperator<K, V, O> {Real expectationValue(V state);}

### net.gommagomma.smfn.math.linearalgebra.natural:
- final class NaturalVector extends AbstractRank1Tensor<Natural, NaturalVector> { //... }
- final class NaturalVectorFactory implements SemimoduleVectorFactory<Natural, NaturalVector> {}
- final class NaturalSemimodule implements Semimodule<Natural, NaturalVector> {}
- final class NaturalMatrixFactory implements SemiringMatrixFactory<Natural, NaturalVector, NaturalMatrix> {}
- final class NaturalMatrix extends AbstractSemiringMatrix<Natural, NaturalVector, NaturalMatrix, NaturalMatrixFactory> {}
- final class NaturalMatrixSemimodule implements SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix> {}

###net.gommagomma.smfn.math.linearalgebra.signedint:
- final class SignedIntVector extends AbstractRank1Tensor<SignedInt, SignedIntVector> implements ModuleElement<SignedInt, SignedIntVector> {//...}
- final class SignedIntModule implements Module<SignedInt, SignedIntVector> {}
- final class SignedIntMatrixFactory implements RingMatrixFactory<SignedInt, SignedIntVector, SignedIntMatrix> {}
- final class SignedIntMatrix extends AbstractRingMatrix<SignedInt, SignedIntVector, SignedIntMatrix, SignedIntMatrixFactory> {}
- final class SignedIntMatrixModule implements RingMatrixModule<SignedInt, SignedIntVector, SignedIntMatrix> {}

### net.gommagomma.smfn.math.linearalgebra.real:
- final class RealVector extends AbstractRank1Tensor<Real, RealVector> implements InnerProductSpaceElement<Real, RealVector> {//...}
- final class RealVectorSpace implements HilbertSpace<Real, RealVector> {//...}
- final class RealMatrixFactory implements FieldMatrixFactory<Real, RealVector, RealMatrix> {}
- final class RealMatrix extends AbstractFieldMatrix<Real, RealVector, RealMatrix, RealMatrixFactory> {//...}
- final class RealMatrixSpace implements FieldMatrixSpace<Real, RealVector, RealMatrix> {//...}
- class RealVectorProjection extends AbstractProjectionOperator<Real, RealVector> {}

### net.gommagomma.smfn.math.linearalgebra.complex:
- final class ComplexVector extends AbstractRank1Tensor<Complex, ComplexVector> implements InnerProductSpaceElement<Complex, ComplexVector> { /... }
- final class ComplexVectorSpace implements HilbertSpace<Complex, ComplexVector> {//...}
- final class ComplexMatrixFactory implements FieldMatrixFactory<Complex, ComplexVector, ComplexMatrix> {}
- final class ComplexMatrix implements AbstractFieldMatrix<Complex, ComplexVector, ComplexMatrix, ComplexMatrixFactory> {}
- final class ComplexMatrixSpace implements FieldMatrixSpace<Complex, ComplexVector, ComplexMatrix> {//...}
- class ComplexVectorProjection extends AbstractProjectionOperator<Complex, ComplexVector> {}

### net.gommagomma.smfn.math.linearalgebra.rational:
- final class RationalVector extends AbstractRank1Tensor<Rational, RationalVector> implements InnerProductSpaceElement<Rational, RationalVector> { /... }
- final class RationalVectorSpace implements VectorSpace<Rational, RationalVector> {//...}
- final class RationalMatrixFactory implements FieldMatrixFactory<Rational, RationalVector, RationalMatrix> {}
- final class RationalMatrix implements AbstractFieldMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixFactory> {//...}
- final class RationalMatrixSpace implements FieldMatrixSpace<Rational, RationalVector, RationalMatrix> {//...}

## net.gommagomma.smfn.math.geometry
-------------------------------------
- interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> extends MathFunction<D, C> {}
- class Point implements AlgebraicElement<Point> {}
- class Circle implements GeometryEntity<RealVector, Real> {}
- class Ellipse implements GeometryEntity<RealVector, Real> {}

## net.gommagomma.smfn.math.analysis
------------------------------------
### net.gommagomma.smfn.math.analysis.functions:
- final class PolynomialFunction<K extends FieldElement<K>> implements CommutativeRingElement<PolynomialFunction<K>>, MathFunction<K, K> {}
- final class LinearFunction<K extends FieldElement<K>> implements CommutativeRingElement<LinearFunction<K>>, MathFunction<K, K>  {}

### net.gommagomma.smfn.math.analysis.models:
- interface DynamicSystem<K extends FieldElement<K>, T extends VectorElement<K, T>> {T derivative(T state, Real time);}
- interface IterativeSystem<T> {T nextIteration(T current);}

### net.gommagomma.smfn.math.analysis.solvers.core:
- interface Solver<T extends AlgebraicElement<T>, R> {}
- interface IterativeSolver<T extends AlgebraicElement<T>, R> extends Solver<T, R> {R solve(T initial, IterativeSystem<T> system, ConvergenceTest<T> test, ConvergenceParameters params, MetricSpace<T> space);}
- interface IntervalSolver<K extends FieldElement<K>, V extends VectorElement<K, V>> extends Solver<V, V> {V integrate(DynamicSystem<K, V> system, V initialState, Real startTime, Real endTime, IntegrationParameters params);}
- class ConvergenceParameters {public final Real tolerance;  public final int maxIterations;}
- class IntegrationParameters {public final Real fixedStepSize; public final ConvergenceParameters convergenceParams}
- interface ConvergenceTest<T extends AlgebraicElement<T>> {boolean isConverged(T current, T previous, ConvergenceParameters params, int iteration, MetricSpace<T> space);}

### net.gommagomma.smfn.math.analysis.solvers.differential:
- interface NumericalDifferentiator<K extends FieldElement<K>> {K derivativeAt(MathFunction<K, K> function, K x, K h);}
- class CentralDifferenceDifferentiator<K extends FieldElement<K>> 
implements NumericalDifferentiator<K> {}

### net.gommagomma.smfn.math.analysis.solvers.iterative:
- class NewtonRaphsonSolver<K extends FieldElement<K>> implements MetricSolver<K, K> {}

### net.gommagomma.smfn.math.analysis.solvers.ode:
- interface ODESolver<K extends FieldElement<K>, T extends VectorElement<K, T>> extends IntervalSolver<K, T> {T step(DynamicSystem<K, T> system, T currentState, Real currentTime, Real deltaTime);}
- class RungeKutta4Solver<K extends FieldElement<K> & CreatableFromDouble<K>, T extends VectorElement<K, T>> 
implements ODESolver<K, T> {}

### net.gommagomma.smfn.math.analysis.fractals;
- class MandelbrotSolver implements Solver<Complex, Integer> {}
- class MandelbrotFunction implements MathFunction<Complex, Real> {}
- class JuliaSolver implements Solver<Complex, Integer> {}
- class JuliaFunction implements MathFunction<Complex, Real> {}

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

- introduzione delle matrici quadrate (come anello moltiplicativo);

- public class Point<K extends FieldElement<K>, V extends VectorElement<K, V>>
implements AlgebraicElement<Point<K, V>>

- Per robustezza assoluta in librerie matematiche generiche, si preferisce un "epsilon relativo" (ulps - units in the last place), che adatta la tolleranza alla grandezza dei numeri confrontati.


- Suggerimento: Vincola l'interfaccia HermitianOperator in modo più stretto, non solo a VectorElement, ma a InnerProductSpaceElement.
// Vincolo più stretto per i problemi di MQ:
public interface HermitianOperator<K extends FieldElement<K> & NormableElement<Real, K>, 
                                  V extends InnerProductSpaceElement<K, V>, 
                                  O extends HermitianOperator<K, V, O>> 
    extends LinearOperator<K, V, O> {

    Real expectationValue(V state); // Funziona solo se il prodotto scalare è definito
}

- Polinomi: Evaluatable<X, X>  - interfaccia chiave che definisca il concetto di "radice" (valutazione)

- interfaccia ScalarFactory !!! (ScalarFactory -> SemimoduleVectorFactory -> SemiringMatrixFactory)
e centralizza lì la costruzione di tutti gli elementi
public interface ScalarFactory<K extends SemiringElement<K>> {
    /** Restituisce l'identità additiva (Zero) dello scalare K. */
    K getZeroScalar();
    /** Restituisce l'identità moltiplicativa (Uno) dello scalare K. */
    K getOneScalar();
    /** * Crea un nuovo elemento K partendo da un primitivo double.
     * Questo è il metodo statico 'valueOf()' che mancava. 
     */
    K createScalar(double value);
}
public interface SemimoduleVectorFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> 
    extends ScalarFactory<K> // Eredita le capacità di creazione di K
{
    /** Crea un nuovo elemento vettore V a partire da un array di scalari K. */
    V createVector(K[] data);
}
public interface SemiringMatrixFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
extends ScalarFactory<K> // Eredita getZeroScalar(), getOneScalar(), createScalar(double)
{ 
    M createMatrix(K[][] data); 
    M createZeroMatrix(int rows, int cols); 
    V createVector(K[] data); 
    
    // NOTA: getZeroScalar() e getOneScalar() sono stati rimossi da qui
    // e ora sono ereditati da ScalarFactory.
}

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
- Rational.valueOf(double) : 
 Se questo è l'intento (esattezza bit-a-bit): L'implementazione è perfetta.
 Se l'intento è "trova la frazione più vicina": Spesso nelle librerie scientifiche si usa l'algoritmo delle Frazioni Continue per  convertire 0.100000001 in 1/10.
 Suggerimento: Potresti voler aggiungere un metodo statico alternativo: Rational.approximate(double val, double epsilon).

- Soluzione Architetturale: Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).

- Soluzione Architetturale: Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).

- trasformazioni:
- AbstractLinearTransformation
LinearTransformation<K, V> extends MathFunction<V, V>, e la classe concreta MatrixOperator implementerebbe questa interfaccia, delegando il calcolo a Mv.
class AffineMapper
interface AffineTransform<K extends FieldElement<K>, V extends VectorElement<K, V>> {
    V transform(V inputVector);
    AffineTransform<K, V> inverse();
    AffineTransform<K, V> compose(AffineTransform<K, V> other);
}
impl (in linearalgebra.real):
class RealAffineTransform implements AffineTransform<Real, RealVector> {//...}

- Polynomial Ring: Hai PolynomialFunction che implementa CommutativeRingElement<Polynomial<K>>. Questa classe probabilmente rappresenta solo il polinomio come funzione. Manca la classe Polynomial<K> che rappresenta il Polinomio stesso (l'anello dei polinomi K[x]). Questo è cruciale per la Fattorizzazione, il Calcolo degli Autovalori (attraverso il polinomio caratteristico) e la teoria dei Campi.

- struttura mq
|   |-- mq/                                  
|   |   \-- operators/
|   |   |   \-- HamiltonianOperator, PositionOperator, MomentumOperator # Suggeriti
|   |   \-- dynamics/
|   |   |   \-- SchrodingerEquationSystem, SchrodingerSolver # Suggeriti/Rilocati
|   |   \-- core/ # Vecchia interfaces
|   |   |   \-- Observable
|   |   \-- states/
|   |   |   \-- StateVector # Suggerito
|   |   \-- problems/
|   |       \-- ParticleInABox # Suggerito

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

