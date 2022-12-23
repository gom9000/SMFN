/*
 * Complex.java
 *  __ _ ___ _ __  _ __  __ _ __ _ ___ _ __  _ __  __ _ 
 * / _` / _ \ '  \| '  \/ _` / _` / _ \ '  \| '  \/ _` |
 * \__, \___/_|_|_|_|_|_\__,_\__, \___/_|_|_|_|_|_\__,_|
 * |___/                     |___/                      
 *
 * gommagomma.net - SMFN
 */


package net.gommagomma.smfn.math.algebra;


/**
 * Representation of a complex number.
 * @author gommagomma.net
 */
public class Complex
implements ScalarFieldElement<Complex>
{
	double real, imaginary;


    /**
     * Constructs a new complex number with the given real and imaginary parts.
     * @param real the real part of the complex number
     * @param imaginary the imaginary part of the complex number
     */
    public Complex(double real, double imaginary)
    {
    	this.real = real;
    	this.imaginary = imaginary;
    }


    /**
     * Constructs a new complex number (0,0).
     */
    public Complex()
    {
    	this(0, 0);
    }


    /**
     * Returns the real part of this complex number.
     * @return the real part of this complex number
     */
    public double getRe()
    {
    	return this.real;
    }


    /**
     * Returns the imaginary part of this complex number.
     * @return the imaginary part of this complex number
     */
    public double getIm()
    {
    	return this.imaginary;
    }


    /**
     * Conjugates the current complex number.
     * @return the conjugate
     */
    public Complex conjugate()
    {
    	this.imaginary = -this.imaginary;

    	return this;
    }


	@Override
	public double modulus()
	{
		return Math.sqrt(Math.pow(this.real, 2) + Math.pow(this.imaginary, 2));
	}


	@Override
	public Complex power(int power)
	{
		double re = this.real;
		double im = this.imaginary;
    	for (int ii = 1; ii < power; ii++)
    	{
    		re = (re * this.real) - (im * this.imaginary);
        	im = (im * this.imaginary) + (im * this.real);        	
    	}

		return new Complex(re, im);
	}


	@Override
	public Complex square()
	{
		double re = (this.real * this.real) - (this.imaginary * this.imaginary);
        double im = 2 * (this.real * this.imaginary);

        return new Complex(re, im);
	}


    @Override
    public final boolean equals(Object z)
    {
        return (z instanceof Complex && this.real == ((Complex)z).getRe()) && (this.imaginary == ((Complex)z).getIm());
    }


    @Override
    public String toString()
    {
    	StringBuilder s = new StringBuilder();

//    	s.append("(").append(this.real).append(",").append(this.imaginary).append(")");
    	s.append(this.real);
    	if (this.imaginary >= 0) { s.append("+"); }
    	s.append(this.imaginary).append("i");

    	return s.toString();
    }
}
