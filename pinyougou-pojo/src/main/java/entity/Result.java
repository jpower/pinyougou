package entity;

import java.io.Serializable;

/**
 * Created by 周大侠
 * 2018-12-01 20:05
 */
public class Result implements Serializable {

    private boolean success; // 是否成功

    public Result() {
    }

    private String message; // 返回信息

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
