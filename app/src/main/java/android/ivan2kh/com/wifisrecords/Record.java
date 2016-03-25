package android.ivan2kh.com.wifisrecords;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by john on 3/16/2016.
 */
public class Record implements Serializable {
    private static final long serialVersionUID = -5542763198975959222L;

    private String comment = "";
    private int count = 0;
    private Date date;

    public Record(Date date, int count, String comment) {
        this.setDate(date);
        this.setComment(comment);
        this.setCount(count);
    }

    public Date getDate() {        return date;    }

    public void setDate(Date date) {        this.date = date;    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
