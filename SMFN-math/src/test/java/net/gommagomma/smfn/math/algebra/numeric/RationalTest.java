package net.gommagomma.smfn.math.algebra.numeric;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import org.junit.jupiter.api.Test;


class RationalTest
{
	// Test constructors

    @Test
    void constructorShouldHandleZeroDenominator()
    {
        assertThatIllegalArgumentException().isThrownBy(() -> new Rational(1, 0))
            .withMessageContaining("Denominator cannot be zero");
    }

    @Test
    void constructorShouldNormalize()
    {
        Rational r = new Rational(4, 8);
        assertThat(r.getNumerator()).isEqualTo(1);
        assertThat(r.getDenominator()).isEqualTo(2);
    }

    @Test
    void constructorShouldHandleNegativeDenominator()
    {
        Rational r = new Rational(1, -2);
        assertThat(r.getNumerator()).isEqualTo(-1);
        assertThat(r.getDenominator()).isEqualTo(2);
    }

    @Test
    void constructorShouldHandleDoubleNegative()
    {
        Rational r = new Rational(-1, -2);
        assertThat(r.getNumerator()).isEqualTo(1);
        assertThat(r.getDenominator()).isEqualTo(2);
    }
    
    @Test
    void constructorShouldHandleZeroNumerator()
    {
        Rational r = new Rational(0, 5);
        assertThat(r.getNumerator()).isZero();
        assertThat(r.getDenominator()).isEqualTo(1);
    }


    // --- Test operations

    @Test
    void addShouldWork()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);
        Rational sum = a.add(b);
        assertThat(sum).isEqualTo(new Rational(3, 4));
    }
    
    @Test
    void addShouldHandleDifferentSigns()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(-1, 4);
        Rational sum = a.add(b);
        assertThat(sum).isEqualTo(new Rational(1, 4));
    }

    @Test
    void negateShouldFlipSign()
    {
        Rational r = new Rational(3, 4);
        Rational negated = r.negate();
        assertThat(negated).isEqualTo(new Rational(-3, 4));

        Rational negativeR = new Rational(-1, 2);
        Rational positiveR = negativeR.negate();
        assertThat(positiveR).isEqualTo(new Rational(1, 2));
    }

    @Test
    void multiplyShouldWork()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(2, 3);
        Rational product = a.multiply(b);
        assertThat(product).isEqualTo(new Rational(1, 3));
    }
    
    @Test
    void multiplyByZeroShouldBeZero()
    {
        Rational a = new Rational(10, 3);
        Rational b = new Rational(0, 1);
        Rational product = a.multiply(b);
        assertThat(product).isEqualTo(new Rational(0, 1));
    }

    @Test
    void inverseShouldFlipNumeratorAndDenominator()
    {
        Rational r = new Rational(3, 4);
        Rational inverse = r.inverse();
        assertThat(inverse).isEqualTo(new Rational(4, 3));
    }

    @Test
    void inverseOfNegativeShouldMaintainSignConvention()
    {
        Rational r = new Rational(-2, 3);
        Rational inverse = r.inverse();
        assertThat(inverse.getNumerator()).isEqualTo(-3); 
        assertThat(inverse.getDenominator()).isEqualTo(2);
    }

    @Test
    void inverseShouldThrowExceptionForZero()
    {
        Rational zero = new Rational(0, 1);
        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(zero::inverse)
            .withMessageContaining("Cannot take the inverse of zero");
    }

    @Test
    void subtractShouldWorkForSimpleCases()
    {
        Rational a = new Rational(3, 4);
        Rational b = new Rational(1, 2);
        Rational result = a.subtract(b);
        assertThat(result).isEqualTo(new Rational(1, 4));
    }

    @Test
    void subtractShouldHandleNegativeResults()
    {
        Rational a = new Rational(1, 4);
        Rational b = new Rational(1, 2);
        Rational result = a.subtract(b);
        assertThat(result).isEqualTo(new Rational(-1, 4));
    }

    @Test
    void divideShouldWorkForSimpleCases()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);
        Rational result = a.divide(b);
        assertThat(result).isEqualTo(new Rational(2, 1));
    }
    
    @Test
    void divideShouldWorkWhenResultIsNotInteger()
    {
        Rational a = new Rational(2, 3);
        Rational b = new Rational(4, 5);
        Rational result = a.divide(b);
        assertThat(result).isEqualTo(new Rational(5, 6));
    }

    @Test
    void divideShouldThrowExceptionWhenDividingByZero()
    {
        Rational a = new Rational(1, 2);
        Rational zero = new Rational(0, 1);
        
        // Il metodo divide() chiama inverse(), che lancia ArithmeticException se il numeratore è 0.
        assertThatExceptionOfType(ArithmeticException.class).isThrownBy(() -> a.divide(zero))
            .withMessageContaining("Cannot take the inverse of zero in a Field");
    }

    
    // --- Test java standards

    @Test
    void isEqualShouldReturnTrueForEqualRationals()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(2, 4);
        assertThat(a.isEqual(b)).isTrue();
        assertThat(a).isEqualTo(b);
    }

    @Test
    void isEqualShouldReturnFalseForDifferentRationals()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 3);
        assertThat(a.isEqual(b)).isFalse();
        assertThat(a).isNotEqualTo(b);
    }
    
    @Test
    void toStringShouldOutputFractionCorrectly()
    {
        Rational r = new Rational(1, 2);
        assertThat(r.toString()).hasToString("1/2");
    }

    @Test
    void toStringShouldOutputIntegerCorrectly() {
        Rational r = new Rational(4, 1);
        assertThat(r.toString()).hasToString("4");
    }

    @Test
    void testAddAssociativity()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);
        Rational c = new Rational(5, 2);

        Rational left = a.add(b).add(c);
        Rational right = a.add(b.add(c));
        assertThat(left).isEqualTo(right);
    }

    @Test
    void testAddCommutativity()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);

        Rational left = a.add(b);
        Rational right = b.add(a);
        assertThat(left).isEqualTo(right);
    }

    @Test
    void testMultiplyAssociativity()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);
        Rational c = new Rational(5, 2);

        Rational left = a.multiply(b).multiply(c);
        Rational right = a.multiply(b.multiply(c));
        assertThat(left).isEqualTo(right);
    }

    @Test
    void testMultiplyCommutativity()
    {
        Rational a = new Rational(1, 2);
        Rational b = new Rational(1, 4);

        Rational left = a.multiply(b);
        Rational right = b.multiply(a);
        assertThat(left).isEqualTo(right);
    }
}