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

    public List<Coordinate> getSilver() {
        return silver;
    }

    public List<Coordinate> getGold() {
        return gold;
    }

    @JsonIgnore
    public List<Coordinate> getAllResources() {
        return Stream.concat(Stream.concat(gold.stream(), silver.stream()), regular.stream()).collect(Collectors.toList());
    }
}
