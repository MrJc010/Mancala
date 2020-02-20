import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.*;
import javax.swing.*;

/**
 * A dialog to allow player to set initial stone count
 * and a board layout to be used for game play
 * @author Team7.
 *
 */
public class StartView extends JDialog
{
	private static final long serialVersionUID = 1L;
	private Container frame;
	private int stoneCount;
	private Layout[] layouts;
	private Layout layout;
	private int width = 380;
	private int height = 250;

	/**
	 * Creates a popup dialog to choose number of stones and the layout
	 * @param owner a frame for the dialog
	 */
	public StartView(Frame f, Layout[] layouts)
	{
		super(f, true);
		stoneCount = 3;
		this.layouts = layouts;
		layout = layouts[0];

		frame = getContentPane();
		setSize(width,height);
		JPanel stonePanel = new JPanel();
		JPanel layoutPanel = new JPanel();
		JLabel pic = new JLabel();
		
		ImageIcon logo = new ImageIcon("resources/logo.png");
		ImageIcon icon = new ImageIcon("resources/icon.png");
		JLabel lo = new JLabel(logo);
		JLabel ic = new JLabel(icon);

		JLabel chooseStones = new JLabel("Initial stone count: ");

		JRadioButton three = new JRadioButton("3", true);
		JRadioButton four = new JRadioButton("4");
		ButtonGroup stoneGroup = new ButtonGroup();

		stonePanel.add(three);
		stoneGroup.add(three);
		stonePanel.add(four);
		stoneGroup.add(four);

		JLabel chooseLayout = new JLabel("Choose a layout: ");

		JRadioButton[] layoutButtons  = new JRadioButton[layouts.length];
		ButtonGroup layoutGroup = new ButtonGroup();
		for (int i = 0; i < layouts.length; i++)
		{
			layoutButtons[i] = new JRadioButton(layouts[i].getName(), i == 0);
			layoutPanel.add(layoutButtons[i]);
			layoutGroup.add(layoutButtons[i]);
			layoutButtons[i].addActionListener(setLayout(i));
		}

		JButton start = new JButton("Start Game");
		start.setBorderPainted(false);
		JButton quit = new JButton("Play Later");
		quit.setBorderPainted(false);
		
		three.addActionListener(setStoneCount(3));
		four.addActionListener(setStoneCount(4));
		start.addActionListener(event ->
			{
				frame.setVisible(false);
				dispose();
		});
		quit.addActionListener(event -> System.exit(0));
		Box box = Box.createHorizontalBox();
		box.add(Box.createHorizontalStrut(135));
		box.add(pic);
		// stone choices
		Box box1 = Box.createHorizontalBox();
		box1.add(Box.createHorizontalStrut(135));
		box1.add(stonePanel);

		// layout choices
		Box box2 = Box.createVerticalBox();
		box2.add(layoutPanel);

		// start button
		//quit button
		Box box3 = Box.createHorizontalBox();
		box3.add(Box.createVerticalStrut(100));
		box3.add(Box.createHorizontalStrut(150));
		JPanel button = new JPanel(new FlowLayout());
		button.add(quit);
		button.add(start);
		box3.add(button);

		frame.setLayout(new FlowLayout(FlowLayout.LEFT));
		frame.add(lo);
		frame.add(ic);
		frame.add(chooseStones);
		frame.add(box1);
		frame.add(chooseLayout);
		frame.add(box2);
		frame.add(box3);
		setResizable(false);
	}

	/**
	 * Allows the dialog to popup and be visible
	 * @return a string representation of the popup dialog
	 */
	public String showDialog()
	{
		setVisible(true);
		return "Startup Dialog";
	}

	/**
	 * A listener to choose the stone count
	 * @param stoneNumber the initial stone count
	 * @return an anonymous ActionListener class
	 */
	public ActionListener setStoneCount(final int stoneNumber)
	{
		return new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				stoneCount = stoneNumber;
			}
		};
	}

	/**
	 * A listener to choose the layout
	 * @param layoutNumber the layout number
	 * @return an anonymous ActionListener class
	 */
	public ActionListener setLayout(final int layoutNumber)
	{
		return new
				ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				layout = layouts[layoutNumber];
			}
		};
	}

	/**
	 * Gets the stone count
	 * @return the stone count
	 */
	public int stoneNumber()
	{
		return stoneCount;
	}

	/**
	 * Gets the layout selected in the dialog
	 * @return BoardLayout to use
	 */
	public Layout getSelectedLayout()
	{
		return layout;
	}
}
