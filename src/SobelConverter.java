public class SobelConverter {
    // Sobel算子核

    /**
     *Sobel算子计算：
     *使用3x3的Sobel_X核计算水平梯度Ix
     *使用3x3的Sobel_Y核计算垂直梯度Iy
     *计算梯度幅值G = √(Ix² + Iy²)

     * 代价矩阵转换：
     * 首先找到整个矩阵中的最大梯度值G_max
     * 使用公式 f_G = (G_max - G) / G_max 将梯度转换为代价
     * 代价值范围在0到1之间，边缘处代价低，平滑区域代价高
     */
    private static final int[][] SOBEL_X = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    private static final int[][] SOBEL_Y = {
            {-1, -2, -1},
            {0, 0, 0},
            {1, 2, 1}
    };

    /**
     * 将灰度矩阵转换为代价矩阵
     * @param grayMatrix 输入的灰度矩阵
     * @return 代价矩阵
     */
    public static double[][] convertToCostMatrix(double[][] grayMatrix) {
        int width = grayMatrix.length;
        int height = grayMatrix[0].length;
        double[][] costMatrix = new double[width][height];

        // 计算最大梯度值，用于归一化
        double maxG = 0;

        // 首先计算梯度幅值G
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                // 计算Ix和Iy
                double Ix = calculateConvolution(grayMatrix, x, y, SOBEL_X);
                double Iy = calculateConvolution(grayMatrix, x, y, SOBEL_Y);

                // 计算梯度幅值G
                double G = Math.sqrt(Ix * Ix + Iy * Iy);
                costMatrix[x][y] = G;

                // 更新最大梯度值
                if (G > maxG) {
                    maxG = G;
                }
            }
        }

        // 归一化并转换为代价
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (maxG > 0) {
                    // 使用公式 f_G = (G_max - G) / G_max
                    costMatrix[x][y] = (maxG - costMatrix[x][y]) / maxG;
                } else {
                    costMatrix[x][y] = 0;
                }
            }
        }

        return costMatrix;
    }

    /**
     * 计算卷积
     * @param matrix 输入矩阵
     * @param x 中心点x坐标
     * @param y 中心点y坐标
     * @param kernel 卷积核
     * @return 卷积结果
     */
    private static double calculateConvolution(double[][] matrix, int x, int y, int[][] kernel) {
        double sum = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nx = x + i;
                int ny = y + j;
                if (nx >= 0 && nx < matrix.length && ny >= 0 && ny < matrix[0].length) {
                    sum += matrix[nx][ny] * kernel[i + 1][j + 1];
                }
            }
        }
        return sum;
    }

    /**
     * 打印矩阵部分内容
     * @param matrix 要打印的矩阵
     * @param title 标题
     * @param size 要打印的区域大小
     */
    public static void printMatrix(double[][] matrix, String title, int size) {
        System.out.println("\n===== " + title + " =====");
        int displaySize = Math.min(size, Math.min(matrix.length, matrix[0].length));

        for (int y = 0; y < displaySize; y++) {
            for (int x = 0; x < displaySize; x++) {
                System.out.printf("%6.3f ", matrix[x][y]);
            }
            System.out.println();
        }
    }
}
