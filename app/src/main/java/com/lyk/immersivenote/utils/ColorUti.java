package com.lyk.immersivenote.utils;

/**
 * Created by John on 2015/8/31.
 */
public class ColorUti {

    public static String getHexStringForColor(int color){
        int first = color/16;
        int second = color%16;
        return getSingleNumberHex(first)+getSingleNumberHex(second);

    }

    public static  String getSingleNumberHex(int number){
        String forReturn = null;
        if(number > 9){
            switch(number){
                case 10:
                    forReturn = "A";
                    break;
                case 11:
                    forReturn = "B";
                    break;
                case 12:
                    forReturn = "C";
                    break;
                case 13:
                    forReturn = "D";
                    break;
                case 14:
                    forReturn = "E";
                    break;
                case 15:
                    forReturn = "F";
                    break;
            }
        }
        else{
            forReturn = String.valueOf(number);
        }
        return forReturn;
    }

}
