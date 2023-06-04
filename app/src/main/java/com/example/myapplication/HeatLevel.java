package com.example.myapplication;

import java.util.HashMap;

public enum HeatLevel {
    LEVEL1(0,"더위를 잘 탄다."),
    LEVEL2(1,"보통이다"),
    LEVEL3(2,"더위를 잘 타지 않는다."),;

    static private final HashMap<Integer, String> sDialogMap;

    static {
        sDialogMap = new HashMap<>(HeatLevel.values().length);
        for (HeatLevel type : HeatLevel.values()) {
            sDialogMap.put(type.idx, type.content);
        }
    }

    int idx;
    String content;

    HeatLevel(int idx, String content) {
        this.idx = idx;
        this.content = content;
    }

    public int byIdx() {
        return idx;
    }

    public String byContent() {
        return content;
    }
}
