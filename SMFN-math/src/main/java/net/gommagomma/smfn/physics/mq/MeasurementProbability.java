package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Real;

/**
 * Una coppia (autovalore, probabilita' secondo la regola di Born) --
 * restituita da QuantumSystemSimulator.measurementProbabilities(), la
 * distribuzione completa senza il rumore di un campionamento ripetuto.
 */
public final class MeasurementProbability
{
	private final Real value;
	private final Real probability;

	public MeasurementProbability(Real value, Real probability) {
		this.value = value;
		this.probability = probability;
	}

	public Real getValue() { return value; }
	public Real getProbability() { return probability; }

	@Override
	public String toString() { return "P(" + value + ") = " + probability; }
}
