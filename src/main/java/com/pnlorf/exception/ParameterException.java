package com.pnlorf.exception;

/**
 * 自定义异常处理，描述类.. throw new ParameterException("XXXX")
 * <p>
 * Created by 冰诺莫语 on 2015/10/27.
 */
public class ParameterException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6417641452178955756L;

    public ParameterException() {
    }

    public ParameterException(String message) {
        super(message);
    }

    public ParameterException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParameterException(Throwable cause) {
        super(cause);
    }
    
}
