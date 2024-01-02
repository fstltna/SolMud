package com.planet_ink.coffee_mud.Abilities.Prayers;
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
   Copyright 2014-2024 Bo Zimmerman

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
public class Prayer_LightHammer extends Prayer
{
	@Override
	public String ID()
	{
		return "Prayer_LightHammer";
	}

	private final static String localizedName = CMLib.lang().L("Hammer of Light");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_HOLY;
	}

	@Override
	public int castingQuality(final MOB mob, final Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!CMLib.flags().isEvil((target)))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	protected void addToSet(final Set<Item> set, final Item I)
	{
		if(I instanceof Weapon)
			set.add(I);
	}

	protected String getWeaponName(final MOB mob)
	{
		final Deity D = mob.charStats().getMyDeity();
		if(D != null)
		{
			final Set<Item> set=new HashSet<Item>();
			addToSet(set, D.fetchWieldedItem());
			addToSet(set, D.fetchHeldItem());
			for(final Enumeration<Item> i=D.items();i.hasMoreElements();)
				addToSet(set,i.nextElement());
			if(set.size()>0)
			{
				final List<Item> V = new XVector<Item>(set);
				return V.get(CMLib.dice().roll(1, V.size(), -1)).name(mob);
			}
		}
		return L("a hammer");
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null)
			return false;
		final boolean undead=CMLib.flags().isUndead(target);

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if((success)
		&&(CMLib.flags().isEvil(target)))
		{
			final String wname = getWeaponName(mob);
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,
					L(auto?L("<T-NAME> is filled with holy light!"):
						L("^S<S-NAME> @x1 for @x2 of light to strike <T-NAMESELF>!^?@x3",
								prayWord(mob),wname,CMLib.protocol().msp("spelldam1.wav",40))));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),null);
			final Room R=target.location();
			if((R.okMessage(mob,msg))&&((R.okMessage(mob,msg2))))
			{
				R.send(mob,msg);
				R.send(mob,msg2);
				int harming=CMLib.dice().roll(2,adjustedLevel(mob,asLevel)+5,adjustedLevel(mob,asLevel));
				if((msg.value()>0)||(msg2.value()>0))
					harming=(int)Math.round(CMath.div(harming,2.0));
				if(undead)
					harming=harming*2;
				if(CMLib.flags().isEvil(target))
				{
					final String Wname = CMStrings.capitalizeFirstLetter(wname);
					CMLib.combat().postDamage(mob,target,this,harming,CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BURSTING,
							L("^S@x1 of holy light <DAMAGE> <T-NAME>!^?",Wname));
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> @x1, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
