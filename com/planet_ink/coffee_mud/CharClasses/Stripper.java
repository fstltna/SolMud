package com.planet_ink.coffee_mud.CharClasses;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2003-2024 Bo Zimmerman

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

	   http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
public class Stripper extends StdCharClass
{
	@Override
	public String ID()
	{
		return "Stripper";
	}

	private final static String localizedStaticName = CMLib.lang().L("Stripper");

	@Override
	public String name()
	{
		return localizedStaticName;
	}

	@Override
	public String baseClass()
	{
		return "Bard";
	}

	@Override
	public String getMovementFormula()
	{
		return "8*((@x2<@x3)/18)";
	}

	@Override
	public int getBonusPracLevel()
	{
		return 1;
	}

	@Override
	public int getBonusAttackLevel()
	{
		return 0;
	}

	@Override
	public int getAttackAttribute()
	{
		return CharStats.STAT_CHARISMA;
	}

	@Override
	public int getLevelsPerBonusDamage()
	{
		return 10;
	}

	@Override
	public String getHitPointsFormula()
	{
		return "((@x6<@x7)/2)+(2*(1?7))";
	}

	@Override
	public String getManaFormula()
	{
		return "((@x4<@x5)/6)+(1*(1?2))";
	}

	@Override
	protected String armorFailMessage()
	{
		return L("<S-NAME> armor makes <S-HIM-HER> mess up <S-HIS-HER> <SKILL>!");
	}

	@Override
	public int allowedArmorLevel()
	{
		return CharClass.ARMOR_CLOTH;
	}

	@Override
	public int allowedWeaponLevel()
	{
		return CharClass.WEAPONS_THIEFLIKE;
	}

	private final Set<Integer> disallowedWeapons = buildDisallowedWeaponClasses();

	@Override
	protected Set<Integer> disallowedWeaponClasses(final MOB mob)
	{
		return disallowedWeapons;
	}

	public Stripper()
	{
		super();
		maxStatAdj[CharStats.STAT_CHARISMA]=4;
		maxStatAdj[CharStats.STAT_STRENGTH]=4;
	}

	@Override
	public void initializeClass()
	{
		super.initializeClass();
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Natural",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Ranged",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Specialization_Sword",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Recall",50,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Write",50,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Swim",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Skill_Befriend",50,true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Dance_Stop",100,true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),1,"Dance_CanCan",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),2,"Thief_Lore",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),2,"Dance_Foxtrot",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),2,"Thief_Hide",false);

		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Fighter_Kick",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Skill_Climb",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),3,"Dance_Tarantella",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Thief_Sneak",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Thief_Autosneak",false,CMParms.parseSemicolons("*_Sneak",true));
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Thief_Appraise",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),4,"Dance_Waltz",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Skill_Dodge",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Dance_Salsa",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),5,"Dance_Grass",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),6,"Dance_Clog",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),6,"Fighter_GracefulDismount",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Thief_Distract",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),7,"Dance_Capoeira",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Dance_Tap",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),8,"Fighter_Spring",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Skill_Disarm",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),9,"Dance_Basse",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Fighter_BodyFlip",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),10,"Dance_Tango",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Fighter_Spring",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Dance_Polka",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),11,"Skill_DenyBackdoor",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),12,"Fighter_DesperateMoves",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),12,"Dance_Manipuri",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Skill_Trip",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),13,"Dance_Cotillon",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Skill_TwoWeaponFighting",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Dance_Ballet",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),14,"Thief_Surrender",false);

		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Fighter_Tumble",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),15,"Dance_Jitterbug",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),16,"Dance_Butoh",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Skill_Attack2",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Dance_Courante",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Skill_Attack2",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),17,"Thief_CarefulStep",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Dance_Musette",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Skill_FullNelson",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),18,"Skill_Amazonride",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Fighter_Endurance",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"Fighter_Cartwheel",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),19,"fighter_CoverDefence",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),20,"Fighter_CircleTrip",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Fighter_Roll",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),21,"Dance_Jingledress",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),22,"Dance_Morris",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Fighter_BlindFighting",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),23,"Skill_ImprovedThrowing",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Dance_Macabre",true);
		CMLib.ableMapper().addCharAbilityMapping(ID(),24,"Thief_EscapeBonds",false);

		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Fighter_CircleTrip",false);
		CMLib.ableMapper().addCharAbilityMapping(ID(),25,"Dance_War",true);

		CMLib.ableMapper().addCharAbilityMapping(ID(),30,"Dance_Square",true);
	}

	@Override
	public int availabilityCode()
	{
		return Area.THEME_FANTASY;
	}

	@Override
	public void executeMsg(final Environmental host, final CMMsg msg)
	{
		super.executeMsg(host,msg);
		Bard.visitationBonusMessage(host,msg);
	}

	private final String[] raceRequiredList=new String[]{
		"Human","Humanoid","Elf","Halfling","Githyanki","Aarakocran","Merfolk","Faerie"
	};

	@Override
	public String[] getRequiredRaceList()
	{
		return raceRequiredList;
	}

	@SuppressWarnings("unchecked")
	private final Pair<String,Integer>[] minimumStatRequirements=new Pair[]{
		new Pair<String,Integer>("Charisma",Integer.valueOf(9)),
		new Pair<String,Integer>("Strength",Integer.valueOf(9))
	};

	@Override
	public Pair<String, Integer>[] getMinimumStatRequirements()
	{
		return minimumStatRequirements;
	}

	protected boolean stripCheck(final MOB mob)
	{
		for(int i=0;i<mob.numItems();i++)
		{
			final Item I=mob.getItem(i);
			if((I!=null)
			&&(I.amWearingAt(Wearable.WORN_TORSO)
				||I.amWearingAt(Wearable.WORN_ABOUT_BODY))
			&&(((I instanceof Armor)||(I instanceof Shield)))
			&&(!(I instanceof FalseLimb))
			&&(!(I instanceof BodyToken)))
				return false;
		}
		return true;
	}


	protected WeakHashMap<MOB,Boolean> stripMap=new WeakHashMap<MOB,Boolean>();

	@Override
	public void affectCharStats(final MOB affected, final CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		// must be the last thing in this method
		Boolean dostrip = null;
		synchronized(stripMap)
		{
			if(stripMap.containsKey(affected))
				dostrip=stripMap.get(affected);
		}
		if(dostrip==null)
		{
			dostrip=Boolean.valueOf(stripCheck(affected));
			synchronized(stripMap)
			{
				stripMap.put(affected, dostrip);
			}
		}
		if(dostrip.booleanValue())
		{
			affectableStats.setStat(CharStats.STAT_MAX_DEXTERITY_ADJ,
					affectableStats.getStat(CharStats.STAT_MAX_DEXTERITY_ADJ)+1+(int)Math.round(Math.floor(affectableStats.getClassLevel(this)/30)));
			affectableStats.setStat(CharStats.STAT_MAX_CHARISMA_ADJ,
					affectableStats.getStat(CharStats.STAT_MAX_CHARISMA_ADJ)+1+(int)Math.round(Math.floor(affectableStats.getClassLevel(this)/15)));
		}
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(myHost instanceof MOB))
			return super.okMessage(myHost,msg);
		final MOB myChar=(MOB)myHost;

		if((msg.source()==myChar)
		&&(msg.target() instanceof Item)
		&&((msg.targetMinor()==CMMsg.TYP_WEAR)||(msg.targetMinor()==CMMsg.TYP_REMOVE)))
		{
			synchronized(stripMap)
			{
				stripMap.remove(msg.source());
			}
		}
		else
		if(msg.amITarget(myChar))
		{
			if((msg.tool() instanceof Weapon)
			&&(msg.targetMinor()==CMMsg.TYP_DAMAGE))
			{
				final int classLevel=myChar.charStats().getClassLevel(this);
				int recovery=(classLevel/5);
				final double minPct=.10+((classLevel>33)?((classLevel-30)*.0025):0);
				final int minAmount=(int)Math.round(CMath.mul(msg.value(), minPct));
				if(recovery < minAmount)
					recovery=minAmount;
				msg.setValue(msg.value()-recovery);
			}
			else
			if((CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS))
			&&(msg.tool() instanceof Ability)
			&&((((Ability)msg.tool()).classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_ENCHANTMENT)
			&&(msg.sourceMinor()!=CMMsg.TYP_TEACH))
			{
				if(CMLib.dice().rollPercentage()<=myChar.charStats().getClassLevel(this))
				{
					myChar.location().show(myChar,null,msg.source(),CMMsg.MSG_OK_ACTION,L("<S-NAME> gracefully avoid(s) the @x1 attack from <O-NAMESELF>!",msg.tool().name()));
					return false;
				}
			}
		}
		return super.okMessage(myChar,msg);
	}

	@Override
	public String getOtherLimitsDesc()
	{
		return "";
	}

	@Override
	public List<Item> outfit(final MOB myChar)
	{
		if(outfitChoices==null)
		{
			final Weapon w=CMClass.getWeapon("Shortsword");
			if(w == null)
				return new Vector<Item>();
			outfitChoices=new Vector<Item>();
			outfitChoices.add(w);
			cleanOutfit(outfitChoices);
		}
		return outfitChoices;
	}

	@Override
	public void grantAbilities(final MOB mob, final boolean isBorrowedClass)
	{
		super.grantAbilities(mob,isBorrowedClass);
		if(mob.playerStats()==null)
		{
			final List<AbilityMapper.AbilityMapping> V=CMLib.ableMapper().getUpToLevelListings(ID(),
												mob.charStats().getClassLevel(ID()),
												false,
												false);
			for(final AbilityMapper.AbilityMapping able : V)
			{
				final Ability A=CMClass.getAbility(able.abilityID());
				if((A!=null)
				&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG)
				&&(!CMLib.ableMapper().getDefaultGain(ID(),true,A.ID())))
				{
					giveMobAbility(mob,A,
								   CMLib.ableMapper().getDefaultProficiency(ID(),true,A.ID()),
								   CMLib.ableMapper().getDefaultParm(ID(),true,A.ID()),
								   isBorrowedClass);
				}
			}
		}
	}

	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected instanceof MOB)
		{
			if(CMLib.flags().isStanding((MOB)affected))
			{
				final MOB mob=(MOB)affected;
				final int attArmor=(((int)Math.round(CMath.div(mob.charStats().getStat(CharStats.STAT_DEXTERITY),9.0)))+1)*(mob.charStats().getClassLevel(this)-1);
				affectableStats.setArmor(affectableStats.armor()-attArmor);
			}
		}
	}

	@Override
	public int adjustExperienceGain(final MOB host, final MOB mob, final MOB victim, final int amount)
	{
		return Bard.bardAdjustExperienceGain(host, mob, victim, amount, 5.0);
	}

	@Override
	public String getOtherBonusDesc()
	{
		return L("Receives defensive bonus for high dexterity.  Receives group bonus combat experience when in an intelligent group, and more for a group of players.  "
				+ "Receives exploration and pub-finding experience based on danger level. Bonus max dex/30 and cha/15 levels when torso and body are bare.");
	}

	@Override
	public void level(final MOB mob, final List<String> newAbilityIDs)
	{
		super.level(mob, newAbilityIDs);
		if(CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS))
			return;
		final int attArmor=(((int)Math.round(CMath.div(mob.charStats().getStat(CharStats.STAT_DEXTERITY),9.0)))+1);
		mob.tell(L("^NYour grace grants you a defensive bonus of ^H@x1^?.^N",""+attArmor));
	}
}

