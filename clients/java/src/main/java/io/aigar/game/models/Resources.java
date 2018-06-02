package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Resources {
    private List<Coordinate> regular;
    private List<Coordinate> silver;
    private List<Coordinate> gold;

    public List<Coordinate> getRegular() {
        return regular;
    }

    public void setRegular(List<Coordinate> regular) {
        this.regular = regular;
    }

    public List<Coordinate> getSilver() {
        return silver;
    }

    public void setSilver(List<Coordinate> silver) {
        this.silver = silver;
    }

    public List<Coordinate> getGold() {
        return gold;
    }

    public void setGold(List<Coordinate> gold) {
        this.gold = gold;
    }

    @JsonIgnore
    public List<Coordinate> getAllResources() {
        return Stream.concat(Stream.concat(gold.stream(), silver.stream()), regular.stream()).collect(Collectors.toList());
    }
}
