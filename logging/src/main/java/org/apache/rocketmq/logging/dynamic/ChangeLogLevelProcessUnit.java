package org.apache.rocketmq.logging.dynamic;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

import static org.apache.rocketmq.logging.dynamic.LogConstant.LOGGER_NOT_EXSIT;
import static org.apache.rocketmq.logging.dynamic.LogConstant.LOGGER_TYPE_UNKNOWN;
import static org.apache.rocketmq.logging.dynamic.LogConstant.PARAMETER_TYPE_ERROR;

/**
 * 日志级别动态调整
 */
public class ChangeLogLevelProcessUnit extends AbstractProcessUnitImpl {
    private final Logger log = LoggerFactory.getLogger(ChangeLogLevelProcessUnit.class);
    
    @Override
    public String setLogLevel(String logLevel) {
        // 如果为NULL就是默认等级
        if (null == logLevel) {
            logLevel = defaultLevel;
        }
        
        log.info("[LoggerLevel]设置所有Log级别");
        if (null == loggerMap || loggerMap.isEmpty()) {
            log.warn("[LoggerLevel]当前工程中不存在任何Logger,无法调整Logger级别");
            return "";
        }
        Set<Map.Entry<String, Object>> entries = loggerMap.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            Object logger = entry.getValue();
            if (null == logger) {
                throw new RuntimeException(LOGGER_NOT_EXSIT);
            }
            if (logFrameworkType == LogFrameworkType.LOG4J) {
                org.apache.log4j.Logger targetLogger = (org.apache.log4j.Logger) logger;
                org.apache.log4j.Level targetLevel = org.apache.log4j.Level.toLevel(logLevel);
                targetLogger.setLevel(targetLevel);
            } else if (logFrameworkType == LogFrameworkType.LOGBACK) {
                ch.qos.logback.classic.Logger targetLogger = (ch.qos.logback.classic.Logger) logger;
                ch.qos.logback.classic.Level targetLevel = ch.qos.logback.classic.Level.toLevel(logLevel);
                targetLogger.setLevel(targetLevel);
            } else if (logFrameworkType == LogFrameworkType.LOG4J2) {
                org.apache.logging.log4j.core.config.LoggerConfig loggerConfig = (org.apache.logging.log4j.core.config.LoggerConfig) logger;
                org.apache.logging.log4j.Level targetLevel = org.apache.logging.log4j.Level.toLevel(logLevel);
                loggerConfig.setLevel(targetLevel);
                org.apache.logging.log4j.core.LoggerContext ctx = (org.apache.logging.log4j.core.LoggerContext) org.apache.logging.log4j.LogManager
                    .getContext(false);
                ctx.updateLoggers(); // This causes all Loggers to refetch information from their LoggerConfig.
            } else {
                throw new RuntimeException(LOGGER_TYPE_UNKNOWN);
            }
        }
        return "success";
    }
    
    @Override
    public String setLogLevel(JSONArray data) {
        return PARAMETER_TYPE_ERROR;
    }
    
}
