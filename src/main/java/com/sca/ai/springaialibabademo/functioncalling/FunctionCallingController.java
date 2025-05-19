package com.sca.ai.springaialibabademo.functioncalling;

import com.sca.ai.springaialibabademo.ChatClient.advisors.LogAdvisor;
import com.sca.ai.springaialibabademo.functioncalling.Tools.AdvisorLogSwitchTool;
import com.sca.ai.springaialibabademo.functioncalling.Tools.DateTimeTool;
import com.sca.ai.springaialibabademo.functioncalling.Tools.WeatherLocationForDaysTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FunctionCallingController {
    private final ChatClient.Builder dashScopeChatClientBuilder;
    private final LogAdvisor logAdvisor;

    FunctionCallingController(ChatClient.Builder dashScopeChatClientBuilder, LogAdvisor logAdvisor) {
        this.logAdvisor = logAdvisor;
        this.dashScopeChatClientBuilder = dashScopeChatClientBuilder;
    }

    @RequestMapping(value = "/ai/tool", produces = "text/html;charset=utf-8")
    public String testToolCalling(@RequestParam(value = "msg", defaultValue = "现在几点了？") String msg) {
        log.info("msg = {}", msg);
        return dashScopeChatClientBuilder.build()
                .prompt(msg)
                .tools(new DateTimeTool(), new WeatherLocationForDaysTool(), new AdvisorLogSwitchTool(logAdvisor))
                .advisors(logAdvisor)
                .call()
                .content();
    }
}
