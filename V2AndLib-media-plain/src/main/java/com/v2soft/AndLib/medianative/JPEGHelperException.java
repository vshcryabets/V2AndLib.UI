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
        super("Err:"+code+ " "+getStringCode(code));
        mCode = code;
    }

    public static String getStringCode(int code) {
        switch (code) {
            case ERR_NO_SUCH_HANDLER:
                return "NO_SUCH_HANDLER";
            case ERR_SMALL_OUTPUT_BUFFER:
                return "SMALL_OUTPUT_BUFFER";
            case ERR_OPPERATION_SUCCESS:
                return "OPPERATION_SUCCESS";
            case ERR_WRONG_INPUT_BUFFER:
                return "WRONG_INPUT_BUFFER";
            case ERR_NULL_BUFFER:
                return "NULL_BUFFER";
            case ERR_CANT_GET_RESULT_CLASS:
                return "JNI ERR_CANT_GET_RESULT_CLASS";
            case ERR_NO_FILE:
                return "No input file";
            case ERR_JPEG_DECODER:
                return "Decoder/Encoder failed";
            case ERR_CANT_CREATE_FILE:
                return "Can't create file";
            case ERR_INCORRECT_GEOMETRY_PARAMETER:
                return "Wrong geometry parameter";
        }
        return "Unknown error";
    }

    public int getCode() {
        return mCode;
    }
}
