package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.numeric.Complex;
import net.gommagomma.smfn.math.algebra.numeric.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.solvers.core.ConvergenceParameters;
import net.gommagomma.smfn.math.analysis.solvers.core.IntegrationParameters;
import net.gommagomma.smfn.math.analysis.solvers.core.IntervalSolver;
import net.gommagomma.smfn.math.analysis.solvers.ode.EmbeddedRK23Solver;
import net.gommagomma.smfn.math.analysis.solvers.ode.ODESolver;
import net.gommagomma.smfn.math.analysis.solvers.ode.RungeKutta4Solver;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.VectorElement;

public class QuantumSimulationTest
{
	// Helper method generico per calcolare il valore di aspettazione di qualsiasi Observable
    private <K extends FieldElement<K>, V extends VectorElement<K, V>> 
            Real measureExpectation(Observable<K, V, ?> observable, V state) {
        // Chiama il metodo ereditato da HermitianOperator
        return observable.expectationValue(state); 
    }


	/**
	 * Questa simulazione modella un sistema quantistico molto semplice:
	 * un qubit (un sistema a due livelli, come lo spin di un elettrone o un atomo a due stati energetici)
	 * Simula la misurazione dell'energia di un singolo qubit in uno stato di sovrapposizione bilanciata.
	 * 
	 * Il Sistema (L'Hamiltoniano H):
	 * Viene definito un operatore Hamiltoniano H (l'osservabile che rappresenta l'energia) in una matrice 2x2 diagonale.
	 * Gli autovalori (i valori misurabili dell'energia) sono impostati a +1 e -1.
	 * Lo Stato Iniziale (|psi>): Viene creato uno stato di "superposizione". 
	 * Questo significa che il sistema non è definito come "su" (+1) o "giù" (-1),
	 * ma è una combinazione uguale di entrambi gli stati (\(|psi\rangle =\frac{1}{\sqrt{2}}|su\rangle +\frac{1}{\sqrt{2}}|giù\rangle \)).
	 * 
	 * La Misura (Valore di Aspettazione):
	 * Il test calcola il valore medio dell'energia che ci si aspetterebbe di misurare se si preparasse lo stesso stato
	 *     \(|psi\rangle \) molte volte.
	 *     
	 * Il Risultato Atteso:
	 * Data la superposizione perfettamente bilanciata tra +1 e -1, il valore di aspettazione calcolato è esattamente 0.0.
	 * Il test verifica che il software produca questo risultato. 
	 */
    @Test
    public void testEnergyMeasurementForSuperpositionState() {
        // 1. Definisci l'osservabile (l'Hamiltoniano H)
        // Usiamo un Hamiltoniano 2x2 con autovalori 1 e -1.
        // H = |1  0|
        //     |0 -1|
    	Observable<Complex, ComplexVector, ?> H = new HamiltonianOperator(new Complex[][] {
            {new Complex(1.0), new Complex(0.0)},
            {new Complex(0.0), new Complex(-1.0)}
        });

        // 2. Definisci lo stato quantistico iniziale (|psi>)
        // Uno stato di sovrapposizione normalizzato: |psi> = (1/sqrt(2), 1/sqrt(2))
        // Ci aspettiamo che l'energia media (valore di aspettazione) sia 0.
        double invSqrt2 = 1.0 / Math.sqrt(2.0);
        Complex psi_up = new Complex(invSqrt2, 0.0);
        Complex psi_down = new Complex(invSqrt2, 0.0);
        ComplexVector psi_state = new ComplexVector(psi_up, psi_down);

        // 3. Esegui la "misura" (calcola il valore di aspettazione E = <psi|H|psi>)
        Real expectedEnergy = measureExpectation(H, psi_state);

        // 4. Verifica il risultato atteso
        Real expectedValue = new Real(0.0);

        // Usiamo isEqual() che gestisce la tolleranza EPSILON per i double
        assertTrue(expectedEnergy.isMathematicallyEqualTo(expectedValue), "Il valore di aspettazione dell'energia doveva essere 0.0");
    }


    /**
     * Questa simulazione è un'estensione più generica della prima, applicata a un sistema di dimensione maggiore (N=3).
     */
    @Test
    public void testEnergyMeasurementNxN() {
        // Creazione di un Hamiltoniano 3x3 arbitrario (devi assicurarti che sia hermitiano)
        Complex[][] hData = new Complex[][] {
            {new Complex(2.0), new Complex(0.0), new Complex(0.0)},
            {new Complex(0.0), new Complex(1.0), new Complex(0.0)},
            {new Complex(0.0), new Complex(0.0), new Complex(3.0)}
        };
        
        Observable<Complex, ComplexVector, ?> H = new HamiltonianOperator(hData);

        // Crea un vettore di stato 3D normalizzato
        ComplexVector psi_state = new ComplexVector(
            new Complex(1/Math.sqrt(3)), 
            new Complex(1/Math.sqrt(3)), 
            new Complex(1/Math.sqrt(3))
        );

        Real expectedEnergy = measureExpectation(H, psi_state);
        
        // Verifica il risultato atteso
        Real expectedValue = new Real(2.0);

        // Usiamo isEqual() che gestisce la tolleranza EPSILON per i double
        assertTrue(expectedEnergy.isMathematicallyEqualTo(expectedValue), "Il valore di aspettazione dell'energia doveva essere 2.0");
    }


    @Test
    public void testQuantumTimeEvolution() {
        // 1. Definisci l'Hamiltoniano (es. un campo magnetico costante sull'asse X)
    	Observable<Complex, ComplexVector, ?> Hx = new HamiltonianOperator(new Complex[][] {
            {new Complex(0.0), new Complex(1.0)},
            {new Complex(1.0), new Complex(0.0)}
        });
        
        // 2. Definisci il sistema differenziale
        SchrodingerEquationSystem system = new SchrodingerEquationSystem(Hx);

        // 3. Definisci lo stato iniziale |psi(t=0)> (es. |su> state)
        ComplexVector psi_initial = new ComplexVector(new Complex(1.0), new Complex(0.0));

        // 4. Configura il solutore (come nel punto 2)
        ComplexField scalarFactory = ComplexField.getInstance();
        ODESolver<Complex, ComplexVector> mqSolver = new RungeKutta4Solver<>(scalarFactory);

        // 5. Esegui l'integrazione: Fai evolvere lo stato da t=0.0 a t=PI/2, con passo dt=0.0001
        Real startTime = new Real(0.0);
        Real endTime = new Real(Math.PI/2.0);
        Real deltaTime = new Real(0.001);
        IntegrationParameters params = new IntegrationParameters(deltaTime);

        ComplexVector psi_final = mqSolver.integrate(system, psi_initial, startTime, endTime, params);

        // 6. Verifica il risultato atteso
        // Con questo Hx e questo tempo finale (PI), lo stato dovrebbe essere |giù> = (0, 1)
        ComplexVector expected_final = new ComplexVector(new Complex(0.0), new Complex(0.0, -1.0));
        //System.out.println("psi final = " + psi_final + ", psi expected = " + expected_final);
        assertTrue(psi_final.isMathematicallyEqualTo(expected_final), "Lo stato finale doveva essere |giù>, psi=" + psi_final);
    }

    @Test
    public void testQuantumTimeEvolutionAdaptive() {
        // 1. Definisci l'Hamiltoniano (es. un campo magnetico costante sull'asse X)
    	Observable<Complex, ComplexVector, ?> Hx = new HamiltonianOperator(new Complex[][] {
            {new Complex(0.0), new Complex(1.0)},
            {new Complex(1.0), new Complex(0.0)}
        });
        
        // 2. Definisci il sistema differenziale
        SchrodingerEquationSystem system = new SchrodingerEquationSystem(Hx);

        // 3. Definisci lo stato iniziale |psi(t=0)> (es. |su> state)
        ComplexVector psi_initial = new ComplexVector(new Complex(1.0), new Complex(0.0));

        // 4. Configura il solutore (ORA ADATTIVO)
        Field<Complex> complexField = ComplexField.getInstance();
        
        // Inizializziamo l'EmbeddedRK23Solver passando solo il Field<Complex>
        ComplexField scalarFactory = ComplexField.getInstance();
        IntervalSolver<Complex, ComplexVector> mqSolver = new EmbeddedRK23Solver<>(scalarFactory);

        // 5. Esegui l'integrazione: Fai evolvere lo stato da t=0.0 a t=PI/2
        Real startTime = new Real(0.0);
        Real endTime = new Real(Math.PI/2.0);

        // Scegliamo una tolleranza target per l'errore locale (es. 1e-8)
        Real tolerance = new Real(1e-6);
        ConvergenceParameters convParams = new ConvergenceParameters(tolerance, 0); // max iterazioni non è molto rilevante qui

        // Creiamo IntegrationParameters usando i parametri di convergenza, non fixedStepSize
        IntegrationParameters params = new IntegrationParameters(convParams); 
        
        // Chiamiamo integrate()
        ComplexVector psi_final = mqSolver.integrate(system, psi_initial, startTime, endTime, params);

        // 6. Verifica il risultato atteso (usando una tolleranza adeguata)
        // Il risultato corretto atteso è (0, -i)
        ComplexVector expected_final = new ComplexVector(new Complex(0.0), new Complex(0.0, -1.0));
        
        System.out.println("psi final = " + psi_final + ", psi expected = " + expected_final);
        
        // Usiamo una tolleranza (EPSILON) per il confronto del risultato finale
        // L'errore finale dovrebbe essere ben al di sotto della tolleranza locale impostata (1e-8)
        final double FINAL_EPSILON = 1e-7;

        assertTrue(psi_final.isMathematicallyEqualTo(expected_final), "Lo stato finale doveva essere |giù>, psi=" + psi_final);
    }
}
