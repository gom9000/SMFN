package net.gommagomma.smfn.math;


import net.gommagomma.smfn.math.algebra.*;


public class MainTest {
	public static void main(String[] args)
	throws Exception
	{
		Field<ComplexField, Complex> field = new ComplexField();
		Complex z1 = new Complex(1,-5);
		Complex z2 = new Complex(2,2);

		Complex z3 = field.sum(z1, z2);
		System.out.println(z1 + " + " + z2 + " = " + z3);

		Space<VectorSpace<RealField, Real>, Vector<Real>, RealField, Real> realVectorSpace 
		     = new VectorSpace<RealField, Real>(4, new RealField());
		Vector<Real> v1 = new Vector<Real>(4);
		v1.add(new Real(4));
		v1.add(new Real(4));
		v1.add(new Real(4));
		v1.add(new Real(4));
		System.out.println(v1 + " mod = " + v1.modulus());
		System.out.println("v1 + v1 = " + realVectorSpace.sum(v1, v1));
		
		Vector<Complex> v3 = new Vector<Complex>(2);
		v3.add(new Complex(2,2));
		v3.add(new Complex(2,2));
		System.out.println(v3 + " mod = " + v3.modulus());
	}
}
