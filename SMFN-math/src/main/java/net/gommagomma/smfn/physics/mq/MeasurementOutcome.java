package net.gommagomma.smfn.physics.mq;

import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;

/**
 * Esito di una misura quantistica vera: l'autovalore effettivamente
 * ottenuto (con probabilita' secondo la regola di Born) e lo stato su cui
 * il sistema e' collassato -- l'autovettore corrispondente, gia'
 * normalizzato dal solver agli autovalori.
 *
 * A differenza di QuantumSystemSimulator.measure() (valore di aspettazione,
 * deterministico, nessun collasso), questo e' l'atto di misura vero e
 * proprio: ripetuto sullo stesso stato, da' in generale esiti diversi,
 * distribuiti secondo |<lambda_i|psi>|^2.
 */
public final class MeasurementOutcome
{
	private final Real value;
	private final Vector<Complex> collapsedState;

	public MeasurementOutcome(Real value, Vector<Complex> collapsedState) {
		this.value = value;
		this.collapsedState = collapsedState;
	}

	public Real getValue() { return value; }
	public Vector<Complex> getCollapsedState() { return collapsedState; }
}
