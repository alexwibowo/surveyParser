package com.github.wibowo.survey.app;

import org.kohsuke.args4j.Option;

public final class ArgumentsBean implements Arguments{

    @Option(name = "--questionFile", usage = "Survey question file", required = true)
    private String questionFile;

    @Option(name = "--responseFile", usage = "Survey response file", required = true)
    private String responseFile;

    @Option(name = "--enableStreamingMode", usage = "Enable streaming mode")
    private boolean enableStreamingMode = false;

    @Override
    public String getQuestionFile() {
        return questionFile;
    }

    public ArgumentsBean setQuestionFile(String questionFile) {
        this.questionFile = questionFile;
        return this;
    }

    @Override
    public String getResponseFile() {
        return responseFile;
    }

    public ArgumentsBean setResponseFile(String responseFile) {
        this.responseFile = responseFile;
        return this;
    }

    @Override
    public boolean isEnableStreamingMode() {
        return enableStreamingMode;
    }

    public ArgumentsBean setEnableStreamingMode(boolean enableStreamingMode) {
        this.enableStreamingMode = enableStreamingMode;
        return this;
    }

    @Override
    public String toString() {
        return "ArgumentsBean{" +
                "questionFile='" + questionFile + '\'' +
                ", responseFile='" + responseFile + '\'' +
                ", enableStreamingMode=" + enableStreamingMode +
                '}';
    }
}
