package cn.hz.ddbm.pc.core.exception;

/**
 * 工作模式是连续的。
 * 1，如果无异常，继续下一个节点
 * 2，如果有异常，判断规则如下
 * 中断流程（外部io异常，网络等动等）执行fluent策略连续，触发：                        IOException
 * 中断流程除（内部错误：不可重复执行，执行次数受限……）再次调度可触发：                  InterruptedFlowException
 * 中断流程（内部程序错误：配置错误，代码错误）再次调度不响应：                         PauseFlowException/IllegalEntityException/IllegalFunctionException
 */