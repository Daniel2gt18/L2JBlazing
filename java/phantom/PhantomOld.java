package phantom;

import java.util.logging.Level;

import com.l2jmega.gameserver.data.SkillTable.FrequentSkill;
import com.l2jmega.gameserver.data.xml.AdminData;
import com.l2jmega.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jmega.gameserver.model.L2Effect;
import com.l2jmega.gameserver.model.L2Party.MessageType;
import com.l2jmega.gameserver.model.World;
import com.l2jmega.gameserver.model.WorldObject;
import com.l2jmega.gameserver.model.actor.Creature;
import com.l2jmega.gameserver.model.actor.appearance.PcAppearance;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.actor.instance.StaticObject;
import com.l2jmega.gameserver.model.actor.template.PlayerTemplate;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.olympiad.OlympiadManager;
import com.l2jmega.gameserver.model.pledge.ClanMember;
import com.l2jmega.gameserver.network.serverpackets.PledgeShowMemberListUpdate;

/**
 * @author Elfocrash
 */
public class PhantomOld extends Player
{
	
	protected PhantomOld(int objectId)
	{
		super(objectId);
	}
	
	public PhantomOld(int objectId, PlayerTemplate template, String accountName, PcAppearance app)
	{
		super(objectId, template, accountName, app);
	}
	
	public synchronized void despawnPlayer()
	{
		try
		{
			// Put the online status to false
			setOnlineStatus(false, true);
			
			// abort cast & attack and remove the target. Cancels movement aswell.
			abortAttack();
			abortCast();
			stopMove(null);
			setTarget(null);
			
			removeMeFromPartyMatch();
			
			if (isFlying())
				removeSkill(FrequentSkill.WYVERN_BREATH.getSkill().getId(), false);
			
			// Stop all scheduled tasks
			stopAllTimers();
			
			// Cancel the cast of eventual fusion skill users on this target.
			for (Creature character : getKnownType(Creature.class))
				if (character.getFusionSkill() != null && character.getFusionSkill().getTarget() == this)
					character.abortCast();
			
			// Stop signets & toggles effects.
			for (L2Effect effect : getAllEffects())
			{
				if (effect.getSkill().isToggle())
				{
					effect.exit();
					continue;
				}
				
				switch (effect.getEffectType())
				{
					case SIGNET_GROUND:
					case SIGNET_EFFECT:
						effect.exit();
						break;
					default:
						break;
				}
			}
			
			// Remove the Player from the world
			decayMe();
			
			// If a party is in progress, leave it
			if (getParty() != null)
				getParty().removePartyMember(this, MessageType.Disconnected);
			
			// If the Player has Pet, unsummon it
			if (getPet() != null)
				getPet().unSummon(this);
			
			// Handle removal from olympiad game
			if (OlympiadManager.getInstance().isRegistered(this) || getOlympiadGameId() != -1)
				OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
			
			// set the status for pledge member list to OFFLINE
			if (getClan() != null)
			{
				ClanMember clanMember = getClan().getClanMember(getObjectId());
				if (clanMember != null)
					clanMember.setPlayerInstance(null);
			}
			
			// deals with sudden exit in the middle of transaction
			if (getActiveRequester() != null)
			{
				setActiveRequester(null);
				cancelActiveTrade();
			}
			
			// If the Player is a GM, remove it from the GM List
			if (isGM())
				AdminData.getInstance().deleteGm(this);
			
			// Check if the Player is in observer mode to set its position to its position before entering in observer mode
			if (isInObserverMode())
				setXYZInvisible(getSavedLocation());
			
			// Oust player from boat
			if (getVehicle() != null)
				getVehicle().oustPlayer(this, true, Location.DUMMY_LOC);
			
			// Update inventory and remove them from the world
			getInventory().deleteMe();
			
			// Update warehouse and remove them from the world
			clearWarehouse();
			
			// Update freight and remove them from the world
			clearFreight();
			clearDepositedFreight();
			
			if (isCursedWeaponEquipped())
				CursedWeaponsManager.getInstance().getCursedWeapon(getCursedWeaponEquippedId()).setPlayer(null);
			
			if (getClanId() > 0)
				getClan().broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
			
			if (isSeated())
			{
				final WorldObject object = World.getInstance().getObject(_throneId);
				if (object instanceof StaticObject)
					((StaticObject) object).setBusy(false);
			}
			
			World.getInstance().removePlayer(this); // force remove in case of crash during teleport
			
			// friends & blocklist update
			notifyFriends(false);
			getBlockList().playerLogout();
		}
		catch (Exception e)
		{
			_log.log(Level.WARNING, "Exception on deleteMe()" + e.getMessage(), e);
		}
	}
	
	public void heal()
	{
		setCurrentCp(getMaxCp());
		setCurrentHp(getMaxHp());
		setCurrentMp(getMaxMp());
	}
}
