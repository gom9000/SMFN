package net.gommagomma.smfn.demo.physics;

import java.util.Random;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.analysis.core.solvers.ConvergenceParameters;
import net.gommagomma.smfn.physics.mq.MeasurementOutcome;
import net.gommagomma.smfn.physics.mq.Observable;
import net.gommagomma.smfn.physics.mq.Pauli;
import net.gommagomma.smfn.physics.mq.QuantumState;
import net.gommagomma.smfn.physics.mq.QuantumSystemSimulator;

public class QuantumMeasurementDemo {

    public static void main(String[] args) {
        // 1. Definiamo gli osservabili fisici (matrici di Pauli 2x2)
        Observable<Complex> sigmaZ = new Observable<>(Pauli.SIGMA_Z);
        Observable<Complex> sigmaX = new Observable<>(Pauli.SIGMA_X);

        // 2. Prepariamo lo stato di sovrapposizione bilanciato |+> = (|0> + |1>) / sqrt(2)
        double invSqrt2 = 1.0 / Math.sqrt(2);
        QuantumState psi = QuantumState.of(
            new Complex(invSqrt2, 0.0),
            new Complex(invSqrt2, 0.0)
        );

        QuantumSystemSimulator simulator = new QuantumSystemSimulator();

        // =========================================================================
        // VALORE DI ASPETTAZIONE <psi|H|psi> (Deterministico, nessun collasso)
        // =========================================================================

        // <sigma_z> = 0.0 (pari probabilita' di ottenere +1 o -1 lungo Z)
        Real expZ = simulator.expectationValue(sigmaZ, psi);
        System.out.println("Valore d'aspettazione <sigma_z>: " + expZ.getValue());

        // <sigma_x> = 1.0 (|+> e' un autostato di sigma_x relativo all'autovalore +1)
        Real expX = simulator.expectationValue(sigmaX, psi);
        System.out.println("Valore d'aspettazione <sigma_x>: " + expX.getValue());

        // =========================================================================
        // MISURA PROIETTIVA VERA (Stocastica, Regola di Born e Collasso dello Stato)
        // =========================================================================

        ConvergenceParameters solverParams = new ConvergenceParameters(new Real(1e-12), 100);
        Random rng = new Random();

        // Esegue la misura stocastica su sigma_z: campiona un autovalore (+1 o -1) e fa collassare lo stato
        MeasurementOutcome outcome = simulator.performMeasurement(sigmaZ, psi, solverParams, rng);

        Real measuredValue = outcome.getValue();
        QuantumState collapsedState = outcome.getCollapsedState();

        System.out.println("Esito misurato (Born): " + measuredValue.getValue());
        System.out.println("Stato collassato: " + collapsedState);

        // =========================================================================
        // PROPRIETA' DEL COLLASSO (Determinismo Post-Misura)
        // =========================================================================

        // Ripetere la misura sullo stato gia' collassato restituisce con certezza (100%) lo stesso esito
        Real repeatExp = simulator.expectationValue(sigmaZ, collapsedState);
        System.out.println("Valore d'aspettazione post-collasso su <sigma_z>: " + repeatExp.getValue());
    }
}