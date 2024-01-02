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
public class Prop_RoomsForSale extends Prop_RoomForSale
{
	@Override
	public String ID()
	{
		return "Prop_RoomsForSale";
	}

	@Override
	public String name()
	{
		return "Putting a cluster of rooms up for sale";
	}

	protected String	uniqueLotID	= null;

	@Override
	public Room getATitledRoom()
	{
		if(affected instanceof Room)
			return (Room)affected;
		else
			return CMLib.map().getRoom(landPropertyID());
	}

	@Override
	public List<Room> getTitledRooms()
	{
		final List<Room> roomsV=new ArrayList<Room>();
		final Room R = getATitledRoom();
		if(R!=null) // only return cached rooms here!
			fillCluster(R,roomsV,null,false);
		return roomsV;
	}

	@Override
	public int getNumTitledRooms()
	{
		//TODO: make this faster, maybe?
		return getTitledRooms().size();
	}

	@Override
	public String getTitleID()
	{
		return this.getUniqueLotID();
	}

	// update title, since it may affect room clusters, worries about EVERYONE
	@Override
	public void updateTitle()
	{
		final List<Room> V=getTitledRooms();
		final String owner=getOwnerName();
		final int price=getPrice();
		final boolean rental=rentalProperty();
		final boolean gridLayout = gridLayout();
		final int back=backTaxes();
		String uniqueID="ROOMS_PROPERTY_"+this;
		if(V.size()>0)
			uniqueID="ROOMS_PROPERTY_"+CMLib.map().getExtendedRoomID(V.get(0));
		for(int v=0;v<V.size();v++)
		{
			Room R=V.get(v);
			synchronized(CMClass.getSync("SYNC"+R.roomID()))
			{
				R=CMLib.map().getRoom(R);
				final LandTitle A=(LandTitle)R.fetchEffect(ID());
				if((A!=null)
				&&((!A.getOwnerName().equals(owner))
				   ||(A.getPrice()!=price)
				   ||(A.backTaxes()!=back)
				   ||(A.rentalProperty()!=rental)))
				{
					A.setOwnerName(owner);
					A.setPrice(price);
					A.setBackTaxes(back);
					A.setRentalProperty(rental);
					A.setGridLayout(gridLayout);
					CMLib.database().DBUpdateRoom(R);
				}
				if(A instanceof Prop_RoomsForSale)
					((Prop_RoomsForSale)A).uniqueLotID=uniqueID;
			}
		}
	}

	@Override
	public String getUniqueLotID()
	{
		if(uniqueLotID==null)
			updateTitle();
		return uniqueLotID;
	}

	@Override
	public int getNumConnectedPropertyRooms()
	{
		//TODO: WRONG! AllTitledRooms can include the Uncached.  So, wrong.
		return getTitledRooms().size();
	}

	// update lot, since its called by the savethread, ONLY worries about itself
	@Override
	public void updateLot(final Set<String> optPlayerList)
	{
		if(affected instanceof Room)
		{
			int[] data=updateLotWithThisData((Room)affected,this,false,scheduleReset,optPlayerList,lastItemNums,daysWithNoChange);
			lastItemNums=data[0];
			daysWithNoChange=data[1];
			if((lastDayDone!=((Room)affected).getArea().getTimeObj().getDayOfMonth())
			&&(CMProps.getBoolVar(CMProps.Bool.MUDSTARTED)))
			{
				final Room R=(Room)affected;
				lastDayDone=R.getArea().getTimeObj().getDayOfMonth();
				final List<Room> V=getTitledRooms();
				for(int v=0;v<V.size();v++)
				{
					final Room R2=V.get(v);
					final Prop_RoomForSale PRFS=(Prop_RoomForSale)R2.fetchEffect(ID());
					if(PRFS!=null)
						PRFS.lastDayDone=R.getArea().getTimeObj().getDayOfMonth();
				}
				if((getOwnerName().length()>0)&&rentalProperty()&&(R.roomID().length()>0))
				{
					if(doRentalProperty(R.getArea(),R.roomID(),getOwnerName(),getPrice()))
					{
						setOwnerName("");
						updateTitle();
						data=updateLotWithThisData((Room)affected,this,false,scheduleReset,optPlayerList,lastItemNums,daysWithNoChange);
						lastItemNums=data[0];
						daysWithNoChange=data[1];
					}
				}
			}
			scheduleReset=false;
		}
	}
}
