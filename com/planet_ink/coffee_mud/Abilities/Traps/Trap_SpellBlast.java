package com.planet_ink.coffee_mud.Abilities.Traps;
import com.planet_ink.coffee_mud.Abilities.StdAbility;
import com.planet_ink.coffee_mud.interfaces.*;
import com.planet_ink.coffee_mud.common.*;
import com.planet_ink.coffee_mud.utils.*;
import java.util.*;

public class Trap_SpellBlast extends StdTrap
{
	public String ID() { return "Trap_SpellBlast"; }
	public String name(){ return "spell blast";}
	protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	protected int canTargetCode(){return 0;}
	protected int trapLevel(){return 23;}
	public String requiresToSet(){return "a spell scroll";}
	public Environmental newInstance(){	return new Trap_SpellBlast();}

	private Item getPoison(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			Item I=mob.location().fetchItem(i);
			if((I!=null)
			&&(I instanceof Scroll)
			&&(((Scroll)I).getSpells()!=null)
			&&(((Scroll)I).getSpells().size()>0)
			&&(I.usesRemaining()>0))
				return I;
		}
		return null;
	}
	
	public Trap setTrap(MOB mob, Environmental E, int classLevel, int qualifyingClassLevel)
	{
		if(E==null) return null;
		Item I=getPoison(mob);
		if((I!=null)&&(I instanceof Scroll)){
			Vector V=((Scroll)I).getSpells();
			if(V.size()>0)
				setMiscText(((Ability)V.firstElement()).ID());
			I.setUsesRemaining(I.usesRemaining()-1);
		}
		return super.setTrap(mob,E,classLevel,qualifyingClassLevel);
	}
	
	public boolean canSetTrapOn(MOB mob, Environmental E)
	{
		if(!super.canSetTrapOn(mob,E)) return false;
		Item I=getPoison(mob);
		if((I==null)
		&&(mob!=null))
		{
			mob.tell("You'll need to set down a scroll with a spell first.");
			return false;
		}
		return true;
	}
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((!invoker().mayIFight(target))||(Dice.rollPercentage()<=target.charStats().getSave(CharStats.SAVE_TRAPS)))
				target.location().show(target,null,null,Affect.MASK_GENERAL|Affect.MSG_NOISE,"<S-NAME> avoid(s) setting off a trap!");
			else
			if(target.location().show(target,target,this,Affect.MASK_GENERAL|Affect.MSG_NOISE,"<S-NAME> set(s) off a trap!"))
			{
				super.spring(target);
				Ability A=CMClass.getAbility(text());
				if(A==null) A=CMClass.getAbility("Spell_Fireball");
				if(A!=null) A.invoke(invoker(),target,true);
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
