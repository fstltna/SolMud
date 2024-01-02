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
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary.CompiledZMask;
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
public class ObjectGuardian extends StdBehavior
{
	@Override
	public String ID()
	{
		return "ObjectGuardian";
	}

	protected boolean sentinal=false;
	protected boolean dropok=false;
	protected CompiledZMask itemMask = null;
	protected CompiledZMask mobMask = null;

	@Override
	public void setParms(final String parameters)
	{
		super.setParms(parameters);
		final List<String> parts=CMParms.parse(parameters.toUpperCase());
		sentinal=parts.contains("SENTINAL")||parts.contains("SENTINEL");
		dropok=parts.contains("DROPOK")||parts.contains("DROPOK");
		itemMask = CMLib.masking().maskCompile(CMParms.getParmStr(parameters, "ITEMMASK", ""));
		mobMask = CMLib.masking().maskCompile(CMParms.getParmStr(parameters, "MOBMASK", ""));
	}

	@Override
	public String accountForYourself()
	{
		return "valuable object guarding";
	}

	@Override
	public boolean okMessage(final Environmental host, final CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;
		final MOB mob=msg.source();
		final MOB monster=(MOB)host;
		if(sentinal)
		{
			if(!canActAtAll(monster))
				return true;
			if(monster.amFollowing()!=null)
				return true;
			if(monster.curState().getHitPoints()<((int)Math.round(monster.maxState().getHitPoints()/4.0)))
				return true;
		}
		else
		if(!canFreelyBehaveNormal(host))
			return true;

		if((mob!=monster)
		&&(((msg.sourceMinor()==CMMsg.TYP_THROW)&&(monster.location()==CMLib.map().roomLocation(msg.target())))
			||(msg.sourceMinor()==CMMsg.TYP_DROP))
		&&(!dropok)
		&&((itemMask==null)||(!(msg.tool() instanceof Item))||CMLib.masking().maskCheck(itemMask, msg.tool(), false))
		&&((mobMask==null)||CMLib.masking().maskCheck(mobMask, mob, false)))
		{
			final CMMsg msgs=CMClass.getMsg(monster,mob,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> won't let <T-NAME> drop that."));
			if(monster.location().okMessage(monster,msgs))
			{
				monster.location().send(monster,msgs);
				return false;
			}
		}
		else
		if((mob!=monster)
		&&((msg.sourceMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
		&&(msg.target() instanceof Item)
		&&(!mob.isMine(msg.target()))
		&&((itemMask==null)||CMLib.masking().maskCheck(itemMask, msg.target(), false))
		&&((mobMask==null)||CMLib.masking().maskCheck(mobMask, mob, false)))
		{
			final CMMsg msgs=CMClass.getMsg(monster,mob,CMMsg.MSG_NOISYMOVEMENT,L("<S-NAME> won't let <T-NAME> touch that."));
			if(monster.location().okMessage(monster,msgs))
			{
				monster.location().send(monster,msgs);
				return false;
			}
		}
		return true;
	}
}
