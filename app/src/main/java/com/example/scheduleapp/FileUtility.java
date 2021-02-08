package com.example.scheduleapp;

import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.GeneralSecurityException;

import android.content.Context;
import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * ファイル読み書きのユーティリティ
 * @author NekoZ
 * @version 1.0.0
 */
public class FileUtility{

    private static Context context; //ファイルを開くためのcontext

    /**
     * コンストラクタ
     * @param aContext ファイルを開くためのcontext
     */
    protected static void initialize(Context aContext){
        context = aContext;
    }

    /**
     * スケジュールの読み込みを行う
     * @param fileName ファイル名
     * @return ファイルの内容
     * @throws IOException 読み込み失敗
     */
    public static String readFile(String fileName)throws IOException {
        StringBuilder aBuilder = new StringBuilder();
        File schedule = new File(context.getFilesDir(), fileName);
        if (schedule.exists()) {
            FileInputStream fileInputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                aBuilder.append(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        }else{
            throw new FileNotFoundException();
        }
        return new String(aBuilder);
    }

    /**
     * スケージュールをファイルに書き込む
     * @param fileName ファイル名
     * @param value 書き込む文字列
     * @throws IOException 書き込み失敗
     */
    public static void writeFile(String fileName,String value) throws IOException{
        FileOutputStream fileOutputStream = context.openFileOutput(fileName,context.MODE_PRIVATE);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
        bufferedWriter.write(value);
        bufferedWriter.close();
        return;
    }

    /**
     * アセットからファイルを読み込む
     * @param fileName ファイル名
     * @throws IOException 読み込み失敗
     */
    public static String readAssets(String fileName) throws IOException{
        AssetManager assetManager = context.getResources().getAssets();
        StringBuilder aBuilder = new StringBuilder();
        InputStream inputStream = assetManager.open(fileName);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            aBuilder.append(line);
        }
        bufferedReader.close();
        return new String(aBuilder);
    }


    /**
     * AES暗号化されたファイルを復号化して読み込む
     * @param fileName /ファイル名
     * @param key 秘密鍵
     * @return 復号化した文字列
     * @throws GeneralSecurityException 復号化失敗
     * @throws IOException 読み込み失敗
     */
    public static String readFileByAES(String fileName,SecretKey key) throws GeneralSecurityException,IOException{
        String aString = FileUtility.readFile(fileName);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decByte = Base64.decode(aString, Base64.DEFAULT); // byte配列にデコード
        byte[] decrypted = cipher.doFinal(decByte); // 復号化
        String decodedString = new String(decrypted); // Stringに変換
        return decodedString;
    }


    /**
     * AES暗号化方式で書き込む
     * @param fileName ファイル名
     * @param aString 書き込む文字列
     * @param key 暗号化キー
     * @throws GeneralSecurityException 暗号化失敗
     * @throws IOException 書き込み失敗
     */
    public static void writeFileByAES(String fileName, String aString, SecretKey key) throws GeneralSecurityException,IOException {
        //暗号化
        System.out.println(key);
        System.out.println(aString);
        System.out.println(fileName);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encrypted = cipher.doFinal(aString.toString().getBytes()); // byte配列を暗号化
        String encodedString = Base64.encodeToString(encrypted, Base64.DEFAULT);

        FileUtility.writeFile(fileName,encodedString);
        return;
    }
}
