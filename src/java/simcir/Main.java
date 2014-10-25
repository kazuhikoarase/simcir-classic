package simcir;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MediaTracker;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import com.d_project.ui.DComponent;
import com.d_project.ui.DContainer;
import com.d_project.ui.DEventQueue;
import com.d_project.ui.DPanel;
import com.d_project.ui.DToolbar;
import com.d_project.ui.event.DActionEvent;
import com.d_project.ui.event.DActionListener;

/**
 * Main
 * @author Kazuhiko Arase
 */
public class Main extends Applet implements DActionListener {

    public static void main(String[] args) {
            Main m = new Main();
        CircuitFrame frame = new CircuitFrame();
        frame.start(m);
    }

/* ******************************************************** */

    private static final String[] command = new String[]{
        "New", "Open", "SaveAs", 
        "Cut", "Copy", "Paste"
    };

    private static final String SUFFIX = ".cml";

    DEventQueue  queue;

    String depName;
    String dirName;
    String fileName;

    String nameBase;
    int    nameCount;

    DToolbar toolbar;
    CircuitBoard board;

    public Main() {
        setBackground(Color.lightGray);
        setFont(new Font("SansSerif", Font.PLAIN, 12) );        
        setLayout(new BorderLayout() );

        nameBase = "Circuit";
        nameCount = 1;

        toolbar = new DToolbar(command);
        board   = new CircuitBoard();
        board.toolbar = toolbar;
        DContainer cont = new CirContainer(toolbar, board);
        toolbar.addActionListener(this);
        cont.setSize(600, 400);        

        queue = new DEventQueue(false);

        add("Center", new DPanel(cont, queue) );

    }

    public void init() {

        initApp();

        toolbar.setEnable(false, "New");
        toolbar.setEnable(false, "Open");
        toolbar.setEnable(false, "SaveAs");

        String file = getParameter("file");
        if (file != null) {
            try {
                URL url = new URL(getDocumentBase(), file);
                    CircuitDocument doc = new CircuitDocument(url);
                board.clear();
                board.paste(doc, false);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    void initApp() {
        try {        
            InputStream ris = getClass().getResourceAsStream("toolbar.gif");
            if (ris != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                int data;
                while ( (data = ris.read() ) != -1) {
                    bos.write(data);
                }
                bos.close();
                ris.close();
                Image image = getToolkit().createImage(bos.toByteArray() );
                preloadImage(image);
                toolbar.setImage(image);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        queue.start();
    }

    public void stop() {
        queue.stop();
    }

    public void preloadImage(Image image) {
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(image, 0);
        try {
            mt.waitForAll();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    void newFile() {
        board.clear();
        depName = nameBase + (nameCount++) + SUFFIX;
        setFileName(null);
    }

    void save() {
        if (fileName == null) {
            saveAs();
        } else {
            saveImpl();
        }
    }

    Frame getFrame() {
        Component comp = getParent();
        while (comp != null && !(comp instanceof Frame) ) {
            comp = comp.getParent();
        }
        return (Frame)comp;
    }

    void open() {
        Frame frame = getFrame();
        FileDialog dialog = new FileDialog(frame, "Open", FileDialog.LOAD);
        if (doModal(dialog) ) { 
            try {
                String fileName = this.fileName;
                if (fileName != null) {
                    String dirName = this.dirName;
                    File file = (dirName != null)? new File(dirName, fileName) : new File(fileName);
                    InputStream is = new BufferedInputStream(new FileInputStream(file) );
                    CircuitDocument doc = new CircuitDocument(is);
                    board.clear();
                    board.paste(doc, false);
                    is.close();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    void saveAs() {
        Frame frame = getFrame();
        FileDialog dialog = new FileDialog(frame, "SaveAs", FileDialog.SAVE);
        dialog.setFile( (fileName != null)? fileName : depName);
        if (doModal(dialog) ) { 
            saveImpl();
        }
    }

    private void saveImpl() {
        try {
            String fileName = this.fileName;
            if (fileName != null) {
                String dirName  = this.dirName;
                File file = (dirName != null)? new File(dirName, fileName) : new File(fileName);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file) );
                board.write(os);
                os.close();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setFileName(String fileName) {

        this.fileName = fileName;

        String title = "Circuit Simulator";
        if (fileName != null) {
            title += " - [" + fileName + "]";
        } else {
            title += " - [" + depName + "]";
        }        
        setTitle(title);        
    }

    public void setTitle(String title) {
        Frame frame = getFrame();
        if (frame != null) frame.setTitle(title);
    }

    private boolean doModal(FileDialog dialog) {
        if (dirName != null) dialog.setDirectory(dirName);
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if (fileName != null) {
            dirName = dialog.getDirectory();
            setFileName(fileName);
            return true;
        }
        return false;
    }

    public void actionPerformed(DActionEvent e) {
        action(e.getActionCommand() );
    }

    public void action(String command) {
        if (command.equals("New") ) {
            newFile();
        } else if (command.equals("Open") ) {
            open();
        } else if (command.equals("Save") ) {
            save();
        } else if (command.equals("SaveAs") ) {
            saveAs();
        } else if (command.equals("Copy") ) {
            board.copy();
        } else if (command.equals("Cut") ) {
            board.cut();
        } else if (command.equals("Paste") ) {
            board.paste();
        } else if (command.equals("SelectAll") ) {
            board.selectAll();
        }
    }
}

class CirContainer extends DContainer {

    DComponent comp1;
    DComponent comp2;

    public CirContainer(DComponent comp1, DComponent comp2) {
        add(this.comp1 = comp1);
        add(this.comp2 = comp2);
    }

    public void doLayout() {
        Dimension size = getSize();
        comp1.setBounds(0, 0, 32 * 6, 32);
        comp2.setBounds(0, 32, size.width, size.height - 32);
    }
}

