package net.gommagomma.smfn.physics.mq;

import java.util.List;

import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.numerics.Complex;
import net.gommagomma.smfn.math.algebra.numerics.Real;
import net.gommagomma.smfn.math.algebra.structures.ComplexField;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;

/**
 * Rappresenta un osservabile fisico in meccanica quantistica, modellato matematicamente
 * come un operatore hermitiano ($M = M^\dagger$) caratterizzato da autovalori reali.
 *
 * Generico su K, deliberatamente: un'Hamiltoniana puo' essere genuinamente reale
 * (un oscillatore armonico, discretizzato su una griglia spaziale -- nessun
 * numero complesso in vista) o genuinamente complessa (un sistema di spin, con
 * termini fuori diagonale complessi) -- sono entrambi casi fisici comuni, non
 * l'uno un caso degenere dell'altro. Fissare K=Complex a priori avrebbe
 * costretto anche il caso reale a passare per il solver hermitiano complesso,
 * ignorando il dispatcher generico (GeneralEigenvalueSolver) gia' costruito
 * apposta per scegliere l'algoritmo giusto in base a K.
 *
 * QuantumState/SchrodingerEquationSystem restano invece fissati a Complex:
 * l'evoluzione temporale ha sempre una i esplicita (d|psi>/dt = -iH|psi>),
 * quindi genera dinamica complessa anche per un'Hamiltoniana reale.
 * toComplex() e' il ponte esplicito per quel momento.
 */
public class Observable<K extends ScalarElement<K>>
{
	private final SquareMatrix<K> operator;

	/**
     * Costruisce un nuovo osservabile a partire dalla matrice quadrata specificata,
     * verificando che l'operatore soddisfi la proprieta' di hermiticita'.
     *
     * @param operator la matrice quadrata che rappresenta l'operatore
     * @throws IllegalArgumentException se la matrice non e' hermitiana
     */
	public Observable(SquareMatrix<K> operator) {
		if (!operator.isHermitian()) {
			throw new IllegalArgumentException("An Observable must be represented by a Hermitian operator (M = M^dagger).");
		}
		this.operator = operator;
	}

	/**
     * Restituisce la matrice quadrata associata all'operatore sottostante dell'osservabile.
     *
     * @return la matrice operatore
     */
	public SquareMatrix<K> asOperator() {
		return operator;
	}

	/**
	 * Converte un Observable<Real> nel corrispondente Observable<Complex>
	 * (parte immaginaria zero su ogni componente) -- il ponte esplicito
	 * verso QuantumState/SchrodingerEquationSystem, che restano fissati a
	 * Complex per costruzione fisica.
	 */
	public static Observable<Complex> toComplex(Observable<Real> observable) {
		ComplexField C = ComplexField.INSTANCE;
		SquareMatrix<Real> real = observable.asOperator();
		int n = real.getN();

		Complex[] data = new Complex[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				data[i * n + j] = new Complex(real.get(i, j).getValue(), 0.0);
			}
		}

		SquareMatrix<Complex> complexOperator = SquareMatrixElementFactory.of(C, List.of(data));
		return new Observable<>(complexOperator);
	}
}
