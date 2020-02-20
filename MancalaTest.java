/**
 * The class containing the main method.
 * @author Team7
 */
public class MancalaTest
{
	public static void main(String[] args)
	{
		Layout[] layouts = 
			{
				new ClassicLayout(2, 6),
	 			new PokemonLayout(2, 6)
			};

		Controller g = new Controller(layouts);
	}
}
