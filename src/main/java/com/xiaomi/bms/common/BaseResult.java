package com.xiaomi.bms.common;

import com.xiaomi.bms.constant.ResultConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhuzhe1018
 * @date 2024/6/16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResult<T> implements Serializable {
    private int status;
    private String msg;
    private T data;
    private static final long serialVersionUID = 1L;

    public BaseResult(int status, String msg) {
        this.status=status;
        this.msg=msg;
    }

    public static <T> BaseResult<T> isSuccess() {
        return new BaseResult<>(ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MESSAGE);
    }
    public static <T> BaseResult<T> isSuccess(T data) {
        return new BaseResult<>(ResultConstant.SUCCESS_CODE, ResultConstant.SUCCESS_MESSAGE,data);
    }
    public static <T> BaseResult<T> isError(String msg) {
        return new BaseResult<>(ResultConstant.ERROR_CODE, msg);
    }
}
