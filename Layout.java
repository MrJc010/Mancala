import java.awt.*;
import java.awt.geom.*;

/**
 * This is the abstract class for Layout
 * This project will have two layouts: simple and fancy
 * The two layout will implements this abstract class
 */
public abstract class Layout
{
	
	protected Rectangle2D.Double[][] pitRects;
	protected int width;
	protected int height;
	/**
	 * Constructor that creates the layout of the game
	 * @param player the number of players
	 * @param boardLength the number of pits
	 */
	public Layout(int player, int boardLength)
	{
		pitRects = new Rectangle2D.Double[player][boardLength];
	}

	/**
	 * Abstract method that redraws the board
	 * This method will be implemented in subclass
	 * @param g Graphics element
	 * @param b the board object for drawing images
	 * @param pits the pits
	 * @param mancalas the mancalas
	 */
	public abstract void redraw(Graphics g, Board b, Pit[][] pits,
			int[] mancalas);

	/**
	 * Abstract method to get the Layout name
	 * We will using this method to make a label later
	 * @return The string to use as the name
	 */
	public abstract String getName();

	/**
	 * Gets the bounding boxes for the pits
	 * @return the name of the layout
	 */
	public Rectangle2D.Double[][] getPitRects() 
	{ 
		return pitRects; 
	}

	/**
	 * Sets the size of the board
	 * @param w the width
	 * @param h the height
	 */
	public void setSize(int w, int h)
	{
		width = w;
		height = h;
	}
}

