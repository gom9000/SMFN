package net.gommagomma.smfn.math.linearalgebra.core.structures.factories;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.SemiringElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.SemimoduleElement;
import net.gommagomma.smfn.math.linearalgebra.core.elements.vectors.matrices.SemiringMatrixElement;


public interface SemiringMatrixFactory<K extends SemiringElement<K>, V extends SemimoduleElement<K, V>, M extends SemiringMatrixElement<K, V, M>>
{ 
	M createMatrix(K[][] data); 
	M createZeroMatrix(int rows, int cols); 
	V createVector(K[] data); 
	K getZeroScalar();
	K getOneScalar();
}
