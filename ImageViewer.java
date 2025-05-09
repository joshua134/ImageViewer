package imageview;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

public class ImageViewer extends JFrame{
    private JLabel imageLabel;
    private JPanel thumbnailPanel;
    private JScrollPane scrollPane;
    private List<File> imageFiles = new ArrayList<>();
    private BufferedImage currentImage = null;
    
    public ImageViewer(){
        setTitle("View Image");
        setLocationRelativeTo(null);
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
        imageLabel = new JLabel("Drop an image here ...", SwingUtilities.CENTER);
        imageLabel.setFont(new Font("serif", Font.BOLD, 17));
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        
        setLayout(new BorderLayout());
        add(new JScrollPane(imageLabel), BorderLayout.CENTER);
        
        imageLabel.setTransferHandler(new TransferHandler(){
            @Override
            public boolean canImport(TransferHandler.TransferSupport support){
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
            
            @Override
            public boolean importData(TransferHandler.TransferSupport support){
                try{
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    File file = files.get(0);
                    displayImage(file);
                    return true;
                } catch(Exception e){
                    e.printStackTrace();
                    return false;
                }
            }
        });
        
        addComponentListener(new ComponentAdapter(){
            public void componentResized(ComponentEvent e){
                if(currentImage != null){
                    updateImage(currentImage);
                }
            }

            
        });
        
        thumbnailPanel = new JPanel();
        thumbnailPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        scrollPane = new JScrollPane(thumbnailPanel);
        scrollPane.setPreferredSize(new Dimension(0,100));
        add(scrollPane, BorderLayout.SOUTH);
        
        setupMenuBar();
    }
    
    private void updateImage(BufferedImage currentImage) {
        int labelWidth = imageLabel.getWidth();
        int labelHeight = imageLabel.getHeight();
        
        if(labelWidth == 0 || labelHeight == 0) return;
        
        Image scaled = currentImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);
        imageLabel.setIcon(new ImageIcon(scaled));
        imageLabel.setText(null);
        
    }
    
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenu aboutMenu = new JMenu();
        
        JMenuItem openFolder = new JMenuItem("Open Folder");
        openFolder.addActionListener(e -> openFolder());
        fileMenu.add(openFolder);
        
        JMenuItem clearAll = new JMenuItem("Clear All");
        clearAll.addActionListener(e -> clearAll());
        fileMenu.add(clearAll);
        
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e -> JOptionPane.showMessageDialog(this, "ImageViewer v1.0\nMade with Java Swing", "About", JOptionPane.INFORMATION_MESSAGE));
        aboutMenu.add(about);
        
        menuBar.add(fileMenu);
        menuBar.add(aboutMenu);
        setJMenuBar(menuBar);
        
        
    }
    
    private void displayImage(File file) {
        try{
            currentImage = ImageIO.read(file);
            updateImage(currentImage);
        }catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Could not load the image!", "Error", 0);
        }
    }
    
    public static void main(String[] args) {
        ImageViewer applicationInstance = new ImageViewer();
        
        SwingUtilities.invokeLater(() -> {
            applicationInstance.setVisible(true);
        });
    
    }

    private void openFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION){
            File folder = chooser.getSelectedFile();
            File[] files = folder.listFiles((dir, name) -> {
                String lName = name.toLowerCase();
                return lName.endsWith(".jpg") || lName.endsWith(".png") || lName.endsWith(".jpeg") || lName.endsWith(".gif");
            });
            
            if(files != null){
                imageFiles.clear();
                thumbnailPanel.removeAll();
                
                for(File file: files){
                    imageFiles.add(file);
                    ImageIcon thumb = new ImageIcon(file.getAbsolutePath());
                    Image image = thumb.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    JButton thumbButton = new JButton(new ImageIcon(image));
                    thumbButton.setPreferredSize(new Dimension(90,90));
                    thumbButton.addActionListener(e -> displayImage(file));
                    thumbnailPanel.add(thumbButton);
                }
                
                thumbnailPanel.revalidate();
                thumbnailPanel.repaint();
                
                
                if(!imageFiles.isEmpty()){
                    displayImage(imageFiles.get(0));
                }
            }
        }
        
    }

    private void clearAll() {
        imageLabel.setIcon(null);
        imageLabel.setText("Drop an image here ...");
        thumbnailPanel.removeAll();
        thumbnailPanel.revalidate();
        thumbnailPanel.repaint();
        imageFiles.clear();
        currentImage = null;
    }
    
}
