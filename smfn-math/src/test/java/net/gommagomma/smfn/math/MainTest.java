package net.gommagomma.smfn.math;


import net.gommagomma.smfn.math.algebra.*;


public class MainTest
{
	public static void main(String[] args)
	throws Exception
	{
		Field<Complex> field = new ComplexField();
		Complex z1 = new Complex(1,-5);
		Complex z2 = new Complex(2,0);

		Complex z3 = field.sum(z1, z2);
		System.out.println(z1 + " + " + z2 + " = " + z3);

		Space<Vector<Real>, Real> realVectorSpace = new VectorSpace<Real>(4, new RealField());
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

		Complex z = new Complex(1, -1);
		System.out.println("z = " + z);
		System.out.println("|z| = " + z.modulus());
		System.out.println("z^2 = " + z.power(2));
		System.out.println("z^2+z = " + field.sum(z.power(2),z));
		System.out.println("|z^2+z| = " + field.sum(z.power(2),z).modulus());
		System.out.println("(z^2+z)^2 = " + field.sum(z.power(2),z).power(2));
		System.out.println("(z^2+z)^2+z = " + ( field.sum(field.sum(z.power(2),z), z)  ));
		System.out.println("|(z^2+z)^2+z| = " + ( field.sum(field.sum(z.power(2),z), z)  ).modulus());
	}
}
