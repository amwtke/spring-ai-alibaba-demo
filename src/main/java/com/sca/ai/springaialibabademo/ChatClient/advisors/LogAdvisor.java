package com.sca.ai.springaialibabademo.ChatClient.advisors;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.*;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Slf4j
public class LogAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    public static final String LOG_SWITCH = "log_advisor_switcher";
    public volatile Boolean THE_2ND_SWITCHER = true;

    private final ReadWriteLock switcherLock = new ReentrantReadWriteLock();

    private AdvisedRequest before(AdvisedRequest request) {
        Optional<Object> switcher = Optional.ofNullable(request.adviseContext().get(LOG_SWITCH));
        switcherLock.readLock().lock();
        if ((Boolean) switcher.orElse(true) && THE_2ND_SWITCHER) {
            log.info("在AdvisedRequest中->{}", request.toString());
        }
        switcherLock.readLock().unlock();
        return request;
    }

    private AdvisedResponse after(AdvisedResponse advisedResponse) {
        Optional<Object> switcher = Optional.ofNullable(advisedResponse.adviseContext().get(LOG_SWITCH));
        switcherLock.readLock().lock();
        if ((Boolean) switcher.orElse(true) && THE_2ND_SWITCHER) {
            log.info("在advised Respone中->{}", advisedResponse.toString());
        }
        switcherLock.readLock().unlock();
        return advisedResponse;
    }

    private Flux<AdvisedResponse> after(Flux<AdvisedResponse> advisedResponseFlux) {
        return advisedResponseFlux.map(r -> {
            Optional<Object> switcher = Optional.ofNullable(r.adviseContext().get(LOG_SWITCH));
            switcherLock.readLock().lock();
            if ((Boolean) switcher.orElse(true) && THE_2ND_SWITCHER) {
                log.info("在异步advisedResponseFlux中->{}", r.toString());
            }
            switcherLock.readLock().unlock();
            return r;
        });
    }

    @Override
    @NonNull
    public AdvisedResponse aroundCall(@NonNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedRequest before = before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(before);
        return Objects.requireNonNull(after(advisedResponse));
    }

    @Override
    @NonNull
    public Flux<AdvisedResponse> aroundStream(@NonNull AdvisedRequest advisedRequest, @NonNull StreamAroundAdvisorChain chain) {
        AdvisedRequest before = before(advisedRequest);

        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(before);

        return after(advisedResponseFlux);
    }

    @Override
    public String getName() {
        return LogAdvisor.class.getName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public void switchLog(Boolean flag) {
        switcherLock.writeLock().lock();
        THE_2ND_SWITCHER = flag;
        switcherLock.writeLock().unlock();
    }

    public Boolean getSwitcher() {
        Boolean tmp;
        switcherLock.readLock().lock();
        tmp = THE_2ND_SWITCHER;
        switcherLock.readLock().unlock();
        return tmp;
    }
}
