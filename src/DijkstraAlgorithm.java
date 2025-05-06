import java.awt.Point;
import java.util.*;

public class DijkstraAlgorithm {
    private static final int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final double[] distance = {Math.sqrt(2), 1, Math.sqrt(2), 1, 1, Math.sqrt(2), 1, Math.sqrt(2)};

    public static java.util.List<Point> findShortestPath(double[][] costMatrix, Point start, Point end) {
        int width = costMatrix.length;
        int height = costMatrix[0].length;

        // 优先队列，按距离排序
        PriorityQueue<Node> pq = new PriorityQueue<>();// PriorityQueue 会自动维护一个按距离升序排列的队列
        double[][] dist = new double[width][height];
        Point[][] prev = new Point[width][height];

        // 初始化：距离都设为无穷大，表示尚未找到路径到达这些节点
        for (int x = 0; x < width; x++) {
            Arrays.fill(dist[x], Double.POSITIVE_INFINITY);
        }
        dist[start.x][start.y] = 0;
        pq.add(new Node(start, 0));

        // Dijkstra算法主循环
        while (!pq.isEmpty()) {
            Node current = pq.poll();//移除并返回队列中距离最小的 Node 对象
            if (current.point.equals(end)) break;

            //每次8方向fori遍历添加（未检查过的点）到pq队列中，下一次弹出距离最小的点，选出新的距离最小点，继续循环
            for (int i = 0; i < 8; i++) {
                int nx = current.point.x + dx[i];
                int ny = current.point.y + dy[i];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    // 计算新的距离 = 当前距离 + 代价 * 物理距离
                    double newDist = dist[current.point.x][current.point.y]
                            + costMatrix[nx][ny] * distance[i];

                    if (newDist < dist[nx][ny]) {
                        dist[nx][ny] = newDist;
                        prev[nx][ny] = current.point;//prev[nx][ny]记录下一步的父节点
                        pq.add(new Node(new Point(nx, ny), newDist));
                    }
                }
            }
        }

        // 回溯路径
        return reconstructPath(prev, end);
    }

    private static java.util.List<Point> reconstructPath(Point[][] prev, Point end) {
        LinkedList<Point> path = new LinkedList<>();
        Point current = end;

        while (current != null) {
            path.addFirst(current);
            current = prev[current.x][current.y];
        }

        return path;
    }

    private static class Node implements Comparable<Node> {
        Point point;
        double distance;

        Node(Point p, double d) {
            point = p;
            distance = d;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(distance, other.distance);
        }
    }
}