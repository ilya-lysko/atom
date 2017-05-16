package ru.atom.gamemechanics.entities;

import ru.atom.gamemechanics.gameinterfaces.GameObject;
import ru.atom.gamemechanics.gameinterfaces.Positionable;
import ru.atom.gamemechanics.gameinterfaces.Tickable;
import ru.atom.gamemechanics.geometry.Point;

/**
 * Created by kinetik on 02.05.17.
 */
public class Wood implements Positionable, Tickable, GameObject {

    private final int wallId;

    public int getWallId() {
        return wallId;
    }

    public long getTickValue() {
        return tickValue;
    }

    public void setTickValue(long tickValue) {
        this.tickValue = tickValue;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    private long tickValue;
    private Point position;

    public Wood(int wallId, long tickValue, Point position) {
        this.wallId = wallId;
        this.tickValue = tickValue;
        this.position = position;
    }

    @Override
    public int getId() {
        return this.wallId;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public void tick(long elapsed) {
        this.tickValue += elapsed;
    }
}
