package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import jdk.nashorn.internal.objects.annotations.Getter;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class PerformancePlugin implements Plugin {
    StopWatch sw = new StopWatch();

    @Override
    public String code() {
        return "performance";
    }

    @Override
    public void interrupteFlow(String s, FsmContext ctx) {
        sw.prettyPrint();
    }


    @Override
    public void onActionFinally(String name, FsmContext ctx) {
        sw.stop(name);
    }

    @Override
    public void onActionException(String actionName, Enum preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void postAction(String name, Enum lastNode, FsmContext ctx) {

    }

    @Override
    public void preAction(String name, FsmContext ctx) {
        sw.start(name);
    }
}

final class StopWatch {
    private final Map<String, TaskInfo> tasks = new HashMap();
    public String prettyPrint() {
        StringBuilder sb = new StringBuilder("");
        sb.append('\n');

        sb.append("---------------------------------------------\n");
        sb.append("ns         %     Task name\n");
        sb.append("---------------------------------------------\n");
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(9);
        nf.setGroupingUsed(false);
        NumberFormat pf = NumberFormat.getPercentInstance();
        pf.setMinimumIntegerDigits(3);
        pf.setGroupingUsed(false);
        for (TaskInfo task : tasks.values()) {
            sb.append(task.getTaskName()).append("  ");
            sb.append(pf.format((double) task.getExecuteSeconds())).append("  ");
            sb.append(task.getTaskName()).append('\n');
        }

        return sb.toString();

    }


    public void stop(String name) {
        this.tasks.get(name).stop();
    }

    public void start(String name) {
        this.tasks.computeIfAbsent(name, s -> new TaskInfo(name)).start();
    }
}

final class TaskInfo {

    private final String taskName;

    private final long startNanos;
    private       long currentStartNanos;
    private       long executeCount;
    private       long executeNanos;

    TaskInfo(String taskName) {
        this.taskName          = taskName;
        this.startNanos        = System.nanoTime();
        this.currentStartNanos = this.startNanos;
        this.executeCount      = 1;
    }

    /**
     * Get the name of this task.
     */
    public String getTaskName() {
        return this.taskName;
    }

    private static long nanosToMillis(long duration) {
        return TimeUnit.NANOSECONDS.toMillis(duration);
    }

    private static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }


    public void stop() {
        this.executeNanos += (System.nanoTime() - this.currentStartNanos);
        this.executeCount      = this.executeCount + 1;
        this.currentStartNanos = 0l;
    }

    public void start() {
        this.currentStartNanos = System.nanoTime();
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public long getExecuteNanos() {
        return executeNanos;
    }
    public long getExecuteSeconds() {
        return nanosToMillis(executeNanos);
    }
}