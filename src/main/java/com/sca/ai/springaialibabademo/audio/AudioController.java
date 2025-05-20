package com.sca.ai.springaialibabademo.audio;

import com.alibaba.cloud.ai.dashscope.api.DashScopeSpeechSynthesisApi;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeAudioTranscriptionOptions;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.alibaba.dashscope.audio.asr.vocabulary.Vocabulary;
import com.alibaba.dashscope.audio.asr.vocabulary.VocabularyService;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscription;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class AudioController {
    static private final FileSystemResource audioFile = new FileSystemResource("src/main/resources/audio.mp3");
    private final DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel;
    private final DashScopeAudioTranscriptionModel dashScopeAudioTranscriptionModel;

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    AudioController(DashScopeSpeechSynthesisModel dashScopeSpeechSynthesisModel, DashScopeAudioTranscriptionModel dashScopeAudioTranscriptionModel) {
        this.dashScopeSpeechSynthesisModel = dashScopeSpeechSynthesisModel;
        this.dashScopeAudioTranscriptionModel = dashScopeAudioTranscriptionModel;
    }

    @RequestMapping(value = "/ai/audio", produces = "audio/mpeg3")
    public byte[] text2Audio(@RequestParam(value = "msg", defaultValue = "阙里人家是吴贻弓执导的电视剧") String msg) {
        log.info("语音:{}", msg);
        return dashScopeSpeechSynthesisModel.call(new SpeechSynthesisPrompt(msg
                        , DashScopeSpeechSynthesisOptions.builder()
                        .withVoice("longcheng")
                        .withResponseFormat(DashScopeSpeechSynthesisApi.ResponseFormat.MP3)
                        .build()
                ))
                .getResult().getOutput().getAudio().array();
    }

    @RequestMapping("/ai/audio/tran")
    public String audioToText() {
        return dashScopeAudioTranscriptionModel.call(new AudioTranscriptionPrompt(audioFile
                                , DashScopeAudioTranscriptionOptions.builder()
//                                .withModel("paraformer-realtime-v2")
                                .withSampleRate(48000)
                                .withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.MP3)
//                        .withVocabularyId("vocab-prefix-cf7935da7aec44c5bff89cf725392393")
                                .withLanguageHints(List.of("zh"))
                                .build()
                        )
                )
                .getResult()
                .getOutput();
    }

    @RequestMapping("/ai/audio/stran")
    public Flux<String> audioToTextStream() {
        return dashScopeAudioTranscriptionModel.stream(new AudioTranscriptionPrompt(audioFile
                                , DashScopeAudioTranscriptionOptions.builder()
//                                .withModel("paraformer-realtime-v2")
                                .withSampleRate(48000)
                                .withFormat(DashScopeAudioTranscriptionOptions.AudioFormat.MP3)
                                .withLanguageHints(List.of("zh"))
                                .build()
                        )
                )
                .mapNotNull(AudioTranscriptionResponse::getResult)
                .mapNotNull(AudioTranscription::getOutput);
    }

    @RequestMapping("/ai/audio/tran_sdk")
    public String audioToTextSdk() {
        Recognition recognizer = new Recognition();
        RecognitionParam param =
                RecognitionParam.builder()
                        .model("paraformer-realtime-v2")
                        .format("mp3")
                        .sampleRate(48000)
                        .apiKey(apiKey)
                        .vocabularyId("vocab-prefix-cf7935da7aec44c5bff89cf725392393")
                        // “language_hints”只支持paraformer-v2和paraformer-realtime-v2模型
                        .parameter("language_hints", new String[]{"zh"})
                        .build();
        return recognizer.call(param, audioFile.getFile());
    }

    @GetMapping("/ai/audio/hot_word")
    public String createHotWord(@RequestParam(value = "hw", defaultValue = "阙里人家") String hotWord) throws NoApiKeyException, InputRequiredException {
        JsonArray vocabulary = new JsonArray();
        List<HotWord> wordList = new ArrayList<>();
        wordList.add(new HotWord("吴贻弓", 4, "zh"));
        wordList.add(new HotWord("阙里人家", 4, "zh"));
        wordList.add(new HotWord(hotWord, 4, "zh"));

        for (HotWord word : wordList) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("text", word.text);
            jsonObject.addProperty("weight", word.weight);
            jsonObject.addProperty("lang", word.lang);
            vocabulary.add(jsonObject);
        }
        // create vocabulary
        VocabularyService service = new VocabularyService(apiKey);
        Vocabulary myVoc = service.createVocabulary("paraformer-v2", "prefix", vocabulary);
        log.info("your vocabulary id is:{} ", myVoc.getVocabularyId());
        return myVoc.getVocabularyId();
    }

    @Data
    @AllArgsConstructor
    class HotWord {
        private String text;
        private int weight;
        String lang;
    }
}
