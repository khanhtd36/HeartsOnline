package model.player;

public enum Position {
    SOUTH(0),
    WEST(1),
    NORTH(2),
    EAST(3);

    private int order;

    Position(int order) {
        this.order = order;
    }

    public int getOrder() {return order;}
}
