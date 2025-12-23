package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;

/**
 * Definisce un problema di ricerca di radici SCALARE: T t.c. f(T) = Zero.
 * T: Tipo dello scalare (FieldElement), sia per il dominio che per il codominio.
 */
public interface ScalarRootFindingProblem<T extends FieldElement<T>>
extends AnalysisProblem<T>
{
    /**
     * La funzione scalare f(T) il cui zero deve essere trovato.
     * @return La MathFunction f: T -> T.
     */
	Mapping<T, T> getFunction();
}
