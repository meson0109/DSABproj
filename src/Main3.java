import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.*;

public class Main3 {
    private static JFrame selectionFrame;
    private static JFrame imageFrame;
    public static BufferedImage selectedImage;
    public static double[][] grayMatrix;
    public static double[][] costMatrix;

    private static Point currentMousePoint = null;

    // 种子点和路径
    private static java.util.List<Point> seedPoints = new ArrayList<>();
    private static java.util.List<java.util.List<Point>> allPaths = new ArrayList<>();
    private static java.util.List<Point> currentPath = new ArrayList<>();
    private static final int MAX_PATH_LENGTH = 100; // 路径最大长度阈值

    public static void main(String[] args) {
        createSelectionWindow();

    }

    private static void createSelectionWindow() {
        selectionFrame = new JFrame("选择图片");
        selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectionFrame.setSize(400, 200);
        selectionFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());

        JLabel label = new JLabel("请选择要查看的图片文件", SwingConstants.CENTER);
        panel.add(label, BorderLayout.CENTER);

        JButton selectButton = new JButton("选择图片");
        selectButton.setPreferredSize(new Dimension(150, 40));
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(selectButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        selectionFrame.add(panel);
        selectionFrame.setVisible(true);
    }

    private static void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "图片文件", "jpg", "jpeg", "png", "gif", "bmp");
        fileChooser.setFileFilter(filter);

        int returnValue = fileChooser.showOpenDialog(selectionFrame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(selectedFile);

                selectionFrame.setVisible(false);

                selectedImage = image;
                grayMatrix = GrayConverter.convertToGrayMatrix(image);

                // 转换为代价矩阵并打印
                costMatrix = SobelConverter.convertToCostMatrix(grayMatrix);

                // 创建并显示图片窗口
                showImageWindow(image, selectedFile.getName());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(selectionFrame,
                        "The picture cannot be loaded.: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showImageWindow(BufferedImage image, String title) {

        imageFrame = new JFrame("图片查看 - " + title);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        imageFrame.setSize(image.getWidth(), image.getHeight());
        imageFrame.setResizable(false);

        ImagePanel imagePanel = new ImagePanel(image);
        imageFrame.add(imagePanel);

        JLabel statusLabel = new JLabel();
        statusLabel.setText(String.format("img size: %d x %d", image.getWidth(), image.getHeight()));
        imageFrame.add(statusLabel, BorderLayout.SOUTH);

        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }

    static class ImagePanel extends JPanel {
        private final BufferedImage image;
        private BufferedImage displayImage;
        private boolean stop=false;

        public ImagePanel(BufferedImage image) {
            this.image = image;
            this.displayImage = copyImage(image);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (stop && SwingUtilities.isRightMouseButton(e) && e.getClickCount()==2) {
                        stop=false;
                        currentPath.clear();
                        seedPoints.clear();
                        allPaths.clear();
                        currentMousePoint = null;
                        repaint();
                    }else if (!stop && e.getClickCount()==2) {
                        if(SwingUtilities.isLeftMouseButton(e)){
                            //双击左键用dijkstra算法计算currentMousePoint和第一个种子点之间的路径，并停止追踪
                            Point firstSeed = seedPoints.get(0);
                            java.util.List<Point> path = DijkstraAlgorithm.findShortestPath(
                                    costMatrix, firstSeed, currentMousePoint);
                            currentPath = new ArrayList<>();
                            currentPath.addAll(path.subList(1, path.size()));
                            allPaths.add(new ArrayList<>(currentPath));
                            repaint();
                            currentPath.clear();
                            //stop tracking
                            stop=true;
                            currentMousePoint = null;
                        }else if(SwingUtilities.isRightMouseButton(e)){
                            //双击右键清空所有点
                            currentPath.clear();
                            seedPoints.clear();
                            allPaths.clear();
                            currentMousePoint = null;
                            repaint();
                        }
                    } else if (!stop ) {
                        if(SwingUtilities.isLeftMouseButton(e)){
                            // 左键：设置新起点或添加路径点
                            if (seedPoints.isEmpty()) {
                                seedPoints.add(e.getPoint());
                                currentPath.clear();
                                currentPath.add(e.getPoint());
                            } else {
                                // 计算从最后种子点到当前点的路径
                                currentMousePoint = e.getPoint();
                                Point lastSeed = seedPoints.get(seedPoints.size()-1);
                                java.util.List<Point> pathSegment = DijkstraAlgorithm.findShortestPath(
                                        costMatrix, lastSeed, currentMousePoint);
                                if (!pathSegment.isEmpty()) {
                                    currentPath = new ArrayList<>();
                                    currentPath.add(lastSeed);
                                    currentPath.addAll(pathSegment.subList(1, pathSegment.size()));

                                    allPaths.add(new ArrayList<>(currentPath));
                                    seedPoints.add(e.getPoint());
                                    currentPath.clear();
                                    currentPath.add(e.getPoint());
                                }
                            }
                            repaint();
                        }else if(SwingUtilities.isRightMouseButton(e)){
                            //单击右键撤销一步
                            if (!seedPoints.isEmpty()) {
                                Point lastSeed = seedPoints.get(seedPoints.size()-1);
                                if(distBetweenPoints(lastSeed,currentMousePoint)<10){
                                    if(!allPaths.isEmpty()) {
                                        currentPath.clear();
                                        seedPoints.remove(seedPoints.size()-1);
                                        allPaths.remove(allPaths.size()-1);
//                            if (!seedPoints.isEmpty()) {
//                                currentPath.addAll(allPaths.get(allPaths.size()-1));
//                            }
                                        updateCurrentPath();
                                    }
                                }
                            }else{
                                currentPath.clear();
                                seedPoints.clear();
                                allPaths.clear();
                                currentMousePoint = null;
                            }

                            repaint();
                        }
                    }

                }
            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    if (!stop && !seedPoints.isEmpty() && !currentPath.isEmpty()) {
                        currentMousePoint = e.getPoint();
                        updateCurrentPath();
                        repaint();
                    }
                }
            });
        }

        private void updateCurrentPath() {
            Point lastSeed = seedPoints.get(seedPoints.size()-1);
            java.util.List<Point> newSegment = DijkstraAlgorithm.findShortestPath(
                    costMatrix, lastSeed, currentMousePoint);

            if (!newSegment.isEmpty()) {
                // 种子点+segment路径中除种子点之后的点（包含currentMousePoint）
                currentPath = new ArrayList<>();
                currentPath.add(lastSeed);
                currentPath.addAll(newSegment.subList(1, newSegment.size()));

                // 检查路径长度，如果太长则添加为中继点
                if (currentPath.size() > MAX_PATH_LENGTH) {
                    int midIndex = currentPath.size() / 2;
                    Point newSeed = currentPath.get(midIndex);
                    seedPoints.add(newSeed);
                    allPaths.add(new ArrayList<>(currentPath.subList(0, midIndex+1)));
                    currentPath = new ArrayList<>(currentPath.subList(midIndex, currentPath.size()));
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = displayImage.createGraphics();
            g2d.drawImage(image, 0, 0, null);

            // 已完成的路径PathCooling
            g2d.setColor(Color.RED);
            g2d.setStroke(new BasicStroke(2));
            for (java.util.List<Point> path : allPaths) {
                drawPath(g2d, path);
            }

            // 当前正在追踪的路径
            if (!currentPath.isEmpty()) {
                g2d.setColor(Color.ORANGE);
                g2d.setStroke(new BasicStroke(2));
                drawPath(g2d, currentPath);
            }

            // 所有固定的种子点
            g2d.setColor(Color.GREEN);
            for (Point seed : seedPoints) {
                g2d.fillOval(seed.x - 4, seed.y - 4, 8, 8);
            }

            // 当前鼠标位置
            if (currentMousePoint != null) {
                g2d.setColor(Color.BLUE);
                g2d.fillOval(currentMousePoint.x - 3, currentMousePoint.y - 3, 6, 6);
            }

            g2d.dispose();
            g.drawImage(displayImage, 0, 0, null);
        }

        private void drawPath(Graphics2D g2d, java.util.List<Point> path) {
            if (path.size() > 1) {
                for (int i = 0; i < path.size() - 1; i++) {
                    Point p1 = path.get(i);
                    Point p2 = path.get(i + 1);
                    g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                }
            }
        }

        private BufferedImage copyImage(BufferedImage source) {
            BufferedImage copy = new BufferedImage(
                    source.getWidth(), source.getHeight(), source.getType());
            Graphics g = copy.createGraphics();
            g.drawImage(source, 0, 0, null);
            g.dispose();
            return copy;
        }

        public double distBetweenPoints(Point currnet, Point other) {
            int dx = currnet.x - other.x;
            int dy = currnet.y - other.y;
            return Math.sqrt(dx * dx + dy * dy);
        }
    }
}