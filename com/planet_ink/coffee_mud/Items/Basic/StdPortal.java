package com.planet_ink.coffee_mud.Items.Basic;
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
@SuppressWarnings("unchecked")
public class StdPortal extends StdContainer implements Rideable, Exit
{
	protected String	doorName	= "";
	protected String	closedText	= "";
	protected String	putString		= "";
	protected String	rideString		= "";
	protected String	stateString		= "";
	protected String	stateSubjectStr	= "";
	protected String	mountString		= "";
	protected String	dismountString	= "";

	@Override
	public String ID()
	{
		return "StdPortal";
	}

	public StdPortal()
	{
		super();
		setName("a portal");
		setDisplayText("a portal is here.");
		setDescription("It's difficult to see where it leads.  Try ENTER and find out!");
		basePhyStats().setWeight(10000);
		recoverPhyStats();
		capacity=10000;
		material=RawMaterial.RESOURCE_NOTHING;
	}

	// common item/mob stuff
	@Override
	public boolean isMobileRideBasis()
	{
		return false;
	}

	@Override
	public String genericName()
	{
		if(CMLib.english().startsWithAnIndefiniteArticle(name())&&(CMStrings.numWords(name())<4))
			return CMStrings.removeColors(name());
		return L("a portal");
	}

	@Override
	public Basis rideBasis()
	{
		return Rideable.Basis.ENTER_IN;
	}

	@Override
	public void setRideBasis(final Basis basis)
	{
	}

	@Override
	public int riderCapacity()
	{
		return 1;
	}

	@Override
	public void setRiderCapacity(final int newCapacity)
	{
	}

	@Override
	public int numRiders()
	{
		return 0;
	}

	@Override
	public Enumeration<Rider> riders()
	{
		return EmptyEnumeration.INSTANCE;
	}

	@Override
	public Rider fetchRider(final int which)
	{
		return null;
	}

	@Override
	public void addRider(final Rider mob)
	{
	}

	@Override
	public void delRider(final Rider mob)
	{
	}

	@Override
	public void recoverPhyStats()
	{
		CMLib.flags().setReadable(this, false);
		super.recoverPhyStats();
	}

	@Override
	public Set<MOB> getRideBuddies(final Set<MOB> list)
	{
		return list;
	}

	@Override
	public boolean mobileRideBasis()
	{
		return false;
	}

	@Override
	public String stateString(final Rider R)
	{
		if((R==null)||(stateString.length()==0))
			return "in";
		return stateString;
	}

	@Override
	public String getStateString()
	{
		return stateString;
	}

	@Override
	public void setStateString(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			stateString="";
		else
		{
			if(str.equalsIgnoreCase(this.stateString(null)))
				stateString="";
			else
				stateString=str.trim();
		}
	}

	@Override
	public String putString(final Rider R)
	{
		if((R==null)||(putString.length()==0))
			return "in";
		return putString;
	}

	@Override
	public String getPutString()
	{
		return putString;
	}

	@Override
	public void setPutString(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			putString="";
		else
		{
			if(str.equalsIgnoreCase(this.putString(null)))
				putString="";
			else
				putString=str.trim();
		}
	}

	@Override
	public String mountString(final int commandType, final Rider R)
	{
		if((R==null)||(mountString.length()==0))
			return "enter(s)";
		return mountString;
	}

	@Override
	public String getMountString()
	{
		return mountString;
	}

	@Override
	public void setMountString(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			mountString="";
		else
		{
			if(str.equalsIgnoreCase(this.mountString(0,null)))
				mountString="";
			else
				mountString=str.trim();
		}
	}

	@Override
	public String dismountString(final Rider R)
	{
		if((R==null)||(dismountString.length()==0))
			return "emerge(s) from";
		return dismountString;
	}

	@Override
	public String getDismountString()
	{
		return dismountString;
	}

	@Override
	public void setDismountString(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			dismountString="";
		else
		{
			if(str.equalsIgnoreCase(this.dismountString(null)))
				dismountString="";
			else
				dismountString=str.trim();
		}
	}

	@Override
	public String stateStringSubject(final Rider R)
	{
		if((R==null)||(stateSubjectStr.length()==0))
			return "occupied by";
		return stateSubjectStr;
	}

	@Override
	public String getStateStringSubject()
	{
		return stateSubjectStr;
	}

	@Override
	public void setStateStringSubject(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			this.stateSubjectStr="";
		else
		{
			if(str.equalsIgnoreCase(this.stateStringSubject(null)))
				stateSubjectStr="";
			else
				stateSubjectStr=str.trim();
		}
	}

	@Override
	public String rideString(final Rider R)
	{
		if((R==null)||(rideString.length()==0))
			return "enter(s)";
		return rideString;
	}

	@Override
	public String getRideString()
	{
		return rideString;
	}

	@Override
	public void setRideString(final String str)
	{
		if((str==null)||(str.trim().length()==0))
			rideString="";
		else
		{
			if(str.equalsIgnoreCase(this.rideString(null)))
				rideString="";
			else
				rideString=str.trim();
		}
	}

	@Override
	public short exitUsage(final short change)
	{
		return 0;
	}

	@Override
	public String displayText()
	{
		return displayText;
	}

	@Override
	public boolean amRiding(final Rider mob)
	{
		return false;
	}

	protected Room getDestinationRoom(final Room fromRoom)
	{
		Room R=null;
		final List<String> V=CMParms.parseSemicolons(readableText(),true);
		if(V.size()>0)
			R=CMLib.map().getRoom(V.get(CMLib.dice().roll(1,V.size(),-1)));
		return R;
	}

	@Override
	public Room lastRoomUsedFrom(final Room fromRoom)
	{
		return getDestinationRoom(fromRoom);
	}

	protected final Exit[] tempExitPairs = new Exit[2];
	protected Exit[] getTemporaryExits()
	{
		synchronized(tempExitPairs)
		{
			if(tempExitPairs[0]==null)
			{
				tempExitPairs[0]=CMClass.getExit("OpenPrepositional");
				tempExitPairs[1]=CMClass.getExit("OpenPrepositional");
				tempExitPairs[0].setMiscText(name());
				tempExitPairs[1].setMiscText(name());
			}
		}
		return tempExitPairs;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_LEAVE:
			if((msg.tool() == getTemporaryExits()[0])
			&&(msg.target() instanceof Room))
			{
				final CMMsg msg2 = (CMMsg)msg.copyOf();
				msg2.setTarget(this);
				msg2.setSourceCode(CMMsg.MSG_ENTER);
				msg2.setTargetCode(CMMsg.MSG_ENTER);
				if(!super.okMessage(myHost, msg2))
					return false;
			}
			break;
		case CMMsg.TYP_DISMOUNT:
			if(msg.amITarget(this))
			{
				// protects from standard item rejection
				return true;
			}
			break;
		case CMMsg.TYP_SIT:
			if(msg.amITarget(this))
			{
				if(msg.sourceMessage().indexOf(mountString(CMMsg.TYP_SIT,msg.source()))>0)
				{
					if(getDestinationRoom(msg.source().location())==null)
					{
						msg.source().tell(L("This portal is broken.. nowhere to go!"));
						return false;
					}
					if(hasADoor()&&(!isOpen()))
					{
						msg.source().tell(L("@x1 is closed.",name()));
						return false;
					}
					msg.modify(msg.source(),msg.target(),msg.tool(),
							   msg.sourceMajor()|CMMsg.TYP_ENTER,msg.sourceMessage(),
							   msg.targetMajor()|CMMsg.TYP_ENTER,msg.targetMessage(),
							   msg.othersMajor()|CMMsg.TYP_ENTER,null);
					return true;
				}
				msg.source().tell(L("You cannot sit on @x1.",name()));
				return false;
			}
			break;
		case CMMsg.TYP_SLEEP:
			if(msg.amITarget(this))
			{
				msg.source().tell(L("You cannot sleep on @x1.",name()));
				return false;
			}
			break;
		case CMMsg.TYP_MOUNT:
			if(msg.amITarget(this))
			{
				msg.source().tell(L("You cannot mount @x1, try Enter.",name()));
				return false;
			}
			break;
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		switch(msg.targetMinor())
		{
		case CMMsg.TYP_DISMOUNT:
			break;
		case CMMsg.TYP_ENTER:
			if(msg.amITarget(this))
			{
				if(msg.sourceMessage().indexOf(mountString(CMMsg.TYP_SIT,msg.source()))>0)
				{
					final Room thisRoom=msg.source().location();
					Room R=getDestinationRoom(thisRoom);
					if(R==null)
						R=thisRoom;
					final Exit[] tempExits = getTemporaryExits();
					final Exit E2=tempExits[0];
					final Exit E=tempExits[1];
					final Exit oldE=thisRoom.getRawExit(Directions.GATE);
					final Room oldR=thisRoom.rawDoors()[Directions.GATE];
					final Exit oldE2=R.getRawExit(Directions.GATE);
					try
					{
						synchronized(CMClass.getSync(("GATE_"+CMLib.map().getExtendedTwinRoomIDs(thisRoom,R))))
						{
							thisRoom.rawDoors()[Directions.GATE]=R;
							thisRoom.setRawExit(Directions.GATE,E);
							E2.basePhyStats().setDisposition(PhyStats.IS_NOT_SEEN);
							R.setRawExit(Directions.GATE,E2);
							CMLib.tracking().walk(msg.source(),Directions.GATE,false,false,false);
						}
					}
					finally
					{
						thisRoom.rawDoors()[Directions.GATE]=oldR;
						thisRoom.setRawExit(Directions.GATE,oldE);
						R.setRawExit(Directions.GATE,oldE2);
					}
					msg.setTarget(null);
				}
			}
			break;
		case CMMsg.TYP_LEAVE:
			if((msg.tool() == getTemporaryExits()[0])
			&&(msg.target() instanceof Room))
			{
				final CMMsg msg2 = (CMMsg)msg.copyOf();
				msg2.setTarget(this);
				msg2.setSourceCode(CMMsg.MSG_ENTER);
				msg2.setTargetCode(CMMsg.MSG_ENTER);
				super.executeMsg(myHost, msg2);
			}
			break;
		case CMMsg.TYP_FLEE:
		case CMMsg.TYP_SLEEP:
		case CMMsg.TYP_MOUNT:
		case CMMsg.TYP_SIT:
			break;
		}
	}

	@Override
	public boolean hasADoor()
	{
		return super.hasADoor();
	}

	@Override
	public boolean defaultsLocked()
	{
		return super.hasALock();
	}

	@Override
	public boolean defaultsClosed()
	{
		return super.hasADoor();
	}

	@Override
	public void setDoorsNLocks(final boolean hasADoor, final boolean isOpen, final boolean defaultsClosed, final boolean hasALock, final boolean isLocked, final boolean defaultsLocked)
	{
		super.setDoorsNLocks(hasADoor, isOpen, defaultsClosed, hasALock, isLocked, defaultsLocked);
	}

	@Override
	public boolean isReadable()
	{
		return false;
	}

	@Override
	public void setReadable(final boolean isTrue)
	{
	}

	private static final StringBuilder empty=new StringBuilder("");

	@Override
	public StringBuilder viewableText(final MOB mob, final Room myRoom)
	{
		Room room=this.getDestinationRoom(myRoom);
		if(room == null)
			room = myRoom;
		if(room==null)
			return empty;
		final StringBuilder Say=new StringBuilder("");
		if(mob.isAttributeSet(MOB.Attrib.SYSOPMSGS))
		{
			Say.append("^H("+CMLib.map().getExtendedRoomID(room)+")^? "+room.displayText(mob)+CMLib.flags().getDispositionBlurbs(room,mob)+" ");
			Say.append("via ^H("+ID()+")^? "+(isOpen()?name():closedText()));
		}
		else
		if(( CMLib.flags().canBeSeenBy(this,mob)|| (isOpen()&&hasADoor()))
		&&(CMLib.flags().isSeeable(this)))
		{
			if(isOpen())
			{
				if(!CMLib.flags().canBeSeenBy(room,mob))
					Say.append("darkness");
				else
					Say.append(name()+CMLib.flags().getDispositionBlurbs(this,mob));
			}
			else
			if((CMLib.flags().canBeSeenBy(this,mob))&&(closedText().trim().length()>0))
				Say.append(closedText()+CMLib.flags().getDispositionBlurbs(this,mob));
		}
		return Say;
	}

	@Override
	public String doorName()
	{
		return doorName;
	}

	@Override
	public String closedText()
	{
		return closedText;
	}

	@Override
	public String closeWord()
	{
		return "close";
	}

	@Override
	public String openWord()
	{
		return "open";
	}

	@Override
	public void setExitParams(final String newDoorName,
							  final String newCloseWord,
							  final String newOpenWord,
							  final String newClosedText)
							  {
		doorName=newDoorName;
		closedText=newClosedText;
	}

	@Override
	public int openDelayTicks()
	{
		return 0;
	}

	@Override
	public void setOpenDelayTicks(final int numTicks)
	{
	}

	@Override
	public String temporaryDoorLink()
	{
		return "";
	}

	@Override
	public void setTemporaryDoorLink(final String link)
	{
	}

}
