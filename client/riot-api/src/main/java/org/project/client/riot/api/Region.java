package org.project.client.riot.api;

public enum Region {
    ASIA("asia"),
    AMERICAS("americas"),
    EUROPE("europe"),
    KR("kr"),
    NA1("na1"),
    SEA("sea"),
    ESPORTS("esports"),
    ESPORTS_EU("esportseu");


    private final String value;


    Region(String value) {
        this.value = value;
    }


    public String getValue() {
        return value;
    }
}