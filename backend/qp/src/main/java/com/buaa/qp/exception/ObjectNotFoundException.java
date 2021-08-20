package com.buaa.qp.exception;

public class ObjectNotFoundException extends Exception{
    @Override
    public String toString() {
        return "未找到相关对象";
    }
}