package com.pnlorf.exception;

/**
 * 系统业务异常 throw new SystemException("XXXX")
 * <p>
 * Created by 冰诺莫语 on 2015/10/27.
 */
public class SystemException extends RuntimeException {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2332608236621015980L;

    private String code;

    public SystemException() {
    }

    public SystemException(String message) {
        super(message);
    }

    public SystemException(String code, String message) {
        super(message);
        this.code = code;
    }

    public SystemException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemException(Throwable cause) {
        super(cause);
    }

    public SystemException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
