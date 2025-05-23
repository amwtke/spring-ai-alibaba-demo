package com.sca.ai.springaialibabademo.rag;

import com.sca.ai.springaialibabademo.rag.service.EsVectorStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class RagController {
    private final ChatClient.Builder dashScopeChatClientBuilder;
    private final ElasticsearchVectorStore elasticsearchVectorStore;

    private final EsVectorStoreService esVectorStoreService;

    @Autowired
    private MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    private static final String SYS_MSG = """
            你是一位专业的室内设计顾问，精通各种装修风格、材料选择和空间布局。请基于提供的参考资料，为用户提供专业、详细且实用的建议。在回答时，请注意：
                                     1. 准确理解用户的具体需求"
                                     2. 结合参考资料中的实际案例
                                     3. 提供专业的设计理念和原理解释
                                     4. 考虑实用性、美观性和成本效益
                                     5. 如有需要，可以提供替代方案
            """;
    private static final String DEFAULT_PROMPT = "请提供几种推荐的装修风格?";

    RagController(ChatClient.Builder dashScopeChatClientBuilder, ElasticsearchVectorStore elasticsearchVectorStore, EsVectorStoreService esVectorStoreService) {
        this.dashScopeChatClientBuilder = dashScopeChatClientBuilder;
        this.elasticsearchVectorStore = elasticsearchVectorStore;
        this.esVectorStoreService = esVectorStoreService;
    }

    @GetMapping(value = "/ai/rag",produces = "text/html;charset=utf-8")
    public Flux<String> ragTest(@RequestParam(value = "msg", defaultValue = DEFAULT_PROMPT) String msg) {
        return dashScopeChatClientBuilder.defaultSystem(SYS_MSG)
                .build().prompt(msg)
                .advisors(messageChatMemoryAdvisor)
                .advisors(getRagAdvisor())
                .stream().content();
    }


    @GetMapping("/ai/rag/test")
    public String ragTest() {
        log.info("Data insert!");
        List<Document> documents = List.of(
                new Document("""
                        产品说明书:产品名称：智能机器人
                        产品描述：智能机器人是一个智能设备，能够自动完成各种任务。
                        功能：
                        1. 自动导航：机器人能够自动导航到指定位置。
                        2. 自动抓取：机器人能够自动抓取物品。
                        3. 自动放置：机器人能够自动放置物品。
                        """));

// Add the documents to Elasticsearch
        elasticsearchVectorStore.add(documents);

// Retrieve documents similar to a query
        List<Document> results = this.elasticsearchVectorStore.similaritySearch(SearchRequest.builder().query("机器人能够扫地吗？").topK(5).build());
        return results.stream().map(Document::getText).collect(Collectors.joining());
    }

    @GetMapping("/ai/rag/data")
    public void ragData() {
        log.info("Data insert!");
        esVectorStoreService.AddDocs();
    }

    private Advisor getRagAdvisor() {
        FilterExpressionBuilder b = new FilterExpressionBuilder();
//        var filterExpression = b.and(
//                b.and(
//                        b.eq("year", "2023"),         // 筛选2023年的案例
//                        b.eq("location", "indoor")),   // 仅选择室内案例
//                b.and(
//                        b.eq("type", "interior"),      // 类型为室内设计
//                        b.in("room", "living_room", "study", "kitchen")  // 指定房间类型
//                ));

//        var filterExpression = b.eq("year", "2023");
        return RetrievalAugmentationAdvisor.builder()
                // 配置查询增强器 注入documents的地方
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)        // 允许空上下文查询
                        .build())
                .queryExpander(MultiQueryExpander.builder().chatClientBuilder(dashScopeChatClientBuilder).build())
                // 配置文档检索器
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(elasticsearchVectorStore)
//                        .similarityThreshold(0.5)       // 相似度阈值
                        .topK(3)                        // 返回文档数量
//                        .filterExpression(filterExpression.build())     // 文档过滤表达式
                        .build())
                .build();
    }
}
