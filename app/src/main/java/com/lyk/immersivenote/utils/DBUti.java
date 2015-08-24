package com.lyk.immersivenote.utils;

/**
 * Created by John on 2015/8/24.
 */
public class DBUti {
    public static String getTableNameById(Long id){
        return "table"+id;
    }
    public static String getTableNameById(int id){
        return "table"+id;
    }
}
