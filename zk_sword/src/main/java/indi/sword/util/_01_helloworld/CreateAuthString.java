package indi.sword.util._01_helloworld;

import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;

public class CreateAuthString {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        System.out.println(DigestAuthenticationProvider.generateDigest("jike:123456"));
    }
}
