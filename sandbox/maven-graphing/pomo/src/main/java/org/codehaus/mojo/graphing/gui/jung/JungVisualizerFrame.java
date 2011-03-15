package org.codehaus.mojo.graphing.gui.jung;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.codehaus.mojo.graphing.gui.KeyAction;
import org.codehaus.mojo.graphing.gui.WindowHandler;
import org.codehaus.mojo.graphing.model.GraphModel;
import org.codehaus.mojo.graphing.model.factory.StaticGraphModelFactory;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import edu.uci.ics.jung.graph.impl.DirectedSparseVertex;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PickedState;
import edu.uci.ics.jung.visualization.PluggableRenderer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.DAGLayout;
import edu.uci.ics.jung.visualization.transform.Transformer;

public class JungVisualizerFrame
extends JFrame
{
    private Log log;
    private WindowHandler winhandler;
    private VisualizationViewer vv;
    private PluggableRenderer pr;
    private Graph g;
    
    public JungVisualizerFrame(Log logger)
    {
        this.log = logger;
        initGui();
    }
    
    private void initGui() {
        this.winhandler = new WindowHandler(this, true);
        this.winhandler.setPersistLocation(true);
        this.winhandler.setPersistSize(true);
        
        String lnf = UIManager.getCrossPlatformLookAndFeelClassName();
        setLookAndFeel(lnf);

        setName("maven-jung-visualizer");
        setTitle("Jung Visualizer");
        
        getContentPane().setLayout(new BorderLayout());
        
        this.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_MASK),
                "exit");

        this.getRootPane().getActionMap().put(
            "exit",
            new KeyAction(new ActionHandler(), "exit"));

        g = getGraph();

        // Menu Bar
        setJMenuBar(createMainMenu());
        
        this.getContentPane().add(createJungComponent(), BorderLayout.CENTER);
        
        this.winhandler.setSizePreferred(new Dimension(800, 700));
        addWindowListener(this.winhandler);
    }
    
    protected JComponent createJungComponent()
    {
        Layout layout = new DAGLayout( g );
        pr = new PluggableRenderer();
        vv = new VisualizationViewer( layout, pr );
        vv.setPickSupport( new ShapePickSupport() );
        
        PickedState picked_state = vv.getPickedState();

        Transformer affineTransformer = vv.getLayoutTransformer();
        
        JPanel jp = new JPanel();
        jp.setLayout( new BorderLayout() );

        vv.setBackground( Color.white );
        GraphZoomScrollPane scrollPane = new GraphZoomScrollPane( vv );
        jp.add( scrollPane );

        return vv;
    }
    
    protected Graph getGraph()
    {
        StaticGraphModelFactory graphfactory = new StaticGraphModelFactory(new SystemStreamLog());
        GraphModel gmodel = graphfactory.getGraphModel("graph-model-dom4j.xml");
        
        Graph graph = new DirectedSparseGraph();
        
        Map vertexMap = new HashMap();
        
        Iterator it = gmodel.getEdgesIterator();
        while(it.hasNext())
        {
            org.codehaus.mojo.graphing.model.Edge edge = (org.codehaus.mojo.graphing.model.Edge) it.next();
            Vertex vparent = (Vertex) vertexMap.get(edge.getNode1().getId());
            if(vparent == null) {
                vparent = graph.addVertex(new DirectedSparseVertex());
                // transparency.setNumber( vparent, new MutableDouble( 0.9 ) );
                // voltages.setNumber(vparent, new Double(0.9));
                vertexMap.put(edge.getNode1().getId(), vparent);
            } 
            Vertex vchild = (Vertex) vertexMap.get(edge.getNode2().getId());
            if(vchild == null) {
                vchild = graph.addVertex(new DirectedSparseVertex());
                // transparency.setNumber( vchild, new MutableDouble( 0.9 ) );
                // voltages.setNumber(vchild, new Double(0.9));
                vertexMap.put(edge.getNode2().getId(), vchild);
            }
            Edge e = graph.addEdge(new DirectedSparseEdge(vparent, vchild));
        }
        
        return graph;
    }    
    
    private JMenuBar createMainMenu() {
        ActionHandler actionHandler = new ActionHandler();

        JMenuBar mainMenu = new JMenuBar();
        mainMenu.add(createFileMenu(actionHandler));
        mainMenu.add(createViewMenu(actionHandler)); 

        return mainMenu;
    }
    
    private JMenu createFileMenu(ActionHandler actionHandler) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');

        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic('x');
        fileExit.setActionCommand("exit");
        fileExit.addActionListener(actionHandler);
        fileMenu.add(fileExit);
        return fileMenu;
    }    

    private JMenu createViewMenu(ActionHandler actionHandler) {
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('v');

        JMenuItem layoutKK = new JMenuItem("Use KKLayout");
        layoutKK.setMnemonic('k');
        layoutKK.setActionCommand("layout-kk");
        layoutKK.addActionListener(actionHandler);
        viewMenu.add(layoutKK);
        return viewMenu;
    }    
    
    public void setLookAndFeel(String uiclassname) {
        try {
            UIManager.setLookAndFeel(uiclassname);
            SwingUtilities.updateComponentTreeUI(this);
        } catch (ClassNotFoundException e1) {
            log.warn("Unable to set Look and Feel (it is missing).");
        } catch (InstantiationException e1) {
            log.warn("Unable to set Look and Feel (cannot be instantiated by JRE).");
        } catch (IllegalAccessException e1) {
            log.warn("Unable to set Look and Feel (cannot be used by JRE).");
        } catch (UnsupportedLookAndFeelException e1) {
            log.warn("Unable to set Look and Feel (not supported by JRE).");
        }
    }
    
    public class ActionHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() instanceof JMenuItem) {
                if ("exit".equals(e.getActionCommand())) {
                    winhandler.close();
                } else if("layout-kk".equals(e.getActionCommand())) {
                    // TODO: swap to KKLayout.
                }
            }
        }
    }    
    
    public static void main(String args[])
    {
        JungVisualizerFrame frame = new JungVisualizerFrame(new SystemStreamLog());
        frame.setVisible(true);
    }
}
