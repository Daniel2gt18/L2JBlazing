package com.l2jmega.gameserver.scripting.scripts.ai.group;
 
import java.util.HashMap;
import java.util.Map;
 
import com.l2jmega.gameserver.model.actor.Attackable;
import com.l2jmega.gameserver.model.actor.Npc;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.scripting.EventType;
import com.l2jmega.gameserver.scripting.scripts.ai.L2AttackableAIScript;
 
/**
 * Angel spawns... When one of the angels in the keys dies, the other angel will spawn.
 */
public class PolymorphingHalisha extends L2AttackableAIScript
{
    private static final Map<Integer, Integer> ANGELSPAWNS = new HashMap<>();
    
    static
    {
        ANGELSPAWNS.put(29046, 29047);
    }
    
    public PolymorphingHalisha()
    {
        super("ai/group");
    }
    
    @Override
    protected void registerNpcs()
    {
        addEventIds(ANGELSPAWNS.keySet(), EventType.ON_KILL);
    }
    
    @Override
    public String onKill(Npc npc, Player killer, boolean isPet)
    {
        final Attackable newNpc = (Attackable) addSpawn(ANGELSPAWNS.get(npc.getNpcId()), npc, false, 0, false);
        attack(newNpc, killer);
        
        return super.onKill(npc, killer, isPet);
    }
}