package com.filmland.utils;

import org.apache.commons.validator.routines.EmailValidator;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;

public class Helper {

    final private static String DATE_FORMAT = "dd-MM-yyyy";
    public static boolean isValidEmailAddress (String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPassword(String password) {

        // Check length
        // Check empty
        // Check contains matching criteria
        return true;
    }

    public static String encodeString(String password) {
        try {
            return new String(Base64.getEncoder().encode(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error encoding password, returning original value back!");
            return password;
        }
    }

    public static String decodeString(String password) {
        try {
            return new String(Base64.getDecoder().decode(password.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            System.out.println("Error decoding password, returning original value back!"+e.getMessage());
            return password;
        }
    }

    // Not familiar with schedulers so adding 1 month to payment date here before adding to DB
    // calculate payment date from subscription date
    public static Date calculatePaymentDate(Date subscriptionDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(subscriptionDate);
        calendar.add(Calendar.MONTH, 1);

        return calendar.getTime();
    }

    public static boolean isDatePassed(final Date date) {
        LocalDate currentDate = LocalDate.now();

        LocalDate dateInputted = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        return dateInputted.isBefore(currentDate) || dateInputted.isEqual((currentDate));
    }

//
//    public static void main(String[] args) {
//        String password = "Test@1234";
//        String encode = encodeString(password);
//        System.out.println("Encode = "+encode);
//        System.out.println("Decode = "+decodeString(encode));
//    }
}
