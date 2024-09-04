package cn.hz.ddbm.pc.newcore.support;

import lombok.Getter;

@Getter
public class ActionResult {
    Boolean success;
    String  message;

    public static ActionResult success() {
        return new ActionResult(true, null);
    }

    public static ActionResult fail(String message) {
        return new ActionResult(false, message);
    }

    public ActionResult(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @Override
    public String toString() {
        return "ActionResult{" +
                "success=" + success +
                ", errorMessage='" + message + '\'' +
                '}';
    }
}
