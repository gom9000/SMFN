package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.analysis.differential.ODESolver;
import net.gommagomma.smfn.math.analysis.differential.RungeKutta4Solver;
import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.core.algebra.structures.Field;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;

public class QuantumSimulationTest
{
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
        HamiltonianOperator H = new HamiltonianOperator(new Complex[][] {
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
        Real expectedEnergy = H.expectationValue(psi_state);

        // 4. Verifica il risultato atteso
        Real expectedValue = new Real(0.0);
        
        // Usiamo isEqual() che gestisce la tolleranza EPSILON per i double
        assertTrue(expectedEnergy.isEqual(expectedValue), "Il valore di aspettazione dell'energia doveva essere 0.0");
    }


    /**
     * Questa simulazione è un'estensione più generica della prima, applicata a un sistema di dimensione maggiore (N=3).
     */
    @Test
    public void testEnergyMeasurementNxN() {
        // Creazione di un Hamiltoniano 3x3 arbitrario (devi assicurarti che sia hermitiano)
        Complex[][] hData = new Complex[][] {
            {new Complex(2.0), Complex.ZERO, Complex.ZERO},
            {Complex.ZERO, new Complex(1.0), Complex.ZERO},
            {Complex.ZERO, Complex.ZERO, new Complex(3.0)}
        };
        
        HamiltonianOperator H = new HamiltonianOperator(hData);

        // Crea un vettore di stato 3D normalizzato
        ComplexVector psi_state = new ComplexVector(
            new Complex(1/Math.sqrt(3)), 
            new Complex(1/Math.sqrt(3)), 
            new Complex(1/Math.sqrt(3))
        );

        Real expectedEnergy = H.expectationValue(psi_state);
        
        // Verifica il risultato atteso
        Real expectedValue = new Real(2.0);
        
        // Usiamo isEqual() che gestisce la tolleranza EPSILON per i double
        assertTrue(expectedEnergy.isEqual(expectedValue), "Il valore di aspettazione dell'energia doveva essere 2.0");
    }


    @Test
    public void testQuantumTimeEvolution() {
        // 1. Definisci l'Hamiltoniano (es. un campo magnetico costante sull'asse X)
        HamiltonianOperator Hx = new HamiltonianOperator(new Complex[][] {
            {new Complex(0.0), new Complex(1.0)},
            {new Complex(1.0), new Complex(0.0)}
        });
        
        // 2. Definisci il sistema differenziale
        SchrodingerEquationSystem system = new SchrodingerEquationSystem(Hx);

        // 3. Definisci lo stato iniziale |psi(t=0)> (es. |su> state)
        ComplexVector psi_initial = new ComplexVector(new Complex(1.0), new Complex(0.0));

        // 4. Configura il solutore (come nel punto 2)
        Field<Complex> complexField = ComplexField.getInstance();
        Function<Real, Complex> r2c = Complex::new;
        ODESolver<Complex, ComplexVector> mqSolver = new RungeKutta4Solver<>(complexField, r2c);

        // 5. Esegui l'integrazione: Fai evolvere lo stato da t=0.0 a t=PI/2, con passo dt=0.01
        Real startTime = new Real(0.0);
        Real endTime = new Real(Math.PI/2.0);
        Real deltaTime = new Real(0.000001);

        ComplexVector psi_final = mqSolver.integrate(system, psi_initial, startTime, endTime, deltaTime);

        // 6. Verifica il risultato atteso
        // Con questo Hx e questo tempo finale (PI), lo stato dovrebbe essere |giù> = (0, 1)
        ComplexVector expected_final = new ComplexVector(new Complex(0.0), new Complex(-1.0));
        System.out.println("psi final = " + psi_final + ", psi expected = " + expected_final);
        assertTrue(psi_final.isEqual(expected_final), "Lo stato finale doveva essere |giù>, psi=" + psi_final);
    }
}
