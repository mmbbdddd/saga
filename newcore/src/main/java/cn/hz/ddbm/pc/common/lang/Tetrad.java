package cn.hz.ddbm.pc.common.lang;


import java.io.Serializable;
import java.util.Objects;

public class Tetrad<O, T, S, F> implements Serializable {
    private static final long serialVersionUID = 1L;
    O one;
    T two;
    S three;
    F four;

    public static <O, T, S, F> Tetrad<O, T, S, F> of(O o, T t, S s, F f) {
        return new Tetrad<>(o, t, s, f);
    }

    public Tetrad(O one, T two, S three, F four) {
        this.one   = one;
        this.two   = two;
        this.three = three;
        this.four  = four;
    }

    public void setOne(O one) {
        this.one = one;
    }

    public void setTwo(T two) {
        this.two = two;
    }

    public void setThree(S three) {
        this.three = three;
    }

    public void setFour(F four) {
        this.four = four;
    }

    public O getOne() {
        return one;
    }

    public T getTwo() {
        return two;
    }

    public S getThree() {
        return three;
    }

    public F getFour() {
        return four;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Tetrad<?, ?, ?, ?> tetrad = (Tetrad<?, ?, ?, ?>) object;
        return Objects.equals(one, tetrad.one) && Objects.equals(two, tetrad.two) && Objects.equals(three, tetrad.three) && Objects.equals(four, tetrad.four);
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two, three, four);
    }
}
