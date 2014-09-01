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
import java.util.List;
import java.text.*;
import java.awt.event.*;
import javax.swing.undo.*;
import java.awt.dnd.DropTarget;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import net.sourceforge.vietpad.*;
import net.sourceforge.vietpad.inputmethod.*;
import net.sourceforge.vietocr.wia.*;
//import uk.org.jsane.JSane_Base.JSane_Base_Frame;
//import uk.org.jsane.JSane_Gui.Swing.JSane_Scan_Dialog;

public class Gui extends javax.swing.JFrame {

    public static final String APP_NAME = "VietOCR";
    public static final String TO_BE_IMPLEMENTED = "To be implemented in subclass";
    static final boolean MAC_OS_X = System.getProperty("os.name").startsWith("Mac");
    static final boolean WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
    static final Locale VIETNAM = new Locale("vi", "VN");
    static final String UTF8 = "UTF-8";
    ResourceBundle vietpadResources, bundle;
    static final Preferences prefs = Preferences.userRoot().node("/net/sourceforge/vietocr");
    private int filterIndex;
    private FileFilter[] fileFilters;
    protected Font font;
    private final Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
    protected int imageIndex;
    private int imageTotal;
    private List<ImageIconScalable> imageList;
    protected List<IIOImage> iioImageList;
    public final String EOL = System.getProperty("line.separator");
    private String currentDirectory;
    private String outputDirectory;
    protected String tessPath, dangAmbigsPath;
    private Properties prop, config;
    protected String curLangCode = "eng";
    protected String[] langCodes;
    private String[] langs;
    private ImageIconScalable imageIcon;
    private boolean isFitImageSelected;
    private JFileChooser filechooser;
    protected boolean wordWrapOn, dangAmbigsOn;
    private String selectedInputMethod;
    protected float scaleX = 1f;
    protected float scaleY = 1f;
    protected static String selectedUILang = "en";
    private int originalW, originalH;
    private final float ZOOM_FACTOR = 1.25f;
    private Point curScrollPos;
    private File textFile;
    private java.util.List<String> mruList = new java.util.ArrayList<String>();
    private String strClearRecentFiles;
    private boolean textChanged = true;
    private RawListener rawListener;
    private final String DATAFILE_SUFFIX = ".inttemp";

    /**
     * Creates new form Gui
     */
    public Gui() {
        File baseDir = Utilities.getBaseDir(Gui.this);
        if (WINDOWS) {
            tessPath = new File(baseDir, "tesseract").getPath();
        } else {
            tessPath = prefs.get("TesseractDirectory", "/usr/bin");
        }

        prop = new Properties();

        try {
            File tessdataDir = new File(tessPath, "tessdata");
            if (!tessdataDir.exists()) {
                String TESSDATA_PREFIX = System.getenv("TESSDATA_PREFIX");
                if (TESSDATA_PREFIX == null && !WINDOWS) { // if TESSDATA_PREFIX env var not set
                    TESSDATA_PREFIX = "/usr/share/tesseract-ocr/"; // default install path of tessdata on Linux
                    tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                    if (!tessdataDir.exists()) {
                        TESSDATA_PREFIX = "/usr/local/share/"; // default make install path of tessdata on Linux
                        tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                    }
                } else {
                    tessdataDir = new File(TESSDATA_PREFIX, "tessdata");
                }
            }

            langCodes = tessdataDir.list(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(DATAFILE_SUFFIX);
                }
            });

            File xmlFile = new File(baseDir, "data/ISO639-3.xml");
            prop.loadFromXML(new FileInputStream(xmlFile));
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(null, "Missing ISO639-3.xml file. Cannot find it in " + new File(baseDir, "data").getPath() + " directory.", APP_NAME, JOptionPane.ERROR_MESSAGE);
//            ioe.printStackTrace();
        } catch (Exception exc) {
//            exc.printStackTrace();
        } finally {
            if (langCodes == null) {
                langs = new String[0];
            } else {
                langs = new String[langCodes.length];
            }
            for (int i = 0; i < langs.length; i++) {
                langCodes[i] = langCodes[i].replace(DATAFILE_SUFFIX, "");
                langs[i] = prop.getProperty(langCodes[i], langCodes[i]);
            }
        }

        selectedInputMethod = prefs.get("inputMethod", "Telex");
        config = new Properties();

        try {
            UIManager.setLookAndFeel(prefs.get("lookAndFeel", UIManager.getSystemLookAndFeelClassName()));
            config.loadFromXML(getClass().getResourceAsStream("config.xml"));
        } catch (Exception e) {
//            e.printStackTrace();
            // keep default LAF
        }

        initComponents();
        jLabelStatus.setVisible(false); // use jProgressBar instead for (more animation) task status

        // Hide Scan buttons for non-Windows OS because of WIA Automation
        if (!WINDOWS) {
            this.jToolBar2.remove(this.jButtonScan);
            this.jMenuFile.remove(this.jMenuItemScan);
        }

        new DropTarget(this.jImageLabel, new FileDropTargetListener(Gui.this));
        new DropTarget(this.jTextArea1, new FileDropTargetListener(Gui.this));

        addWindowListener(
                new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent e) {
                        quit();
                    }

                    @Override
                    public void windowOpened(WindowEvent e) {
                        updateSave(false);
                        setExtendedState(prefs.getInt("windowState", Frame.NORMAL));
                    }
                });

        this.setTitle(APP_NAME);
        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui"); // NOI18N
        currentDirectory = prefs.get("currentDirectory", null);
        filechooser = new JFileChooser(currentDirectory);
        filechooser.setDialogTitle(bundle.getString("jButtonOpen.ToolTipText"));
        FileFilter allImageFilter = new SimpleFilter("bmp;gif;jpg;jpeg;jp2;png;pnm;pbm;pgm;ppm;tif;tiff;pdf", "All Image Files");
        FileFilter bmpFilter = new SimpleFilter("bmp", "Bitmap");
        FileFilter gifFilter = new SimpleFilter("gif", "GIF");
        FileFilter jpegFilter = new SimpleFilter("jpg;jpeg", "JPEG");
        FileFilter jpeg2000Filter = new SimpleFilter("jp2", "JPEG 2000");
        FileFilter pngFilter = new SimpleFilter("png", "PNG");
        FileFilter pnmFilter = new SimpleFilter("pnm;pbm;pgm;ppm", "PNM");
        FileFilter tiffFilter = new SimpleFilter("tif;tiff", "TIFF");

        FileFilter pdfFilter = new SimpleFilter("pdf", "PDF");
        FileFilter textFilter = new SimpleFilter("txt", "UTF-8 Text");

        filechooser.setAcceptAllFileFilterUsed(false);
        filechooser.addChoosableFileFilter(allImageFilter);
        filechooser.addChoosableFileFilter(bmpFilter);
        filechooser.addChoosableFileFilter(gifFilter);
        filechooser.addChoosableFileFilter(jpegFilter);
        filechooser.addChoosableFileFilter(jpeg2000Filter);
        filechooser.addChoosableFileFilter(pngFilter);
        filechooser.addChoosableFileFilter(pnmFilter);
        filechooser.addChoosableFileFilter(tiffFilter);
        filechooser.addChoosableFileFilter(pdfFilter);
        filechooser.addChoosableFileFilter(textFilter);

        filterIndex = prefs.getInt("filterIndex", 0);
        fileFilters = filechooser.getChoosableFileFilters();
        if (filterIndex < fileFilters.length) {
            filechooser.setFileFilter(fileFilters[filterIndex]);
        }
        vietpadResources = ResourceBundle.getBundle("net.sourceforge.vietpad.Resources");

        wordWrapOn = prefs.getBoolean("wordWrap", false);

        dangAmbigsPath = prefs.get("DangAmbigsPath", new File(baseDir, "data").getPath());
        dangAmbigsOn = prefs.getBoolean("dangAmbigs", true);

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
            JOptionPane.showMessageDialog(Gui.this, bundle.getString("Tesseract_is_not_found._Please_specify_its_path_in_Settings_menu."), APP_NAME, JOptionPane.INFORMATION_MESSAGE);
        }

        populatePopupMenu();
        rawListener = new RawListener();
        this.jTextArea1.getDocument().addUndoableEditListener(rawListener);
        undoSupport.addUndoableEditListener(new SupportListener());
        m_undo.discardAllEdits();
        updateUndoRedo();
        updateCutCopyDelete(false);

        // Populate MRU List
        String[] fileNames = prefs.get("MruList", "").split(File.pathSeparator);
        for (String fileName : fileNames) {
            if (!fileName.equals("")) {
                mruList.add(fileName);
            }
        }
        updateMRUMenu();

        // Paste image from clipboard
        KeyEventDispatcher dispatcher = new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V && e.getID() == KeyEvent.KEY_PRESSED) {
                    try {
                        Image image = ImageHelper.getClipboardImage();
                        if (image != null) {
                            File tempFile = File.createTempFile("tmp", ".png");
                            ImageIO.write((BufferedImage) image, "png", tempFile);
                            openFile(tempFile);
                            tempFile.deleteOnExit();
                            e.consume();
//                            return true; // not dispatch the event to the component, in this case, the textarea
                        }
                    } catch (Exception ex) {
                    }
                }
                return false;
            }
        };

        DefaultKeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(dispatcher);
    }

    /**
     * 
     */
    private void populatePopupMenu() {
        popup.removeAll();

        m_undoAction = new AbstractAction(vietpadResources.getString("Undo")) {

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

        m_redoAction = new AbstractAction(vietpadResources.getString("Redo")) {

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

        actionCut = new AbstractAction(vietpadResources.getString("Cut")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.cut();
                updatePaste();
            }
        };

        popup.add(actionCut);

        actionCopy = new AbstractAction(vietpadResources.getString("Copy")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.copy();
                updatePaste();
            }
        };


        popup.add(actionCopy);

        actionPaste = new AbstractAction(vietpadResources.getString("Paste")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                undoSupport.beginUpdate();
                jTextArea1.paste();
                undoSupport.endUpdate();
            }
        };

        popup.add(actionPaste);

        actionDelete = new AbstractAction(vietpadResources.getString("Delete")) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.replaceSelection(null);
            }
        };

        popup.add(actionDelete);
        popup.addSeparator();

        actionSelectAll = new AbstractAction(vietpadResources.getString("Select_All"), null) {

            @Override
            public void actionPerformed(ActionEvent e) {
                jTextArea1.selectAll();
            }
        };

        popup.add(actionSelectAll);
    }

    /**
     * Update MRU Submenu.
     */
    private void updateMRUMenu() {
        this.jMenuRecentFiles.removeAll();

        if (mruList.isEmpty()) {
            this.jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
        } else {
            Action mruAction = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    JMenuItem item = (JMenuItem) e.getSource();
                    String fileName = item.getText();

                    if (fileName.equals(strClearRecentFiles)) {
                        mruList.clear();
                        jMenuRecentFiles.removeAll();
                        jMenuRecentFiles.add(bundle.getString("No_Recent_Files"));
                    } else {
                        openFile(new File(fileName));
                    }
                }
            };

            for (String fileName : mruList) {
                JMenuItem item = this.jMenuRecentFiles.add(fileName);
                item.addActionListener(mruAction);
            }
            this.jMenuRecentFiles.addSeparator();
            strClearRecentFiles = bundle.getString("Clear_Recent_Files");
            JMenuItem clearItem = this.jMenuRecentFiles.add(strClearRecentFiles);
            clearItem.addActionListener(mruAction);
        }
    }

    /**
     * Update MRU List.
     *
     * @param fileName
     */
    private void updateMRUList(String fileName) {
        if (mruList.contains(fileName)) {
            mruList.remove(fileName);
        }
        mruList.add(0, fileName);

        if (mruList.size() > 10) {
            mruList.remove(10);
        }

        updateMRUMenu();
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
            updateSave(true);
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
        } catch (OutOfMemoryError oome) {
//            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, APP_NAME + vietpadResources.getString("_has_run_out_of_memory.\nPlease_restart_") + APP_NAME + vietpadResources.getString("_and_try_again."), vietpadResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
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
        jButtonSave = new javax.swing.JButton();
        jButtonOCR = new javax.swing.JButton();
        jButtonCancelOCR = new javax.swing.JButton();
        jButtonCancelOCR.setVisible(false);
        jButtonClear = new javax.swing.JButton();
        jToggleButtonSpellCheck = new javax.swing.JToggleButton();
        jLabelLanguage = new javax.swing.JLabel();
        jComboBoxLang = new JComboBox(langs);
        jComboBoxLang.setSelectedItem(prefs.get("langCode", null));
        if (langCodes != null && jComboBoxLang.getSelectedIndex() != -1) {
            curLangCode = langCodes[jComboBoxLang.getSelectedIndex()];
        }
        jSplitPane1 = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        VietKeyListener keyLst = new VietKeyListener(jTextArea1);
        jTextArea1.addKeyListener(keyLst);
        VietKeyListener.setInputMethod(InputMethods.valueOf(selectedInputMethod));
        VietKeyListener.setSmartMark(true);
        VietKeyListener.consumeRepeatKey(true);
        VietKeyListener.setVietModeEnabled(curLangCode.contains("vie"));
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
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuRecentFiles = new javax.swing.JMenu();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuCommand = new javax.swing.JMenu();
        jMenuItemOCR = new javax.swing.JMenuItem();
        jMenuItemOCRAll = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemPostProcess = new javax.swing.JMenuItem();
        jMenuImage = new javax.swing.JMenu();
        jMenuItemMetadata = new javax.swing.JMenuItem();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        jCheckBoxMenuItemScreenshotMode = new javax.swing.JCheckBoxMenuItem();
        jMenuFormat = new javax.swing.JMenu();
        jCheckBoxMenuWordWrap = new javax.swing.JCheckBoxMenuItem();
        jMenuItemFont = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
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

        jSeparator6 = new javax.swing.JPopupMenu.Separator();
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
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemOptions = new javax.swing.JMenuItem();
        jMenuTools = new javax.swing.JMenu();
        jMenuItemMergeTiff = new javax.swing.JMenuItem();
        jMenuItemMergePdf = new javax.swing.JMenuItem();
        jMenuItemSplitPdf = new javax.swing.JMenuItem();
        jMenuHelp = new javax.swing.JMenu();
        jMenuItemHelp = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemAbout = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(500, 360));
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
        jButtonScan.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonScan.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonScan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonScanActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonScan);

        jButtonSave.setText(bundle.getString("jButtonSave.Text")); // NOI18N
        jButtonSave.setToolTipText(bundle.getString("jButtonSave.ToolTipText")); // NOI18N
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonSave);

        jButtonOCR.setText("OCR");
        jButtonOCR.setToolTipText(bundle.getString("jButtonOCR.ToolTipText")); // NOI18N
        jButtonOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOCRActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonOCR);

        jButtonCancelOCR.setText(bundle.getString("jButtonCancelOCR.Text")); // NOI18N
        jButtonCancelOCR.setToolTipText(bundle.getString("jButtonCancelOCR.ToolTipText")); // NOI18N
        jButtonCancelOCR.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCancelOCR.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCancelOCR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelOCRActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonCancelOCR);

        jButtonClear.setText(bundle.getString("jButtonClear.Text")); // NOI18N
        jButtonClear.setToolTipText(bundle.getString("jButtonClear.ToolTipText")); // NOI18N
        jButtonClear.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonClearActionPerformed(evt);
            }
        });
        jToolBar2.add(jButtonClear);
        jToolBar2.add(Box.createHorizontalGlue());

        jToggleButtonSpellCheck.setIcon(new javax.swing.ImageIcon(getClass().getResource("/net/sourceforge/vietocr/icons/spellcheck.png"))); // NOI18N
        jToggleButtonSpellCheck.setFocusable(false);
        jToggleButtonSpellCheck.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonSpellCheck.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonSpellCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonSpellCheckActionPerformed(evt);
            }
        });
        jToolBar2.add(jToggleButtonSpellCheck);
        jToolBar2.add(Box.createHorizontalStrut(20));
        jToggleButtonSpellCheck.getAccessibleContext().setAccessibleName("jToggleButtonSpellCheck");

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

        jMenuItemSaveAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSaveAs.setText(bundle.getString("jMenuItemSaveAs.Text")); // NOI18N
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);
        jMenuFile.add(jSeparator4);

        jMenuRecentFiles.setText(bundle.getString("jMenuRecentFiles.Text")); // NOI18N
        jMenuFile.add(jMenuRecentFiles);
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

        jMenuItemOCR.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
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

        jMenuItemPostProcess.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPostProcess.setText(bundle.getString("jMenuItemPostProcess.Text")); // NOI18N
        jMenuItemPostProcess.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPostProcessActionPerformed(evt);
            }
        });
        jMenuCommand.add(jMenuItemPostProcess);

        jMenuBar2.add(jMenuCommand);

        jMenuImage.setText(bundle.getString("jMenuImage.Text")); // NOI18N

        jMenuItemMetadata.setText(bundle.getString("jMenuItemMetadata.Text")); // NOI18N
        jMenuItemMetadata.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMetadataActionPerformed(evt);
            }
        });
        jMenuImage.add(jMenuItemMetadata);
        jMenuImage.add(jSeparator11);

        jCheckBoxMenuItemScreenshotMode.setSelected(true);
        jCheckBoxMenuItemScreenshotMode.setText("Screenshot Mode");
        jMenuImage.add(jCheckBoxMenuItemScreenshotMode);

        jMenuBar2.add(jMenuImage);

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

        jMenuTools.setText(bundle.getString("jMenuTools.Text")); // NOI18N

        jMenuItemMergeTiff.setText(bundle.getString("jMenuItemMergeTiff.Text")); // NOI18N
        jMenuItemMergeTiff.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMergeTiffActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMergeTiff);

        jMenuItemMergePdf.setText(bundle.getString("jMenuItemMergePdf.Text")); // NOI18N
        jMenuItemMergePdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemMergePdfActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemMergePdf);

        jMenuItemSplitPdf.setText(bundle.getString("jMenuItemSplitPdf.Text")); // NOI18N
        jMenuItemSplitPdf.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSplitPdfActionPerformed(evt);
            }
        });
        jMenuTools.add(jMenuItemSplitPdf);

        jMenuBar2.add(jMenuTools);

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
            VietKeyListener.setVietModeEnabled(curLangCode.contains("vie"));
            if (this.jToggleButtonSpellCheck.isSelected()) {
                this.jToggleButtonSpellCheck.doClick();
                this.jToggleButtonSpellCheck.doClick();
            }
        }
    }//GEN-LAST:event_jComboBoxLangItemStateChanged

    private void jMenuItemOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOCRActionPerformed
        OCRActionPerformed();
    }//GEN-LAST:event_jMenuItemOCRActionPerformed

    void OCRActionPerformed() {
        // to be implemented in subclas
    }

    private void jMenuItemOCRAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOCRAllActionPerformed
        OCRAllActionPerformed();
    }//GEN-LAST:event_jMenuItemOCRAllActionPerformed

    void OCRAllActionPerformed() {
        // to be implemented in subclass
    }

    private void jMenuItemPostProcessActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPostProcessActionPerformed
        PostProcessActionPerformed();
    }//GEN-LAST:event_jMenuItemPostProcessActionPerformed

    void PostProcessActionPerformed() {
        // to be implemented in subclass
    }

    private void jButtonPrevPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPrevPageActionPerformed
        ((JImageLabel) jImageLabel).deselect();
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
    }//GEN-LAST:event_jButtonPrevPageActionPerformed

    private void jButtonNextPageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNextPageActionPerformed
        ((JImageLabel) jImageLabel).deselect();
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
    }//GEN-LAST:event_jButtonNextPageActionPerformed

    private void jButtonFitImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFitImageActionPerformed
        this.jButtonFitImage.setEnabled(false);
        this.jButtonActualSize.setEnabled(true);
        this.jButtonZoomIn.setEnabled(false);
        this.jButtonZoomOut.setEnabled(false);
        ((JImageLabel) jImageLabel).deselect();
        curScrollPos = this.jScrollPane2.getViewport().getViewPosition();
        scaleX = (float) originalW / (float) this.jScrollPane2.getWidth();
        scaleY = (float) originalH / (float) this.jScrollPane2.getHeight();
        fitImageChange(this.jScrollPane2.getWidth(), this.jScrollPane2.getHeight());
        isFitImageSelected = true;
    }//GEN-LAST:event_jButtonFitImageActionPerformed

    private void jButtonActualSizeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualSizeActionPerformed
        this.jButtonFitImage.setEnabled(true);
        this.jButtonActualSize.setEnabled(false);
        this.jButtonZoomIn.setEnabled(true);
        this.jButtonZoomOut.setEnabled(true);
        ((JImageLabel) jImageLabel).deselect();
        fitImageChange(originalW, originalH);
        scaleX = scaleY = 1f;
        isFitImageSelected = false;
    }//GEN-LAST:event_jButtonActualSizeActionPerformed

    void fitImageChange(final int width, final int height) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                imageIcon.setScaledSize(width, height);
                jScrollPane2.getViewport().setViewPosition(curScrollPos);
                jImageLabel.revalidate();
                jScrollPane2.repaint();
            }
        });
    }

    private void jButtonZoomOutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomOutActionPerformed
        ((JImageLabel) jImageLabel).deselect();
        doChange(false);
        isFitImageSelected = false;
    }//GEN-LAST:event_jButtonZoomOutActionPerformed

    private void jButtonZoomInActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonZoomInActionPerformed
        ((JImageLabel) jImageLabel).deselect();
        doChange(true);
        isFitImageSelected = false;
    }//GEN-LAST:event_jButtonZoomInActionPerformed

    void doChange(final boolean isZoomIn) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                int width = imageIcon.getIconWidth();
                int height = imageIcon.getIconHeight();

                if (isZoomIn) {
                    imageIcon.setScaledSize((int) (width * ZOOM_FACTOR), (int) (height * ZOOM_FACTOR));
                } else {
                    imageIcon.setScaledSize((int) (width / ZOOM_FACTOR), (int) (height / ZOOM_FACTOR));
                }
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

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        jMenuItemSaveActionPerformed(evt);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOCRActionPerformed
        jMenuItemOCRActionPerformed(evt);
    }//GEN-LAST:event_jButtonOCRActionPerformed

    private void jButtonClearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonClearActionPerformed
        if (textFile == null || promptToSave()) {
            this.jTextArea1.setText(null);
            this.jTextArea1.requestFocusInWindow();
            textFile = null;
            updateSave(false);
        }
    }//GEN-LAST:event_jButtonClearActionPerformed

    private void jCheckBoxMenuWordWrapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuWordWrapActionPerformed
        setLineWrap();
    }//GEN-LAST:event_jCheckBoxMenuWordWrapActionPerformed

    void setLineWrap() {
        // to be implemented in subclass
    }

    private void jMenuItemAboutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemAboutActionPerformed
        try {
            String version = config.getProperty("Version");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date releaseDate = sdf.parse(config.getProperty("ReleaseDate"));

            JOptionPane.showMessageDialog(this, APP_NAME + ", " + version + " \u00a9 2007\n"
                    + "Java GUI Frontend for Tesseract 2.0x OCR Engine\n"
                    + DateFormat.getDateInstance(DateFormat.LONG).format(releaseDate)
                    + "\nhttp://vietocr.sourceforge.net", APP_NAME, JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }//GEN-LAST:event_jMenuItemAboutActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        quit();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    void quit() {
        if (!promptToSave()) {
            return;
        }
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

        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < this.mruList.size(); i++) {
            buf.append(this.mruList.get(i)).append(File.pathSeparatorChar);
        }
        prefs.put("MruList", buf.toString());

        if (getExtendedState() == NORMAL) {
            prefs.putInt("frameHeight", getHeight());
            prefs.putInt("frameWidth", getWidth());
            prefs.putInt("frameX", getX());
            prefs.putInt("frameY", getY());
        }

        prefs.putInt("filterIndex", filterIndex);

        System.exit(0);
    }

    private void jMenuItemFontActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemFontActionPerformed
        openFontDialog(curLangCode);
    }//GEN-LAST:event_jMenuItemFontActionPerformed

    void openFontDialog(String langCode) {
        // to be implemented in subclass
    }

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        if (filechooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentDirectory = filechooser.getCurrentDirectory().getPath();
            openFile(filechooser.getSelectedFile());

            for (int i = 0; i < fileFilters.length; i++) {
                if (fileFilters[i] == filechooser.getFileFilter()) {
                    filterIndex = i;
                    break;
                }
            }
        }
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    /**
     * Opens image file.
     *
     */
    public void openFile(final File selectedFile) {
        // if text file, load it into textarea
        if (selectedFile.getName().endsWith(".txt")) {
            if (!promptToSave()) {
                return;
            }
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        new FileInputStream(selectedFile), "UTF8"));
                this.jTextArea1.read(in, null);
                in.close();
                this.textFile = selectedFile;
                javax.swing.text.Document doc = this.jTextArea1.getDocument();
                if (doc.getText(0, 1).equals("\uFEFF")) {
                    doc.remove(0, 1); // remove BOM
                }
                doc.addUndoableEditListener(rawListener);
                updateMRUList(selectedFile.getPath());
                updateSave(false);
                this.jTextArea1.requestFocusInWindow();
            } catch (Exception e) {
            }
            return;
        }

        jLabelStatus.setText(bundle.getString("Loading_image..."));
        jProgressBar1.setIndeterminate(true);
        jProgressBar1.setString(bundle.getString("Loading_image..."));
        jProgressBar1.setVisible(true);
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);
        this.jButtonOCR.setEnabled(false);
        this.jMenuItemOCR.setEnabled(false);
        this.jMenuItemOCRAll.setEnabled(false);

        SwingWorker loadWorker = new SwingWorker<File, Void>() {

            @Override
            protected File doInBackground() throws Exception {
                iioImageList = ImageIOHelper.getIIOImageList(selectedFile);
                imageList = ImageIconScalable.getImageList(iioImageList);
                return selectedFile;
            }

            @Override
            protected void done() {
                jProgressBar1.setIndeterminate(false);

                try {
                    loadImage(get());
                    jLabelStatus.setText(bundle.getString("Loading_completed"));
                    jProgressBar1.setString(bundle.getString("Loading_completed"));
                    updateMRUList(selectedFile.getPath());
                } catch (InterruptedException ignore) {
//                    ignore.printStackTrace();
                    jLabelStatus.setText("Loading canceled.");
                    jProgressBar1.setString("Loading canceled.");
                } catch (java.util.concurrent.ExecutionException e) {
                    String why = null;
                    Throwable cause = e.getCause();
                    if (cause != null) {
                        if (cause instanceof OutOfMemoryError) {
                            why = bundle.getString("OutOfMemoryError");
                        } else {
                            why = cause.getMessage();
                        }
                    } else {
                        why = e.getMessage();
                    }
                    e.printStackTrace();
//                    jLabelStatus.setText(null);
//                    jProgressBar1.setString(null);
                    JOptionPane.showMessageDialog(Gui.this, why, APP_NAME, JOptionPane.ERROR_MESSAGE);
                    jProgressBar1.setVisible(false);
                } finally {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                    jButtonOCR.setEnabled(true);
                    jMenuItemOCR.setEnabled(true);
                    jMenuItemOCRAll.setEnabled(true);
                }
            }
        };

        loadWorker.execute();
    }

    void loadImage(File selectedFile) {
        if (imageList == null) {
            JOptionPane.showMessageDialog(this, bundle.getString("Cannotloadimage"), APP_NAME, JOptionPane.ERROR_MESSAGE);
            return;
        }

        imageTotal = imageList.size();
        imageIndex = 0;
        scaleX = scaleY = 1f;
        isFitImageSelected = false;

        displayImage();

        this.setTitle(selectedFile.getName() + " - " + APP_NAME);

        ((JImageLabel) jImageLabel).deselect();

        this.jButtonFitImage.setEnabled(true);
        this.jButtonActualSize.setEnabled(false);
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
        imageIcon = imageList.get(imageIndex).clone();
        originalW = imageIcon.getIconWidth();
        originalH = imageIcon.getIconHeight();

        if (this.isFitImageSelected) {
            // scale image to fit the scrollpane
            imageIcon.setScaledSize(this.jScrollPane2.getWidth(), this.jScrollPane2.getHeight());
            scaleX = (float) originalW / (float) this.jScrollPane2.getWidth();
            scaleY = (float) originalH / (float) this.jScrollPane2.getHeight();
        } else if (Math.abs(scaleX - 1f) > 0.001f) {
            // scale image for zoom
            imageIcon.setScaledSize((int) (originalW / scaleX), (int) (originalH / scaleY));
        }

        jImageLabel.setIcon(imageIcon);
        this.jScrollPane2.getViewport().setViewPosition(curScrollPos = new Point());
        jImageLabel.revalidate();
    }

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        saveAction();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    boolean saveAction() {
        if (textFile == null || !textFile.exists()) {
            return saveFileDlg();
        } else {
            return saveTextFile();
        }
    }

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        saveFileDlg();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    boolean saveFileDlg() {
        outputDirectory = prefs.get("outputDirectory", null);
        JFileChooser chooser = new JFileChooser(outputDirectory);
        FileFilter txtFilter = new SimpleFilter("txt", "UTF-8 Text");
        chooser.addChoosableFileFilter(txtFilter);
        chooser.setDialogTitle(vietpadResources.getString("Save_As"));
        if (textFile != null) {
            chooser.setSelectedFile(textFile);
        }

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            outputDirectory = chooser.getCurrentDirectory().getPath();
            File f = chooser.getSelectedFile();
            if (chooser.getFileFilter() == txtFilter) {
                if (!f.getName().endsWith(".txt")) {
                    f = new File(f.getPath() + ".txt");
                }
                if (textFile != null && textFile.getPath().equals(f.getPath())) {
                    if (JOptionPane.NO_OPTION == JOptionPane.showConfirmDialog(
                            Gui.this,
                            textFile.getName() + " already exists.\nDo you want to replace it?",
                            "Confirm Save As", JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE)) {
                        return false;
                    }
                } else {
                    textFile = f;
                }
            }
            return saveTextFile();
        } else {
            return false;
        }
    }

    boolean saveTextFile() {
        getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        getGlassPane().setVisible(true);

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(textFile), UTF8));
            jTextArea1.write(out);
            out.close();
            updateMRUList(textFile.getPath());
            updateSave(false);
        } catch (OutOfMemoryError oome) {
//            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, APP_NAME + vietpadResources.getString("_has_run_out_of_memory.\nPlease_restart_") + APP_NAME + vietpadResources.getString("_and_try_again."), vietpadResources.getString("Out_of_Memory"), JOptionPane.ERROR_MESSAGE);
        } catch (FileNotFoundException fnfe) {
            showError(fnfe, vietpadResources.getString("Error_saving_file_") + textFile + vietpadResources.getString(".\nFile_is_inaccessible."));
        } catch (Exception ex) {
            showError(ex, vietpadResources.getString("Error_saving_file_") + textFile);
        } finally {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                    getGlassPane().setVisible(false);
                }
            });
        }

        return true;
    }

    /**
     *  Displays a dialog to save changes.
     *
     *@return    false if user canceled, true else
     */
    protected boolean promptToSave() {
        if (!textChanged) {
            return true;
        }
        switch (JOptionPane.showConfirmDialog(this,
                vietpadResources.getString("Do_you_want_to_save_the_changes_to")
                + " \"" + (textFile == null ? vietpadResources.getString("Untitled") : textFile.getName()) + "\"?",
                APP_NAME, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE)) {
            case JOptionPane.YES_OPTION:
                return saveAction();
            case JOptionPane.NO_OPTION:
                return true;
            default:
                return false;
        }
    }

    /**
     *  Updates the Save action.
     *
     *@param  modified  whether file has been modified
     */
    void updateSave(boolean modified) {
        if (textChanged != modified) {
            textChanged = modified;
            this.jButtonSave.setEnabled(modified);
            this.jMenuItemSave.setEnabled(modified);
            rootPane.putClientProperty("windowModified", Boolean.valueOf(modified));
            // see http://developer.apple.com/qa/qa2001/qa1146.html
        }
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
//            exc.printStackTrace();
        }

        for (Window win : Window.getWindows()) {
            SwingUtilities.updateComponentTreeUI(win);
            win.validate();
        }

        SwingUtilities.updateComponentTreeUI(popup);
        SwingUtilities.updateComponentTreeUI(filechooser);
    }

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        jSplitPane1.setDividerLocation(jSplitPane1.getWidth() / 2);

        if (isFitImageSelected && imageIcon != null) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ((JImageLabel) jImageLabel).deselect();
                    scaleX *= (float) imageIcon.getIconWidth() / (float) jScrollPane2.getWidth();
                    scaleY *= (float) imageIcon.getIconHeight() / (float) jScrollPane2.getHeight();
                    fitImageChange(jScrollPane2.getWidth(), jScrollPane2.getHeight());
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
        scaleX = scaleY = 1f;
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
                    File tempImageFile = File.createTempFile("tmp", WINDOWS ? ".bmp" : ".png");

                    if (tempImageFile.exists()) {
                        tempImageFile.delete();
                    }
                    if (WINDOWS) {
                        WiaScannerAdapter adapter = new WiaScannerAdapter(); // with MS WIA
                        // The reason for not using PNG format is that jai-imageio library would throw an "I/O error reading PNG header" error.
                        tempImageFile = adapter.ScanImage(FormatID.wiaFormatBMP, tempImageFile.getCanonicalPath());
                    } else {
//                        JSane_Base_Frame frame = JSane_Scan_Dialog.getScan("localhost", 6566); // with SANE
//                        ImageIO.write(frame.getImage(false), "png", tempImageFile);
                    }
                    openFile(tempImageFile);
                    tempImageFile.deleteOnExit();
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

    void rotateImage(int angle) {
        try {
            imageIcon = imageList.get(imageIndex).getRotatedImageIcon(Math.toRadians(angle));
            imageList.set(imageIndex, imageIcon); // persist the rotated image
            iioImageList.get(imageIndex).setRenderedImage((BufferedImage) imageIcon.getImage());
            displayImage();
        } catch (OutOfMemoryError oome) {
//            oome.printStackTrace();
            JOptionPane.showMessageDialog(this, oome.getMessage(), bundle.getString("OutOfMemoryError"), JOptionPane.ERROR_MESSAGE);
        }
    }

    private void jMenuItemOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOptionsActionPerformed
        openOptionsDialog();
    }//GEN-LAST:event_jMenuItemOptionsActionPerformed

    void openOptionsDialog() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jMenuItemChangeCaseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemChangeCaseActionPerformed
        openChangeCaseDialog();
    }//GEN-LAST:event_jMenuItemChangeCaseActionPerformed

    void openChangeCaseDialog() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jMenuItemRemoveLineBreaksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRemoveLineBreaksActionPerformed
        removeLineBreaks();
    }//GEN-LAST:event_jMenuItemRemoveLineBreaksActionPerformed

    void removeLineBreaks() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jMenuItemMergeTiffActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMergeTiffActionPerformed
        mergeTiffs();
    }//GEN-LAST:event_jMenuItemMergeTiffActionPerformed

    void mergeTiffs() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jMenuItemSplitPdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSplitPdfActionPerformed
        splitPdf();
    }//GEN-LAST:event_jMenuItemSplitPdfActionPerformed

    void splitPdf() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jButtonCancelOCRActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelOCRActionPerformed
        CancelOCRActionPerformed();
    }//GEN-LAST:event_jButtonCancelOCRActionPerformed

    void CancelOCRActionPerformed() {
        // to be implemented in subclass
    }
    private void jMenuItemMergePdfActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMergePdfActionPerformed
        mergePdf();
    }//GEN-LAST:event_jMenuItemMergePdfActionPerformed

    void mergePdf() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    private void jMenuItemMetadataActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemMetadataActionPerformed
        readImageMetadata();
    }//GEN-LAST:event_jMenuItemMetadataActionPerformed

    private void jToggleButtonSpellCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonSpellCheckActionPerformed
        SpellChecker sp = new SpellChecker(this.jTextArea1, curLangCode);
        if (this.jToggleButtonSpellCheck.isSelected()) {
            sp.enableSpellCheck();
        } else {
            sp.disableSpellCheck();
        }
        this.jTextArea1.repaint();
    }//GEN-LAST:event_jToggleButtonSpellCheckActionPerformed

    void readImageMetadata() {
        JOptionPane.showMessageDialog(this, TO_BE_IMPLEMENTED);
    }

    void changeUILanguage(final Locale locale) {
        if (locale.equals(Locale.getDefault())) {
            return; // no change in locale
        }
        Locale.setDefault(locale);
        bundle = java.util.ResourceBundle.getBundle("net.sourceforge.vietocr.Gui");
        vietpadResources = ResourceBundle.getBundle("net.sourceforge.vietpad.Resources");

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
    protected javax.swing.JButton jButtonCancelOCR;
    private javax.swing.JButton jButtonClear;
    private javax.swing.JButton jButtonFitImage;
    private javax.swing.JButton jButtonNextPage;
    protected javax.swing.JButton jButtonOCR;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonPrevPage;
    private javax.swing.JButton jButtonRotateCCW;
    private javax.swing.JButton jButtonRotateCW;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonScan;
    private javax.swing.JButton jButtonZoomIn;
    private javax.swing.JButton jButtonZoomOut;
    protected javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemScreenshotMode;
    protected javax.swing.JCheckBoxMenuItem jCheckBoxMenuWordWrap;
    protected javax.swing.JComboBox jComboBoxLang;
    protected javax.swing.JLabel jImageLabel;
    private javax.swing.JLabel jLabelCurIndex;
    private javax.swing.JLabel jLabelLanguage;
    protected javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenu jMenuCommand;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenu jMenuFormat;
    private javax.swing.JMenu jMenuHelp;
    private javax.swing.JMenu jMenuImage;
    private javax.swing.JMenu jMenuInputMethod;
    private javax.swing.JMenuItem jMenuItemAbout;
    private javax.swing.JMenuItem jMenuItemChangeCase;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFont;
    private javax.swing.JMenuItem jMenuItemHelp;
    private javax.swing.JMenuItem jMenuItemMergePdf;
    private javax.swing.JMenuItem jMenuItemMergeTiff;
    private javax.swing.JMenuItem jMenuItemMetadata;
    protected javax.swing.JMenuItem jMenuItemOCR;
    protected javax.swing.JMenuItem jMenuItemOCRAll;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemOptions;
    protected javax.swing.JMenuItem jMenuItemPostProcess;
    private javax.swing.JMenuItem jMenuItemRemoveLineBreaks;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemScan;
    private javax.swing.JMenuItem jMenuItemSplitPdf;
    private javax.swing.JMenu jMenuLookAndFeel;
    private javax.swing.JMenu jMenuRecentFiles;
    private javax.swing.JMenu jMenuSettings;
    private javax.swing.JMenu jMenuTools;
    private javax.swing.JMenu jMenuUILang;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelStatus;
    protected javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemEng;
    private javax.swing.JRadioButtonMenuItem jRadioButtonMenuItemViet;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    protected javax.swing.JTextArea jTextArea1;
    private javax.swing.JToggleButton jToggleButtonSpellCheck;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JPopupMenu popup;
    // End of variables declaration//GEN-END:variables
    private final UndoManager m_undo = new UndoManager();
    protected final UndoableEditSupport undoSupport = new UndoableEditSupport();
    private Action m_undoAction, m_redoAction, actionCut, actionCopy, actionPaste, actionDelete, actionSelectAll;
    private final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    private JFrame helptopicsFrame;
}
