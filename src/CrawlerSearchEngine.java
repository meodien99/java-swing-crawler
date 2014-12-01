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
        GridBagConstraints constraints;
        GridBagLayout layout = new GridBagLayout();
        searchPanel.setLayout(layout);

        JLabel startLB = new JLabel("Start URL : ");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0 , 0); // same as border in css: top - right - bot - left size
        layout.setConstraints(startLB, constraints);
        searchPanel.add(startLB);


        startTF = new JTextField();
        constraints = new GridBagConstraints();
        //fill textfield with remain size
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(startLB, constraints);
        searchPanel.add(startTF);


        JLabel maxLabel = new JLabel("Max URLs to crawl :");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(limitCheckBox, constraints);
        searchPanel.add(maxLabel);


        maxComboBox = new JComboBox(URLS);
        maxComboBox.setEditable(true);
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxComboBox, constraints);
        searchPanel.add(maxComboBox);


        limitCheckBox = new JCheckBox("Limit crawling to Start URL site :");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.WEST;
        constraints.insets = new Insets(0, 10, 0, 0);
        layout.setConstraints(limitCheckBox, constraints);
        searchPanel.add(limitCheckBox);


        JLabel blankLB = new JLabel();
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(blankLB, constraints);
        searchPanel.add(blankLB);


        JLabel logLB = new JLabel("Matches Log File:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(logLB, constraints);
        searchPanel.add(logLB);


        String file = System.getProperty("user.dir") + System.getProperty("file.separator") + "crawler.log";
        logTF = new JTextField(file);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(logTF, constraints);
        searchPanel.add(logTF);


        JLabel searchLB = new JLabel("Search String:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(searchLB, constraints);
        searchPanel.add(searchLB);


        searchTF = new JTextField();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(5, 5, 0, 0);
        constraints.gridwidth = 2;
        constraints.weightx = 1.0d;
        layout.setConstraints(searchTF, constraints);
        searchPanel.add(searchTF);


        caseCheckBox = new JCheckBox("Case Sensitive:");
        constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 0, 5);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        layout.setConstraints(caseCheckBox, constraints);
        searchPanel.add(caseCheckBox);


        searchButton = new JButton("Search :");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                actionSearch();
            }
        });
        constraints = new GridBagConstraints();
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(searchButton, constraints);
        searchPanel.add(searchButton);


        JSeparator separator = new JSeparator();
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 5, 5);
        layout.setConstraints(separator, constraints);
        searchPanel.add(separator);


        JLabel crawlingLB2 = new JLabel("Crawling : ");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawlingLB2, constraints);
        searchPanel.add(crawlingLB2);


        crawlingLB = new JLabel();
        crawlingLB.setFont(crawlingLB.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawlingLB, constraints);
        searchPanel.add(crawlingLB);


        JLabel crawledLB2 = new JLabel("Crawled URLs: ");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(crawledLB2, constraints);
        searchPanel.add(crawledLB2);


        crawledLB = new JLabel();
        crawledLB.setFont(crawledLB.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(crawledLB, constraints);
        searchPanel.add(crawledLB);


        JLabel toCrawlLB2 = new JLabel("URLs to Crawl:");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(toCrawlLB2, constraints);
        searchPanel.add(toCrawlLB2);


        toCrawlLB = new JLabel();
        toCrawlLB.setFont(toCrawlLB.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(toCrawlLB, constraints);
        searchPanel.add(toCrawlLB);



    }

    private void actionSearch(){
        //if stop button clicked turns crawling flag off
    }

    protected void actionExit(){
        System.exit(0);
    }
}
