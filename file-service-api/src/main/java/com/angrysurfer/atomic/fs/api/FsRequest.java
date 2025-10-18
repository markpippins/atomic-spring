package com.angrysurfer.atomic.fs.api;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class FsRequest {

    private String alias;
    private List<String> path;
    private String toAlias;
    private List<String> toPath;
    private String operation;
    private String filename;
    private String newName;

    public FsRequest(String alias, List<String> path, String string) {
        this.alias = alias;
        this.path = path;
        this.operation = string;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getPath() {
        return path;
    }

    public void setPath(List<String> path) {
        this.path = path;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getToAlias() {
        return toAlias;
    }

    public void setToAlias(String toAlias) {
        this.toAlias = toAlias;
    }

    public List<String> getToPath() {
        return toPath;
    }

    public void setToPath(List<String> toPath) {
        this.toPath = toPath;
    }

}
