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
|-- physics                                 # Package per le applicazioni fisiche (Meccanica, Elettromagnetismo, MQ, RG)
|   |-- core/                               (Interfacce fisiche base)
|   |-- mechanics/                          (Dinamica, gravità, cinematica)
|   |-- em                                  # Classi per campi E e B
|   |-- mq                                  # Classi per funzioni d'onda, operatori (HamiltonianOperator, Observable, ...)
|   |   |-- models
|   |-- relativity                          # Classi per metriche tensoriali
</pre>





# NOTE:
- Semiring dice correttamente che l'addizione è commutativa, ma non c'è nessuna CommutativeAdditiveMonoid. Quindi il type system non rappresenta completamente l'assioma. Ma aggiungerebbe una interfaccia vuota che non serve...
  Lo stesso per EuclideanDomain che estende CommutativeRing, ma non c'è alcuna rappresentazione dell'assenza di divisori dello zero. Ma sarebbe una interfaccia vuota inutile...
- VectorFieldPlotter (con le freccette?)
- Demo di ZnRing su tutte le sue possibilità in algebra/linearalgebra/...;
- Da valutare un "epsilon relativo" (ulps - units in the last place), che adatta la tolleranza alla grandezza dei numeri confrontati.
- Nelle implementazioni concrete (es. RealMatrix), considera di usare internamente double[] o double[][] primitivi per lo storage, e crea gli oggetti Real "on the fly" solo quando richiesti tramite get(row, col).
- Per la Fisica (Simulazione e Animazione)
    - SimulationPanel: Un pannello che esegue un loop di aggiornamento a tempo fisso (es. 60 FPS).
    - PhysicsRenderer: Logica per disegnare corpi e frecce per le forze o i campi elettrici.
    - Camera: Logica per gestire la vista, permettendo all'utente di muovere la visuale nello spazio simulato.
- mq.models:
  - HarmonicOscillator
  - ParticleInBox             facile
  - FiniteSquareWell          facile
  - QuantumTunneling          medio
  - HydrogenRadialEquation    facile
  - HydrogenAtom              medio
  - TwoLevelSystem            medio
  - PeriodicPotential         facile
	- MorsePotential            facile
  - QuantumRotor              medio
  - DoubleWell                facile