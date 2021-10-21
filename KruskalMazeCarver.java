package mazes.logic.carvers;

import graphs.EdgeWithData;
import graphs.minspantrees.MinimumSpanningTreeFinder;
import mazes.entities.Room;
import mazes.entities.Wall;
import mazes.logic.MazeGraph;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Carves out a maze based on Kruskal's algorithm.
 */
public class KruskalMazeCarver extends MazeCarver {
    MinimumSpanningTreeFinder<MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder;
    private final Random rand;

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random();
    }

    public KruskalMazeCarver(MinimumSpanningTreeFinder
                                 <MazeGraph, Room, EdgeWithData<Room, Wall>> minimumSpanningTreeFinder,
                             long seed) {
        this.minimumSpanningTreeFinder = minimumSpanningTreeFinder;
        this.rand = new Random(seed);
    }

    @Override
    protected Set<Wall> chooseWallsToRemove(Set<Wall> walls) {
        Set<EdgeWithData<Room, Wall>> edges = new HashSet<>();
        Set<Wall> newWalls = new HashSet<>();
        for (Wall wall : walls) {
            Double weight = rand.nextDouble();
            EdgeWithData<Room, Wall> newEdge = new EdgeWithData<>(wall.getRoom1(), wall.getRoom2(), weight, wall);
            edges.add(newEdge);
        }
        HashSet<EdgeWithData<Room, Wall>> hello = (HashSet<EdgeWithData<Room, Wall>>)
            this.minimumSpanningTreeFinder.findMinimumSpanningTree(new MazeGraph(edges)).edges();

        for (EdgeWithData<Room, Wall> wall : hello) {
            newWalls.add(wall.data());
        }
        return newWalls;
    }
}
