package io.aigar.game.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.mini2Dx.gdx.math.Vector2;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Resources {
    private List<Vector2> regular;
    private List<Vector2> silver;
    private List<Vector2> gold;

    public List<Vector2> getRegular() {
        return regular;
    }

    public List<Vector2> getSilver() {
        return silver;
    }

    public List<Vector2> getGold() {
        return gold;
    }

    @JsonIgnore
    public List<Vector2> getAllResources() {
        return Stream.concat(Stream.concat(gold.stream(), silver.stream()), regular.stream()).collect(Collectors.toList());
    }
}
