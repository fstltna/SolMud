package com.planet_ink.coffee_mud.Abilities.Properties;
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
   Copyright 2003-2025 Bo Zimmerman

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
public class Prop_AbilityImmunity extends Property implements TriggeredAffect
{
	@Override
	public String ID()
	{
		return "Prop_AbilityImmunity";
	}

	@Override
	public String name()
	{
		return "Ability Immunity";
	}

	@Override
	protected int canAffectCode()
	{
		return Ability.CAN_MOBS | Ability.CAN_ITEMS | Ability.CAN_ROOMS | Ability.CAN_EXITS | Ability.CAN_AREAS;
	}

	@Override
	public String accountForYourself()
	{
		if(immStr == null)
		{
			final List<String> names=new ArrayList<String>(diseases.size());
			for(final String diseaseID : diseases)
			{
				Ability A=CMClass.getAbility(diseaseID);
				if(A==null)
					A=CMClass.findAbility(diseaseID);
				if(A!=null)
					names.add(A.name());
			}
			immStr=L("Immunity to @x1",CMLib.english().toEnglishStringList(names));
		}
		return immStr;
	}

	protected List<String>		diseases	= new Vector<String>();
	protected List<String>		messages	= new Vector<String>();
	protected boolean			owner		= false;
	protected boolean			wearer		= false;
	protected String			immStr		= null;

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ALWAYS;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_RESISTER;
	}

	@Override
	public void setMiscText(final String newText)
	{
		immStr = null;
		messages=new Vector<String>();
		diseases=CMParms.parseSemicolons(newText,true);
		owner = false;
		wearer = false;
		for(int d=0;d<diseases.size();d++)
		{
			final String s=diseases.get(d);
			if(s.equalsIgnoreCase("owner"))
				owner=true;
			else
			if(s.equalsIgnoreCase("wearer"))
				wearer=true;
			else
			{
				final int x=s.indexOf('=');
				if(x<0)
					messages.add("");
				else
				{
					diseases.set(d,s.substring(0,x).trim().toUpperCase());
					messages.add(s.substring(x+1).trim());
				}
			}
		}
		super.setMiscText(newText);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if ((msg.target() != null)
		&& (msg.tool() instanceof Ability )
		&&(msg.sourceMinor()!=CMMsg.TYP_TEACH)
		&& ((msg.amITarget(affected))
			||(affected instanceof Area)
			||(owner && (affected instanceof Item) && (msg.target()==((Item)affected).owner()))
			||(owner && (affected instanceof Item) && (msg.target()==((Item)affected).owner()) && (!((Item)affected).amWearingAt(Wearable.IN_INVENTORY)))))
		{
			final Ability A = (Ability)msg.tool();
			for(int i = 0; i < diseases.size(); i++)
			{
				if((CMLib.english().containsString(A.ID(),diseases.get(i)))
				||(CMLib.english().containsString(A.name(),diseases.get(i))))
				{
					if(msg.target() instanceof MOB)
						((MOB)msg.target()).tell(L("You are immune to @x1.",msg.tool().name()));
					if(msg.source()!=msg.target())
					{
						final String s=(messages.size()>i)?messages.get(i):"";
						if(s.length()>0)
							msg.source().tell(msg.source(),msg.target(),msg.tool(),s);
						else
							msg.source().tell(msg.source(),msg.target(),msg.tool(),L("<T-NAME> seem(s) immune to <O-NAME>."));
					}
					return false;
				}
			}
		}
		return super.okMessage(myHost, msg);
	}

	@Override
	public String getStat(final String code)
	{
		if(code == null)
			return "";
		if(code.equalsIgnoreCase("STAT-LEVEL"))
		{
			int level=0;
			for(final String diseaseID : diseases)
			{
				Ability A=CMClass.getAbility(diseaseID);
				if(A==null)
					A=CMClass.findAbility(diseaseID);
				if(A!=null)
				{
					int mul=-1;
					if(A.abstractQuality()==Ability.QUALITY_MALICIOUS)
						mul=1;
					level += ((mul*(CMLib.ableMapper().lowestQualifyingLevel(A.ID()))/2));
				}
			}
			return ""+level;
		}
		return "";
	}

	@Override
	public void setStat(final String code, final String val)
	{
		if(code!=null)
		{
			if(code.equalsIgnoreCase("STAT-LEVEL"))
			{

			}
			else
			if(code.equalsIgnoreCase("TONEDOWN"))
			{
				setStat("TONEDOWN-MISC",val);
			}
			else
			if((code.equalsIgnoreCase("TONEDOWN-ARMOR"))
			||(code.equalsIgnoreCase("TONEDOWN-WEAPON"))
			||(code.equalsIgnoreCase("TONEDOWN-MISC")))
			{

			}
		}
		super.setStat(code, val);
	}
}
