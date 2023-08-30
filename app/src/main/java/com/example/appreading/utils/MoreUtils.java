package com.example.appreading.utils;

public class MoreUtils {

        public static <T> T coalesce(T one, T two)
        {
            return one != null ? one : two;
        }

}
