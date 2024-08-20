package cn.hz.ddbm.pc.common.lang;


import java.util.Objects;

public class Triple<L, M, R> {
    private static final long serialVersionUID = 1L;
    final                L    left;
    final                M    middle;
    final                R    right;

    public Triple(L left, M middle, R right) {
        this.left   = left;
        this.middle = middle;
        this.right  = right;
    }

    public static <L, M, R> Triple<L, M, R> of(L left, M middle, R right) {
        return new Triple<>(left, middle, right);
    }

    public L getLeft() {
        return left;
    }

    public M getMiddle() {
        return middle;
    }

    public R getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(left, triple.left) && Objects.equals(middle, triple.middle) && Objects.equals(right, triple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, middle, right);
    }

    @Override
    public String toString() {
        return "Triple{" +
                "left=" + left +
                ", middle=" + middle +
                ", right=" + right +
                '}';
    }
}
