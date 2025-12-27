package net.gommagomma.smfn.math.analysis.core.problems;

import net.gommagomma.smfn.math.algebra.core.Mapping;
import net.gommagomma.smfn.math.linearalgebra.real.RealVector;

//Trovare l'intersezione tra un cerchio e una linea diventa un problema di RootFinding 
//su un Morphism<RealVector, RealVector>.
public class GeometryIntersectionProblem implements RootFindingProblem<Real, RealVector> {
 // La funzione è la differenza tra le equazioni implicite delle due entità
 @Override
 public Mapping<RealVector, RealVector> getFunction() { ... }
}