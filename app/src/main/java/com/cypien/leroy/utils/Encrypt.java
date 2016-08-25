package com.cypien.leroy.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alex on 8/21/2015.
 */

//Clasa pentru encryptarea unui string in md5
public class Encrypt {
    private Encrypt(){

    }
    public static String getMD5EncryptedPass(String pass){

        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] data = pass.getBytes();
            byte[] digested = md.digest(data);
            for(byte b: digested){
                sb.append(Integer.toHexString(0xFF & b));
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Encrypt.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }

    private static char[] hextable = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String byteArrayToHex(byte[] array) {
        String s = "";
        for (int i = 0; i < array.length; ++i) {
            int di = (array[i] + 256) & 0xFF; // Make it unsigned
            s = s + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
        }
        return s;
    }

    public static String digest(String s, String algorithm) {
        MessageDigest m = null;
        try {
            m = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return s;
        }

        try {
            m.update(s.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            m.update(s.getBytes());
        }
        return byteArrayToHex(m.digest());
    }

    public static String getMD5UTFEncryptedPass(String s) {
        return digest(s, "MD5");
    }


}
