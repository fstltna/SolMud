package com.planet_ink.coffee_mud.Abilities.Traps;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.interfaces.ItemPossessor.Expire;
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
   Copyright 2003-2024 Bo Zimmerman

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
public class StdTrap extends StdAbility implements Trap
{
	@Override
	public String ID()
	{
		return "StdTrap";
	}

	private final static String	localizedName	= CMLib.lang().L("standard trap");

	@Override
	public String name()
	{
		return localizedName;
	}

	public static final String[]	TRIGGER	= { "SPRING" };

	@Override
	public String[] triggerStrings()
	{
		return TRIGGER;
	}

	protected boolean	sprung		= false;
	protected int		reset		= 60;	// 5 minute reset is standard
	protected int		ableCode	= 0;
	protected boolean	disabled	= false;
	protected int		trapLevel 	= -1;

	public StdTrap()
	{
		super();
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	public int enchantQuality()
	{
		return Ability.QUALITY_INDIFFERENT;
	}

	@Override
	protected int canAffectCode()
	{
		return 0;
	}

	@Override
	protected int canTargetCode()
	{
		return 0;
	}

	protected int trapLevel()
	{
		return trapLevel;
	}

	@Override
	public void setAbilityCode(final int code)
	{
		ableCode = code;
	}

	@Override
	public int abilityCode()
	{
		return ableCode;
	}

	@Override
	public boolean isABomb()
	{
		return false;
	}

	@Override
	public String requiresToSet()
	{
		return "";
	}

	protected List<String> newMessaging=new ArrayList<String>(0);
	protected String invokerName=null;

	protected PairVector<MOB,Integer> safeDirs=null;

	protected int baseRejuvTime(int level)
	{
		if(level>=30)
			level=29;
		int ticks=(int)Math.round((30.0-(CMath.mul(level,.75)))*30.0);
		if(ticks<1)
			ticks=1;
		return ticks;
	}

	protected int baseDestructTime(final int level)
	{
		return level*30;
	}

	protected boolean getTravelThroughFlag()
	{
		return false;
	}

	@Override
	public boolean disabled()
	{
		return (sprung&&disabled)
			   ||(affected==null)
			   ||(affected.fetchEffect(ID())==null);
	}

	protected boolean doesSaveVsTraps(final MOB target)
	{
		int save=target.charStats().getSave(CharStats.STAT_SAVE_TRAPS);
		if(invoker()!=null)
		{
			save += target.phyStats().level();
			save -= invoker().phyStats().level();
		}
		return (CMLib.dice().rollPercentage()<=save);
	}

	protected boolean isLocalExempt(final MOB target)
	{
		if(target==null)
			return false;
		final Room R=target.location();
		if((!canBeUninvoked())
		&&(!isABomb())
		&&(R!=null))
		{
			if((CMLib.law().getLandTitle(R)!=null)
			&&(CMLib.law().doesHavePriviledgesHere(target,R)))
				return true;

			if((target.isMonster())
			&&(target.getStartRoom()!=null)
			&&(target.getStartRoom().getArea()==R.getArea()))
				return true;
		}
		return false;
	}

	protected boolean canInvokeTrapOn(final MOB invoker, final MOB target)
	{
		if((invoker==null)
		||(invoker.mayIFight(target)
			&&(!invoker.getGroupMembers(new HashSet<MOB>()).contains(target))))
		{
			if(!isLocalExempt(target))
				return true;
		}
		return false;
	}

	@Override
	public void disable()
	{
		disabled=true;
		sprung=true;
		if(!canBeUninvoked())
		{
			tickDown=getReset();
			CMLib.threads().startTickDown(this,Tickable.TICKID_TRAP_RESET,1);
		}
		else
			unInvoke();
	}

	@Override
	public void setReset(final int resetTicks)
	{
		if((!sprung)&&(!disabled)&&(resetTicks>=0))
			tickDown=resetTicks;
		reset = resetTicks;
	}

	@Override
	public int getReset()
	{
		return reset;
	}

	@Override
	public MOB invoker()
	{
		if(invoker==null)
		{
			if((invokerName!=null)&&(!invokerName.equalsIgnoreCase("null")))
				invoker=CMLib.players().getLoadPlayer(invokerName);
			if(invoker==null)
			{
				invoker=CMClass.getMOB("StdMOB");
				invoker.setLocation(CMClass.getLocale("StdRoom"));
				invoker.basePhyStats().setLevel(affected.phyStats().level());
				invoker.phyStats().setLevel(affected.phyStats().level());
			}
		}
		else
			invokerName=invoker.Name();
		return super.invoker();
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_TRAP;
	}

	@Override
	public void setMiscText(String text)
	{
		text=text.trim();
		if(text.startsWith("`"))
		{
			final int x=text.indexOf("` ",1);
			if(x>=0)
			{
				invokerName=text.substring(1,x);
				text=text.substring(x+2).trim();
			}
		}
		while(text.startsWith("\""))
		{
			final int x=text.indexOf("\"",1);
			if(x>=0)
			{
				newMessaging.add(text.substring(1,x));
				text=text.substring(x+1).trim();
			}
			else
				break;
		}
		if(text.startsWith(":"))
		{
			final int x=text.indexOf(':');
			final int y=text.indexOf(':',x+1);
			if((x>=0)&&(y>x))
			{
				final String ac = text.substring(x+1,y).trim();
				final int z=ac.indexOf('/');
				if((z>0)
				&&(CMath.isInteger(ac.substring(0,z).trim()))
				&&(CMath.isInteger(ac.substring(z+1).trim())))
				{
					trapLevel=CMath.s_int(ac.substring(0,z).trim());
					setAbilityCode(CMath.s_int(ac.substring(z+1).trim()));
				}
				else
				if(CMath.isInteger(ac))
					setAbilityCode(CMath.s_int(ac));
				text=text.substring(y+1).trim();
			}
		}
		if(text.trim().length()>0)
			super.setMiscText(text.trim());
	}

	@Override
	public String text()
	{
		final StringBuilder txt=new StringBuilder("");
		if((invokerName != null)
		&&(!invokerName.equalsIgnoreCase("null")))
			txt.append("`"+invokerName+"` ");
		for(final String msg : newMessaging)
			txt.append("\""+msg+"\" ");
		txt.append(":"+trapLevel()+"/"+abilityCode()+":");
		txt.append(super.text());
		return txt.toString();
	}

	protected String getTrigMsg(final String defMsg)
	{
		if(newMessaging.size()>0)
		{
			final String str = newMessaging.get(0);
			if(str.length()>0)
				return str;
		}
		return defMsg;
	}

	protected String getDamMsg(final String defMsg)
	{
		if(newMessaging.size()>1)
		{
			final String str = newMessaging.get(1);
			if(str.length()>0)
				return str;
		}
		return defMsg;
	}

	protected String getAvoidMsg(final String defMsg)
	{
		if(newMessaging.size()>2)
		{
			final String str = newMessaging.get(2);
			if(str.length()>0)
				return str;
		}
		return defMsg;
	}

	protected synchronized PairVector<MOB,Integer> getSafeDirs()
	{
		if(safeDirs == null)
			safeDirs=new PairVector<MOB,Integer>();
		return safeDirs;
	}

	@Override
	public CMObject copyOf()
	{
		final StdTrap obj=(StdTrap)super.copyOf();
		obj.safeDirs=null;
		return obj;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((!disabled())&&(affected instanceof Item))
		{
			if((msg.tool()==affected)
			&&(msg.targetMinor()==CMMsg.TYP_GIVE)
			&&(msg.targetMessage()!=null)
			&&(msg.target() instanceof MOB)
			&&(!msg.source().getGroupMembers(new HashSet<MOB>()).contains(msg.target())))
			{
				msg.source().tell((MOB)msg.target(),msg.tool(),null,L("<S-NAME> can't accept <T-NAME>."));
				return false;
			}
		}
		if((!sprung)
		&& CMath.bset(canAffectCode(),Ability.CAN_ROOMS)
		&& getTravelThroughFlag()
		&& msg.amITarget(affected)
		&& (affected instanceof Room)
		&& (msg.tool() instanceof Exit))
		{
			final Room room=(Room)affected;
			if ((msg.targetMinor()==CMMsg.TYP_LEAVE)||(msg.targetMinor()==CMMsg.TYP_FLEE))
			{
				final int movingInDir=CMLib.map().getExitDir(room, (Exit)msg.tool());
				if((movingInDir!=Directions.DOWN)&&(movingInDir!=Directions.UP))
				{
					final PairVector<MOB,Integer> safeDirs=getSafeDirs();
					synchronized(safeDirs)
					{
						for(final Iterator<Pair<MOB,Integer>> i=safeDirs.iterator();i.hasNext();)
						{
							final Pair<MOB,Integer> p=i.next();
							if(p.first == msg.source())
							{
								i.remove();
								if(movingInDir==p.second.intValue())
									return true;
								spring(msg.source());
								return !sprung();
							}
						}
					}
				}
			}
		}
		return super.okMessage(myHost,msg);
	}

	@Override
	public void activateBomb()
	{
		if(isABomb())
		{
			tickDown=getReset();
			sprung=false;
			disabled=false;
			CMLib.threads().startTickDown(this,Tickable.TICKID_TRAP_RESET,1);
		}
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(sprung)
		{
			super.executeMsg(myHost,msg);
			return;
		}
		if(CMath.bset(canAffectCode(), Ability.CAN_ROOMS)
		&& msg.amITarget(affected)
		&&(msg.targetMinor()==CMMsg.TYP_ENTER))
		{
			if(getTravelThroughFlag())
			{
				if ((affected instanceof Room)
				&& (msg.tool() instanceof Exit))
				{
					final Room room=(Room)affected;
					final int movingInDir=CMLib.map().getExitDir(room, (Exit)msg.tool());
					if((movingInDir!=Directions.DOWN)&&(movingInDir!=Directions.UP))
					{
						final PairVector<MOB,Integer> safeDirs=getSafeDirs();
						synchronized(safeDirs)
						{
							final int dex=safeDirs.indexOfFirst(msg.source());
							if(dex>=0)
								safeDirs.remove(dex);
							while(safeDirs.size()>room.numInhabitants()+1)
								safeDirs.remove(0);
							safeDirs.add(new Pair<MOB,Integer>(msg.source(),Integer.valueOf(movingInDir)));
						}
					}
				}
			}
			else
			if(!msg.source().isMine(affected))
				spring(msg.source());
		}
		else
		if(CMath.bset(canAffectCode(),Ability.CAN_EXITS)
		&&(msg.amITarget(affected))
		&&((affected instanceof Exit)||(affected instanceof Container)))
		{
			if((affected instanceof Exit)
			&&(((Exit)affected).hasADoor())
			&&(((Exit)affected).hasALock())
			&&(((Exit)affected).isLocked()))
			{
				if(msg.targetMinor()==CMMsg.TYP_UNLOCK)
					spring(msg.source());
			}
			else
			if((affected instanceof Container)
			&&(((Container)affected).hasADoor())
			&&(((Container)affected).hasALock())
			&&(((Container)affected).isLocked()))
			{
				if(msg.targetMinor()==CMMsg.TYP_UNLOCK)
					spring(msg.source());
			}
			else
			if(msg.targetMinor()==CMMsg.TYP_OPEN)
				spring(msg.source());
		}
		else
		if((CMath.bset(canAffectCode(),Ability.CAN_ITEMS))
		&&(msg.amITarget(affected))
		&&(affected instanceof Item))
		{
			if(isABomb())
			{
				if((msg.targetMinor()==CMMsg.TYP_HOLD)
				&&(msg.source().isMine(affected)))
				{
					msg.source().tell(msg.source(),affected,null,L("You activate <T-NAME>."));
					activateBomb();
				}
			}
			else
			{
				if(((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
				&&(!msg.source().isMine(affected)))
					spring(msg.source());
			}
		}
		super.executeMsg(myHost,msg);
	}

	@Override
	public boolean maySetTrap(final MOB mob, final int asLevel)
	{
		if(trapLevel()<0)
			return false;
		if(asLevel<0)
			return true;
		if(asLevel>=trapLevel())
			return true;
		return false;
	}

	@Override
	public boolean canReSetTrap(final MOB mob)
	{
		final Physical P=affected;
		if(P==null)
		{
			if(mob!=null)
				mob.tell(L("This trap is not presently set."));
			return false;
		}
		if(mob!=null)
		{
			if((!maySetTrap(mob,mob.phyStats().level()))
			&&(!mob.charStats().getCurrentClass().leveless())
			&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
			{
				mob.tell(L("You are not high enough level (@x1) to set that trap.",""+trapLevel()));
				return false;
			}
		}
		final Trap T=(Trap)P.fetchEffect(ID());
		if(T!=this)
		{
			if(mob!=null)
				mob.tell(L("This trap is not presently set on @x1.",P.name()));
			return false;
		}

		if(T.invoker() != mob)
		{
			if(mob!=null)
				mob.tell(L("The trap was not set by you."));
			return false;
		}

		return true;
	}

	@Override
	public boolean canSetTrapOn(final MOB mob, final Physical P)
	{
		if(mob!=null)
		{
			if((!maySetTrap(mob,mob.phyStats().level()))
			&&(!mob.charStats().getCurrentClass().leveless())
			&&(!CMSecurity.isDisabled(CMSecurity.DisFlag.LEVELS)))
			{
				mob.tell(L("You are not high enough level (@x1) to set that trap.",""+trapLevel()));
				return false;
			}
		}
		if(P.fetchEffect(ID())!=null)
		{
			if(mob!=null)
				mob.tell(L("This trap is already set on @x1.",P.name()));
			return false;
		}
		if(!canAffect(P))
		{
			if(mob!=null)
				mob.tell(L("You can't set '@x1' on @x2.",name(),P.name()));
			return false;
		}
		if((canAffectCode()&Ability.CAN_EXITS)==Ability.CAN_EXITS)
		{
			if((P instanceof Item)&&(!(P instanceof Container)))
			{
				if(mob!=null)
					mob.tell(L("@x1 has no lid, so '@x2' cannot be set on it.",P.name(),name()));
				return false;
			}
			if(((P instanceof Exit)&&(!(((Exit)P).hasADoor()))))
			{
				if(mob!=null)
					mob.tell(L("@x1 has no door, so '@x2' cannot be set on it.",P.name(),name()));
				return false;
			}
			if(((P instanceof Container)&&(!(((Container)P).hasADoor()))))
			{
				if(mob!=null)
					mob.tell(L("@x1 has no lid, so '@x2' cannot be set on it.",P.name(),name()));
				return false;
			}
		}
		return true;
	}

	@Override
	public List<Item> getTrapComponents()
	{
		return new Vector<Item>(1);
	}

	@Override
	public Trap setTrap(final MOB mob, final Physical P, final int trapBonus, final int qualifyingClassLevel, final boolean perm)
	{
		if(P==null)
			return null;
		final int rejuv=baseRejuvTime(qualifyingClassLevel+trapBonus);
		final Trap T=(Trap)copyOf();
		T.setReset(rejuv);
		T.setInvoker(mob);
		T.setSavable(false);
		T.setAbilityCode(trapBonus);
		P.addEffect(T);
		if(perm)
		{
			T.setSavable(true);
			T.makeNonUninvokable();
		}
		else
		if(!isABomb())
			CMLib.threads().startTickDown(T,Tickable.TICKID_TRAP_DESTRUCTION,baseDestructTime(qualifyingClassLevel+trapBonus));
		return T;
	}

	@Override
	public void setInvoker(final MOB mob)
	{
		if(mob!=null)
			invokerName=mob.Name();
		super.setInvoker(mob);
	}

	protected void tellOwner(final ItemPossessor P, final String msg)
	{
		if(P instanceof Room)
			((Room)P).showHappens(CMMsg.MSG_OK_ACTION, msg);
		else
		if(P instanceof MOB)
			((MOB)P).tell(msg);
	}

	protected boolean canExplodeOutOf(final int material)
	{
		switch(material&RawMaterial.MATERIAL_MASK)
		{
		case RawMaterial.MATERIAL_METAL:
		case RawMaterial.MATERIAL_MITHRIL:
		case RawMaterial.MATERIAL_GLASS:
		case RawMaterial.MATERIAL_ROCK:
		case RawMaterial.MATERIAL_LIQUID:
		case RawMaterial.MATERIAL_ENERGY:
		case RawMaterial.MATERIAL_SYNTHETIC:
		case RawMaterial.MATERIAL_GAS:
			return false;
		}
		return true;
	}

	protected boolean doesInnerExplosionDestroy(final int material)
	{
		return false;
	}

	protected void explodeContainer(final Container C)
	{
		if((!canExplodeOutOf(C.material()))
		||(!CMLib.utensils().canBeRuined(C)))
		{
			tellOwner(C.owner(), L("Something happened inside @x1.",C.name()));
			return;
		}
		if(doesInnerExplosionDestroy(C.material()))
		{
			tellOwner(C.owner(), L("@x1 is destroyed.",C.name()));
			final List<Item> contents=C.getDeepContents();
			for(final Item I : contents)
			{
				final Item I2=CMLib.utensils().ruinItem(I);
				if(I2 != null)
				{
					I.owner().addItem(I2, Expire.Monster_Body);
					I.destroy();
				}
				else
					I.setContainer(null);
			}
			C.destroy();
		}
		if(C.owner() instanceof MOB)
			spring((MOB)C.owner());
		else
		if(C.owner() instanceof Room)
			springOnRoomMobs((Room)C.owner());
	}

	protected void springOnRoomMobs(final Room R)
	{
		if(R!=null)
		{
			for(int i=R.numInhabitants()-1;i>=0;i--)
			{
				final MOB M=R.fetchInhabitant(i);
				if(M!=null)
					spring(M);
			}
		}
	}

	protected void explodeBomb(final Physical P)
	{
		final Item I=(Item)affected;
		if(I.container() != null)
			explodeContainer(I.container());
		else
		if(I.owner() instanceof MOB)
			spring((MOB)I.owner());
		else
		if(I.owner() instanceof Room)
			springOnRoomMobs((Room)I.owner());
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if((unInvoked)&&(canBeUninvoked()))
			return false;

		if(tickID==Tickable.TICKID_TRAP_DESTRUCTION)
		{
			if(canBeUninvoked())
				disable();
			return false;
		}
		else
		if((tickID==Tickable.TICKID_TRAP_RESET)&&(getReset()>0))
		{
			if((--tickDown)<=0)
			{
				if( isABomb()
				&&(affected instanceof Item))
				{
					final Item I=(Item)affected;
					explodeBomb(I.owner());
					disable();
					unInvoke();
					I.destroy();
					return false;
				}
				sprung=false;
				disabled=false;
				return false;
			}
		}
		return true;
	}

	@Override
	public void resetTrap(final MOB mob)
	{
		if(sprung())
		{
			sprung=false;
			disabled=false;
			if(!isABomb())
				CMLib.threads().deleteTick(this, Tickable.TICKID_TRAP_RESET);
		}
	}

	@Override
	public boolean sprung()
	{
		return sprung && (!disabled());
	}

	@Override
	public void spring(final MOB target)
	{
		sprung=true;
		disabled=false;
		tickDown=getReset();
		if(!isABomb())
			CMLib.threads().startTickDown(this,Tickable.TICKID_TRAP_RESET,1);
	}

	protected RawMaterial findFirstResource(final Room room, final int resource)
	{
		return CMLib.materials().findFirstResource(room, resource);
	}

	protected RawMaterial findMostOfMaterial(final Room room, final int material)
	{
		return CMLib.materials().findMostOfMaterial(room, material);
	}

	protected void destroyResources(final Room room, final int resource, final String subType, final int number)
	{
		CMLib.materials().destroyResourcesValue(room,number,resource,subType.hashCode(),0,0);
	}

	protected int findNumberOfResource(final Room room, final RawMaterial resource)
	{
		return CMLib.materials().findNumberOfResourceLike(room, resource);
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		final MOB target=super.getTarget(mob, commands, givenTarget);
		if(target == null)
			return false;
		if(!super.proficiencyCheck(mob, 0, auto))
			return true;
		if(!super.invoke(mob, commands, target, auto, asLevel))
			return false;
		final StdTrap T=(StdTrap)copyOf();
		T.setInvoker(mob);
		T.setAffectedOne(mob);
		T.spring(target);
		return true;
	}
}
