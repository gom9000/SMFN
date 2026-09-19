## struttura dei package
<pre>
net.gommagomma.smfn/
|-- math/
|   |-- algebra/
|   |   |-- core/
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
- Semiring dice correttamente che l'addizione è commutativa, ma non c'è nessuna CommutativeAdditiveMonoid. Quindi il type system non rappresenta completamente l'assioma.
  Lo stesso per EuclideanDomain che estende CommutativeRing, ma non c'è alcuna rappresentazione dell'assenza di divisori dello zero.
- Newton usa solo la distanza tra iterazioni e non il residuo. forse un ConvergenceCriteria più ricco per esprimere contemporaneamente e separatamente la distanza ed il residuo...
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

	