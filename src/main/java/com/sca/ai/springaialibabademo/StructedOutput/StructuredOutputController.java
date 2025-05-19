package com.sca.ai.springaialibabademo.StructedOutput;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sca.ai.springaialibabademo.ChatClient.advisors.LogAdvisor;
import com.sca.ai.springaialibabademo.StructedOutput.models.ActorFiles;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StructuredOutputController {

    private final ChatClient.Builder openAiChatClientBuilder;
    private final LogAdvisor logAdvisor;

    StructuredOutputController(ChatClient.Builder openAiChatClientBuilder, LogAdvisor logAdvisor) {
        this.openAiChatClientBuilder = openAiChatClientBuilder;
        this.logAdvisor = logAdvisor;
    }

    @GetMapping(value = "/ai/cc/people_works", produces = "text/json;charset=utf-8")
    public String genStructuredOutput(@RequestParam(value = "name", defaultValue = "tom hanks") String msg,
                                      @RequestParam(value = "nr", defaultValue = "5") Integer nr) {
        ActorFiles actor = openAiChatClientBuilder.build().prompt()
                .user(u -> u.text("生成{people}的{nr}部作品结果。")
                        .param("people", msg)
                        .param("nr", nr)
                )
                .advisors(logAdvisor)
                .call()
                .entity(ActorFiles.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(actor);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
