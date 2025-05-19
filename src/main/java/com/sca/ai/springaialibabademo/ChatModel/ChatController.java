package com.sca.ai.springaialibabademo.ChatModel;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController()
@Slf4j
public class ChatController {
    private final DashScopeChatModel dashScopeChatModel;

    public ChatController(DashScopeChatModel dashScopeChatModel) {
        this.dashScopeChatModel = dashScopeChatModel;
    }

    @RequestMapping("/ai/chat")
    public String gen(@RequestParam(value = "user_msg", defaultValue = "讲个笑话") String userMsg,
                      @RequestParam(value = "sys_msg", required = false) String sysMsg,
                      @RequestParam(value = "t", required = false) Double tempo) {

        List<Message> messages = Strings.isBlank(sysMsg) ? List.of(new UserMessage(userMsg)) :
                Arrays.asList(new UserMessage(userMsg), new SystemMessage(sysMsg));
        log.info("user_msg:{},sys_msg:{},t:{}", userMsg, sysMsg, tempo);
        return dashScopeChatModel.call(new Prompt(messages, DashScopeChatOptions.builder()
                .withTemperature(tempo).build())
        ).getResult().getOutput().getText();
    }

    @RequestMapping(value = "/ai/chat-stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> gen_stream(@RequestParam(value = "user_msg", defaultValue = "讲个笑话") String userMsg,
                                   @RequestParam(value = "sys_msg", required = false) String sysMsg,
                                   @RequestParam(value = "t", required = false) Double tempo) {

        List<Message> messages = Strings.isBlank(sysMsg) ? List.of(new UserMessage(userMsg)) :
                Arrays.asList(new UserMessage(userMsg), new SystemMessage(sysMsg));
        log.info("user_msg:{},sys_msg:{},t:{}", userMsg, sysMsg, tempo);
        return dashScopeChatModel.stream(new Prompt(messages, DashScopeChatOptions.builder()
                        .withTemperature(tempo).build()))
                .mapNotNull(ChatResponse::getResult)
                .mapNotNull(Generation::getOutput)
                .mapNotNull(AbstractMessage::getText);
    }
}
