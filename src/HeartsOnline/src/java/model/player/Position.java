package model.player;

import java.io.Serializable;

public enum Position implements Serializable {
    SOUTH(0, "Nam"),
    WEST(1, "Tây"),
    NORTH(2, "Bắc"),
    EAST(3, "Đông");

    private int order;
    private String name;

    Position(int order, String name) {
        this.order = order;
        this.name = name;
    }

    public int getOrder() {return order;}

    public String getName() {
        return name;
    }

    public Position next() {
        int order = this.order;
        order++;
        if (order >= 4) order -= 4;

        return Position.values()[order];
    }
}
