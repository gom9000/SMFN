# SMFN eXPerience
------------------------------------------------------------------------    

## struttura dei package
<pre>
smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/                           (AlgbraicElement, AlgebraicStructure, Commutative)
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- additive/               (...)
|   |   |   |   |-- multiplicative/         (...)
|   |   |   |   |-- capabilities/           (...)
|   |   |   |-- structures/                 (AdditiveMonoid, Semiring, Ring, CommutativeRing, Group, AbelianGroup, Field)
|   |   |-- numeric/                        (Natural, Signedint, Rational, Real, Complex)
|   |   |-- structures/                     (NaturalSemiring, IntegerRing, RationalField, RealField, ComplexField)
|   |-- linearalgebra                       # Algebra Lineare (Vettori, Matrici, Tensori)
|   |   |-- core                            # Interfacce per Vettori/Matrici (VectorElement, Matrix)
|   |   |   |-- algorythms/					 # GaussianEliminator, GaussJordanEliminator,...
|   |   |-- complex                         # Implementazioni per C (ComplexVector, ComplexMatrix)
|   |   |-- rational                        # Implementazioni per Q (RationalVector, RationalMatrix)
|   |   |-- real                            # Implementazioni per R (RealVector, RealMatrix)
|   |   |-- signedint                       # Implementazioni per Z (SignedIntVector, SignedIntMatrix)
|   |-- geometry/								 # (GeometryEntity, Point, Circle, Ellipse)
|   |-- analysis/                           # Calcolo (Funzioni, Derivate, Integrali, Risolutori Numerici)
|   |   |-- core/                           # Contiene le interfacce generiche riutilizzabili
|   |   |-- functions/						 # LinearFunction, PolynomialFunction, ...
|   |   |-- fractals/                       # Contiene implementazioni specifiche di frattali
|   |   |-- solvers/                        # Contiene implementazioni di metodi numerici (es. NewtonRaphsonSolver)
|   |   |-- integral/                       # (es. Metodi di quadratura numerica)
|   |   |-- differential/                   # (es. Runge-Kutta per ODE)
|   |-- utils                               # Utility e Costanti (MathConstants, MathUtils)
|-- graphics/                               # Logica specifica per la viewport e il rendering
|   |-- core/                               # Interfacce grafiche (Renderer, Viewport, ColorMapper)
|   |-- swing/                              # impl (SwingRenderer1D)
|   |-- plotting/                           # (FunctionPlotter, CartesianaxisPlotter)
|-- physics                                 # Package per le applicazioni fisiche (Elettromagnetismo, MQ, RG)
|   |-- core/                               (Interfacce fisiche base: particella, forza...)
|   |-- mechanics/                          (Dinamica, gravità, cinematica)
|   |-- em                                  # Classi per campi E e B
|   |-- mq                                  # Classi per funzioni d'onda, operatori
|   |-- relativity                          # Classi per metriche tensoriali
</pre>


## net.gommagomma.smfn.math.algebra
-----------------------------------
### net.gommagomma.smfn.math.algebra.core:
- interface Commutative {}
- interface AlgebraicElement<E extends AlgebraicElement<E>> { boolean isEqual(E other); E copy();}
- interface AlgebraicStructure<E extends AlgebraicElement<E>> { String getName(); boolean contains(E e); }
- net.gommagomma.smfn.math.algebra.core.elements.additive:
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
- interface ComparableElement<E extends ComparableElement<E>> extends AlgebraicElement<E>, Comparable<E>{ default boolean isLessThan(E other) { return compareTo(other) < 0; }default boolean isGreaterThan(E other) {return compareTo(other) > 0;}}
- interface Sqrtable<E extends Sqrtable<E>> extends AlgebraicElement<E> { E sqrt(); }
- interface Exponentiable<E extends Exponentiable<E>> extends AlgebraicElement<E> { E power(int exponent);}
- interface Normable<N extends FieldElement<N>, E extends Normable<N, E>> extends AlgebraicElement<E> {N norm();}
- interface NormableOrderedFieldElement<E extends NormableOrderedFieldElement<E>> extends FieldElement<E>, ComparableElement<E>, Normable<E, E>, Sqrtable<E>{}

### net.gommagomma.smfn.math.algebra.core.structures:
- interface AdditiveMonoid<E extends AdditiveMonoidElement<E>> extends AlgebraicStructure<E>{ E additiveIdentity(); }}
- interface Group<E extends GroupElement<E>> extends AdditiveMonoid<E> {}
- interface AbelianGroup<E extends AbelianGroupElement<E>> extends Group<E> {}
- interface Ring<E extends RingElement<E>> extends Semiring<E>, AbelianGroup<E> {}
- interface CommutativeRing<E extends CommutativeRingElement<E>> extends Ring<E> {}
- interface Field<E extends FieldElement<E>> extends CommutativeRing<E> {}
- interface Semiring<E extends SemiringElement<E>> extends AlgebraicStructure<E>{E additiveIdentity(); E multiplicativeIdentity(); }

### net.gommagomma.smfn.math.algebra.numeric:
- final class Natural implements SemiringElement<Natural>, Exponentiable<Natural>, ComparableElement<Natural> { /* ... */ }
- final class SignedInt implements CommutativeRingElement<SignedInt>, Exponentiable<SignedInt>, ComparableElement<SignedInt> { /* ... */ }
- final class Rational implements FieldElement<Rational>, NormableOrderedFieldElement<Rational>, Exponentiable<Rational> { /* ... */ }
- final class Real implements FieldElement<Real>, NormableOrderedFieldElement<Real>, Exponentiable<Real> { /* ... */ }
- final class Complex implements FieldElement<Complex>, Normable<Real, Complex>, Exponentiable<Complex>, Sqrtable<Complex> { /* ... */ }

### net.gommagomma.smfn.math.algebra.structures:
- class NaturalSemiring implements Semiring<Natural> { /* ... */ }
- class IntegerRing implements CommutativeRing<SignedInt> { /* ... */ }
- class RationalField implements Field<Rational> { /* ... */ }
- class RealField implements Field<Real> { /* ... */ }
- class ComplexField implements Field<Complex> { /* ... */ }

## net.gommagomma.smfn.math.linearalgebra
-----------------------------------------
### net.gommagomma.smfn.math.linearalgebra.core:
- interface SpaceElement<V extends SpaceElement<V>> extends AlgebraicElement<V>{}
- interface VectorElement<K extends RingElement<K>, V extends VectorElement<K, V>> extends SpaceElement<V>, AbelianGroupElement<V> {
    Module<V, K> getModule(); int dimension(); K get(int index); V multiplyByScalar(K scalar);
    K dotProduct(V other); default K getScalarZero() {//...} default K getScalarOne() {//...}
- interface NormedVectorElement<K extends NormableOrderedFieldElement<K>, V extends NormedVectorElement<K, V>>extends VectorElement<K, V>, Normable<K, V>{//...}}
- interface SemimoduleElement<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>> extends CommutativeMonoidElement<V>, SpaceElement<V> {}
- interface Space<V extends AlgebraicElement<V>> extends AlgebraicStructure<V> {int dimension();}
- interface Module<V extends VectorElement<K, V>, K extends RingElement<K>> extends Space<V> {Ring<K> getScalarRing(); }
- interface VectorSpace<V extends VectorElement<K, V>, K extends FieldElement<K>> extends Module<V, K> {Field<K> getScalarRing();}
- interface MatrixElement<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>> extends AlgebraicElement<M> {}
- interface MatrixSpace<K extends FieldElement<K>, V extends VectorElement<K, V>, M extends MatrixElement<K, V, M>> extends Space<M>{Field<K> getScalarField();	int getMatrixRows();	int getMatrixColumns();}

### net.gommagomma.smfn.math.linearalgebra.core.algorithms
- final class GaussianElimination {public static <K extends FieldElement<K>> K determinant(K[][] matrixData, K elementZero, Comparator<K> magnitudeComparator) {}}
- final class GaussJordanElimination {public static <K extends FieldElement<K>> K[][] inverse(K[][] matrixData, K elementZero, K elementOne, java.util.Comparator<K> magnitudeComparator) {}}

### net.gommagomma.smfn.math.linearalgebra.natural:
- class NaturalVector implements SemimoduleElement<Natural, NaturalVector> { //... }
net.gommagomma.smfn.math.linearalgebra.signedint:
- class IntegerModule implements Module<SignedIntVector, SignedInt>
- class SignedIntVector implements VectorElement<SignedInt, SignedIntVector> {//...}

### net.gommagomma.smfn.math.linearalgebra.real:
- class RealVector implements NormedVector<Real, RealVector> {//...}
- class RealVectorSpace implements VectorSpace<RealVector, Real> {//...}
- class RealMatrix implements MatrixElement<Real, RealVector, RealMatrix> {//...}
- class RealMatrixSpace implements MatrixSpace<Real, RealVector, RealMatrix> {//...}

### net.gommagomma.smfn.math.linearalgebra.complex:
- class ComplexVector implements Vector<Complex, ComplexVector> { /... }
- class ComplexVectorSpace implements VectorSpace<ComplexVector, Complex> {//...}
- class ComplexMatrix implements MatrixElement<Complex, ComplexVector, ComplexMatrix> {//...}
- class ComplexMatrixSpace implements MatrixSpace<Complex, ComplexVector, ComplexMatrix> {//...}

### net.gommagomma.smfn.math.linearalgebra.rational:
- class RationalVector implements Vector<Rational, RationalVector> { /... }
- class RationalVectorSpace implements VectorSpace<RationalVector, Rational> {//...}
- class RationalMatrix implements MatrixElement<Rational, RationalVector, RationalMatrix> {//...}
- class RationalMatrixSpace implements MatrixSpace<Rational, RationalVector, RationalMatrix> {//...}

## net.gommagomma.smfn.math.geometry
-------------------------------------
- interface GeometryEntity<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> extends MathFunction<D, C> {}
- class Point implements NormedVectorElement<Real, Point> {}
- class Circle implements GeometryEntity<RealVector, Real> {}
- class Ellipse implements GeometryEntity<RealVector, Real> {}

## net.gommagomma.smfn.math.analysis
------------------------------------
### net.gommagomma.smfn.math.analysis.core:
- interface MathFunction<D extends AlgebraicElement<D>, C extends AlgebraicElement<C>> {C evaluate(D input);}
- interface IterativeSystem<T> { T nextIteration(T current);}
- interface ConvergenceTest<T> { boolean isConverged(T current, T previous, Real tolerance);}
- interface Solver<P, S> {S solve(P problem, ConvergenceTest<S> test);}

### net.gommagomma.smfn.math.analysis.functions:
- final class Polynomial<K extends FieldElement<K>> implements CommutativeRingElement<Polynomial<K>>, MathFunction<K, K> {}
- final class LinearFunction<K extends FieldElement<K>> implements CommutativeRingElement<LinearFunction<K>>, MathFunction<K, K>  {}

### net.gommagomma.smfn.math.analysis.fractals;
- class MandelbrotSolver implements Solver<Complex, Integer> {}
- class MandelbrotFunction implements MathFunction<Complex, Real> {}
- class JuliaFunction implements MathFunction<Complex, Real> {}


- abstractMatrix ?
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

e per l'analisi:
interface MetricSpace<T extends AlgebraicElement<T>> extends Space<T>
    double distance(T e1, T 2);
interface NormedSpace<S extends FieldElement<S>, V extends VectorElement<S, V>> extends VectorSpace<S, V>, MetricSpace<V>
    doubl norm(V v);


## net.gommagomma.smfn.graphics
-------------------------------
### net.gommagomma.smfn.core:
- final class Viewport {}
- final class ViewportController {}
- interface Renderer {}
- interface Renderer1D extends Renderer {}
- interface Renderer2D extends Renderer {}
- interface ColorMapper {Color map(E value);}

### net.gommagomma.smfn.plotting:
- class FunctionPlotter1D
- class FunctionPlotter2D
- class CartesianAxisPlotter

### net.gommagomma.smfn.drivers.swing:
- class SwingRenderer1D extends Canvas implements Renderer1D
- class SwingRenderer2D extends Canvas implements Renderer2D


## net.gommagomma.smfn.physics
------------------------------
B. Per la Fisica (Simulazione e Animazione)
Avrai bisogno di:

    SimulationPanel: Un pannello che esegue un loop di aggiornamento a tempo fisso (es. 60 FPS).
    PhysicsRenderer: Logica per disegnare gli oggetti fisici (es. la classe Particle dal package smfn.physics.core). Disegnerà cerchi per i corpi, frecce per le forze o i campi elettrici.
    Camera: Logica per gestire la vista, permettendo all'utente di muovere la visuale nello spazio simulato.


todo:
- javadoc e formattazioni strutture;
- test sugli assiomi field;





