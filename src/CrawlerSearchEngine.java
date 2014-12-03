package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        setSize(800, 800);

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
        layout.setConstraints(startTF, constraints);
        searchPanel.add(startTF);


        JLabel maxLabel = new JLabel("Max URLs to crawl :");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(maxLabel, constraints);
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


        JLabel progressLB = new JLabel();
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 0, 0);
        layout.setConstraints(progressLB, constraints);
        searchPanel.add(progressLB);


        progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setStringPainted(true);
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 0, 5);
        layout.setConstraints(progressBar, constraints);
        searchPanel.add(progressBar);


        JLabel matchesLB2 = new JLabel("Search matches :");
        constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(5, 5, 10, 0);
        layout.setConstraints(matchesLB2, constraints);
        searchPanel.add(matchesLB2);


        matchesLB = new JLabel();
        matchesLB.setFont(matchesLB.getFont().deriveFont(Font.PLAIN));
        constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        constraints.insets = new Insets(5, 5, 10, 5);
        layout.setConstraints(matchesLB, constraints);
        searchPanel.add(matchesLB);


        //set up matches table
        table = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"URL"})){
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };

        //set up matches panel
        JPanel matchesPanel = new JPanel();
        matchesPanel.setBorder(BorderFactory.createTitledBorder("Matches"));
        matchesPanel.setLayout(new BorderLayout());
        matchesPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        //add panel to display
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(searchPanel, BorderLayout.NORTH);
        getContentPane().add(matchesPanel, BorderLayout.CENTER);
    }

    private void actionSearch(){
        //if stop button clicked turns crawling flag off
        if(crawling) {
            crawling = false;
            return;
        }

        ArrayList<String> errorsList = new ArrayList<String>();

        //validate that start URLs has been entered
        String startURL = startTF.getText().trim();
        if(startURL.length() < 1) {
            errorsList.add("Missing start URL");
        } else if (verifyURL(startURL) == null){
            errorsList.add("Invalid start URL");
        }

        //validate max URLs is either empty or is a number
        int maxURLs = 0;
        String max = ((String) maxComboBox.getSelectedItem()).trim();

        if(max.length() > 0) {
            try {
                maxURLs = Integer.parseInt(max);
            } catch (NumberFormatException e){
                e.printStackTrace();
            }
            if(maxURLs < 1) {
                errorsList.add("Invalid max number value");
            }
        }

        //validate that matches log file has been entered
        String logFile = logTF.getText().trim();
        if(logFile.length() < 1) {
            errorsList.add("Missing Matches Log File");
        }

        //validate that search string has been entered
        String searchString = searchTF.getText().trim();
        if(searchString.length() < 1){
            errorsList.add("Missing search string");
        }

        //show errors, if any, and return
        if(errorsList.size() > 0){
            StringBuffer message = new StringBuffer();

            //concatenate error into string message
            for(String error: errorsList){
                message.append(error);
                message.append("\n");
            }

            showError(message.toString());
            return;
        }

        //Remove "www" from start URL if present
        startURL = removeWwwFromURL(startURL);

        //Start the search crawler
        search(logFile, startURL, maxURLs, searchString);
    }

    protected void actionExit(){
        System.exit(0);
    }

    private void search(final String logFile, final String startURL, final int maxURLs, final String searchString){
        //Start the Search in new Thread
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //Show hour glass cursor while crawling is under way
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                //disable search control
                startTF.setEnabled(false);
                maxComboBox.setEnabled(false);
                limitCheckBox.setEnabled(false);
                logTF.setEnabled(false);
                searchTF.setEnabled(false);
                caseCheckBox.setEnabled(false);

                //swtich search button to "STOP"
                searchButton.setText("STOP");

                //Reset stats
                table.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"URL"}){
                    public boolean isCellEditable(int row, int column){
                        return false;
                    }
                });
                updateStats(startURL, 0, 0, maxURLs);

                //Open matches Log File
                try{
                    logFileWriter = new PrintWriter(new FileWriter(logFile));
                } catch(IOException e){
                    showError("Unable to open matches Log File");
                    return;
                }

                //turn crawling flag on
                crawling = true;

                //perform the actual crawling
                crawl(startURL, maxURLs, limitCheckBox.isSelected(), searchString, caseCheckBox.isSelected());

                //turn crawling flag off
                crawling = false;

                //close matches log file
                try{
                    logFileWriter.close();
                }catch (Exception e){
                    showError("Unable to close matches Log File");
                    return;
                }

                //mark search as done
                crawlingLB.setText("Done");

                //Enable search control
                startTF.setEnabled(true);
                maxComboBox.setEnabled(true);
                limitCheckBox.setEnabled(true);
                logTF.setEnabled(true);
                searchTF.setEnabled(true);
                caseCheckBox.setEnabled(true);


                //switch search button back to "search"
                searchButton.setText("Search");

                //return default Cursor
                setCursor(Cursor.getDefaultCursor());

                //Show message if search string is not found
                if(table.getRowCount() == 0 ){
                    JOptionPane.showMessageDialog(CrawlerSearchEngine.this, "Your search String is not found",
                            "Search String not found",JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        thread.start();
    }

    //show dialog box if error
    private void showError(String messages){
        JOptionPane.showMessageDialog(this, messages, "Error", JOptionPane.ERROR_MESSAGE);
    }

    //update crawling stats
    private void updateStats(String crawling, int crawled, int toCrawl, int maxUrls){
        crawlingLB.setText(crawling);
        crawlingLB.setText(" " + crawled);
        crawlingLB.setText(" " + toCrawl);

        //update progress bar
        if(maxUrls == -1){
            progressBar.setMaximum(crawled + toCrawl);
        } else {
            progressBar.setMaximum(maxUrls);
        }
        progressBar.setValue(crawled);

        matchesLB.setText(" " + table.getRowCount());
    }

    //add match to matches table and log file
    private void addMatch(String url){
        //Add URL to matches table
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.addRow(new Object[]{url});

        //Add URL to matches log file
        try{
            logFileWriter.println(url);
        } catch (Exception e){
            showError("Unable to match log file");
        }
    }

    //verify URL format
    private URL verifyURL(String url){
        //Only allow HTTP URL
        if(!url.toLowerCase().startsWith("http://")){
            return null;
        }

        //verify format URL
        URL verifiedURL = null;
        try{
            verifiedURL = new URL(url);
        }catch (MalformedURLException e){
            return null;
        }

        return verifiedURL;
    }

    //Check if robot is allowed to access the given URL
    private boolean isRobotAllowed(URL urlToCheck){
        String host = urlToCheck.getHost().toLowerCase();

        //Retrieve host's disallow last form cache
        ArrayList disallowList = (ArrayList) disallowListURLCache.get(host);

        //if list is not in the cache, download and cache it
        if(disallowList == null){
            disallowList = new ArrayList();

            try {
                URL robotsFileURl = new URL("http://"+ urlToCheck);

                //open connection to robot file URL for reading
                BufferedReader reader = new BufferedReader(new InputStreamReader(robotsFileURl.openStream()));

                //Read robot file, creating list or disallowed paths
                String line;
                while((line = reader.readLine()) != null){
                    if(line.indexOf("Disallow:") == 0) {
                        String disallowPath = line.substring("Disallow:".length());

                        //check disallow path for comments and remove if present
                        int commentIndex = disallowPath.indexOf("#");
                        if(commentIndex != -1) {
                            disallowPath = disallowPath.substring(0, commentIndex);
                        }

                        //remove leading of trailing spaces from disallow path
                        disallowPath = disallowPath.trim();

                        //add disallow path to list
                        disallowList.add(disallowPath);
                    }
                }

                //add new disallow list to cache
                disallowListURLCache.put(host, disallowList);

            } catch (Exception e){
                //assume robot is allowed since an exception is thrown if the robot file doesn't exit
                return true;
            }
        }

        //Loop through disallow list to see if the crawling is allowed for given URL
        String file = urlToCheck.getFile();
        for(int i=0; i < disallowList.size(); i++){
            String disallow = (String) disallowList.get(i);

            if(file.startsWith(disallow)){
                return  false;
            }
        }

        return true;
    }

    //Download page at URL
    private String downloadPage(URL pageURL){
        try{
            //open connection to URL for reading
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream()));

            //Read file into buffer
            String line;
            StringBuffer pageBuffer = new StringBuffer();
            while((line = reader.readLine()) != null){
                pageBuffer.append(line);
            }

            return pageBuffer.toString();
        } catch (Exception e){

        }
        return null;
    }

    //Remove leading www from URL's host if present
    private String removeWwwFromURL(String url){
        int index = url.indexOf("://www.");
        if(index != -1){
            return url.substring(0, index + 3) + url.substring(index + 7);
        }
        return (url);
    }

    //parse through page contents and retrieve links
    private ArrayList retrieveLinks(URL pageURL, String pageContents, HashSet crawledList, boolean limitHost){
        //complete link matching pattern
        Pattern p = Pattern.compile("<a\\s+href\\s*=\\s*\"?(.*?)[\"|>]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(pageContents);

        //create list of link matches
        ArrayList linkList = new ArrayList();
        while(m.find()){
            String link = m.group(1).trim();

            //skip empty links
            if(link.length() < 1){
                continue;
            }

            //skip links that are just page anchor
            if(link.charAt(0) == '#'){
                continue;
            }

            //skip mailto links
            if(link.indexOf("mailto:") != -1){
                continue;
            }

            //skip javascript links
            if(link.toLowerCase().indexOf("javascript") != -1){
                continue;
            }

            //prefix absolute and relative url if necessary
            if(link.indexOf("://") == -1){
                String file = pageURL.getFile();
                //Hanle absolute links
                if(link.charAt(0) == '/'){
                    link = "http://" + pageURL.getHost() + "/" + link;
                } else {
                    String path = file.substring(0, file.lastIndexOf('/') + 1);
                    link = "http://" + pageURL.getHost() + path + link;
                }
            }

            //remove anchors from link
            int index = link.indexOf('#');
            if(index != -1){
                link = link.substring(0, index);
            }

            //remove leading www. from URL's host if present
            link = removeWwwFromURL(link);

            //verify link and skip if invalid
            URL verifiedLink = verifyURL(link);

            if(verifiedLink == null) {
                continue;
            }

            //if specified, limit links to those having the same host as the start URL
            if( limitHost && !pageURL.getHost().toLowerCase().equals(verifiedLink.getHost().toLowerCase())){
                continue;
            }

            //skip link if it has already crawled
            if(crawledList.contains(link)){
                continue;
            }

            //add link to list
            linkList.add(link);
        }

        return (linkList);
    }

    // determine whether or not search string is matched in the given page content
    private boolean searchStringMatches(String pageContents, String searchString, boolean caseSensitive){
        String searchContents = pageContents;

        //if case sensitive search, low sensitive page contents for comparsion
        if(!caseSensitive) {
            searchContents = pageContents.toLowerCase();
        }

        //split search string to individual term
        Pattern p = Pattern.compile("[\\s]+");
        String[] terms = p.split(searchString);

        //check to see if each terms matches
        for(int i = 0; i < terms.length; i++){
            if(caseSensitive){
                if(searchContents.indexOf(terms[i]) == -1)
                    return false;
            } else {
                if(searchContents.indexOf(terms[i].toLowerCase()) == -1)
                    return false;
            }
        }

        return true;
    }

    public void crawl(String startURL, int maxURLs, boolean limitHost, String searchString, boolean caseSensitive){
        //setup crawl list
        HashSet crawledList = new HashSet();
        LinkedHashSet toCrawlList = new LinkedHashSet();

        toCrawlList.add(startURL);

        //perform actual crawling by looping through the to crawl list
        while( crawling && toCrawlList.size() > 0){
            //check to see if max URL count has been reached, if it was specified
            if( maxURLs != -1){
                if(crawledList.size() == maxURLs){
                    break;
                }
            }

            //get URL a bottom of the list
            String url = (String) toCrawlList.iterator().next();

            //remove URL from crawl List
            toCrawlList.remove(url);

            // convert string url to URL object
            URL verifiedURL = verifyURL(url);

            // skip URL if robots are not allowed to access it
            if(!isRobotAllowed(verifiedURL)){
                continue;
            }

            //Update crawling stats
            updateStats(url, crawledList.size(), toCrawlList.size(), maxURLs);

            //add page to crawledList
            crawledList.add(url);

            //download the page at given URL
            String pageContents = downloadPage(verifiedURL);

            //If the page was downloaded successfully, retrieve full all of its links and then see if it contains search string
            if(pageContents != null && pageContents.length() > 0){
                ArrayList links = retrieveLinks(verifiedURL, pageContents, crawledList, limitHost);

                //add links to the toCrawlList
                toCrawlList.addAll(links);

                //check if search string is present in page and if so record and match
                if(searchStringMatches(pageContents, searchString, caseSensitive)){
                    addMatch(url);
                }
            }

            //update crawling stats
            updateStats(url, crawledList.size(), toCrawlList.size(), maxURLs);
        }
    }

    public static void main(String[] args){
        CrawlerSearchEngine crawler = new CrawlerSearchEngine();
        crawler.setVisible(true);
    }
}
