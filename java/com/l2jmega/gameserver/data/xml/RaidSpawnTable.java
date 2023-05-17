package com.l2jmega.gameserver.data.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import com.l2jmega.gameserver.xmlfactory.XMLDocumentFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class RaidSpawnTable {
  private static Logger _log = Logger.getLogger(RaidSpawnTable.class.getName());
  
  private static final Map<Integer, List<String>> _map = new HashMap<>();
  
  public static RaidSpawnTable getInstance() {
    return SingletonHolder._instance;
  }
  
  protected RaidSpawnTable() {
    try {
      File f = new File("./data/xml/raidspawn.xml");
      Document doc = XMLDocumentFactory.getInstance().loadDocument(f);
      for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
        if ("list".equalsIgnoreCase(n.getNodeName()))
          for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
            List<String> raidSpawn = new ArrayList<>();
            if ("boss".equalsIgnoreCase(d.getNodeName())) {
              NamedNodeMap attrs = d.getAttributes();
              int id = Integer.valueOf(attrs.getNamedItem("id").getNodeValue()).intValue();
              raidSpawn = parseList(attrs.getNamedItem("spawn").getNodeValue());
              _map.put(Integer.valueOf(id), raidSpawn);
            } 
          }  
      } 
      _log.info("RaidBoss FIX SPAWN: Loaded " + _map.size() + " Raids.");
    } catch (Exception e) {
      _log.warning("RaidBoss FIX SPAWN: Error while loading Raids table: " + e);
    } 
  }
  
  public static final List<String> parseList(String line) {
    List<String> list = new ArrayList<>();
    for (String id : line.split(","))
      list.add(id); 
    return list;
  }
  
  public List<String> getBossSpawn(int bossId) {
    return _map.get(Integer.valueOf(bossId));
  }
  
  public boolean containsBoss(int bossId) {
    if (_map != null && _map.containsKey(Integer.valueOf(bossId)))
      return true; 
    return false;
  }
  
  private static class SingletonHolder {
    protected static final RaidSpawnTable _instance = new RaidSpawnTable();
  }
}
