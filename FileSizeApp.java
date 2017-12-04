package filesizeapp;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JComboBox;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.math.BigDecimal;
import java.awt.Font;
import java.awt.Desktop;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
public class FileSizeApp extends JFrame implements ActionListener {
    Container contentpane = getContentPane();
    JPanel pnlHead = new JPanel();
    JPanel pnlSize = new JPanel();
    JPanel grid = new JPanel(new GridLayout(8,10));
    JPanel West = new JPanel();
    
    public int maxFiles = 1000;
    public int count = 0;
    public String currentDirectory = "";
    public JButton [] folders = new JButton[maxFiles];
    public File folder = new File("C:/");
    public File[] listOfFiles = folder.listFiles();
    public JButton [] files = new JButton[maxFiles];
    public JLabel FileSize = new JLabel(" KB");
    public JButton goBack = new JButton("Go Back");
    public JLabel percentageLabel = new JLabel();
    public JLabel percentageLabelText = new JLabel("% Complete");
    public JButton openfolder = new JButton("Open Folder in windows explorer");
    public FileSizeApp(){
        super("File Size Application");
        setSize(1080, 800);
        setDefaultCloseOperation( EXIT_ON_CLOSE );

        contentpane.add("North", pnlHead);
        contentpane.add("Center", grid);
        contentpane.add("West", West);
        contentpane.add("South", pnlSize);
        setVisible( true );
    }
    public void actionPerformed( ActionEvent event){
                if(event.getSource() instanceof JButton){
                if(event.getSource() == openfolder){
                    try {
                        Desktop.getDesktop().open(new File(currentDirectory));
                    } catch (IOException ex) {
                        Logger.getLogger(FileSizeApp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else{
                    //remove all old things
                    for(int c = 0; c<count; c++){
                        if(folders[c] != null){ //delete all folders
                        grid.remove(folders[c]);
                        folders[c] = null;
                        }
                        if(files[c] != null){ //delete all files
                        grid.remove(files[c]);
                        files[c] = null;
                        }    
                        }
                    pnlSize.remove(FileSize);
                    pnlHead.remove(goBack);
                    pnlHead.remove(openfolder);
                    goBack = null;
                    FileSize = null;
                    revalidate();
                    repaint();
                    System.out.println("test");
                    setVisible(true);

                    createSymbols(event.getActionCommand()); //create folders from new directory
                }
            }
        }
    
    
    public static void main(String[] args) {
        FileSizeApp gui = new FileSizeApp();
        gui.driveSelect();

    }
    public void driveSelect(){
        File[] roots = File.listRoots();
        for(int i = 0; i < roots.length ; i++){
        folders[i] = new JButton(String.valueOf(roots[i]));
        folders[i].setActionCommand(String.valueOf(roots[i])); //set additional information for button (directory)
        folders[i].addActionListener(this);
        grid.add(folders[i]);
        }
        setVisible(true);
    }
    public void createSymbols(String directory){
        

        //go back button
        currentDirectory = directory;
        String oldFolder = directory;
        String[] seperated = oldFolder.split("\\\\");
        oldFolder = "";
        for(int i = 0; i<seperated.length-1;i++){
            oldFolder+=seperated[i];
            oldFolder+="\\";}
        System.out.println(oldFolder);
        goBack = new JButton("Go Back");
        goBack.setActionCommand(oldFolder); //set additional information for button (directory)
        pnlHead.add(goBack);
        pnlHead.add(openfolder);
        openfolder.addActionListener(this);
        if(goBack.getActionCommand() == null || String.valueOf(goBack.getActionCommand())==""){
            goBack.setEnabled(false);
        }else {goBack.setEnabled(true);
        goBack.addActionListener(this);}
        //go back button end
        
        folder = new File(directory);
        listOfFiles = folder.listFiles();
        
        //percentage and file size calculater 
        count = 0;
        for(int i = 0; i<listOfFiles.length; i++){
            if(listOfFiles[i]!=null){
                count++;
            }
        }
        
        
        float percentage = 0;
        float c = 1;
        
        float[] fileSizes = new float[count]; //calculate subdirectories size
        JLabel percentageLabel = new JLabel();
        JLabel percentageLabelText = new JLabel("% Complete");
        pnlSize.add(percentageLabel);
        pnlSize.add(percentageLabelText);        
        for(int i = 0; i < count; i++) {
            System.out.println(i);
            percentage = ((c)/count)*100;
            BigDecimal bd = new BigDecimal(Float.toString(percentage));
            bd = bd.setScale(0, BigDecimal.ROUND_HALF_DOWN);
            c++;
            percentageLabel.setText(String.valueOf(bd));        
            fileSizes[i] = calculateFileSize(listOfFiles[i]);   
            revalidate();
        }
        
        pnlSize.remove(percentageLabel);
        pnlSize.remove(percentageLabelText);
        percentageLabel = null;
        percentageLabelText = null;
        float totalFileSizes = 0;
        for(int i = 0; i<fileSizes.length; i++){ //add all subdirectories
            totalFileSizes+=fileSizes[i];
        }
        
        
        //print out total size of folder
        if(totalFileSizes/1024000000>1){
            BigDecimal fs = new BigDecimal(Float.toString(totalFileSizes/1024000000));
            fs = fs.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            String FileSizeString = String.valueOf(fs);
            FileSize = new JLabel(FileSizeString +" GigaBytes"); //get file size
        }else if(totalFileSizes/1024000>1){
            BigDecimal fs = new BigDecimal(Float.toString(totalFileSizes/1024000));
            fs = fs.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            String FileSizeString = String.valueOf(fs);
            FileSize = new JLabel(FileSizeString +" MegaBytes"); //get file size
        }else if(totalFileSizes/1024>1){
            BigDecimal fs = new BigDecimal(Float.toString(totalFileSizes/1024));
            fs = fs.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            String FileSizeString = String.valueOf(fs);
            FileSize = new JLabel(FileSizeString +" KiloBytes"); //get file size
        }else{
            BigDecimal fs = new BigDecimal(Float.toString(totalFileSizes));
            fs = fs.setScale(2, BigDecimal.ROUND_HALF_DOWN);
            String FileSizeString = String.valueOf(fs);
            FileSize = new JLabel(FileSizeString +" Bytes"); //get file size
        }
        pnlSize.add(FileSize);
       //percentage and file size calculater end
        
        //create buttons etc
        if(listOfFiles != null){
        for (int i = 0; i < listOfFiles.length; i++) {  //design folders and files
            if (listOfFiles[i].isFile()) {
              files[i] = new JButton(listOfFiles[i].getName());
            } else if (listOfFiles[i].isDirectory()) {
              folders[i] = new JButton(listOfFiles[i].getName());
              folders[i].setActionCommand(String.valueOf(listOfFiles[i])); //set additional information for button (directory)
             }
        }}
        
        for (int i = 0; i < count; i++) { //add all folders if they are not null
            if(folders[i] != null){
                BigDecimal rc = null;
                float redColor = fileSizes[i]/totalFileSizes*255;
                rc = new BigDecimal(Float.toString(redColor));
                rc = rc.setScale(0, BigDecimal.ROUND_HALF_DOWN);
                int redColorInt = Integer.valueOf(rc.intValue());
                System.out.println(redColorInt);
                folders[i].setBackground(new Color(255-redColorInt,255-redColorInt,255-redColorInt));
                grid.add(folders[i]);
                folders[i].setForeground(Color.red);
                folders[i].setFont(new Font("Avenir", Font.BOLD, 15));
                folders[i].addActionListener(this);}}

        for (int i = 0; i < files.length; i++) { //ad all files after folders if they are not null
            if(files[i] != null){
                grid.add(files[i]);
                files[i].setEnabled(false);}}

        setVisible( true );
    }
    
    //calculate file size method
    public static float calculateFileSize(File file) {
        float fileSize = 0L;
        if(file.isDirectory() && file.exists()) {
           File[] children = file.listFiles();
           if (children != null) {
           for(File child : children) {
             fileSize += calculateFileSize(child);
           }
        }
        }
        else {
          fileSize = file.length();
        }
        return fileSize;
      }

}



    

