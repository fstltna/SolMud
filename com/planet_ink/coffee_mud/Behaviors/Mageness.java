package com.planet_ink.coffee_mud.Behaviors;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.AbilityMapper.AbilityMapping;
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
public class Mageness extends CombatAbilities
{
	@Override
	public String ID()
	{
		return "Mageness";
	}

	@Override
	public String accountForYourself()
	{
		return "magliness";
	}

	protected void getSomeMoreMageAbilities(final MOB mob)
	{
		final List<AbilityMapping> all=new ArrayList<AbilityMapping>();
		for(final CharClass C : mob.baseCharStats().getCharClasses())
			all.addAll(CMLib.ableMapper().getUpToLevelListings(C.ID(), mob.baseCharStats().getClassLevel(C),false,false));
		{
			final Set<String> dup = new TreeSet<String>();
			for(final Iterator<AbilityMapping> i=all.iterator();i.hasNext();)
			{
				final AbilityMapping map = i.next();
				if(dup.contains(map.abilityID()))
					i.remove();
				else
				{
					dup.add(map.abilityID());
					if(mob.fetchAbility(map.abilityID())!=null)
						i.remove();
				}
			}
		}
		if(all.size()==0)
			return;
		for(int a=0;a<((mob.basePhyStats().level())+5);a++)
		{
			Ability addThis=null;
			AbilityMapping map=null;
			int tries=0;
			while((addThis==null)&&((++tries)<10))
			{
				map = all.get(CMLib.dice().roll(1, all.size(), -1));
				addThis=CMClass.getAbility(map.abilityID());
				if(addThis == null)
					addThis=CMClass.randomAbility();
				if((CMLib.ableMapper().qualifyingLevel(mob,addThis)<0)
				||(!CMLib.ableMapper().qualifiesByLevel(mob,addThis))
				||(((addThis.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)&&(!addThis.appropriateToMyFactions(mob)))
				||(mob.fetchAbility(addThis.ID())!=null)
				||((addThis.abstractQuality()!=Ability.QUALITY_MALICIOUS)
				   &&(addThis.abstractQuality()!=Ability.QUALITY_BENEFICIAL_SELF)
				   &&(addThis.abstractQuality()!=Ability.QUALITY_BENEFICIAL_OTHERS)))
					addThis=null;
			}
			if(addThis!=null)
			{
				addThis=(Ability)addThis.newInstance();
				addThis.setSavable(false);
				addThis.setProficiency(CMLib.ableMapper().getMaxProficiency(addThis.ID())/2);
				mob.addAbility(addThis);
				addThis.autoInvocation(mob, false);
				if(map != null)
					all.remove(map);
			}
		}
	}

	@Override
	public void startBehavior(final PhysicalAgent forMe)
	{
		super.startBehavior(forMe);
		if(!(forMe instanceof MOB))
			return;
		final MOB mob=(MOB)forMe;
		combatMode=COMBAT_RANDOM;
		makeClass(mob,getParmsMinusCombatMode(),"Mage");
		newCharacter(mob);
		getSomeMoreMageAbilities(mob);
		//%%%%%att,armor,damage,hp,mana,move
		if((preCastSet==Integer.MAX_VALUE)||(preCastSet<=0))
		{
			setCombatStats(mob,-10,-50,-10,-15,50,-50, true);
			setCharStats(mob);
		}
	}
}
