package com.sca.ai.springaialibabademo.functioncalling.Tools;

import com.sca.ai.springaialibabademo.ChatClient.advisors.LogAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

@Slf4j
public class AdvisorLogSwitchTool {
    private final LogAdvisor logAdvisor;

    public AdvisorLogSwitchTool(LogAdvisor logAdvisor) {
        this.logAdvisor = logAdvisor;
    }

    @Tool(description = "设置Advisor log的开关，flag是开关的值，打开是true，关闭是false。设置成功返回true，否则返回false。")
    Boolean setLogAdvisorSwitcher(@ToolParam(description = "设置开关的值参数") Boolean flag) {
        log.info("设置logadvisor：{}", flag);
        logAdvisor.switchLog(flag);
        return true;
    }
}
