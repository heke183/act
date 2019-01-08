package com.xianglin.act.common.util;

/**
 * @author yefei
 * @date 2018-01-24 14:10
 */
public class GoldSequenceUtil {

    private final static int GOLD_SEQUENCE_LENGTH = 32;

    public static String getSequence(long partyId, long sequence) {
        String result = String.valueOf(partyId);
        int length = result.length() + String.valueOf(sequence).length();
        if (length < GOLD_SEQUENCE_LENGTH) {
            for (int a = 0; a < GOLD_SEQUENCE_LENGTH - length; a++) {
                result = result + "0";
            }
        }
        return result + String.valueOf(sequence);
    }

    public static void main(String[] args) {
        System.out.println(getSequence(1000000000002146L, 123));
    }
}
