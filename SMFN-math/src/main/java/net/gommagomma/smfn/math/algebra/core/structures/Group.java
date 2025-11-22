package net.gommagomma.smfn.math.algebra.core.structures;


import net.gommagomma.smfn.math.algebra.core.elements.additive.GroupElement;


// Un gruppo è un monoide con inversi garantiti (gestiti dall'interfaccia GroupElement)
public interface Group<E extends GroupElement<E>>
extends AdditiveMonoid<E>
{}
