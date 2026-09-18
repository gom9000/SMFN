# SMFN-library  
Type: Java Math Library | Status: Continuous Research (Sawdust alert!)

A "slow-burning" Java experimental library for mathematical modelling. It is not designed for performance, but rather to represent abstract mathematical structures and use them to perform symbolic and numerical calculations, making it possible to handle symbolic polynomial arithmetic, solve differential equations or simulate quantum mechanics problems.


## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/
|   |   |   |-- elements/                   (...)
|   |   |   |   |-- tensors/                (TensorElement)
|   |   |   |   \-- capabilities/           (Absolutable,Exponentiable,Normable,Sqrtable,Orderable)
|   |   |   |   \-- factories/           	  (NumericFactory,CompositeElementFactory)
|   |   |   |-- structures/                 (AdditiveMonoid,MultiplicativeMonoid,ComutativeMultiplicativeMonoid,Semiring,Ring,CommutativeRing,Group, AbelianGroup,Field)
|   |   |   |   \-- capabilities/           (ApproximateStructure,ExactStructure,NumericFactory)
|   |   |-- numerics/                       (Natural, Signedint, ZnElement, Rational, Real, Complex)
|   |   |-- structures/                     (NaturalSemiring, IntegerRing, ZnRing, RationalField, RealField, ComplexField)
|   |   \-- polynomial/
|   |-- linearalgebra                       # Vettori, Matrici e Spazi
|   |   |-- operators/                      # (CharacteristicPolynomialMorphism)
|   |   |-- matrices/                       # (...)
|   |   \-- vectors/                        # (...)
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
|   |-- mechanics/                          (Dinamica, gravit�, cinematica)
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





# TODO:
- VectorFieldPlotter
// 1. Definiamo la matrice di trasformazione o il campo
SquareMatrix<Real> A = SquareMatrixElementFactory.of(R, R.of(0), R.of(-1), R.of(1), R.of(0)); // Es. rotazione pura
VectorField2D field = (x, y) -> matrixMultiply(A, x, y);

// 2. Setup del plotter vettoriale
VectorFieldPlotter plotter = new VectorFieldPlotter()
    .gridDensity(25, 25)          // Risoluzione della griglia
    .maxArrowLength(0.8)         // Limite per evitare sovrapposizioni
    .colorMapper(v -> Color.BLUE); // Colore basato sull'intensit�

// 3. Rendering nel frame
renderer.startDrawing();
renderer.clear(Color.WHITE);
plotter.plot(renderer, viewport, field);
CartesianAxisPlotter.plotAxes(renderer, viewport, Color.DARK_GRAY, true);
renderer.endDrawingAndFlush();

- Demo di linearalgebra con elementi ZnRing;
- factory of() per complex/rational;

- Per robustezza assoluta in librerie matematiche generiche, si preferisce un "epsilon relativo" (ulps - units in the last place), che adatta la tolleranza alla grandezza dei numeri confrontati.

- ComplexVector: Dot Product
Stai calcolando <v,w>=SOMMA(v(i) x w(i)\). Questa � la convenzione standard dei Matematici (lineare nel primo argomento, antilineare nel secondo).
Attenzione per il package mq (Quantum Mechanics): Nella notazione di Dirac (Fisica), il prodotto scalare (bra-ket <phi|psi> �, per convenzione, antilineare nel primo argomento (bra) e lineare nel secondo (ket): <phi|psi>=SOMMA(phi(i)\ x psi(i))
Se userai questa classe ComplexVector per i tuoi StateVector quantistici, dovrai ricordarti che v.dotProduct(w) calcoler� matematicamente <w|v> (o invertire la logica nella classe HilbertSpace specifica per la MQ).

- Soluzione Architetturale: Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).

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
    PhysicsRenderer: Logica per disegnare gli oggetti fisici (es. la classe Particle dal package smfn.physics.core). Disegnerà cerchi per i corpi, frecce per le forze o i campi elettrici.
    Camera: Logica per gestire la vista, permettendo all'utente di muovere la visuale nello spazio simulato.

- derivata simbolica di un polinomio
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

	