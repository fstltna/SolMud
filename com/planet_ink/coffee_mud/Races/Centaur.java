package com.planet_ink.coffee_mud.Races;
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
   Copyright 2001-2024 Bo Zimmerman

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
public class Centaur extends StdRace
{
	@Override
	public String ID()
	{
		return "Centaur";
	}

	private final static String localizedStaticName = CMLib.lang().L("Centaur");

	@Override
	public String name()
	{
		return localizedStaticName;
	}

	@Override
	public int shortestMale()
	{
		return 60;
	}

	@Override
	public int shortestFemale()
	{
		return 60;
	}

	@Override
	public int heightVariance()
	{
		return 12;
	}

	@Override
	public int lightestWeight()
	{
		return 1150;
	}

	@Override
	public int weightVariance()
	{
		return 400;
	}

	@Override
	public long forbiddenWornBits()
	{
		return Wearable.WORN_WAIST | Wearable.WORN_LEGS | Wearable.WORN_FEET;
	}

	private final static String localizedStaticRacialCat = CMLib.lang().L("Equine");

	@Override
	public String racialCategory()
	{
		return localizedStaticRacialCat;
	}

	@Override
	public boolean useRideClass()
	{
		return true;
	}

	private final String[]	racialEffectNames			= { "Fighter_Hardiness"};
	private final int[]		racialEffectLevels			= { 1};
	private final String[]	racialEffectParms			= { "" };

	@Override
	protected String[] racialEffectNames()
	{
		return racialEffectNames;
	}

	@Override
	protected int[] racialEffectLevels()
	{
		return racialEffectLevels;
	}

	@Override
	protected String[] racialEffectParms()
	{
		return racialEffectParms;
	}

	private final String[]	racialAbilityNames			= { "Skill_Buck" };
	private final int[]		racialAbilityLevels			= { 5 };
	private final int[]		racialAbilityProficiencies	= { 50 };
	private final boolean[]	racialAbilityQuals			= { false, false };
	private final String[]	racialAbilityParms			= { "", "" };

	@Override
	public String[] racialAbilityNames()
	{
		return racialAbilityNames;
	}

	@Override
	public int[] racialAbilityLevels()
	{
		return racialAbilityLevels;
	}

	@Override
	public int[] racialAbilityProficiencies()
	{
		return racialAbilityProficiencies;
	}

	@Override
	public boolean[] racialAbilityQuals()
	{
		return racialAbilityQuals;
	}

	@Override
	public String[] racialAbilityParms()
	{
		return racialAbilityParms;
	}

	private final String[]	culturalAbilityNames			= { "Elvish", "Foraging" };
	private final int[]		culturalAbilityProficiencies	= { 75, 50 };

	@Override
	public String[] culturalAbilityNames()
	{
		return culturalAbilityNames;
	}

	@Override
	public int[] culturalAbilityProficiencies()
	{
		return culturalAbilityProficiencies;
	}

	//  							  an ey ea he ne ar ha to le fo no gi mo wa ta wi
	private static final int[] parts={0 ,2 ,2 ,1 ,1 ,2 ,2 ,1 ,4 ,4 ,1 ,0 ,1 ,1 ,1 ,0 };

	@Override
	public int[] bodyMask()
	{
		return parts;
	}

	protected static Vector<RawMaterial> resources=new Vector<RawMaterial>();

	@Override
	public int availabilityCode()
	{
		return Area.THEME_FANTASY | Area.THEME_SKILLONLYMASK;
	}

	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_SEE_INFRARED);
	}

	@Override
	public void affectCharStats(final MOB affectedMOB, final CharStats affectableStats)
	{
		super.affectCharStats(affectedMOB, affectableStats);
		affectableStats.adjStat(CharStats.STAT_STRENGTH,1);
		affectableStats.adjStat(CharStats.STAT_INTELLIGENCE,1);
		affectableStats.adjStat(CharStats.STAT_CHARISMA,-1);
		affectableStats.adjStat(CharStats.STAT_WISDOM,-1);
	}

	@Override
	public void unaffectCharStats(final MOB affectedMOB, final CharStats affectableStats)
	{
		super.unaffectCharStats(affectedMOB, affectableStats);
		affectableStats.adjStat(CharStats.STAT_STRENGTH,-1);
		affectableStats.adjStat(CharStats.STAT_INTELLIGENCE,-1);
		affectableStats.adjStat(CharStats.STAT_CHARISMA,+1);
		affectableStats.adjStat(CharStats.STAT_WISDOM,+1);
	}

	@Override
	public Weapon[] getNaturalWeapons()
	{
		if(this.naturalWeaponChoices.length==0)
		{
			final Weapon naturalWeapon=CMClass.getWeapon("GenWeapon");
			naturalWeapon.setName(L("a pair of hooves"));
			naturalWeapon.setMaterial(RawMaterial.RESOURCE_BONE);
			naturalWeapon.setUsesRemaining(1000);
			naturalWeapon.setWeaponDamageType(Weapon.TYPE_BASHING);
			this.naturalWeaponChoices = new Weapon[] {naturalWeapon};
		}
		return super.getNaturalWeapons();
	}

	@Override
	public String makeMobName(final char gender, final int age)
	{
		switch(age)
		{
			case Race.AGE_INFANT:
			case Race.AGE_TODDLER:
				return name().toLowerCase()+" foal";
			case Race.AGE_CHILD:
			case Race.AGE_YOUNGADULT:
			{
				switch(gender)
				{
				case 'M':
				case 'm':
					return name().toLowerCase() + " colt";
				case 'F':
				case 'f':
					return name().toLowerCase() + " filly";
				default:
					return "young " + name().toLowerCase();
				}
			}
			case Race.AGE_MATURE:
			case Race.AGE_MIDDLEAGED:
			default:
			{
				switch(gender)
				{
				case 'M':
				case 'm':
					return "male " + name().toLowerCase();
				case 'F':
				case 'f':
					return "female " + name().toLowerCase();
				default:
					return name().toLowerCase();
				}
			}
			case Race.AGE_OLD:
			case Race.AGE_VENERABLE:
			case Race.AGE_ANCIENT:
			{
				switch(gender)
				{
				case 'M':
				case 'm':
					return "old male " + name().toLowerCase();
				case 'F':
				case 'f':
					return "old female " + name().toLowerCase();
				default:
					return "old " + name().toLowerCase();
				}
			}
		}
	}

	@Override
	public List<Item> outfit(final MOB myChar)
	{
		if(outfitChoices==null)
		{
			// Have to, since it requires use of special constructor
			final Armor s1=CMClass.getArmor("GenShirt");
			if(s1 == null)
				return new Vector<Item>();
			outfitChoices=new Vector<Item>();
			s1.setName(L("a woven battleharness"));
			s1.setDisplayText(L("a woven battleharness lies here."));
			s1.setMaterial(RawMaterial.RESOURCE_COTTON);
			s1.setDescription(L("There are lots of little loops and folks for hanging tools about it."));
			((Container)s1).setCapacity(100);
			((Container)s1).setContainTypes(Container.CONTAIN_ONEHANDWEAPONS|Container.CONTAIN_OTHERWEAPONS|Container.CONTAIN_SWORDS);
			s1.text();
			outfitChoices.add(s1);

			final Armor s2=CMClass.getArmor("GenShoes");
			s2.setName(L("a pair of horseshoes"));
			s2.setMaterial(RawMaterial.RESOURCE_IRONWOOD);
			s2.setDisplayText(L("a pair of horseshoes have been left here."));
			final Ability a1=CMClass.getAbility("Prop_WearOverride");
			if(a1!=null)
				a1.setMiscText("-RACE +HORSE +CENTAUR");
			s2.addNonUninvokableEffect(a1);
			s2.text();
			outfitChoices.add(s2);
			final Armor s2a = (Armor)s2.copyOf();
			outfitChoices.add(s2a);

			final Armor s3=CMClass.getArmor("GenArmor");
			s3.setName(L("some saddlebags"));
			s3.setDisplayText(L("some saddlebags lay here."));
			s3.setDescription(L("These simple leather saddlebags are common amongst centaurs."));
			((Container)s3).setCapacity(252);
			s3.setRawProperLocationBitmap(Wearable.WORN_BACK);
			final Ability a2=CMClass.getAbility("Prop_WearOverride");
			if(a2!=null)
				a2.setMiscText("-racecat +equine");
			s3.addNonUninvokableEffect(a1);
			s3.text();
			outfitChoices.add(s3);
			cleanOutfit(outfitChoices);
		}
		return outfitChoices;
	}
	@Override
	public String healthText(final MOB viewer, final MOB mob)
	{
		final double pct=(CMath.div(mob.curState().getHitPoints(),mob.maxState().getHitPoints()));

		if(pct<.10)
			return L("^r@x1^r is hovering on deaths door!^N",mob.name(viewer));
		else
		if(pct<.20)
			return L("^r@x1^r is covered in blood and matted hair.^N",mob.name(viewer));
		else
		if(pct<.30)
			return L("^r@x1^r is bleeding badly from lots of wounds.^N",mob.name(viewer));
		else
		if(pct<.40)
			return L("^y@x1^y has large patches of bloody matted fur.^N",mob.name(viewer));
		else
		if(pct<.50)
			return L("^y@x1^y has some bloody matted fur.^N",mob.name(viewer));
		else
		if(pct<.60)
			return L("^p@x1^p has a lot of cuts and gashes.^N",mob.name(viewer));
		else
		if(pct<.70)
			return L("^p@x1^p has a few cut patches.^N",mob.name(viewer));
		else
		if(pct<.80)
			return L("^g@x1^g has a cut patch of fur.^N",mob.name(viewer));
		else
		if(pct<.90)
			return L("^g@x1^g has some disheveled fur.^N",mob.name(viewer));
		else
		if(pct<.99)
			return L("^g@x1^g has some misplaced hairs.^N",mob.name(viewer));
		else
			return L("^c@x1^c is in perfect health.^N",mob.name(viewer));
	}

	@Override
	public List<RawMaterial> myResources()
	{
		synchronized(resources)
		{
			if(resources.size()==0)
			{
				resources.addElement(makeResource
				(L("@x1 mane",name().toLowerCase()),RawMaterial.RESOURCE_FUR));
				for(int i=0;i<2;i++)
				{
					resources.addElement(makeResource
					(L("a strip of @x1 leather",name().toLowerCase()),RawMaterial.RESOURCE_LEATHER));
				}
				resources.addElement(makeResource
				(L("a pound of @x1 meat",name().toLowerCase()),RawMaterial.RESOURCE_BEEF));
				resources.addElement(makeResource
				(L("some @x1 blood",name().toLowerCase()),RawMaterial.RESOURCE_BLOOD));
				resources.addElement(makeResource
				(L("a pile of @x1 bones",name().toLowerCase()),RawMaterial.RESOURCE_BONE));
			}
		}
		return resources;
	}
}
