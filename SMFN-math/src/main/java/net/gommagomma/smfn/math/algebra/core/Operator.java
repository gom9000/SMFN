package net.gommagomma.smfn.math.algebra.core;

public interface Operator<T> extends Mapping<T, T>
{
    static <T> Operator<T> identity() {
        return t -> t;
    }

    default Operator<T> then(Operator<T> next) {
        return (T t) -> next.apply(this.apply(t));
    }

    default Operator<T> power(int n) {
        if (n < 0) throw new IllegalArgumentException("Negative power not supported for general operators.");
        if (n == 0) return identity();
        return (T t) -> {
            T result = t;
            for (int i = 0; i < n; i++) result = this.apply(result);
            return result;
        };
    }
}
