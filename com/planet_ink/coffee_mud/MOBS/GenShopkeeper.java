package com.planet_ink.coffee_mud.MOBS;

import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.TimeClock.TimePeriod;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;

import com.planet_ink.coffee_mud.Libraries.interfaces.*;

/*
   Copyright 2001-2024 Bo Zimmerman

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
public class GenShopkeeper extends StdShopKeeper
{
	@Override
	public String ID()
	{
		return "GenShopkeeper";
	}

	protected String	prejudiceFactors	= "";
	private String		ignoreMask			= "";

	public GenShopkeeper()
	{
		super();
		_name = "a generic shopkeeper";
		setDescription("He looks like he wants to sell something to you.");
		setDisplayText("A generic shopkeeper stands here.");
		basePhyStats().setAbility(CMProps.getMobHPBase()); // his only off-default
	}

	@Override
	public boolean isGeneric()
	{
		return true;
	}

	@Override
	public String getRawPrejudiceFactors()
	{
		return prejudiceFactors;
	}

	@Override
	public void setPrejudiceFactors(final String factors)
	{
		prejudiceFactors = factors;
	}

	@Override
	public String getRawIgnoreMask()
	{
		return ignoreMask;
	}

	@Override
	public void setIgnoreMask(final String factors)
	{
		ignoreMask = factors;
	}

	@Override
	public String text()
	{
		if (CMProps.getBoolVar(CMProps.Bool.MOBCOMPRESS))
			miscText = CMLib.encoder().compressString(CMLib.coffeeMaker().getEnvironmentalMiscTextXML(this, false));
		else
			miscText = CMLib.coffeeMaker().getEnvironmentalMiscTextXML(this, false);
		return super.text();
	}

	@Override
	public void setMiscText(final String newText)
	{
		super.setMiscText(newText);
		CMLib.coffeeMaker().resetGenMOB(this, newText);
	}

	private final static String[]	MYCODES	= { "WHATISELL", "PREJUDICE", "BUDGET", "DEVALRATE",
												"INVRESETRATE", "IGNOREMASK", "PRICEMASKS",
												"ITEMMASK","SIVIEWTYPES"};

	@Override
	public String getStat(final String code)
	{
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			return CMLib.coffeeMaker().getGenMobStat(this, code);
		switch (getCodeNum(code))
		{
		case 0:
			return "" + getWhatIsSoldMask();
		case 1:
			return getRawPrejudiceFactors();
		case 2:
			return getRawBbudget();
		case 3:
			return getRawDevalueRate();
		case 4:
			return "" + getRawInvResetRate();
		case 5:
			return getRawIgnoreMask();
		case 6:
			return CMParms.toListString(getRawItemPricingAdjustments());
		case 7:
			return this.getWhatIsSoldZappermask();
		case 8:
			return CMParms.toListString(viewFlags());
		default:
			if(code.equalsIgnoreCase("BUDGETRESETDATE"))
			{
				final TimeClock C = (TimeClock)CMLib.time().homeClock(this).copyOf();
				C.bump(TimePeriod.HOUR, (int)(this.budgetTickDown/CMProps.getTicksPerMudHour()));
				return C.getShortTimeDescription();
			}
			else
			if(code.equalsIgnoreCase("INVRESETDATE"))
			{
				final TimeClock C = (TimeClock)CMLib.time().homeClock(this).copyOf();
				C.bump(TimePeriod.HOUR, (int)(this.invResetTickDown/CMProps.getTicksPerMudHour()));
				return C.getShortTimeDescription();
			}
			return CMProps.getStatCodeExtensionValue(getStatCodes(), xtraValues, code);
		}
	}

	@Override
	public void setStat(final String code, final String val)
	{
		if (CMLib.coffeeMaker().getGenMobCodeNum(code) >= 0)
			CMLib.coffeeMaker().setGenMobStat(this, code, val);
		else
		{
			switch (getCodeNum(code))
			{
			case 0:
			{
				if ((val.length() == 0) || (CMath.isLong(val)))
					setWhatIsSoldMask(CMath.s_long(val));
				else
				if (CMParms.containsIgnoreCase(ShopKeeper.DEAL_DESCS, val))
					setWhatIsSoldMask(CMParms.indexOfIgnoreCase(ShopKeeper.DEAL_DESCS, val));
				break;
			}
			case 1:
				setPrejudiceFactors(val);
				break;
			case 2:
				setBudget(val);
				break;
			case 3:
				setDevalueRate(val);
				break;
			case 4:
				setInvResetRate(CMath.s_parseIntExpression(val));
				break;
			case 5:
				setIgnoreMask(val);
				break;
			case 6:
				setItemPricingAdjustments((val.trim().length() == 0) ? new String[0] : CMParms.toStringArray(CMParms.parseCommas(val, true)));
				break;
			case 7:
				this.setWhatIsSoldZappermask(val.trim());
				break;
			case 8:
				viewFlags().clear();
				for(final String s : CMParms.parseCommas(val.toUpperCase().trim(), true))
				{
					final ViewType V=(ViewType)CMath.s_valueOf(ViewType.class, s);
					if(V!=null)
						viewFlags().add(V);
				}
				break;
			default:
				CMProps.setStatCodeExtensionValue(getStatCodes(), xtraValues, code, val);
				break;
			}
		}
	}

	@Override
	protected int getCodeNum(final String code)
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
		final String[] MYCODES = CMProps.getStatCodesList(GenShopkeeper.MYCODES, this);
		final String[] superCodes = CMParms.toStringArray(GenericBuilder.GenMOBCode.values());
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
		if (!(E instanceof GenShopkeeper))
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
