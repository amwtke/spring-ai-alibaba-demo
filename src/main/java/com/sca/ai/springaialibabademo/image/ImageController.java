package com.sca.ai.springaialibabademo.image;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@RestController
public class ImageController {
    static private final FileSystemResource imageFile = new FileSystemResource("src/main/resources/multimodal.test.png");

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    private final DashScopeImageModel dashScopeImageModel;
    private final ChatClient.Builder dashScopeChatClientBuilder;

    ImageController(DashScopeImageModel dashScopeImageModel, ChatClient.Builder dashScopeChatClientBuilder) {
        this.dashScopeImageModel = dashScopeImageModel;
        this.dashScopeChatClientBuilder = dashScopeChatClientBuilder;
    }

    @GetMapping(value = "/ai/image")
    public String getImage(@RequestParam(value = "msg", defaultValue = "海边的龙虾") String msg) {
        log.info("image msg:{}", msg);
        return dashScopeImageModel.call(new ImagePrompt(msg, DashScopeImageOptions.builder()
//                        .withModel(DEFAULT_IMAGES_MODEL_NAME)
                        .withModel("wanx2.1-t2i-plus")
                        .withN(1)
                        .build()))
                .getResult().getOutput().getUrl();
    }

    @GetMapping("/ai/cc/multi")
    public String multimodality(@RequestParam(value = "msg", defaultValue = "在图片中你能看到什么？") String msg) {
        return dashScopeChatClientBuilder.build()
                .prompt(new Prompt(msg, DashScopeChatOptions.builder()
                        .withModel("qwen-vl-max-latest")
//                        .withModel("qwen-vl-plus")
                        .withMultiModel(true)
                        .build()))
                .user(u -> u.text(msg).media(MimeTypeUtils.IMAGE_PNG, imageFile)
                )
                .call().content();
    }

    @GetMapping("/ai/cc/multi_sdk")
    public String multimodality_sdk(@RequestParam(value = "msg", defaultValue = "在图片中你能看到什么？") String msg) throws NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
//                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
//                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"),
                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/rabbit.png"),
                        Collections.singletonMap("image", "/Users/xiaojin/workspace/demo/spring-ai-alibaba-demo/src/main/resources/multimodal.test.png"),
                        Collections.singletonMap("text", msg))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(apiKey)
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-vl-plus")
                .message(userMessage)
                .build();
        MultiModalConversationResult result = conv.call(param);
        return JsonUtils.toJson(result);
    }
}
