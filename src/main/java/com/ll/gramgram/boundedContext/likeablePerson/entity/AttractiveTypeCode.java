package com.ll.gramgram.boundedContext.likeablePerson.entity;

public enum AttractiveTypeCode {
    OUTLOOK(1, "외모"), PERSONALITY(2,"성격"), ABILITY(3,"능력");

    private final int value;
    private final String symbol;
    AttractiveTypeCode(int value, String symbol) {
        this.value = value;
        this.symbol = symbol;
    }

    public static final AttractiveTypeCode[] ATTRACTIVE_TYPE_CODES = AttractiveTypeCode.values();
    public int getValue() {return value;}
    public String getSymbol() {return symbol;}

    public static String of(int code) {
        if (code < 1 || code > 3) {throw new IllegalArgumentException("Invalid AttractiveTypeCode : " + code);}
        return ATTRACTIVE_TYPE_CODES[code - 1].symbol;
    }
};
