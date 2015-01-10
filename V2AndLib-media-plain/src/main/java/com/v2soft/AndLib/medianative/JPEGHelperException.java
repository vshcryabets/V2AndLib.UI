package com.v2soft.AndLib.medianative;

/**
 * @author V.Shcryabets (vshcryabets@gmail.com)
 */
public class JPEGHelperException extends Exception {
    public static final int ERR_NO_SUCH_HANDLER = -1012;
    public static final int ERR_SMALL_OUTPUT_BUFFER = -1013;
    public static final int ERR_OPPERATION_SUCCESS = -1014;
    public static final int ERR_WRONG_INPUT_BUFFER = -1015;
    public static final int ERR_NULL_BUFFER = -1016;
    public static final int ERR_CANT_GET_RESULT_CLASS = -1017;
    public static final int ERR_NO_FILE = -1018;
    public static final int ERR_JPEG_DECODER = -1019;
    public static final int ERR_CANT_CREATE_FILE = -1020;
    public static final int ERR_INCORRECT_GEOMETRY_PARAMETER = -1021;

    protected int mCode;

    public JPEGHelperException(int code) {
        super("Err:"+code);
        mCode = code;
    }

    public int getCode() {
        return mCode;
    }
}
