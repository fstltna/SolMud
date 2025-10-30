package com.planet_ink.coffee_mud.Behaviors;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.MaskingLibrary.CompiledZMask;
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
public class Wimpy extends StdBehavior
{
	@Override
	public String ID()
	{
		return "Wimpy";
	}

	protected int			tickWait	= 0;
	protected int			tickDown	= 0;
	protected boolean		veryWimpy	= false;
	protected String		maskStr		= "";
	protected CompiledZMask	mask		= null;

	@Override
	public boolean grantsAggressivenessTo(final MOB M)
	{
		return false;
	}

	@Override
	public String accountForYourself()
	{
		if(maskStr.trim().length()>0)
			return "wimpy fear of "+CMLib.masking().maskDesc(maskStr,true).toLowerCase();
		else
			return "wimpy fear of combat";
	}

	@Override
	public void setParms(final String newParms)
	{
		super.setParms(newParms);
		tickWait=CMParms.getParmInt(newParms,"delay",0);
		tickDown=tickWait;
		veryWimpy=CMParms.getParmInt(newParms,"very",0)==1;
		maskStr = CMLib.masking().separateZapperMask(newParms);
		this.mask=null;
		if(maskStr.length()>0)
			this.mask=CMLib.masking().getPreCompiledMask(maskStr);
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		super.tick(ticking,tickID);
		if(tickID!=Tickable.TICKID_MOB)
			return true;
		if(((--tickDown)<0)&&(ticking instanceof MOB))
		{
			tickDown=tickWait;
			final MOB monster=(MOB)ticking;
			final Room mobR=monster.location();
			if(mobR!=null)
			{
				for(int m=0;m<mobR.numInhabitants();m++)
				{
					final MOB M=mobR.fetchInhabitant(m);
					if((M!=null)
					&&(M!=monster)
					&&(CMLib.masking().maskCheck(mask,M,false)))
					{
						if((M.getVictim()==monster)
						&&(monster.isInCombat()))
						{
							final List<String> choices = new ArrayList<String>();
							for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
							{
								final Room R=mobR.getRoomInDir(d);
								if((R!=null)
								&&(R.getArea()==mobR.getArea()))
								{
									final Exit E=mobR.getExitInDir(d);
									if(E!=null)
									{
										if(E.isOpen()
										&&((d != Directions.UP)||(CMLib.flags().isFlying(monster))))
											choices.add(CMLib.directions().getDirectionName(d));
									}
								}
							}
							if(choices.size()>0)
								CMLib.commands().postFlee(monster, choices.get(CMLib.dice().roll(1, choices.size(), -1)));
							else
							{
								CMLib.commands().postFlee(monster, "NOWHERE");
								CMLib.tracking().beMobile(monster,false,false,false,false,null,null);
							}
						}
						if((veryWimpy&&(!monster.isInCombat())))
						{
							final List<Behavior> V=CMLib.flags().flaggedBehaviors(monster,Behavior.FLAG_MOBILITY);
							for(final Behavior B : V)
							{
								int tries=0;
								while(((++tries)<100)&&(mobR==monster.location()))
									B.tick(monster,Tickable.TICKID_MOB);
								if(mobR!=monster.location())
									return true;
							}
							if(mobR==monster.location())
								CMLib.tracking().beMobile(monster,false,false,false,false,null,null);
						}
					}
				}
			}
		}
		return true;
	}
}
