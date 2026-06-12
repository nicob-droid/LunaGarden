package io.github.nicobdroid.lunagarden;
import java.io.Serializable;

public class ResultVegItem implements Serializable {
    public final static int RESULT_VEG_ITEM_SOW = 0;
    public final static int RESULT_VEG_ITEM_COLLECT = 1;
    public final static int RESULT_VEG_ITEM_FORBIDDEN = 2;

    private String mainMessage;
    private String subMessage;
    private int pictureResId;
    private int action;

    /**
     * @return the pictureResId
     */
    public int getPictureResId() {
        return pictureResId;
    }

    /**
     * @param pictureResId
     *            the pictureResId to set
     */
    public void setPictureResId(int pictureResId) {
        this.pictureResId = pictureResId;
    }

    /**
     *
     * @return fruitname
     */

    public String getMainMessage() {
        return mainMessage;
    }

    public void setMainMessage(String mainMessage) {
        this.mainMessage = mainMessage;
    }

    public String getSubMessage() {
        return subMessage;
    }

    public void setSubMessage(String message) {
        this.subMessage = message;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

}

