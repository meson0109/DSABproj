import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class Main {
    private static JFrame selectionFrame;
    private static JFrame imageFrame;
    public static BufferedImage selectedImage;
    public static double[][] grayMatrix;
    public static double[][] costMatrix;

    public static void main(String[] args) {
        // 创建选择窗口
        createSelectionWindow();
        while (selectedImage != null) {
            System.out.println(123);
            break;
        }

    }

    private static void createSelectionWindow() {
        selectionFrame = new JFrame("选择图片");
        selectionFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        selectionFrame.setSize(400, 200);
        selectionFrame.setLocationRelativeTo(null); // 居中显示

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

                // 隐藏选择窗口
                selectionFrame.setVisible(false);

                // 创建并显示图片窗口
                showImageWindow(image, selectedFile.getName());

                selectedImage = image;
                grayMatrix = GrayConverter.convertToGrayMatrix(image);

                // 打印灰度矩阵
                SobelConverter.printMatrix(grayMatrix, "灰度矩阵", 10);

                // 转换为代价矩阵并打印
                costMatrix = SobelConverter.convertToCostMatrix(grayMatrix);
                SobelConverter.printMatrix(costMatrix, "代价矩阵", 10);


            } catch (Exception ex) {
                JOptionPane.showMessageDialog(selectionFrame,
                        "无法加载图片: " + ex.getMessage(),
                        "错误",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void showImageWindow(BufferedImage image, String title) {
        imageFrame = new JFrame("图片查看 - " + title);
        imageFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 设置窗口大小为图片尺寸
        imageFrame.setSize(image.getWidth(), image.getHeight());
        imageFrame.setResizable(false); // 禁止调整窗口大小

        // 创建图片标签
        JLabel imageLabel = new JLabel(new ImageIcon(image));
        imageFrame.add(imageLabel);

        // 添加状态栏
        JLabel statusLabel = new JLabel();
        statusLabel.setText(String.format("图片尺寸: %d x %d", image.getWidth(), image.getHeight()));
        imageFrame.add(statusLabel, BorderLayout.SOUTH);

        // 居中显示窗口
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setVisible(true);
    }

}