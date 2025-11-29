package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.core.algebra.numeric.Real;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;
import net.gommagomma.smfn.math.linearalgebra.complex.HamiltonianOperator;

public class QuantumSimulationTest
{
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
}
