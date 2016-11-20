package cesi.com.tchatapp.model;


/**
 * The type Message.
 */
public class Message {

    private String username;
    private String msg;
    private long date;

    /**
     * Instantiates a new Message.
     *
     * @param username the username
     * @param message  the message
     * @param date     the date
     */
    public Message(String username, String message, long date) {
        this.username = username;
        this.msg = message;
        this.date = date;
    }

    /**
     * Gets username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets msg.
     *
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }


    /**
     * Gets date.
     *
     * @return the date
     */
    public long getDate() {
        return date;
    }

}
