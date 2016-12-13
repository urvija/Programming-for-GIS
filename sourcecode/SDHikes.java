package sourcecode;

import javax.swing.*;
import java.io.*;
import java.sql.Array;
import java.math.*;
import java.util.Vector;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.awt.*;
import java.awt.event.*;

import com.esri.mo2.ui.bean.*;
import com.esri.mo2.ui.tb.ZoomPanToolBar;
import com.esri.mo2.ui.tb.SelectionToolBar;
import com.esri.mo2.ui.ren.LayerProperties;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import com.esri.mo2.data.feat.*;
import com.esri.mo2.map.dpy.FeatureLayer;
import com.esri.mo2.map.dpy.BaseFeatureLayer;
import com.esri.mo2.map.draw.AoFillStyle;
import com.esri.mo2.map.draw.AoLineStyle;
import com.esri.mo2.map.draw.SimpleMarkerSymbol;
import com.esri.mo2.map.draw.BaseSimpleRenderer;
import com.esri.mo2.map.draw.TrueTypeMarkerSymbol;
import com.esri.mo2.file.shp.*;
import com.esri.mo2.map.dpy.Layerset;
import com.esri.mo2.ui.bean.Tool;
import java.awt.geom.*;
import com.esri.mo2.ui.dlg.AboutBox;
import com.esri.mo2.cs.geom.*;
import com.esri.mo2.map.draw.*;
import com.esri.mo2.ui.tb.*;

public class SDHikes extends JFrame {

	static Map map = new Map();
	static boolean fullMap = true;
	static boolean helpToolOn;
	Legend legend;
	Legend legend2;
	Layer layer = new Layer();
	Layer layer2 = new Layer();
	Layer layer3 = null;
	static com.esri.mo2.map.dpy.Layer layer4;
	com.esri.mo2.map.dpy.Layer activeLayer;
	int activeLayerIndex;
	com.esri.mo2.cs.geom.Point initPoint, endPoint;
	double distance;

	JMenuBar mbar = new JMenuBar();
	JMenu file = new JMenu("File");
	JMenu theme = new JMenu("Theme");
	JMenu layercontrol = new JMenu("LayerControl");
	JMenu help = new JMenu("Help");
	JMenu readme = new JMenu ("README");

	JMenuItem attribitem = new JMenuItem("open attribute table", new ImageIcon("images/tableview.gif"));
	JMenuItem createlayeritem = new JMenuItem("create layer from selection", new ImageIcon("images/Icon0915b.jpg"));
	static JMenuItem promoteitem = new JMenuItem("promote selected layer", new ImageIcon("images/promote.jpg"));
	JMenuItem demoteitem = new JMenuItem("demote selected layer", new ImageIcon("images/demote.jpg"));
	JMenuItem printitem = new JMenuItem("Print", new ImageIcon("images/print.gif"));
	JMenuItem addlyritem = new JMenuItem("Add Layer", new ImageIcon("images/addtheme.gif"));
	JMenuItem remlyritem = new JMenuItem("Remove Layer", new ImageIcon("images/delete.gif"));
	JMenuItem propsitem = new JMenuItem("Legend Editor", new ImageIcon("images/properties.gif"));
	JMenu helptopics = new JMenu("Help Topics");
	JMenuItem tocitem = new JMenuItem("Table of Contents");
	JMenuItem tocitem1 = new JMenuItem("Table of Contents");
	JMenuItem legenditem = new JMenuItem("Legend Editor");
	JMenuItem legenditem1 = new JMenuItem("Legend_Editor");
	JMenuItem layercontrolitem = new JMenuItem("Layer Control");
	JMenuItem layercontrolitem1 = new JMenuItem("Layer Control");
	JMenuItem distancemeasureitem = new JMenuItem("Measure Tool");
	JMenuItem distancemeasureitem1 = new JMenuItem("Measure Tool");
	JMenuItem XYitem = new JMenuItem("XY Button");
	JMenuItem XYitem1 = new JMenuItem("XY Button");
	JMenuItem hotlinkitem = new JMenuItem("HotLink Tool");
	JMenuItem hotlinkitem1 = new JMenuItem("HotLink Tool");
	JMenuItem helptoolitem = new JMenuItem("Help Tool");
	JMenuItem contactitem = new JMenuItem("Contact us");
	JMenuItem aboutitem = new JMenuItem("About MOJO...");
	JMenuItem getstart = new JMenuItem("Getting Started");
	JMenu highlights = new JMenu("Highlights");
	Toc toc = new Toc();

	String SDCountyShp = "C://esri//MOJ20//SDHikes//shapefiles//sdcounty.shp";
	String SDHikesShp = "C://esri//MOJ20//SDHikes//shapefiles//SDHikes.shp";

	String datapathname = "";
	String legendname = "";
	ZoomPanToolBar zptb = new ZoomPanToolBar();
	static SelectionToolBar stb = new SelectionToolBar();
	JToolBar jtb = new JToolBar();
	ComponentListener complistener;
	JLabel statusLabel = new JLabel("status bar    LOC");
	static JLabel milesLabel = new JLabel("   DIST:  0 mi    ");
	static JLabel kmLabel = new JLabel("  0 km    ");
	java.text.DecimalFormat df = new java.text.DecimalFormat("0.000");
	JPanel myjp = new JPanel();
	JPanel myjp2 = new JPanel();
	JButton prtjb = new JButton(new ImageIcon("images/print.gif"));
	JButton addlyrjb = new JButton(new ImageIcon("images/addtheme.gif"));
	JButton ptrjb = new JButton(new ImageIcon("images/pointer.gif"));
	JButton distjb = new JButton(new ImageIcon("images/measure_1.gif"));
	JButton XYjb = new JButton("XY");
	JButton helpjb = new JButton(new ImageIcon("images/help2.gif"));
	JButton hotjb = new JButton(new ImageIcon("images/hotlink.gif"));
	Arrow arrow = new Arrow();
	static HelpTool helpTool = new HelpTool();
	ActionListener lis;
	ActionListener layerlis;
	ActionListener layercontrollis;
	ActionListener helplis;
	TocAdapter mytocadapter;
	Toolkit tk = Toolkit.getDefaultToolkit();
	Image bolt = tk.getImage("images/hotlink_32x32-32.gif");
	java.awt.Cursor boltCursor = tk.createCustomCursor(bolt, new java.awt.Point(6, 30), "bolt");
	MyPickAdapter picklis = new MyPickAdapter();
	Identify hotlink = new Identify();

	class MyPickAdapter implements PickListener {
		public void beginPick(PickEvent pe) {
		};
		public void endPick(PickEvent pe) {
		}
		public void foundData(PickEvent pe) {
			com.esri.mo2.data.feat.Cursor c = pe.getCursor();
			Feature f = null;
			if (c != null)
				f = (Feature)c.next();
			try {
				HotPick hotpick = new HotPick(f);
			} catch (Exception e) {
			}
		}
	};
	static Envelope env;
	public SDHikes() {
		super("San Diego County Hikes");
		helpToolOn = false;
		this.setSize(700, 450);
		zptb.setMap(map);
		stb.setMap(map);
		setJMenuBar(mbar);
		ActionListener lisZoom = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = false;
			}
		};
		ActionListener lisFullExt = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				fullMap = true;
			}
		};
		MouseAdapter mlLisZoom = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
					try {
						HelpDialog helpdialog = new HelpDialog((String)helpText.get(4));
						helpdialog.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};
		MouseAdapter mlLisZoomActive = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
					try {
						HelpDialog helpdialog = new HelpDialog((String)helpText.get(5));
						helpdialog.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};
		MouseAdapter mlLisMeasureTool = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
					try {
						HelpDialog helpdialog = new HelpDialog((String)helpText.get(6));
						helpdialog.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};
		MouseAdapter mlLisXY = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
					try {
						HelpDialog helpdialog = new HelpDialog((String)helpText.get(7));
						helpdialog.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};
		MouseAdapter mlHotLink = new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				if (SwingUtilities.isRightMouseButton(me) && helpToolOn) {
					try {
						HelpDialog helpdialog = new HelpDialog((String)helpText.get(8));
						helpdialog.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};

		JButton zoomInButton = (JButton) zptb.getActionComponent("ZoomIn");
		JButton zoomFullExtentButton = (JButton) zptb.getActionComponent("ZoomToFullExtent");
		JButton zoomToSelectedLayerButton = (JButton) zptb.getActionComponent("ZoomToSelectedLayer");
		zoomInButton.addActionListener(lisZoom);
		zoomInButton.addMouseListener(mlLisZoom);
		zoomFullExtentButton.addActionListener(lisFullExt);
	    zoomToSelectedLayerButton.addActionListener(lisZoom);
		zoomToSelectedLayerButton.addMouseListener(mlLisZoomActive);
		complistener = new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				if (fullMap) {
					map.setExtent(env);
					map.zoom(1.0);
					map.redraw();
				}
			}
		};
		addComponentListener(complistener);
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == prtjb || source instanceof JMenuItem) {
					com.esri.mo2.ui.bean.Print mapPrint = new com.esri.mo2.ui.bean.Print();
					mapPrint.setMap(map);
					mapPrint.doPrint();// prints the map
				} else if (source == ptrjb) {
					map.setSelectedTool(arrow);
					DistanceTool.resetDist();
				} else if (source == distjb) {
					DistanceTool distanceTool = new DistanceTool();
					map.setSelectedTool(distanceTool);
				} else if (source == XYjb) {
					try {
						AddXYtheme addXYtheme = new AddXYtheme();
						addXYtheme.setMap(map);
						addXYtheme.setVisible(false);
						map.redraw();
						//System.out.println("map is drawn");
					} catch (IOException e) {
					}
				} else if (source == helpjb) {
					helpToolOn = true;
					map.setSelectedTool(helpTool);
				} else if (source == hotjb) {
					hotlink.setCursor(boltCursor);
					map.setSelectedTool(hotlink);
				} else {
					try {
						AddLyrDialog aldlg = new AddLyrDialog();
						aldlg.setMap(map);
						aldlg.setVisible(true);
					} catch (IOException e) {
					}
				}
			}
		};
		layercontrollis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				String source = ae.getActionCommand();
				System.out.println(activeLayerIndex + " active index");
				if (source == "promote selected layer")
					map.getLayerset().moveLayer(activeLayerIndex,++activeLayerIndex);
				else
					map.getLayerset().moveLayer(activeLayerIndex,--activeLayerIndex);
				enableDisableButtons();
				map.redraw();
			}
		};
		helplis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if (arg == "About MOJO...") {
						AboutBox aboutbox = new AboutBox();
						aboutbox.setLogo(new ImageIcon("images/about.jpg"));
						aboutbox.setProductName("MOJO");
						aboutbox.setProductVersion("1.0");
						aboutbox.setVisible(true);
					} else if (arg == "Contact us") {
						try {
							String s = "\n\n\n\n              Any enquiries should be addressed to "
                            + "\n\n\n                         urvija.1983@gmail.com";
							HelpDialog helpdialog = new HelpDialog(s);
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Table of Contents") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(0));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Legend Editor") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(1));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Layer Control") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(2));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Measure Tool") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(6));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "XY Button") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(7));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "HotLink Tool") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(8));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Help Tool") {
						try {
							HelpDialog helpdialog = new HelpDialog((String)helpText.get(3));
							helpdialog.setVisible(true);
						} catch (IOException e) {
						}
					}
				}
			}
		};
		layerlis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source instanceof JMenuItem) {
					String arg = ae.getActionCommand();
					if (arg == "Add Layer") {
						try {
							AddLyrDialog aldlg = new AddLyrDialog();
							aldlg.setMap(map);
							aldlg.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Remove Layer") {
						try {
							com.esri.mo2.map.dpy.Layer dpylayer = legend.getLayer();
							map.getLayerset().removeLayer(dpylayer);
							map.redraw();
							remlyritem.setEnabled(false);
							propsitem.setEnabled(false);
							attribitem.setEnabled(false);
							promoteitem.setEnabled(false);
							demoteitem.setEnabled(false);
							stb.setSelectedLayer(null);
							zptb.setSelectedLayer(null);
						} catch (Exception e) {
						}
					} else if (arg == "Legend Editor") {
						LayerProperties lp = new LayerProperties();
						lp.setLegend(legend);
						lp.setSelectedTabIndex(0);
						lp.setVisible(true);
					} else if (arg == "open attribute table") {
						try {
							layer4 = legend.getLayer();
							AttrTab attrtab = new AttrTab();
							attrtab.setVisible(true);
						} catch (IOException ioe) {
						}
					}else if (arg == "Getting Started") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(9));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Table of Contents") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(0));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Legend_Editor") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(1));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Layer Control") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(2));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "Measure Tool") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(6));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "XY Button") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(7));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					} else if (arg == "HotLink Tool") {
						try {
							ReadmeDialog readme = new ReadmeDialog((String)helpText.get(8));
							readme.setVisible(true);
						} catch (IOException e) {
						}
					}else if (arg == "create layer from selection") {
						com.esri.mo2.map.draw.BaseSimpleRenderer sbr = new com.esri.mo2.map.draw.BaseSimpleRenderer();
						com.esri.mo2.map.draw.SimplePolygonSymbol simplepolysymbol =  new com.esri.mo2.map.draw.SimplePolygonSymbol();
						simplepolysymbol.setPaint(AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL, new java.awt.Color(255,255,0)));
						simplepolysymbol.setBoundary(true);
						layer4 = legend.getLayer();
						Legend legend3;
						FeatureLayer flayer2 = (FeatureLayer) layer4;
						System.out.println("has selected"+ flayer2.hasSelection());
						if (flayer2.hasSelection()) {
							SelectionSet selectset = flayer2.getSelectionSet();
							FeatureLayer selectedlayer = flayer2.createSelectionLayer(selectset);
							sbr.setLayer(selectedlayer);
							sbr.setSymbol(simplepolysymbol);
							selectedlayer.setRenderer(sbr);
							Layerset layerset = map.getLayerset();
							layerset.addLayer(selectedlayer);
							try{
								legend3 = toc.findLegend(selectedlayer);
							} catch(Exception e){
								Flash flash = new Flash(legend2);
								flash.start();
								map.redraw(); // necessary to see color immediately
							}
						}
						else{
							System.out.println("you must select some features from ");
							System.out.println("a layer before creating a new layer.");
						}
					}
				}
			}
		};
		toc.setMap(map);
		mytocadapter = new TocAdapter() {
			public void click(TocEvent e) {
				System.out.println(activeLayerIndex + "dex");
				legend = e.getLegend();
				activeLayer = legend.getLayer();
				stb.setSelectedLayer(activeLayer);
				zptb.setSelectedLayer(activeLayer);
				activeLayerIndex = map.getLayerset().indexOf(activeLayer);
				com.esri.mo2.map.dpy.Layer[] layers = { activeLayer };
				hotlink.setSelectedLayers(layers);// replaces setToc from MOJ10
				remlyritem.setEnabled(true);
				propsitem.setEnabled(true);
				attribitem.setEnabled(true);
				enableDisableButtons();
			}
		};
		map.addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseMoved(MouseEvent me) {
				com.esri.mo2.cs.geom.Point worldPoint = null;
				if (map.getLayerCount() > 0) {
					worldPoint = map.transformPixelToWorld(me.getX(), me.getY());
					String s = "X:" + df.format(worldPoint.getX()) + " " + "Y:" + df.format(worldPoint.getY());
					statusLabel.setText(s);
				} else
					statusLabel.setText("X:0.000 Y:0.000");
			}
		});
		toc.addTocListener(mytocadapter);
		remlyritem.setEnabled(false); // assume no layer initially selected
		propsitem.setEnabled(false);
		attribitem.setEnabled(false);
		promoteitem.setEnabled(false);
		demoteitem.setEnabled(false);
		printitem.addActionListener(lis);
		addlyritem.addActionListener(layerlis);
		remlyritem.addActionListener(layerlis);
		propsitem.addActionListener(layerlis);
		attribitem.addActionListener(layerlis);
		createlayeritem.addActionListener(layerlis);
		promoteitem.addActionListener(layercontrollis);
		demoteitem.addActionListener(layercontrollis);
		tocitem.addActionListener(helplis);
		legenditem.addActionListener(helplis);
		layercontrolitem.addActionListener(helplis);
		distancemeasureitem.addActionListener(helplis);
		XYitem.addActionListener(helplis);
		hotlinkitem.addActionListener(helplis);
		helptoolitem.addActionListener(helplis);
		contactitem.addActionListener(helplis);
		aboutitem.addActionListener(helplis);
		getstart.addActionListener(layerlis);
		tocitem1.addActionListener(layerlis);
		legenditem1.addActionListener(layerlis);
		layercontrolitem1.addActionListener(layerlis);
		distancemeasureitem1.addActionListener(layerlis);
		XYitem1.addActionListener(layerlis);
		hotlinkitem1.addActionListener(layerlis);
		file.add(addlyritem);
		file.add(printitem);
		file.add(remlyritem);
		file.add(propsitem);
		theme.add(attribitem);
		theme.add(createlayeritem);
		layercontrol.add(promoteitem);
		layercontrol.add(demoteitem);
		help.add(helptopics);
		helptopics.add(tocitem);
		helptopics.add(legenditem);
		helptopics.add(layercontrolitem);
		helptopics.add(distancemeasureitem);
		helptopics.add(XYitem);
		helptopics.add(hotlinkitem);
		help.add(helptoolitem);
		help.add(contactitem);
		help.add(aboutitem);
		readme.add(getstart);
		readme.add(highlights);
		highlights.add(tocitem1);
		highlights.add(legenditem1);
		highlights.add(layercontrolitem1);
		highlights.add(distancemeasureitem1);
		highlights.add(XYitem1);
		highlights.add(hotlinkitem1);
		mbar.add(file);
		mbar.add(theme);
		mbar.add(layercontrol);
		mbar.add(help);
		mbar.add(readme);
		prtjb.addActionListener(lis);
		prtjb.setToolTipText("print map");
		addlyrjb.addActionListener(lis);
		addlyrjb.setToolTipText("add layer");
		ptrjb.addActionListener(lis);
		distjb.addActionListener(lis);
		distjb.addMouseListener(mlLisMeasureTool);
		XYjb.addActionListener(lis);
		XYjb.addMouseListener(mlLisXY);
		helpjb.addActionListener(lis);
		XYjb.setToolTipText("add a layer of points from a file");
		prtjb.setToolTipText("pointer");
		distjb.setToolTipText("press-drag-release to measure a distance");
		helpjb.setToolTipText("left click here, then right click on a tool to learn about that tool");
		hotlink.addPickListener(picklis);
		hotlink.setPickWidth(15);// sets tolerance for hotlink clicks
		hotjb.addActionListener(lis);
		hotjb.addMouseListener(mlHotLink);
		hotjb.setToolTipText("hotlink tool--click somthing to get information about it.");
		jtb.add(prtjb);
		jtb.add(addlyrjb);
		jtb.add(ptrjb);
		jtb.add(distjb);
		jtb.add(XYjb);
		jtb.add(hotjb);
		jtb.add(helpjb);
		myjp.add(jtb);
		myjp.add(zptb);
		myjp.add(stb);
		myjp2.add(statusLabel);
		myjp2.add(milesLabel);
		myjp2.add(kmLabel);
		setuphelpText();
		getContentPane().add(map, BorderLayout.CENTER);
		getContentPane().add(myjp, BorderLayout.NORTH);
		getContentPane().add(myjp2, BorderLayout.SOUTH);
		addShapefileToMap(layer, SDCountyShp);
		addShapefileToMap(layer2,SDHikesShp);
		getContentPane().add(toc, BorderLayout.WEST);

		java.util.List list = toc.getAllLegends();
		com.esri.mo2.map.dpy.Layer lay1 = ((Legend)list.get(1)).getLayer();  // 1: sdcounty layer - polygon layer
		com.esri.mo2.map.dpy.Layer lay0 = ((Legend)list.get(0)).getLayer();	 // 0: SDHikes layer - point layer
		FeatureLayer flayer1 = (FeatureLayer)lay1;
		FeatureLayer flayer0 = (FeatureLayer)lay0;
		BaseSimpleRenderer bsr1 = (BaseSimpleRenderer)flayer1.getRenderer();
		BaseSimpleRenderer bsr0 = (BaseSimpleRenderer)flayer0.getRenderer();
		SimplePolygonSymbol sym1 = (SimplePolygonSymbol)bsr1.getSymbol();
		SimpleMarkerSymbol sym0 = (SimpleMarkerSymbol)bsr0.getSymbol();
		sym1.setPaint(AoFillStyle.getPaint(com.esri.mo2.map.draw.AoFillStyle.SOLID_FILL, new java.awt.Color(221,211,238)));

		sym0.setSymbolColor(new java.awt.Color(0, 0, 0));
		bsr1.setSymbol(sym1);
		bsr0.setSymbol(sym0);

		BaseSimpleLabelRenderer bslr1 = new BaseSimpleLabelRenderer();
		FeatureClass fclass0 = flayer0.getFeatureClass();
		String [] colnames = fclass0.getFields().getNames();
		//System.out.println(colnames[2]);  // state name field
		Fields fields = fclass0.getFields();
		Field field = fields.getField(2); //capture state_name field
		//System.out.println(field.getName());
		bslr1.setLabelField(field); //make state_name the label field
		flayer0.setLabelRenderer(bslr1);//add label renderer feature layer

		//toc.setCheckBoxVisibility(false);
		//JComponent jcb = lay0.getCheckBox();
		//jcb.setVisible(false);
	}
	private void addShapefileToMap(Layer layer, String s) {
		String datapath = s;
		layer.setDataset("0;" + datapath);
		map.add(layer);
	}
	private void setuphelpText() {
		String s0 = "  Table of Contents \n\n"
		+ "  The toc or table of contents, is to the left of the map. \n"
        + "  Each entry is called a 'legend' and represents a map 'layer' or \n"
        + "  a 'theme'.  If you click on a legend, that layer is called the \n"
        + "  active layer, or selected layer.  The display (rendering) property of active \n"
        + "  layer can be controlled using the Legend Editor, and the legends can be \n"
        + "  reordered using Layer Control.  Both Legend Editor and Layer Control \n"
        + "  are separate Help Topics.";
		helpText.add(s0);
		String s1 = "  Legend Editor \n\n"
		+ "  The Legend Editor is a menu item found under the File menu. \n"
        + "  When you select a layer in the table of content it activates Legend Editor menu \n"
        + "  item. Clicking on Legend Editor will open a window giving you the choices \n"
        + "  for displaying selected layer in different ways.  For example, you can control the color \n"
        + "  used to display the layer on the map, or you can display map using multiple colors etc.";
		helpText.add(s1);
		String s2 = "  Layer Control \n\n"
		+ "  Layer Control is a Menu on the menu bar.  If you have selected a \n"
        + "  layer by clicking on a legend in the toc (table of contents) to the left of \n"
        + "  the map, then the promote and demote tools will become visible.  Clicking on \n"
        + "  promote will raise the selected legend one position up, and \n"
        + "  clicking on demote will lower that legend one position down.";
		helpText.add(s2);
		String s3 = "  Help Tool \n\n"
		+ "  This tool is intended to help you to learn about certain other tools. \n"
        + "  You need to start with a left mouse button click on the Help Tool. \n"
        + "  And then RIGHT click on the tool you want to know about more.\n"
        + "  Click on the Arrow tool to stop using the help tool.";
		helpText.add(s3);
		String s4 = "  Zoom In Tool \n\n"
		+ "  If you click on the Zoom In tool, and then click on the map, you \n"
        + "  will see a part of the map in greater detail.  You can zoom in multiple times. \n"
        + "  You can also sketch a rectangular part of the map, and zoom to that. You can \n"
        + "  undo a Zoom In with clicking a Zoom Out or a Zoom to Full Extent button";
		helpText.add(s4);
		String s5 = "  Zoom to Active Layer Tool \n\n"
		+ "  To use Zoom to Active Layer tool, you must select a layer first .\n"
        + "  If you then click on Zoom to Active Layer, you will be shown enough of \n"
        + "  the full map to see all of the features in the layer you select.";
		helpText.add(s5);
		String s6 = "  Distance Measure Tool \n\n"
		+ "  This is a Distance Measure Tool. It measures distance \n"
		+ "  between two selected points. You need to click on any point \n"
		+ "  on the map and drag the mouse and release. This will give \n"
		+ "  the distance between the mouse click and release.";
        helpText.add(s6);
        String s7 = "  XY Button Tool \n\n"
		+ "  This XY tool creates a new layer from available CSV \n"
        + "  file. You need to click on the XY button and navigate to the \n"
        + "  folder containing CSV file. Add the CSV file and it will \n"
        + "  create a layer on the map.";
		helpText.add(s7);
		String s8 = "  Hotlink Tool \n\n"
		+ "  Hotlink tool gives the information of the selected layer (point layer) \n"
		+ "  For example, if you click on the SDHikes layer and then select hotlink bolt and then \n"
		+ "  click on say, Mount Woodson point then it will give the information like webiste link, yelp \n"
		+ "  link, image of the hike etc" ;
		helpText.add(s8);
		String s9 = "  This application is a GIS tool showcasing major Hikes in San Diego county. \n"
		+ "  User can use hotlink tool or identify tool to get more information on a particular \n"
		+ "  hike and see more details like location of the hike and elevation gain, hike time, \n"
		+ "  image of the hike etc. \n\n"
		+ "  We have also provided website link and yelp link for respectiive hikes \n"
		+ "  so that user can get more information. \n\n"
		+ "  This application has been developed to implement interactive environment. \n"
		+ "  Also user(s) have flexibility to customize the application to suit their \n"
		+ "  individual interest and requirements. \n\n"
		+ "  Thank you for using the application.";
		helpText.add(s9);
	}
	public static void main(String[] args) {
		SDHikes hike = new SDHikes();
		hike.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.out.println("Thank you. Have a Happy Holiday");
				System.exit(0);
			}
		});
		hike.setVisible(true);
		env = map.getExtent(); 	// if i maximize the screensize, it resizes the layers on the screen at that time.
	}
	private void enableDisableButtons() {
		int layerCount = map.getLayerset().getSize();
		if (layerCount < 2) {
			promoteitem.setEnabled(false);
			demoteitem.setEnabled(false);
		} else if (activeLayerIndex == 0) {
			demoteitem.setEnabled(false);
			promoteitem.setEnabled(true);
		} else if (activeLayerIndex == layerCount - 1) {
			promoteitem.setEnabled(false);
			demoteitem.setEnabled(true);
		} else {
			promoteitem.setEnabled(true);
			demoteitem.setEnabled(true);
		}
	}
	private ArrayList helpText = new ArrayList(3);
}

// following is an Add Layer dialog window
class AddLyrDialog extends JDialog {
	Map map;
	ActionListener lis;
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JPanel panel1 = new JPanel();
	com.esri.mo2.ui.bean.CustomDatasetEditor cus = new com.esri.mo2.ui.bean.CustomDatasetEditor();
	AddLyrDialog() throws IOException {
		setBounds(50, 50, 520, 430);
		setTitle("Select a theme/layer");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		lis = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				Object source = ae.getSource();
				if (source == cancel)
					setVisible(false);
				else {
				try {
					setVisible(false);
					map.getLayerset().addLayer(cus.getLayer());
					map.redraw();
					if (SDHikes.stb.getSelectedLayers() != null)
						SDHikes.promoteitem.setEnabled(true);
					} catch (IOException e) {
					}
				}
			}
		};
		ok.addActionListener(lis);
		cancel.addActionListener(lis);
		getContentPane().add(cus, BorderLayout.CENTER);
		panel1.add(ok);
		panel1.add(cancel);
		getContentPane().add(panel1, BorderLayout.SOUTH);
	}
	public void setMap(com.esri.mo2.ui.bean.Map map1) {
		map = map1;
	}
}

class AddXYtheme extends JDialog {
	Map map;
	Vector s1 = new Vector();
	Vector s2 = new Vector();
	Vector s3 = new Vector();
	Vector s4 = new Vector();
	Vector s5 = new Vector();
	Vector s6 = new Vector();
	Vector s7 = new Vector();
	Vector s8 = new Vector();
	Vector s9 = new Vector();
	Vector s10 = new Vector();

	JFileChooser jfc = new JFileChooser();
	BasePointsArray bpa = new BasePointsArray();
	AddXYtheme() throws IOException {
		setBounds(50, 50, 520, 430);
		jfc.showOpenDialog(this);
		if (jfc.getSelectedFile() != null) {
		try {
			File file = jfc.getSelectedFile();
			FileReader fred = new FileReader(file);
			BufferedReader in = new BufferedReader(fred);
			String s; // = in.readLine();
			double x, y;
			int n = 0;
			while ((s = in.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(s, ",");
				x = Double.parseDouble(st.nextToken());
				y = Double.parseDouble(st.nextToken());
				bpa.insertPoint(n++, new com.esri.mo2.cs.geom.Point(x, y));
				s1.addElement(st.nextToken());
				s2.addElement(st.nextToken());
				s3.addElement(st.nextToken());
				s4.addElement(st.nextToken());
				s5.addElement(st.nextToken());
				s6.addElement(st.nextToken());
				s7.addElement(st.nextToken());
				s8.addElement(st.nextToken());
				s9.addElement(st.nextToken());
				s10.addElement(st.nextToken());
			}
		} catch (IOException e) {
			}

		XYfeatureLayer xyfl = new XYfeatureLayer(bpa, map, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);
		xyfl.setVisible(true);
		map = SDHikes.map;
		map.getLayerset().addLayer(xyfl);
		map.redraw();

		CreateXYShapeDialog cxysd = new CreateXYShapeDialog(xyfl);
		cxysd.setVisible(true);
		}
	}
	public void setMap(com.esri.mo2.ui.bean.Map map1) {
		map = map1;
	}
}

class XYfeatureLayer extends BaseFeatureLayer {
	BaseFields fields;
	private java.util.Vector featureVector;
	public XYfeatureLayer(BasePointsArray bpa, Map map, Vector s1, Vector s2, Vector s3, Vector s4, Vector s5, Vector s6, Vector s7, Vector s8, Vector s9, Vector s10) {

		createFeaturesAndFields(bpa, map, s1, s2, s3, s4, s5, s6, s7, s8, s9, s10);

		BaseFeatureClass bfc = getFeatureClass("SDHikes", bpa);
		setFeatureClass(bfc);
		BaseSimpleRenderer srd = new BaseSimpleRenderer();
		TrueTypeMarkerSymbol ttm = new TrueTypeMarkerSymbol();
		ttm.setFont(new Font("AEZ camping", Font.PLAIN, 35));
		ttm.setColor(new Color(0, 0, 0));
		ttm.setCharacter("97"); //hiking font
		//SimpleMarkerSymbol sms = new SimpleMarkerSymbol();
		//sms.setType(SimpleMarkerSymbol.CIRCLE_MARKER);
		//sms.setSymbolColor(new Color(255, 0, 0));
		//sms.setWidth(5);
		//srd.setSymbol(sms);
		srd.setSymbol(ttm);
		setRenderer(srd);
		// without setting layer capabilities, the points will not
		// display (but the toc entry will still appear)
		XYLayerCapabilities lc = new XYLayerCapabilities();
		setCapabilities(lc);
	}
	private void createFeaturesAndFields(BasePointsArray bpa, Map map, Vector s1, Vector s2, Vector s3, Vector s4, Vector s5, Vector s6, Vector s7, Vector s8, Vector s9, Vector s10) {
	    //System.out.println("In createFeaturesAndFields");
		featureVector = new java.util.Vector();
		fields = new BaseFields();
		createDbfFields();
		int j = 0;
		//System.out.println("BPA Size" + bpa.size());
		for (int i = 0; i < bpa.size(); i++) {
			//System.out.println("Inside loop::" + i + ":: and j is ::" + j);
			BaseFeature feature = new BaseFeature(); // feature is a row
			feature.setFields(fields);
			com.esri.mo2.cs.geom.Point p = new com.esri.mo2.cs.geom.Point(bpa.getPoint(i));
			feature.setValue(0, p); // p = gets sequence numbers like 0,1,2,3....
			feature.setValue(1, new Integer(0)); // point data
			feature.setValue(1, s1.elementAt(j));
			feature.setValue(2, s2.elementAt(j));
			feature.setValue(3, s3.elementAt(j));
			feature.setValue(4, s4.elementAt(j));
			feature.setValue(5, s5.elementAt(j));
			feature.setValue(6, s6.elementAt(j));
			feature.setValue(7, s7.elementAt(j));
			feature.setValue(8, s8.elementAt(j));
			feature.setValue(9, s9.elementAt(j));
			feature.setValue(10, s10.elementAt(j));
			feature.setDataID(new BaseDataID("SDHikes", i));
			featureVector.addElement(feature);
			j = j + 1;
		}
	}
	private void createDbfFields() {
		System.out.println("Inside createDbfFileds");
		fields.addField(new BaseField("#", Field.ESRI_SHAPE, 0, 0));
		fields.addField(new BaseField("HikeID", java.sql.Types.VARCHAR, 10,0));
		fields.addField(new BaseField("HikeName", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("Distance", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("Elevation", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("Difficulty", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("Location", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("HikeTime", java.sql.Types.VARCHAR, 50, 0));
		fields.addField(new BaseField("Image", java.sql.Types.VARCHAR, 100, 0));
		fields.addField(new BaseField("Website", java.sql.Types.VARCHAR, 100, 0));
		fields.addField(new BaseField("YelpLink", java.sql.Types.VARCHAR, 100,0));
	}
	public BaseFeatureClass getFeatureClass(String name, BasePointsArray bpa) {
		com.esri.mo2.map.mem.MemoryFeatureClass featClass = null;
		try {
			//System.out.println("inside base feature class");
			featClass = new com.esri.mo2.map.mem.MemoryFeatureClass(MapDataset.POINT, fields);
		} catch (IllegalArgumentException iae) {}
		featClass.setName(name);
		for (int i = 0; i < bpa.size(); i++) {
			featClass.addFeature((Feature) featureVector.elementAt(i));
		}
		return featClass;
	}
	private final class XYLayerCapabilities extends
    com.esri.mo2.map.dpy.LayerCapabilities {
		XYLayerCapabilities() {
			for (int i = 0; i < this.size(); i++) {
				setAvailable(this.getCapabilityName(i), true);
				setEnablingAllowed(this.getCapabilityName(i), true);
				getCapability(i).setEnabled(true);
			}
		}
	}
}

class AttrTab extends JDialog {
	JPanel panel1 = new JPanel();
	com.esri.mo2.map.dpy.Layer layer = SDHikes.layer4;
	JTable jtable = new JTable(new MyTableModel());
	JScrollPane scroll = new JScrollPane(jtable);
	public AttrTab() throws IOException {
		setBounds(70, 70, 450, 350);
		setTitle("Attribute Table");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		TableColumn tc = null;
		int numCols = jtable.getColumnCount();
		for (int j = 0; j < numCols; j++) {
			tc = jtable.getColumnModel().getColumn(j);
			tc.setMinWidth(70);
		}
		getContentPane().add(scroll, BorderLayout.CENTER);
	}
}

class MyTableModel extends AbstractTableModel {
	com.esri.mo2.map.dpy.Layer layer = SDHikes.layer4;
	MyTableModel() {
		qfilter.setSubFields(fields);
		com.esri.mo2.data.feat.Cursor cursor = flayer.search(qfilter);
		while (cursor.hasMore()) {
			ArrayList inner = new ArrayList();
			Feature f = (com.esri.mo2.data.feat.Feature) cursor.next();
			inner.add(0, String.valueOf(row));
			for (int j = 1; j < fields.getNumFields(); j++) {
				inner.add(f.getValue(j).toString());
			}
			data.add(inner);
			row++;
		}
	}
	FeatureLayer flayer = (FeatureLayer) layer;
	FeatureClass fclass = flayer.getFeatureClass();
	String columnNames[] = fclass.getFields().getNames();
	ArrayList data = new ArrayList();
	int row = 0;
	int col = 0;
	BaseQueryFilter qfilter = new BaseQueryFilter();
	Fields fields = fclass.getFields();
	public int getColumnCount() {
		return fclass.getFields().getNumFields();
	}
	public int getRowCount() {
		return data.size();
	}
	public String getColumnName(int colIndx) {
		return columnNames[colIndx];
	}
	public Object getValueAt(int row, int col) {
		ArrayList temp = new ArrayList();
		temp = (ArrayList) data.get(row);
		return temp.get(col);
	}
}

class CreateShapeDialog extends JDialog {
	String name = "";
	String path = "";
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JTextField nameField = new JTextField("enter layer name here, then hit ENTER", 25);
	com.esri.mo2.map.dpy.FeatureLayer selectedlayer;
	ActionListener lis = new ActionListener() {
    public void actionPerformed(ActionEvent ae) {
    	Object o = ae.getSource();
    	//System.out.println("inside create shape dialog");
		if (o == nameField) {
        	name = nameField.getText().trim();
        	path = ((ShapefileFolder) (SDHikes.layer4.getLayerSource())).getPath();
        	System.out.println(path + "    " + name);
    	} else if (o == cancel)
    	    setVisible(false);
    	    else {
            try {
                ShapefileWriter.writeFeatureLayer(selectedlayer, path, name, 2);
            } catch (Exception e) {
                System.out.println("write error");
            }
            setVisible(false);
        	}//else
		}//elseif
	};
	JPanel panel1 = new JPanel();
	JLabel centerlabel = new JLabel();
	CreateShapeDialog(com.esri.mo2.map.dpy.FeatureLayer layer5) {
		selectedlayer = layer5;
		setBounds(40, 350, 450, 150);
		setTitle("Create new shapefile?");
		addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		setVisible(false);
		}
	});
	nameField.addActionListener(lis);
	ok.addActionListener(lis);
	cancel.addActionListener(lis);
	String s = "<HTML> To make a new shapefile from the new layer, enter<BR>"
	+ "the new name you want for the layer and click OK.<BR>"
	+ "You can then add it to the map in the usual way.<BR>"
	+ "Click ENTER after replacing the text with your layer name";
	centerlabel.setHorizontalAlignment(JLabel.CENTER);
	centerlabel.setText(s);
	getContentPane().add(centerlabel, BorderLayout.CENTER);
	panel1.add(nameField);
	panel1.add(ok);
	panel1.add(cancel);
	getContentPane().add(panel1, BorderLayout.SOUTH);
	}
}

class CreateXYShapeDialog extends JDialog {
	String name = "";
	String path = "";
	JButton ok = new JButton("OK");
	JButton cancel = new JButton("Cancel");
	JTextField nameField = new JTextField("Enter layer name here, then hit ENTER", 35);
	JTextField pathField = new JTextField("Enter full path name here, then hit ENTER", 35);
	com.esri.mo2.map.dpy.FeatureLayer XYlayer;
	ActionListener lis = new ActionListener() {
    public void actionPerformed(ActionEvent ae) {
    	Object o = ae.getSource();
    	if (o == pathField) {
        	path = pathField.getText().trim();
        	//System.out.println(path);
    	} else if (o == nameField) {
        	name = nameField.getText().trim();
        	//System.out.println(path + "    " + name);
    	} else if (o == cancel)
    	    setVisible(false);
        	else { // ok button clicked
            try {
           		 System.out.println("Printing: " + XYlayer.getName() + " Path: "+ path + " Name: " +name);
				 ShapefileWriter.writeFeatureLayer(XYlayer,path,name);
		    } catch (Exception e) {
                System.out.println("write error");
            }
            setVisible(false);
        	}//else
		}//elseif
	};
	JPanel panel1 = new JPanel();
	JPanel panel2 = new JPanel();
	JLabel centerlabel = new JLabel();

	CreateXYShapeDialog(com.esri.mo2.map.dpy.FeatureLayer layer5) {
		XYlayer = layer5;
		setBounds(40, 250, 600, 300);
		setTitle("Create new shapefile?");
		addWindowListener(new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			setVisible(false);
		}
	});
	nameField.addActionListener(lis);
	pathField.addActionListener(lis);
	ok.addActionListener(lis);
	cancel.addActionListener(lis);
	String s = "<HTML> To make a new shapefile from the new layer, enter<BR>"
	+ "the new name you want for the layer and click ENTER.<BR>"
	+ "then enter a path to the folder you want to store <BR>"
	+ "the new shapefile and click ENTER once again <BR>"
	+ "For example type C:\\esri\\MOJ20\\MyApplication <BR>"
	+ "You can then add the new shapefile to the map in the usual way.<BR>"
	+ "Don't forget to Click ENTER after replacing the text with your layer name";
	centerlabel.setHorizontalAlignment(JLabel.CENTER);
	centerlabel.setText(s);
	panel1.add(centerlabel);
	panel1.add(nameField);
	panel1.add(pathField);
	panel2.add(ok);
	panel2.add(cancel);
	getContentPane().add(panel2, BorderLayout.SOUTH);
	getContentPane().add(panel1, BorderLayout.CENTER);
	}//actionlistener lis
}

class HelpDialog extends JDialog {
	JTextArea helptextarea;
	public HelpDialog(String inputText) throws IOException {
		setBounds(70, 70, 460, 250);
		setTitle("Help");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		helptextarea = new JTextArea(inputText, 7, 40);
		JScrollPane scrollpane = new JScrollPane(helptextarea);
		helptextarea.setEditable(false);
		getContentPane().add(scrollpane, "Center");
	}
}

class ReadmeDialog extends JDialog {
	JTextArea readmetextarea;
	public ReadmeDialog(String inputText) throws IOException {
		setBounds(70, 70, 500, 300);
		setTitle("Getting Started");
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		readmetextarea = new JTextArea(inputText, 7, 40);
		JScrollPane scrollpane = new JScrollPane(readmetextarea);
		readmetextarea.setEditable(false);
		getContentPane().add(scrollpane, "Center");
	}
}

class HelpTool extends Tool {
}

class Arrow extends Tool {
	public void mouseClicked(MouseEvent me){
	}
}

class Flash extends Thread {
	Legend legendflash;
	Flash(Legend legendin) {
		legendflash = legendin;
	}
	public void run() {
		for (int i = 0; i < 12; i++) {
			try {
				Thread.sleep(500);
				legendflash.toggleSelected();
			} catch (Exception e) {
			}
		}
	}
}

class DistanceTool extends DragTool {
	int startx, starty, endx, endy;
	com.esri.mo2.cs.geom.Point initPoint, endPoint, currPoint;
	double distance;
	public static void resetDist() {
		SDHikes.milesLabel.setText("DIST   0 mi   ");
		SDHikes.kmLabel.setText("   0 km    ");
	}
	public void mousePressed(MouseEvent me) {
		startx = me.getX();
		starty = me.getY();
		initPoint = SDHikes.map.transformPixelToWorld(me.getX(), me.getY());
	}
	public void mouseReleased(MouseEvent me) {
		endx = me.getX();
		endy = me.getY();
		endPoint = SDHikes.map.transformPixelToWorld(me.getX(), me.getY());
		distance = (69.44 / (2 * Math.PI)) * 360
        		* Math.acos(Math.sin(initPoint.y * 2 * Math.PI / 360)
                * Math.sin(endPoint.y * 2 * Math.PI / 360)
                + Math.cos(initPoint.y * 2 * Math.PI / 360)
                * Math.cos(endPoint.y * 2 * Math.PI / 360)
                * (Math.abs(initPoint.x - endPoint.x) < 180 ? Math
                .cos((initPoint.x - endPoint.x) * 2 * Math.PI / 360) : Math.cos((360 - Math.abs(initPoint.x - endPoint.x))
                * 2 * Math.PI /360)));
		System.out.println(distance);
		SDHikes.milesLabel.setText("DIST: " + new Float((float) distance).toString() + " mi  ");
		SDHikes.kmLabel.setText(new Float((float) (distance * 1.6093)).toString() + " km");
		Graphics g = super.getGraphics();
		g.setColor(Color.blue);
		g.drawLine(startx,starty,endx,endy);
	}
	public void cancel() {
	};
}

// HotPick tool. It displays a custom cursor.
// When user clicks on a point in a point layer, it displays basic info and three buttons with names
// "Click here to see the Image", "Click here for WebSite" and "Click here for Yelp".
// So when the user clicks on any of the buttons, it performs the action accordingly.
// Also, user can either click on the button or there is an option for them for a keyboard shortcut key.
// e.g Alt + W will open up the website in google chrome.
// I have also provided ToolTipText so user can find out what the button does and what is the shortcut key for that button.

class HotPick extends JDialog {
	JPanel infoPanel = new JPanel();
	String os_type = null;
	JPanel btnPanel = new JPanel();
	JLabel image = new JLabel();
	ActionListener hotpickbtnlis;

	HotPick(Feature f) throws IOException {
		if (os_type == null) {
			os_type = System.getProperty("os.name");
		}
		if (f.getDataID().getSource().trim().equalsIgnoreCase("SDHikes")) {
			JTextArea location = new JTextArea();
        	location.setText("Location : " + (String)f.getValue(6));
        	location.setLineWrap(true);
        	location.setWrapStyleWord(true);
        	location.setOpaque(false);
        	location.setEditable(false);

			JTextArea distance = new JTextArea();
			distance.setText("Hike Distance : " + (String)f.getValue(3));
        	distance.setLineWrap(true);
        	distance.setWrapStyleWord(true);
        	distance.setOpaque(false);
        	distance.setEditable(false);

			JTextArea elevation = new JTextArea();
        	elevation.setText("Elevation : " + (String)f.getValue(4));
        	elevation.setLineWrap(true);
        	elevation.setWrapStyleWord(true);
        	elevation.setOpaque(false);
        	elevation.setEditable(false);

            this.setTitle((String) f.getValue(2));

			final JButton websitelinkbtn = new JButton("Click here for WebSite", new ImageIcon("images/website.jpg"));
			websitelinkbtn.setMnemonic(KeyEvent.VK_W);
			websitelinkbtn.setToolTipText("Press Alt + W for keyboard Shortcut");
			final JButton yelplinkbtn = new JButton("Click here for Yelp", new ImageIcon("images/yelp.gif"));
			yelplinkbtn.setMnemonic(KeyEvent.VK_Y);
			yelplinkbtn.setToolTipText("Press Alt + Y for keyboard Shortcut");
			final JButton imagebtn = new JButton("Click here to see the Image", new ImageIcon("images/image.png"));
			imagebtn.setMnemonic(KeyEvent.VK_M);
			imagebtn.setToolTipText("Press Alt + M for keyboard Shortcut");

			final String webAddr = (String) f.getValue(9);
			final String yelpAddr = (String) f.getValue(10);
			final String imageName = (String) f.getValue(8);

			hotpickbtnlis = new ActionListener() {
				public void actionPerformed(ActionEvent ae) {
					Object source = ae.getSource();
					try {
						if (source == websitelinkbtn) {
						try {
							if (os_type.indexOf("Windows") != -1) {
								Runtime.getRuntime().exec("C:\\Program Files\\Google\\Chrome\\Application\\CHROME.EXE " + webAddr);
							} //if
							else if (os_type.indexOf("Mac") != -1) {
								Runtime.getRuntime().exec(new String[] { "open", "-a", "Safari", webAddr });
							}//elseif
						}//try
						catch (Exception ex) {
							System.out.println("cannot execute command. " + ex);
						}//catch
						} //if
						else if (source == yelplinkbtn) {
						try {
							// String command = googlePath[i];
							if (os_type.indexOf("Windows") != -1) {
								Runtime.getRuntime().exec("C:\\Program Files\\Google\\Chrome\\Application\\CHROME.EXE " + yelpAddr);
							} //if
							else if (os_type.indexOf("Mac") != -1) {
								Runtime.getRuntime().exec(new String[] { "open", "-a", "Safari", yelpAddr });
							}//elseif
							}//try
						catch (Exception ex) {
							System.out.println("cannot execute command. " + ex);
						}//catch
						} //elseif
						else if (source == imagebtn) {
						try {
							ImageIcon hikeImg = new ImageIcon(imageName);
							JPanel img0 = new JPanel();
							JLabel img1 = new JLabel();
							getContentPane().add(img0);
							img0.add(img1);
							img1.setBounds(0,0,380,380);
							img1.setIcon(hikeImg);
							img1.setVisible(true);
							img1.repaint();
							pack();
							}//try
							catch (Exception ex) {
								System.out.println("cannot execute command. " + ex);
							}//catch
						} //elseif

					} catch (Exception e) {
						System.out.println("Error:::");
					}
				}
			};

			websitelinkbtn.addActionListener(hotpickbtnlis);
			yelplinkbtn.addActionListener(hotpickbtnlis);
			imagebtn.addActionListener(hotpickbtnlis);

			infoPanel.setLayout(new GridLayout(3, 1));
			infoPanel.add(location);
			infoPanel.add(distance);
			infoPanel.add(elevation);

			btnPanel.setLayout(new GridLayout(3, 1));
            btnPanel.add(imagebtn);
            btnPanel.add(websitelinkbtn);
			btnPanel.add(yelplinkbtn);

			this.getContentPane().add(infoPanel, BorderLayout.NORTH);
			this.getContentPane().add(btnPanel, BorderLayout.SOUTH);
			this.setBounds(50,50,500, 300);
			this.setResizable(false);
			setVisible(true);
		}
	}
}
