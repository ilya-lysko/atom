package ru.atom.gamemechanics.gameinterfaces;

import ru.atom.gamemechanics.geometry.Point;

/**
 * Created by kinetik on 02.05.17.
 */
public interface Positionable extends GameObject {
    /**
     * @return Current position
     */
    Point getPosition();
}
