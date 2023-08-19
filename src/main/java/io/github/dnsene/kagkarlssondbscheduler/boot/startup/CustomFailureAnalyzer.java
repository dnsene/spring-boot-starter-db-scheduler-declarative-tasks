package io.github.dnsene.kagkarlssondbscheduler.boot.startup;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

public class CustomFailureAnalyzer extends AbstractFailureAnalyzer<StartupException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, StartupException cause) {
        return new FailureAnalysis(cause.getOrigin(), cause.getAction(), rootFailure);
    }
}
