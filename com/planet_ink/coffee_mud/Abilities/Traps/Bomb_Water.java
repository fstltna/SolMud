package com.planet_ink.coffee_mud.Abilities.Traps;
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
   Copyright 2003-2025 Bo Zimmerman

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
public class Bomb_Water extends StdBomb
{
	@Override
	public String ID()
	{
		return "Bomb_Water";
	}

	private final static String	localizedName	= CMLib.lang().L("water bomb");

	@Override
	public String name()
	{
		return localizedName;
	}

	public Bomb_Water()
	{
		super();
		trapLevel = 1;
	}

	@Override
	public String requiresToSet()
	{
		return "a water container";
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final List<Item> V=new Vector<Item>();
		V.add(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_FRESHWATER));
		return V;
	}

	@Override
	public boolean canSetTrapOn(final MOB mob, final Physical P)
	{
		if(!super.canSetTrapOn(mob,P))
			return false;
		if((!(P instanceof LiquidHolder))
		||(((LiquidHolder)P).liquidHeld()!=((LiquidHolder)P).liquidRemaining())
		||(((LiquidHolder)P).liquidType()!=RawMaterial.RESOURCE_FRESHWATER))
		{
			if(mob!=null)
				mob.tell(L("You need a full water container to make this out of."));
			return false;
		}
		return true;
	}

	@Override
	protected boolean canExplodeOutOf(final int material)
	{
		return false;
	}

	@Override
	public void spring(final MOB target)
	{
		if(target.location()!=null)
		{
			if((target==invoker())
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(doesSaveVsTraps(target)))
			{
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,
						getAvoidMsg(L("<S-NAME> avoid(s) the water bomb!")));
			}
			else
			{
				final String triggerMsg = getTrigMsg(L("@x1 explodes water all over <T-NAME>!",affected.name()));
				if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,triggerMsg))
				{
					super.spring(target);
					CMLib.utensils().extinguish(invoker(),target,true);
				}
			}
		}
	}
}
