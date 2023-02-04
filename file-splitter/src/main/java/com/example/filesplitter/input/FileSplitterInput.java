package com.example.filesplitter.input;

public class FileSplitterInput {

    private String sourcePath;
    private Long lineSeperatorNumber;
    private String destinationPath;


    public String getSourcePath() {
        return sourcePath;
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public Long getLineSeperatorNumber() {
        return lineSeperatorNumber;
    }

    public void setLineSeperatorNumber(Long lineSeperatorNumber) {
        this.lineSeperatorNumber = lineSeperatorNumber;
    }

    public String getDestinationPath() {
        return destinationPath;
    }

    public void setDestinationPath(String destinationPath) {
        this.destinationPath = destinationPath;
    }

}
