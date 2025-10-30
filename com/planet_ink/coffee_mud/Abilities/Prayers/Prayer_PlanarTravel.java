package com.planet_ink.coffee_mud.Abilities.Prayers;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.StdPlanarAbility;
import com.planet_ink.coffee_mud.Abilities.interfaces.PlanarAbility.*;
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

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/*
   Copyright 2016-2025 Bo Zimmerman

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
public class Prayer_PlanarTravel extends StdPlanarAbility
{
	@Override
	public String ID()
	{
		return "Prayer_PlanarTravel";
	}

	private final static String	localizedName	= CMLib.lang().L("Planar Travel");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	protected int canTargetCode()
	{
		return 0;
	}

	@Override
	public int classificationCode()
	{
		return Ability.ACODE_PRAYER | Ability.DOMAIN_COSMOLOGY;
	}

	@Override
	public long flags()
	{
		return Ability.FLAG_UNHOLY | Ability.FLAG_HOLY | Ability.FLAG_TRANSPORTING;
	}

	@Override
	protected int overrideMana()
	{
		return Ability.COST_ALL - 90;
	}

	@Override
	public int abstractQuality()
	{
		return Ability.QUALITY_INDIFFERENT;
	}

	private static final String[] triggerStrings =I(new String[] {"PRAY","PR"});

	@Override
	public String[] triggerStrings()
	{
		return triggerStrings;
	}

	protected String prayWord(final MOB mob)
	{
		if(mob.charStats().getWorshipCharID().length()>0)
			return "pray(s) to "+mob.charStats().getWorshipCharID();
		return "pray(s)";
	}

	@Override
	protected String castingMessage(final MOB mob, final boolean auto)
	{
		return auto?L("<S-NAME> gain(s) access to other planes of existence!"):L("^S<S-NAME> @x1 for access to other planes of existence!^?",prayWord(mob));
	}

	@Override
	protected String failMessage(final MOB mob, final boolean auto)
	{
		return L("<S-NAME> @x1 for planar travel, but nothing happens.",prayWord(mob));
	}

	@Override
	public boolean invoke(final MOB mob, final List<String> commands, final Physical givenTarget, final boolean auto, final int asLevel)
	{
		if(!Prayer.prayerAlignmentCheck(this,mob,auto))
			return false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		return true;
	}
}
