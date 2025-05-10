import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class StuffAlgorithm {
    private static final int[] dx = {-1, 0, 1, -1, 1, -1, 0, 1};
    private static final int[] dy = {-1, -1, -1, 0, 0, 1, 1, 1};

    public static void main(String[] args) {
        int[][] boolMatrix= {
                {0,0,0,0,0},
                {1,1,0,1,1},
                {1,0,1,0,1},
                {1,1,1,1,1}
        };
        System.out.println(Arrays.deepToString(boolMatrix));
        surround(boolMatrix,4,5);
        System.out.println(Arrays.deepToString(boolMatrix));
    }
    public static void surround(int[][] boolMatrix, int width, int height) {
        // 首先标记所有外部区域为-1
        markOutside(boolMatrix, width, height);

        // 标记外围点(与外部相邻的路径点)
        markBoundary(boolMatrix, width, height);

        // 标记剩余区域
        markRemaining(boolMatrix, width, height);
    }

    private static void markOutside(int[][] matrix, int width, int height) {
        // 使用队列进行泛洪填充
        Queue<int[]> queue = new LinkedList<>();

        // 添加所有边缘的空格
        for (int i = 0; i < width; i++) {
            if (matrix[i][0] == 0) queue.add(new int[]{i, 0});
            if (matrix[i][height-1] == 0) queue.add(new int[]{i, height-1});
        }
        for (int j = 0; j < height; j++) {
            if (matrix[0][j] == 0) queue.add(new int[]{0, j});
            if (matrix[width-1][j] == 0) queue.add(new int[]{width-1, j});
        }

        // 泛洪填充所有连通的外部区域
        while (!queue.isEmpty()) {
            int[] pos = queue.poll();
            int x = pos[0], y = pos[1];

            if (matrix[x][y] != 0) continue;

            matrix[x][y] = -1;

            // 检查4邻域(上下左右)
            int[] dirs = {-1, 0, 1, 0, -1};
            for (int k = 0; k < 4; k++) {
                int nx = x + dirs[k];
                int ny = y + dirs[k+1];

                if (nx >= 0 && nx < width && ny >= 0 && ny < height && matrix[nx][ny] == 0) {
                    queue.add(new int[]{nx, ny});
                }
            }
        }
    }

    private static void markBoundary(int[][] matrix, int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (matrix[i][j] == 1) {
                    // 检查8邻域是否有外部区域
                    int[] dirs = {-1, 0, 1, 0, -1};
                    for (int k = 0; k < 4; k++) {
                        int nx = i + dirs[k];
                        int ny = j + dirs[k+1];

                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            if (matrix[nx][ny] == -1) {
                                matrix[i][j] = 2; // 边界点
                                break;
                            }
                        } else {
                            // 矩阵外的也认为是外部
                            matrix[i][j] = 2;
                            break;
                        }
                    }
                }
            }
        }
    }

    private static void markRemaining(int[][] matrix, int width, int height) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][j] = 3; // 内部空白
                } else if (matrix[i][j] == 1) {
                    matrix[i][j] = 3; // 完全被包围的路径点
                }
            }
        }
    }
}