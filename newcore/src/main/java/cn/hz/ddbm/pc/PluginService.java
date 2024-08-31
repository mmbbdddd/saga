package cn.hz.ddbm.pc;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.util.ArrayList;
import java.util.List;

public class PluginService {
    List<Plugin> processorPlugins;

    public PluginService(List<Plugin> processorPlugins) {
        this.processorPlugins = processorPlugins;
    }

    public void pre(FlowContext ctx) {
        List<Plugin> plugins = ctx.getProfile().getPlugins();
        plugins.addAll(processorPlugins);
        plugins.forEach((plugin) -> {
            try {
                plugin.preAction(ctx);
            } catch (Exception e) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
            }
        });
    }

    public void post(State lastNode, FlowContext ctx) {
        List<Plugin> plugins = ctx.getProfile().getPlugins();
        plugins.addAll(processorPlugins);
        plugins.forEach((plugin) -> {
            try {
                plugin.postAction(lastNode, ctx);
            } catch (Exception e) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
            }
        });
    }

    public void error(State preNode, Exception e, FlowContext ctx) {
        List<Plugin> plugins = ctx.getProfile().getPlugins();
        plugins.addAll(processorPlugins);
        plugins.forEach((plugin) -> {
            try {
                plugin.errorAction(preNode, e, ctx);
            } catch (Exception e2) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e2);
            }
        });
    }

    public void _finally(FlowContext ctx) {
        List<Plugin> plugins = ctx.getProfile().getPlugins();
        plugins.addAll(processorPlugins);
        plugins.forEach((plugin) -> {
            try {
                plugin.finallyAction(ctx);
            } catch (Exception e) {
                Logs.error.error("{},{}", ctx.getFlow().getName(), ctx.getId(), e);
            }
        });
    }
}
