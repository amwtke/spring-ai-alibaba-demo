package com.sca.ai.springaialibabademo.audio;

import com.alibaba.cloud.ai.dashscope.api.DashScopeSpeechSynthesisApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AudioController {
    private final DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel;

    AudioController(DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel) {
        this.dashScopeSpeechSynthesisModel = dashScopeSpeechSynthesisModel;
    }

    @RequestMapping(value = "/ai/audio", produces = "audio/mpeg3")
    public byte[] text2Audio(@RequestParam(value = "msg", defaultValue = "你好大模型！") String msg) {
        log.info("语音:{}", msg);
        return dashScopeSpeechSynthesisModel.call(new SpeechSynthesisPrompt(msg
                        , DashScopeSpeechSynthesisOptions.builder()
                        .withVoice("longcheng")
                        .withResponseFormat(DashScopeSpeechSynthesisApi.ResponseFormat.MP3)
                        .build()
                ))
                .getResult().getOutput().getAudio().array();
    }
}
