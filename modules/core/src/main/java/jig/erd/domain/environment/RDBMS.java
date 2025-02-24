package jig.erd.domain.environment;

public enum RDBMS {
    H2("jdbc:h2:"),
    POSTGRESQL("jdbc:postgresql:");

    private String urlPrefix;

    RDBMS(String urlPrefix) {
        this.urlPrefix = urlPrefix;
    }

    public static RDBMS from(String url) {
        for (RDBMS value : values()) {
            if (url.startsWith(value.urlPrefix)) {
                return value;
            }
        }
        throw new UnsupportedOperationException("サポートしていないRDBMSです: " + url);
    }
}
