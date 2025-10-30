package com.planet_ink.coffee_mud.Abilities.Druid;
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
public class Chant_MagneticEarth extends Chant
{
	@Override
	public String ID()
	{
		return "Chant_MagneticEarth";
	}

	private final static String localizedName = CMLib.lang().L("Magnetic Earth");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_CHANT|Ability.DOMAIN_DEEPMAGIC;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	protected int canAffectCode()
	{
		return Ability.CAN_ROOMS;
	}

	@Override
	protected int canTargetCode()
	{
		return Ability.CAN_ROOMS;
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if((affected instanceof Room))
		{
			final Room R=(Room)affected;
			final List<Item> toGo=new ArrayList<Item>(1);
			boolean didSomething=false;
			final Set<MOB> grp=(invoker()!=null) ? invoker().getGroupMembers(new HashSet<MOB>()) : new TreeSet<MOB>();
			for(int m=0;m<R.numInhabitants();m++)
			{
				final MOB M=R.fetchInhabitant(m);
				if((M!=null)
				&&(invoker()!=M)
				&&((invoker()==null)||(invoker().mayIFight(M)))
				&&(!grp.contains(M)))
				{
					toGo.clear();
					for(int i=0;i<M.numItems();i++)
					{
						final Item I=M.getItem(i);
						if((I!=null)
						&&(((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_METAL)
						   ||((I.material()&RawMaterial.MATERIAL_MASK)==RawMaterial.MATERIAL_MITHRIL))
						&&(I.container()==null)
						&&(I.amWearingAt(Wearable.IN_INVENTORY)
						   ||I.amWearingAt(Wearable.WORN_HELD)
						   ||I.amWearingAt(Wearable.WORN_WIELD)
						   ||I.amWearingAt(Wearable.WORN_EYES)
						   ||I.amWearingAt(Wearable.WORN_MOUTH)))
							toGo.add(I);
					}
					for(int i=0;i<toGo.size();i++)
					{
						final Item I=toGo.get(i);
						if(CMLib.commands().postDrop(M,I,true,true,false))
						{
							didSomething=true;
							R.show(M,I,CMMsg.MSG_OK_VISUAL,L("<T-NAME> is pulled away from <S-NAME> to the magnetic ground!"));
						}
					}
				}
			}
			if(didSomething)
			{
				R.recoverRoomStats();
				R.recoverRoomStats();
			}
		}
		return true;
	}

	protected boolean checked=false;

	@Override
	public void executeMsg(final Environmental host, final CMMsg msg)
	{
		if((!checked)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER)
		&&(affected instanceof Room))
		{
			checked=true;
			if(!CMLib.threads().isTicking(this,-1))
				CMLib.threads().startTickDown(this,Tickable.TICKID_SPELL_AFFECT,1);
		}
		super.executeMsg(host,msg);
	}

	@Override
	public int castingQuality(final MOB mob, final Physical target)
	{
		if(mob!=null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				if((R.domainType()!=Room.DOMAIN_INDOORS_CAVE)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_CITY)
				&&((R.domainType()!=Room.DOMAIN_INDOORS_STONE)||(R.maxRange()<5)||(R.phyStats().weight()<3))
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
				&&(R.domainType()!=Room.DOMAIN_OUTDOORS_ROCKS)
				&&((R.getAtmosphere()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_ROCK))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		final Room target=mob.location();
		if(target==null)
			return false;
		if((!auto)
		&&(!CMLib.flags().isACityRoom(target))
		&&(target.domainType()!=Room.DOMAIN_OUTDOORS_MOUNTAINS)
		&&(target.domainType()!=Room.DOMAIN_OUTDOORS_ROCKS)
		&&((target.getAtmosphere()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_ROCK))
		{
			mob.tell(L("This chant only works in caves, mountains, or rocky areas."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,null,this,verbalCastCode(mob,null,auto),auto?"":L("^S<S-NAME> chant(s) to the ground.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					final Set<MOB> grp=mob.getGroupMembers(new HashSet<MOB>());
					for(int i=0;i<target.numInhabitants();i++)
					{
						final MOB M=target.fetchInhabitant(i);
						if((M!=null)
						&&(mob!=M)
						&&(mob.mayIFight(M))
						&&(!grp.contains(M)))
							mob.location().show(mob,M,CMMsg.MASK_MALICIOUS|CMMsg.TYP_OK_VISUAL,null);
					}
					mob.location().showHappens(CMMsg.MSG_OK_VISUAL,L("The ground gains a powerful magnetic field!"));
					maliciousAffect(mob,target,asLevel,0,-1);
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) the ground, but the magic fades."));
		// return whether it worked
		return success;
	}
}
