package com.pandaq.sample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by huxinyu on 2020/5/14.
 * Email : panda.h@foxmail.com
 * Description :
 */
public class Testa {

    public static void main(String[] args) {
        findTableware();
    }

    public static List<String> findTableware() {
        int size = 0;
        List tableWares = Arrays.asList("bowl", "knife", "fork", "chopsticks");
        List<String> result = new ArrayList<>();
        Scanner scan = new Scanner(System.in);
        if (scan.hasNextLine()) {
            String sizeStr = scan.nextLine();
            try {
                size = Integer.parseInt(sizeStr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (int i = 0; i < size; i++) {
                String input = scan.nextLine();
                if (tableWares.contains(input)) {
                    result.add(input);
                }
            }
        }
        System.out.println(result);
        return result;
    }

}
