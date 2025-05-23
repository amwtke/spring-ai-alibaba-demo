package com.sca.ai.springaialibabademo.config;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingProperties;
import com.alibaba.cloud.ai.autoconfigure.dashscope.ResolvedConnectionProperties;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingModel;
import com.alibaba.cloud.ai.dashscope.embedding.DashScopeEmbeddingOptions;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.ai.embedding.observation.EmbeddingModelObservationConvention;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeConnectionUtils.resolveConnectionProperties;

@Configuration
public class EmbeddingsConfig {

    @Bean
    public DashScopeEmbeddingModel dashscopeEmbeddingModel(
            DashScopeConnectionProperties commonProperties,
            DashScopeEmbeddingProperties embeddingProperties,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            RetryTemplate retryTemplate,
            ResponseErrorHandler responseErrorHandler,
            ObjectProvider<ObservationRegistry> observationRegistry,
            ObjectProvider<EmbeddingModelObservationConvention> observationConvention
    ) {

        var dashScopeApi = dashscopeEmbeddingApi(
                commonProperties,
                embeddingProperties,
                restClientBuilder,
                webClientBuilder,
                responseErrorHandler
        );

        var embeddingModel = new DashScopeEmbeddingModel(
                dashScopeApi,
                embeddingProperties.getMetadataMode(),
                DashScopeEmbeddingOptions.builder().withModel("text-embedding-v3").withDimensions(512).build(),
                retryTemplate,
                observationRegistry.getIfUnique(() -> ObservationRegistry.NOOP)
        );
        observationConvention.ifAvailable(embeddingModel::setObservationConvention);

        return embeddingModel;
    }

    private DashScopeApi dashscopeEmbeddingApi(
            DashScopeConnectionProperties commonProperties,
            DashScopeEmbeddingProperties embeddingProperties,
            RestClient.Builder restClientBuilder,
            WebClient.Builder webClientBuilder,
            ResponseErrorHandler responseErrorHandler
    ) {
        ResolvedConnectionProperties resolved = resolveConnectionProperties(commonProperties, embeddingProperties,
                "embedding");

        return new DashScopeApi(resolved.baseUrl(), resolved.apiKey(), resolved.workspaceId(), restClientBuilder,
                webClientBuilder, responseErrorHandler);
    }
}
