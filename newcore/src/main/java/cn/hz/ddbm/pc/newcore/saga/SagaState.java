package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;

import java.util.Objects;

/**
 * @param <M> 主状态，
 */
@Getter
public class SagaState<M extends Enum<M>> extends State<Triple<M, SagaState.Offset, Boolean>> {
    M       master;
    Offset  offset;
    Boolean isForward;

    private static <M extends Enum<M>> String buildCode(M master, Offset offset, Boolean isForward) {
        if (isForward) {
            return String.format(">>>>%s:%s", master.name(), offset.name());
        } else {
            return String.format("<<<<%s:%s", master.name(), offset.name());
        }
    }

    public SagaState(M master, Offset offset, Boolean isForward) {
        super(Triple.of(master, offset, isForward));
        this.master    = master;
        this.offset    = offset;
        this.isForward = isForward;
    }


    @Override
    public Triple<M, SagaState.Offset, Boolean> code() {
        return code;
    }

    public SagaState<M> cloneSelf() {
        return new SagaState<>(this.getMaster(), this.offset, this.isForward);
    }


    public enum Offset {
        task, failover, su, retry, fail;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SagaState<?> state1 = (SagaState<?>) object;
        return Objects.equals(master, state1.master) && offset == state1.offset && Objects.equals(isForward, state1.isForward);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), master, offset, isForward);
    }

    @Override
    public String toString() {
        return buildCode(this.master, this.offset, this.isForward);
    }
}
