package io.github.nicobdroid.lunagarden.settings;
import java.io.Serializable;

public class VegItem implements Serializable {

    private String preferenceKey;
    private String fruitname;
    private String message;
    private int pictureResId;
    private boolean isCheckboxChecked;

    public String getPreferenceKey() {
        return preferenceKey;
    }

    public void setPreferenceKey(String preferenceKey) {
        this.preferenceKey = preferenceKey;
    }

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

    public String getFruitname() {
        return fruitname;
    }

    public void setFruitname(String fruitname) {
        this.fruitname = fruitname;
    }

    /**
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Method to check the check box is checked or not.
     *
     * @return isCheckboxChecked
     */
    public boolean isCheckboxChecked() {
        return isCheckboxChecked;
    }

    public void setCheckboxChecked(boolean isCheckboxChecked) {
        this.isCheckboxChecked = isCheckboxChecked;
    }

}

