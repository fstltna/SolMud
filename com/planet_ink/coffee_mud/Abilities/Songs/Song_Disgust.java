package com.planet_ink.coffee_mud.Abilities.Songs;
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
   Copyright 2002-2025 Bo Zimmerman

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
public class Song_Disgust extends Song
{
	@Override
	public String ID()
	{
		return "Song_Disgust";
	}

	private final static String localizedName = CMLib.lang().L("Disgust");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	protected boolean HAS_QUANTITATIVE_ASPECT()
	{
		return false;
	}

	@Override
	public long flags()
	{
		return super.flags() | Ability.FLAG_MINDALTERING;
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return true;
		if(mob==invoker)
			return true;
		if(invoker==null)
			return true;
		final Room room=invoker.location();
		if((!mob.isInCombat())&&(room!=null))
		{
			final MOB newMOB=room.fetchRandomInhabitant();
			if(newMOB!=mob)
			{
				if(invoker() != null)
				{
					final double pct = super.statBonusPct();
					final int ilevel = (int)Math.round(CMath.mul(invoker().phyStats().level(),pct));
					final int chance = ((mob.phyStats().level()-ilevel-getXLEVELLevel(invoker()))*20);
					if(CMLib.dice().rollPercentage()<chance)
						return true;
				}
				room.show(mob,newMOB,CMMsg.MSG_OK_ACTION,L("<S-NAME> appear(s) disgusted with <T-NAMESELF>."));
				CMLib.combat().postAttack(mob,newMOB,mob.fetchWieldedItem());
			}
		}
		return true;
	}

	@Override
	public int castingQuality(final MOB mob, final Physical target)
	{
		if(mob!=null)
		{
			if(mob.location().numInhabitants()<3)
				return Ability.QUALITY_INDIFFERENT;
		}
		return super.castingQuality(mob,target);
	}

}
