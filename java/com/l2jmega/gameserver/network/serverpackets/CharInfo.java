package com.l2jmega.gameserver.network.serverpackets;

import com.l2jmega.Config;
import com.l2jmega.events.CTF;
import com.l2jmega.events.TvT;
import com.l2jmega.gameserver.data.NpcTable;
import com.l2jmega.gameserver.instancemanager.AioManager;
import com.l2jmega.gameserver.instancemanager.CursedWeaponsManager;
import com.l2jmega.gameserver.model.DressMe;
import com.l2jmega.gameserver.model.L2Party;
import com.l2jmega.gameserver.model.actor.instance.Player;
import com.l2jmega.gameserver.model.itemcontainer.Inventory;
import com.l2jmega.gameserver.model.location.Location;
import com.l2jmega.gameserver.model.zone.ZoneId;
import com.l2jmega.gameserver.skills.AbnormalEffect;

public class CharInfo extends L2GameServerPacket
{
	private final Player _activeChar;
	private final Inventory _inv;
	
	
	public CharInfo(Player cha)
	{
		_activeChar = cha;
		_inv = _activeChar.getInventory();
	}
	
	@Override
	protected final void writeImpl()
	{
		Player player = getClient().getActiveChar();
		boolean gmSeeInvis = false;
		boolean tournament_observer = false;
		boolean tournament_pt = false;
		boolean tournament_start = false;
		boolean Olympiad_start = false;
		boolean Olympiad_observer = false;
		boolean tournament_zone = false;
		
		boolean tvt = false;
		boolean ctf = false;
		
		if (_activeChar._inEventTvT)
		{
			Player tmp = getClient().getActiveChar();
			if (tmp != null)
			{
				if ((TvT.is_started() || TvT.is_teleport()) && tmp._inEventTvT && !_activeChar._teamNameTvT.equals(tmp._teamNameTvT))
					tvt = true;
			}
		}
		
		if (_activeChar._inEventCTF)
		{
			Player tmp = getClient().getActiveChar();
			if (tmp != null)
			{
				if ((CTF.is_started() || CTF.is_teleport()) && tmp._inEventCTF && !_activeChar._teamNameCTF.equals(tmp._teamNameCTF))
					ctf = true;
			}
		}
		
		final L2Party party = _activeChar.getParty();
		
		if (_activeChar.getAppearance().getInvisible())
		{
			Player tmp = getClient().getActiveChar();
			if (tmp != null)
			{
				if (_activeChar.isArenaProtection() && tmp.isArenaObserv())
					tournament_observer = true;
				else if (_activeChar.isArenaProtection() && party != null && !tmp.isArenaAttack() && tmp.isInArenaEvent() && party.getPartyMembers().contains(tmp))
					tournament_pt = true;
				else if (_activeChar.isArenaProtection() && tmp.isArenaAttack() && !tmp.isInObserverMode())
					tournament_start = true;
				else if ((_activeChar.isInsideZone(ZoneId.TOURNAMENT) && !_activeChar.isGM() && !_activeChar.isOlympiadProtection()) && tmp.isInsideZone(ZoneId.TOURNAMENT) && !tmp.isInObserverMode() && !tmp.isOlympiadProtection())
					tournament_zone = true;
				else if (tmp.isGM())
					gmSeeInvis = true;
			}
		}
		
		if (_activeChar.isOlympiadProtection())
		{
			Player tmp = getClient().getActiveChar();
			if (tmp != null)
			{
				if (tmp.isOlympiadProtection() && !tmp.isInObserverMode())
					Olympiad_start = true;
				else if (tmp.isInObserverMode() && !tmp.isArenaObserv())
					Olympiad_observer = true;
			}
		}
		
		writeC(0x03);
		writeD(_activeChar.getX());
		writeD(_activeChar.getY());
		writeD(_activeChar.getZ());
		writeD(_activeChar.getHeading());
		writeD(_activeChar.getObjectId());
		writeS(_activeChar.getName());
		writeD(_activeChar.getRace().ordinal());
		writeD(_activeChar.getAppearance().getSex().ordinal());
		
		if (_activeChar.getClassIndex() == 0)
			writeD(_activeChar.getClassId().getId());
		else
			writeD(_activeChar.getBaseClass());
		
		DressMe dress = _activeChar.getDress();
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIRALL));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_HEAD));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_LHAND));	
		writeD((dress == null) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : ((dress.getGlovesId() == 0) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_GLOVES) : dress.getGlovesId()));
		writeD((dress == null) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : ((dress.getChestId() == 0) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_CHEST) : dress.getChestId()));
		writeD((dress == null) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : ((dress.getLegsId() == 0) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_LEGS) : dress.getLegsId()));
		writeD((dress == null) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET) : ((dress.getFeetId() == 0) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_FEET) : dress.getFeetId()));	
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_BACK));
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_RHAND));		
		writeD((dress == null) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR) : ((dress.getHairId() == 0) ? _inv.getPaperdollItemId(Inventory.PAPERDOLL_HAIR) : dress.getHairId()));	
		writeD(_inv.getPaperdollItemId(Inventory.PAPERDOLL_FACE));
		
		// c6 new h's
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_RHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeD(_inv.getPaperdollAugmentationId(Inventory.PAPERDOLL_LHAND));
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		writeH(0x00);
		
		writeD(_activeChar.getPvpFlag());
		writeD(_activeChar.getKarma());
		
		writeD(_activeChar.getMAtkSpd());
		writeD(_activeChar.getPAtkSpd());
		
		writeD(_activeChar.getPvpFlag());
		writeD(_activeChar.getKarma());
		
		int _runSpd = _activeChar.getStat().getBaseRunSpeed();
		int _walkSpd = _activeChar.getStat().getBaseWalkSpeed();
		int _swimSpd = _activeChar.getStat().getBaseSwimSpeed();
		writeD(_runSpd); // base run speed
		writeD(_walkSpd); // base walk speed
		
		if (_activeChar.isPhantom() && !_activeChar.isPhantomMysticMuse())
			writeD(Config.PHANTOM_SPEED); // swim run speed
		else if (_activeChar.isPhantomMysticMuse())
			writeD(Config.PHANTOM_ATK_SPEED); // swim run speed
		else
			writeD(_swimSpd); // swim run speed
		
		writeD(_swimSpd); // swim walk speed
		writeD(_runSpd);
		writeD(_walkSpd);
		writeD(_activeChar.isFlying() ? _runSpd : 0); // fly run speed
		writeD(_activeChar.isFlying() ? _walkSpd : 0); // fly walk speed
		
		writeF(_activeChar.getStat().getMovementSpeedMultiplier()); // run speed multiplier
		
		writeF(_activeChar.getStat().getAttackSpeedMultiplier()); // attack speed multiplier
		
		if (_activeChar.isPhantomAntBot())
		{
			writeF(0);
			writeF(-150);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 44 && _activeChar.getAppearance().getSex().ordinal() == 0)
		{
			writeF(11);
			writeF(28);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 44 && _activeChar.getAppearance().getSex().ordinal() == 1)
		{
			writeF(7);
			writeF(27);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 49 && _activeChar.getAppearance().getSex().ordinal() == 0)
		{
			writeF(7);
			writeF(27.5);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 49 && _activeChar.getAppearance().getSex().ordinal() == 1)
		{
			writeF(8);
			writeF(25.5);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 53 && _activeChar.getAppearance().getSex().ordinal() == 0)
		{
			writeF(9);
			writeF(18);
		}
		else if (_activeChar.isPhantom() && _activeChar.getClassId().getId() == 53 && _activeChar.getAppearance().getSex().ordinal() == 1)
		{
			writeF(5);
			writeF(19);
		}
		else if (_activeChar.getMountType() != 0)
		{
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionRadius());
			writeF(NpcTable.getInstance().getTemplate(_activeChar.getMountNpcId()).getCollisionHeight());
		}
		else
		{
			writeF(_activeChar.getCollisionRadius());
			writeF(_activeChar.getCollisionHeight());
		}
		
		writeD(_activeChar.getAppearance().getHairStyle());
		writeD(_activeChar.getAppearance().getHairColor());
		writeD(_activeChar.getAppearance().getFace());
		
		if (tvt || ctf)
			writeS("");
		else if(AioManager.getInstance().hasAioPrivileges(this._activeChar.getObjectId()) || _activeChar.isAio())
			writeS(Config.AIO_TITLE);
		else
			writeS(_activeChar.getTitle());
		
		if (((TvT.is_started() || TvT.is_teleport()) && _activeChar._inEventTvT) || ((CTF.is_started() || CTF.is_teleport()) && _activeChar._inEventCTF))
		{
			writeD(0);
			writeD(0);
			writeD(0);
			writeD(0);
		}
		else
		{
			writeD(_activeChar.getClanId());
			writeD(_activeChar.getClanCrestId());
			writeD(_activeChar.getAllyId());
			writeD(_activeChar.getAllyCrestId());
		}
		
		writeD(0);
		
		if (_activeChar.isPhantomAntBot())
			writeC(0); // standing = 1 sitting = 0
		else
			writeC(_activeChar.isSitting() ? 0 : 1); // standing = 1 sitting = 0
		
		writeC(_activeChar.isRunning() ? 1 : 0); // running = 1 walking = 0
		writeC(_activeChar.isInCombat() ? 1 : 0);
		writeC(_activeChar.isAlikeDead() ? 1 : 0);
		
		if (gmSeeInvis || tournament_pt || tournament_start || tournament_observer || Olympiad_start || Olympiad_observer || tournament_zone)
			writeC(0);
		else
			writeC(_activeChar.getAppearance().getInvisible() ? 1 : 0); // invisible = 1 visible =0
		
		writeC(_activeChar.getMountType()); // 1 on strider 2 on wyvern 0 no mount
		writeC(_activeChar.getStoreType().getId()); // 1 - sellshop
		
		writeH(_activeChar.getCubics().size());
		for (int id : _activeChar.getCubics().keySet())
			writeH(id);
		
		writeC(_activeChar.isInPartyMatchRoom() ? 1 : 0);
		
		if (gmSeeInvis) {
			writeD(_activeChar.getAbnormalEffect() | AbnormalEffect.STEALTH.getMask());
		} else {
			writeD(_activeChar.getAbnormalEffect());
		}
		
		writeC(_activeChar.getRecomLeft());
		writeH(_activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
		writeD(_activeChar.getClassId().getId());
		
		writeD(_activeChar.getMaxCp());
		writeD((int) _activeChar.getCurrentCp());
		// visao do target
		//if (_activeChar.isInOlympiadMode())
		//	writeC(_activeChar.isMounted() ? 0 : _activeChar.OlympiadEnchantEffect());
		//else if (Config.DISABLE_DONATE_ENCHANT)
		//	writeC(_activeChar.isMounted() ? 0 : _activeChar.CustomEnchantEffect());
		if (player.isDisableGlowWeapon()){
			writeC(0);
		}else{
			writeC(_activeChar.isMounted() ? 0 : _activeChar.getEnchantEffect());
		}
		if (_activeChar.getTeam() == 1 || _activeChar.getTeamTour() == 1)
			writeC(0x01); // team circle around feet 1= Blue, 2 = red
		else if (_activeChar.getTeam() == 2 || _activeChar.getTeamTour() == 2)
			writeC(0x02); // team circle around feet 1= Blue, 2 = red
		else
			writeC(0x00); // team circle around feet 1= Blue, 2 = red
		
		writeD(_activeChar.getClanCrestLargeId());
		writeC(_activeChar.isNoble() ? 1 : 0); // Symbol on char menu ctrl+I
		
		if (_activeChar.isHero() && (((TvT.is_started() || TvT.is_teleport()) && _activeChar._inEventTvT) || ((CTF.is_started() || CTF.is_teleport()) && _activeChar._inEventCTF)))
			writeC(0x00);
		else
			writeC((_activeChar.isHero() || (_activeChar.isGM() && Config.GM_HERO_AURA)) ? 1 : 0); // Hero Aura
		
		writeC(_activeChar.isFishing() ? 1 : 0); // 0x01: Fishing Mode (Cant be undone by setting back to 0)
		
		Location loc = _activeChar.getFishingLoc();
		if (loc != null)
		{
			writeD(loc.getX());
			writeD(loc.getY());
			writeD(loc.getZ());
		}
		else
		{
			writeD(0);
			writeD(0);
			writeD(0);
		}
		
		writeD(_activeChar.getAppearance().getNameColor());
		
		writeD(0x00); // isRunning() as in UserInfo?
		
		writeD(_activeChar.getPledgeClass());
		writeD(_activeChar.getPledgeType());
		
		writeD(AioManager.getInstance().hasAioPrivileges(_activeChar.getObjectId()) ? Config.AIO_TCOLOR : _activeChar.getAppearance().getTitleColor());
		
		if (_activeChar.isCursedWeaponEquipped())
			writeD(CursedWeaponsManager.getInstance().getCurrentStage(_activeChar.getCursedWeaponEquippedId()) - 1);
		else
			writeD(0x00);
	}
}