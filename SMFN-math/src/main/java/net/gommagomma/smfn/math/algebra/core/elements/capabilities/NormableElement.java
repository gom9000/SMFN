package net.gommagomma.smfn.math.algebra.core.elements.capabilities;

import net.gommagomma.smfn.math.algebra.core.elements.multiplicative.FieldElement;
import net.gommagomma.smfn.math.algebra.core.AlgebraicElement;

// Interfaccia base per qualsiasi elemento che possiede una norma.
// N è il tipo del campo su cui la norma è definita (es. Real, Rational).
// E è il tipo dell'elemento stesso (es. Complex, RealVector).
public interface NormableElement<N extends FieldElement<N>, E extends NormableElement<N, E>> 
extends AlgebraicElement<E> 
{
    N norm();
}