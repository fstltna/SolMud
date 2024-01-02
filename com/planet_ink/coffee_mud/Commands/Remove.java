package com.planet_ink.coffee_mud.Commands;
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
public class Remove extends StdCommand
{
	public Remove()
	{
	}

	private final String[]	access	= I(new String[] { "REMOVE", "REM" });

	@Override
	public String[] getAccessWords()
	{
		return access;
	}

	private final static Class<?>[][] internalParameters=new Class<?>[][]{{Item.class},{Item.class,Boolean.class}};

	@Override
	public boolean execute(final MOB mob, final List<String> commands, final int metaFlags)
		throws java.io.IOException
	{
		final Vector<String> origCmds=new XVector<String>(commands);
		if(commands.size()<2)
		{
			CMLib.commands().postCommandFail(mob,origCmds,L("Remove what?"));
			return false;
		}
		commands.remove(0);
		final List<Item> items = new ArrayList<Item>();
		items.addAll(CMLib.english().fetchItemList(mob,mob,null,commands,Wearable.FILTER_WORNONLY,false));
		if(items.size()==0)
			CMLib.commands().postCommandFail(mob,origCmds,L("You don't seem to be wearing that."));
		else
		for(int i=0;i<items.size();i++)
		{
			final Item item=items.get(i);
			final CMMsg msg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,L("<S-NAME> remove(s) <T-NAME>."));
			if(mob.location().okMessage(mob,msg))
				mob.location().send(mob,msg);
			else
			if(items.size()==1)
				CMLib.commands().postCommandRejection(msg.source(),msg.target(),msg.tool(),origCmds);
		}
		return false;
	}

	@Override
	public Object executeInternal(final MOB mob, final int metaFlags, final Object... args) throws java.io.IOException
	{
		if(!super.checkArguments(internalParameters, args))
			return Boolean.FALSE;
		if(args[0] instanceof Item)
		{
			final Room R=mob.location();
			final Item item=(Item)args[0];
			final boolean quiet=((args.length>1) && (args[1] instanceof Boolean)) ? ((Boolean)args[1]).booleanValue() : false;
			final CMMsg newMsg=CMClass.getMsg(mob,item,null,CMMsg.MSG_REMOVE,quiet?null:L("<S-NAME> remove(s) <T-NAME>."));
			if(R==null)
			{
				if( mob.okMessage(mob,newMsg)
				&& item.okMessage(mob,newMsg))
				{
					mob.executeMsg(mob,newMsg);
					item.executeMsg(mob,newMsg);
					return Boolean.TRUE;
				}
			}
			else
			if(R.okMessage(mob,newMsg))
			{
				R.send(mob,newMsg);
				return Boolean.TRUE;
			}
			return Boolean.FALSE;
		}
		return Boolean.FALSE;
	}

	@Override
	public double combatActionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandCombatActionCost(ID());
	}

	@Override
	public double actionsCost(final MOB mob, final List<String> cmds)
	{
		return CMProps.getCommandActionCost(ID());
	}

	@Override
	public boolean canBeOrdered()
	{
		return true;
	}

}
