package com.palliative_care.model;

public class Message {

    String id = "";
    String message = "";
    String file = "";
    String fileType = "";
    String fromId = "";
    String toId = "";
    Boolean seen = false;
    long timestamp = 0;

    public Message() {
    }

    public Message(String id, String message, String file, String fileType,
                   String fromId, String toId, Boolean seen, long timestamp) {
        this.id = id;
        this.message = message;
        this.file = file;
        this.fileType = fileType;
        this.fromId = fromId;
        this.toId = toId;
        this.seen = seen;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
