package com.planet_ink.coffee_mud.Abilities.Diseases;
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
public class Disease_PlanarPlague extends Disease
{
	@Override
	public String ID()
	{
		return "Disease_PlanarPlague";
	}

	private final static String localizedName = CMLib.lang().L("Planar Plague");

	@Override
	public String name()
	{
		return localizedName;
	}

	private final static String localizedStaticDisplay = CMLib.lang().L("(Planar Plague)");

	@Override
	public String displayText()
	{
		return localizedStaticDisplay;
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
		return Ability.QUALITY_MALICIOUS;
	}

	@Override
	public boolean putInCommandlist()
	{
		return false;
	}

	@Override
	protected int DISEASE_TICKS()
	{
		return 48;
	}

	@Override
	protected int DISEASE_DELAY()
	{
		return 4;
	}

	@Override
	protected String DISEASE_DONE()
	{
		return L("The glowing sores on your body clear up.");
	}

	@Override
	protected String DISEASE_START()
	{
		return L("^G<S-NAME> look(s) seriously ill!^?");
	}

	@Override
	protected String DISEASE_AFFECT()
	{
		return L("<S-NAME> watch(es) <S-HIS-HER> body erupt with a fresh batch of painful glowing eruptions!");
	}

	@Override
	public int spreadBitmap()
	{
		return DiseaseAffect.SPREAD_CONSUMPTION|DiseaseAffect.SPREAD_PROXIMITY|DiseaseAffect.SPREAD_CONTACT|DiseaseAffect.SPREAD_STD;
	}

	@Override
	public int difficultyLevel()
	{
		return 7;
	}

	protected int abilityCode = 0;
	@Override
	public int abilityCode()
	{
		final Physical P=affected;
		if((P != null)
		&&(abilityCode < P.basePhyStats().level()))
			abilityCode=P.basePhyStats().level();
		return abilityCode;
	}

	@Override
	public void setAbilityCode(final int newCode)
	{
		abilityCode = newCode;
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(affected==null)
			return false;
		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((!mob.amDead())&&((--diseaseTick)<=0))
		{
			MOB diseaser=invoker;
			if(diseaser==null)
				diseaser=mob;
			diseaseTick=DISEASE_DELAY();
			mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,DISEASE_AFFECT());
			int dmg=mob.phyStats().level()/6;
			if(dmg<1)
				dmg=1;
			CMLib.combat().postDamage(diseaser,mob,this,dmg,CMMsg.MASK_ALWAYS|CMMsg.TYP_DISEASE,-1,null);
			catchIt(mob);
			return true;
		}
		return true;
	}

	@Override
	public void affectCharStats(final MOB affected, final CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(affected==null)
			return;
		final int ac = this.abilityCode();
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,
				(int)Math.round(affectableStats.getStat(CharStats.STAT_CONSTITUTION)-Math.ceil(ac/10)));
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<3)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,3);
		affectableStats.setStat(CharStats.STAT_DEXTERITY,
				(int)Math.round(affectableStats.getStat(CharStats.STAT_DEXTERITY)-Math.ceil(ac/10)));
		if(affectableStats.getStat(CharStats.STAT_DEXTERITY)<3)
			affectableStats.setStat(CharStats.STAT_DEXTERITY,3);
	}
	
	@Override
	public void affectPhyStats(final Physical affected, final PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(affected==null)
			return;
		final int ac = this.abilityCode();
		affectableStats.setAttackAdjustment(affectableStats.attackAdjustment()-ac);
		affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GLOWING);
	}
	
	@Override
	public void affectCharState(final MOB affected, final CharState affectableMaxState)
	{
		super.affectCharState(affected,affectableMaxState);
		if(affected==null)
			return;
		final int ac = this.abilityCode();
		affectableMaxState.setHitPoints(affectableMaxState.getHitPoints() - (ac*2));
		if(affectableMaxState.getHitPoints()<=0)
			affectableMaxState.setHitPoints(1);
		affectableMaxState.setMovement(affectableMaxState.getMovement() - (ac*2));
		if(affectableMaxState.getHitPoints()<=0)
			affectableMaxState.setHitPoints(1);
	}
}
