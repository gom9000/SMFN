package net.gommagomma.smfn.math.analysis.core.problems;

import java.util.Arrays;
import java.util.List;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.ScalarElement;
import net.gommagomma.smfn.math.algebra.core.structures.Field;
import net.gommagomma.smfn.math.algebra.core.structures.composite.ScalarStructure;
import net.gommagomma.smfn.math.analysis.core.functions.DifferentiableMultivariateFunction;
import net.gommagomma.smfn.math.analysis.core.functions.MultivariateFunction;
import net.gommagomma.smfn.math.analysis.numerical.functionals.differentiation.CentralDifferenceGradientEstimator;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrix;
import net.gommagomma.smfn.math.linearalgebra.matrices.square.SquareMatrixElementFactory;
import net.gommagomma.smfn.math.linearalgebra.vectors.Vector;
import net.gommagomma.smfn.math.linearalgebra.vectors.VectorSemimodule;

/**
 * Sistema quadrato di n funzioni multivariate qualsiasi (n equazioni, n incognite): F(v) = (f1(v),...,fn(v)) = 0.
 */
public class MultivariateFunctionSystemProblem<K extends ScalarElement<K>, S extends Field<K> & ScalarStructure<K>>
implements DifferentiableVectorProblem<K>
{
	private final List<MultivariateFunction<K>> functions;
	private final VectorSemimodule<K, S> vectorSpace;
	private final CentralDifferenceGradientEstimator<K, S> numericFallback;

	public MultivariateFunctionSystemProblem(List<MultivariateFunction<K>> functions, S scalarStructure, K fallbackStep) {
		if (functions == null || functions.isEmpty()) {
			throw new IllegalArgumentException("Serve almeno una funzione.");
		}
		this.functions = functions;
		this.vectorSpace = new VectorSemimodule<>(scalarStructure, functions.size());
		this.numericFallback = new CentralDifferenceGradientEstimator<>(scalarStructure, fallbackStep);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Vector<K> apply(Vector<K> v) {
		K[] values = (K[]) new ScalarElement[functions.size()];
		for (int i = 0; i < functions.size(); i++) {
			values[i] = functions.get(i).apply(v);
		}
		return vectorSpace.of(values);
	}

	@Override
	public Mapping<Vector<K>, SquareMatrix<K>> getJacobian() {
		return v -> {
			int n = functions.size();
			@SuppressWarnings("unchecked")
			K[] data = (K[]) new ScalarElement[n * n];

			for (int i = 0; i < n; i++) {
				MultivariateFunction<K> f = functions.get(i);
				Vector<K> gradient = (f instanceof DifferentiableMultivariateFunction)
					? ((DifferentiableMultivariateFunction<K>) f).getGradient().apply(v)
					: numericFallback.estimateAt(f, v);

				for (int j = 0; j < n; j++) {
					data[i * n + j] = gradient.get(j);
				}
			}

			return SquareMatrixElementFactory.of(vectorSpace.getScalarStructure(), Arrays.asList(data));
		};
	}
}
