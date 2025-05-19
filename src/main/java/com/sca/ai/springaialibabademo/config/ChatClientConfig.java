package com.sca.ai.springaialibabademo.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.sca.ai.springaialibabademo.ChatClient.advisors.LogAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.sca.ai.springaialibabademo.ChatClient.advisors.LogAdvisor.LOG_SWITCH;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Configuration
@Slf4j
public class ChatClientConfig {
    @Bean
    ChatClient.Builder dashScopeChatClientBuilder(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel)
                .defaultAdvisors(a -> a.param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 4))
                .defaultAdvisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, "default"))
                .defaultAdvisors(a -> a.param(LOG_SWITCH, true));
    }

    @Bean
    ChatMemory dashScopeChatMemory() {
        return new InMemoryChatMemory();
    }

    @Bean
    MessageChatMemoryAdvisor dashScopeMessageChatMemoryAdvisor(ChatMemory openAiChatMemory) {
        return new MessageChatMemoryAdvisor(openAiChatMemory);
    }

    @Bean
    LogAdvisor openAiChatLogAdvisor() {
        return new LogAdvisor();
    }

}
