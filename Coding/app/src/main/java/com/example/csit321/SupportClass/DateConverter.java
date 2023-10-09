package com.example.csit321.SupportClass;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateConverter {
//    @TypeConverter
//    public static Date toDate (Long dateLong){
//        return dateLong == null ? null : new Date(dateLong);
//    }
//
//    @TypeConverter
//    public static Long fromDate (Date date){
//        return date == null ? null: date.getTime();
//    }
    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @TypeConverter
    public static Date stringToDate(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date value) {

        return value == null ? null : df.format(value);
    }
}
