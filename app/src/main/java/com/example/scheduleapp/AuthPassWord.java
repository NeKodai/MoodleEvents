package com.example.scheduleapp;

/**
 * 2段階認証のワンタイムパスワードの計算
 * @author NekoZ
 * @version 1.0.1
 */

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class AuthPassWord {
    /**
     * 秘密鍵から2進数の文字列にして返す
     * @param aKey 秘密鍵の文字列
     * @return 2進数に変換した文字列
     */
    private static String keyTobBinaryString(String aKey) {

        StringBuilder builder = new StringBuilder();
        for (Character aCharacter : aKey.toCharArray()) {
            if (aCharacter == '=') {
                continue;
            }
            Integer modifiedInteger = aCharacter < 65 ? (aCharacter - 50 + 26) : (aCharacter - 65);
            String bitString = Integer.toBinaryString(modifiedInteger);
            builder.append(String.format("%5s", bitString).replace(" ", "0"));
        }
        return new String(builder);
    }

    /**
     * 2進数文字列をbyte配列にして返す
     * SecretKeySpec実行のため、byte型の配列に変換する必要がある
     * @param BinaryString 秘密鍵を2進数に変換した文字列
     * @return byte型の配列
     */
    private static byte[] binaryStringToByteArray(String BinaryString) {
        List<Byte> aByteList = new ArrayList<>();
        Integer len = BinaryString.length();
        BinaryString = String.format("%-80s", BinaryString).replace(" ", "0");
        for (int i = 0; i < len; i += 8) {
            String aString = BinaryString.substring(i, i + 8);
            Integer aInteger = Integer.parseInt(aString, 2);
            if (aInteger > 127) {
                aInteger = -128 + (aInteger - 128);
            }
            aByteList.add(aInteger.byteValue());
        }

        //byte型の配列に変換
        byte[] aByteArray = new byte[aByteList.size()];
        for (Integer i = 0; i < aByteList.size(); i++) {
            aByteArray[i] = aByteList.get(i);
        }
        return aByteArray;
    }

    /**
     * 秘密鍵のbyte型配列をハッシュ値のbyte型配列に変換する
     * @param byteArray 秘密鍵のbyte型の配列
     * @param timeIndex 時間に対するインデックス番号
     * @return ハッシュ値のbyte型配列
     */
    private static byte[] byteArrayToHash(byte[] byteArray, Long timeIndex)
            throws NoSuchAlgorithmException, InvalidKeyException {

        SecretKeySpec signKey = new SecretKeySpec(byteArray, "HmacSHA1");
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(timeIndex);
        byte[] timeBytes = buffer.array();
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hashByteArray = mac.doFinal(timeBytes);
        return hashByteArray;
    }

    /**
     * ハッシュ値からワンタイムパスワードの文字列へ変換する
     * @param hashByteArray ハッシュ値のbyte型配列
     * @return ワンタイムパスワードの文字列
     */
    private static String hashToAuthPass(byte[] hashByteArray) {
        Integer offset = hashByteArray[19] & 0xf;
        Long truncatedHash = Long.valueOf(hashByteArray[offset] & 0x7f);
        for (Integer i = 1; i < 4; i++) {
            truncatedHash <<= 8;
            truncatedHash |= hashByteArray[offset + i] & 0xff;
        }
        Long authPassNumber = (truncatedHash % 1000000);

        //右詰め0埋め
        String authPassString = String.format("%06d", authPassNumber);
        return authPassString;
    }

    /**
     * byte型の配列を出力する
     * @param byteArray byte型の配列
     */
    private static void printByteArray(byte[] byteArray, String format) {
        System.out.printf("[");
        String formatNext = format + ", ";
        for (Integer i = 0; i < byteArray.length; i++) {
            if (i == byteArray.length - 1) {
                System.out.printf(format, byteArray[i]);
                System.out.printf("]%n");
            } else {
                System.out.printf(formatNext, byteArray[i]);
            }
        }
    }

    /**
     * 秘密鍵からワンタイムパスワードを取得する
     * @param secretKey 秘密鍵の文字列
     * @return ワンタイムパスワードの文字列
     */
    public static String getAuthPass(String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        secretKey = secretKey.trim();
        Long TC = System.currentTimeMillis() / 1000L;
        Long timeIndex = ((TC - 0) / 30);
        String binaryString = keyTobBinaryString(secretKey);
        byte[] byteArray = binaryStringToByteArray(binaryString);
        byte[] hashByteArray = byteArrayToHash(byteArray, timeIndex);
        return hashToAuthPass(hashByteArray);
    }
}