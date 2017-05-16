package ru.atom.gamemechanics.entities;

import ru.atom.gamemechanics.gameinterfaces.GameObject;
import ru.atom.gamemechanics.gameinterfaces.Movable;
import ru.atom.gamemechanics.gameinterfaces.Positionable;
import ru.atom.gamemechanics.gameinterfaces.Tickable;
import ru.atom.gamemechanics.geometry.Direction;
import ru.atom.gamemechanics.geometry.Point;


/**
 * Created by kinetik on 02.05.17.
 */
public class Pawn implements Movable, Positionable, Tickable, GameObject {

    private final int playerId;
    private int velocity;
    private Point position;
    private long tickValue;

    public Pawn(int playerId, int velocity, Point position, long tickValue) {
        this.playerId = playerId;
        if (velocity <= 0) {
            throw new IllegalArgumentException();
        }
        this.velocity = velocity;
        this.position = position;
        this.tickValue = tickValue;
    }

    @Override
    public int getId() {
        return this.playerId;
    }

    @Override
    public void tick(long elapsed) {
        this.tickValue = this.tickValue + elapsed;
    }

    @Override
    public Point getPosition() {
        return this.position;
    }

    @Override
    public Point move(Direction direction) {
        switch (direction) {
            case UP:
                this.position = new Point(this.position.getxCoord(), this.position.getyCoord() + this.velocity);
                break;
            case DOWN:
                this.position = new Point(this.position.getxCoord(), this.position.getyCoord() - this.velocity);
                break;
            case LEFT:
                this.position = new Point(this.position.getxCoord() - this.velocity, this.position.getyCoord());
                break;
            case RIGHT:
                this.position = new Point(this.position.getxCoord() + this.velocity, this.position.getyCoord());
                break;
            default:
                break;
        }
        return this.position;
    }
}
