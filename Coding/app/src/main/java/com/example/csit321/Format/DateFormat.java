package com.example.csit321.Format;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateFormat {

    public Date StringToDate1 (String sdate) throws ParseException //format dd/mm/yyyy
    {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(sdate);
    }

    public String dateToString1 (Date date) throws ParseException //format dd/mm/yyyy
    {
        return new SimpleDateFormat("HH:mm:ss dd-MM-yyyy").format(date);
    }

    public String dateToString2 (Date date) throws ParseException //format dd/mm/yyyy
    {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

}
