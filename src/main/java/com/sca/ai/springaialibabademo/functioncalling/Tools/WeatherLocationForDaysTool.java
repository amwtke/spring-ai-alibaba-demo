package com.sca.ai.springaialibabademo.functioncalling.Tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;

public class WeatherLocationForDaysTool {
    private static final Logger logger = LoggerFactory.getLogger(WeatherLocationForDaysTool.class);

    @Tool(description = "获取未来n天的天气服务，输入参数：地址与未来n天，今天n=0；明天n=1")
    public String WeatherLocationForDaysFunction(String location, int days) {
        logger.info("模型返回,地点：{},n天:{}", location, days);
        if (days == 0) {
            return "天气晴，气温30度";
        }
        if (location.equals("武汉")) {
            return "天气晴转多云，气温20度";
        }
        return "天气雨，气温18度";
    }
}
