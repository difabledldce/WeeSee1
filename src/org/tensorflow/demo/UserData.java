package org.tensorflow.demo;

public class UserData {
    String msgUserName;
    String msgBody;
    String link;
    String subID;
    long timestamp;
    int newState;
    String msgUID;

    public UserData(String msgUserName, String msgBody, String link, String subID, long timestamp, int newState, String msgUID) {
        this.msgUserName = msgUserName;
        this.msgBody = msgBody;
        this.link = link;
        this.subID = subID;
        this.timestamp = timestamp;
        this.newState = newState;
        this.msgUID = msgUID;
    }


    public String getMsgUserName() {
        return msgUserName;
    }

    public void setMsgUserName(String msgUserName) {
        this.msgUserName = msgUserName;
    }

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public UserData(String msgUserName, String msgBody, Long timeStamp, long timestamp, String subID, String link) {
        this.msgUserName = msgUserName;
        this.msgBody = msgBody;
        this.timestamp = timestamp;
        this.link=link;
    }
}
