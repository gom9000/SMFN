# SMFN eXPerience
------------------------------------------------------------------------    

## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/                        (AlgebraicElement, AlgebraicStructure, Commutative)
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- additive/               (...)
|   |   |   |   |-- multiplicative/         (...)
|   |   |   |   \-- capabilities/           (ComparableElement,ExponentiableElement,NormableElement,SqrtableElement)
|   |   |   |-- structures/                 (AdditiveMonoid,Semiring,Ring,CommutativeRing,Group, AbelianGroup,Field)
|   |   |-- numeric/                        (Natural, Signedint, Rational, Real, Complex)
|   |   \-- structures/                     (NaturalSemiring, IntegerRing, RationalField, RealField, ComplexField)
|   |-- linearalgebra                       # Vettori, Matrici e Spazi
|   |   |-- core                   		     # Interfacce per Vettori, Matrici, Spazi
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
|   |-- mechanics/                          (Dinamica, gravit‡†, cinematica)
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
- interface AlgebraicElement<E extends AlgebraicElement<E>> { boolean isEqual(E other); E copy();}
- interface AlgebraicStructure<E extends AlgebraicElement<E>> { String getName(); boolean contains(E e); }
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
- interface Field<E extends FieldElement<E>> extends CommutativeRing<E> {E valueOf(double value);}

### net.gommagomma.smfn.math.algebra.numeric:
- final class Natural implements SemiringElement<Natural>, Exponentiable<Natural>, ComparableElement<Natural> { /* ... */ }
- final class SignedInt implements CommutativeRingElement<SignedInt>, ExponentiableElement<SignedInt>, ComparableElement<SignedInt> { /* ... */ }
- final class Rational implements FieldElement<Rational>, NormableElement<Real, Rational>, ExponentiableElement<Rational>, ComparableElement<Rational> { /* ... */ }
- final class Real implements FieldElement<Real>, NormableElement<Real, Real>, ExponentiableElement<Real>, SqrtableElement<Real>, ComparableElement<Real> { /* ... */ }
- final class Complex implements FieldElement<Complex>, NormableElement<Real, Complex>, ExponentiableElement<Complex>, SqrtableElement<Complex> { /* ... */ }

### net.gommagomma.smfn.math.algebra.structures:
- class NaturalSemiring implements Semiring<Natural> { /* ... */ }
- class IntegerRing implements CommutativeRing<SignedInt> { /* ... */ }
- class RationalField implements Field<Rational> { /* ... */ }
- class RealField implements Field<Real> { /* ... */ }
- class ComplexField implements Field<Complex> { /* ... */ }

## net.gommagomma.smfn.math.linearalgebra
-----------------------------------------
### net.gommagomma.smfn.math.linearalgebra.core.elements.vectors:
- interface SpaceElement<V extends SpaceElement<V>> extends AlgebraicElement<V>{}
- interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends SpaceElement<V>, CommutativeMonoidElement<V> {int dimension(); K get(int index); V multiplyByScalar(K scalar);}
- interface ModuleElement<K extends RingElement<K>, V extends ModuleElement<K, V>> extends SemimoduleElement<K, V>, AbelianGroupElement<V> {  V createNewInstance(@SuppressWarnings("unchecked") K... components); }
- interface VectorElement<K extends FieldElement<K>, V extends VectorElement<K, V>>
extends ModuleElement<K, V> {}
- interface NormedVectorElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends NormedVectorElement<K, V>>
extends VectorElement<K, V>, Normable<Real, V>{  default Real distanceTo(V other) {}}
- interface InnerProductSpaceElement<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends NormedVectorElement<K, V> {K dotProduct(V other);}

### net.gommagomma.smfn.math.linearalgebra.core.elements.matrices:
- interface SemiringMatrixElement<K extends SemiringElement<K>,V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends AlgebraicElement<M>, CommutativeMonoidElement<M> { int getRows(); int getColumns(); K get(int row, int col); V getRowVector(int row); V getColumnVector(int col);  M multiply(M other);  M multiplyByScalar(K scalar); V multiply(V vector);}
- interface RingMatrixElement<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixElement<K, V, M>, AbelianGroupElement<M> {}
- interface FieldMatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>> extends RingMatrixElement<K, V, M> { K determinant(); M inverse(); M transpose(); }
- abstract class AbstractSemiringMatrix<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>, F extends SemiringMatrixFactory<K, V, M>> implements SemiringMatrixElement<K, V, M> {    protected final K[][] data; protected final int rows;  protected final int cols;  protected final F factory;}
- abstract class AbstractRingMatrix<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>, F extends RingMatrixFactory<K, V, M>> extends AbstractSemiringMatrix<K, V, M, F> implements RingMatrixElement<K, V, M> {}
- abstract class AbstractFieldMatrix<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends FieldMatrixElement<K, V, M>, F extends FieldMatrixFactory<K, V, M>> extends AbstractRingMatrix<K, V, M, F> implements FieldMatrixElement<K, V, M> {}

### net.gommagomma.smfn.math.linearalgebra.core.structures.spaces:
- interface Space<V extends AlgebraicElement<V>> extends AlgebraicStructure<V> {}
- interface Semimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends Space<V> {Semiring<K> getScalarStructure();}
- interface Module<K extends RingElement<K>, V extends ModuleElement<K, V>> extends Semimodule<K, V> {Ring<K> getScalarStructure(); }
- interface VectorSpace<K extends FieldElement<K>, V extends VectorElement<K, V>> extends Module<K, V> {Field<K> getScalarStructure();}
- interface MetricSpace<T extends AlgebraicElement<T>> extends Space<T> {Real distance(T point1, T point2);}
- class ScalarMetricSpace<T extends AbelianGroupElement<T> & NormableElement<Real, T>> implements MetricSpace<T> {}
- interface InnerProductSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> {default K innerProduct(V v1, V v2) {return v1.dotProduct(v2);} @Override   default Real distance(V point1, V point2) { return point1.distanceTo(point2); }}
extends VectorSpace<K, V>, MetricSpace<V>
- interface HilbertSpace<K extends FieldElement<K> & NormableElement<Real, K>, V extends InnerProductSpaceElement<K, V>> extends InnerProductSpace<K, V> {}

- interface SemiringMatrixSemimodule<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>> extends Space<M> {Semiring<K> getScalarStructure();	int getMatrixRows(); int getMatrixColumns();}
- interface RingMatrixModule<K extends RingElement<K>, V extends ModuleElement<K, V>, M extends RingMatrixElement<K, V, M>> extends SemiringMatrixSemimodule<K, V, M> { @Override	Ring<K> getScalarStructure();}
- interface FieldMatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends 
extends RingMatrixModule<K, V, M>{Field<K> getScalarField();}

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
- final class NaturalVector implements SemimoduleElement<Natural, NaturalVector> { //... }
- final class NaturalSemimodule implements Semimodule<Natural, NaturalVector> {}
- final class NaturalMatrixFactory implements SemiringMatrixFactory<Natural, NaturalVector, NaturalMatrix> {}
- final class NaturalMatrix extends AbstractSemiringMatrix<Natural, NaturalVector, NaturalMatrix, NaturalMatrixFactory>  {}
- final class NaturalMatrixSemimodule implements SemiringMatrixSemimodule<Natural, NaturalVector, NaturalMatrix> {}

###net.gommagomma.smfn.math.linearalgebra.signedint:
- final class SignedIntVector implements ModuleElement<SignedInt, SignedIntVector> {//...}
- final class SignedIntModule implements Module<SignedInt, SignedIntVector> {}
- final class SignedIntMatrixFactory implements RingMatrixFactory<SignedInt, SignedIntVector, SignedIntMatrix> {}
- final class SignedIntMatrix extends AbstractRingMatrix<SignedInt, SignedIntVector, SignedIntMatrix, SignedIntMatrixFactory> {}
- final class SignedIntMatrixModule implements RingMatrixModule<SignedInt, SignedIntVector, SignedIntMatrix> {}

### net.gommagomma.smfn.math.linearalgebra.real:
- final class RealVector implements InnerProductSpaceElement<Real, RealVector> {//...}
- final class RealVectorSpace implements HilbertSpace<Real, RealVector> {//...}
- final class RealMatrixFactory implements MatrixFactory<Real, RealVector, RealMatrix> {}
- final class RealMatrix extends AbstractMatrix<Real, RealVector, RealMatrix, RealMatrixFactory> {//...}
- final class RealMatrixSpace implements MatrixSpace<Real, RealVector, RealMatrix> {//...}
- class RealVectorProjection extends AbstractProjectionOperator<Real, RealVector> {}

### net.gommagomma.smfn.math.linearalgebra.complex:
- final class ComplexVector implements InnerProductSpaceElement<Complex, ComplexVector> { /... }
- final class ComplexVectorSpace implements HilbertSpace<Complex, ComplexVector> {//...}
- final class ComplexMatrix implements AbstractMatrix<Complex, ComplexVector, ComplexMatrix, ComplexMatrixFactory> {}
- final class ComplexMatrixSpace implements MatrixSpace<Complex, ComplexVector, ComplexMatrix> {//...}
- class ComplexVectorProjection extends AbstractProjectionOperator<Complex, ComplexVector> {}

### net.gommagomma.smfn.math.linearalgebra.rational:
- final class RationalVector implements InnerProductSpaceElement<Rational, RationalVector> { /... }
- final class RationalVectorSpace implements VectorSpace<Rational, RationalVector> {//...}
- final class RationalMatrixFactory implements MatrixFactory<Rational, RationalVector, RationalMatrix> {}
- final class RationalMatrix implements AbstractMatrix<Rational, RationalVector, RationalMatrix, RationalMatrixFactory> {//...}
- final class RationalMatrixSpace implements MatrixSpace<Rational, RationalVector, RationalMatrix> {//...}

## net.gommagomma.smfn.math.geometry
-------------------------------------
- interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> extends MathFunction<D, C> {}
- class Point {}
- class Circle implements GeometryEntity<RealVector, Real> {}
- class Ellipse implements GeometryEntity<RealVector, Real> {}

## net.gommagomma.smfn.math.analysis
------------------------------------
### net.gommagomma.smfn.math.analysis.functions:
- final class PolynomialFunction<K extends FieldElement<K>> implements CommutativeRingElement<Polynomial<K>>, MathFunction<K, K> {}
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
- class RungeKutta4Solver<K extends FieldElement<K>, T extends VectorElement<K, T>> 
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


B. Per la Fisica (Simulazione e Animazione)
Avrai bisogno di:

    SimulationPanel: Un pannello che esegue un loop di aggiornamento a tempo fisso (es. 60 FPS).
    PhysicsRenderer: Logica per disegnare gli oggetti fisici (es. la classe Particle dal package smfn.physics.core). Disegner√† cerchi per i corpi, frecce per le forze o i campi elettrici.
    Camera: Logica per gestire la vista, permettendo all'utente di muovere la visuale nello spazio simulato.



- Suggerimento: class Point<K extends FieldElement<K>, V extends VectorElement<K, V>> extends AbstractVector<K, V> implements GeometryEntity<V, K> {} (o qualcosa di simile) renderebbe il punto un vettore geometrico definito in uno spazio vettoriale specifico (es. Point<Real, RealVector>).

- trasformazioni

trasformazioni (in core):
class AffineMapper
interface AffineTransform<K extends FieldElement<K>, V extends VectorElement<K, V>> {
    V transform(V inputVector);
    AffineTransform<K, V> inverse();
    AffineTransform<K, V> compose(AffineTransform<K, V> other);
}
impl (in linearalgebra.real):
class RealAffineTransform implements AffineTransform<Real, RealVector> {//...}

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


todo:
math.analysis.solvers.integral: metodi di quadratura numerici (Regola di Simpson, Regola del Trapezio o Gauss-Legendre)
net.gommagomma.smfn.math.linearalgebra.solvers o estendono AbstractMatrix: 
	Decomposizione LU, Decomposizione QR
	QR Algorithm
math.analysis.solvers.ode: EmbeddedRK23Solver / RKF45Solver / Crank-Nicolson
physics.mq.solvers: Metodo agli Elementi Finiti (FEM) o alle Differenze Finite (FDM)
math.linearalgebra.solvers: Algoritmo di Lanczos o Arnoldi


Mantieni isEqual(Real other) come metodo matematico con tolleranza, ma fai in modo che Object.equals(Object other) e hashCode() usino l'uguaglianza esatta del double sottostante.
@Override
public final boolean equals(Object other) 
{
    if (this == other) return true;
    if (!(other instanceof Real)) return false;
    Real real = (Real) other;
    return Double.doubleToLongBits(this.value) == Double.doubleToLongBits(real.value);
}

// AGGIORNAMENTO 2: Rendi hashCode() coerente con l'uguaglianza esatta
@Override
public final int hashCode()
{
    return java.util.Objects.hash(this.value);
}