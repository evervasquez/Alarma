package es.ever.fisialarma;

import group.pals.android.lib.ui.lockpattern.util.IEncrypter;

import java.util.zip.CRC32;

import android.content.Context;

public class LPEncrypter implements IEncrypter {

    public String encrypt(Context context, String s) {
        CRC32 c = new CRC32();
        c.update(s.getBytes());

        return String.format("%08x", c.getValue());
    }// encrypt()
}