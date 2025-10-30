package com.planet_ink.coffee_mud.Abilities.Spells;
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
   Copyright 2020-2025 Bo Zimmerman

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
public class Spell_PlanarBurst extends Spell
{

	@Override
	public String ID()
	{
		return "Spell_PlanarBurst";
	}

	private final static String localizedName = CMLib.lang().L("Planar Burst");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public int maxRange()
	{
		return adjustedMaxInvokerRange(10);
	}

	@Override
	public int minRange()
	{
		return 0;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	protected int canAffectCode()
	{
		return 0;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;
	}

	@Override
	public double combatCastingTime(final MOB mob, final List<String> cmds)
	{
		return CMath.div(CMProps.getSkillCombatActionCost(ID(), 300.0),100.0);
	}

	@Override
	public long flags()
	{
		return super.flags();
	}

	@Override
	public boolean preInvoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel, final int secondsElapsed, final double actionsRemaining)
	{
		final Room R=mob.location();
		if(R==null)
			return false;
		final Area areaA=R.getArea();
		if((CMLib.flags().getPlaneOfExistence(mob) == null)
		||(!(areaA instanceof SubArea)))
		{
			mob.tell(L("This magic would not work here."));
			return false;
		}
		PlanarAbility planeA=null;
		for(final Enumeration<Ability> a=areaA.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if(A instanceof PlanarAbility)
				planeA=(PlanarAbility)A;
		}
		if(planeA==null)
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if((h==null)||(h.size()==0))
		{
			mob.tell(L("There doesn't appear to be anyone here worth bursting."));
			return false;
		}
		if(secondsElapsed==0)
		{
			String opposedPlane="Opposing";
			if((planeA.getOpposed()!=null)
			&&(planeA.getOpposed().size()>0))
				opposedPlane=planeA.getOpposed().get(CMLib.dice().roll(1, planeA.getOpposed().size(), -1));
			if(!mob.isInCombat())
			{
				for (final Object element : h)
				{
					final MOB target=(MOB)element;
					if(mob.mayIFight(target))
						mob.location().show(mob,null,this,somaticCastCode(mob,null,auto),null);
				}
			}
			return mob.location().show(mob,null,this,somaticCastCode(mob,null,auto),
					auto?"":L("^S<S-NAME> begin(s) to evoke(s) the @x1 plane.^?",opposedPlane));
		}
		return true;
	}
	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		final Room R=mob.location();
		if(R==null)
			return false;
		final Area areaA=R.getArea();
		if((CMLib.flags().getPlaneOfExistence(mob) == null)
		||(!(areaA instanceof SubArea)))
		{
			mob.tell(L("This magic would not work here."));
			return false;
		}
		PlanarAbility planeA=null;
		for(final Enumeration<Ability> a=areaA.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if(A instanceof PlanarAbility)
				planeA=(PlanarAbility)A;
		}
		if(planeA==null)
			return false;

		final Set<MOB> h=properTargets(mob,givenTarget,auto);
		if((h==null)||(h.size()==0))
		{
			mob.tell(L("There doesn't appear to be anyone here worth bursting."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			String opposedPlane="Opposing";
			if((planeA.getOpposed()!=null)
			&&(planeA.getOpposed().size()>0))
				opposedPlane=planeA.getOpposed().get(CMLib.dice().roll(1, planeA.getOpposed().size(), -1));
			if(mob.location().show(mob,null,this,somaticCastCode(mob,null,auto),
					auto?L("A burst of @x1 energy occurs!",opposedPlane)
						:L("^S<S-NAME> evoke(s) a burst of energy from the @x1 plane.^?",opposedPlane)))
			{
				for (final Object element : h)
				{
					final MOB target=(MOB)element;

					final CMMsg msg=CMClass.getMsg(mob,target,this,somaticCastCode(mob,target,auto),null);
					final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_SOMANTIC|CMMsg.TYP_CAST_SPELL|(auto?CMMsg.MASK_ALWAYS:0),null);
					if(mob.location().okMessage(mob,msg)&&mob.location().okMessage(mob,msg2))
					{
						mob.location().send(mob,msg);
						mob.location().send(mob,msg2);
						invoker=mob;

						int damage = (int)Math.round(CMath.mul(target.maxState().getHitPoints(),1.2));

						final int midLevel=(int)Math.round(CMath.div(adjustedLevel(mob,asLevel),2.0));
						if(midLevel<target.phyStats().level())
							damage=(int)Math.round(CMath.mul(damage,0.30));

						if((msg.value()>0)||(msg2.value()>0))
							damage = (int)Math.round(CMath.div(damage,4.0));

						if(damage<=0)
							damage=1;
						damage += super.getX1Level(mob);
						if(target.location()==mob.location())
						{
							CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,Weapon.TYPE_BURSTING,L("The burst <DAMAGES> <T-NAME>!"));
						}
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,null,L("<S-NAME> attempt(s) to evoke a burst of extra-planar energy, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
