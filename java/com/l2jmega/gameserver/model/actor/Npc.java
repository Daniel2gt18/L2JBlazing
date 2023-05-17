package com.l2jmega.gameserver.model.actor;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.IconTable;
import com.l2jmega.gameserver.data.ItemTable;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.data.SkillTable;
import com.l2jmega.gameserver.data.SkillTable.FrequentSkill;
import com.l2jmega.gameserver.data.cache.HtmCache;
import com.l2jmega.gameserver.data.sql.ClanTable;
import com.l2jmega.gameserver.data.xml.MultisellData;
import com.l2jmega.gameserver.data.xml.NewbieBuffData;
import com.l2jmega.gameserver.idfactory.IdFactory;
import com.l2jmega.gameserver.instancemanager.DimensionalRiftManager;
import com.l2jmega.gameserver.instancemanager.GrandBossManager;
import com.l2jmega.gameserver.instancemanager.RaidBossInfoManager;
import com.l2jmega.gameserver.instancemanager.SevenSigns;
import com.l2jmega.gameserver.instancemanager.games.Lottery;
import com.l2jmega.gameserver.model.L2Skill;
import com.l2jmega.gameserver.model.L2Spawn;
import com.l2jmega.gameserver.model.NewbieBuff;
import com.l2jmega.gameserver.model.ShotType;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.ai.CtrlIntention;
import com.l2jmega.gameserver.model.actor.instance.Chest;
import com.l2jmega.gameserver.model.actor.instance.Fisherman;
import com.l2jmega.gameserver.model.actor.instance.Gatekeeper;
import com.l2jmega.gameserver.model.actor.instance.GrandBoss;
import com.l2jmega.gameserver.model.actor.instance.Merchant;
import com.l2jmega.gameserver.model.actor.instance.Monster;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.RaidBoss;
import com.l2jmega.gameserver.model.actor.instance.WarehouseKeeper;
import com.l2jmega.gameserver.model.actor.stat.NpcStat;
import com.l2jmega.gameserver.model.actor.status.NpcStatus;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate.AIType;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate.Race;
import com.l2jmega.gameserver.model.actor.template.NpcTemplate.SkillType;
import com.l2jmega.gameserver.model.entity.Castle;
import com.l2jmega.gameserver.model.item.DropCategory;
import com.l2jmega.gameserver.model.item.DropData;
import com.l2jmega.gameserver.model.item.instance.ItemInstance;
import com.l2jmega.gameserver.model.item.kind.Item;
import com.l2jmega.gameserver.model.item.kind.Weapon;
import com.l2jmega.gameserver.model.pledge.Clan;
import com.l2jmega.gameserver.network.SystemMessageId;
import com.l2jmega.gameserver.network.clientpackets.Say2;
import com.l2jmega.gameserver.network.serverpackets.AbstractNpcInfo.NpcInfo;
import com.l2jmega.gameserver.network.serverpackets.ActionFailed;
import com.l2jmega.gameserver.network.serverpackets.ExShowVariationCancelWindow;
import com.l2jmega.gameserver.network.serverpackets.ExShowVariationMakeWindow;
import com.l2jmega.gameserver.network.serverpackets.MagicSkillUse;
import com.l2jmega.gameserver.network.serverpackets.MoveToPawn;
import com.l2jmega.gameserver.network.serverpackets.NpcHtmlMessage;
import com.l2jmega.gameserver.network.serverpackets.NpcSay;
import com.l2jmega.gameserver.network.serverpackets.ServerObjectInfo;
import com.l2jmega.gameserver.network.serverpackets.SocialAction;
import com.l2jmega.gameserver.network.serverpackets.SystemMessage;
import com.l2jmega.gameserver.scripting.EventType;
import com.l2jmega.gameserver.scripting.Quest;
import com.l2jmega.gameserver.scripting.QuestState;
import com.l2jmega.gameserver.scripting.ScriptManager;
import com.l2jmega.gameserver.taskmanager.DecayTaskManager;
import com.l2jmega.gameserver.taskmanager.RandomAnimationTaskManager;
import com.l2jmega.gameserver.templates.StatsSet;
import com.l2jmega.gameserver.templates.skills.L2SkillType;
import com.l2jmega.gameserver.util.Broadcast;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import com.l2jmega.commons.concurrent.ThreadPool;
import com.l2jmega.commons.lang.StringUtil;
import com.l2jmega.commons.random.Rnd;

/**
 * This class represents a Non Playable Character (or NPC) in the world.
 */
public class Npc extends Creature
{
	public static final int INTERACTION_DISTANCE = 150;
	private static final int SOCIAL_INTERVAL = 12000;
	
	private static final int[] fafurions =
	{
		25198
	};
	
	private static final int[] palibati =
	{
		25252
	};
	
	private static final int[] beast =
	{
		25269
	};
	
	private static final int[] plague =
	{
		25523
	};
	
	private static final int[] water =
	{
		25199
	};
	
	private static final int[] krokian =
	{
		25202
	};
	
	private static final int[] olkuth =
	{
		25244
	};
	
	private static final int[] glaki =
	{
		25245
	};
	
	private static final int[] ocean =
	{
		25205
	};
	
	private static final int[] taik =
	{
		25256
	};
	
	private static final int[] lord =
	{
		25407
	};
	
	private static final int[] barakiel =
	{
		25325
	};
	private static final int[] hekaton =
	{
		25299
	};
	
	private static final int[] brakki =
	{
		25305
	};
	
	private static final int[] tayr =
	{
		25302
	};
	
	private static final int[] shadith =
	{
		25309
	};
	private static final int[] moss =
	{
		25312
	};
	private static final int[] horus =
	{
		25315
	};
	private static final int[] orfen =
	{
		29014
	};
	
	private static final int[] core =
	{
		29006
	};
	
	private static final int[] queen =
	{
		29001
	};
	
	private static final int[] zaken =
	{
		29022
	};
	
	private static final int[] sailren =
	{
		29065
	};
	
	private static final int[] frintezza =
	{
        29045
	};
	
	private static final int[] valakas =
	{
		29028
	};
	
	private static final int[] antharas =
	{
		29019
	};
	
	private static final int[] baium =
	{
		29020
	};
	
	private L2Spawn _spawn;
	
	volatile boolean _isDecayed = false;
	
	private int _spoilerId = 0;
	
	private long _lastSocialBroadcast = 0;
	
	private int _currentLHandId;
	private int _currentRHandId;
	private int _currentEnchant;
	
	private double _currentCollisionHeight; // used for npc grow effect skills
	private double _currentCollisionRadius; // used for npc grow effect skills
	
	private int _currentSsCount = 0;
	private int _currentSpsCount = 0;
	private int _shotsMask = 0;
	
	private int _scriptValue = 0;
	
	private Castle _castle;
	
	public boolean _isEventMobTvT = false, _isEventMobCTF = false, _isCTF_throneSpawn = false, _isCTF_Flag = false;
	
	/** The _ ct f_ flag team name. */
	public String _CTF_FlagTeamName;
	
	/**
	 * Broadcast a SocialAction packet.
	 * @param id the animation id.
	 */
	public void onRandomAnimation(int id)
	{
		final long now = System.currentTimeMillis();
		if (now - _lastSocialBroadcast > SOCIAL_INTERVAL)
		{
			_lastSocialBroadcast = now;
			broadcastPacket(new SocialAction(this, id));
		}
	}
	
	/**
	 * Create a RandomAnimation Task that will be launched after the calculated delay.
	 */
	public void startRandomAnimationTimer()
	{
		if (!hasRandomAnimation())
			return;
		
		final int timer = (isMob()) ? Rnd.get(Config.MIN_MONSTER_ANIMATION, Config.MAX_MONSTER_ANIMATION) : Rnd.get(Config.MIN_NPC_ANIMATION, Config.MAX_NPC_ANIMATION);
		RandomAnimationTaskManager.getInstance().add(this, timer);
	}
	
	/**
	 * @return true if the server allows Random Animation, false if not or the AItype is a corpse.
	 */
	public boolean hasRandomAnimation()
	{
		return (Config.MAX_NPC_ANIMATION > 0 && !getTemplate().getAiType().equals(AIType.CORPSE));
	}
	
	/**
	 * Constructor of L2Npc (use Creature constructor).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Call the Creature constructor to set the _template of the Creature (copy skills from template to object and link _calculators to NPC_STD_CALCULATOR)</li> <li>Set the name of the Creature</li> <li>Create a RandomAnimation Task that will be launched after the calculated delay if the server
	 * allow it</li><BR>
	 * <BR>
	 * @param objectId Identifier of the object to initialized
	 * @param template The L2NpcTemplate to apply to the NPC
	 */
	public Npc(int objectId, NpcTemplate template)
	{
		super(objectId, template);
		
		for (L2Skill skill : template.getSkills(SkillType.PASSIVE))
			addStatFuncs(skill.getStatFuncs(this));
		
		initCharStatusUpdateValues();
		
		// initialize the "current" equipment
		_currentLHandId = template.getLeftHand();
		_currentRHandId = template.getRightHand();
		_currentEnchant = template.getEnchantEffect();
		
		// initialize the "current" collisions
		_currentCollisionHeight = template.getCollisionHeight();
		_currentCollisionRadius = template.getCollisionRadius();
		
		// Set the name of the Creature
		setName(template.getName());
		setTitle(template.getTitle());
		
		_castle = template.getCastle();
	}
	
	@Override
	public void initCharStat()
	{
		setStat(new NpcStat(this));
	}
	
	@Override
	public NpcStat getStat()
	{
		return (NpcStat) super.getStat();
	}
	
	@Override
	public void initCharStatus()
	{
		setStatus(new NpcStatus(this));
	}
	
	@Override
	public NpcStatus getStatus()
	{
		return (NpcStatus) super.getStatus();
	}
	
	/** Return the L2NpcTemplate of the L2Npc. */
	@Override
	public final NpcTemplate getTemplate()
	{
		return (NpcTemplate) super.getTemplate();
	}
	
	/**
	 * @return the generic Identifier of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getNpcId()
	{
		return getTemplate().getNpcId();
	}
	
	@Override
	public boolean isAttackable()
	{
		return true;
	}
	
	/**
	 * Return the Level of this L2Npc contained in the L2NpcTemplate.
	 */
	@Override
	public final int getLevel()
	{
		return getTemplate().getLevel();
	}
	
	/**
	 * @return True if the L2Npc is agressive (ex : L2MonsterInstance in function of aggroRange).
	 */
	public boolean isAggressive()
	{
		return false;
	}
	
	/**
	 * Return True if this L2Npc is undead in function of the L2NpcTemplate.
	 */
	@Override
	public boolean isUndead()
	{
		return getTemplate().getRace() == Race.UNDEAD;
	}
	
	/**
	 * Broadcast a NpcInfo packet with state of abnormal effect.
	 */
	@Override
	public void updateAbnormalEffect()
	{
		for (Player player : getKnownType(Player.class))
		{
			if (getMoveSpeed() == 0)
				player.sendPacket(new ServerObjectInfo(this, player));
			else
				player.sendPacket(new NpcInfo(this, player));
		}
	}
	
	/**
	 * Set the Title of the Creature. Concatens it if length > 16.
	 * @param value The String to test.
	 */
	@Override
	public final void setTitle(String value)
	{
		if (value == null)
			_title = "";
		else
			_title = value;
	}
	
	@Override
	public boolean isAutoAttackable(Creature attacker)
	{
		return false;
	}
	
	/**
	 * @return the Identifier of the item in the left hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getLeftHandItem()
	{
		return _currentLHandId;
	}
	
	/**
	 * @return the Identifier of the item in the right hand of this L2Npc contained in the L2NpcTemplate.
	 */
	public int getRightHandItem()
	{
		return _currentRHandId;
	}
	
	public int getEnchantEffect()
	{
		return _currentEnchant;
	}
	
	public final int getSpoilerId()
	{
		return _spoilerId;
	}
	
	public final void setSpoilerId(int value)
	{
		_spoilerId = value;
	}
	
	/**
	 * Overidden in L2CastleWarehouse, L2ClanHallManager and L2Warehouse.
	 * @return true if this L2Npc instance can be warehouse manager.
	 */
	public boolean isWarehouse()
	{
		return false;
	}
	
	@Override
	public void onAction(Player player)
	{
		// Set the target of the player
		if (player.getTarget() != this)
			player.setTarget(this);
		else
		{
			// Check if the player is attackable (without a forced attack) and isn't dead
			if (isAutoAttackable(player))
			{
				player.getAI().setIntention(CtrlIntention.ATTACK, this);
			}
			else
			{
				// Calculate the distance between the Player and the L2Npc
				if (!canInteract(player))
				{
					// Notify the Player AI with INTERACT
					player.getAI().setIntention(CtrlIntention.INTERACT, this);
				}
				else
				{
					// Rotate the player to face the instance
					player.sendPacket(new MoveToPawn(player, this, Npc.INTERACTION_DISTANCE));
					
					// Send ActionFailed to the player in order to avoid he stucks
					player.sendPacket(ActionFailed.STATIC_PACKET);
					
					if (hasRandomAnimation())
						onRandomAnimation(Rnd.get(8));
					
					if (_isEventMobTvT)
					{
						TvT.showEventHtml(player, String.valueOf(getObjectId()));
						return;
					}
					else if (_isEventMobCTF)
					{
						CTF.showEventHtml(player, String.valueOf(getObjectId()));
						return;
					}
					else if (_isCTF_Flag && player._inEventCTF)
					{
						CTF.showFlagHtml(player, String.valueOf(this.getObjectId()), _CTF_FlagTeamName);
						return;
					}
					else if (_isCTF_throneSpawn)
						return;
	
					List<Quest> qlsa = getTemplate().getEventQuests(EventType.QUEST_START);
					if (qlsa != null && !qlsa.isEmpty())
						player.setLastQuestNpcObject(getObjectId());
					
					List<Quest> qlst = getTemplate().getEventQuests(EventType.ON_FIRST_TALK);
					if (qlst != null && qlst.size() == 1)
						qlst.get(0).notifyFirstTalk(this, player);
					else
						showChatWindow(player);
				}
			}
		}
	}
	
	  public static void sendNpcDrop(Player player, int npcId, int page)
	     {
	         final int ITEMS_PER_LIST = 7;
	         final NpcTemplate npc = NpcTable.getInstance().getTemplate(npcId);
	         if (npc == null)
	             return;
	         
	         if (npc.getDropData().isEmpty())
	         {
	             player.sendMessage("This target have not drop info.");
	             return;
	         }
	         
	         final List<DropCategory> list = new ArrayList<>();
	         npc.getDropData().forEach(c -> list.add(c));
	         Collections.reverse(list);
	         
	         int myPage = 1;
	         int i = 0;
	         int shown = 0;
	         boolean hasMore = false;
	         
	         final StringBuilder sb = new StringBuilder();
	         for (DropCategory cat : list)
	         {
	             if (shown == ITEMS_PER_LIST)
	             {
	                 hasMore = true;
	                 break;
	             }
	             
	             for (DropData drop : cat.getAllDrops())
	             {
	                 double chance = (drop.getItemId() == 57 ? drop.getChance() * Config.RATE_DROP_ADENA : drop.getChance() * Config.RATE_DROP_ITEMS) / 10000;
	                 chance = chance > 100 ? 100 : chance;
	                 
	                 String percent = null;
	                 if (chance <= 0.001)
	                 {
	                     DecimalFormat df = new DecimalFormat("#.####");
	                     percent = df.format(chance);
	                 }
	                 else if (chance <= 0.01)
	                 {
	                     DecimalFormat df = new DecimalFormat("#.###");
	                     percent = df.format(chance);
	                 }
	                 else
	                 {
	                     DecimalFormat df = new DecimalFormat("##.##");
	                     percent = df.format(chance);
	                 }
	                 
	                 Item item = ItemTable.getInstance().getTemplate(drop.getItemId());
	                 String name = item.getName();
	                 
	                 if (name.startsWith("Recipe: "))
	                     name = "R: " + name.substring(8);
	                 
	                 if (name.length() >= 40)
	                     name = name.substring(0, 37) + "...";
	                 
	                 if (myPage != page)
	                 {
	                     i++;
	                     if (i == ITEMS_PER_LIST)
	                     {
	                         myPage++;
	                         i = 0;
	                     }
	                     continue;
	                 }
	                 
	                 if (shown == ITEMS_PER_LIST)
	                 {
	                     hasMore = true;
	                     break;
	                 }
	                 

	                 sb.append("<table width=280 bgcolor=000000><tr>");
	                 sb.append("<td width=44 height=41 align=center><table bgcolor=" + (cat.isSweep() ? "FF00FF" : "FFFFFF") + " cellpadding=6 cellspacing=\"-5\"><tr><td><button width=32 height=32 back=" +IconTable.getIcon(item.getItemId())+ " fore=" +IconTable.getIcon(item.getItemId())+ "></td></tr></table></td>");
	                 sb.append("<td width=240>" + (cat.isSweep() ? "<font color=ff00ff>" + name + "</font>" : name) + "<br1><font color=B09878>" + (cat.isSweep() ? "Spoil" : "Drop") + " Chance : " + percent + "%</font></td>");
	                 sb.append("</tr></table><img src=L2UI.SquareGray width=280 height=1>");
	                 shown++;
	             }
	         }
	         sb.append("<img height=" + (294 - (shown * 42)) + ">");
	         sb.append("<img height=8><img src=L2UI.SquareGray width=280 height=1>");
	         sb.append("<table width=280 bgcolor=000000><tr>");
	         sb.append("<td align=center width=70>" + (page > 1 ? "<button value=\"< PREV\" action=\"bypass droplist " + npcId + " " + (page - 1) + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "") + "</td>");
	         sb.append("<td align=center width=140>Page " + page + "</td>");
	         sb.append("<td align=center width=70>" + (hasMore ? "<button value=\"NEXT >\" action=\"bypass droplist " + npcId + " " + (page + 1) + "\" width=65 height=19 back=L2UI_ch3.smallbutton2_over fore=L2UI_ch3.smallbutton2>" : "") + "</td>");
	         sb.append("</tr></table><img src=L2UI.SquareGray width=280 height=1>");
	         
	         final NpcHtmlMessage html = new NpcHtmlMessage(200);
	         html.setFile("data/html/droplist.htm");
	         html.replace("%list%", sb.toString());
	         html.replace("%name%", npc.getName());
	         player.sendPacket(html);
	     }
	
	@Override
	public void onActionShift(Player player)
	{
		// Check if the Player is a GM
		if (player.isGM())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/admin/npcinfo.htm");
			html.replace("%class%", getClass().getSimpleName());
			html.replace("%id%", getTemplate().getNpcId());
			html.replace("%lvl%", getTemplate().getLevel());
			html.replace("%name%", getName());
			html.replace("%race%", getTemplate().getRace().toString());
			html.replace("%tmplid%", getTemplate().getIdTemplate());
			html.replace("%castle%", (getCastle() != null) ? getCastle().getName() : "none");
			html.replace("%aggro%", getTemplate().getAggroRange());
			html.replace("%corpse%", StringUtil.getTimeStamp(getTemplate().getCorpseTime()));
			html.replace("%enchant%", getTemplate().getEnchantEffect());
			html.replace("%hp%", (int) getCurrentHp());
			html.replace("%hpmax%", getMaxHp());
			html.replace("%mp%", (int) getCurrentMp());
			html.replace("%mpmax%", getMaxMp());
			html.replace("%patk%", getPAtk(null));
			html.replace("%matk%", getMAtk(null, null));
			html.replace("%pdef%", getPDef(null));
			html.replace("%mdef%", getMDef(null, null));
			html.replace("%accu%", getAccuracy());
			html.replace("%evas%", getEvasionRate(null));
			html.replace("%crit%", getCriticalHit(null, null));
			html.replace("%rspd%", getMoveSpeed());
			html.replace("%aspd%", getPAtkSpd());
			html.replace("%cspd%", getMAtkSpd());
			html.replace("%str%", getSTR());
			html.replace("%dex%", getDEX());
			html.replace("%con%", getCON());
			html.replace("%int%", getINT());
			html.replace("%wit%", getWIT());
			html.replace("%men%", getMEN());
			html.replace("%loc%", getX() + " " + getY() + " " + getZ());
			html.replace("%dist%", (int) Math.sqrt(player.getDistanceSq(this)));
			html.replace("%ele_fire%", getDefenseElementValue((byte) 2));
			html.replace("%ele_water%", getDefenseElementValue((byte) 3));
			html.replace("%ele_wind%", getDefenseElementValue((byte) 1));
			html.replace("%ele_earth%", getDefenseElementValue((byte) 4));
			html.replace("%ele_holy%", getDefenseElementValue((byte) 5));
			html.replace("%ele_dark%", getDefenseElementValue((byte) 6));
			
			if (getSpawn() != null)
			{
				html.replace("%spawn%", getSpawn().getLoc().toString());
				html.replace("%loc2d%", (int) Math.sqrt(getPlanDistanceSq(getSpawn().getLocX(), getSpawn().getLocY())));
				html.replace("%loc3d%", (int) Math.sqrt(getDistanceSq(getSpawn().getLocX(), getSpawn().getLocY(), getSpawn().getLocZ())));
				html.replace("%resp%", StringUtil.getTimeStamp(getSpawn().getRespawnDelay()));
				html.replace("%rand_resp%", StringUtil.getTimeStamp(getSpawn().getRespawnRandom()));
			}
			else
			{
				html.replace("%spawn%", "<font color=FF0000>null</font>");
				html.replace("%loc2d%", "<font color=FF0000>--</font>");
				html.replace("%loc3d%", "<font color=FF0000>--</font>");
				html.replace("%resp%", "<font color=FF0000>--</font>");
				html.replace("%rand_resp%", "<font color=FF0000>--</font>");
			}
			
			if (hasAI())
			{
				html.replace("%ai_intention%", "<font color=\"LEVEL\">Intention</font><table width=\"100%\"><tr><td><font color=\"LEVEL\">Intention:</font></td><td>" + getAI().getIntention().name() + "</td></tr>");
				html.replace("%ai%", "<tr><td><font color=\"LEVEL\">AI:</font></td><td>" + getAI().getClass().getSimpleName() + "</td></tr></table><br>");
			}
			else
			{
				html.replace("%ai_intention%", "");
				html.replace("%ai%", "");
			}
			
			html.replace("%ai_type%", getTemplate().getAiType().name());
			html.replace("%ai_clan%", (getTemplate().getClans() != null) ? "<tr><td width=100><font color=\"LEVEL\">Clan:</font></td><td align=right width=170>" + Arrays.toString(getTemplate().getClans()) + " " + getTemplate().getClanRange() + "</td></tr>" + ((getTemplate().getIgnoredIds() != null) ? "<tr><td width=100><font color=\"LEVEL\">Ignored ids:</font></td><td align=right width=170>" + Arrays.toString(getTemplate().getIgnoredIds()) + "</td></tr>" : "") : "");
			html.replace("%ai_move%", String.valueOf(getTemplate().canMove()));
			html.replace("%ai_seed%", String.valueOf(getTemplate().isSeedable()));
			html.replace("%ai_ssinfo%", _currentSsCount + "[" + getTemplate().getSsCount() + "] - " + getTemplate().getSsRate() + "%");
			html.replace("%ai_spsinfo%", _currentSpsCount + "[" + getTemplate().getSpsCount() + "] - " + getTemplate().getSpsRate() + "%");
			html.replace("%butt%", ((this instanceof Merchant) ? "<button value=\"Shop\" action=\"bypass -h admin_show_shop " + getNpcId() + "\" width=65 height=19 back=\"L2UI_ch3.smallbutton2_over\" fore=\"L2UI_ch3.smallbutton2\">" : ""));
			player.sendPacket(html);
		}
		else if (this instanceof Monster || this instanceof RaidBoss || this instanceof GrandBoss || this instanceof Chest)
		sendNpcDrop(player, getTemplate().getNpcId(), 1);
		
		if (player.getTarget() != this)
			player.setTarget(this);
		else
			player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * @return the L2Castle this L2Npc belongs to.
	 */
	public final Castle getCastle()
	{
		return _castle;
	}
	
	public void setCastle(Castle castle)
	{
		_castle = castle;
	}
	
	/**
	 * Open a quest or chat window on client with the text of the L2Npc in function of the command.
	 * @param player The player to test
	 * @param command The command string received from client
	 */
	public void onBypassFeedback(Player player, String command)
	{
		if (command.equalsIgnoreCase("TerritoryStatus"))
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			
			if (getCastle().getOwnerId() > 0)
			{
				html.setFile("data/html/territorystatus.htm");
				Clan clan = ClanTable.getInstance().getClan(getCastle().getOwnerId());
				html.replace("%clanname%", clan.getName());
				html.replace("%clanleadername%", clan.getLeaderName());
			}
			else
				html.setFile("data/html/territorynoclan.htm");
			
			html.replace("%castlename%", getCastle().getName());
			html.replace("%taxpercent%", getCastle().getTaxPercent());
			html.replace("%objectId%", getObjectId());
			
			if (getCastle().getCastleId() > 6)
				html.replace("%territory%", "The Kingdom of Elmore");
			else
				html.replace("%territory%", "The Kingdom of Aden");
			
			player.sendPacket(html);
		}
		else if (command.startsWith("Quest"))
		{
			String quest = "";
			try
			{
				quest = command.substring(5).trim();
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			
			if (quest.isEmpty())
				showQuestWindowGeneral(player, this);
			else
				showQuestWindowSingle(player, this, ScriptManager.getInstance().getQuest(quest));
		}
		else if (command.startsWith("Chat"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			showChatWindow(player, val);
		}
		else if (command.startsWith("Link"))
		{
			String path = command.substring(5).trim();
			if (path.indexOf("..") != -1)
				return;
			
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setFile("data/html/" + path);
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
		}
		else if (command.startsWith("Loto"))
		{
			int val = 0;
			try
			{
				val = Integer.parseInt(command.substring(5));
			}
			catch (IndexOutOfBoundsException ioobe)
			{
			}
			catch (NumberFormatException nfe)
			{
			}
			
			if (val == 0)
			{
				// new loto ticket
				for (int i = 0; i < 5; i++)
					player.setLoto(i, 0);
			}
			showLotoWindow(player, val);
		}
		else if (command.startsWith("CPRecovery"))
		{
			makeCPRecovery(player);
		}
		else if (command.startsWith("SupportMagic"))
		{
			makeSupportMagic(player);
		}
		else if (command.startsWith("multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(9).trim(), player, this, false);
		}
		else if (command.startsWith("exc_multisell"))
		{
			MultisellData.getInstance().separateAndSend(command.substring(13).trim(), player, this, true);
		}
		else if (command.startsWith("Augment"))
		{
			int cmdChoice = Integer.parseInt(command.substring(8, 9).trim());
			switch (cmdChoice)
			{
				case 1:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_TO_BE_AUGMENTED);
					player.sendPacket(ExShowVariationMakeWindow.STATIC_PACKET);
					break;
				case 2:
					player.sendPacket(SystemMessageId.SELECT_THE_ITEM_FROM_WHICH_YOU_WISH_TO_REMOVE_AUGMENTATION);
					player.sendPacket(ExShowVariationCancelWindow.STATIC_PACKET);
					break;
			}
		}
		else if (command.startsWith("EnterRift"))
		{
			try
			{
				Byte b1 = Byte.parseByte(command.substring(10)); // Selected Area: Recruit, Soldier etc
				DimensionalRiftManager.getInstance().start(player, b1, this);
			}
			catch (Exception e)
			{
			}
		}
		else if (command.startsWith("ChangeRiftRoom"))
		{
			if (player.isInParty() && player.getParty().isInDimensionalRift())
				player.getParty().getDimensionalRift().manualTeleport(player, this);
		}
		else if (command.startsWith("ExitRift"))
		{
			if (player.isInParty() && player.getParty().isInDimensionalRift())
				player.getParty().getDimensionalRift().manualExitRift(player, this);
		}
	}
	
	/**
	 * Collect quests in progress and possible quests and show proper quest window.
	 * @param player : The player that talk with the L2Npc.
	 * @param npc : The L2Npc instance.
	 */
	public static void showQuestWindowGeneral(Player player, Npc npc)
	{
		List<Quest> quests = new ArrayList<>();
		
		List<Quest> awaits = npc.getTemplate().getEventQuests(EventType.ON_TALK);
		if (awaits != null)
		{
			for (Quest quest : awaits)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				QuestState qs = player.getQuestState(quest.getName());
				if (qs == null || qs.isCreated())
					continue;
				
				quests.add(quest);
			}
		}
		
		List<Quest> starts = npc.getTemplate().getEventQuests(EventType.QUEST_START);
		if (starts != null)
		{
			for (Quest quest : starts)
			{
				if (quest == null || !quest.isRealQuest() || quests.contains(quest))
					continue;
				
				quests.add(quest);
			}
		}
		
		if (quests.isEmpty())
			showQuestWindowSingle(player, npc, null);
		else if (quests.size() == 1)
			showQuestWindowSingle(player, npc, quests.get(0));
		else
			showQuestWindowChoose(player, npc, quests);
	}
	
	/**
	 * Open a quest window on client with the text of the L2Npc. Create the QuestState if not existing.
	 * @param player : the player that talk with the L2Npc.
	 * @param npc : the L2Npc instance.
	 * @param quest : the quest HTMLs to show.
	 */
	public static void showQuestWindowSingle(Player player, Npc npc, Quest quest)
	{
		if (quest == null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
			html.setHtml(Quest.getNoQuestMsg());
			player.sendPacket(html);
			
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return;
		}
		
		if (quest.isRealQuest() && (player.getWeightPenalty() >= 3 || player.getInventoryLimit() * 0.8 <= player.getInventory().getSize()))
		{
			player.sendPacket(SystemMessageId.INVENTORY_LESS_THAN_80_PERCENT);
			return;
		}
		
		QuestState qs = player.getQuestState(quest.getName());
		if (qs == null)
		{
			if (quest.isRealQuest() && player.getAllQuests(false).size() >= 25)
			{
				final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
				html.setHtml(Quest.getTooMuchQuestsMsg());
				player.sendPacket(html);
				
				player.sendPacket(ActionFailed.STATIC_PACKET);
				return;
			}
			
			List<Quest> qlst = npc.getTemplate().getEventQuests(EventType.QUEST_START);
			if (qlst != null && qlst.contains(quest))
				qs = quest.newQuestState(player);
		}
		
		if (qs != null)
			quest.notifyTalk(npc, qs.getPlayer());
	}
	
	/**
	 * Shows the list of available quest of the L2Npc.
	 * @param player : The player that talk with the L2Npc.
	 * @param npc : The L2Npc instance.
	 * @param quests : The list containing quests of the L2Npc.
	 */
	public static void showQuestWindowChoose(Player player, Npc npc, List<Quest> quests)
	{
		final StringBuilder sb = new StringBuilder("<html><body>");
		
		for (Quest q : quests)
		{
			StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Quest ", q.getName(), "\">[", q.getDescr());
			
			final QuestState qs = player.getQuestState(q.getName());
			if (qs != null && qs.isStarted())
				sb.append(" (In Progress)]</a><br>");
			else if (qs != null && qs.isCompleted())
				sb.append(" (Done)]</a><br>");
			else
				sb.append("]</a><br>");
		}
		
		sb.append("</body></html>");
		
		final NpcHtmlMessage html = new NpcHtmlMessage(npc.getObjectId());
		html.setHtml(sb.toString());
		html.replace("%objectId%", npc.getObjectId());
		player.sendPacket(html);
		
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instancies).<BR>
	 * <BR>
	 */
	@Override
	public ItemInstance getActiveWeaponInstance()
	{
		return null;
	}
	
	/**
	 * Return the weapon item equipped in the right hand of the L2Npc or null.
	 */
	@Override
	public Weapon getActiveWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int weaponId = getTemplate().getRightHand();
		if (weaponId < 1)
			return null;
		
		// Get the weapon item equipped in the right hand of the L2Npc
		Item item = ItemTable.getInstance().getTemplate(weaponId);
		if (!(item instanceof Weapon))
			return null;
		
		return (Weapon) item;
	}
	
	/**
	 * Return null (regular NPCs don't have weapons instancies).
	 */
	@Override
	public ItemInstance getSecondaryWeaponInstance()
	{
		return null;
	}
	
	/**
	 * Return the item equipped in the left hand of the L2Npc or null.
	 */
	@Override
	public Item getSecondaryWeaponItem()
	{
		// Get the weapon identifier equipped in the right hand of the L2Npc
		int itemId = getTemplate().getLeftHand();
		if (itemId < 1)
			return null;
		
		// Return the item equipped in the left hand of the L2Npc
		return ItemTable.getInstance().getTemplate(itemId);
	}
	
	/**
	 * <B><U> Format of the pathfile </U> :</B><BR>
	 * <BR>
	 * <li>if the file exists on the server (page number = 0) : <B>data/html/default/12006.htm</B> (npcId-page number)</li> <li>if the file exists on the server (page number > 0) : <B>data/html/default/12006-1.htm</B> (npcId-page number)</li> <li>if the file doesn't exist on the server :
	 * <B>data/html/npcdefault.htm</B> (message : "I have nothing to say to you")</li><BR>
	 * <BR>
	 * <B><U> Overriden in </U> :</B><BR>
	 * <BR>
	 * <li>L2GuardInstance : Set the pathfile to data/html/guard/12006-1.htm (npcId-page number)</li><BR>
	 * <BR>
	 * @param npcId The Identifier of the L2Npc whose text must be display
	 * @param val The number of the page to display
	 * @return the pathfile of the selected HTML file in function of the npcId and of the page number.
	 */
	public String getHtmlPath(int npcId, int val)
	{
		String filename;
		
		if (val == 0)
			filename = "data/html/default/" + npcId + ".htm";
		else
			filename = "data/html/default/" + npcId + "-" + val + ".htm";
		
		if (HtmCache.getInstance().isLoadable(filename))
			return filename;
		
		return "data/html/npcdefault.htm";
	}
	
	/**
	 * Make the NPC speaks to his current knownlist.
	 * @param message The String message to send.
	 */
	public void broadcastNpcSay(String message)
	{
		broadcastPacket(new NpcSay(getObjectId(), Say2.ALL, getNpcId(), message));
	}
	
	/**
	 * Open a Loto window on client with the text of the L2Npc.
	 * @param player : The player that talk with the L2Npc.
	 * @param val : The number of the page of the L2Npc to display.
	 */
	// 0 - first buy lottery ticket window
	// 1-20 - buttons
	// 21 - second buy lottery ticket window
	// 22 - selected ticket with 5 numbers
	// 23 - current lottery jackpot
	// 24 - Previous winning numbers/Prize claim
	// >24 - check lottery ticket by item object id
	public void showLotoWindow(Player player, int val)
	{
		int npcId = getTemplate().getNpcId();
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		
		if (val == 0) // 0 - first buy lottery ticket window
		{
			html.setFile(getHtmlPath(npcId, 1));
		}
		else if (val >= 1 && val <= 21) // 1-20 - buttons, 21 - second buy lottery ticket window
		{
			if (!Lottery.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			html.setFile(getHtmlPath(npcId, 5));
			
			int count = 0;
			int found = 0;
			// counting buttons and unsetting button if found
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == val)
				{
					// unsetting button
					player.setLoto(i, 0);
					found = 1;
				}
				else if (player.getLoto(i) > 0)
				{
					count++;
				}
			}
			
			// if not rearched limit 5 and not unseted value
			if (count < 5 && found == 0 && val <= 20)
				for (int i = 0; i < 5; i++)
					if (player.getLoto(i) == 0)
					{
						player.setLoto(i, val);
						break;
					}
			
			// setting pusshed buttons
			count = 0;
			for (int i = 0; i < 5; i++)
				if (player.getLoto(i) > 0)
				{
					count++;
					String button = String.valueOf(player.getLoto(i));
					if (player.getLoto(i) < 10)
						button = "0" + button;
					String search = "fore=\"L2UI.lottoNum" + button + "\" back=\"L2UI.lottoNum" + button + "a_check\"";
					String replace = "fore=\"L2UI.lottoNum" + button + "a_check\" back=\"L2UI.lottoNum" + button + "\"";
					html.replace(search, replace);
				}
			
			if (count == 5)
			{
				String search = "0\">Return";
				String replace = "22\">The winner selected the numbers above.";
				html.replace(search, replace);
			}
		}
		else if (val == 22) // 22 - selected ticket with 5 numbers
		{
			if (!Lottery.getInstance().isStarted())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_CURRENT_SOLD);
				return;
			}
			if (!Lottery.getInstance().isSellableTickets())
			{
				// tickets can't be sold
				player.sendPacket(SystemMessageId.NO_LOTTERY_TICKETS_AVAILABLE);
				return;
			}
			
			int price = Config.ALT_LOTTERY_TICKET_PRICE;
			int lotonumber = Lottery.getInstance().getId();
			int enchant = 0;
			int type2 = 0;
			
			for (int i = 0; i < 5; i++)
			{
				if (player.getLoto(i) == 0)
					return;
				
				if (player.getLoto(i) < 17)
					enchant += Math.pow(2, player.getLoto(i) - 1);
				else
					type2 += Math.pow(2, player.getLoto(i) - 17);
			}
			
			if (!player.reduceAdena("Loto", price, this, true))
				return;
			
			Lottery.getInstance().increasePrize(price);
			
			ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), 4442);
			item.setCount(1);
			item.setCustomType1(lotonumber);
			item.setEnchantLevel(enchant);
			item.setCustomType2(type2);
			
			player.addItem("Loto", item, player, false);
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.EARNED_ITEM_S1).addItemName(4442));
			
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 23) // 23 - current lottery jackpot
		{
			html.setFile(getHtmlPath(npcId, 3));
		}
		else if (val == 24) // 24 - Previous winning numbers/Prize claim
		{
			final int lotoNumber = Lottery.getInstance().getId();
			
			final StringBuilder sb = new StringBuilder();
			for (ItemInstance item : player.getInventory().getItems())
			{
				if (item == null)
					continue;
				
				if (item.getItemId() == 4442 && item.getCustomType1() < lotoNumber)
				{
					StringUtil.append(sb, "<a action=\"bypass -h npc_%objectId%_Loto ", item.getObjectId(), "\">", item.getCustomType1(), " Event Number ");
					
					int[] numbers = Lottery.decodeNumbers(item.getEnchantLevel(), item.getCustomType2());
					for (int i = 0; i < 5; i++)
						StringUtil.append(sb, numbers[i], " ");
					
					int[] check = Lottery.checkTicket(item);
					if (check[0] > 0)
					{
						switch (check[0])
						{
							case 1:
								sb.append("- 1st Prize");
								break;
							case 2:
								sb.append("- 2nd Prize");
								break;
							case 3:
								sb.append("- 3th Prize");
								break;
							case 4:
								sb.append("- 4th Prize");
								break;
						}
						StringUtil.append(sb, " ", check[1], "a.");
					}
					sb.append("</a><br>");
				}
			}
			
			if (sb.length() == 0)
				sb.append("There is no winning lottery ticket...<br>");
			
			html.setFile(getHtmlPath(npcId, 4));
			html.replace("%result%", sb.toString());
		}
		else if (val == 25) // 25 - lottery instructions
		{
			html.setFile(getHtmlPath(npcId, 2));
			html.replace("%prize5%", Config.ALT_LOTTERY_5_NUMBER_RATE * 100);
			html.replace("%prize4%", Config.ALT_LOTTERY_4_NUMBER_RATE * 100);
			html.replace("%prize3%", Config.ALT_LOTTERY_3_NUMBER_RATE * 100);
			html.replace("%prize2%", Config.ALT_LOTTERY_2_AND_1_NUMBER_PRIZE);
		}
		else if (val > 25) // >25 - check lottery ticket by item object id
		{
			int lotonumber = Lottery.getInstance().getId();
			ItemInstance item = player.getInventory().getItemByObjectId(val);
			if (item == null || item.getItemId() != 4442 || item.getCustomType1() >= lotonumber)
				return;
			int[] check = Lottery.checkTicket(item);
			
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S2_S1_DISAPPEARED).addItemName(4442));
			
			int adena = check[1];
			if (adena > 0)
				player.addAdena("Loto", adena, this, true);
			player.destroyItem("Loto", item, this, false);
			return;
		}
		html.replace("%objectId%", getObjectId());
		html.replace("%race%", Lottery.getInstance().getId());
		html.replace("%adena%", Lottery.getInstance().getPrize());
		html.replace("%ticket_price%", Config.ALT_LOTTERY_TICKET_PRICE);
		html.replace("%enddate%", DateFormat.getDateInstance().format(Lottery.getInstance().getEndDate()));
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public void makeCPRecovery(Player player)
	{
		if (getNpcId() != 31225 && getNpcId() != 31226)
			return;
		
		if (player.isCursedWeaponEquipped())
		{
			player.sendMessage("Go away, you're not welcome here.");
			return;
		}
		
		// Consume 100 adenas
		if (player.reduceAdena("RestoreCP", 100, player.getCurrentFolkNPC(), true))
		{
			setTarget(player);
			doCast(FrequentSkill.ARENA_CP_RECOVERY.getSkill());
			player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.S1_CP_WILL_BE_RESTORED).addCharName(player));
		}
	}
	
	/**
	 * Add Newbie buffs to Player according to its level.
	 * @param player : The player that talk with the Npc.
	 */
	public void makeSupportMagic(Player player)
	{
		if (player == null)
			return;
		
		// Prevent a cursed weapon wielder of being buffed.
		if (player.isCursedWeaponEquipped())
			return;
		
		int playerLevel = player.getLevel();
		int lowestLevel = 0;
		int higestLevel = 0;
		
		// Select the player.
		setTarget(player);
		
		// Calculate the min and max level between which the player must be to obtain buff.
		if (player.isMageClass())
		{
			lowestLevel = NewbieBuffData.getInstance().getMagicLowestLevel();
			higestLevel = NewbieBuffData.getInstance().getMagicHighestLevel();
		}
		else
		{
			lowestLevel = NewbieBuffData.getInstance().getPhysicLowestLevel();
			higestLevel = NewbieBuffData.getInstance().getPhysicHighestLevel();
		}
		
		// If the player is too high level, display a message and return.
		if (playerLevel > higestLevel || !player.isNewbie())
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Newbie Guide:<br>Only a <font color=\"LEVEL\">novice character of level " + higestLevel + " or less</font> can receive my support magic.<br>Your novice character is the first one that you created and raised in this world.</body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		
		// If the player is too low level, display a message and return.
		if (playerLevel < lowestLevel)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml("<html><body>Come back here when you have reached level " + lowestLevel + ". I will give you support magic then.</body></html>");
			html.replace("%objectId%", getObjectId());
			player.sendPacket(html);
			return;
		}
		
		// Go through the NewbieBuffData list and cast skills.
		for (NewbieBuff buff : NewbieBuffData.getInstance().getBuffs())
		{
			if (buff.isMagicClassBuff() == player.isMageClass() && playerLevel >= buff.getLowerLevel() && playerLevel <= buff.getUpperLevel())
			{
				final L2Skill skill = SkillTable.getInstance().getInfo(buff.getSkillId(), buff.getSkillLevel());
				if (skill.getSkillType() == L2SkillType.SUMMON)
					player.doCast(skill);
				else
					doCast(skill);
			}
		}
	}
	
	/**
	 * Returns true if html exists
	 * @param player
	 * @param type
	 * @return boolean
	 */
	private boolean showPkDenyChatWindow(Player player, String type)
	{
		String content = HtmCache.getInstance().getHtm("data/html/" + type + "/" + getNpcId() + "-pk.htm");
		if (content != null)
		{
			final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
			html.setHtml(content);
			player.sendPacket(html);
			player.sendPacket(ActionFailed.STATIC_PACKET);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Open a chat window on client with the text of the L2Npc.
	 * @param player : The player that talk with the L2Npc.
	 */
	public void showChatWindow(Player player)
	{
		showChatWindow(player, 0);
	}
	
	public void showChatWindow(Player player, int val)
	{
		if (player.getKarma() > 0)
		{
			if (!Config.KARMA_PLAYER_CAN_SHOP)
			{
				if (this instanceof Fisherman)
				{
					if (showPkDenyChatWindow(player, "fisherman"))
						return;
				}
				else if (this instanceof Merchant)
				{
					if (showPkDenyChatWindow(player, "merchant"))
						return;
				}
			}
			else if (!Config.KARMA_PLAYER_CAN_USE_GK && this instanceof Gatekeeper)
			{
				if (showPkDenyChatWindow(player, "teleporter"))
					return;
			}
			else if (!Config.KARMA_PLAYER_CAN_USE_WH && this instanceof WarehouseKeeper)
			{
				if (showPkDenyChatWindow(player, "warehouse"))
					return;
			}
		}
		
		final int npcId = getNpcId();
		String filename;
		
		if (npcId >= 31865 && npcId <= 31918)
			filename = SevenSigns.SEVEN_SIGNS_HTML_PATH + "rift/GuardianOfBorder.htm";
		else
			filename = getHtmlPath(npcId, val);
		
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%name%", getName());
		html.replace("%countbuff%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		html.replace("%fafurions%", fafurions());
		html.replace("%palibati%", palibati());
		html.replace("%beast%", beast());
		html.replace("%plague%", plague());
		html.replace("%water%", water());
		html.replace("%krokian%", krokian());
		html.replace("%olkuth%", olkuth());
		html.replace("%glaki%", glaki());
		html.replace("%ocean%", ocean());
		html.replace("%taik%", taik());
		html.replace("%lord%", lord());
		html.replace("%barakiel%", barakiel());
		html.replace("%hekaton%", hekaton());
		html.replace("%brakki%", brakki());
		html.replace("%tayr%", tayr());
		html.replace("%moss%", moss());
		html.replace("%horus%", horus());
		html.replace("%shadith%", shadith());
		html.replace("%orfen%", orfen());
		html.replace("%core%", core());
		html.replace("%sailren%", sailren());
		html.replace("%zaken%", zaken());
		html.replace("%baium%", baium());
		html.replace("%antharas%", antharas());
		html.replace("%valakas%", valakas());
		html.replace("%frintezza%", frintezza());
		html.replace("%queen%", queen());
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	public static String fafurions()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : fafurions)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String palibati()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : palibati)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String beast()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : beast)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String plague()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : plague)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String water()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : water)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String krokian()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : krokian)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String olkuth()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : olkuth)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String glaki()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : glaki)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String ocean()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : ocean)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String taik()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : taik)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String lord()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : lord)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String barakiel()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : barakiel)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String hekaton()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : hekaton)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String brakki()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : brakki)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String tayr()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : tayr)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String shadith()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : shadith)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String moss()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : moss)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String horus()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : horus)
		{
			long delay = 0;
			if (NpcTable.getInstance().getTemplate(boss).isType("RaidBoss"))
			{
				delay = RaidBossInfoManager.getInstance().getRaidBossRespawnTime(boss);
				
			}
			else
				continue;
			
			if (delay <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String orfen()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : orfen)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String core()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : core)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String queen()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : queen)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String baium()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : baium)
		{
			
			StatsSet infobaium = GrandBossManager.getInstance().getStatsSet(boss);
			long tempbaium = infobaium.getLong("respawn_time");
			
			if (tempbaium <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String antharas()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : antharas)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String valakas()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : valakas)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String frintezza()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : frintezza)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String sailren()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : sailren)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	public static String zaken()
	{
		StringBuilder sb = new StringBuilder();
		
		for (int boss : zaken)
		{
			StatsSet infogrand = GrandBossManager.getInstance().getStatsSet(boss);
			long tempgrand = infogrand.getLong("respawn_time");
			
			if (tempgrand <= System.currentTimeMillis())
			{
		        sb.append("<font color=\"9CC300\">Alive</font>");
			}
			else
			{
				sb.append("<font color=\"ff4d4d\">Dead</font>");
			}
		}
		return sb.toString();
	}
	
	/**
	 * Open a chat window on client with the text specified by the given file name and path, relative to the datapack root.
	 * @param player : The player that talk with the L2Npc.
	 * @param filename : The filename that contains the text to send.
	 */
	public void showChatWindow(Player player, String filename)
	{
		final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
		html.setFile(filename);
		html.replace("%objectId%", getObjectId());
		html.replace("%countbuff%", player.getBuffCount() + " / " + player.getMaxBuffCount());
		player.sendPacket(html);
		
		// Send a Server->Client ActionFailed to the Player in order to avoid that the client wait another packet
		player.sendPacket(ActionFailed.STATIC_PACKET);
	}
	
	/**
	 * @return the Exp Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_XP).
	 */
	public int getExpReward()
	{
		return (int) (getTemplate().getRewardExp() * Config.RATE_XP);
	}
	
	/**
	 * @return the SP Reward of this L2Npc contained in the L2NpcTemplate (modified by RATE_SP).
	 */
	public int getSpReward()
	{
		return (int) (getTemplate().getRewardSp() * Config.RATE_SP);
	}
	
	/**
	 * Kill the L2Npc (the corpse disappeared after 7 seconds).<BR>
	 * <BR>
	 * <B><U> Actions</U> :</B><BR>
	 * <BR>
	 * <li>Create a DecayTask to remove the corpse of the L2Npc after 7 seconds</li> <li>Set target to null and cancel Attack or Cast</li> <li>Stop movement</li> <li>Stop HP/MP/CP Regeneration task</li> <li>Stop all active skills effects in progress on the Creature</li> <li>Send the Server->Client
	 * packet StatusUpdate with current HP and MP to all other Player to inform</li> <li>Notify Creature AI</li><BR>
	 * <BR>
	 * <B><U> Overriden in </U> :</B><BR>
	 * <BR>
	 * <li>L2Attackable</li><BR>
	 * <BR>
	 * @param killer The Creature who killed it
	 */
	@Override
	public boolean doDie(Creature killer)
	{
		if (!super.doDie(killer))
			return false;
		
		_currentLHandId = getTemplate().getLeftHand();
		_currentRHandId = getTemplate().getRightHand();
		_currentEnchant = getTemplate().getEnchantEffect();
		_currentCollisionHeight = getTemplate().getCollisionHeight();
		_currentCollisionRadius = getTemplate().getCollisionRadius();
		DecayTaskManager.getInstance().add(this, getTemplate().getCorpseTime());
		return true;
	}
	
	/**
	 * Set the spawn of the L2Npc.<BR>
	 * <BR>
	 * @param spawn The L2Spawn that manage the L2Npc
	 */
	public void setSpawn(L2Spawn spawn)
	{
		_spawn = spawn;
	}
	
	@Override
	public void onSpawn()
	{
		super.onSpawn();
		
		// initialize ss/sps counts.
		_currentSsCount = getTemplate().getSsCount();
		_currentSpsCount = getTemplate().getSpsCount();
		
		List<Quest> quests = getTemplate().getEventQuests(EventType.ON_SPAWN);
		if (quests != null)
			for (Quest quest : quests)
				quest.notifySpawn(this);
	}
	
	@Override
	public void onDecay()
	{
		if (isDecayed())
			return;
		
		setDecayed(true);
		
		List<Quest> quests = getTemplate().getEventQuests(EventType.ON_DECAY);
		if (quests != null)
			for (Quest quest : quests)
				quest.notifyDecay(this);
		
		// Remove the L2Npc from the world when the decay task is launched.
		super.onDecay();
		
		// Respawn it, if possible.
		if (_spawn != null)
			_spawn.doRespawn();
	}
	
	@Override
	public void deleteMe()
	{
		// Decay
		onDecay();
		
		super.deleteMe();
	}
	
	/**
	 * @return the L2Spawn object that manage this L2Npc.
	 */
	public L2Spawn getSpawn()
	{
		return _spawn;
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
	
	public boolean isDecayed()
	{
		return _isDecayed;
	}
	
	public void setDecayed(boolean decayed)
	{
		_isDecayed = decayed;
	}
	
	public void endDecayTask()
	{
		if (!isDecayed())
		{
			DecayTaskManager.getInstance().cancel(this);
			onDecay();
		}
	}
	
	/**
	 * Used for animation timers, overridden in L2Attackable.
	 * @return true if L2Attackable, false otherwise.
	 */
	public boolean isMob()
	{
		return false;
	}
	
	public void setLHandId(int newWeaponId)
	{
		_currentLHandId = newWeaponId;
	}
	
	public void setRHandId(int newWeaponId)
	{
		_currentRHandId = newWeaponId;
	}
	
	public void setEnchant(int enchant)
	{
		_currentEnchant = enchant;
	}
	
	public void setCollisionHeight(double height)
	{
		_currentCollisionHeight = height;
	}
	
	@Override
	public double getCollisionHeight()
	{
		return _currentCollisionHeight;
	}
	
	public void setCollisionRadius(double radius)
	{
		_currentCollisionRadius = radius;
	}
	
	@Override
	public double getCollisionRadius()
	{
		return _currentCollisionRadius;
	}
	
	public int getScriptValue()
	{
		return _scriptValue;
	}
	
	public void setScriptValue(int val)
	{
		_scriptValue = val;
	}
	
	public boolean isScriptValue(int val)
	{
		return _scriptValue == val;
	}
	
	public Npc scheduleDespawn(long delay)
	{
		ThreadPool.schedule(new DespawnTask(), delay);
		return this;
	}
	
	protected class DespawnTask implements Runnable
	{
		@Override
		public void run()
		{
			if (!isDecayed())
				deleteMe();
		}
	}
	
	@Override
	protected final void notifyQuestEventSkillFinished(L2Skill skill, WorldObject target)
	{
		try
		{
			List<Quest> quests = getTemplate().getEventQuests(EventType.ON_SPELL_FINISHED);
			if (quests != null)
			{
				Player player = null;
				if (target != null)
					player = target.getActingPlayer();
				
				for (Quest quest : quests)
					quest.notifySpellFinished(this, player, skill);
			}
		}
		catch (Exception e)
		{
			_log.log(Level.SEVERE, "", e);
		}
	}
	
	@Override
	public boolean isMovementDisabled()
	{
		return super.isMovementDisabled() || !getTemplate().canMove() || getTemplate().getAiType().equals(AIType.CORPSE);
	}
	
	@Override
	public boolean isCoreAIDisabled()
	{
		return super.isCoreAIDisabled() || getTemplate().getAiType().equals(AIType.CORPSE);
	}
	
	@Override
	public void sendInfo(Player activeChar)
	{
		if (getMoveSpeed() == 0)
			activeChar.sendPacket(new ServerObjectInfo(this, activeChar));
		else
			activeChar.sendPacket(new NpcInfo(this, activeChar));
	}
	
	@Override
	public boolean isChargedShot(ShotType type)
	{
		return (_shotsMask & type.getMask()) == type.getMask();
	}
	
	@Override
	public void setChargedShot(ShotType type, boolean charged)
	{
		if (charged)
			_shotsMask |= type.getMask();
		else
			_shotsMask &= ~type.getMask();
	}
	
	@Override
	public void rechargeShots(boolean physical, boolean magic)
	{
		if (physical)
		{
			if (_currentSsCount <= 0)
				return;
			
			if (Rnd.get(100) > getTemplate().getSsRate())
				return;
			
			_currentSsCount--;
			Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2154, 1, 0, 0), 600);
			setChargedShot(ShotType.SOULSHOT, true);
		}
		
		if (magic)
		{
			if (_currentSpsCount <= 0)
				return;
			
			if (Rnd.get(100) > getTemplate().getSpsRate())
				return;
			
			_currentSpsCount--;
			Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2061, 1, 0, 0), 600);
			setChargedShot(ShotType.SPIRITSHOT, true);
		}
	}
	
	@Override
	public int getSkillLevel(int skillId)
	{
		for (List<L2Skill> list : getTemplate().getSkills().values())
		{
			for (L2Skill skill : list)
				if (skill.getId() == skillId)
					return skill.getLevel();
		}
		return -1;
	}
	
	@Override
	public L2Skill getSkill(int skillId)
	{
		for (List<L2Skill> list : getTemplate().getSkills().values())
		{
			for (L2Skill skill : list)
				if (skill.getId() == skillId)
					return skill;
		}
		return null;
	}
}