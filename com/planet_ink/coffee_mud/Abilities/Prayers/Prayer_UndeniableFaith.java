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
   Copyright 2004-2025 Bo Zimmerman

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
public class Prayer_UndeniableFaith extends Prayer
{
	@Override
	public String ID()
	{
		return "Prayer_UndeniableFaith";
	}

	private final static String localizedName = CMLib.lang().L("Undeniable Faith");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_PRAYER|Ability.DOMAIN_EVANGELISM;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_OK_OTHERS;
	}

	@Override
	protected int canAffectCode()
	{
		return CAN_MOBS;
	}

	@Override
	protected int canTargetCode()
	{
		return CAN_MOBS;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_NEUTRAL|Ability.FLAG_CHARMING|Ability.FLAG_MINDALTERING;
	}

	@Override
	protected int overrideMana()
	{
		return 100;
	}

	protected String godName="";
	private static PairList<MOB,Long> convertStack=new PairVector<MOB,Long>();

	@Override
	public void unInvoke()
	{
		final MOB M=(MOB)affected;
		super.unInvoke();
		if(canBeUninvoked())
			M.tell(L("Your compelled faith is finally subsided."));
	}

	@Override
	public void affectCharStats(final MOB affected, final CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(godName.length()>0)
			affectableStats.setWorshipCharID(godName);
	}

	@Override
	public boolean okMessage(final Environmental host, final CMMsg msg)
	{
		if((affected instanceof MOB)
		&&(msg.amISource((MOB)affected))
		&&(msg.sourceMinor()==CMMsg.TYP_REBUKE)
		&&(msg.target()!=null)
		&&((msg.target()==invoker())||(msg.target().Name().equals(godName))))
		{
			msg.source().tell(L("Your faith is too undeniable."));
			return false;
		}
		return super.okMessage(host,msg);
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null)
			return false;
		if((mob.charStats().getWorshipCharID().length()==0)
		||(mob.charStats().getMyDeity()==null))
		{
			if(!auto)
				mob.tell(L("You must worship a god to use this prayer."));
			return false;
		}
		final Deity D=mob.charStats().getMyDeity();
		if((target.charStats().getWorshipCharID().length()>0)
		&&(target.charStats().getMyDeity()!=null))
		{
			if(!auto)
				mob.tell(L("@x1 worships @x2, and may not be converted with this prayer.",target.name(mob),target.charStats().getWorshipCharID()));
			return false;
		}
		if((CMLib.flags().isAnimalIntelligence(target)||CMLib.flags().isGolem(target)||(D==null)))
		{
			if(!auto)
				mob.tell(L("@x1 can not be converted with this prayer.",target.name(mob)));
			return false;
		}
		if(!auto)
		{
			if(convertStack.containsFirst(target))
			{
				final Long L=convertStack.get(convertStack.indexOfFirst(target)).second;
				if((System.currentTimeMillis()-L.longValue())>CMProps.getMillisPerMudHour()*5)
					convertStack.removeElementFirst(target);
			}
			if(convertStack.containsFirst(target))
			{
				mob.tell(L("@x1 must wait to be undeniably faithful again.",target.name(mob)));
				return false;
			}
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int levelDiff=target.phyStats().level()-(mob.phyStats().level()+(2*getXLEVELLevel(mob)));
		if(levelDiff<0)
			levelDiff=0;
		final boolean success=proficiencyCheck(mob,-(levelDiff*25),auto);
		int type=verbalCastCode(mob,target,auto);
		int mal=CMMsg.MASK_MALICIOUS;
		if(auto)
		{
			type=CMath.unsetb(type,CMMsg.MASK_MALICIOUS);
			mal=0;
		}
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,type,auto?"":L("^S<S-NAME> @x1 for <T-NAMESELF> to BELIEVE!^?",prayWord(mob)));
			final CMMsg msg2=CMClass.getMsg(target,D,this,CMMsg.MSG_SERVE,L("<S-NAME> BELIEVE(S) !!!"));
			final CMMsg msg3=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_VERBAL|mal|CMMsg.TYP_MIND|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))
			&&(mob.location().okMessage(mob,msg3))
			&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg3);
				if((msg.value()<=0)&&(msg3.value()<=0))
				{
					target.location().send(target,msg2);
					if(mob!=target)
						CMLib.leveler().postExperience(mob,"ABILITY:"+ID(),target,null,25, false);
					godName=mob.charStats().getWorshipCharID();
					beneficialAffect(mob,target,asLevel,CMProps.getIntVar(CMProps.Int.TICKSPERMUDMONTH));
					convertStack.add(target,Long.valueOf(System.currentTimeMillis()));
				}
			}
		}
		else
			beneficialWordsFizzle(mob,target,auto?"":L("<S-NAME> @x1 for <T-NAMESELF>, but nothing happens.",prayWord(mob)));

		// return whether it worked
		return success;
	}
}
