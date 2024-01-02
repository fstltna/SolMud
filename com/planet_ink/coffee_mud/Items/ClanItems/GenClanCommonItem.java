package com.planet_ink.coffee_mud.Items.ClanItems;

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
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

import com.planet_ink.coffee_mud.Libraries.interfaces.*;

/*
   Copyright 2004-2024 Bo Zimmerman

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
public class GenClanCommonItem extends StdClanCommonItem
{
	@Override
	public String ID()
	{
		return "GenClanCommonItem";
	}

	protected String	readableText	= "";

	public GenClanCommonItem()
	{
		super();
		setName("a generic clan worker item");
		setDisplayText("a generic clan worker item sits here.");
		setDescription("");
		basePhyStats().setWeight(2);
		setMaterial(RawMaterial.RESOURCE_COTTON);
		recoverPhyStats();
	}

	@Override
	public boolean isGeneric()
	{
		return true;
	}

	@Override
	public String text()
	{
		return CMLib.coffeeMaker().getEnvironmentalMiscTextXML(this, false);
	}

	@Override
	public String readableText()
	{
		return readableText;
	}

	@Override
	public void setReadableText(final String text)
	{
		readableText = text;
		glows = text.equalsIgnoreCase("Mining");
	}

	@Override
	public void setMiscText(final String newText)
	{
		miscText = "";
		CMLib.coffeeMaker().unpackEnvironmentalMiscTextXML(this, newText, false);
		recoverPhyStats();
	}

	private final static String[]	MYCODES	= { "CLANID", "CITYPE" };

	@Override
	public String getStat(final String code)
	{
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenItemStat(this, code);
		switch (getInternalCodeNum(code))
		{
		case 0:
			return clanID();
		case 1:
			return "" + getClanItemType().ordinal();
		default:
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}

	@Override
	public void setStat(final String code, final String val)
	{
		if (CMLib.coffeeMaker().getGenItemCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenItemStat(this, code, val);
		else
		{
			switch (getInternalCodeNum(code))
			{
			case 0:
				setClanID(val);
				break;
			case 1:
				setClanItemType(ClanItem.ClanItemType.getValueOf(val));
				break;
			default:
				CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
				break;
			}
		}
	}

	private int getInternalCodeNum(final String code)
	{
		for (int i = 0; i < MYCODES.length; i++)
		{
			if (code.equalsIgnoreCase(MYCODES[i]))
				return i;
		}
		return -1;
	}

	private static String[]	codes	= null;

	@Override
	public String[] getStatCodes()
	{
		if (codes != null)
			return codes;
		final String[] MYCODES = CMProps.getStatCodesList(GenClanCommonItem.MYCODES, this);
		final String[] superCodes = CMParms.toStringArray(GenericBuilder.GenItemCode.values());
		codes = new String[superCodes.length + MYCODES.length];
		int i = 0;
		for (; i < superCodes.length; i++)
			codes[i] = superCodes[i];
		for (int x = 0; x < MYCODES.length; i++, x++)
			codes[i] = MYCODES[x];
		return codes;
	}

	@Override
	public boolean sameAs(final Environmental E)
	{
		if (!(E instanceof GenClanCommonItem))
			return false;
		final String[] codes = getStatCodes();
		for (int i = 0; i < codes.length; i++)
		{
			if (!E.getStat(codes[i]).equals(getStat(codes[i])))
				return false;
		}
		return true;
	}
}
