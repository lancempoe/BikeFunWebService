package com.settings;

/**
 * Created with IntelliJ IDEA.
 * User: lancepoehler
 * Date: 4/12/13
 * Time: 4:20 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ForeignIdType {
    FaceBook("Facebook"), Google("Google");

    private String name;

    private ForeignIdType(String name) {
        this.name = name;
    }

    public static ForeignIdType fromName(String parseName) throws Exception {
        if(parseName==null) {
            throw new Exception("Hey, no such type!!");
        }

        if(FaceBook.name.equalsIgnoreCase(parseName)) {
            return FaceBook;
        } else if(Google.name.equalsIgnoreCase(parseName)) {
            return Google;
        }

        throw new Exception("Hey, no such type!!");
    }

}
