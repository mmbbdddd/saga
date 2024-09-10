package cn.hz.ddbm.pc.newcore.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logs {
    public static Logger report = LoggerFactory.getLogger("report");
    public static Logger digest = LoggerFactory.getLogger("digest");
    public static Logger error  = LoggerFactory.getLogger("error");
    public static Logger flow   = LoggerFactory.getLogger("flow");
    public static Logger debug   = LoggerFactory.getLogger("debug");
    //    最高级别的异常 ：状态和会话处理异常，有可能导致状态不一致（极低概率）
    public static Logger status = LoggerFactory.getLogger("status");
}
