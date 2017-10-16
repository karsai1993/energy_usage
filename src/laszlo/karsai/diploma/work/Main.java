package laszlo.karsai.diploma.work;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Main {
	private static Connection connect = null;
    private static Statement statement = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null;
    private static String basicViewName = "Basic View";
    private static String advancedViewName = "Advanced View";
    private static String alertsByUserName = "Alerts Based On User Input";
    private static String alertsByDefaultName = "Alerts Regarding Basic Applications";
    private static String analyzeName = "Analyze Popular Process Data";
    private static int counterForMock = 0;
    private static Boolean hasIntelProcessor = false;
    private static Boolean isAppropriateAppInstalled = false;
    private static int sleepTimeInSeconds = 3;
    private static double pricePerKiloWattHour = 37;
    
  public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException, URISyntaxException {
	  //Class.forName("com.mysql.jdbc.Driver");
	  Path currentRelativePath = Paths.get("");
	  String currentPath = currentRelativePath.toAbsolutePath().toString();
	  hasIntelProcessor = checkIfProcessorIsIntel();
	  isAppropriateAppInstalled = checkIfAppropriateAppIsInstalled();
	  if (hasIntelProcessor) {
		  File file = new File("PowerLog3.0.exe");
		  if (!file.exists()) {
			  JOptionPane.showMessageDialog(null, "PowerLog3.0.exe is not found at location: "+currentPath, "Error", JOptionPane.ERROR_MESSAGE);
			  System.exit(0);
		  }
	  }
	  File file = new File("energy_usage.db");
	  if (!file.exists()) {
		  JOptionPane.showMessageDialog(null, "energy_usage.db is not found at location: "+currentPath, "Error", JOptionPane.ERROR_MESSAGE);
		  System.exit(0);
	  }
	  
	  String url = "jdbc:sqlite:"+currentPath+"/energy_usage.db";
	  connect = DriverManager.getConnection(url);
	  statement = connect.createStatement();
	  /*
	   * 1,kezdeni kell valamit a hasIntelProcessor-ral, hogy csak akkor
	   * legyenek power értékek,ha hasIntelProcessor true KÉSZ
	   * 2, basic power handling -> ott csak bizonyos process-ek vannak megjelenítve,
	   * de a power ugyanaz, mint az advanced esetén KÉSZ
	   * 3, future prediction advanced esetén -> külön funkció KÉSZ
	   * 4, description-t befejezni KÉSZ
	   * 5, kód egyszerűsítés KÉSZ
	   * 6, beolvasásnál umlaut y kezelés KÉSZ
	   * 7, portable database KÉSZ
	   * 8, UX szempontok összegyűjtése/hozzáadása
	   */
	  startWelcomeGUIDnD();
  }
  
  public static JPanel getConsumptionPrediction(PowerData powerData, List<ProcessData> processData, long durationInMiliSec) {
	  if (powerData == null) {
		  return null;
	  }
	  Collections.sort(processData,new Comparator<ProcessData>() {
	  	    @Override
	  	    public int compare(ProcessData first, ProcessData second) {
	  	        return Double.compare(second.getCpuUsage(),first.getCpuUsage());
	  	    }
	  });
	  JPanel panel = new JPanel(new GridBagLayout());
	  long oneHourInMiliSec = 1 * 60 * 60 * 1000;
	  long oneDayInMiliSec = oneHourInMiliSec * 24;
	  long oneWeekInMiliSec = oneDayInMiliSec * 7;
	  long twoWeeksInMiliSec = oneWeekInMiliSec * 2;
	  double powerValue = 0;
	  double energyInKiloWattHour = 0;
	  double energyPrice = 0;
	  double sumOfOneHourConsumption = 0;
	  double sumOfOneDayConsumption = 0;
	  double sumOfOneWeekConsumption = 0;
	  double sumOfTwoWeeksConsumption = 0;
	  double currOfOneHourConsumption = 0;
	  double currOfOneDayConsumption = 0;
	  double currOfOneWeekConsumption = 0;
	  double currOfTwoWeeksConsumption = 0;
	  GridBagConstraints c = new GridBagConstraints();
	  c.fill = GridBagConstraints.HORIZONTAL;
	  c.weightx = 1;
	  c.gridy = 5;
	  c.gridx = 0; 
	  panel.add(new JLabel("Process Name"), c);
	  c.gridx = 1;
	  panel.add(new JLabel("One Hour Consumption [HUF]"),c);
	  c.gridx = 2;
	  panel.add(new JLabel("One Day Consumption [HUF]"),c);
	  c.gridx = 3;
	  panel.add(new JLabel("One Week Consumption [HUF]"),c);
	  c.gridx = 4;
	  panel.add(new JLabel("Two Weeks Consumption [HUF]"),c);
	  for (ProcessData data : processData) {
		  if (!data.getName().equals("System Idle Process")) {
			  c.gridy ++;
			  c.gridx = 0; 
			  panel.add(new JLabel(data.getName()), c);
			  powerValue = powerData.getPowerInWatt() * data.getCpuUsage();
			  energyInKiloWattHour = powerValue * durationInMiliSec / 1000 / 3600 / 1000;
			  energyPrice = energyInKiloWattHour * pricePerKiloWattHour;
			  c.gridx = 1;
			  currOfOneHourConsumption = energyPrice * oneHourInMiliSec / durationInMiliSec;
			  panel.add(new JLabel(String.valueOf(roundFiveDigits(currOfOneHourConsumption))),c);
			  c.gridx = 2;
			  currOfOneDayConsumption = energyPrice * oneDayInMiliSec / durationInMiliSec;
			  panel.add(new JLabel(String.valueOf(roundFiveDigits(currOfOneDayConsumption))),c);
			  c.gridx = 3;
			  currOfOneWeekConsumption = energyPrice * oneWeekInMiliSec / durationInMiliSec;
			  panel.add(new JLabel(String.valueOf(roundFiveDigits(currOfOneWeekConsumption))),c);
			  c.gridx = 4;
			  currOfTwoWeeksConsumption = energyPrice * twoWeeksInMiliSec / durationInMiliSec;
			  panel.add(new JLabel(String.valueOf(roundFiveDigits(currOfTwoWeeksConsumption))),c);
			  if (!Double.isNaN(powerValue)) {
				  sumOfOneHourConsumption += currOfOneHourConsumption;
				  sumOfOneDayConsumption += currOfOneDayConsumption;
				  sumOfOneWeekConsumption += currOfOneWeekConsumption;
				  sumOfTwoWeeksConsumption += currOfTwoWeeksConsumption;
			  }
		  }
	  }
	  c.gridx = 0;
	  c.gridy = 0;
	  panel.add(new JLabel("Summary Of Energy Cost Predicted For One Hour [HUF]: "+roundFiveDigits(sumOfOneHourConsumption)),c);
	  c.gridy = 1;
	  panel.add(new JLabel("Summary Of Energy Cost Predicted For One Day [HUF]: "+roundFiveDigits(sumOfOneDayConsumption)),c);
	  c.gridy = 2;
	  panel.add(new JLabel("Summary Of Energy Cost Predicted For One Week [HUF]: "+roundFiveDigits(sumOfOneWeekConsumption)),c);
	  c.gridy = 3;
	  panel.add(new JLabel("Summary Of Energy Cost Predicted For Two Weeks [HUF]: "+roundFiveDigits(sumOfTwoWeeksConsumption)),c);
	  c.gridy = 4;
	  panel.add(new JLabel(" "),c);
	  panel.repaint();
	  panel.revalidate();
	  return panel;
  }
  
  public static PowerData getPowerData() throws IOException, URISyntaxException {
	  if (!hasIntelProcessor || !isAppropriateAppInstalled) {
		  return null;
	  }
	  Process process = new ProcessBuilder("PowerLog3.0.exe","-duration","1","-verbose").start();
      InputStream stdout = process.getInputStream ();
	  BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
	  String line;
	  double power = 0;
	  String [] parts = null;
	  List<PowerData> powerDataList = new ArrayList<>();
	  while ((line = reader.readLine ()) != null) {
		  if (line.contains("IA power")) {
			  parts = line.split("=");
			  power = Double.parseDouble(parts[1].trim());
			  powerDataList.add(new PowerData(power));
		  }
	  }
	  int listElementCount = powerDataList.size();
	  double sumOfPower = 0;
	  for (int i = 0; i < listElementCount; i++) {
		  sumOfPower += powerDataList.get(i).getPowerInWatt();
	  }
	  return new PowerData(round(sumOfPower / listElementCount));
  }
  
  public static Boolean checkIfAppropriateAppIsInstalled() throws IOException, URISyntaxException {
	  if (!hasIntelProcessor) {
		  return false;
	  }
	  Process process = new ProcessBuilder("PowerLog3.0.exe","-duration","1","-verbose").start();
      InputStream stdout = process.getInputStream ();
	  BufferedReader reader = new BufferedReader (new InputStreamReader(stdout));
	  String line;
	  while ((line = reader.readLine ()) != null) {
		  if (line.contains("Error")) {
			  return false;
		  }
	  }
	  return true;
  }
  
  public static Boolean checkIfProcessorIsIntel() throws NumberFormatException, IOException {
	  InputStream is = null;
	  InputStreamReader isr = null;
	  BufferedReader br = null;

	  Boolean isIntel = false;

      List<String> command = new ArrayList<String>();
      command.add("WMIC");
      command.add("cpu");
      command.add("get");
      command.add("Name");
      
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String line;
          while ((line = br.readLine()) != null) {
          	line = line.trim();
          	if (!line.equals("") && !line.equals("Name")) {
          		isIntel = line.startsWith("Intel");
          	}
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
	  return isIntel;
  }
  
  public static double round(double value) {
	    if (Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
	    	return value;
	    }
	  	BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(2, RoundingMode.HALF_UP);
	    return bd.doubleValue();
  }
  
  public static double roundFiveDigits(double value) {
	  	if (Double.isNaN(value) || value == Double.POSITIVE_INFINITY) {
	    	return value;
	    }
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(5, RoundingMode.HALF_UP);
	    return bd.doubleValue();
}
  
  public static void addApplicationsWithValuesForDefaultAlerts() throws SQLException {
	  preparedStatement = connect.prepareStatement("delete from `default_limits`");
	  preparedStatement.executeUpdate();
	  List<String> defaultAlertApplicationList = new ArrayList<>();
	  defaultAlertApplicationList.add("iexplore.exe,"+5+","+80);
	  defaultAlertApplicationList.add("firefox.exe,"+5+","+80);
	  defaultAlertApplicationList.add("chrome.exe,"+5+","+80);
	  for (int i = 0; i < defaultAlertApplicationList.size(); i++) {
		  String [] currElement = defaultAlertApplicationList.get(i).split(",");
		  preparedStatement = connect.prepareStatement("insert into `default_limits` (`name`,`cpu_limit`,`memory_limit`)"
				   +" values (?,?,?)");
		  preparedStatement.setString(1, currElement[0]);
		  preparedStatement.setString(2, currElement[1]);
		  preparedStatement.setString(3, currElement[2]);
		  preparedStatement.executeUpdate();
	  }
  }
  public static void startBasicGUI(double cpuLimitValue, double memLimitValue, Boolean isUserAlertRequired, Boolean isDefaultAlertRequired, Boolean isPopularProcessAnalyzerRequired) {
	  JFrame frame = new JFrame("Energy Cost Observer - Basic");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    DefaultListModel<String> model = new DefaultListModel<>();
	    JList<String> elements = new JList<>(model);
	    elements.setEnabled(false);
	    JPanel instructionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	    JLabel instructionLabel = new JLabel("Below, you can see the currently running processes! Click for details!");
	    instructionPanel.add(instructionLabel);
	    instructionPanel.add(new JLabel("The list gets updated in every 10 seconds!"));
	    panel.add(instructionPanel,BorderLayout.PAGE_START);
	    panel.add(new JScrollPane(elements),BorderLayout.CENTER);
	    JPanel panelForInfoAndButton = new JPanel(new BorderLayout());
	    JPanel infoPanel = new JPanel(new BorderLayout());
	    JPanel basicDescriptionPanel = new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.NORTH;
	    c.gridx = 0;
	    c.gridy = 0;
	    JLabel titleLabel = new JLabel();
	    JLabel cpuLabel = new JLabel();
	    JLabel memLabel = new JLabel();
	    elements.addListSelectionListener(new ListSelectionListener() {	
			@Override
			public void valueChanged(ListSelectionEvent e) {
				infoPanel.removeAll();
				basicDescriptionPanel.removeAll();
				infoPanel.add(titleLabel,BorderLayout.PAGE_START);
				c.weightx = 1;
				c.gridx = 0;
				basicDescriptionPanel.add(cpuLabel,c);
				c.gridx = 1;
				basicDescriptionPanel.add(memLabel, c);
				infoPanel.add(basicDescriptionPanel, BorderLayout.PAGE_END);
				infoPanel.repaint();
				infoPanel.revalidate();
			}
		});
	    infoPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
	    JPanel panelForBtn = new JPanel(new GridBagLayout());
	    c.weightx = 1;
	    JButton backBtn = new JButton("Back To Welcome Screen");
	    backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startWelcomeGUIDnD();
				frame.dispose();
			}
		});
	    panelForBtn.add(backBtn,c);
	    JPanel consumptionPanel = new JPanel(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane(consumptionPanel); 
		scrollPane.setPreferredSize( new Dimension( 1200, 275 ) );
		consumptionPanel.addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent arg0) {
				Window window = SwingUtilities.getWindowAncestor(consumptionPanel);
				if (window instanceof Dialog) {
					Dialog dialog = (Dialog) window;
					if (!dialog.isResizable()) {
						dialog.setResizable(true);
					}
				}
			}
		});
	    JButton consumptionBtn = new JButton("Get Predicted Costs For Current Consumption");
	    consumptionBtn.setEnabled(false);
	    consumptionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, scrollPane, "Energy Cost Observer - Basic - Consumption Prediction" , JOptionPane.PLAIN_MESSAGE);  
			}
		});
	    c.gridx = 2;
	    if (hasIntelProcessor && isAppropriateAppInstalled) {
		    panelForBtn.add(consumptionBtn,c);
	    }
	    JButton endBtn = new JButton("End Process");
	    endBtn.setEnabled(false);
	    c.gridx = 3;
	    panelForBtn.add(endBtn,c);
	    panelForInfoAndButton.add(panelForBtn,BorderLayout.PAGE_END);
	    panel.add(panelForInfoAndButton,BorderLayout.PAGE_END);
	    frame.setContentPane(panel);
	    frame.setSize(700, 400);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    List<ProcessData> processDataList = new ArrayList<>();
	    
	    SwingWorker<Void,List<ProcessData>> worker=new SwingWorker<Void,List<ProcessData>>() {
	          @Override
	          protected Void doInBackground() throws Exception {
	        	  int numberOfLogicalProcessors = getNumOfLogicalProcessors();
	        	  while (true) {
	        		  if (!frame.isVisible()) {
	        			  break;
	  	    		  }
	        		  processDataList.clear();
	        		  long start = System.nanoTime() / 1000000;
		  	    	  PowerData powerData = getPowerData();
		  	    	  List<ProcessData> processData = createBasicProcessDataArray(numberOfLogicalProcessors);
		              publish(processData);
		              long durationInMiliSec = System.nanoTime() / 1000000 - start;
		  	    	  List<String> sumEnergyDataPerProcess = getSumEnergyDataPerProcessFromDatabase(powerData,processData,durationInMiliSec);
			  	      consumptionPanel.removeAll();
			  	      JPanel subConsumptionPanel = new JPanel(new GridBagLayout());
			  	      subConsumptionPanel = getConsumptionPrediction(powerData,processDataList,durationInMiliSec);
			  	      if (subConsumptionPanel != null) {
			  	    	  consumptionPanel.add(subConsumptionPanel, BorderLayout.CENTER);
			  	      }
			  	      scrollPane.repaint();
			  	      scrollPane.revalidate();
			  	      elements.setEnabled(true);
			  	      consumptionBtn.setEnabled(true);
		  	    	  if (isUserAlertRequired) {
	  	    			startAlertGui(frame, 
	  	    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"memory",memLimitValue,null),
	  	    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"cpu",cpuLimitValue,sumEnergyDataPerProcess));
	  	    		  }
	        		  if (isDefaultAlertRequired) {
	        			  startAlertGui(frame, 
	      	    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"memory",null),
	      	    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"cpu",sumEnergyDataPerProcess));
	        		  }
	        		  if (isPopularProcessAnalyzerRequired) {
	        			  processFoundCounter(processData);
	        			  startAlertGui(frame, 
	  	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"memory",null),
	  	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"cpu",sumEnergyDataPerProcess));
	        		  }
	        		  elements.addListSelectionListener(new ListSelectionListener() {	
	        				@Override
	        				public void valueChanged(ListSelectionEvent e) {
	        					if (!elements.isSelectionEmpty()) {
	        						endBtn.setEnabled(true);
	        						int selectedIndex = elements.getSelectedIndex();
		        					for (int i = 0; i < processDataList.size(); i++) {
		        						if (selectedIndex == processDataList.get(i).getIndex()) {
		        							titleLabel.setText("Window Title: "+processDataList.get(i).getWindowTitle());
		        							cpuLabel.setText("CPU Usage [%]: "+String.valueOf(processDataList.get(i).getCpuUsage()));
		        							memLabel.setText("Used Memory Size [MB]: "+String.valueOf(processDataList.get(i).getMemUsage()));
		        						}
		        					}
		        					infoPanel.removeAll();
		        					basicDescriptionPanel.removeAll();
		        					infoPanel.add(titleLabel,BorderLayout.PAGE_START);
		        					c.weightx = 1;
		        					c.gridx = 0;
		        					basicDescriptionPanel.add(cpuLabel,c);
		        					c.gridx = 1;
		        					basicDescriptionPanel.add(memLabel, c);
		        					infoPanel.add(basicDescriptionPanel, BorderLayout.PAGE_END);
		        					infoPanel.repaint();
		        					infoPanel.revalidate();
		        					panelForInfoAndButton.add(infoPanel,BorderLayout.PAGE_START);
		        					panelForInfoAndButton.revalidate();
	        					} else {
	        						endBtn.setEnabled(false);
	        						panelForInfoAndButton.remove(infoPanel);
	        						panelForInfoAndButton.revalidate();
	        					}
	        				}
	        			});
	        		  endBtn.addActionListener(new ActionListener() {
	        				@Override
	        				public void actionPerformed(ActionEvent e) {
	        					for (int i = 0; i < processDataList.size(); i++) {
	        						
	        						if (elements.getSelectedIndex() == processDataList.get(i).getIndex()) {
	        							Runtime rt = Runtime.getRuntime();
	        							try {
											rt.exec("taskkill /pid "+processDataList.get(i).getProcessId()+" /f");
										} catch (IOException e1) {
											e1.printStackTrace();
										}
	        							frame.dispose();
	        							startBasicGUI(memLimitValue,cpuLimitValue,isUserAlertRequired,isDefaultAlertRequired,isPopularProcessAnalyzerRequired);
	        							endBtn.setEnabled(false);
	        						}
	        					}
	        				}
	        			});
	        		  Thread.sleep(sleepTimeInSeconds / 3 * 10 * 1000);
	        	  }
				return null;
	          }
	          @Override
	          public void process(List<List<ProcessData>> processData) {
	        	  for (List<ProcessData> process : processData) {
	        		  if (model != null) {
	        			  model.removeAllElements();
	        		  }
	        	      for (ProcessData entry : process) {
	        	    	  if (entry.getStatus().equals("Running") &&
	        	    		  !entry.getUserName().equals("N/A") &&
	        	    		  entry.getCpuTimeInSec() > 0) {
	        	    		  	model.addElement(entry.getName());
	        	    		  	int index = processDataList.size();
	        	    		  	processDataList.add(new ProcessData(entry.getName(),
	        	    		  			index,
	        	    		  			entry.getMemUsage(), 
	        	    		  			entry.getCpuUsage(), 
	        	    		  			entry.getWindowTitle(), 
	        	    		  			entry.getProcessId()));
	        	    	  }
	        	      }
	        	  }
	          }
	      };
	    worker.execute();  
  }
  
  public static void startWelcomeGUIDnD() {
	  JFrame frame = new JFrame("Energy Cost Observer - Welcome");
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  ListTransferHandler listHandler = new ListTransferHandler();
	  JList<String> list1, list2;
	  DefaultListModel<String> list1Model = new DefaultListModel<>();
	  list1Model.addElement(basicViewName);
      list1Model.addElement(advancedViewName);
      list1Model.addElement(alertsByUserName);
      list1Model.addElement(alertsByDefaultName);
      list1Model.addElement(analyzeName);
	  list1 = new JList<>(list1Model);
	  list1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  list1.setTransferHandler(listHandler);
	  list1.setDragEnabled(true);
	  list1.setDropMode(DropMode.INSERT);
	  JScrollPane list1View = new JScrollPane(list1);
	  list1View.setPreferredSize(new Dimension(500, 100));
	  JPanel panel1 = new JPanel();
	  panel1.setLayout(new BorderLayout());
	  panel1.add(new JLabel("Available features"),BorderLayout.PAGE_START);
	  panel1.add(list1View, BorderLayout.CENTER);
	  panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	  
	  DefaultListModel<String> list2Model = new DefaultListModel<>();
	  list2 = new JList<>(list2Model);
	  list2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	  list2.setTransferHandler(listHandler);
	  list2.setDragEnabled(true);
	  list2.setDropMode(DropMode.INSERT);
	  JScrollPane list2View = new JScrollPane(list2);
	  list2View.setPreferredSize(new Dimension(200, 100));
	  JPanel panel2 = new JPanel();
	  panel2.setLayout(new BorderLayout());
	  panel2.add(new JLabel("Feature(s) to execute"),BorderLayout.PAGE_START);
	  panel2.add(list2View, BorderLayout.CENTER);
	  panel2.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	  
	  JPanel middlePanel = new JPanel();
	  middlePanel.setLayout(new BorderLayout());
	  middlePanel.add(panel1, BorderLayout.PAGE_START);
	  middlePanel.add(panel2, BorderLayout.PAGE_END);
	  middlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
	  JPanel panel = new JPanel(new BorderLayout());
	  panel.add(middlePanel, BorderLayout.CENTER);
	  JPanel topPanel = new JPanel(new GridBagLayout());
	  GridBagConstraints cTop = new GridBagConstraints();
	  cTop.fill = GridBagConstraints.NORTH;
	  cTop.gridx = 0;
	  cTop.gridy = 0;
	  topPanel.add(new JLabel("Welcome to Energy Cost Observer!"),cTop);
	  cTop.gridy = 1;
	  topPanel.add(new JLabel("Drag and drop one or more available features which you want to use into the other field!"),cTop);
	  panel.add(topPanel, BorderLayout.PAGE_START);
	  JPanel bottomPanel = new JPanel(new GridBagLayout());
	  GridBagConstraints cBottom = new GridBagConstraints();
	  cBottom.fill = GridBagConstraints.NORTH;
	  cBottom.gridx = 0;
	  cBottom.gridy = 0;
	  JPanel infoPanel = new JPanel(new GridBagLayout());
	  JButton infoBtn = new JButton("Click an available item");
	  infoBtn.setEnabled(false);
	  GridBagConstraints cInfo = new GridBagConstraints();
	  cInfo.fill = GridBagConstraints.NORTH;
	  cInfo.gridx = 0;
	  cInfo.gridy = 0;
	  infoPanel.add(infoBtn,cInfo);
	  bottomPanel.add(infoPanel,cBottom);
	  JButton btn = new JButton("Execute");
	  cBottom.gridy = 1;
	  bottomPanel.add(new JLabel(" "),cBottom);
	  cBottom.gridy = 2;
	  bottomPanel.add(btn,cBottom);
	  panel.add(bottomPanel,BorderLayout.PAGE_END);
	  frame.setContentPane(panel);
	  frame.pack();
	  frame.setLocationRelativeTo(null);
	  frame.setVisible(true);
	  infoBtn.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				addDescription(list1.getSelectedValue());
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		private void addDescription(String selectedValue) throws SQLException {
			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.NORTH;
			c.gridx = 0;
			c.gridy = 0;
			JLabel mainHeader = new JLabel("This feature allows you to:");
			mainHeader.setForeground(Color.BLUE);
			panel.add(mainHeader,c);
			String windowTitle = "";
			if (selectedValue.equals(basicViewName)) {
				windowTitle = selectedValue;
				JLabel [] headers = {new JLabel("get information package about processes which"),
						new JLabel("get information package consisting of:")};
				for (int i = 0; i < headers.length; i++) {
					headers[i].setForeground(Color.BLUE);
				}
				c.gridy = 1;
				panel.add(headers[0],c);
				c.gridy = 2;
				panel.add(new JLabel("are running, and"),c);
				c.gridy = 3;
				panel.add(new JLabel("are started by a user, and"),c);
				c.gridy = 4;
				panel.add(new JLabel("are using the CPU"),c);
				c.gridy = 5;
				panel.add(headers[1],c);
				c.gridy = 6;
				panel.add(new JLabel("CPU usage [%], and"),c);
				c.gridy = 7;
				panel.add(new JLabel("used Memory size [MB], and"),c);
				if (hasIntelProcessor) {
					c.gridy = 8;
					panel.add(new JLabel("name of the window of the process, and"),c);
					c.gridy = 9;
					panel.add(new JLabel("energy cost prediction for current consumption"),c);
				} else {
					c.gridy = 8;
					panel.add(new JLabel("name of the window of the process"),c);
				}
			} else if (selectedValue.equals(advancedViewName)) {
				windowTitle = selectedValue;
				JLabel [] headers = {new JLabel("get CPU Usage information about all processes consisting of:"),
						new JLabel("get Memory Usage information about all processes consisting of:")};
				for (int i = 0; i < headers.length; i++) {
					headers[i].setForeground(Color.BLUE);
				}
				c.gridy ++;
				panel.add(headers[0],c);
				c.gridy ++;
				panel.add(new JLabel("overall CPU Usage [%], and"),c);
				c.gridy ++;
				panel.add(new JLabel("number of all processes, and"),c);
				c.gridy ++;
				panel.add(new JLabel("CPU Usage [%] of each processes, and"),c);
				if (hasIntelProcessor) {
					c.gridy ++;
					panel.add(new JLabel("line chart about the overall CPU utilization [%], and"),c);
					c.gridy ++;
					panel.add(new JLabel("energy cost prediction for current comsumption"),c);
				} else {
					c.gridy ++;
					panel.add(new JLabel("line chart about the overall CPU utilization [%]"),c);
				}
				c.gridy ++;
				panel.add(headers[1],c);
				c.gridy ++;
				panel.add(new JLabel("overall Memory Usage [%], and"),c);
				c.gridy ++;
				panel.add(new JLabel("total Memory size [GB], and"),c);
				c.gridy ++;
				panel.add(new JLabel("used Memory size [GB], and"),c);
				c.gridy ++;
				panel.add(new JLabel("Memory size [MB] of each processes, and"),c);
				c.gridy ++;
				panel.add(new JLabel("line chart about the overall Memory utilization [%]"),c);
			} else if (selectedValue.equals(alertsByUserName)) {
				windowTitle = selectedValue;
				c.gridy ++;
				panel.add(new JLabel("specify limit value for CPU usage[%] and used MEMORY size [MB], and"),c);
				if (hasIntelProcessor) {
					c.gridy ++;
					panel.add(new JLabel("get alerts when a process steps over the specified value, and"),c);
					c.gridy ++;
					panel.add(new JLabel("get CPU related enegy consumption cost with the alert"),c);
				} else {
					c.gridy ++;
					panel.add(new JLabel("get alerts when a process steps over the specified value"),c);
				}
			} else if (selectedValue.equals(alertsByDefaultName)) {
				windowTitle = selectedValue;
				if (hasIntelProcessor) {
					c.gridy ++;
					panel.add(new JLabel("get alerts when a process steps over the related default value, and"),c);
					c.gridy ++;
					panel.add(new JLabel("get CPU related enegy consumption cost with the alert"),c);
				} else {
					c.gridy ++;
					panel.add(new JLabel("get alerts when a process steps over the related default value"),c);
				}
				JLabel header = new JLabel("The processes and their default values:");
				header.setForeground(Color.BLUE);
				c.gridy ++;
				panel.add(header,c);
				preparedStatement = connect.prepareStatement("select name,cpu_limit,memory_limit from `default_limits`");
				resultSet = preparedStatement.executeQuery();
				while(resultSet.next()) {
					c.gridy ++;
					panel.add(new JLabel(resultSet.getString("name")+" - CPU usage limit [%]: "+resultSet.getString("cpu_limit")+", used MEMORY size limit [MB]: "+resultSet.getString("memory_limit")),c);
				}
			} else if (selectedValue.equals(analyzeName)) {
				windowTitle = selectedValue;
				if (hasIntelProcessor) {
					c.gridy ++;
					panel.add(new JLabel("get alerts when one of the 20 most popular processes steps over its average value, and"),c);
					c.gridy ++;
					panel.add(new JLabel("get CPU related enegy consumption cost with the alert"),c);
				} else {
					c.gridy ++;
					panel.add(new JLabel("get alerts when one of the 20 most popular processes steps over its average value"),c);
				}
			}
			JScrollPane scrollPane = new JScrollPane(panel); 
			scrollPane.setPreferredSize( new Dimension( 600, 300 ) );
			JOptionPane.showMessageDialog(frame, scrollPane, windowTitle, JOptionPane.PLAIN_MESSAGE);
		}
	  });
	  list1.addListSelectionListener(new ListSelectionListener() {
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (!list1.isSelectionEmpty()) {
				infoBtn.setEnabled(true);
				infoBtn.setText("Details about "+list1.getSelectedValue());
			} else {
				infoBtn.setEnabled(false);
				infoBtn.setText("Click an available item");
			}
		}
	  });
	  btn.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (list2Model.size() == 0) {
			    JOptionPane.showMessageDialog(null, "None of available features have been selected!", "Error", JOptionPane.ERROR_MESSAGE);
			} else if (list2Model.contains(basicViewName) && list2Model.contains(advancedViewName)) {
				JOptionPane.showMessageDialog(null, "It is not suggested to use both Basic View and Advanced View! Please, choose one of them only!", "Warning", JOptionPane.WARNING_MESSAGE);
			} else {
				if (list2Model.size() == 1 && list2Model.contains(analyzeName)) {
					startAnalyzePopularProcessDataGUI();
				} else if (list2Model.size() == 1 && list2Model.contains(alertsByDefaultName)) {
					startAlertByDefaultInputGUI();
				} else if (list2Model.contains(alertsByUserName)) {
					getUserInputRegardingAlertsAndShowGUI(list2Model);
				} else {
					if (list2Model.contains(basicViewName)) {
						if (list2Model.contains(alertsByDefaultName)) {
							if (list2Model.contains(analyzeName)) {
								startBasicGUI(0, 0, false, true, true);	
							} else {
								startBasicGUI(0, 0, false, true, false);	
							}
						} else {
							if (list2Model.contains(analyzeName)) {
								startBasicGUI(0, 0, false, false, true);
							} else {
								startBasicGUI(0, 0, false, false, false);
							}
						}
					}
					if (list2Model.contains(advancedViewName)) {
						if (list2Model.contains(alertsByDefaultName)) {
							if (list2Model.contains(analyzeName)) {
								startAdvancedGUI(0, 0, false, true, true);
							} else {
								startAdvancedGUI(0, 0, false, true, false);
							}
						} else {
							if (list2Model.contains(analyzeName)) {
								startAdvancedGUI(0, 0, false, false, true);
							} else {
								startAdvancedGUI(0, 0, false, false, false);
							}
						}
					}
				}
				frame.dispose();
			}
		}
	  });
}

  public static void startAnalyzePopularProcessDataGUI() {
	JFrame frame = new JFrame("Popular Process Data");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel panel = new JPanel(new GridBagLayout());
	  GridBagConstraints c = new GridBagConstraints();
	  c.fill = GridBagConstraints.NORTH;
	  frame.add(panel);
	  frame.setPreferredSize(new Dimension(300, 100));
	  frame.pack();  
	  frame.setLocationRelativeTo(null);
	  frame.setVisible(true);
    
    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
      @Override
      protected Void doInBackground() throws Exception {
    	  int numOfLogicalProcessors = getNumOfLogicalProcessors();
    	  while (true) {
    		  if (!frame.isVisible()) {
    			break;
    		}
    		ImageIcon animatedGif = new ImageIcon(Main.class.getResource("/resources/Gear.gif")); // Path/URL to your gif
		    JLabel processingLabel = new JLabel("Processing...");
	    	JLabel splashLabel = new JLabel(animatedGif);
		    panel.removeAll();
		    c.gridx = 0;
		    c.gridy = 0;
		    panel.add(processingLabel,c);
		    c.gridy = 1;
		    panel.add(splashLabel,c);
		    panel.repaint();
		    panel.revalidate();
		    long start = System.nanoTime() / 1000000;
    		PowerData powerData = getPowerData();
    		List<ProcessData> processData = createAdvancedProcessDataArray(numOfLogicalProcessors);
    		long durationInMiliSec = System.nanoTime() / 1000000 - start;
    		List<String> sumEnergyDataPerProcess = getSumEnergyDataPerProcessFromDatabase(powerData,processData,durationInMiliSec);
    		processFoundCounter(processData);
    		panel.removeAll();
    		c.gridy = 0;
    		panel.add(new JLabel("Processing is done!"),c);
    		panel.repaint();
    		panel.revalidate();
    		startAlertGui(frame, 
    				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"memory",null),
    				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"cpu",sumEnergyDataPerProcess));
    		Thread.sleep(sleepTimeInSeconds * 1000);
        }
		return null;
      }
    };
    worker.execute();
}

  public static void getUserInputRegardingAlertsAndShowGUI(DefaultListModel<String> list2Model) {
	JFrame frame = new JFrame("It's time to give the limit values!");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	JPanel panel = new JPanel(new GridBagLayout());
	GridBagConstraints constraints = new GridBagConstraints();
	constraints.fill = GridBagConstraints.NORTH;
	constraints.gridx = 0;
	JButton okBtn = new JButton("Done");
	constraints.gridy = 4;
	panel.add(okBtn,constraints);
	JTextField cpuLimit = getLimitOfSystemOutput(panel, okBtn, "CPU Usage [%]", 0);
	JTextField memLimit = getLimitOfSystemOutput(panel, okBtn, "Memory Value [MB]", 1);
	constraints.gridy = 3;
	panel.add(new JLabel(" "),constraints);
	frame.setContentPane(panel);
	frame.setPreferredSize(new Dimension(600, 200));
	frame.pack();
	frame.setLocationRelativeTo(null);
	frame.setVisible(true);
	okBtn.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			String cpuLimitText = cpuLimit.getText();
			String memLimitText = memLimit.getText();
			double cpuLimitValue;
			double memLimitValue;
			if (cpuLimitText.equals("")) {
				cpuLimitValue = 100;
			} else {
				cpuLimitValue = Double.parseDouble(cpuLimitText);
			}
			if (memLimitText.equals("")) {
				memLimitValue = 10000;
			} else {
				memLimitValue = Double.parseDouble(memLimitText);
			}
			if (list2Model.size() == 1 && list2Model.contains(alertsByUserName)) {
				startAlertByUserInputGUI(cpuLimitValue, memLimitValue, false, false);
			}
			if (list2Model.size() == 2 && list2Model.contains(alertsByUserName) && list2Model.contains(alertsByDefaultName)) {
				startAlertByUserInputGUI(cpuLimitValue, memLimitValue, true, false);
			}
			if (list2Model.size() == 2 && list2Model.contains(alertsByUserName) && list2Model.contains(analyzeName)) {
				startAlertByUserInputGUI(cpuLimitValue, memLimitValue, false, true);
			}
			if (list2Model.size() == 3 && list2Model.contains(alertsByUserName) &&
					list2Model.contains(alertsByDefaultName) && list2Model.contains(analyzeName)) {
				startAlertByUserInputGUI(cpuLimitValue, memLimitValue, true, true);
			}
			if (list2Model.contains(basicViewName)) {
				if (list2Model.contains(alertsByDefaultName)) {
					if (list2Model.contains(analyzeName)) {
						startBasicGUI(cpuLimitValue, memLimitValue, true, true, true);
					} else {
						startBasicGUI(cpuLimitValue, memLimitValue, true, true, false);
					}
				} else {
					if (list2Model.contains(analyzeName)) {
						startBasicGUI(cpuLimitValue, memLimitValue, true, false, true);
					} else {
						startBasicGUI(cpuLimitValue, memLimitValue, true, false, false);
					}
				}
			}
			if (list2Model.contains(advancedViewName)) {
				if (list2Model.contains(alertsByDefaultName)) {
					if (list2Model.contains(analyzeName)) {
						startAdvancedGUI(cpuLimitValue, memLimitValue, true, true, true);
					} else {
						startAdvancedGUI(cpuLimitValue, memLimitValue, true, true, false);
					}
				} else {
					if (list2Model.contains(analyzeName)) {
						startAdvancedGUI(cpuLimitValue, memLimitValue, true, false, true);
					} else {
						startAdvancedGUI(cpuLimitValue, memLimitValue, true, false, false);
					}
				}
			}
			frame.dispose();
		}
	});
}

  public static void startAlertByDefaultInputGUI() {
	  JFrame frame = new JFrame("Default Input");
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  JPanel panel = new JPanel(new GridBagLayout());
	  GridBagConstraints c = new GridBagConstraints();
	  c.fill = GridBagConstraints.NORTH;
	  frame.add(panel);
	  frame.setPreferredSize(new Dimension(300, 100));
	  frame.pack();  
	  frame.setLocationRelativeTo(null);
	  frame.setVisible(true);
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	      @Override
	      protected Void doInBackground() throws Exception {
	    	  int numOfLogicalProcessors = getNumOfLogicalProcessors();
	    	  while (true) {
	    		  if (!frame.isVisible()) {
	    			break;
	    		}
	    		ImageIcon animatedGif = new ImageIcon(Main.class.getResource("/resources/Gear.gif")); // Path/URL to your gif
		    	JLabel processingLabel = new JLabel("Processing...");
	    		JLabel splashLabel = new JLabel(animatedGif);
		    	panel.removeAll();
		    	c.gridx = 0;
		    	c.gridy = 0;
		    	panel.add(processingLabel,c);
		    	c.gridy = 1;
		    	panel.add(splashLabel,c);
		    	panel.repaint();
		    	panel.revalidate();
		    	long start = System.nanoTime() / 1000000;
	    		PowerData powerData = getPowerData();
	    		List<ProcessData> processData = createAdvancedProcessDataArray(numOfLogicalProcessors);
	    		long durationInMiliSec = System.nanoTime() / 1000000 - start;
	    		List<String> sumEnergyDataPerProcess = getSumEnergyDataPerProcessFromDatabase(powerData,processData,durationInMiliSec);
	    		//addMockData(frame, processData, counterForMock);
	    		panel.removeAll();
	    		c.gridy = 0;
	    		panel.add(new JLabel("Processing is done!"),c);
	    		panel.repaint();
	    		panel.revalidate();
	    		startAlertGui(frame, 
	    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"memory",null),
	    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"cpu",sumEnergyDataPerProcess));
	    		Thread.sleep(sleepTimeInSeconds * 1000);
	        }
			return null;
	      }
	    };
	    worker.execute();
}

  public static void startAlertByUserInputGUI(double cpuLimitValue, double memLimitValue, Boolean isDefaultAlertRequired, Boolean isPopularProcessAnalyzerRequired) {
	  JFrame frame = new JFrame("User Input");
	  frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  JPanel panel = new JPanel(new GridBagLayout());
	  GridBagConstraints c = new GridBagConstraints();
	  c.fill = GridBagConstraints.NORTH;
	  frame.add(panel);
	  frame.setPreferredSize(new Dimension(300, 100));
	  frame.pack();  
	  frame.setLocationRelativeTo(null);
	  frame.setVisible(true);
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	      @Override
	      protected Void doInBackground() throws Exception {
	    	  int numOfLogicalProcessors = getNumOfLogicalProcessors();
	    	  while (true) {
	    		  if (!frame.isVisible()) {
	    			break;
	    		}
	    		ImageIcon animatedGif = new ImageIcon(Main.class.getResource("/resources/Gear.gif")); // Path/URL to your gif
			    JLabel processingLabel = new JLabel("Processing...");
		    	JLabel splashLabel = new JLabel(animatedGif);
			    panel.removeAll();
			    c.gridx = 0;
			    c.gridy = 0;
			    panel.add(processingLabel,c);
			    c.gridy = 1;
			    panel.add(splashLabel,c);
			    panel.repaint();
			    panel.revalidate();
			    long start = System.nanoTime() / 1000000;
	    		PowerData powerData = getPowerData();
	    		List<ProcessData> processData = createAdvancedProcessDataArray(numOfLogicalProcessors);
	    		long durationInMiliSec = System.nanoTime() / 1000000 - start;
	    		List<String> sumEnergyDataPerProcess = getSumEnergyDataPerProcessFromDatabase(powerData,processData,durationInMiliSec);
	    		//addMockData(frame,processData,counterForMock);
	    		panel.removeAll();
	    		c.gridy = 0;
	    		panel.add(new JLabel("Processing is done!"),c);
	    		panel.repaint();
	    		panel.revalidate();
	    		startAlertGui(frame, 
    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"memory",memLimitValue,null),
    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"cpu",cpuLimitValue,sumEnergyDataPerProcess));
	    		if (isDefaultAlertRequired) {
	    			startAlertGui(frame, 
		    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"memory",null),
		    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"cpu",sumEnergyDataPerProcess));
	    		}
	    		if (isPopularProcessAnalyzerRequired) {
	    			processFoundCounter(processData);
	        		startAlertGui(frame, 
	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"memory",null),
	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"cpu",sumEnergyDataPerProcess));
	    		}
	    		Thread.sleep(sleepTimeInSeconds * 1000);
	        }
			return null;
	      }
	    };
	    worker.execute();
}
  
  public static JTextField getLimitOfSystemOutput(JPanel panel, JButton btn, String limitedOutput, int order) {
	  GridBagConstraints constraints = new GridBagConstraints();
	  JPanel mainRowPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
	  JPanel inputCheckPanel = new JPanel();
	  mainRowPanel.add(new JLabel("Please define a limit value for "+limitedOutput+": "));
	  JTextField textField = new JTextField();
	  textField.setPreferredSize(new Dimension(80, 20));
	  textField.getDocument().addDocumentListener(new DocumentListener() {
		  @Override
		  public void removeUpdate(DocumentEvent e) {
			  inputCheckPanel.removeAll();
			  inputCheckPanel.revalidate();
			  if (!textField.getText().equals("")) {
				  if (isNumber(textField.getText())) {
					  if (Double.parseDouble(textField.getText()) >= 0) {
						  btn.setEnabled(true);
						  inputCheckPanel.removeAll();
						  inputCheckPanel.add(new JLabel("Good format :)"));
						  inputCheckPanel.revalidate();
					  } else {
						  btn.setEnabled(false);
						  inputCheckPanel.removeAll();
						  inputCheckPanel.add(new JLabel("Still negative value :("));
						  inputCheckPanel.revalidate();
					  }
				  } else {
					  btn.setEnabled(false);
					  inputCheckPanel.removeAll();
					  inputCheckPanel.add(new JLabel("Still not a number :("));
					  inputCheckPanel.revalidate();
				  }
			  } else {
				  btn.setEnabled(true);
			  }
		  }
		  @Override
		  	public void insertUpdate(DocumentEvent e) {
			  if (isNumber(textField.getText())) {
				  if (Double.parseDouble(textField.getText()) >= 0) {
					  btn.setEnabled(true);
					  inputCheckPanel.removeAll();
					  inputCheckPanel.add(new JLabel("Good! It is a number :)"));
					  inputCheckPanel.revalidate();
				  } else {
					  btn.setEnabled(false);
					  inputCheckPanel.removeAll();
					  inputCheckPanel.add(new JLabel("Wrong! It is not a positive number :("));
					  inputCheckPanel.revalidate();
				  }
			  } else {
				  btn.setEnabled(false);
				  inputCheckPanel.removeAll();
				  inputCheckPanel.add(new JLabel("Wrong! It is not a number :("));
				  inputCheckPanel.revalidate();
			  }
		  }
		  @Override
		  public void changedUpdate(DocumentEvent e) {}
	  });
	  mainRowPanel.add(textField);
	  mainRowPanel.add(inputCheckPanel);
	  constraints.gridy = order;
	  panel.add(mainRowPanel, constraints);
	  return textField;
  }
  
  public static Boolean isNumber(String s) {
	    try { 
	        Double.parseDouble(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	        return false;
	    }
	    return true;
	}
  
  public static void startAdvancedGUI(double cpuLimitValue, double memLimitValue, Boolean isUserAlertRequired, Boolean isDefaultAlertRequired, Boolean isPopularProcessAnalyzerRequired) {
	  JFrame frame = new JFrame("Energy Cost Observer - Advanced");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    JPanel panel = new JPanel(new BorderLayout());
	    JPanel cpu = new JPanel();
	    JPanel mems = new JPanel();
	    JPanel cpuCharts = new JPanel();
	    JPanel memCharts = new JPanel();
	    cpu.setLayout(new GridBagLayout());
	    mems.setLayout(new GridBagLayout());
	    cpuCharts.setLayout(new GridLayout(0,2));
	    memCharts.setLayout(new GridLayout(0,2));
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.addTab("CPU",cpu);
	    tabbedPane.addTab("CPU CHARTS", cpuCharts);
		tabbedPane.addTab("MEMORY", mems);
		tabbedPane.addTab("MEMORY CHARTS", memCharts);
	    JScrollPane pane = new JScrollPane(tabbedPane);
	    panel.add(pane,BorderLayout.CENTER);
	    JPanel panelForBtn = new JPanel(new GridBagLayout());
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.NORTH;
	    c.weightx = 1;
	    c.gridx = 0;
	    c.gridy = 0;
	    JButton backBtn = new JButton("Back To Welcome Screen");
	    backBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startWelcomeGUIDnD();
				frame.dispose();
			}
		});
	    panelForBtn.add(backBtn,c);
	    JPanel consumptionPanel = new JPanel(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane(consumptionPanel); 
		scrollPane.setPreferredSize( new Dimension( 1200, 600 ) );
		consumptionPanel.addHierarchyListener(new HierarchyListener() {
			@Override
			public void hierarchyChanged(HierarchyEvent arg0) {
				Window window = SwingUtilities.getWindowAncestor(consumptionPanel);
				if (window instanceof Dialog) {
					Dialog dialog = (Dialog) window;
					if (!dialog.isResizable()) {
						dialog.setResizable(true);
					}
				}
			}
		});
	    JButton consumptionBtn = new JButton("Get Predicted Costs For Current Consumption");
	    consumptionBtn.setEnabled(false);
	    consumptionBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, scrollPane, "Energy Cost Observer - Advanced - Consumption Prediction" , JOptionPane.PLAIN_MESSAGE);  
			}
		});
	    c.gridx = 1;
	    if (hasIntelProcessor && isAppropriateAppInstalled) {
		    panelForBtn.add(consumptionBtn,c);
	    }
	    panel.add(panelForBtn,BorderLayout.PAGE_END);
	    frame.setContentPane(panel);
	    frame.setSize(900, 800);
	    frame.setLocationRelativeTo(null);
	    frame.setVisible(true);
	    
	    SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
	      @Override
	      protected Void doInBackground() throws Exception {
	    	  int numOfLogicalProcessors = getNumOfLogicalProcessors();
	    	  List<Double> cpuGraphScoresMXBen = new ArrayList<Double>();
	    	  List<Double> cpuGraphScoresCmd = new ArrayList<Double>();
	    	  List<Double> memGraphScoresMXBen = new ArrayList<Double>();
	    	  List<Double> memGraphScoresCmd = new ArrayList<Double>();
	    	  while (true) {
	    		if (!frame.isVisible()) {
	    			break;
	    		}
	    		long start = System.nanoTime() / 1000000;
	    		PowerData powerData = getPowerData();
	    		List<ProcessData> processData = createAdvancedProcessDataArray(numOfLogicalProcessors);
	    		long durationInMiliSec = System.nanoTime() / 1000000 - start;
	    		OperatingSystemMXBeanHandler operatingSystemMXBeanHandler = getOperatingSystemMXBeanComponents();
	    		List<String> sumEnergyDataPerProcess = getSumEnergyDataPerProcessFromDatabase(powerData,processData,durationInMiliSec);
	    		cpu.removeAll();
	    		mems.removeAll();
	    		cpuCharts.removeAll();
	    		memCharts.removeAll();
	    		createCpuHeader(cpu,processData,operatingSystemMXBeanHandler,powerData,sumEnergyDataPerProcess);
	    		createTabGui(cpu,processData,"cpu");
	    		createMemoryHeader(mems,processData,operatingSystemMXBeanHandler);
	    		createTabGui(mems,processData,"memory");
	    		createCPUCharts(cpuCharts,cpuGraphScoresMXBen,cpuGraphScoresCmd,processData,operatingSystemMXBeanHandler);
	    		createMemoryCharts(memCharts,memGraphScoresMXBen,memGraphScoresCmd,processData,operatingSystemMXBeanHandler);
	    		//addMockData(processData,memLimitValue,cpuLimitValue);
	    		consumptionPanel.removeAll();
	    		JPanel subConsumptionPanel = new JPanel(new GridBagLayout());
	    		subConsumptionPanel = getConsumptionPrediction(powerData,processData,durationInMiliSec);
	    		if (subConsumptionPanel != null) {
	    			consumptionPanel.add(subConsumptionPanel, BorderLayout.CENTER);
	    		}
	    		scrollPane.repaint();
	    		scrollPane.revalidate();
	    		consumptionBtn.setEnabled(true);
	    		if (isUserAlertRequired) {
	    			startAlertGui(frame, 
	    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"memory",memLimitValue,null),
	    					toDoIfLimitOverloadedInCaseOfUserInput(processData,"cpu",cpuLimitValue,sumEnergyDataPerProcess));
	    		}
	    		if (isDefaultAlertRequired) {
	    			startAlertGui(frame, 
		    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"memory",null),
		    				toDoIfLimitOverloadedInCaseOfDefaultInput(processData,"cpu",sumEnergyDataPerProcess));
	    		}
	    		if (isPopularProcessAnalyzerRequired) {
	    			processFoundCounter(processData);
	        		startAlertGui(frame, 
	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"memory",null),
	        				toDoIfPopularProcessCurrentValueIsOverThanAvarage(processData,"cpu",sumEnergyDataPerProcess));
	    		}
	    		Thread.sleep(sleepTimeInSeconds * 1000);
	        }
			return null;
	      }
	    };
	    worker.execute();
  }
  
  public static List<String> getSumEnergyDataPerProcessFromDatabase(PowerData powerData,List<ProcessData> processData, long durationInMiliSec) throws SQLException {
	  if (powerData == null) {
		  return null;
	  }
	  double powerValue = powerData.getPowerInWatt();
	  double energyInKiloWattHour = powerValue * durationInMiliSec / 1000 / 3600 / 1000;
	  double energyPrice = energyInKiloWattHour * pricePerKiloWattHour;
	  List<String> nameAndValueList = new ArrayList<>();
	  List<String> nameList = new ArrayList<>();
	  String name = "";
	  String [] parts;
	  Boolean alreadyContainedProcess = false;
	  for (ProcessData data : processData) {
		  if (!data.getName().equals("System Idle Process") && !Double.isNaN(data.getCpuUsage())) {
			  alreadyContainedProcess = false;
			  nameList.clear();
			  nameAndValueList.clear();
			  preparedStatement = connect.prepareStatement("select name,value from `energy_data` order by value desc");
			  resultSet = preparedStatement.executeQuery();
			  while (resultSet.next()) {
				  name = resultSet.getString("name");
				  if (!nameList.contains(name)) {
					  nameList.add(name);
					  nameAndValueList.add(name+","+resultSet.getDouble("value"));
				  }
			  }
			  for (String element : nameAndValueList) {
				  parts = element.split(",");
				  if (parts[0].equals(data.getName())) {
					  alreadyContainedProcess = true;
					  preparedStatement = connect.prepareStatement("insert into `energy_data` (`name`,`value`)"
							   +" values (?,?)");
					  preparedStatement.setString(1, parts[0]);
					  preparedStatement.setDouble(2, data.getCpuUsage() * energyPrice + Double.parseDouble(parts[1]));
					  preparedStatement.executeUpdate();
					  break;
				  }
			  }
			  if (!alreadyContainedProcess) {
				  preparedStatement = connect.prepareStatement("insert into `energy_data` (`name`,`value`)"
						   +" values (?,?)");
				  preparedStatement.setString(1, data.getName());
				  preparedStatement.setDouble(2, data.getCpuUsage() * energyPrice);
				  preparedStatement.executeUpdate();
			  }
		  }
	  }
	  nameList.clear();
	  nameAndValueList.clear();
	  preparedStatement = connect.prepareStatement("select name,value from `energy_data` order by value desc");
	  resultSet = preparedStatement.executeQuery();
	  while (resultSet.next()) {
		  name = resultSet.getString("name");
		  if (!nameList.contains(name)) {
			  nameList.add(name);
			  nameAndValueList.add(name+","+resultSet.getDouble("value"));
		  }
	  }
	  return nameAndValueList;
  }
  
  public static void addMockData (JFrame frame, List<ProcessData> processData, int counter) {
	  if (frame.getTitle().equals("User Input")) {
		  processData.add(new ProcessData(9999999,"mockMEM.exe", 901, 89));
		  processData.add(new ProcessData(9999997,"mockCPU.exe", 699, 96));
	  }
	  if (frame.getTitle().equals("Default Input")) {
		  processData.add(new ProcessData(9999999,"mockMEM.exe", 901, 89));
		  processData.add(new ProcessData(9999997,"mockCPU.exe", 699, 96));
	  }
	  if (frame.getTitle().equals("Popular Process Data")) {
		  processData.add(new ProcessData(9999999,"mockMEM.exe", 901, 89));
		  processData.add(new ProcessData(9999997,"mockCPU.exe", 699, 96));
	  }
  }
  
  public static void startAlertGui(JFrame frame, JPanel memHeaderPanel, JPanel cpuHeaderPanel) {
	  JPanel panel = new JPanel(new GridBagLayout());
	  GridBagConstraints c = new GridBagConstraints();
	  c.fill = GridBagConstraints.NORTH;
	  c.gridx = 0;
	  c.gridy = 0;
	  if (cpuHeaderPanel != null) {
		  cpuHeaderPanel.repaint();
		  cpuHeaderPanel.revalidate();
		  panel.add(cpuHeaderPanel,c);
		  c.gridy = 1;
	  }
	  if (memHeaderPanel != null) {
		  memHeaderPanel.repaint();
		  memHeaderPanel.revalidate();
		  panel.add(memHeaderPanel,c);  
	  }
	  if (cpuHeaderPanel != null || memHeaderPanel != null) {
		  JScrollPane scrollPane = new JScrollPane(panel); 
		  scrollPane.setPreferredSize( new Dimension( 1000, 500 ) );
		  JOptionPane.showMessageDialog(frame, scrollPane, frame.getTitle()+" Related Alerts", JOptionPane.PLAIN_MESSAGE);  
	  }
  }
  
  public static void processFoundCounter(List<ProcessData> processData) throws SQLException {
	  List<String> processListWithMaxValues = new ArrayList<>();
	  List<String> handledProcessNameList = new ArrayList<>();
	  for (ProcessData data : processData) {
		  if (!data.getName().equals("System Idle Process") && !handledProcessNameList.contains(data.getName())) {
			  double maxCpuValue = data.getCpuUsage();
			  double maxMemoryValue = data.getMemUsage();
			  for (ProcessData subData : processData) {
				  if (subData.getName().equals(data.getName())) {
					  if (subData.getCpuUsage() > maxCpuValue) {
						  maxCpuValue = subData.getCpuUsage();
					  }
					  if (subData.getMemUsage() > maxMemoryValue) {
						  maxMemoryValue = subData.getMemUsage();
					  }
				  }
			  }
			  handledProcessNameList.add(data.getName());
			  processListWithMaxValues.add(data.getName()+","+maxCpuValue+","+maxMemoryValue);
		  }
	  }
	  for (String data : processListWithMaxValues) {
		  String [] parts = data.split(",");
		  String currName = parts[0];
		  double currCpuValue = Double.parseDouble(parts[1]);
		  double currMemValue = Double.parseDouble(parts[2]);
		  preparedStatement = connect.prepareStatement("select name,counter from `process_counter` order by counter desc");
		  resultSet = preparedStatement.executeQuery();
		  List<String> alreadyStoredProcessNameList = new ArrayList<>();
		  List<Integer> alreadyStoredProcessCounterList = new ArrayList<>();
		  String name;
		  while(resultSet.next()) {
			  name = resultSet.getString("name");
			  if (!alreadyStoredProcessNameList.contains(name)) {
				  alreadyStoredProcessNameList.add(name);
				  alreadyStoredProcessCounterList.add(resultSet.getInt("counter"));  
			  }
		  }
		  int order;
		  if (alreadyStoredProcessNameList.contains(currName)) {
			  order = alreadyStoredProcessNameList.indexOf(currName);
			  preparedStatement = connect.prepareStatement("insert into `process_counter` (`name`,`cpu_value`,`memory_value`,`counter`)"
					   +" values (?,?,?,?)");
			  preparedStatement.setString(1, currName);
			  preparedStatement.setDouble(2, currCpuValue);
			  preparedStatement.setDouble(3, currMemValue);
			  preparedStatement.setInt(4, alreadyStoredProcessCounterList.get(order) + 1);
			  preparedStatement.executeUpdate();
		  } else {
			  preparedStatement = connect.prepareStatement("insert into `process_counter` (`name`,`cpu_value`,`memory_value`,`counter`)"
					   +" values (?,?,?,?)");
			  preparedStatement.setString(1, currName);
			  preparedStatement.setDouble(2, currCpuValue);
			  preparedStatement.setDouble(3, currMemValue);
			  preparedStatement.setInt(4, 1);
			  preparedStatement.executeUpdate();
		  }  
	  }
  }
  
  @SuppressWarnings({ "resource"})
  public static JPanel toDoIfPopularProcessCurrentValueIsOverThanAvarage(List<ProcessData> processData, String type, List<String> sumEnergyDataPerProcess) throws IOException, SQLException, ParseException {
	  List<String> processOverLimitList = new ArrayList<>();
	  Calendar calendar = Calendar.getInstance();
	  preparedStatement = connect.prepareStatement("select name,cpu_value,memory_value from `process_counter` order by counter desc");
	  resultSet = preparedStatement.executeQuery();
	  List<String> popularProcessNameList = new ArrayList<>();
	  List<String> popularProcessDataList = new ArrayList<>();
	  List<String> popularProcessWithAverageDataList = new ArrayList<>();
	  String name;
	  double value;
	  while (resultSet.next()) {
		  name = resultSet.getString("name");
		  if (type.equals("memory")) {
			  value = resultSet.getDouble("memory_value");
		  } else {
			  value = resultSet.getDouble("cpu_value");
		  }
		  popularProcessDataList.add(name+","+value);
		  if (!popularProcessNameList.contains(name)) {
			  popularProcessNameList.add(name);
		  }
		  if (popularProcessNameList.size() == 20) {
			  break;
		  }
	  }
	  String [] processParts;
	  int processCounter = 0;
	  double sumOfValue = 0;
	  for (String processName : popularProcessNameList) {
		  for (String processDataInfo : popularProcessDataList) {
			  processParts = processDataInfo.split(",");
			  if (processName.equals(processParts[0])) {
				  processCounter ++;
				  sumOfValue += Double.parseDouble(processParts[1]);
			  }
		  }
		  popularProcessWithAverageDataList.add(processName+","+round(sumOfValue / processCounter));
		  processCounter = 0;
		  sumOfValue = 0;
	  }
	  double avaragePercentForLimit = 200;
	  ProcessData currData;
	  String [] processDataParts;
	  for (String popularProcessData : popularProcessWithAverageDataList) {
		  processDataParts = popularProcessData.split(",");
		  for (int i = 0; i < processData.size(); i++) {
	    	  currData = processData.get(i);
	    	  if (currData.getName().equals(processDataParts[0])) {
	    		  if (type.equals("memory")) {
		    		  if (currData.getMemUsage() > Double.parseDouble(processDataParts[1]) * avaragePercentForLimit / 100) {
		        		  processOverLimitList.add(currData.getName()+","+currData.getMemUsage()+","+Double.parseDouble(processDataParts[1]));
		    		  }
		    	  } else if (type.equals("cpu")) {
		    		  if (currData.getCpuUsage() > Double.parseDouble(processDataParts[1]) * avaragePercentForLimit / 100) {
		        		  processOverLimitList.add(currData.getName()+","+currData.getCpuUsage()+","+Double.parseDouble(processDataParts[1]));
		    		  }
		    	  }
	    	  }
	      }
	  }
	  if (processOverLimitList.size() != 0) {
		  JPanel panel = new JPanel(new GridBagLayout());
		  GridBagConstraints c = new GridBagConstraints();
		  c.fill = GridBagConstraints.HORIZONTAL;
		  c.gridx = 0;
		  c.gridy = 0;
		  int delayDueToHistory = 0;
		  String unit = "";
		  if (type.equals("memory")) {
			  unit = "[MB]";
		  } else if (type.equals("cpu")) {
			  unit = "[%]";
		  }
		  JLabel header = null;
		  List<String> processNameList = new ArrayList<>();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
			  }
		  }
		  if (processNameList.size() == 1) {
			  header = new JLabel("Process having greater "+type.toUpperCase()+" Usage value than the "+avaragePercentForLimit+" [%] of the AVERAGE value is below! (1 process)");
		  } else {
			  header = new JLabel("Processes having greater "+type.toUpperCase()+" Usage value than the "+avaragePercentForLimit+" [%] of the AVERAGE value are below! ("+processNameList.size()+" processes)");
		  }
		  header.setForeground(Color.BLUE);
		  panel.add(header,c);
		  delayDueToHistory ++;
		  c.gridy = delayDueToHistory;
		  panel.add(new JLabel(" "),c);
		  processNameList.clear();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
				  delayDueToHistory ++;
				  c.gridy = delayDueToHistory;
				  JLabel processName = new JLabel(parts[0]+" (average: "+parts[2]+" "+unit+", current value: "+parts[1]+" "+unit+")");
				  processName.setForeground(Color.RED);
				  panel.add(processName,c);
				  JButton endBtn = new JButton();
				  endBtn.setText("click to end process");
				  endBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						endBtn.setBackground(Color.YELLOW);
						endBtn.setText("the process has stopped");
						Runtime rt = Runtime.getRuntime();
							try {
							rt.exec("taskkill /im "+parts[0]+" /f");
							} catch (IOException e1) {
							e1.printStackTrace();
						}
						endBtn.setEnabled(false);
					}
				  });
				  c.gridx = 2;
				  panel.add(endBtn,c);
				  c.gridx = 0;
				  preparedStatement = connect.prepareStatement("select time,value from `average_related_alerts`"
		              		+ "where `type`=? and `name`=?"
		              		+ "order by `time` desc");
				  preparedStatement.setString(1,type);
				  preparedStatement.setString(2, parts[0]);
				  delayDueToHistory = commonToDoIfAlertRequiredAndGetYScalePos(panel, preparedStatement, delayDueToHistory, parts, c, unit, sumEnergyDataPerProcess, 3 * 7 * 24 * 60 * 60, "3 weeks");
				  preparedStatement = connect.prepareStatement("insert into `average_related_alerts` (`type`,`time`,`name`,`value`)"
						   +" values (?,?,?,?)");
				  preparedStatement.setString(1, type);
				  preparedStatement.setTimestamp(2, new Timestamp(calendar.getTime().getTime()));
				  preparedStatement.setString(3, parts[0]);
				  preparedStatement.setString(4, parts[1]);
				  preparedStatement.executeUpdate();
			  }
		  }
		  return panel;
	  }
      return null;
  }
  
  @SuppressWarnings({ "resource"})
  public static JPanel toDoIfLimitOverloadedInCaseOfDefaultInput(List<ProcessData> processData, String type, List<String> sumEnergyDataPerProcess) throws IOException, SQLException, ParseException {
	  ProcessData currData;
	  List<String> processOverLimitList = new ArrayList<>();
	  Calendar calendar = Calendar.getInstance();
	  preparedStatement = connect.prepareStatement("select * from `default_limits`");
	  resultSet = preparedStatement.executeQuery();
	  while (resultSet.next()) {
		  for (int i = 0; i < processData.size(); i++) {
	    	  currData = processData.get(i);
	    	  if (currData.getName().equals(resultSet.getString("name"))) {
	    		  if (type.equals("memory")) {
		    		  if (currData.getMemUsage() > Double.parseDouble(resultSet.getString("memory_limit"))) {
		        		  processOverLimitList.add(currData.getName()+","+currData.getMemUsage());
		    		  }
		    	  } else if (type.equals("cpu")) {
		    		  if (currData.getCpuUsage() > Double.parseDouble(resultSet.getString("cpu_limit"))) {
		        		  processOverLimitList.add(currData.getName()+","+currData.getCpuUsage());
		    		  }
		    	  }
	    	  }
	      }
	  }
	  if (processOverLimitList.size() != 0) {
		  JPanel panel = new JPanel(new GridBagLayout());
		  GridBagConstraints c = new GridBagConstraints();
		  c.fill = GridBagConstraints.HORIZONTAL;
		  c.gridx = 0;
		  c.gridy = 0;
		  int delayDueToHistory = 0;
		  String unit = "";
		  if (type.equals("memory")) {
			  unit = "[MB]";
		  } else if (type.equals("cpu")) {
			  unit = "[%]";
		  }
		  JLabel header = null;
		  List<String> processNameList = new ArrayList<>();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
			  }
		  }
		  if (processNameList.size() == 1) {
			  header = new JLabel("Process having greater "+type.toUpperCase()+" Usage value than the DEFAULT limit is below! (1 process)");
		  } else {
			  header = new JLabel("Processes having greater "+type.toUpperCase()+" Usage value than the DEFAULT limit are below! ("+processNameList.size()+" processes)");
		  }
		  header.setForeground(Color.BLUE);
		  panel.add(header,c);
		  delayDueToHistory ++;
		  c.gridy = delayDueToHistory;
		  panel.add(new JLabel(" "),c);
		  processNameList.clear();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
				  delayDueToHistory ++;
				  c.gridy = delayDueToHistory;
				  JLabel processName = new JLabel(parts[0]);
				  processName.setForeground(Color.RED);
				  panel.add(processName,c);
				  JButton endBtn = new JButton();
				  endBtn.setText("click to end process");
				  endBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						endBtn.setBackground(Color.YELLOW);
						endBtn.setText("the process has stopped");
						Runtime rt = Runtime.getRuntime();
							try {
							rt.exec("taskkill /im "+parts[0]+" /f");
							} catch (IOException e1) {
							e1.printStackTrace();
						}
						endBtn.setEnabled(false);
					}
				  });
				  c.gridx = 2;
				  panel.add(endBtn,c);
				  c.gridx = 0;
				  preparedStatement = connect.prepareStatement("select time,value from `default_alerts`"
		              		+ "where `type`=? and `name`=?"
		              		+ "order by `time` desc");
				  preparedStatement.setString(1,type);
				  preparedStatement.setString(2, parts[0]);
				  delayDueToHistory = commonToDoIfAlertRequiredAndGetYScalePos(panel, preparedStatement, delayDueToHistory, parts, c, unit, sumEnergyDataPerProcess, 7 * 24 * 60 * 60, "1 week");
				  preparedStatement = connect.prepareStatement("insert into `default_alerts` (`type`,`time`,`name`,`value`)"
						   +" values (?,?,?,?)");
				  preparedStatement.setString(1, type);
				  preparedStatement.setTimestamp(2, new java.sql.Timestamp(calendar.getTime().getTime()));
				  preparedStatement.setString(3, parts[0]);
				  preparedStatement.setString(4, parts[1]);
				  preparedStatement.executeUpdate();
			  }
		  }
		  return panel;
	  }
      return null;
  }
  
  public static int commonToDoIfAlertRequiredAndGetYScalePos(JPanel panel, PreparedStatement preparedStatement, int delayDueToHistory, 
	  String [] parts, GridBagConstraints c, String unit, List<String> sumEnergyDataPerProcess, int timeLimit, String timeLimitText) throws SQLException {
	  //panel = new JPanel(new GridBagLayout());
	  Calendar calendar = Calendar.getInstance();
	  resultSet = preparedStatement.executeQuery();
	  if (resultSet != null) {
		  while (resultSet.next()) {
            	Timestamp timestamp = resultSet.getTimestamp("time");
            	Date date = new Date(timestamp.getTime());
            	Date currDate = calendar.getTime();
            	long diff = currDate.getTime() - date.getTime();
            	if (diff / 1000 <= timeLimit) {
            		delayDueToHistory ++;
            		c.gridy = delayDueToHistory;
					panel.add(new JLabel("Attention: ~ stepped over the limit multiple times in the last "+timeLimitText+", so check its usage!"),c);
					break;
            	}
		  }
		  if(sumEnergyDataPerProcess != null) {
			  delayDueToHistory ++;
			  c.gridy = delayDueToHistory;
	          String [] energyParts;
	          double energyPrice = 0;
	          for (String element : sumEnergyDataPerProcess) {
	        	  energyParts = element.split(",");
	        	  if (energyParts[0].equals(parts[0])) {
	        		  energyPrice = Double.parseDouble(energyParts[1]);
	        		  break;
	        	  }
	          }
	          JLabel energyCostLabel = new JLabel("Up to now, the generated energy cost related to this process is "+roundFiveDigits(energyPrice)+" [HUF].");
			  energyCostLabel.setForeground(Color.RED);
			  panel.add(energyCostLabel,c);
			  delayDueToHistory ++;
			  c.gridy = delayDueToHistory;
			  JLabel energyCostMessageLabel = new JLabel("As this process has caused an alert, it is suggested to be stopped to reduce energy costs!");
			  energyCostMessageLabel.setForeground(Color.RED);
			  panel.add(energyCostMessageLabel,c);
		  }
		  int counter = 0;
		  int maxHistoryLines = 10;
		  resultSet = preparedStatement.executeQuery();
		  while (resultSet.next()) {
			  	counter ++;
		  }
          if (counter != 0) {
        	  delayDueToHistory ++;
    		  c.gridy = delayDueToHistory;
	          String pluralHandlerVerb = " has";
	          String pluralHandlerAdverb = "it";
	          if (counter > 1) {
	        	  pluralHandlerVerb = "s have";
	        	  pluralHandlerAdverb = "them";
	          }
        	  if (counter <= maxHistoryLines) {
	        	  panel.add(new JLabel("So far, "+counter+" alert"+pluralHandlerVerb+" been registered to this process and limit! Please, take a look at below for details regarding "+pluralHandlerAdverb+"!"),c);
	          } else {
	        	  panel.add(new JLabel("So far, "+counter+" alert"+pluralHandlerVerb+" been registered to this process and limit! Please, take a look at below for details regarding the last "+maxHistoryLines+" of them!"),c);
	          }
          }
          resultSet = preparedStatement.executeQuery();
          counter = 0; 
		  while (resultSet.next()) {
			  	counter ++;
			  	if (counter > maxHistoryLines) {
			  		break;
			  	}
            	String time = "";
            	Date date = new Date(resultSet.getTimestamp("time").getTime());
            	Date currDate = calendar.getTime();
            	long seconds = (currDate.getTime() - date.getTime()) / 1000;
            	long minutes = seconds / 60;
            	long hours = minutes / 60;
            	long mDay = hours / 24;
            	long mHr = hours % 24;
            	long mMin = minutes % 60;
            	long mSec = seconds % 60;
            	Boolean isStartCalendarItemFound = false;
            	if (mDay != 0) {
            		isStartCalendarItemFound = true;
            		if (mDay == 1) {
            			time += "1 day";
            		} else {
            			time += mDay+" days";
            		}
            	}
            	if (mHr != 0) {
            		if (isStartCalendarItemFound) {
            			time += ", ";
            		} else {
            			isStartCalendarItemFound = true;
            		}
            		if (mHr == 1) {
            			time += "1 hour";
            		} else {
            			time += mHr+" hours";
            		}
            	}
            	if (mMin != 0) {
            		if (isStartCalendarItemFound) {
            			time += ", ";
            		} else {
            			isStartCalendarItemFound = true;
            		}
            		if (mMin == 1) {
            			time += "1 minute";
            		} else {
            			time += mMin+" minutes";
            		}
            	}
            	if (mSec != 0) {
            		if (isStartCalendarItemFound) {
            			time += " and ";
            		}
            		if (mSec == 1) {
            			time += "1 second";
            		} else {
            			time += mSec+" seconds";
            		}
            	}
            	delayDueToHistory ++;
      		  	c.gridy = delayDueToHistory;
				panel.add(new JLabel(time+" ago : "+resultSet.getString("value")+" "+unit),c);
        }
		  delayDueToHistory ++;
		  c.gridy = delayDueToHistory;
		  panel.add(new JLabel(" "),c);
	  }
	  return delayDueToHistory;
  }
  
  @SuppressWarnings({ "resource"})
  public static JPanel toDoIfLimitOverloadedInCaseOfUserInput(List<ProcessData> processData, String type, double limit, List<String> sumEnergyDataPerProcess) throws IOException, SQLException, ParseException {
	  ProcessData currData;
	  List<String> processOverLimitList = new ArrayList<>();
	  Calendar calendar = Calendar.getInstance();
	  for (int i = 0; i < processData.size(); i++) {
    	  currData = processData.get(i);
    	  if (type.equals("memory") && !currData.getName().equals("System Idle Process")) {
    		  if (currData.getMemUsage() > limit) {
        		  processOverLimitList.add(currData.getName()+","+currData.getMemUsage());
    		  }
    	  } else if (type.equals("cpu") && !currData.getName().equals("System Idle Process")) {
    		  if (currData.getCpuUsage() > limit) {
        		  processOverLimitList.add(currData.getName()+","+currData.getCpuUsage());
    		  }
    	  }
      }
	  if (processOverLimitList.size() != 0) {
		  JPanel panel = new JPanel(new GridBagLayout());
		  GridBagConstraints c = new GridBagConstraints();
		  c.fill = GridBagConstraints.HORIZONTAL;
		  c.gridx = 0;
		  c.gridy = 0;
		  int delayDueToHistory = 0;
		  String unit = "";
		  if (type.equals("memory")) {
			  unit = "[MB]";
		  } else if (type.equals("cpu")) {
			  unit = "[%]";
		  }
		  JLabel header = null;
		  List<String> processNameList = new ArrayList<>();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
			  }
		  }
		  if (processNameList.size() == 1) {
			  header = new JLabel("Process having greater "+type.toUpperCase()+" Usage value than the USER INPUT BASED limit ("+limit+" "+unit+") is below! (1 process)");
		  } else {
			  header = new JLabel("Processes having greater "+type.toUpperCase()+" Usage value than the USER INPUT BASED limit ("+limit+" "+unit+") are below! ("+processNameList.size()+" processes)");
		  }
		  header.setForeground(Color.BLUE);
		  panel.add(header,c);
		  delayDueToHistory ++;
		  c.gridy = delayDueToHistory;
		  panel.add(new JLabel(" "),c);
		  processNameList.clear();
		  for (int i = 0; i < processOverLimitList.size(); i++) {
			  String [] parts = processOverLimitList.get(i).split(",");
			  if (!processNameList.contains(parts[0])) {
				  processNameList.add(parts[0]);
				  delayDueToHistory ++;
				  c.gridy = delayDueToHistory;
				  JLabel processName = new JLabel(parts[0]);
				  processName.setForeground(Color.RED);
				  panel.add(processName,c);
				  JButton endBtn = new JButton();
				  endBtn.setText("click to end process");
				  endBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						endBtn.setBackground(Color.YELLOW);
						endBtn.setText("the process has stopped");
						Runtime rt = Runtime.getRuntime();
							try {
							rt.exec("taskkill /im "+parts[0]+" /f");
							} catch (IOException e1) {
							e1.printStackTrace();
						}
						endBtn.setEnabled(false);
					}
				  });
				  c.gridx = 2;
				  panel.add(endBtn,c);
				  c.gridx = 0;
				  preparedStatement = connect.prepareStatement("select time,value from `alerts`"
	              		+ "where `type`=? and `name`=? and `limit`=?"
	              		+ "order by `time` desc");
				  preparedStatement.setString(1,type);
				  preparedStatement.setString(2, parts[0]);
				  preparedStatement.setDouble(3, limit);
				  delayDueToHistory = commonToDoIfAlertRequiredAndGetYScalePos(panel, preparedStatement, delayDueToHistory, parts, c, unit, sumEnergyDataPerProcess, 30 * 60, "30 minutes");
				  preparedStatement = connect.prepareStatement("insert into `alerts` (`type`,`time`,`name`,`value`,`limit`)"
						  +" values (?,?,?,?,?)");
				  preparedStatement.setString(1, type);
				  preparedStatement.setTimestamp(2, new Timestamp(calendar.getTime().getTime()));
				  preparedStatement.setString(3, parts[0]);
				  preparedStatement.setString(4, parts[1]);
				  preparedStatement.setDouble(5, limit);
				  preparedStatement.executeUpdate();
			  }
		  }
		  return panel;
	  }
      return null;
  }
  
  public static void createCPUCharts(JPanel panel, List<Double> graphScoresMXBen, List<Double> graphScoresCmd, List<ProcessData> processData, OperatingSystemMXBeanHandler operatingSystemMXBeanHandler) {
	  	graphScoresMXBen.add(operatingSystemMXBeanHandler.getSystemCpuLoad());
	  	double valueOfCpuUsageByCommandLineTools = 0;
		double currValue = 0;
	    String currName = "";
	    int numOfProcesses = processData.size();
		for (int i = 0; i < numOfProcesses; i++) {
			currValue = processData.get(i).getCpuUsage();
	    	currName = processData.get(i).getName();
	    	if (!Double.isNaN(currValue) &&
	    		!currName.equals("System Idle Process")) {
	    		valueOfCpuUsageByCommandLineTools += currValue;
			}
		}
		graphScoresCmd.add(valueOfCpuUsageByCommandLineTools);
	  	panel.add(new GraphPanel(graphScoresMXBen,"cpu","OperatingSystemMXBean"));
	  	panel.add(new GraphPanel(graphScoresCmd,"cpu","Command Line Tools"));
	  	panel.revalidate();
		if (graphScoresMXBen.size() == 20) {
			graphScoresMXBen.remove(0);
		}
		if (graphScoresCmd.size() == 20) {
			graphScoresCmd.remove(0);
		}
  }
  
  public static void createMemoryCharts(JPanel panel, List<Double> graphScoresMXBen, List<Double> graphScoresCmd, List<ProcessData> processData, OperatingSystemMXBeanHandler operatingSystemMXBeanHandler) {
	  	graphScoresMXBen.add(operatingSystemMXBeanHandler.getUsedPhysicalMemorySize() / operatingSystemMXBeanHandler.getTotalPhysicalMemorySize() * 100);
	  	double sumOfMemoryDataByCommandLineTools = 0;
		for (int i = 0; i < processData.size(); i++) {
			sumOfMemoryDataByCommandLineTools += processData.get(i).getMemUsage();
		}
		sumOfMemoryDataByCommandLineTools /= 1024;
		graphScoresCmd.add(sumOfMemoryDataByCommandLineTools / operatingSystemMXBeanHandler.getTotalPhysicalMemorySize() * 100);
	  	panel.add(new GraphPanel(graphScoresMXBen,"mem","OperatingSystemMXBean"));
	  	panel.add(new GraphPanel(graphScoresCmd,"mem","Command Line Tools"));
	  	panel.revalidate();
		if (graphScoresMXBen.size() == 20) {
			graphScoresMXBen.remove(0);
		}
		if (graphScoresCmd.size() == 20) {
			graphScoresCmd.remove(0);
		}
}
  
  public static List<ProcessData> createBasicProcessDataArray(int numOfLogicalProcessors) throws IOException {
	  InputStream is = null;
	  InputStreamReader isr = null;
	  BufferedReader br = null;

      List<String> command = new ArrayList<String>();
      command.add("WMIC");
      command.add("path");
      command.add("Win32_PerfFormattedData_PerfProc_Process");
      command.add("get");
      command.add("ElapsedTime,");
      command.add("IDProcess");
      
      List<CpuCalculationHelper> cpuCalcHelperData = new ArrayList<CpuCalculationHelper>();
      List<ProcessData> processData = new ArrayList<ProcessData>();
      
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String lineFromWmic;
          int counter = 0;
          String [] lineParts;
          ArrayList<String> subCpuCalcHelperDataRecord = new ArrayList<String>();
          while ((lineFromWmic = br.readLine()) != null) {
          	if (!lineFromWmic.equals("")) {
          		counter ++;
          		if (counter >= 3) {
          			lineParts = lineFromWmic.split(" ");
          			for (int i = 0; i < lineParts.length; i++) {
          				if (!lineParts[i].equals("")) {
          					subCpuCalcHelperDataRecord.add(lineParts[i]);
          				}
          			}
          			cpuCalcHelperData.add(new CpuCalculationHelper(Integer.parseInt(subCpuCalcHelperDataRecord.get(1)), Double.parseDouble(subCpuCalcHelperDataRecord.get(0))));
          			subCpuCalcHelperDataRecord.clear();
          		}
          	}
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
      command.clear();
      command.add("tasklist");
      command.add("/fo");
      command.add("LIST");
      command.add("/v");
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String lineFromTaskList;
          int counter = 0;
          String [] lineParts;
          String name = "";
          String windowTitle = "";
          String status = "";
          String userName = "";
          int id = -1;
          double memUsage = 0;
          double cpuUsage;
          double elapsedTime = 0;
          double cpuTime = 0;
          double hoursInSec;
          double minutesInSec;
          Boolean commonIdFound = false;
          while ((lineFromTaskList = br.readLine()) != null) {
        	  lineFromTaskList = lineFromTaskList.trim();
        	  if (!lineFromTaskList.equals("")) {
        		  counter ++;
        		  if (counter == 1 || 
        			  counter == 2 ||
        			  counter == 5 ||
        			  counter == 6 ||
        			  counter == 7 ||
        			  counter == 8 ||
        			  counter == 9) {
        			  lineParts = lineFromTaskList.split(":");
        			  if(counter == 1) {
        				  name = lineParts[1].trim();
        			  }
        			  if (counter == 2) {
        				  id = Integer.parseInt(lineParts[1].trim());
        			  }
        			  if (counter == 5) {
        				  if (lineParts[1].contains("�")) {
        					  memUsage = Double.parseDouble(lineParts[1].trim().replaceAll("�", "").split(" ")[0]) / 1024;
        				  } else {
        					  memUsage = Double.parseDouble(lineParts[1].trim().replaceAll(",", "").split(" ")[0]) / 1024;
        				  }
        			  }
        			  if (counter == 6) {
        				  status = lineParts[1].trim();
        			  }
        			  if (counter == 7) {
        				  userName = lineParts[1].trim();
        			  }
        			  if (counter == 8) {
        				  hoursInSec = Double.parseDouble(lineParts[1].trim()) * 3600;
        				  minutesInSec = Double.parseDouble(lineParts[2].trim()) * 60;
        				  cpuTime = hoursInSec + minutesInSec + Double.parseDouble(lineParts[3].trim());
        				  for (int i = 0; i < cpuCalcHelperData.size(); i++) {
        		        	  if (id == cpuCalcHelperData.get(i).getProcessId()) {
        		        		  elapsedTime = cpuCalcHelperData.get(i).getElapsedTime();
        		        		  commonIdFound = true;
        		        		  break;
        		        	  }
        		          }
        			  }
        			  if (counter == 9) {
        				  windowTitle = lineParts[1].trim();
        				  if (lineParts.length > 2) {
        					  for (int i = 0; i < lineParts.length - 2; i++) {
        						  windowTitle += ":" + lineParts[i+2];
        					  }
        				  }
        			  }
        		  }
        		  if (counter % 9 == 0) {
        			  counter = 0;
        			  if (commonIdFound) {
        				  cpuTime /= numOfLogicalProcessors;
        				  cpuUsage = cpuTime / elapsedTime * 100;
        				  processData.add(new ProcessData(name, round(memUsage), round(cpuUsage), windowTitle, id, cpuTime, status, userName));
        			  }
        			  commonIdFound = false;
        		  }
        	  }
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
      return processData;
  }
  
  public static List<ProcessData> createAdvancedProcessDataArray(int numOfLogicalProcessors) throws IOException {
	  InputStream is = null;
	  InputStreamReader isr = null;
	  BufferedReader br = null;

      List<String> command = new ArrayList<String>();
      command.add("WMIC");
      command.add("path");
      command.add("Win32_PerfFormattedData_PerfProc_Process");
      command.add("get");
      command.add("ElapsedTime,");
      command.add("IDProcess");
      
      List<CpuCalculationHelper> cpuCalcHelperData = new ArrayList<CpuCalculationHelper>();
      List<ProcessData> processData = new ArrayList<ProcessData>();
      
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String lineFromWmic;
          int counter = 0;
          String [] lineParts;
          ArrayList<String> subCpuCalcHelperDataRecord = new ArrayList<String>();
          while ((lineFromWmic = br.readLine()) != null) {
          	if (!lineFromWmic.equals("")) {
          		counter ++;
          		if (counter >= 3) {
          			lineParts = lineFromWmic.split(" ");
          			for (int i = 0; i < lineParts.length; i++) {
          				if (!lineParts[i].equals("")) {
          					subCpuCalcHelperDataRecord.add(lineParts[i]);
          				}
          			}
          			cpuCalcHelperData.add(new CpuCalculationHelper(Integer.parseInt(subCpuCalcHelperDataRecord.get(1)), Double.parseDouble(subCpuCalcHelperDataRecord.get(0))));
          			subCpuCalcHelperDataRecord.clear();
          		}
          	}
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
      command.clear();
      command.add("tasklist");
      command.add("/fo");
      command.add("LIST");
      command.add("/v");
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String lineFromTaskList;
          int counter = 0;
          String [] lineParts;
          String name = "";
          int id = -1;
          double memUsage = 0;
          double cpuUsage;
          double elapsedTime = 0;
          double cpuTime = 0;
          double hoursInSec;
          double minutesInSec;
          Boolean commonIdFound = false;
          while ((lineFromTaskList = br.readLine()) != null) {
        	  lineFromTaskList = lineFromTaskList.trim();
        	  if (!lineFromTaskList.equals("")) {
        		  counter ++;
        		  if (counter == 1 || 
        			  counter == 2 ||
        			  counter == 5 ||
        			  counter == 8) {
        			  lineParts = lineFromTaskList.split(":");
        			  if(counter == 1) {
        				  name = lineParts[1].trim();
        			  }
        			  if (counter == 2) {
        				  id = Integer.parseInt(lineParts[1].trim());
        			  }
        			  if (counter == 5) {
        				  if (lineParts[1].contains("�")) {
        					  memUsage = Double.parseDouble(lineParts[1].trim().replaceAll("�", "").split(" ")[0]) / 1024;
        				  } else {
        					  memUsage = Double.parseDouble(lineParts[1].trim().replaceAll(",", "").split(" ")[0]) / 1024;
        				  }
        			  }
        			  if (counter == 8) {
        				  hoursInSec = Double.parseDouble(lineParts[1].trim()) * 3600;
        				  minutesInSec = Double.parseDouble(lineParts[2].trim()) * 60;
        				  cpuTime = hoursInSec + minutesInSec + Double.parseDouble(lineParts[3].trim());
        				  for (int i = 0; i < cpuCalcHelperData.size(); i++) {
        		        	  if (id == cpuCalcHelperData.get(i).getProcessId()) {
        		        		  elapsedTime = cpuCalcHelperData.get(i).getElapsedTime();
        		        		  commonIdFound = true;
        		        		  break;
        		        	  }
        		          }
        			  }
        		  }
        		  if (counter % 9 == 0) {
        			  counter = 0;
        			  if (commonIdFound) {
        				  cpuTime /= numOfLogicalProcessors;
        				  cpuUsage = cpuTime / elapsedTime * 100;
        				  processData.add(new ProcessData(id, name, round(memUsage), round(cpuUsage)));
        			  }
        			  commonIdFound = false;
        		  }
        	  }
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
      return processData;
  }
  
  public static int getNumOfLogicalProcessors() throws NumberFormatException, IOException {
	  InputStream is = null;
	  InputStreamReader isr = null;
	  BufferedReader br = null;

	  int num = 1;

      List<String> command = new ArrayList<String>();
      command.add("WMIC");
      command.add("cpu");
      command.add("get");
      command.add("NumberOfLogicalProcessors");
      
      try {
          ProcessBuilder builder = new ProcessBuilder(command);
          Process process = builder.start();
          is = process.getInputStream();
          isr = new InputStreamReader(is);
          br = new BufferedReader(isr);

          String line;
          while ((line = br.readLine()) != null) {
          	line = line.trim();
          	if (!line.equals("") && !line.equals("NumberOfLogicalProcessors")) {
          		num = Integer.parseInt(line);
          	}
          }
      } finally {
          if (br != null)
              br.close();
          if (isr != null)
              isr.close();
          if (is != null)
              is.close();
      }
	  return num;
  }
  
  public static OperatingSystemMXBeanHandler getOperatingSystemMXBeanComponents() {
	  OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
	  double freePhysicalMemorySize = 0;
	  double systemCpuLoad = 0;
	  double totalPhysicalMemorySize = 0;
	  for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
		  method.setAccessible(true);
		    if (method.getName().equals("getTotalPhysicalMemorySize") 
		    		&& Modifier.isPublic(method.getModifiers())) {
		            Object value;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		            totalPhysicalMemorySize = Double.parseDouble(String.valueOf(value)) / 1024 / 1024 / 1024;
		        } catch (Exception e) {
		            value = e;
		        }
		    } else if (method.getName().equals("getFreePhysicalMemorySize") 
		    		&& Modifier.isPublic(method.getModifiers())) {
		    	Object value;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		            freePhysicalMemorySize = Double.parseDouble(String.valueOf(value)) / 1024 / 1024 / 1024;
		        } catch (Exception e) {
		            value = e;
		        }
		    } else if (method.getName().equals("getSystemCpuLoad") 
		    		&& Modifier.isPublic(method.getModifiers())) {
		    	Object value;
		        try {
		            value = method.invoke(operatingSystemMXBean);
		            systemCpuLoad = Double.parseDouble(String.valueOf(value)) * 100;
		        } catch (Exception e) {
		            value = e;
		        }
		    }
	  }
	  return new OperatingSystemMXBeanHandler(round(totalPhysicalMemorySize), round(freePhysicalMemorySize), round(systemCpuLoad));
  }
  
  public static void createMemoryHeader(JPanel mem, List<ProcessData> processData, OperatingSystemMXBeanHandler operatingSystemMXBeanHandler) throws IOException {
	  double sumOfMemoryDataByCommandLineTools = 0;
	  for (int i = 0; i < processData.size(); i++) {
		  if (!processData.get(i).getName().equals("System Idle Process")) {
			  sumOfMemoryDataByCommandLineTools += processData.get(i).getMemUsage();
		  }
	  }
	  sumOfMemoryDataByCommandLineTools /= 1024;
	  GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;			    	
  	  c.gridx = 0;
      c.gridy = 0;
  	  mem.add(new JLabel("Overall Memory Usage [%] (calculated by OperatingSystemMXBean):   " + String.valueOf(round(operatingSystemMXBeanHandler.getUsedPhysicalMemorySize() / operatingSystemMXBeanHandler.getTotalPhysicalMemorySize() * 100))),c);
  	  c.gridy = 1;
  	  mem.add(new JLabel("Overall Memory Usage [%] (calculated by Command Line Tools):   " + String.valueOf(round(sumOfMemoryDataByCommandLineTools / operatingSystemMXBeanHandler.getTotalPhysicalMemorySize() * 100))),c);
	  c.gridy = 2;
  	  mem.add(new JLabel("Total Memory [GB] (calculated by OperatingSystemMXBean):   " + String.valueOf(operatingSystemMXBeanHandler.getTotalPhysicalMemorySize())),c);
  	  c.gridy = 3;
  	  mem.add(new JLabel("Used Memory [GB] (calculated by OperatingSystemMXBean):   " + String.valueOf(round(operatingSystemMXBeanHandler.getUsedPhysicalMemorySize()))),c);
  	  c.gridy = 4;
  	  mem.add(new JLabel("Used Memory [GB] (calculated by Command Line Tools):   " + String.valueOf(round(sumOfMemoryDataByCommandLineTools))),c);
	  c.gridy = 5;
  	  mem.add(new JLabel(" "),c);
  	  c.gridy = 6;
	  mem.add(new JLabel(" "),c);
	  c.gridy = 7;
	  mem.add(new JLabel(" "),c);
  	  c.gridy = 8;
	  mem.add(new JLabel("Process Name"),c);
	  c.gridx = 1;
	  mem.add(new JLabel("Used MEMORY Size [MB]"),c);
  }
    
  public static void createCpuHeader(JPanel cpu, List<ProcessData> processData, OperatingSystemMXBeanHandler operatingSystemMXBeanHandler, PowerData powerData, List<String> sumEnergyDataPerProcess) throws IOException {
	  Collections.sort(processData,new Comparator<ProcessData>() {
	  	    @Override
	  	    public int compare(ProcessData first, ProcessData second) {
	  	        return Double.compare(second.getCpuUsage(),first.getCpuUsage());
	  	    }
	  });
	  double valueOfCpuUsageByCommandLineTools = 0;
	  double currValue = 0;
      String currName = "";
      int numOfProcesses = processData.size();
	  for (int i = 0; i < numOfProcesses; i++) {
		  currValue = processData.get(i).getCpuUsage();
    	  currName = processData.get(i).getName();
    	  if (!Double.isNaN(currValue) &&
    		  !currName.equals("System Idle Process")) {
    		  valueOfCpuUsageByCommandLineTools += currValue;
		  }
	  }
      GridBagConstraints c = new GridBagConstraints();
      c.fill = GridBagConstraints.HORIZONTAL;			    	
  	  c.gridx = 0;
      c.gridy = 0;
  	  cpu.add(new JLabel("Overall CPU Usage [%] (calculated by OperatingSystemMXBean):   " + String.valueOf(operatingSystemMXBeanHandler.getSystemCpuLoad())),c);
  	  c.gridy = 1;
  	  cpu.add(new JLabel("Overall CPU Usage [%] (calculated by Command Line Tools):   " + String.valueOf(round(valueOfCpuUsageByCommandLineTools))),c);
	  c.gridy = 2;
  	  cpu.add(new JLabel("Number Of processes [1]:   " + String.valueOf(numOfProcesses)),c);
  	  String stringForPowerConsumptionPlace = " ";
  	  String stringForEnergyCostPlace = " ";
  	  if (powerData != null && sumEnergyDataPerProcess != null) {
  		  double sumOfEnergyPrice = 0;
		  for (String element : sumEnergyDataPerProcess) {
	  		  sumOfEnergyPrice += Double.parseDouble(element.split(",")[1]);
	  	  }
  		  stringForPowerConsumptionPlace = "Power Consumption [W]:   "+powerData.getPowerInWatt();
  		  stringForEnergyCostPlace = "Summary Of Overall Energy Cost [HUF]:   "+roundFiveDigits(sumOfEnergyPrice);
  	  }
  	  c.gridy = 3;
	  cpu.add(new JLabel(stringForPowerConsumptionPlace),c);
	  c.gridy = 4;
  	  cpu.add(new JLabel(stringForEnergyCostPlace),c);
  	  c.gridy = 5;
  	  cpu.add(new JLabel(" "),c);
  	  c.gridy = 6;
	  cpu.add(new JLabel(" "),c);
	  c.gridy = 7;
  	  cpu.add(new JLabel(" "),c);
  	  c.gridy = 8;
	  cpu.add(new JLabel("Process Name"),c);
	  c.gridx = 1;
	  cpu.add(new JLabel("CPU Usage [%]"),c);
  }
  
  public static void createTabGui(JPanel panel, List<ProcessData> processData, String type) throws IOException {
	  if (type.equals("cpu")) {
		  Collections.sort(processData,new Comparator<ProcessData>() {
		  	    @Override
		  	    public int compare(ProcessData first, ProcessData second) {
		  	        return Double.compare(second.getCpuUsage(),first.getCpuUsage());
		  	    }
		  });
	  } else {
		  Collections.sort(processData,new Comparator<ProcessData>() {
		  	    @Override
		  	    public int compare(ProcessData first, ProcessData second) {
		  	        return Double.compare(second.getMemUsage(),first.getMemUsage());
		  	    }
		  });
	  }
	  GridBagConstraints cLeft = new GridBagConstraints();
	  GridBagConstraints cRight = new GridBagConstraints();
	  int yCoordinate = 9;
	  int xCoordinate = 0;
	  for (int i = 0; i < processData.size(); i++) {
		  cLeft.fill = GridBagConstraints.HORIZONTAL;
		  cLeft.gridx = xCoordinate;
		  cLeft.gridy = yCoordinate;
		  cLeft.weightx = 1;
		  panel.add(new JLabel(processData.get(i).getName()), cLeft);
		  cRight.fill = GridBagConstraints.HORIZONTAL;
		  cRight.gridx = xCoordinate + 1;
		  cRight.gridy = yCoordinate;
		  cRight.weightx = 1;
		  if (type.equals("cpu")) {
			  panel.add(new JLabel(String.valueOf(processData.get(i).getCpuUsage())),cRight);
		  } else {
			  panel.add(new JLabel(String.valueOf(processData.get(i).getMemUsage())),cRight);
		  }
		  yCoordinate ++;
	  }
	  panel.revalidate();
  }

}
