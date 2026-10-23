package com.example.newsandactions.entities;

public class keys {
    private static String connectionString = "Endpoint=https://jorge933746395.webpubsub.azure.com;AccessKey=VLjbv5PXwqU5jM0Ne58/kcKMCQ2bz6EtxK6FTMs5ULg=;Version=1.0;";
    private static String key = "VLjbv5PXwqU5jM0Ne58/kcKMCQ2bz6EtxK6FTMs5ULg=";

    public static String getClientAccessURL() {
        return clientAccessURL;
    }

    public static void setClientAccessURL(String clientAccessURL) {
        keys.clientAccessURL = clientAccessURL;
    }

    private static String clientAccessURL;

    public static String getConnectionString() {
        return connectionString;
    }

    public static void setConnectionString(String connectionString) {
        keys.connectionString = connectionString;
    }

    public static String getKey() {
        return key;
    }

    public static void setKey(String key) {
        keys.key = key;
    }


}
