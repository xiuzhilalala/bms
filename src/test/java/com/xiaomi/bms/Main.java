package com.xiaomi.bms;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        BigDecimal a = BigDecimal.valueOf(12.00);
        BigDecimal b= null;
        if(b==null){
            System.out.println(1);
        }
        System.out.println(a.compareTo(null));
    }


}





