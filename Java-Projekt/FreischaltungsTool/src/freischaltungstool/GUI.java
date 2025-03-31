package freischaltungstool;

import java.awt.Color;
import java.awt.Font;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JPanel panelEdit;
	private JPanel panelSearch;
	private JPanel panelPackages;
	private JScrollPane scrollPane;

	private JTextField inputReleaseName;
	private JTextField inputReleaseGroup;
	private JTextField inputReleaseComment;
	private JTextField inputEditPCName;
	private JTextField inputEditName;
	private JTextField inputEditGroup;
	private JTextField inputEditComment;
	private JTextField inputSearch;

	private JLabel labelTitelRelease;
	private JLabel labelUndertitelRelease;
	private JLabel labelPickPackage;
	private JLabel labelReleaseName;
	private JLabel labelReleaseGroup;
	private JLabel labelReleaseComment;
	private JLabel labelTitelEdit;
	private JLabel labelEditTransfer;
	private JLabel labelEditPCName;
	private JLabel labelEditName;
	private JLabel labelEditGroup;
	private JLabel labelEditComment;
	private JLabel labelMainTitel;
	private JLabel labelSearch;
	private JLabel labelCountdown;
	private JLabel labelEditInfoDelete;
	private JLabel labelEditInfoPackages;
	private JLabel labelEditDelete;
	private JLabel labelGroup;
	private JLabel labelLegend;
	private JLabel labelStatus;

	private ButtonGroup group;

	private JButton buttonReleaseSave;
	private JButton buttonEditDelete;
	private JButton buttonBack;
	private JButton buttonEditSave;
	private JButton buttonSearch;
	private JButton buttonExit;

	private JCheckBox checkBoxEdit;
	private JCheckBox checkBoxRelease;
	private JCheckBox checkBoxNsLookupSearch;
	private JCheckBox checkBoxNsLookupEdit;
	// private JRadioButton selectableButton;

	private JSeparator separator;
	private JSeparator separator_1;

	private JComboBox<String> comboBoxPackages;
	private JComboBox<String> comboLanguage;
	private JComboBox<String> comboGroup;

	private UserConfig userconfig = new UserConfig();
	private AMSLock amsLock = new AMSLock();
	private String[] user = userconfig.getUserConfig();
	private File ownLockFile = new File("XNIMGRP.CFG.lck");
	private Logger log = new Logger();
	private Language getLanguage = new Language();
	private ReadIni iniFile = new ReadIni();
	private String currentLanguage = user[1];
	private HashMap<String, String> chosenLanguage = new HashMap<>();
	private ArrayList<String> availableLanguages = new ArrayList<>();
	// contains all packages in the Ini-File
	private ArrayList<String> packages = new ArrayList<String>();
	// contains all packages approved packages for the chosen pc
	private ArrayList<String> packagesForPC = new ArrayList<String>();
	private NSLookup checkPC = new NSLookup();
	private javax.swing.Timer timer;
	private static int remainingTime = 900000;
	private long lastModified;
	private boolean wasModified = false;
	private JScrollPane scrollPane_1;
	private JTextArea statusBar;
	private JTextArea infoApplication;

	/**
	 * Create the frame.
	 * 
	 * 
	 */
	public GUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		// will be executed before the application is finally closed
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (remainingTime > 0) {
					log.logMe("Anwendung wurde manuell beendet");
					ownLockFile.delete();
				}
				if (remainingTime == 0) {
					log.logMe("Anwendung wurde aufgrund des überschreiten der Bearbeitungsdauer beendet");
				}
				userconfig.setUserConfig(comboGroup.getSelectedItem(), currentLanguage);

			}
		}, "Shutdown-thread"));

		// Get current delay
		int dismissDelay = ToolTipManager.sharedInstance().getDismissDelay();

		// Keep the tool tip showing
		dismissDelay = Integer.MAX_VALUE;
		ToolTipManager.sharedInstance().setDismissDelay(dismissDelay);

		// start timer for the label which show the remaining work time
		timer = new javax.swing.Timer(1000, new RefreshCountdown());
		timer.start();

		// get texts for the application
		getLanguage.getTexts("#" + user[1], chosenLanguage, availableLanguages);
		lockIni();

		// set remainingTime equals to the value which is set in the text-file
		// "languages.txt", when it is set
		if (chosenLanguage.get("close_Time") != "empty") {
			remainingTime = Integer.parseInt(chosenLanguage.get("close_Time"));
			remainingTime = remainingTime * 60 * 1000;
		}
		// get the alteration date of the ini-file
		lastModified = getAlterationDate();

		setIconImage(Toolkit.getDefaultToolkit()
				.getImage(getClass().getResource("/freischaltungstool/resource/Unbenannt.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1100, 740);
		setResizable(false);
		setTitle(chosenLanguage.get("titleGUI"));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		panelEdit = new JPanel();
		panelEdit.setBounds(0, 0, 1094, 707);
		contentPane.add(panelEdit);
		panelEdit.setLayout(null);
		panelEdit.setVisible(false);

		labelTitelRelease = new JLabel(chosenLanguage.get("labelTitelRelease"));
		labelTitelRelease.setFont(new Font("Tahoma", Font.PLAIN, 26));
		labelTitelRelease.setBounds(12, 33, 182, 32);
		panelEdit.add(labelTitelRelease);

		labelUndertitelRelease = new JLabel(chosenLanguage.get("labelUndertitelRelease"));
		labelUndertitelRelease.setForeground(Color.BLACK);
		labelUndertitelRelease.setFont(new Font("Tahoma", Font.BOLD, 16));
		labelUndertitelRelease.setBounds(328, 13, 394, 18);
		panelEdit.add(labelUndertitelRelease);

		labelPickPackage = new JLabel(chosenLanguage.get("labelPickPackage"));
		labelPickPackage.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelPickPackage.setBounds(12, 78, 233, 19);
		panelEdit.add(labelPickPackage);

		labelReleaseName = new JLabel(chosenLanguage.get("labelReleaseName"));
		labelReleaseName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelReleaseName.setBounds(12, 107, 233, 19);
		panelEdit.add(labelReleaseName);

		labelReleaseGroup = new JLabel(chosenLanguage.get("labelReleaseGroup"));
		labelReleaseGroup.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelReleaseGroup.setBounds(12, 136, 233, 19);
		panelEdit.add(labelReleaseGroup);

		labelReleaseComment = new JLabel(chosenLanguage.get("labelReleaseComment"));
		labelReleaseComment.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelReleaseComment.setBounds(12, 165, 233, 19);
		panelEdit.add(labelReleaseComment);

		inputReleaseName = new JTextField();
		inputReleaseName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputReleaseName.setBounds(258, 107, 300, 24);
		panelEdit.add(inputReleaseName);
		inputReleaseName.setColumns(10);

		inputReleaseGroup = new JTextField();
		inputReleaseGroup.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputReleaseGroup.setBounds(258, 136, 300, 24);
		panelEdit.add(inputReleaseGroup);
		inputReleaseGroup.setColumns(10);

		inputReleaseComment = new JTextField();
		inputReleaseComment.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputReleaseComment.setBounds(258, 165, 300, 24);
		panelEdit.add(inputReleaseComment);
		inputReleaseComment.setColumns(10);

		comboBoxPackages = new JComboBox<String>();
		comboBoxPackages.setFont(new Font("Tahoma", Font.PLAIN, 16));
		comboBoxPackages.setBounds(257, 78, 300, 24);

		panelEdit.add(comboBoxPackages);

		buttonReleaseSave = new JButton(chosenLanguage.get("buttonReleaseSave"));
		buttonReleaseSave.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonReleaseSave.setBounds(570, 164, 152, 25);
		panelEdit.add(buttonReleaseSave);
		buttonReleaseSave.addActionListener(new SaveRelease());

		labelTitelEdit = new JLabel(chosenLanguage.get("labelTitelEdit"));
		labelTitelEdit.setFont(new Font("Tahoma", Font.PLAIN, 26));
		labelTitelEdit.setBounds(12, 218, 383, 32);
		panelEdit.add(labelTitelEdit);

		buttonBack = new JButton(chosenLanguage.get("buttonBack"));
		buttonBack.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonBack.setBounds(12, 669, 182, 25);
		buttonBack.addActionListener(new Back());
		String imgBackward = "/freischaltungstool/resource/BackwardIcon.png";
		URL imgURLBackward = getClass().getResource(imgBackward);
		BufferedImage biBackward;
		try {
			biBackward = ImageIO.read(imgURLBackward);
			ImageIcon icon = new ImageIcon(biBackward);
			buttonBack.setIcon(icon);
		} catch (IOException e) {
			e.printStackTrace();
		}
		buttonBack.setHorizontalAlignment(SwingConstants.CENTER);
		panelEdit.add(buttonBack);

		scrollPane = new JScrollPane();
		scrollPane.setBounds(734, 266, 346, 244);
		panelEdit.add(scrollPane);

		panelPackages = new JPanel();
		scrollPane.setViewportView(panelPackages);
		panelPackages.setLayout(new BoxLayout(panelPackages, BoxLayout.Y_AXIS));
		panelPackages.setBackground(Color.WHITE);

		separator = new JSeparator();
		separator.setBounds(12, 208, 1070, 25);
		panelEdit.add(separator);

		labelEditTransfer = new JLabel(chosenLanguage.get("labelEditTransfer"));
		labelEditTransfer.setFont(new Font("Tahoma", Font.PLAIN, 18));
		labelEditTransfer.setBounds(12, 262, 250, 32);
		panelEdit.add(labelEditTransfer);

		labelEditPCName = new JLabel(chosenLanguage.get("labelEditPCName"));
		labelEditPCName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEditPCName.setBounds(12, 311, 223, 19);
		panelEdit.add(labelEditPCName);

		labelEditName = new JLabel(chosenLanguage.get("labelEditName"));
		labelEditName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEditName.setBounds(12, 340, 223, 19);
		panelEdit.add(labelEditName);

		labelEditGroup = new JLabel(chosenLanguage.get("labelEditGroup"));
		labelEditGroup.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEditGroup.setBounds(12, 368, 223, 19);
		panelEdit.add(labelEditGroup);

		labelEditComment = new JLabel(chosenLanguage.get("labelEditComment"));
		labelEditComment.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEditComment.setBounds(12, 397, 223, 19);
		panelEdit.add(labelEditComment);

		inputEditPCName = new JTextField();
		inputEditPCName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputEditPCName.setBounds(258, 306, 300, 24);
		panelEdit.add(inputEditPCName);
		inputEditPCName.setColumns(10);

		inputEditName = new JTextField();
		inputEditName.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputEditName.setBounds(258, 335, 300, 24);
		panelEdit.add(inputEditName);
		inputEditName.setColumns(10);

		inputEditGroup = new JTextField();
		inputEditGroup.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputEditGroup.setBounds(258, 363, 300, 24);
		panelEdit.add(inputEditGroup);
		inputEditGroup.setColumns(10);

		inputEditComment = new JTextField();
		inputEditComment.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputEditComment.setBounds(258, 392, 300, 24);
		panelEdit.add(inputEditComment);
		inputEditComment.setColumns(10);

		buttonEditSave = new JButton(chosenLanguage.get("buttonEditSave"));
		buttonEditSave.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonEditSave.setBounds(570, 391, 152, 25);
		buttonEditSave.addActionListener(new SaveEdit());
		panelEdit.add(buttonEditSave);
		String imgSave = "/freischaltungstool/resource/SaveIcon.png";
		URL imgURLSave = getClass().getResource(imgSave);
		BufferedImage biSave;
		try {
			biSave = ImageIO.read(imgURLSave);
			ImageIcon icon = new ImageIcon(biSave);
			buttonReleaseSave.setIcon(icon);
			buttonEditSave.setIcon(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}
		buttonEditSave.setHorizontalAlignment(SwingConstants.CENTER);
		buttonReleaseSave.setHorizontalAlignment(SwingConstants.CENTER);

		labelEditDelete = new JLabel(chosenLanguage.get("labelEditDelete"));
		labelEditDelete.setBounds(12, 425, 595, 32);
		panelEdit.add(labelEditDelete);
		labelEditDelete.setFont(new Font("Tahoma", Font.PLAIN, 18));

		buttonEditDelete = new JButton(chosenLanguage.get("buttonEditDelete"));
		buttonEditDelete.setBounds(12, 474, 132, 25);
		panelEdit.add(buttonEditDelete);
		String imgDelete = "/freischaltungstool/resource/DeleteIcon.png";
		URL imgURLDelete = getClass().getResource(imgDelete);
		BufferedImage biDelete;
		try {
			biDelete = ImageIO.read(imgURLDelete);
			ImageIcon icon = new ImageIcon(biDelete);
			buttonEditDelete.setIcon(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}
		buttonEditDelete.setHorizontalAlignment(SwingConstants.CENTER);
		buttonEditDelete.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonEditDelete.addActionListener(new Delete());
		buttonEditDelete.setToolTipText(chosenLanguage.get("tooltipButtonEditDelete"));

		labelEditInfoDelete = new JLabel(chosenLanguage.get("labelEditInfoDelete"));
		labelEditInfoDelete.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelEditInfoDelete.setBounds(154, 480, 450, 19);
		panelEdit.add(labelEditInfoDelete);

		separator_1 = new JSeparator();
		separator_1.setBounds(12, 509, 723, 9);
		panelEdit.add(separator_1);

		String imgName = "/freischaltungstool/resource/Desy_logo.png";
		URL imgURL = getClass().getResource(imgName);
		BufferedImage img;
		try {
			img = ImageIO.read(imgURL);
			ImageIcon icon = new ImageIcon(img);
			JLabel labelDesyLogo = new JLabel(icon);
			labelDesyLogo.setBounds(889, 64, 120, 120);
			panelEdit.add(labelDesyLogo);
		} catch (IOException e) {

			e.printStackTrace();
		}

		checkBoxRelease = new JCheckBox("New check box");
		checkBoxRelease.setBounds(570, 136, 152, 25);
		panelEdit.add(checkBoxRelease);

		checkBoxEdit = new JCheckBox("New check box");
		checkBoxEdit.setBounds(570, 364, 152, 25);
		panelEdit.add(checkBoxEdit);

		labelLegend = new JLabel();
		labelLegend.setBounds(738, 234, 342, 16);
		panelEdit.add(labelLegend);

		buttonExit = new JButton();
		buttonExit.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonExit.setBounds(950, 669, 132, 25);
		buttonExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Close application
				System.exit(0);
			}
		});

		String imgExit = "/freischaltungstool/resource/ExitIcon.png";
		URL imgURLExit = getClass().getResource(imgExit);
		BufferedImage biExit;
		try {
			biExit = ImageIO.read(imgURLExit);
			ImageIcon icon = new ImageIcon(biExit);
			buttonExit.setIcon(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}

		panelEdit.add(buttonExit);

		checkBoxNsLookupEdit = new JCheckBox();
		checkBoxNsLookupEdit.setBounds(570, 338, 156, 25);
		panelEdit.add(checkBoxNsLookupEdit);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setBounds(12, 550, 1068, 110);
		panelEdit.add(scrollPane_1);

		statusBar = new JTextArea();
		statusBar.setEnabled(false);
		statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
		statusBar.setRows(5);
		statusBar.setEditable(false);
		scrollPane_1.setViewportView(statusBar);

		labelStatus = new JLabel("StatusBar");
		labelStatus.setFont(new Font("Tahoma", Font.PLAIN, 15));
		labelStatus.setBounds(12, 521, 710, 16);
		panelEdit.add(labelStatus);

		panelSearch = new JPanel();
		panelSearch.setBounds(0, 0, 1094, 707);
		contentPane.add(panelSearch);
		panelSearch.setLayout(null);

		infoApplication = new JTextArea();
		infoApplication.setEnabled(false);
		infoApplication.setBounds(12, 80, 740, 200);
		infoApplication.setLineWrap(true);
		infoApplication.setWrapStyleWord(true);
		infoApplication.setBackground(SystemColor.menu);
		infoApplication.setEditable(false);
		infoApplication.setDisabledTextColor(Color.BLACK);
		panelSearch.add(infoApplication);

		labelMainTitel = new JLabel(chosenLanguage.get("labelMainTitel"));
		labelMainTitel.setFont(new Font("Tahoma", Font.PLAIN, 26));
		labelMainTitel.setBounds(305, 275, 265, 35);
		panelSearch.add(labelMainTitel);

		labelSearch = new JLabel(chosenLanguage.get("labelSearch"));
		labelSearch.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelSearch.setBounds(306, 338, 116, 19);
		panelSearch.add(labelSearch);

		inputSearch = new JTextField();
		inputSearch.setFont(new Font("Tahoma", Font.PLAIN, 16));
		inputSearch.setBounds(434, 333, 116, 24);
		panelSearch.add(inputSearch);
		inputSearch.setColumns(10);
		inputSearch.addActionListener(new SearchPC());

		buttonSearch = new JButton(chosenLanguage.get("buttonSearch"));
		buttonSearch.setFont(new Font("Tahoma", Font.PLAIN, 16));
		buttonSearch.setBounds(562, 333, 116, 24);
		buttonSearch.addActionListener(new SearchPC());
		String imgForward = "/freischaltungstool/resource/ForwardIcon.png";
		URL imgURLForward = getClass().getResource(imgForward);
		BufferedImage biForward;
		try {
			biForward = ImageIO.read(imgURLForward);
			ImageIcon icon = new ImageIcon(biForward);
			buttonSearch.setIcon(icon);
		} catch (IOException e) {

			e.printStackTrace();
		}

		buttonSearch.setVerticalTextPosition(SwingConstants.CENTER);
		buttonSearch.setHorizontalTextPosition(SwingConstants.LEFT);
		// buttonSearch.setHorizontalAlignment(SwingConstants.LEFT);

		checkBoxNsLookupSearch = new JCheckBox();
		checkBoxNsLookupSearch.setBounds(562, 370, 244, 25);
		panelSearch.add(checkBoxNsLookupSearch);

		panelSearch.add(buttonSearch);

		SimpleDateFormat df = new SimpleDateFormat("mm:ss");
		labelCountdown = new JLabel(chosenLanguage.get("labelCountdown") + df.format(remainingTime));
		labelCountdown.setHorizontalAlignment(SwingConstants.RIGHT);
		labelCountdown.setBounds(614, 13, 468, 18);
		panelSearch.add(labelCountdown);
		labelCountdown.setFont(new Font("Tahoma", Font.PLAIN, 16));
		labelCountdown.setToolTipText(chosenLanguage.get("tooltipLabelCountdown"));

		comboLanguage = new JComboBox<String>();
		comboLanguage.setBounds(904, 669, 178, 24);
		comboLanguage.addActionListener(new ChangeLanguage());
		panelSearch.add(comboLanguage);

		comboGroup = new JComboBox<String>();
		if (user[0].contains("IT")) {
			comboGroup.addItem("IT");
			comboGroup.addItem("IPP");
		} else {
			comboGroup.addItem("IPP");
			comboGroup.addItem("IT");
		}
		comboGroup.setBounds(838, 669, 54, 22);
		panelSearch.add(comboGroup);

		labelGroup = new JLabel();
		labelGroup.setHorizontalAlignment(SwingConstants.RIGHT);
		labelGroup.setBounds(686, 673, 140, 16);
		panelSearch.add(labelGroup);

		String imgBackground = "/freischaltungstool/resource/DESY-LogoGross2.png";
		URL imgURLBackground = getClass().getResource(imgBackground);
		BufferedImage biBackground;
		try {
			biBackground = ImageIO.read(imgURLBackground);
			ImageIcon iconBackground = new ImageIcon(biBackground);
			JLabel labelDesyLogoBackground = new JLabel(iconBackground);
			labelDesyLogoBackground.setBounds(257, 36, 600, 600);
			panelSearch.add(labelDesyLogoBackground);
		} catch (IOException e) {

			e.printStackTrace();
		}

		infoApplication.setOpaque(false);
		checkBoxNsLookupSearch.setOpaque(false);

		checkBoxNsLookupSearch.setSelected(true);
		checkBoxNsLookupEdit.setSelected(true);

		getStatus();
		setTabOrder();
		setLanguage();
		setVisible(true);
	}

	private void getStatus() {
		ArrayList<String> status = new ArrayList<>();
		log.getStatus(status);
		statusBar.setText(status.get(0) + "\r\n" + status.get(1) + "\r\n" + status.get(2) + "\r\n" + status.get(3)
				+ "\r\n" + status.get(4));
	}

	@SuppressWarnings("deprecation")
	private void setTabOrder() {
		// tab order for the search frame

		inputSearch.setNextFocusableComponent(buttonSearch);
		buttonSearch.setNextFocusableComponent(checkBoxNsLookupSearch);
		checkBoxNsLookupSearch.setNextFocusableComponent(comboGroup);
		comboGroup.setNextFocusableComponent(comboLanguage);
		comboLanguage.setNextFocusableComponent(inputSearch);

		// tab order for the edit frame
		comboBoxPackages.setNextFocusableComponent(inputReleaseName);
		inputReleaseName.setNextFocusableComponent(inputReleaseGroup);
		inputReleaseGroup.setNextFocusableComponent(inputReleaseComment);
		inputReleaseComment.setNextFocusableComponent(checkBoxRelease);
		checkBoxRelease.setNextFocusableComponent(buttonReleaseSave);

		inputEditName.setNextFocusableComponent(inputEditGroup);
		inputEditGroup.setNextFocusableComponent(inputEditComment);
		inputEditComment.setNextFocusableComponent(checkBoxNsLookupEdit);
		checkBoxNsLookupEdit.setNextFocusableComponent(checkBoxEdit);
		checkBoxEdit.setNextFocusableComponent(buttonEditSave);
	}

	private long getAlterationDate() {
		File test = new File("XNIMGRP.CFG");
		return test.lastModified();
	}

	private void lockIni() {

		if (ownLockFile.exists() && !ownLockFile.isDirectory()) {
			JOptionPane.showMessageDialog(null, chosenLanguage.get("infoFileInUse") + iniFile.getUsername(),
					chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		} else {
			try {
				FileWriter writer = new FileWriter("XNIMGRP.CFG.lck");
				writer.write(System.getProperty("user.name").toLowerCase());
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * prove if the ini-file was edited. If it was edited, the ArrayList
	 * "packagesForPC" will be refreshed to be sure that the select package was
	 * not approved during work-time by another program
	 * 
	 * @param modifier
	 *            for: 0 = new entry, 1 = transfer entry, 2 = delete entry
	 */
	private void proveIniOnModification(int modifier) {
		boolean check = false;
		ArrayList<String> oldPackagesForPC = new ArrayList<>();

		for (int i = 0; i < packagesForPC.size(); i++) {
			oldPackagesForPC.add(packagesForPC.get(i));
		}

		int numberOfPackages = oldPackagesForPC.size();

		iniFile.getPackageForPC(packagesForPC, inputSearch.getText());
		if (modifier == 0) {
			createJRadioButtons();

			// prove if
			if (oldPackagesForPC.contains(comboBoxPackages.getSelectedItem())) {
				check = true;
			}
		}

		if (numberOfPackages < packagesForPC.size() || numberOfPackages > packagesForPC.size()) {
			wasModified = true;
		}

		Collection<String> collection = packagesForPC;
		oldPackagesForPC.removeAll(collection);
		if (oldPackagesForPC.size() != 0) {
			wasModified = true;
		}

		//
		if (check == true) {
			wasModified = false;
		}

	}

	private class ChangeLanguage implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (comboLanguage.getSelectedItem() != chosenLanguage.get("comboLanguage")) {
				getLanguage.getTexts("#" + comboLanguage.getSelectedItem(), chosenLanguage, availableLanguages);
				currentLanguage = comboLanguage.getSelectedItem().toString();
				setLanguage();
			}
		}
	}

	// set text for all components. The texts comes from a
	// text-file(languages.txt). The texts for the current language are saved to
	// the HashMap "chosenlanguage"
	private void setLanguage() {
		comboLanguage.removeAllItems();
		comboLanguage.addItem(chosenLanguage.get("comboLanguage"));
		for (int i = 0; i < availableLanguages.size(); i++) {
			comboLanguage.addItem(availableLanguages.get(i));
		}

		setTitle(chosenLanguage.get("titleGUI"));

		labelTitelRelease.setText(chosenLanguage.get("labelTitelRelease"));
		if (chosenLanguage.get("tooltiplabelTitelRelease") != "empty")
			labelTitelRelease.setToolTipText(chosenLanguage.get("tooltiplabelTitelRelease"));

		labelUndertitelRelease.setText(chosenLanguage.get("labelUndertitelRelease"));
		if (chosenLanguage.get("tooltiplabelUndertitelRelease") != "empty")
			labelUndertitelRelease.setToolTipText(chosenLanguage.get("tooltiplabelUndertitelRelease"));

		labelPickPackage.setText(chosenLanguage.get("labelPickPackage") + "*");
		if (chosenLanguage.get("tooltiplabelPickPackage") != "empty")
			labelPickPackage.setToolTipText(chosenLanguage.get("tooltiplabelPickPackage"));

		labelReleaseName.setText(chosenLanguage.get("labelReleaseName") + "*");
		if (chosenLanguage.get("tooltiplabelReleaseName") != "empty")
			labelReleaseName.setToolTipText(chosenLanguage.get("tooltiplabelReleaseName"));

		labelReleaseGroup.setText(chosenLanguage.get("labelReleaseGroup") + "*");
		if (chosenLanguage.get("tooltiplabelReleaseGroup") != "empty")
			labelReleaseGroup.setToolTipText(chosenLanguage.get("tooltiplabelReleaseGroup"));

		labelReleaseComment.setText(chosenLanguage.get("labelReleaseComment"));
		if (chosenLanguage.get("tooltiplabelReleaseComment") != "empty")
			labelReleaseComment.setToolTipText(chosenLanguage.get("tooltiplabelReleaseComment"));

		labelTitelEdit.setText(chosenLanguage.get("labelTitelEdit"));
		if (chosenLanguage.get("tooltiplabelTitelEdit") != "empty")
			labelTitelEdit.setToolTipText(chosenLanguage.get("tooltiplabelTitelEdit"));

		labelEditTransfer.setText(chosenLanguage.get("labelEditTransfer"));
		if (chosenLanguage.get("tooltiplabelEditTransfer") != "empty")
			labelEditTransfer.setToolTipText(chosenLanguage.get("tooltiplabelEditTransfer"));

		labelEditPCName.setText(chosenLanguage.get("labelEditPCName") + "*");
		if (chosenLanguage.get("tooltiplabelEditPCName") != "empty")
			labelEditPCName.setToolTipText(chosenLanguage.get("tooltiplabelEditPCName"));

		labelEditName.setText(chosenLanguage.get("labelEditName") + "*");
		if (chosenLanguage.get("tooltiplabelEditName") != "empty")
			labelEditName.setToolTipText(chosenLanguage.get("tooltiplabelEditName"));

		labelEditGroup.setText(chosenLanguage.get("labelEditGroup") + "*");
		if (chosenLanguage.get("tooltiplabelEditGroup") != "empty")
			labelEditGroup.setToolTipText(chosenLanguage.get("tooltiplabelEditGroup"));

		labelEditComment.setText(chosenLanguage.get("labelEditComment"));
		if (chosenLanguage.get("tooltiplabelEditComment") != "empty")
			labelEditComment.setToolTipText(chosenLanguage.get("tooltiplabelEditComment"));

		labelMainTitel.setText(chosenLanguage.get("labelMainTitel"));
		if (chosenLanguage.get("tooltiplabelMainTitel") != "empty")
			labelMainTitel.setToolTipText(chosenLanguage.get("tooltiplabelMainTitel"));

		labelSearch.setText(chosenLanguage.get("labelSearch"));
		if (chosenLanguage.get("tooltiplabelSearch") != "empty")
			labelSearch.setToolTipText(chosenLanguage.get("tooltiplabelSearch"));

		labelEditInfoDelete.setText(chosenLanguage.get("labelEditInfoDelete"));
		if (chosenLanguage.get("tooltiplabelEditInfoDelete") != "empty")
			labelEditInfoDelete.setToolTipText(chosenLanguage.get("tooltiplabelEditInfoDelete"));

		labelEditDelete.setText(chosenLanguage.get("labelEditDelete"));
		if (chosenLanguage.get("tooltiplabelEditDelete") != "empty")
			labelEditDelete.setToolTipText(chosenLanguage.get("tooltiplabelEditDelete"));

		labelLegend.setText("* = " + chosenLanguage.get("labelLegend"));
		if (chosenLanguage.get("tooltiplabelLegend") != "empty")
			labelLegend.setToolTipText(chosenLanguage.get("tooltiplabelLegend"));

		labelStatus.setText(chosenLanguage.get("labelStatus"));
		if (chosenLanguage.get("tooltiplabelStatus") != "empty")
			labelStatus.setToolTipText(chosenLanguage.get("tooltiplabelStatus"));

		buttonReleaseSave.setText(chosenLanguage.get("buttonReleaseSave"));
		if (chosenLanguage.get("tooltipbuttonReleaseSave") != "empty")
			buttonReleaseSave.setToolTipText(chosenLanguage.get("tooltipbuttonReleaseSave"));

		buttonEditDelete.setText(chosenLanguage.get("buttonEditDelete"));
		if (chosenLanguage.get("tooltipbuttonEditDelete") != "empty")
			buttonEditDelete.setToolTipText(chosenLanguage.get("tooltipbuttonEditDelete"));

		buttonBack.setText(chosenLanguage.get("buttonBack"));
		if (chosenLanguage.get("tooltipbuttonBack") != "empty")
			buttonBack.setToolTipText(chosenLanguage.get("tooltipbuttonBack"));

		buttonEditSave.setText(chosenLanguage.get("buttonEditSave"));
		if (chosenLanguage.get("tooltipbuttonEditSave") != "empty")
			buttonEditSave.setToolTipText(chosenLanguage.get("tooltipbuttonEditSave"));

		buttonSearch.setText(chosenLanguage.get("buttonSearch"));
		if (chosenLanguage.get("tooltipbuttonSearch") != "empty")
			buttonSearch.setToolTipText(chosenLanguage.get("tooltipbuttonSearch"));

		buttonExit.setText(chosenLanguage.get("buttonExit"));
		if (chosenLanguage.get("tooltipbuttonExit") != "empty")
			buttonExit.setToolTipText(chosenLanguage.get("tooltipbuttonExit"));

		checkBoxEdit.setText(chosenLanguage.get("checkBoxEntry"));
		if (chosenLanguage.get("tooltipcheckBoxEdit") != "empty")
			checkBoxEdit.setToolTipText(chosenLanguage.get("tooltipcheckBoxEdit"));
		checkBoxRelease.setText(chosenLanguage.get("checkBoxEntry"));
		if (chosenLanguage.get("tooltipcheckBoxRelease") != "empty")
			checkBoxRelease.setToolTipText(chosenLanguage.get("tooltipcheckBoxRelease"));

		if (chosenLanguage.get("tooltipcomboGroup") != "empty")
			comboGroup.setToolTipText(chosenLanguage.get("tooltipcomboGroup"));

		checkBoxNsLookupSearch.setText(chosenLanguage.get("checkBoxNsLookup"));
		if (chosenLanguage.get("tooltipcheckBoxNsLookup") != "empty")
			checkBoxNsLookupSearch.setToolTipText(chosenLanguage.get("tooltipcheckBoxNsLookup"));

		checkBoxNsLookupEdit.setText(chosenLanguage.get("checkBoxNsLookup"));
		if (chosenLanguage.get("tooltipcheckBoxNsLookup") != "empty")
			checkBoxNsLookupEdit.setToolTipText(chosenLanguage.get("tooltipcheckBoxNsLookup"));

		if (chosenLanguage.get("infoAboutApplication") != "empty")
			infoApplication.setText(chosenLanguage.get("infoAboutApplication"));

		labelGroup.setText(chosenLanguage.get("labelGroup"));

		labelCountdown.setToolTipText(chosenLanguage.get("tooltipLabelCountdown"));
		buttonEditDelete.setToolTipText(chosenLanguage.get("tooltipButtonEditDelete"));

	}

	private void resetCloseTimer() {
		if (chosenLanguage.get("close_Time") != "empty") {
			remainingTime = Integer.parseInt(chosenLanguage.get("close_Time"));
			remainingTime = remainingTime * 60 * 1000;
		} else
			remainingTime = 900000;
	}

	private class RefreshCountdown implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			remainingTime -= 1000;
			SimpleDateFormat df = new SimpleDateFormat("mm:ss");
			labelCountdown.setText(chosenLanguage.get("labelCountdown") + df.format(remainingTime));
			if (remainingTime == 0) {
				timer.stop();
				ownLockFile.delete();
				JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoClosedByTime"));
				System.exit(0);
			}
		}
	}

	private class SearchPC implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			resetCloseTimer();
			// prove if "inputSearch" is empty or contains only blank chars
			if (inputSearch.getText().trim().length() != 0) {
				// prove if the entered pcname is found through nslookup or the
				// checkbox (disable nslookup) is activated
				if (checkPC.prove(inputSearch.getText()) == true || checkBoxNsLookupSearch.isSelected() == false) {
					panelSearch.setVisible(false);
					panelEdit.add(labelCountdown);
					labelUndertitelRelease
							.setText(chosenLanguage.get("labelUndertitelRelease") + inputSearch.getText());
					panelEdit.setVisible(true);

					iniFile.getPackages(packages, comboGroup.getSelectedItem());
					// get available packages for the entered pcname
					iniFile.getPackageForPC(packagesForPC, inputSearch.getText());

					comboBoxPackages.removeAllItems();
					comboBoxPackages.addItem(chosenLanguage.get("comboBoxPackages"));
					for (int i = 0; i < packages.size(); i++) {
						comboBoxPackages.addItem(packages.get(i));
					}
					createJRadioButtons();
					comboBoxPackages.requestFocus();
				} else {
					JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoPCNotFound"),
							chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
				}
			} else {
				JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoEmptyFields"),
						chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	/**
	 * 
	 * creates radiobuttons for the approved packages of the entered pcname
	 * 
	 */
	private void createJRadioButtons() {
		// the panel has to be reset, else the texts of the old buttons lies
		// under the new buttons
		panelEdit.setVisible(false);
		panelEdit.setVisible(true);
		panelPackages.removeAll();
		group = new ButtonGroup();
		if (packagesForPC.size() == 0) {
			labelEditInfoPackages = new JLabel(chosenLanguage.get("labelEditInfoPackages"));
			panelPackages.add(labelEditInfoPackages);
		}
		for (int i = 0; i < packagesForPC.size(); i++) {
			JRadioButton selectableButton = new JRadioButton(String.valueOf(i));
			selectableButton.setBackground(Color.WHITE);
			selectableButton.setText(packagesForPC.get(i));
			selectableButton.setFont(new Font("Tahoma", Font.PLAIN, 14));
			selectableButton.setActionCommand("[" + packagesForPC.get(i) + "]");
			group.add(selectableButton);
			panelPackages.add(selectableButton);
		}
	}

	private class SaveRelease implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			resetCloseTimer();

			boolean repeat = true;
			while (repeat == true) {
				// prove if the AMS is editing the ini-file right now
				if (amsLock.checkaccess() == true) {

					int result2 = JOptionPane
							.showOptionDialog(contentPane, chosenLanguage.get("infoBlockedByAMS"),
									chosenLanguage.get("dialogDeleteTitle"), JOptionPane.OK_CANCEL_OPTION,
									JOptionPane.INFORMATION_MESSAGE, null,
									new String[] { chosenLanguage.get("dialogAMSBlockRepeat"),
											chosenLanguage.get("dialogAMSBlockCancel") },
									chosenLanguage.get("dialogDeleteOK"));
					if (result2 != JOptionPane.OK_OPTION) {
						repeat = false;
					}
				} else {
					repeat = false;
					// prove if the ini-file was edited
					if (lastModified != getAlterationDate()) {
						proveIniOnModification(0);
					}

					// prove if the selected item is not the current
					// title(language) of the comboBox
					if (comboBoxPackages.getSelectedItem() != chosenLanguage.get("comboBoxPackages")) {
						// prove if the selected package is already approved
						if (!packagesForPC.contains(comboBoxPackages.getSelectedItem())) {
							// prove if the text fields are not empty
							if (inputReleaseName.getText().trim().length() != 0
									&& inputReleaseGroup.getText().trim().length() != 0) {
								iniFile.writeNewEntry(inputReleaseName.getText(), inputReleaseGroup.getText(),
										inputReleaseComment.getText(), comboBoxPackages.getSelectedItem(),
										inputSearch.getText());
								iniFile.getPackageForPC(packagesForPC, inputSearch.getText());
								createJRadioButtons();

								// get the new alteration date of the ini-file
								lastModified = getAlterationDate();

								wasModified = false;

								log.logMe("Das Paket " + comboBoxPackages.getSelectedItem() + " wurde für den Rechner "
										+ inputSearch.getText() + " freigeschaltet");

								comboBoxPackages.setSelectedItem(chosenLanguage.get("comboBoxPackages"));
								// if comboBoxPackages is selected, the text
								// fields
								// will not be clear after click on the save
								// button
								if (checkBoxRelease.isSelected() == false) {
									inputReleaseName.setText(null);
									inputReleaseGroup.setText(null);
									inputReleaseComment.setText(null);
								}

								// refresh statusbar
								getStatus();

								// success Message-Box
								String successImage = "/freischaltungstool/resource/success.png";
								URL imgURLsuccess = getClass().getResource(successImage);
								BufferedImage biSuccess;
								try {
									biSuccess = ImageIO.read(imgURLsuccess);
									ImageIcon icon = new ImageIcon(biSuccess);
									JOptionPane.showMessageDialog(contentPane,
											chosenLanguage.get("infoSaveReleaseSuccess"),
											chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE, icon);
								} catch (IOException e1) {
									JOptionPane.showMessageDialog(contentPane,
											chosenLanguage.get("infoSaveReleaseSuccess"),
											chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
								}

							} else {
								JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoEmptyFields"),
										chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
							}
						} else {
							if (wasModified == false) {
								JOptionPane.showMessageDialog(contentPane,
										chosenLanguage.get("infoPackageAlreadyApproved"),
										chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
							} else {
								JOptionPane.showMessageDialog(contentPane,
										chosenLanguage.get("infoPackageAlreadyApprovedThroughAMS"),
										chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
							}
							wasModified = false;
						}

					} else {
						JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoEditNoPackageSelected"),
								chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		}
	}

	private class SaveEdit implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			resetCloseTimer();

			// the try-block is there to prove if a package is selected
			try {
				group.getSelection().getActionCommand();
				// prove if the text fields are not empty
				if (inputEditName.getText().trim().length() != 0 && inputEditGroup.getText().trim().length() != 0
						&& inputEditPCName.getText().trim().length() != 0) {
					if (checkPC.prove(inputEditPCName.getText()) == true
							|| checkBoxNsLookupEdit.isSelected() == false) {

						// creates a JOptionPane
						int result = JOptionPane.showOptionDialog(contentPane, chosenLanguage.get("dialogEditText"),
								chosenLanguage.get("dialogDeleteTitle"), JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.INFORMATION_MESSAGE, null,
								new String[] { chosenLanguage.get("dialogDeleteOk"),
										chosenLanguage.get("dialogDeleteCancel") },
								chosenLanguage.get("dialogDeleteOK"));
						// prove if the JOptionPane was checked with OK
						if (result == JOptionPane.OK_OPTION) {

							boolean repeat = true;
							while (repeat == true) {
								// prove if the AMS is editing the ini-file
								// right now
								if (amsLock.checkaccess() == true) {

									int result2 = JOptionPane.showOptionDialog(contentPane,
											chosenLanguage.get("infoBlockedByAMS"),
											chosenLanguage.get("dialogDeleteTitle"), JOptionPane.OK_CANCEL_OPTION,
											JOptionPane.INFORMATION_MESSAGE, null,
											new String[] { chosenLanguage.get("dialogAMSBlockRepeat"),
													chosenLanguage.get("dialogAMSBlockCancel") },
											chosenLanguage.get("dialogDeleteOK"));
									if (result2 != JOptionPane.OK_OPTION) {
										repeat = false;
									}
								} else {
									repeat = false;

									// prove if the ini-file was edited
									if (lastModified != getAlterationDate()) {
										proveIniOnModification(1);
									}

									// group.getSelection().getActionCommand()
									// gives
									// the package in brackets back, but the
									// method
									// contains does not match if there are
									// brackets
									// around the package name. (the brackets
									// are
									// needed to search for a package in the
									// ini-file)
									String currentPackage = "";
									currentPackage = group.getSelection().getActionCommand().replace("]", "");
									currentPackage = currentPackage.replace("[", "");
									// prove if the selected package is already
									// deleted through the AMS
									if (packagesForPC.contains(currentPackage)) {

										ArrayList<String> packagesOfNewPC = new ArrayList<>();
										iniFile.getPackageForPC(packagesOfNewPC, inputEditPCName.getText());

										// prove if the selected package is
										// already
										// approved for the new PC
										if (!packagesOfNewPC.contains(currentPackage)) {

											iniFile.transferEntry(inputEditName.getText(), inputEditGroup.getText(),
													inputEditComment.getText(), inputEditPCName.getText(),
													inputSearch.getText(), group.getSelection().getActionCommand());
											iniFile.getPackageForPC(packagesForPC, inputSearch.getText());

											log.logMe("Das Paket " + group.getSelection().getActionCommand()
													+ " wurde für den Rechner " + inputSearch.getText()
													+ " wurde auf den Rechner " + inputEditPCName.getText()
													+ " übertragen und der alte Eintrag auskommentiert");

											// get the new alteration date of
											// the
											// ini-file
											lastModified = getAlterationDate();

											// refresh statusbar
											getStatus();

											// success Message-Box
											String successImage = "/freischaltungstool/resource/success.png";
											URL imgURLsuccess = getClass().getResource(successImage);
											BufferedImage biSuccess;
											try {
												biSuccess = ImageIO.read(imgURLsuccess);
												ImageIcon icon = new ImageIcon(biSuccess);
												JOptionPane.showMessageDialog(contentPane,
														chosenLanguage.get("infoTransferSuccess"),
														chosenLanguage.get("infoTitle"),
														JOptionPane.INFORMATION_MESSAGE, icon);
											} catch (IOException e1) {
												JOptionPane.showMessageDialog(contentPane,
														chosenLanguage.get("infoTransferSuccess"),
														chosenLanguage.get("infoTitle"),
														JOptionPane.INFORMATION_MESSAGE);
											}

											createJRadioButtons();
											if (checkBoxEdit.isSelected() == false) {
												inputEditPCName.setText(null);
												inputEditName.setText(null);
												inputEditGroup.setText(null);
												inputEditComment.setText(null);
											}
										} else
											JOptionPane.showMessageDialog(contentPane,
													chosenLanguage.get("infoTransferPackageAlreadyApproved"),
													chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
									} else {
										JOptionPane.showMessageDialog(contentPane,
												chosenLanguage.get("infoAlreadyDeletedByAMS"),
												chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
										createJRadioButtons();
									}
								}
							}
						}

					} else {
						JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoPCNotFound"),
								chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoEmptyFields"),
							chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
				}

			} catch (NullPointerException e1) {
				JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoEditNoPackageSelected"),
						chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
			}
		}
	}

	private class Delete implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			resetCloseTimer();
			// the try-block is there to prove if a package is selected
			try {
				System.out.println(group.getSelection().getActionCommand());

				int result = JOptionPane.showOptionDialog(contentPane, chosenLanguage.get("dialogDeleteText"),
						chosenLanguage.get("dialogDeleteTitle"), JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.INFORMATION_MESSAGE, null,
						new String[] { chosenLanguage.get("dialogDeleteOk"), chosenLanguage.get("dialogDeleteCancel") },
						chosenLanguage.get("dialogDeleteOK"));
				if (result == JOptionPane.OK_OPTION) {

					boolean repeat = true;
					while (repeat == true) {

						// prove if the AMS is editing the ini-file right
						// now
						if (amsLock.checkaccess() == true) {

							int result2 = JOptionPane.showOptionDialog(contentPane,
									chosenLanguage.get("infoBlockedByAMS"), chosenLanguage.get("dialogDeleteTitle"),
									JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null,
									new String[] { chosenLanguage.get("dialogAMSBlockRepeat"),
											chosenLanguage.get("dialogAMSBlockCancel") },
									chosenLanguage.get("dialogDeleteOK"));
							if (result2 != JOptionPane.OK_OPTION) {
								repeat = false;
							}
						} else {
							repeat = false;

							// prove if the ini-file was edited
							if (lastModified != getAlterationDate()) {
								proveIniOnModification(2);
							}

							// group.getSelection().getActionCommand() gives the
							// package in brackets back, but the method contains
							// does not match if there are brackets around the
							// package name. (the brackets are needed to search
							// for
							// a package in the ini-file)
							String currentPackage = "";
							currentPackage = group.getSelection().getActionCommand().replace("]", "");
							currentPackage = currentPackage.replace("[", "");
							// prove if the selected package is already deleted
							// through the AMS
							if (packagesForPC.contains(currentPackage)) {

								iniFile.deleteEntry(inputSearch.getText(), group.getSelection().getActionCommand());
								log.logMe("Das Paket " + group.getSelection().getActionCommand()
										+ " wurde für den Rechner " + inputSearch.getText() + " gelöscht");
								iniFile.getPackageForPC(packagesForPC, inputSearch.getText());
								createJRadioButtons();

								// get the new alteration date of the ini-file
								lastModified = getAlterationDate();

								// refresh statusbar
								getStatus();

								// success Message-Box
								String successImage = "/freischaltungstool/resource/success.png";
								URL imgURLsuccess = getClass().getResource(successImage);
								BufferedImage biSuccess;
								try {
									biSuccess = ImageIO.read(imgURLsuccess);
									ImageIcon icon = new ImageIcon(biSuccess);
									JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoDeleteSuccess"),
											chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE, icon);

								} catch (IOException e1) {
									JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoDeleteSuccess"),
											chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);

								}

							} else {
								JOptionPane.showMessageDialog(contentPane,
										chosenLanguage.get("infoAlreadyDeletedByAMS"), chosenLanguage.get("infoTitle"),
										JOptionPane.INFORMATION_MESSAGE);
								createJRadioButtons();
							}
						}

					}

				}
			} catch (NullPointerException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(contentPane, chosenLanguage.get("infoDeleteNoPackageSelected"),
						chosenLanguage.get("infoTitle"), JOptionPane.INFORMATION_MESSAGE);
			}

		}
	}

	private class Back implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			resetCloseTimer();
			panelEdit.setVisible(false);
			panelSearch.add(labelCountdown);
			panelSearch.setVisible(true);
			inputSearch.requestFocus();
		}
	}
}
