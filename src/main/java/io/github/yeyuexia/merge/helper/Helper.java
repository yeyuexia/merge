package io.github.yeyuexia.merge.helper;

public final class Helper {
    public static String getPath(String path, String name) {
        return path == "" ? name : String.format("%s.%s", path, name);
    }

    public static String getCollectionPath(String path, String index) {
        return path == "" ? String.format("{%s}", index) : String.format("%s{%s}", path, index);
    }
}
