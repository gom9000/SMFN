/*
 * ComplexField.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Representation of the complex field.
 * @author gommagomma.net
 */
public class ComplexField
implements Field<Complex>
{
	@Override
	public Complex sum(Complex a, Complex b)
	{
		return new Complex(a.getRe() + b.getRe(), a.getIm() + b.getIm());
	}


	@Override
	public Complex mul(Complex a, Complex b)
	{
		double real = (a.getRe() * b.getRe()) - (a.getIm() * b.getIm());
    	double imaginary = (a.getRe() * b. getIm()) + (a.getIm() * b.getRe());

		return new Complex(real, imaginary);
	}


	@Override
	public Complex sub(Complex a, Complex b)
	{
		return new Complex(a.getRe() - b.getRe(), a.getIm() - b.getIm());
	}


	@Override
	public Complex div(Complex a, Complex b)
	{
		double div = Math.pow(b.modulus(), 2);
    	Complex c = mul(a, b.conjugate());

		return new Complex(c.getRe() / div, c.getIm() / div);
	}
}
