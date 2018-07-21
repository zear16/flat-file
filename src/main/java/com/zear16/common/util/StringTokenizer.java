package com.zear16.common.util;

public class StringTokenizer {

    private final char aChar[];
    private final int nChar;
    private int iChar;

    public StringTokenizer(String str) {
        aChar = str==null ? null : str.toCharArray();
        nChar = aChar==null ? 0 : aChar.length;
    }

    public String getLastToken() {
        if (iChar < nChar) {
            return new String(aChar, iChar, nChar - iChar);
        }
        return null;
    }

    public String getNextToken(char charSep) {
        int iStart = iChar;
        int count = 0;
        for (;;) {
            if (iChar >= nChar) {
                break;
            }
            char c = aChar[iChar];
            if (c == charSep) {
                iChar += 1;
                break;
            }
            iChar += 1;
            count += 1;
        }
        if (count > 0) {
            return new String(aChar, iStart, count);
        }
        return null;
    }
}
