package com.sca.ai.springaialibabademo.functioncalling.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeTool {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeTool.class);

    @Tool(description = "Get the current date and time in the user's timezone")
    String getCurrentDateTime() {
        logger.info("大模型调了时间工具类！");
        return LocalDateTime.now().atZone(LocaleContextHolder.getTimeZone().toZoneId()).toString();
    }

    @Tool(description = "Set a user alarm for the given time, provided in ISO-8601 format, set time string returned")
    String setAlarm(@ToolParam(description = "参数类型是ISO-8601") String time) {
        logger.info("Alarm set for :{}.", time);
        LocalDateTime alarmTime = LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME);
        return alarmTime.toString();
    }
}
