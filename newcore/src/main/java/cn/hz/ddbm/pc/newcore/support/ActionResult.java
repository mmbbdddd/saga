package cn.hz.ddbm.pc.newcore.support;

import lombok.Getter;

@Getter
public class ActionResult {
    Boolean success;
    String  errorMessage;

    public static ActionResult success() {
        return new ActionResult(true, null);
    }

    public static ActionResult fail(String errorMessage) {
        return new ActionResult(false, errorMessage);
    }

    public ActionResult(Boolean success, String errorMessage) {
        this.success      = success;
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "success=" + success +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
