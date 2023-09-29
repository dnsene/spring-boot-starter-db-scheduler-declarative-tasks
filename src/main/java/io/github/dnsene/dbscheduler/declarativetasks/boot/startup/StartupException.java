package io.github.dnsene.dbscheduler.declarativetasks.boot.startup;


public class StartupException extends RuntimeException {

    private final String origin;
    private final String action;

    public StartupException(String origin, String action) {
        this.origin = origin;
        this.action = action;
    }


    public String getOrigin() {
        return origin;
    }

    public String getAction() {
        return action;
    }
}
