package net.gommagomma.smfn.math.analysis.numerical.problems;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.analysis.core.functions.DifferentiableMultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.problems.DifferentiableVectorProblem;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceGradientEstimator;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Sistema quadrato di n funzioni multivariate qualsiasi (n equazioni, n incognite): F(v) = (f1(v),...,fn(v)) = 0.
 * 
 * Date n funzioni f_i: K^n -> K (per i da 0 a n-1), questa classe raggruppa le singole componenti
 * componendo la funzione vettoriale residuo F: K^n -> K^n: F(x) = [ f_0(x), f_1(x), ..., f_{n-1}(x) ]^T
 * 
 * La classe costruisce la matrice Jacobiana quadrata J(x) di dimensione n x n riga per riga, adottando una strategia ibrida:
 * - Se la i-esima funzione implementa DifferentiableMultivariateFunction, il suo gradiente
 *       grad(f_i)(x) viene ottenuto in forma esatta/analitica tramite DifferentiableMultivariateFunction.getGradient()
 * - In caso contrario, il gradiente viene stimato numericamente tramite CentralDifferenceGradientEstimator utilizzando il passo di perturbazione specificato
 * La i-esima riga della matrice Jacobiana corrisponde alle componenti del gradiente della i-esima funzione:
 *   J[i, j](x) = (d f_i) / (d x_j) (x).
 *
 * @param <K> Il tipo dello scalare appartenente al campo sottostante
 * @param <S> La struttura algebrica di campo e struttura scalare associata a K
 */
public class MultivariateFunctionSystemProblem<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
implements DifferentiableVectorProblem<K>
{
	private final List<MultivariateFunction<K>> functions;
	private final VectorSemimodule<K, S> vectorSpace;
	private final CentralDifferenceGradientEstimator<K, S> numericFallback;

	/**
     * Costruisce un nuovo problema di sistema quadrato a partire da una lista di funzioni multivariate.
     *
     * @param functions La lista delle n funzioni scalari f_i: K^n -> K che costituiscono le equazioni del sistema
     * @param scalarStructure La struttura algebrica di campo e scalare per gli elementi K
     * @param fallbackStep Il passo di perturbazione h utilizzato dal differenziatore numerico per le funzioni non analiticamente differenziabili
     * @throws IllegalArgumentException Se la lista delle funzioni e' nulla o vuota
     */
	public MultivariateFunctionSystemProblem(List<MultivariateFunction<K>> functions, S scalarStructure, K fallbackStep) {
		if (functions == null || functions.isEmpty()) {
			throw new IllegalArgumentException("Serve almeno una funzione.");
		}
		this.functions = functions;
		this.vectorSpace = new VectorSemimodule<>(scalarStructure, functions.size());
		this.numericFallback = new CentralDifferenceGradientEstimator<>(scalarStructure, fallbackStep);
	}

	/**
     * Calcola il vettore residuo F(v) = [f_0(v), f_1(v), ..., f_{n-1}(v)]^T
     * valutando ogni singola funzione multivariata nel punto v.
     *
     * @param v Il vettore di input v appartenente a K^n
     * @return Il vettore residuo risultante F(v) appartenente a K^n
     * @throws IllegalArgumentException Se la dimensione del vettore v non coincide con il numero di funzioni del sistema
     */
	@Override
	@SuppressWarnings("unchecked")
	public Vector<K> apply(Vector<K> v) {
		checkDimension(v);
		K[] values = (K[]) new ScalarElement[functions.size()];
		for (int i = 0; i < functions.size(); i++) {
			values[i] = functions.get(i).apply(v);
		}
		return vectorSpace.of(values);
	}

	/**
     * Restituisce la mappa che calcola la matrice Jacobiana J(v) di dimensione n x n del sistema nel punto v.
     * <p>
     * Ciascuna riga i della matrice viene popolata con le componenti del gradiente grad(f_i)(v),
     * alternando il calcolo analitico o la stima alle differenze finite in base alle capacita' esposte da ciascuna funzione.
     * </p>
     *
     * @return Il mapping dal vettore di punto v alla matrice Jacobiana quadrata J(v)
     */
	@Override
	public Mapping<Vector<K>, SquareMatrix<K>> getJacobian() {
		return v -> {
			checkDimension(v);
			int n = functions.size();
			@SuppressWarnings("unchecked")
			K[] data = (K[]) new ScalarElement[n * n];

			for (int i = 0; i < n; i++) {
				MultivariateFunction<K> f = functions.get(i);
				Vector<K> gradient = (f instanceof DifferentiableMultivariateFunction)
					? ((DifferentiableMultivariateFunction<K>) f).getGradient().apply(v)
					: numericFallback.estimateAt(f, v);

				if (gradient.size() != n) {
					throw new IllegalStateException("Il gradiente della funzione " + i + " ha dimensione " + gradient.size()
						+ ", attesa " + n + " (il sistema e' quadrato: n funzioni, n incognite).");
				}

				for (int j = 0; j < n; j++) {
					data[i * n + j] = gradient.get(j);
				}
			}

			return SquareMatrixElementFactory.of(vectorSpace.getScalarStructure(), Arrays.asList(data));
		};
	}

	/**
     * Verifica che il vettore passato in ingresso rispetti la dimensione quadrata del sistema (n incognite per n funzioni).
     *
     * @param v Il vettore da verificare
     * @throws IllegalArgumentException Se la dimensione del vettore non corrisponde al numero di funzioni
     */
	private void checkDimension(Vector<K> v) {
		if (v.size() != functions.size()) {
			throw new IllegalArgumentException("Vector dimension mismatch. Expected " + functions.size() + ", got " + v.size());
		}
	}
}
