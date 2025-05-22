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
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collections;

@Slf4j
@RestController
// 模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models 参考文档。
public class ImageController {
    static private final FileSystemResource imageFile = new FileSystemResource("src/main/resources/multimodal.test.png");
    static private final FileSystemResource audioFile = new FileSystemResource("src/main/resources/audio.mp3");

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;
    private final DashScopeImageModel dashScopeImageModel;
    private final ChatClient.Builder dashScopeChatClientBuilder;

    ImageController(DashScopeImageModel dashScopeImageModel, ChatClient.Builder dashScopeChatClientBuilder) {
        this.dashScopeImageModel = dashScopeImageModel;
        this.dashScopeChatClientBuilder = dashScopeChatClientBuilder;
    }

    @GetMapping(value = "/ai/image")
    public String getImage(@RequestParam(value = "msg", defaultValue = "火星上的城市，废土风格。") String msg) {
        log.info("image msg:{}", msg);
        return dashScopeImageModel.call(new ImagePrompt(msg, DashScopeImageOptions.builder()
//                        .withModel(DEFAULT_IMAGES_MODEL_NAME)
                        .withModel("wanx2.1-t2i-plus")
                        .withN(1)
                        .withWidth(1024)
                        .withHeight(1024)
                        .withNegativePrompt("白色与绿色")
                        .build()))
                .getResult().getOutput().getUrl();
    }

    //应该是程序的bug isStreamingToolFunctionCall 函数判断错误。chatCompletion.output() 有可能是null
    //audio不行，但是图文可以。
    @GetMapping(value = "/ai/cc/multi", produces = "text/html;charset=UTF-8")
    public Flux<String> multimodality(@RequestParam(value = "msg", defaultValue = "在这些文件中你能知道什么？") String msg) {
        return dashScopeChatClientBuilder.build()
                .prompt(new Prompt(msg, DashScopeChatOptions.builder()
                        .withModel("qwen-vl-max-latest")
//                        .withModel("qwen-omni-turbo")
//                        .withModel("qwen-audio-turbo-latest")
                        .withMultiModel(true)
                        .build()))
                .user(u -> u.text(msg)
//                              .media(MimeTypeUtils.IMAGE_PNG, imageFile)
//                                .media(MimeTypeUtils.ALL, UrlResource.from("https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"))
//                                .media(MimeTypeUtils.ALL, audioFile)
                                .media(MimeTypeUtils.ALL, imageFile)
                )
                .stream()
                .chatResponse().mapNotNull(r -> r.getResult()).mapNotNull(r -> r.getOutput()).mapNotNull(r -> r.getText());
    }

    //根据文档 必须是stream调用。才行！
    @GetMapping("/ai/cc/multi_sdk")
    public String multimodality_sdk(@RequestParam(value = "msg", defaultValue = "在图片中你能看到什么？") String msg) throws NoApiKeyException, UploadFileException {
        StringBuilder sb = new StringBuilder();
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(
//                        Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
                        Collections.singletonMap("audio", "/Users/xiaojin/workspace/demo/spring-ai-alibaba-demo/src/main/resources/audio.mp3"),
//                        Collections.singletonMap("audio", "https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250211/tixcef/cherry.wav"),
                        Collections.singletonMap("text", msg))).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(apiKey)
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
//                .model("qwen-vl-plus")
//                .model("qwen-audio-turbo-latest")
                .model("qwen-omni-turbo")
//                .modalities(List.of("audio"))
                .message(userMessage)
                .build();
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.flatMap(r -> Flowable.just(r.getOutput().getChoices()))
                .flatMap(Flowable::fromIterable)
                .doOnEach(r -> {
                    if (r.getValue() != null && r.getValue().getMessage() != null &&
                            r.getValue().getMessage().getContent() != null &&
                            r.getValue().getMessage().getContent().size() > 0) {
                        sb.append(r.getValue().getMessage().getContent().get(0).get("text"));
                    }
                })
                .blockingSubscribe();
        return sb.toString();
    }
}
