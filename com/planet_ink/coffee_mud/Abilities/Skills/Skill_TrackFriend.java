package com.planet_ink.coffee_mud.Abilities.Skills;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.*;

/*
   Copyright 2018-2025 Bo Zimmerman

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
public class Skill_TrackFriend extends StdAbility
{
	@Override
	public String ID()
	{
		return "Skill_TrackFriend";
	}

	private final static String	localizedName	= CMLib.lang().L("Track Friend");

	@Override
	public String name()
	{
		return localizedName;
	}

	protected String	displayText	= L("(Friend Tracking)");

	@Override
	public String displayText()
	{
		return displayText;
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
	public int abstractQuality()
	{
		return Ability.QUALITY_OK_OTHERS;
	}

	private static final String[]	triggerStrings	= I(new String[] { "FTRACK", "TRACKFRIEND", "FRIENDTRACK" });

	@Override
	public String[] triggerStrings()
	{
		return triggerStrings;
	}

	@Override
	public boolean putInCommandlist()
	{
		return false;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_SKILL | Ability.DOMAIN_RACIALABILITY;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_TRACKING;
	}

	@Override
	public int usageType()
	{
		return USAGE_MOVEMENT;
	}

	protected List<Room>	theTrail		= null;
	protected Reference<MOB>mobr			= null;
	public int				nextDirection	= -2;

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(tickID==Tickable.TICKID_MOB)
		{
			if(nextDirection==-999)
				return true;

			if((theTrail==null)
			||(affected == null)
			||(!(affected instanceof MOB)))
				return false;

			final MOB mob=(MOB)affected;

			if(nextDirection==999)
			{
				mob.tell(L("The trail seems to pause here."));
				nextDirection=-2;
				unInvoke();
			}
			else
			if((nextDirection==-1)
			||((mobr!=null)&&(mobr.get()!=null)&&(!CMLib.flags().canSmell(mob,mobr.get()))))
			{
				mob.tell(L("The trail dries up here."));
				nextDirection=-999;
				unInvoke();
			}
			else
			if(nextDirection>=0)
			{
				mob.tell(L("The trail seems to continue @x1.",CMLib.directions().getDirectionName(nextDirection)));
				if(mob.isMonster())
				{
					final Room nextRoom=mob.location().getRoomInDir(nextDirection);
					if((nextRoom!=null)&&(nextRoom.getArea()==mob.location().getArea()))
					{
						final int dir=nextDirection;
						nextDirection=-2;
						CMLib.tracking().walk(mob,dir,false,false);
					}
					else
						unInvoke();
				}
				else
					nextDirection=-2;
			}

		}
		return true;
	}

	@Override
	public void affectPhyStats(final Physical affectedEnv, final PhyStats affectableStats)
	{
		affectableStats.setSensesMask(affectableStats.sensesMask()|PhyStats.CAN_NOT_TRACK);
		super.affectPhyStats(affectedEnv, affectableStats);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);

		if(!(affected instanceof MOB))
			return;

		final MOB mob=(MOB)affected;
		if((msg.amISource(mob))
		&&(msg.amITarget(mob.location()))
		&&(CMLib.flags().canBeSeenBy(mob.location(),mob))
		&&(msg.targetMinor()==CMMsg.TYP_LOOK))
			nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),true);
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		if(!CMLib.flags().isAliveAwakeMobile(mob,false))
			return false;

		if(!CMLib.flags().canBeSeenBy(mob.location(),mob))
		{
			mob.tell(L("You can't see anything to track!"));
			return false;
		}

		final List<Ability> V=CMLib.flags().flaggedAffects(mob,Ability.FLAG_TRACKING);
		for(final Ability A : V) A.unInvoke();
		if(V.size()>0)
		{
			mob.tell(L("You stop tracking."));
			if((commands.size()==0)||(CMParms.combine(commands,0).equalsIgnoreCase("stop")))
				return true;
		}

		theTrail=null;
		nextDirection=-2;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final String mobName=CMParms.combine(commands,0);
		if(mobName.length()==0)
		{
			mob.tell(L("Track whom?"));
			return false;
		}

		if(mob.location().fetchInhabitant(mobName)!=null)
		{
			mob.tell(L("Try 'look'."));
			return false;
		}

		final Set<MOB> H=mob.getGroupMembers(new HashSet<MOB>());
		for(final Pair<Clan,Integer> p : mob.clans())
		{
			final Clan C=p.first;
			final MOB M=C.findMember(mobName);
			if((M!=null)&&(CMLib.flags().isInTheGame(M, true)))
				H.add(M);
		}
		if(mob.playerStats()!=null)
		{
			for(final String s : mob.playerStats().getFriends())
			{
				if(s.equalsIgnoreCase(mobName))
				{
					final MOB M=CMLib.players().getPlayer(mobName);
					if((M!=null)
					&&(CMLib.flags().isInTheGame(M, true)))
						H.add(M);
				}
			}
		}
		boolean possible=false;
		for(final MOB M : H)
		{
			if(M.name().toUpperCase().startsWith(mobName.toUpperCase())
			&& (M!=mob))
				possible=true;
		}
		if(!possible)
		{
			mob.tell(L("You don't have a friend with that name.  Are you sure they are in your group?"));
			return false;
		}

		final boolean success=proficiencyCheck(mob,0,auto);

		TrackingLibrary.TrackingFlags flags;
		flags=CMLib.tracking().newFlags()
			.plus(TrackingLibrary.TrackingFlag.PASSABLE)
			.plus(TrackingLibrary.TrackingFlag.NOEMPTYGRIDS)
			.plus(TrackingLibrary.TrackingFlag.NOAIR)
			.plus(TrackingLibrary.TrackingFlag.NOWATER);
		final ArrayList<Room> rooms=new ArrayList<Room>();
		final int range=50 + (2*super.getXLEVELLevel(mob))+(10*super.getXMAXRANGELevel(mob));
		final List<Room> trashRooms = new ArrayList<Room>();
		if(CMLib.tracking().getRadiantRoomsToTarget(mob.location(), trashRooms, flags, new TrackingLibrary.RFilter() {
			@Override
			public boolean isFilteredOut(final Room hostR, Room R, final Exit E, final int dir)
			{
				R=CMLib.map().getRoom(R);
				final MOB M=R.fetchInhabitant(mobName);
				if((M!=null)
				&&(H.contains(M))
				&&(CMLib.flags().canAccess(mob, R))
				&&(CMLib.flags().isSeeable(M))
				&& (CMLib.flags().canSmell(mob,M)))
					return false;
				return true;
			}
		}, range))
			rooms.add(trashRooms.get(trashRooms.size()-1));

		if(rooms.size()>0)
			theTrail=CMLib.tracking().findTrailToAnyRoom(mob.location(),rooms,flags,range);

		MOB target=null;
		if((theTrail!=null)&&(theTrail.size()>0))
			target=theTrail.get(0).fetchInhabitant(mobName);

		if((success)&&(theTrail!=null)&&(target!=null))
		{
			theTrail.add(mob.location());

			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_QUIETMOVEMENT,mob.isMonster()?null:L("<S-NAME> begin(s) to track <T-NAMESELF>."),null,mob.isMonster()?null:L("<S-NAME> begin(s) to track <T-NAMESELF>."));
			if((mob.location().okMessage(mob,msg))&&(target.okMessage(target,msg)))
			{
				mob.location().send(mob,msg);
				target.executeMsg(target,msg);
				invoker=mob;
				displayText=L("(Tracking @x1)",target.name(mob));
				final Skill_TrackFriend newOne=(Skill_TrackFriend)this.copyOf();
				if(mob.fetchEffect(newOne.ID())==null)
					mob.addEffect(newOne);
				mob.recoverPhyStats();
				newOne.mobr = new WeakReference<MOB>(target);
				newOne.nextDirection=CMLib.tracking().trackNextDirectionFromHere(theTrail,mob.location(),true);
			}
		}
		else
			return beneficialVisualFizzle(mob,null,L("<S-NAME> attempt(s) to track @x1, but can't find the trail.",mobName));
		// return whether it worked
		return success;
	}
}
