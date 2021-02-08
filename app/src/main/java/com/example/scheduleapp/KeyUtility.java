package com.example.scheduleapp;

import android.util.Base64;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 暗号化に関するユーティリティ
 */
public class KeyUtility {

    /**
     * AES暗号化キーを生成する
     * @return AES暗号化キー
     */
    public static SecretKey generateAESKey(){
        SecretKey aKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            aKey = keyGen.generateKey();
        }
        //起きるはずがない
        catch (NoSuchAlgorithmException anException){
            anException.printStackTrace();
        }
        return aKey;
    }

    /**
     * AES暗号化キーを文字列へ変換
     * @param key 暗号化キー
     * @return キーの文字列
     */
    public static String keyToString(SecretKey key){
        String stringKey = "";
        if (key != null) {
            stringKey = Base64.encodeToString(key.getEncoded(), Base64.DEFAULT);
        }
        return stringKey;
    }

    /**
     * 文字列からAES暗号化キーを生成
     * @param stringKey キーの文字列
     * @return AES暗号化キー
     */
    public static SecretKey stringToKey(String stringKey) throws IllegalArgumentException{
        byte[] encodedKey = Base64.decode(stringKey, Base64.DEFAULT);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }
}
