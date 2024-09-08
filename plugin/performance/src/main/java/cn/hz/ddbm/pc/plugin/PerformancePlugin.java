package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.log.Logs;
import org.springframework.context.ApplicationListener;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


public class PerformancePlugin extends Plugin implements ApplicationListener<PerformancePlugin.Event> {
    StopWatch sw;

    public PerformancePlugin() {
        this.sw = new StopWatch();
    }

    @Override
    public String code() {
        return "performance";
    }

    @Override
    public void preAction(FlowContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FlowContext ctx) {

    }

    @Override
    public void errorAction(State preNode, Exception e, FlowContext ctx) {

    }

    @Override
    public void finallyAction(State preNode,FlowContext ctx) {

    }
//
//    @Override
//    public void preAction(FsmContext ctx) {
//        sw.start(ctx.getAction().beanName());
//    }
//
//    @Override
//    public void postAction(State lastNode, FsmContext ctx) {
//
//    }
//
//    @Override
//    public void onActionException(State preNode, Exception e, FsmContext ctx) {
//
//    }
//
//    @Override
//    public void onActionFinally(FsmContext ctx) {
//        sw.stop(ctx.getAction().beanName());
//    }
//

    public void printReport() {
//        try {
//            Thread.sleep(1000l);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        sw.prettyPrint();
    }


//    @Override
//    public void onActionFinally(String name, FsmContext ctx) {
//        sw.stop(name);
//    }
//
//    @Override
//    public void onActionException(String actionName, Enum preNode, Exception e, FsmContext ctx) {
//
//    }
//
//    @Override
//    public void postAction(String name, Enum lastNode, FsmContext ctx) {
//
//    }
//
//    @Override
//    public void preAction(String name, FsmContext ctx) {
//        sw.start(name);
//    }

    @Override
    public void onApplicationEvent(Event event) {
        printReport();
    }

    public static class Event extends org.springframework.context.ApplicationEvent {

        public Event() {
            super("1");
        }
    }
}

final class StopWatch {
    private final ConcurrentMap<String, TaskInfo> tasks;

    public StopWatch() {
        this.tasks = new ConcurrentHashMap<>();
    }

    public void prettyPrint() {
        Logs.report.info("性能统计报表");
        Logs.report.info("action             micros             times");
        for (TaskInfo task : tasks.values()) {
            Logs.report.info("{}             {}             {}", task.getTaskName(), task.getExecuteMicros(), task.getExecuteCount());
        }
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
    private final long   startNanos;
    private       long   currentStartNanos;
    private       long   executeCount;
    private       long   executeNanos;

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


    public void stop() {
        this.executeNanos += (System.nanoTime() - this.currentStartNanos);
        this.executeCount = this.executeCount + 1;
//        this.currentStartNanos = 0l;
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
        return TimeUnit.NANOSECONDS.toSeconds(executeNanos);
    }

    public Long getExecuteMillis() {
        return TimeUnit.NANOSECONDS.toMillis(executeNanos);
    }

    public Long getExecuteMicros() {
        return TimeUnit.NANOSECONDS.toMicros(executeNanos);
    }


}