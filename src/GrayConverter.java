import java.awt.Color;
import java.awt.image.BufferedImage;

public class GrayConverter {
    public static double[][] convertToGrayMatrix(BufferedImage image) throws Exception {
        int width = image.getWidth();
        int height = image.getHeight();
        double[][] grayMatrix = new double[width][height];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color color = new Color(image.getRGB(x, y));
                grayMatrix[x][y] = calculateLuminance(color);
            }
        }

        return grayMatrix;
    }

    // 更精确的亮度计算（感知亮度）
    private static double calculateLuminance(Color color) {
        return 0.2126 * color.getRed() +
                0.7152 * color.getGreen() +
                0.0722 * color.getBlue();
    }

}