/**
 * Copyright @ 2008 Quan Nguyen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.sourceforge.vietocr;

import java.io.*;
import javax.swing.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.util.prefs.Preferences;
import java.util.*;
import java.text.*;
import java.awt.event.*;
import javax.swing.undo.*;
import java.awt.dnd.DropTarget;
import javax.swing.event.*;
import net.sourceforge.vietocr.postprocessing.Processor;
import net.sourceforge.vietpad.*;
import net.sourceforge.vietpad.inputmethod.*;
import net.sourceforge.vietocr.wia.*;

/**
 *
 * @author  Quan Nguyen (nguyenq@users.sf.net)
 *
 */
public class Gui extends javax.swing.JFrame {

    private File imageFile;
    public static final String APP_NAME = "VietOCR";
    static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final Locale VIETNAM = new Locale("vi", "VN");
    protected final String UTF8 = "UTF-8";
    protected ResourceBundle myResources, bundle;
    protected static final Preferences prefs = Preferences.userRoot().node("/net/sourceforge/vietocr");
    private int filterIndex;
    private javax.swing.filechooser.FileFilter[] fileFilters;
    protected Font font;
    private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    private int imageIndex;
    private int imageTotal;
    private ArrayList<ImageIconScalable> imageList;
    private ArrayList<IIOImage> iioImageList;
    public final String EOL = System.getProperty("line.separator");
    private String currentDirectory;
    private String outputDirectory;
    protected String tessPath, dangAmbigsPath;
    private Properties prop, config;
    protected String curLangCode;
    protected String[] langCodes;
    private String[] langs;
    private ImageIconScalable imageIcon;
    private boolean reset;
    private JFileChooser filechooser;
    protected boolean wordWrapOn, dangAmbigsOn;
    private String selectedInputMethod;
    private float scaleX, scaleY;
    protected static String selectedUILang = "en";
    private int originalW, originalH;
    private final float ZOOM_FACTOR = 1.25f;

    /**
     * Creates new form Gui
     */
    public Gui() {
        File baseDir = Utilities.getBaseDir(this);
        if (WINDOWS) {
            tessPath = new File(baseDir, "tesseract").getPath();
        } else {
            tessPath = prefs.get("TesseractDirectory", new File(baseDir, "tesseract").getPath());
        }


        prop = new Properties();

        try {
            File tessdataDir = new File(tessPath, "tessdata");
            if (!tessdataDir.exists()) {
                String TESSDATA_PREFIX = System.getenv("TESSDATA_PREFIX");
                if (TESSDATA_PREFIX == null && !WINDOWS) { // if TESSDATA_PREFIX env var not set
                    TESSDATA_PREFIX = "/usr/local/share/"; // default path of tessdata on Linux after make install
                    tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                    if (!tessdataDir.exists()) {
                        TESSDATA_PREFIX = "/usr/share/tesseract-ocr/"; // other possible path of tessdata
                        tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                    }
                } else {
                    tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                }
            }
            
            langCodes = tessdataDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".inttemp");
                }
            });

            File xmlFile = new File(baseDir, "data/ISO639-3.xml");
            prop.loadFromXML(new FileInputStream(xmlFile));
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Missing ISO639-3.xml file. Cannot find it in " + new File(baseDir, "data").getPath() + " directory.", APP_NAME, JOptionPane.ERROR_MESSAGE);
            ioe.printStackTrace();
        } catch (Exception exc) {
            exc.printStackTrace();
        } finally {
            if (langCodes == null) {
                langs = new String[0];
            } else {
                langs = new String[langCodes.length];
            }
            for (int i = 0; i < langs.length; i++) {
                langCodes[i] = langCodes[i].replace(".inttemp", "");
                langs[i] = prop.getProperty(langCodes[i], langCodes[i]);
            }
        }

        selectedInputMethod = prefs.get("inputMethod", "Telex");
        config = new Properties();

        try {
            UIManager.setLookAndFeel(prefs.get("lookAndFeel", UIManager.getSystemLookAndFeelClassName()));
            config.loadFromXML(getClass().getResourceAsStream("config.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        // keep default LAF
        }

        initComponents();
        jLabelStatus.setVisible(false); // use jProgressBar instead for (more animation) task status

        // Hide Scan buttons for non-Windows OS because of WIA Automation
        if (!WINDOWS) {
            this.jToolBar2.remove(this.jButtonScan);
            this.jMenuFile.remove(this.jMenuItemScan);
        }

        new DropTarget(this.jImageLabel, new ImageDropTargetListener(this));

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        quit();
                    }

                    @Override
                    public void windowOpened(WindowEvent e) {
                        setExtendedState(prefs.getInt("windowState", Frame.NORMAL));
                    }
                });

        this.setTitle(APP_NAME);
        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui"); // NOI18N
        currentDirectory = prefs.get("currentDirectory", null);
        filechooser = new JFileChooser(currentDirectory);
        filechooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText"));
        javax.swing.filechooser.FileFilter tiffFilter = new SimpleFilter("tif;tiff", "TIFF");
        javax.swing.filechooser.FileFilter jpegFilter = new SimpleFilter("jpg;jpeg", "JPEG");
        javax.swing.filechooser.FileFilter gifFilter = new SimpleFilter("gif", "GIF");
        javax.swing.filechooser.FileFilter pngFilter = new SimpleFilter("png", "PNG");
        javax.swing.filechooser.FileFilter bmpFilter = new SimpleFilter("bmp", "Bitmap");
        javax.swing.filechooser.FileFilter allImageFilter = new SimpleFilter("tif;tiff;jpg;jpeg;gif;png;bmp", "All Image Files");

        filechooser.setAcceptAllFileFilterUsed(true);
        filechooser.addChoosableFileFilter(allImageFilter);
        filechooser.addChoosableFileFilter(tiffFilter);
        filechooser.addChoosableFileFilter(jpegFilter);
        filechooser.addChoosableFileFilter(gifFilter);
        filechooser.addChoosableFileFilter(pngFilter);
        filechooser.addChoosableFileFilter(bmpFilter);

        filterIndex = prefs.getInt("filterIndex", 0);
        fileFilters = filechooser.getChoosableFileFilters();
        filechooser.setFileFilter(fileFilters[filterIndex]);

        myResources = ResourceBundle.getBundle("net.sourceforge.vietpad.Resources");

        wordWrapOn = prefs.getBoolean("wordWrap", false);
        
        dangAmbigsPath = prefs.get("DangAmbigsPath", new File(baseDir, "data").getPath());
        dangAmbigsOn = prefs.getBoolean("dangAmbigs", false);

        jTextArea1.setLineWrap(wordWrapOn);
        jCheckBoxMenuWordWrap.setSelected(wordWrapOn);

        font = new Font(
                prefs.get("fontName", MAC_OS_X ? "Lucida Grande" : "Tahoma"),
                prefs.getInt("fontStyle", Font.PLAIN),
                prefs.getInt("fontSize", 12));
        jTextArea1.setFont(font);
        setSize(
                snap(prefs.getInt("frameWidth", 500), 300, screen.width),
                snap(prefs.getInt("frameHeight", 360), 150, screen.height));
        setLocation(
                snap(
                prefs.getInt("frameX", (screen.width - getWidth()) / 2),
                screen.x, screen.x + screen.width - getWidth()),
                snap(
                prefs.getInt("frameY", screen.y + (screen.height - getHeight()) / 3),
                screen.y, screen.y + screen.height - getHeight()));

        if (langCodes == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Tesseract_is_not_found._Please_specify_its_path_in_Settings_menu."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
        }

        populatePopupMenu();

        this.jTextArea1.getDocument().addUndoableEditListener(new RawListener());
        undoSupport.addUndoableEditListener(new SupportListener());
        m_undo.discardAllEdits();
        updateUndoRedo();
        updateCutCopyDelete(false);
    }

    /**
     * 
     */
    void populatePopupMenu() {
        popup.removeAll();
        
        m_undoAction = new AbstractAction(myResources.getString("Undo")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_undo.undo();
                } catch (CannotUndoException ex) {
                    System.err.println(bundle.getString("Unable_to_undo:_") + ex);
                }
                updateUndoRedo();
            }
        };


        popup.add(m_undoAction);

        m_redoAction = new AbstractAction(myResources.getString("Redo")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    m_undo.redo();
                } catch (CannotRedoException ex) {
                    System.err.println(bundle.getString("Unable_to_redo:_") + ex);
                }
                updateUndoRedo();
            }
        };


        popup.add(m_redoAction);
        popup.addSeparator();

        actionCut = new AbstractAction(myResources.getString("Cut")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.cut();
                updatePaste();
            }
        };

        popup.add(actionCut);

        actionCopy = new AbstractAction(myResources.getString("Copy")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.copy();
                updatePaste();
            }
        };


        popup.add(actionCopy);

        actionPaste = new AbstractAction(myResources.getString("Paste")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                undoSupport.beginUpdate();
                jTextArea1.paste();
                undoSupport.endUpdate();
            }
        };

        popup.add(actionPaste);

        actionDelete = new AbstractAction(myResources.getString("Delete")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.replaceSelection(null);
            }
        };

        popup.add(actionDelete);
        popup.addSeparator();

        actionSelectAll = new AbstractAction(myResources.getString("Select_All"), null) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.selectAll();
            }
        };

        popup.add(actionSelectAll);
    }

    /**
     *  Updates the Undo and Redo actions
     */
    private void updateUndoRedo() {
        m_undoAction.setEnabled(m_undo.canUndo());
        m_redoAction.setEnabled(m_undo.canRedo());
    }

    /**
     *  Updates the Cut, Copy, and Delete actions
     *
     *@param  isTextSelected  whether any text currently selected
     */
    private void updateCutCopyDelete(boolean isTextSelected) {
        actionCut.setEnabled(isTextSelected);
        actionCopy.setEnabled(isTextSelected);
        actionDelete.setEnabled(isTextSelected);
    }

    /**
     *  Listens to raw undoable edits
     *
     */
    private class RawListener implements UndoableEditListener {

        /**
         *  Description of the Method
         *
         *@param  e  Description of the Parameter
         */
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            undoSupport.postEdit(e.getEdit());
        }
    }

    /**
     *  Listens to undoable edits filtered by undoSupport
     *
     */
    private class SupportListener implements UndoableEditListener {

        /**
         *  Description of the Method
         *
         *@param  e  Description of the Parameter
         */
        @Override
        public void undoableEditHappened(UndoableEditEvent e) {
            m_undo.addEdit(e.getEdit());
            updateUndoRedo();
        }
    }

    /**
     *  Updates the Paste action
     */
    private void updatePaste() {
        try {
            Transferable clipData = clipboard.getContents(clipboard);
            if (clipData != null) {
                actionPaste.setEnabled(clipData.isDataFlavorSupported(DataFlavor.stringFlavor));
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, APP_NAME + myResources.getString("_has_run_out_of_memory.\nPlease_restart_") + APP_NAME + myResources.getString("_and_try_again."), myResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        popup = new javax.swing.JPopupMenu();
        jToolBar2 = new javax.swing.JToolBar();
        jButtonOpen = new javax.swing.JButton();
        jButtonScan = new javax.swing.JButton();
        jButtonOCR = new javax.swing.JButton();
        jButtonClear = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabelLanguage = new javax.swing.JLabel();
        jComboBoxLang = new JComboBox(langs);
        jComboBoxLang.setSelectedItem(prefs.get("langCode", null));
        if (langCodes != null && jComboBoxLang.getSelectedIndex() != -1) curLangCode = langCodes[jComboBoxLang.getSelectedIndex()];
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        VietKeyListener keyLst = new VietKeyListener(jTextArea1);
        jTextArea1.addKeyListener(keyLst);
        VietKeyListener.setInputMethod(InputMethods.valueOf(selectedInputMethod));
        VietKeyListener.setSmartMark(true);
        VietKeyListener.consumeRepeatKey(true);
        VietKeyListener.setVietModeEnabled("vie".equals(curLangCode));
        jTextArea1.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    popup.show(e.getComponent(), e.getX(), e.getY());
                }
            }

            public void mouseReleased(MouseEvent e) {
                mousePressed(e);
            }
        });
        jTextArea1.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                updateCutCopyDelete(e.getDot() != e.getMark());
            }
        });
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jScrollPane2.getVerticalScrollBar().setUnitIncrement(20);
        jScrollPane2.getHorizontalScrollBar().setUnitIncrement(20);
        jImageLabel = new JImageLabel();
        jLabelCurIndex = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jButtonPrevPage = new javax.swing.JButton();
        jButtonNextPage = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButtonFitImage = new javax.swing.JButton();
        jButtonActualSize = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jButtonZoomIn = new javax.swing.JButton();
        jButtonZoomOut = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jButtonRotateCCW = new javax.swing.JButton();
        jButtonRotateCW = new javax.swing.JButton();
        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        this.jProgressBar1.setVisible(false);
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuItemScan = new javax.swing.JMenuItem();
        jMenuItemSave = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuCommand = new javax.swing.JMenu();
        jMenuItemOCR = new javax.swing.JMenuItem();
        jMenuItemOCRAll = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        jMenuItemPostProcess = new javax.swing.JMenuItem();
        jMenuFormat = new javax.swing.JMenu();
        jCheckBoxMenuWordWrap = new javax.swing.JCheckBoxMenuItem();
        jMenuItemFont = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JSeparator();
        jMenuItemChangeCase = new javax.swing.JMenuItem();
        jMenuItemRemoveLineBreaks = new javax.swing.JMenuItem();
        jMenuSettings = new javax.swing.JMenu();
        jMenuInputMethod = new javax.swing.JMenu();
        ActionListener imlst = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                selectedInputMethod = ae.getActionCommand();
                VietKeyListener.setInputMethod(InputMethods.valueOf(selectedInputMethod));
            }
        };

        ButtonGroup groupInputMethod = new ButtonGroup();

        for (InputMethods im : InputMethods.values()) {
            String inputMethod = im.name();
            JRadioButtonMenuItem radioItem = new JRadioButtonMenuItem(inputMethod, selectedInputMethod.equals(inputMethod));
            radioItem.addActionListener(imlst);
            jMenuInputMethod.add(radioItem);
            groupInputMethod.add(radioItem);
        }

        jSeparator6 = new javax.swing.JSeparator();
        jMenuUILang = new javax.swing.JMenu();
        ButtonGroup group = new ButtonGroup();
        jRadioButtonMenuItemEng = new javax.swing.JRadioButtonMenuItem();
        jRadioButtonMenuItemViet = new javax.swing.JRadioButtonMenuItem();
        jMenuLookAndFeel = new javax.swing.JMenu();
        ActionListener lafLst = new ActionListener() {
            public void actionPerformed(ActionEvent ae) {
                updateLaF(ae.getActionCommand());
            }
        };

        ButtonGroup groupLookAndFeel = new ButtonGroup();
        UIManager.LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < lafs.length; i++) {
            JRadioButtonMenuItem lafButton = new JRadioButtonMenuItem(lafs[i].getName());
            lafButton.setActionCommand(lafs[i].getClassName());
            if (UIManager.getLookAndFeel().getClass().getName().equals(lafButton.getActionCommand())) {
                lafButton.setSelected(true);
            }
            lafButton.addActionListener(lafLst);
            groupLookAndFeel.add(lafButton);
            jMenuLookAndFeel.add(lafButton);
        }
        jSeparator3 = new javax.swing.JSeparator();
        jMenuItemOptions = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(800, 600));
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("net/sourceforge/vietocr/Gui"); // NOI18N
        jButtonOpen.setText(bundle.getString("jButtonOpen.Text")); // NOI18N
        jButtonOpen.setToolTipText(bundle.getString("jButtonOpen.ToolTipText")); // NOI18N
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonOpen);

        jButtonScan.setText(bundle.getString("jButtonScan.Text")); // NOI18N
        jButtonScan.setToolTipText(bundle.getString("jButtonScan.ToolTipText")); // NOI18N
        jButtonScan.setFocusable(false);
        jButtonScan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonScan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScanActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonScan);

        jButtonOCR.setText("OCR");
        jButtonOCR.setToolTipText(bundle.getString("jButtonOCR.ToolTipText")); // NOI18N
        jButtonOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOCRActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonOCR);

        jButtonClear.setText(bundle.getString("jButtonClear.Text")); // NOI18N
        jButtonClear.setToolTipText(bundle.getString("jButtonClear.ToolTipText")); // NOI18N
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonClear);

        jPanel2.setPreferredSize(new java.awt.Dimension(100, 10));
        jToolBar2.add(jPanel2);

        jLabelLanguage.setText(bundle.getString("jLabelLanguage.Text")); // NOI18N
        jToolBar2.add(jLabelLanguage);

        jComboBoxLang.setMaximumSize(new java.awt.Dimension(100, 32767));
        jComboBoxLang.setPreferredSize(new java.awt.Dimension(100, 20));
        jComboBoxLang.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBoxLangItemStateChanged(evt);
            }
        });
        jToolBar2.add(jComboBoxLang);

        getContentPane().add(jToolBar2, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerLocation(250);

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setWrapStyleWord(true);
        jTextArea1.setMargin(new java.awt.Insets(8, 8, 2, 2));
        jScrollPane1.setViewportView(jTextArea1);

        jSplitPane1.setRightComponent(jScrollPane1);

        jPanel1.setLayout(new java.awt.BorderLayout());

        jImageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jScrollPane2.setViewportView(jImageLabel);

        jPanel1.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        jLabelCurIndex.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jPanel1.add(jLabelCurIndex, java.awt.BorderLayout.NORTH);

        jToolBar1.setOrientation(1);

        jButtonPrevPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/PrevPage.gif"))); // NOI18N
        jButtonPrevPage.setToolTipText(bundle.getString("jButtonPrevPage.ToolTipText")); // NOI18N
        jButtonPrevPage.setEnabled(false);
        jButtonPrevPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPrevPageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonPrevPage);

        jButtonNextPage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/NextPage.gif"))); // NOI18N
        jButtonNextPage.setToolTipText(bundle.getString("jButtonNextPage.ToolTipText")); // NOI18N
        jButtonNextPage.setEnabled(false);
        jButtonNextPage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNextPageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonNextPage);
        jToolBar1.add(jSeparator7);

        jButtonFitImage.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/FitImage.gif"))); // NOI18N
        jButtonFitImage.setToolTipText(bundle.getString("jButtonFitImage.ToolTipText")); // NOI18N
        jButtonFitImage.setEnabled(false);
        jButtonFitImage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFitImageActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonFitImage);

        jButtonActualSize.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/ActualSize.gif"))); // NOI18N
        jButtonActualSize.setToolTipText(bundle.getString("jButtonActualSize.ToolTipText")); // NOI18N
        jButtonActualSize.setEnabled(false);
        jButtonActualSize.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActualSizeActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonActualSize);
        jToolBar1.add(jSeparator8);

        jButtonZoomIn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/ZoomIn.gif"))); // NOI18N
        jButtonZoomIn.setToolTipText(bundle.getString("jButtonZoomIn.ToolTipText")); // NOI18N
        jButtonZoomIn.setEnabled(false);
        jButtonZoomIn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZoomInActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonZoomIn);

        jButtonZoomOut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/ZoomOut.gif"))); // NOI18N
        jButtonZoomOut.setToolTipText(bundle.getString("jButtonZoomOut.ToolTipText")); // NOI18N
        jButtonZoomOut.setEnabled(false);
        jButtonZoomOut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonZoomOutActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonZoomOut);
        jToolBar1.add(jSeparator9);

        jButtonRotateCCW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/RotateCCW.gif"))); // NOI18N
        jButtonRotateCCW.setToolTipText(bundle.getString("jButtonRotateCCW.ToolTipText")); // NOI18N
        jButtonRotateCCW.setEnabled(false);
        jButtonRotateCCW.setFocusable(false);
        jButtonRotateCCW.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRotateCCW.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRotateCCW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRotateCCWActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonRotateCCW);

        jButtonRotateCW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/RotateCW.gif"))); // NOI18N
        jButtonRotateCW.setToolTipText(bundle.getString("jButtonRotateCW.ToolTipText")); // NOI18N
        jButtonRotateCW.setEnabled(false);
        jButtonRotateCW.setFocusable(false);
        jButtonRotateCW.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRotateCW.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRotateCW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRotateCWActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonRotateCW);

        jPanel1.add(jToolBar1, java.awt.BorderLayout.WEST);

        jSplitPane1.setLeftComponent(jPanel1);

        getContentPane().add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jPanelStatus.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        jPanelStatus.add(jLabelStatus);

        jProgressBar1.setStringPainted(true);
        jPanelStatus.add(jProgressBar1);

        getContentPane().add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        jMenuFile.setMnemonic('f');
        jMenuFile.setText(bundle.getString("jMenuFile.Text")); // NOI18N

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpen.setText(bundle.getString("jMenuItemOpen.Text")); // NOI18N
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuItemScan.setText(bundle.getString("jMenuItemScan.Text")); // NOI18N
        jMenuItemScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemScanActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemScan);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setText(bundle.getString("jMenuItemSave.Text")); // NOI18N
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);
        jMenuFile.add(jSeparator2);

        jMenuItemExit.setText(bundle.getString("jMenuItemExit.Text")); // NOI18N
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar2.add(jMenuFile);

        jMenuCommand.setMnemonic('c');
        jMenuCommand.setText(bundle.getString("jMenuCommand.Text")); // NOI18N

        jMenuItemOCR.setText(bundle.getString("jMenuItemOCR.Text")); // NOI18N
        jMenuItemOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOCRActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItemOCR);

        jMenuItemOCRAll.setText(bundle.getString("jMenuItemOCRAll.Text")); // NOI18N
        jMenuItemOCRAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOCRAllActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItemOCRAll);
        jMenuCommand.add(jSeparator1);

        jMenuItemPostProcess.setText(bundle.getString("jMenuItemPostProcess.Text")); // NOI18N
        jMenuItemPostProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPostProcessActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItemPostProcess);

        jMenuBar2.add(jMenuCommand);

        jMenuFormat.setText(bundle.getString("jMenuFormat.Text")); // NOI18N

        jCheckBoxMenuWordWrap.setText(bundle.getString("jCheckBoxMenuWordWrap.Text")); // NOI18N
        jCheckBoxMenuWordWrap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuWordWrapActionPerformed(evt);
            }
        });
        jMenuFormat.add(jCheckBoxMenuWordWrap);

        jMenuItemFont.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFont.setText(bundle.getString("jMenuItemFont.Text")); // NOI18N
        jMenuItemFont.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemFontActionPerformed(evt);
            }
        });
        jMenuFormat.add(jMenuItemFont);
        jMenuFormat.add(jSeparator10);

        jMenuItemChangeCase.setText(bundle.getString("jMenuItemChangeCase.Text")); // NOI18N
        jMenuItemChangeCase.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemChangeCaseActionPerformed(evt);
            }
        });
        jMenuFormat.add(jMenuItemChangeCase);

        jMenuItemRemoveLineBreaks.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRemoveLineBreaks.setText(bundle.getString("jMenuItemRemoveLineBreaks.Text")); // NOI18N
        jMenuItemRemoveLineBreaks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRemoveLineBreaksActionPerformed(evt);
            }
        });
        jMenuFormat.add(jMenuItemRemoveLineBreaks);

        jMenuBar2.add(jMenuFormat);

        jMenuSettings.setMnemonic('s');
        jMenuSettings.setText(bundle.getString("jMenuSettings.Text")); // NOI18N

        jMenuInputMethod.setText(bundle.getString("jMenuInputMethod.Text")); // NOI18N
        jMenuSettings.add(jMenuInputMethod);
        jMenuSettings.add(jSeparator6);

        jMenuUILang.setText(bundle.getString("jMenuUILang.Text")); // NOI18N

        group.add(jRadioButtonMenuItemEng);
        jRadioButtonMenuItemEng.setSelected(selectedUILang.equals("en"));
        jRadioButtonMenuItemEng.setText(bundle.getString("jRadioButtonMenuItemEng.Text")); // NOI18N
        jRadioButtonMenuItemEng.setActionCommand("en");
        jRadioButtonMenuItemEng.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemEngActionPerformed(evt);
            }
        });
        jMenuUILang.add(jRadioButtonMenuItemEng);

        group.add(jRadioButtonMenuItemViet);
        jRadioButtonMenuItemViet.setSelected(selectedUILang.equals("vi"));
        jRadioButtonMenuItemViet.setText(bundle.getString("jRadioButtonMenuItemViet.Text")); // NOI18N
        jRadioButtonMenuItemViet.setActionCommand("vi");
        jRadioButtonMenuItemViet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMenuItemVietActionPerformed(evt);
            }
        });
        jMenuUILang.add(jRadioButtonMenuItemViet);

        jMenuSettings.add(jMenuUILang);

        jMenuLookAndFeel.setText(bundle.getString("jMenuLookAndFeel.Text")); // NOI18N
        jMenuSettings.add(jMenuLookAndFeel);
        jMenuSettings.add(jSeparator3);

        jMenuItemOptions.setText(bundle.getString("jMenuItemOptions.Text")); // NOI18N
        jMenuItemOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOptionsActionPerformed(evt);
            }
        });
        jMenuSettings.add(jMenuItemOptions);

        jMenuBar2.add(jMenuSettings);

        jMenuHelp.setMnemonic('a');
        jMenuHelp.setText(bundle.getString("jMenuHelp.Text")); // NOI18N

        jMenuItemHelp.setText(APP_NAME + " " + bundle.getString("jMenuItemHelp.Text"));
        jMenuItemHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemHelpActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemHelp);
        jMenuHelp.add(jSeparator5);

        jMenuItemAbout.setText(bundle.getString("jMenuItemAbout.Text") + " " + APP_NAME);
        jMenuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemAboutActionPerformed(evt);
            }
        });
        jMenuHelp.add(jMenuItemAbout);

        jMenuBar2.add(jMenuHelp);

        setJMenuBar(jMenuBar2);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemHelpActionPerformed
        final String readme = bundle.getString("readme");
        if (MAC_OS_X && new File(readme).exists()) {
            try {
                Runtime.getRuntime().exec(new String[]{"open", "-a", "Help Viewer", readme});
            } catch (IOException x) {
                x.printStackTrace();
            }
        } else {
            if (helptopicsFrame == null) {
                helptopicsFrame = new JFrame(jMenuItemHelp.getText());
                helptopicsFrame.getContentPane().setLayout(new BorderLayout());
                HtmlPane helpPane = new HtmlPane(readme);
                helptopicsFrame.getContentPane().add(helpPane, BorderLayout.CENTER);
                helptopicsFrame.getContentPane().add(helpPane.getStatusBar(), BorderLayout.SOUTH);
                helptopicsFrame.pack();
                helptopicsFrame.setLocation((screen.width - helptopicsFrame.getWidth()) / 2, 40);
            }
            helptopicsFrame.setVisible(true);
        }
    }//GEN-LAST:event_jMenuItemHelpActionPerformed

    private void jComboBoxLangItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBoxLangItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            curLangCode = langCodes[jComboBoxLang.getSelectedIndex()];
            VietKeyListener.setVietModeEnabled(curLangCode.equals("vie"));
        }
    }//GEN-LAST:event_jComboBoxLangItemStateChanged

    private void jCheckBoxMenuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuWordWrapActionPerformed
        setLineWrap();
    }//GEN-LAST:event_jCheckBoxMenuWordWrapActionPerformed

    void setLineWrap() {
        // to be implemented in subclass
    }
    private void jMenuItemPostProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPostProcessActionPerformed
        if (curLangCode == null) {
            return;
        }

        try {
            String selectedText = this.jTextArea1.getSelectedText();
            if (selectedText != null) {
                selectedText = Processor.postProcess(selectedText, curLangCode, dangAmbigsPath, dangAmbigsOn);
                int start = this.jTextArea1.getSelectionStart();
                this.jTextArea1.replaceSelection(selectedText);
                this.jTextArea1.select(start, start + selectedText.length());
            } else {
                this.jTextArea1.setText(Processor.postProcess(jTextArea1.getText(), curLangCode, dangAmbigsPath, dangAmbigsOn));
            }
        } catch (UnsupportedOperationException uoe) {
            uoe.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format(bundle.getString("Post-processing_not_supported_for_%1$s_language."), prop.getProperty(uoe.getMessage())), APP_NAME, JOptionPane.ERROR_MESSAGE);
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItemPostProcessActionPerformed

    private void jButtonFitImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFitImageActionPerformed
        this.jButtonFitImage.setEnabled(false);
        this.jButtonActualSize.setEnabled(true);

        originalW = imageIcon.getIconWidth();
        originalH = imageIcon.getIconHeight();

        scaleX = (float) imageIcon.getIconWidth() / (float) this.jScrollPane2.getWidth();
        scaleY = (float) imageIcon.getIconHeight() / (float) this.jScrollPane2.getHeight();
        fitImageChange(this.jScrollPane2.getWidth(), this.jScrollPane2.getHeight());
        reset = true;
        ((JImageLabel) jImageLabel).deselect();
    }//GEN-LAST:event_jButtonFitImageActionPerformed

    void fitImageChange(final int width, final int height) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (ImageIconScalable tempImageIcon : imageList) {
                    tempImageIcon.setScaledSize(width, height);
                }
                imageIcon = imageList.get(imageIndex);
                jImageLabel.revalidate();
                jScrollPane2.repaint();
            }
        });
    }

    private void jButtonZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomOutActionPerformed
        doChange(false);
        reset = false;
        ((JImageLabel) jImageLabel).deselect();
    }//GEN-LAST:event_jButtonZoomOutActionPerformed

    private void jButtonZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomInActionPerformed
        doChange(true);
        reset = false;
        ((JImageLabel) jImageLabel).deselect();
    }//GEN-LAST:event_jButtonZoomInActionPerformed

    void doChange(final boolean isZoomIn) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                for (ImageIconScalable tempImageIcon : imageList) {
                    int width = tempImageIcon.getIconWidth();
                    int height = tempImageIcon.getIconHeight();

                    if (isZoomIn) {
                        tempImageIcon.setScaledSize((int) (width * ZOOM_FACTOR), (int) (height * ZOOM_FACTOR));
                    } else {
                        tempImageIcon.setScaledSize((int) (width / ZOOM_FACTOR), (int) (height / ZOOM_FACTOR));
                    }
                }
                imageIcon = imageList.get(imageIndex);
                jImageLabel.revalidate();
                jScrollPane2.repaint();

                if (isZoomIn) {
                    scaleX /= ZOOM_FACTOR;
                    scaleY /= ZOOM_FACTOR;
                } else {
                    scaleX *= ZOOM_FACTOR;
                    scaleY *= ZOOM_FACTOR;
                }
            }
        });
    }
    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        jMenuItemOpenActionPerformed(evt);
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jButtonOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOCRActionPerformed
        jMenuItemOCRActionPerformed(evt);
    }//GEN-LAST:event_jButtonOCRActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        this.jTextArea1.setText(null);
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jButtonPrevPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevPageActionPerformed
        imageIndex--;
        if (imageIndex < 0) {
            imageIndex = 0;
        } else {
            this.jLabelStatus.setText(null);
            jProgressBar1.setString(null);
            jProgressBar1.setVisible(false);
            displayImage();
        }
        setButton();
        ((JImageLabel) jImageLabel).deselect();
}//GEN-LAST:event_jButtonPrevPageActionPerformed

    private void jButtonNextPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextPageActionPerformed
        imageIndex++;
        if (imageIndex > imageTotal - 1) {
            imageIndex = imageTotal - 1;
        } else {
            this.jLabelStatus.setText(null);
            jProgressBar1.setString(null);
            jProgressBar1.setVisible(false);
            displayImage();
        }
        setButton();
        ((JImageLabel) jImageLabel).deselect();
}//GEN-LAST:event_jButtonNextPageActionPerformed

    private void jMenuItemOCRAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOCRAllActionPerformed
        if (this.jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        performOCR(iioImageList, -1);
    }//GEN-LAST:event_jMenuItemOCRAllActionPerformed

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        try {
            String version = config.getProperty("Version");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date releaseDate = sdf.parse(config.getProperty("ReleaseDate"));

            JOptionPane.showMessageDialog(this, APP_NAME + ", " + version + " \u00a9 2007\n" +
                    "Java GUI Frontend for Tesseract OCR Engine\n" +
                    DateFormat.getDateInstance(DateFormat.LONG).format(releaseDate) +
                    "\nhttp://vietocr.sourceforge.net", APP_NAME, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed
    void quit() {
        prefs.put("UILanguage", selectedUILang);

        if (currentDirectory != null) {
            prefs.put("currentDirectory", currentDirectory);
        }
        if (outputDirectory != null) {
            prefs.put("outputDirectory", outputDirectory);
        }

        if (!WINDOWS) {
            prefs.put("TesseractDirectory", tessPath);
        }
        
        prefs.put("DangAmbigsPath", dangAmbigsPath);
        prefs.put("inputMethod", selectedInputMethod);
        prefs.put("lookAndFeel", UIManager.getLookAndFeel().getClass().getName());
        prefs.put("fontName", font.getName());
        prefs.putInt("fontSize", font.getSize());
        prefs.putInt("fontStyle", font.getStyle());
        prefs.put("lookAndFeel", UIManager.getLookAndFeel().getClass().getName());
        prefs.putInt("windowState", getExtendedState());
        if (this.jComboBoxLang.getSelectedIndex() != -1) {
            prefs.put("langCode", this.jComboBoxLang.getSelectedItem().toString());
        }

        prefs.putBoolean("wordWrap", wordWrapOn);
        prefs.putBoolean("dangAmbigs", dangAmbigsOn);

        if (getExtendedState() == NORMAL) {
            prefs.putInt("frameHeight", getHeight());
            prefs.putInt("frameWidth", getWidth());
            prefs.putInt("frameX", getX());
            prefs.putInt("frameY", getY());
        }

        prefs.putInt("filterIndex", filterIndex);

//        System.exit(0);
    }
    private void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFontActionPerformed
        openFontDialog(curLangCode);
    }//GEN-LAST:event_jMenuItemFontActionPerformed

    void openFontDialog(String langCode) {
        // to be implemented in subclass
    }

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        outputDirectory = prefs.get("outputDirectory", null);
        JFileChooser chooser = new JFileChooser(outputDirectory);
        javax.swing.filechooser.FileFilter txtFilter = new SimpleFilter("txt", "Unicode UTF-8 Text");
        chooser.addChoosableFileFilter(txtFilter);

        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            outputDirectory = chooser.getCurrentDirectory().getPath();
            File textFile = chooser.getSelectedFile();
            if (chooser.getFileFilter() == txtFilter) {
                if (!textFile.getName().endsWith(".txt")) {
                    textFile = new File(textFile.getPath() + ".txt");
                }
            }
            saveFile(textFile);
        }

    }

    void saveFile(File file) {
        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), UTF8));
            jTextArea1.write(out);
            out.close();
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, APP_NAME + myResources.getString("_has_run_out_of_memory.\nPlease_restart_") + APP_NAME + myResources.getString("_and_try_again."), myResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            showError(fnfe, myResources.getString("Error_saving_file_") + file + myResources.getString(".\nFile_is_inaccessible."));
        } catch (Exception ex) {
            showError(ex, myResources.getString("Error_saving_file_") + file);
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            });
        }

    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOCRActionPerformed
        if (jImageLabel.getIcon() == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_load_an_image."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Rectangle rect = ((JImageLabel) jImageLabel).getRect();

        if (rect != null) {
            try {
                ImageIcon ii = (ImageIcon) this.jImageLabel.getIcon();
                BufferedImage bi = ((BufferedImage) ii.getImage()).getSubimage((int) (rect.x * scaleX), (int) (rect.y * scaleY), (int) (rect.width * scaleX), (int) (rect.height * scaleY));
                IIOImage iioImage = new IIOImage(bi, null, null);
                ArrayList<IIOImage> tempList = new ArrayList<IIOImage>();
                tempList.add(iioImage);
                performOCR(tempList, 0);
            } catch (RasterFormatException rfe) {
                rfe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            performOCR(iioImageList, imageIndex);
        }
    }//GEN-LAST:event_jMenuItemOCRActionPerformed

    /**
     * Perform OCR on images represented by IIOImage.
     * 
     * @param list List of IIOImage
     * @param index Index of page to be OCRed: -1 for all pages
     */
    void performOCR(final ArrayList<IIOImage> list, final int index) {
        if (this.jComboBoxLang.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, bundle.getString("Please_select_a_language."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        jLabelStatus.setText(bundle.getString("OCR_running..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("OCR_running..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
        this.jMenuItemOCRAll.setEnabled(false);

        SwingWorker worker = new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {
                OCR ocrEngine = new OCR(tessPath);
                return ocrEngine.recognizeText(list, index, curLangCode);
            }

            @Override
            protected void done() {
                try {
                    String result = get();
                    jTextArea1.append(result);
                } catch (InterruptedException ignore) {
                    ignore.printStackTrace();
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        if (cause instanceof IOException) {
                            why = bundle.getString("Cannot_find_Tesseract._Please_set_its_path.");
                        } else if (cause instanceof FileNotFoundException) {
                            why = bundle.getString("An_exception_occurred_in_Tesseract_engine_while_recognizing_this_image.");
                        } else if (cause instanceof OutOfMemoryError) {
                            why = bundle.getString("_has_run_out_of_memory.\nPlease_restart_");
                        } else {
                            why = cause.getMessage();
                        }
                    } else {
                        why = e.getMessage();
                    }
                    e.printStackTrace();
//                    System.err.println(why);
                    JOptionPane.showMessageDialog(null, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
                } finally {
                    jLabelStatus.setText(bundle.getString("OCR_completed."));
                    jProgressBar1.setIndeterminate(false);
                    jProgressBar1.setString(bundle.getString("OCR_completed."));
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                    jButtonOCR.setEnabled(true);
                    jMenuItemOCR.setEnabled(true);
                    jMenuItemOCRAll.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed

        int returnVal = filechooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            currentDirectory = filechooser.getCurrentDirectory().getPath();
            openFile(filechooser.getSelectedFile());
            scaleX = scaleY = 1f;

            for (int i = 0; i < fileFilters.length; i++) {
                if (fileFilters[i] == filechooser.getFileFilter()) {
                    filterIndex = i;
                    break;
                }
            }
        }
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    /**
     *  Updates UI component if changes in LAF
     *
     *@param  laf  the look and feel class name
     */
    protected void updateLaF(String laf) {
        try {
            UIManager.setLookAndFeel(laf);
        } catch (Exception exc) {
            // do nothing
            exc.printStackTrace();
        }

        for (Window win : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(win);
            win.pack();
        }

        SwingUtilities.updateComponentTreeUI(popup);
        SwingUtilities.updateComponentTreeUI(filechooser);
    }

    /**
     * Opens image file.
     *
     */
    public void openFile(File selectedFile) {
        imageFile = selectedFile;
        iioImageList = ImageIOHelper.getIIOImageList(imageFile);
        imageList = ImageIOHelper.getImageList(iioImageList);

        if (imageList == null) {
            JOptionPane.showMessageDialog(null, bundle.getString("Cannotloadimage"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        imageTotal = imageList.size();
        imageIndex = 0;

        displayImage();

//        originalW = imageIcon.getIconWidth();
//        originalH = imageIcon.getIconHeight();

        this.setTitle(imageFile.getName() + " - " + APP_NAME);
        jLabelStatus.setText(null);
        jProgressBar1.setString(null);
        jProgressBar1.setVisible(false);
        ((JImageLabel) jImageLabel).deselect();

        this.jButtonFitImage.setEnabled(true);
        this.jButtonZoomIn.setEnabled(true);
        this.jButtonZoomOut.setEnabled(true);

        if (imageList.size() == 1) {
            this.jButtonNextPage.setEnabled(false);
            this.jButtonPrevPage.setEnabled(false);
        } else {
            this.jButtonNextPage.setEnabled(true);
            this.jButtonPrevPage.setEnabled(true);
        }

        this.jButtonRotateCCW.setEnabled(true);
        this.jButtonRotateCW.setEnabled(true);

        setButton();
    }

    void displayImage() {
        this.jLabelCurIndex.setText(bundle.getString("Page_") + (imageIndex + 1) + " " + bundle.getString("of_") + imageTotal);
        imageIcon = imageList.get(imageIndex);

        jImageLabel.setIcon(imageIcon);
        jImageLabel.revalidate();
    }

    void setButton() {
        if (imageIndex == 0) {
            this.jButtonPrevPage.setEnabled(false);
        } else {
            this.jButtonPrevPage.setEnabled(true);
        }

        if (imageIndex == imageList.size() - 1) {
            this.jButtonNextPage.setEnabled(false);
        } else {
            this.jButtonNextPage.setEnabled(true);
        }
    }

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jSplitPane1.setDividerLocation(jSplitPane1.getWidth() / 2);

        if (reset && imageFile != null) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jButtonFitImageActionPerformed(null);
                }
            });
        }
    }//GEN-LAST:event_formComponentResized

private void jRadioButtonMenuItemEngActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemEngActionPerformed
    if (!selectedUILang.equals(evt.getActionCommand())) {
        selectedUILang = evt.getActionCommand();
        changeUILanguage(selectedUILang.equals("vi") ? VIETNAM : Locale.US);
    }
}//GEN-LAST:event_jRadioButtonMenuItemEngActionPerformed

private void jRadioButtonMenuItemVietActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMenuItemVietActionPerformed
    jRadioButtonMenuItemEngActionPerformed(evt);
}//GEN-LAST:event_jRadioButtonMenuItemVietActionPerformed

private void jMenuItemScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemScanActionPerformed
    performScan();
}//GEN-LAST:event_jMenuItemScanActionPerformed

    /**
     * Access scanner and scan documents via WIA.
     *
     */
    void performScan() {
        jLabelStatus.setText(bundle.getString("Scanning..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("Scanning..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        jMenuItemScan.setEnabled(false);
        jButtonScan.setEnabled(false);

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    WiaScannerAdapter adapter = new WiaScannerAdapter();
                    File tempImageFile = File.createTempFile("tempfile", ".bmp");

                    if (tempImageFile.exists()) {
                        tempImageFile.delete();
                    }

                    tempImageFile = adapter.ScanImage(FormatID.wiaFormatBMP, tempImageFile.getCanonicalPath());
                    openFile(tempImageFile);
                } catch (IOException ioe) {
                    JOptionPane.showMessageDialog(null, ioe.getMessage(), "I/O Error", JOptionPane.ERROR_MESSAGE);
                } catch (WiaOperationException woe) {
                    JOptionPane.showMessageDialog(null, woe.getWIAMessage(), woe.getMessage(), JOptionPane.WARNING_MESSAGE);
                } catch (Exception e) {
                    String msg = e.getMessage();
                    if (msg == null || msg.equals("")) {
                        msg = "Scanner Operation Error.";
                    }
                    JOptionPane.showMessageDialog(null, msg, "Scanner Operation Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    jLabelStatus.setText(bundle.getString("Scanning_completed"));
                    jProgressBar1.setIndeterminate(false);
                    jProgressBar1.setString(bundle.getString("Scanning_completed"));
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                    jMenuItemScan.setEnabled(true);
                    jButtonScan.setEnabled(true);
                }
            }
        });
    }

private void jButtonScanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonScanActionPerformed
    jMenuItemScanActionPerformed(evt);
}//GEN-LAST:event_jButtonScanActionPerformed

private void jButtonRotateCCWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRotateCCWActionPerformed
    rotateImage(270);
}//GEN-LAST:event_jButtonRotateCCWActionPerformed

private void jButtonRotateCWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRotateCWActionPerformed
    rotateImage(90);
}//GEN-LAST:event_jButtonRotateCWActionPerformed

private void jButtonActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualSizeActionPerformed
    this.jButtonFitImage.setEnabled(true);
    this.jButtonActualSize.setEnabled(false);

    fitImageChange(originalW, originalH);
    scaleX = scaleY = 1f;

    reset = true;
    ((JImageLabel) jImageLabel).deselect();
}//GEN-LAST:event_jButtonActualSizeActionPerformed

private void jMenuItemOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOptionsActionPerformed
    openOptionsDialog();
}//GEN-LAST:event_jMenuItemOptionsActionPerformed

    void openOptionsDialog() {
        // to be implemented in subclass
    }

private void jMenuItemChangeCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeCaseActionPerformed
    openChangeCaseDialog();
}//GEN-LAST:event_jMenuItemChangeCaseActionPerformed

    void openChangeCaseDialog() {
        // to be implemented in subclass
    }
private void jMenuItemRemoveLineBreaksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveLineBreaksActionPerformed
    removeLineBreaks();
}//GEN-LAST:event_jMenuItemRemoveLineBreaksActionPerformed

    void removeLineBreaks() {
        // to be implemented in subclass
    }

    void rotateImage(int angle) {
        try {
            imageIcon = imageIcon.getRotatedImageIcon(Math.toRadians(angle));
            jImageLabel.setIcon(imageIcon);
            imageList.set(imageIndex, imageIcon);
            iioImageList.get(imageIndex).setRenderedImage((BufferedImage) imageIcon.getImage());
            ((JImageLabel) jImageLabel).deselect();
        } catch (OutOfMemoryError oome) {
            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, oome.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    void changeUILanguage(final Locale locale) {
        if (locale.equals(Locale.getDefault())) {
            return; // no change in locale
        }
        Locale.setDefault(locale);
        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui");
        myResources = ResourceBundle.getBundle("net.sourceforge.vietpad.Resources");

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                FormLocalizer localizer = new FormLocalizer(Gui.this, Gui.class);
                localizer.ApplyCulture(bundle);
                if (imageTotal > 0) {
                    jLabelCurIndex.setText(bundle.getString("Page_") + (imageIndex + 1) + " " + bundle.getString("of_") + imageTotal);
                }
                jMenuItemHelp.setText(APP_NAME + " " + jMenuItemHelp.getText());
                jMenuItemAbout.setText(jMenuItemAbout.getText() + " " + APP_NAME);
                if (helptopicsFrame != null) {
                    helptopicsFrame.setTitle(jMenuItemHelp.getText());
                }
                filechooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText"));
                populatePopupMenu();
            }
        });
    }

    /**
     *  Shows a warning message
     *
     *@param  e        the exception to warn about
     *@param  message  the message to display
     */
    public void showError(Exception e, String message) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, message, APP_NAME, JOptionPane.WARNING_MESSAGE);
    }

    private int snap(final int ideal, final int min, final int max) {
        final int TOLERANCE = 0;
        return ideal < min + TOLERANCE ? min : (ideal > max - TOLERANCE ? max : ideal);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        selectedUILang = prefs.get("UILanguage", "en");
        Locale.setDefault(selectedUILang.equals("vi") ? VIETNAM : Locale.US);

        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new Gui().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActualSize;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonFitImage;
    private javax.swing.JButton jButtonNextPage;
    private javax.swing.JButton jButtonOCR;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonPrevPage;
    private javax.swing.JButton jButtonRotateCCW;
    private javax.swing.JButton jButtonRotateCW;
    private javax.swing.JButton jButtonScan;
    private javax.swing.JButton jButtonZoomIn;
    private javax.swing.JButton jButtonZoomOut;
    protected javax.swing.JCheckBoxMenuItem jCheckBoxMenuWordWrap;
    private javax.swing.JComboBox jComboBoxLang;
    private javax.swing.JLabel jImageLabel;
    private javax.swing.JLabel jLabelCurIndex;
    private javax.swing.JLabel jLabelLanguage;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu jMenuCommand;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFormat;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenu jMenuInputMethod;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemChangeCase;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFont;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemOCR;
    private javax.swing.JMenuItem jMenuItemOCRAll;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemOptions;
    private javax.swing.JMenuItem jMenuItemPostProcess;
    private javax.swing.JMenuItem jMenuItemRemoveLineBreaks;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemScan;
    private javax.swing.JMenu jMenuLookAndFeel;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenu jMenuUILang;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemEng;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemViet;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    protected javax.swing.JTextArea jTextArea1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPopupMenu popup;
    // End of variables declaration//GEN-END:variables
    private final UndoManager m_undo = new UndoManager();
    protected final UndoableEditSupport undoSupport = new UndoableEditSupport();
    private Action m_undoAction,  m_redoAction,  actionCut,  actionCopy,  actionPaste,  actionDelete,  actionSelectAll;
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private JFrame helptopicsFrame;
}


