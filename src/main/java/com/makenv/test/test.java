package com.makenv.test;

import org.junit.Test;

/**
 * Created by Administrator on 2016/12/25.
 */
public class test {
    @Test
    public void test(){
        String s1 = new String("abc");
        String s2 = new String("abc");
        if (s1==s2){
            System.out.println(1);
        }
        if (s1.equals(s2)){
            System.out.println(2);
        }
    }
}
