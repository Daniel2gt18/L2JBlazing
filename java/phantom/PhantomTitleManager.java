package phantom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.l2jmega.commons.random.Rnd;

import com.l2jmega.gameserver.data.PlayerNameTable;

public enum PhantomTitleManager
{
	INSTANCE;
	
	public static final Logger _log = Logger.getLogger(PhantomTitleManager.class.getName());
	private List<String> _fakePlayerTitles;
	
	public void initialise()
	{
		loadWordlist();
	}
	
	public String getRandomAvailableTitle()
	{
		String title = getRandomTitlesFromWordlist();
		
		while (titleAlreadyExists(title))
		{
			title = getRandomTitlesFromWordlist();
		}
		
		return title;
	}
	
	private String getRandomTitlesFromWordlist()
	{
		return _fakePlayerTitles.get(Rnd.get(0, _fakePlayerTitles.size() - 1));
	}
	
	public List<String> getFakePlayerTitles()
	{
		return _fakePlayerTitles;
	}
	
	private void loadWordlist()
	{
		try (LineNumberReader lnr = new LineNumberReader(new BufferedReader(new FileReader(new File("./config/custom/phantom/phantom_title.ini"))));)
		{
			String line;
			ArrayList<String> playersList = new ArrayList<>();
			while ((line = lnr.readLine()) != null)
			{
				if (line.trim().length() == 0 || line.startsWith("#"))
					continue;
				playersList.add(line);
			}
			_fakePlayerTitles = playersList;
			_log.log(Level.INFO, String.format("Loaded %s fake player titles.", _fakePlayerTitles.size()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static boolean titleAlreadyExists(String name)
	{
		return PlayerNameTable.getInstance().getPlayerObjectId(name) > 0;
	}
}
