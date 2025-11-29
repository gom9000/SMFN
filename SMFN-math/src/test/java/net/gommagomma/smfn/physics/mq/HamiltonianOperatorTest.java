package net.gommagomma.smfn.physics.mq;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import net.gommagomma.smfn.math.core.algebra.numeric.Complex;
import net.gommagomma.smfn.math.linearalgebra.complex.ComplexVector;

public class HamiltonianOperatorTest
{
    @Test
    public void testHamiltonianMultiplication() {
        // Hx = |0  1|
        //      |1  0|
        HamiltonianOperator Hx = new HamiltonianOperator(new Complex[][] {
            {new Complex(0.0), new Complex(1.0)},
            {new Complex(1.0), new Complex(0.0)}
        });

        // Vettore |su> = [1, 0]
        ComplexVector stateUp = new ComplexVector(new Complex(1.0), new Complex(0.0));
        // Hx * |su> = |giù> = [0, 1]
        ComplexVector result1 = Hx.evaluate(stateUp);
        ComplexVector expected1 = new ComplexVector(new Complex(0.0), new Complex(1.0));
        assertTrue(result1.isEqual(expected1), "Hx * |su> should be |giù>");

        // Vettore |giù> = [0, 1]
        ComplexVector stateDown = new ComplexVector(new Complex(0.0), new Complex(1.0));
        // Hx * |giù> = |su> = [1, 0]
        ComplexVector result2 = Hx.evaluate(stateDown);
        ComplexVector expected2 = new ComplexVector(new Complex(1.0), new Complex(0.0));
        assertTrue(result2.isEqual(expected2), "Hx * |giù> should be |su>");
    }
}
