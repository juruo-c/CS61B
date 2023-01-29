package byow.lab12;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static int WIDTH = 50;
    private static int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /**
     * Fills the given 2D array of tiles with Blank tiles.
     * @param tiles
     */
    public static void fillWithBlanks(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }
    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(6);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.GRASS;
            case 3: return Tileset.TREE;
            case 4: return Tileset.MOUNTAIN;
            case 5: return Tileset.WATER;
            default: return Tileset.NOTHING;
        }
    }

    private static void printLineWithBlank(TETile[][] Tiles, TETile color, int blankNum,
                                    int tilesNum, int x, int y) {
        x += blankNum;
        for (int i = x; i < x + tilesNum; i ++ ) {
            Tiles[i][y] = color;
        }
    }

    private static void addHexagonHelper(TETile[][] Tiles, TETile color, int size,
                                  int lineLeft, int x, int y) {
        if (lineLeft == 0) {
            return;
        }
        printLineWithBlank(Tiles, color, lineLeft - 1,
                size + 2 * (size - lineLeft), x, y);
        addHexagonHelper(Tiles, color, size, lineLeft - 1, x, y + 1);
        printLineWithBlank(Tiles, color, lineLeft - 1,
                size + 2 * (size - lineLeft), x, y + 2 * lineLeft - 1);
    }

    /**
     * Add a hexagon of the given size with the bottom left coordinate(x, y).
     */
    private static void addHexagon(TETile[][] Tiles, TETile color, int size, int x, int y) {
        addHexagonHelper(Tiles, color, size, size, x, y);
    }

    public static void displayBigHexagon(int size) {
        WIDTH = 11 * size - 6;
        HEIGHT = 10 * size;
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] Tiles = new TETile[WIDTH][HEIGHT];
        fillWithBlanks(Tiles);

        // the 1th column and 5th column
        for (int i = 2 * size; i <= 6 * size; i += 2 * size) {
            addHexagon(Tiles, randomTile(), size, 0, i);
            addHexagon(Tiles, randomTile(), size, 8 * size - 4, i);
        }
        // the 2th column and 4th column
        for (int i = size; i <= 7 * size; i += 2 * size) {
            addHexagon(Tiles, randomTile(), size, 2 * size - 1, i);
            addHexagon(Tiles, randomTile(), size, 6 * size - 3, i);
        }
        // the 3th column
        for (int i = 0; i <= 8 * size; i += 2 * size) {
            addHexagon(Tiles, randomTile(), size, 4 * size - 2, i);
        }

        ter.renderFrame(Tiles);
    }

    public static void main(String[] args) {
        displayBigHexagon(4);
    }
}
