package com.planet_ink.coffee_mud.Abilities.Prayers;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.ExpertiseLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

/*
   Copyright 2020-2024 Bo Zimmerman

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
public class Prayer_ProtectSacredItem extends Prayer
{
	@Override
	public String ID()
	{
		return "Prayer_ProtectSacredItem";
	}

	private final static String localizedName = CMLib.lang().L("Protect Sacred Item");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	protected int canTargetCode()
	{
		return CAN_ITEMS;
	}

	@Override
	protected int canAffectCode()
	{
		return CAN_ITEMS;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_PRAYER|Ability.DOMAIN_HOLYPROTECTION;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_NOORDERING|Ability.FLAG_NEUTRAL;
	}

	@Override
	protected int overrideMana()
	{
		return Ability.COST_ALL;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_INDIFFERENT;
	}

	@Override
	public boolean appropriateToMyFactions(final MOB mob)
	{
		if(mob == null)
			return true;
		return true;
	}

	protected int maintainCondition=100;

	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		super.affectPhyStats(affected, affectableStats);
		if(!(affected instanceof Item))
			return;
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.SENSE_ITEMNORUIN);
		if(((Item)affected).subjectToWearAndTear())
		{
			if(((Item)affected).usesRemaining()>maintainCondition)
				maintainCondition=((Item)affected).usesRemaining();
			((Item)affected).setUsesRemaining(maintainCondition);
		}
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(!(affected instanceof Item))
			return true;
		if(((Item)affected).subjectToWearAndTear())
		{
			if(((Item)affected).usesRemaining()>maintainCondition)
				maintainCondition=((Item)affected).usesRemaining();
			((Item)affected).setUsesRemaining(maintainCondition);
		}
		return true;
	}

	@Override
	public boolean okMessage(final Environmental host, final CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;
		if((msg.target()==affected)
		&&(CMath.bset(msg.targetMajor(),CMMsg.MASK_MALICIOUS)
		   ||((msg.tool() instanceof Ability)&&(((Ability)msg.tool()).abstractQuality()==Ability.QUALITY_MALICIOUS)))
		&&(msg.sourceMinor()!=CMMsg.TYP_TEACH))
		{
			msg.source().tell(L("@x1 is unbreakable!",affected.name()));
			return false;
		}
		return true;
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Protect what?"));
			return false;
		}
		final Physical target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,commands.get(commands.size()-1),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",(commands.get(commands.size()-1))));
			return false;
		}

		commands.remove(commands.size()-1);

		if(!Prayer.checkInfusionMismatch(mob, target))
		{
			mob.tell(L("You can not protect that repulsive relic."));
			return false;
		}
		if(!Prayer.checkRequiredInfusion(mob, target))
		{
			mob.tell(L("You can only protect things empowered or imbued with your deities favor."));
			return false;
		}

		int experienceToLose=500;
		if((mob.getExperience()-experienceToLose)<0)
		{
			mob.tell(L("You don't have enough experience to pray for that."));
			return false;
		}
		// lose all the mana!
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
		experienceToLose=-CMLib.leveler().postExperience(mob,"ABILITY:"+ID(),null,null,-experienceToLose, false);
		mob.tell(L("You lose @x1 experience points for the effort.",""+experienceToLose));

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),
					L(auto?"<T-NAME> appear(s) protected!":"^S<S-NAME> protect(s) <T-NAMESELF>"+inTheNameOf(mob)+".^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				Prayer.infusePhysicalByAlignment(mob,target);
				final Prayer_ProtectSacredItem A=(Prayer_ProtectSacredItem)this.copyOf();
				target.addNonUninvokableEffect(A);
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> @x1 to protect <T-NAME>, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
