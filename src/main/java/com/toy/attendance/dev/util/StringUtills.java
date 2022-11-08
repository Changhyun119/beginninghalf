package com.toy.attendance.dev.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StringUtills {
    

    public static boolean canBeInt(String checkStr) throws Exception {
        try {
            Integer.valueOf(checkStr);
            return true;
        }
        catch(Exception e) {
            return false; 
        }
        
    }

    
}
