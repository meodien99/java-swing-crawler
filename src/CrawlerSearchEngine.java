package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Created by madcat on 12/1/14.
 */
public class CrawlerSearchEngine extends JFrame {
    //Max URLs dropdown values
    private static final String[] URLS = {"50","100","150","200"};

    //Cache of robot disabllow lists
    private HashMap disallowListURLCache = new HashMap();

    //Search GUI controls
    private JTextField startTF, logTF, searchTF;
    private JComboBox maxComboBox;
    private JCheckBox limitCheckBox, caseCheckBox;
    private JButton searchButton;

    //Search stats GUI controls
    private JLabel crawlingLB, crawledLB, toCrawlLB, matchesLB;
    private JProgressBar progressBar;

    //Table listing search matches
    private JTable table;

    //Flag for wether or not crawling is underway
    private boolean crawling;

    //Matches Log file Print Writer
    private PrintWriter logFileWriter;

    public CrawlerSearchEngine(){
        //Set application title
        setTitle("Crawler Search Engine");

        //set window size
        setSize(600, 600);

        //Handle window closing events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                actionExit();
            }
        });

        //setup file menu
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
        fileExitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                actionExit();
            }
        });
        fileMenu.add(fileExitMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        //setup search panel
        JPanel searchPanel = new JPanel();
        GridBagConstraints contraints;
        GridBagLayout layout = new GridBagLayout();
        searchPanel.setLayout(layout);

        JLabel startLB = new JLabel("Start URL : ");
        contraints = new GridBagConstraints();
        contraints.anchor = GridBagConstraints.EAST;
        contraints.insets = new Insets(5, 5, 0 , 0); // same as border in css: top - right - bot - left size
        layout.setConstraints(startLB, contraints);
        searchPanel.add(startLB);


        startTF = new JTextField();
        contraints = new GridBagConstraints();
        //fill textfield with remain size
        contraints.fill = GridBagConstraints.HORIZONTAL;
        contraints.gridwidth = GridBagConstraints.REMAINDER;
        contraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(startLB, contraints);
        searchPanel.add(startTF);


        JLabel maxLabel = new JLabel("Max URLs to crawl :");
        contraints = new GridBagConstraints();
        contraints.anchor = GridBagConstraints.EAST;
        contraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(limitCheckBox, contraints);
        searchPanel.add(maxLabel);


        maxComboBox = new JComboBox(URLS);
        maxComboBox.setEditable(true);
        contraints = new GridBagConstraints();
        contraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxComboBox, contraints);
        searchPanel.add(maxComboBox);


        limitCheckBox = new JCheckBox("Limit crawling to Start URL site :");
        contraints = new GridBagConstraints();
        contraints.anchor = GridBagConstraints.WEST;
        contraints.insets = new Insets(0, 10, 0, 0);
        layout.setConstraints(limitCheckBox, contraints);
        searchPanel.add(limitCheckBox);


        JLabel blankLB = new JLabel();
        contraints = new GridBagConstraints();
        contraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(blankLB, contraints);
        searchPanel.add(blankLB);


        JLabel logLB = new JLabel("Matches Log File:");
        contraints = new GridBagConstraints();
        contraints.anchor = GridBagConstraints.EAST;
        contraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(logLB, contraints);
        searchPanel.add(logLB);


        String file = System.getProperty("user.dir") + System.getProperty("file.separator") + "crawler.log";
        logTF = new JTextField(file);
    }

    protected void actionExit(){
        System.exit(0);
    }
}
