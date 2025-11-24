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
|   |   |-- complex                         # Implementazioni per C (ComplexVector, ComplexMatrix)
|   |   |-- rational                        # Implementazioni per Q (RationalVector, RationalMatrix)
|   |   |-- real                            # Implementazioni per R (RealVector, RealMatrix)
|   |   |-- signedint                       # Implementazioni per Z (SignedIntVector, SignedIntMatrix)
|   |-- analysis/                           # Calcolo (Funzioni, Derivate, Integrali, Risolutori Numerici)
|   |   |-- core/                           # Contiene le interfacce generiche riutilizzabili
|   |   |-- fractals/                       # Contiene implementazioni specifiche di frattali
|   |   |-- solvers/                        # Contiene implementazioni di metodi numerici (es. NewtonRaphsonSolver)
|   |   |-- integral/                       # (es. Metodi di quadratura numerica)
|   |   |-- differential/                   # (es. Runge-Kutta per ODE)
|   |-- utils                               # Utility e Costanti (MathConstants, MathUtils)
|-- graphics/                               # Logica specifica per la viewport e il rendering
|   |-- core/                               # Interfacce grafiche (Renderer, Viewport, DataSource, RenderableData)
|   |-- swing/                              # impl (SwingRenderer)
|   |-- plotting/                           # (FunctionPlotter, ...)
|-- physics                                 # Package per le applicazioni fisiche (Elettromagnetismo, MQ, RG)
|   |-- core/                               (Interfacce fisiche base: particella, forza...)
|   |-- mechanics/                          (Dinamica, gravità, cinematica)
|   |-- em                                  # Classi per campi E e B
|   |-- mq                                  # Classi per funzioni d'onda, operatori
|   |-- relativity                          # Classi per metriche tensoriali
</pre>


## net.gommagomma.smfn.math
---------------------------
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

## net.gommagomma.smfn.math.linearalgebra.core.algorithms
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

### net.gommagomma.smfn.math.analysis.core:
- interface MathFunction<T extends AlgebraicElement<T>, R extends AlgebraicElement<R>> {R evaluate(T input);}
- interface IterativeSystem<T> { T nextIteration(T current);}
- interface ConvergenceTest<T> { boolean isConverged(T current, T previous, Real tolerance);}
- interface Solver<P, S> {S solve(P problem, ConvergenceTest<S> test);}

### net.gommagomma.smfn.math.analysis.fractals;
- class MandelbrotSolver implements Solver<Complex, Integer> {}
- class MandelbrotFunction implements MathFunction<Complex, Real> {}
- class JuliaFunction implements MathFunction<Complex, Real> {}


- AlgebraicElement.copy(): Il metodo copy() è utile ma potrebbe essere ridefinito come un'interfaccia CloneableElement separata se non tutti gli elementi devono essere clonabili (anche se in questo contesto è quasi sempre necessario).
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


## net.gommagomma.smfn.graphics:
### net.gommagomma.smfn.core:
- interface Viewport<E extends AlgebraicElement<E>> {int getPixelWidth();int getPixelHeight();double getMinX();double getMaxX();double getMinY();double getMaxY();E mapPixelToElement(int x, int y);void setMathematicalArea(double minX, double maxX, double minY, double maxY);}
- interface Renderer {void setPixel(int x, int y, int rgbColor);void display();}
- interface ColorMapper {int toRGB(double value);}
- interface CoordinateMapper<E extends AlgebraicElement<E>> {E mapPixelToElement(int x, int y, Viewport<E> viewport);}

### net.gommagomma.smfn.plotting:
- class ElementPlaneViewport<E extends AlgebraicElement<E>> implements Viewport<E> {}
- class LinearColorMapper implements ColorMapper {}
- class LinearComplexCoordinateMapper implements CoordinateMapper<Complex> {}
- class FunctionPlotter<I extends AlgebraicElement<I>, O extends AlgebraicElement<O>>

### net.gommagomma.smfn.swing:
class SwingRenderer implements Renderer {}

Funzionalità della Sezione smfn.graphics
Questa sezione si occuperà di creare View (visualizzazioni) che prendono i tuoi Model (oggetti matematici/fisici) e li disegnano.
A. Per le Funzioni Matematiche (Plotting 2D)
Avrai bisogno di classi per:

    PlottingPanel (o Canvas): Un componente Swing personalizzato in cui disegnare.
    FunctionRenderer: Logica per prendere una funzione (es. un'interfaccia Function<Double, Double>) e campionarla in punti (x, y) per disegnarla come una linea.
    Gestione Assi: Logica per disegnare gli assi cartesiani, le etichette, lo zoom e il panning.

public interface Viewport<K extends FieldElement<K>, V extends VectorElement<K, V>> {
    int getWidth();
    int getHeight();

    // Mappa dal pixel allo spazio matematico
    V mapPixelToMathematicalSpace(NaturalVector pixelCoords);

    // Mappa dallo spazio matematico al pixel (con quantizzazione implicita)
    NaturalVector mapMathematicalSpaceToPixel(V mathCoords);
    
    // Un metodo per ottenere la trasformazione affine sottostante, se necessario
    // AffineTransform<K, V> getMathematicalTransform(); 
}


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





