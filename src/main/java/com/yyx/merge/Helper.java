package com.yyx.merge;

public final class Helper {
    public static String getPath(String path, String name) {
        return path == "" ? name : String.format("%s.%s", path, name);
    }
}
