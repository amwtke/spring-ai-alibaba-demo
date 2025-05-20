package com.sca.ai.springaialibabademo.image;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeImageProperties.DEFAULT_IMAGES_MODEL_NAME;

@Slf4j
@RestController
public class ImageController {
    private final DashScopeImageModel dashScopeImageModel;

    ImageController(DashScopeImageModel dashScopeImageModel) {
        this.dashScopeImageModel = dashScopeImageModel;
    }

    @GetMapping(value = "/ai/image")
    public String getImage(@RequestParam(value = "msg", defaultValue = "海边的龙虾") String msg) {
        log.info("image msg:{}", msg);
        return dashScopeImageModel.call(new ImagePrompt(msg, DashScopeImageOptions.builder()
                        .withModel(DEFAULT_IMAGES_MODEL_NAME)
                        .withN(1)
                        .build()))
                .getResult().getOutput().getUrl();
    }
}
