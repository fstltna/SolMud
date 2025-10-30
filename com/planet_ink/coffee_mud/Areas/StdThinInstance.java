package com.planet_ink.coffee_mud.Areas;
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
import com.planet_ink.coffee_mud.Items.Basic.StdItem;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary.RFilter;
import com.planet_ink.coffee_mud.Libraries.interfaces.TrackingLibrary.RFilters;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.lang.ref.WeakReference;
import java.util.*;

/*
   Copyright 2008-2025 Bo Zimmerman

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
public class StdThinInstance extends StdThinArea implements SubArea
{
	@Override
	public String ID()
	{
		return "StdThinInstance";
	}

	protected long	flags	= Area.FLAG_THIN | Area.FLAG_INSTANCE_PARENT;

	@Override
	public long flags()
	{
		return flags;
	}

	protected final List<AreaInstanceChild>	instanceChildren	= new SVector<AreaInstanceChild>();
	protected volatile int					instanceCounter		= 0;
	protected long							childCheckDown		= CMProps.getMillisPerMudHour() / CMProps.getTickMillis();
	protected volatile int					childTimeout		= 0;
	protected WeakReference<Area>			parentArea			= null;

	protected String getStrippedRoomID(final String roomID)
	{
		final int x=roomID.indexOf('#');
		if(x<0)
			return null;
		return roomID.substring(x);
	}

	protected String convertToMyArea(final String roomID)
	{
		final String strippedID=getStrippedRoomID(roomID);
		if(strippedID==null)
			return null;
		return Name()+strippedID;
	}

	protected boolean qualifiesToBeParentArea(final Area parentA)
	{
		return (CMath.bset(parentA.flags(),Area.FLAG_INSTANCE_PARENT))
		&&(!CMath.bset(parentA.flags(),Area.FLAG_INSTANCE_CHILD));
	}

	@Override
	public Area getSuperArea()
	{
		if((parentArea!=null)&&(parentArea.get()!=null))
			return parentArea.get();
		final int x=Name().indexOf('_');
		if(x<0)
			return null;
		if(!CMath.isNumber(Name().substring(0,x)))
			return null;
		final Area parentA = CMLib.map().getArea(Name().substring(x+1));
		if((parentA==null)
		||(!qualifiesToBeParentArea(parentA)))
			return null;
		parentArea=new WeakReference<Area>(parentA);
		return parentA;
	}

	@Override
	public Room getRoom(String roomID)
	{
		if(!CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD))
			return super.getRoom(roomID);

		if(!isRoom(roomID))
			return null;
		final Room existingR=super.getRoomBase(roomID);
		if(((existingR==null)||(existingR.amDestroyed()))&&(roomID!=null))
		{
			final Area parentA=getSuperArea();
			if(parentA==null)
				return null;

			if(roomID.toUpperCase().startsWith(Name().toUpperCase()+"#"))
				roomID=Name()+roomID.substring(Name().length()); // for case sensitive situations
			final Room parentR=parentA.getRoom(parentA.Name()+getStrippedRoomID(roomID));
			if(parentR==null)
				return null;

			final Room newR=CMLib.database().DBReadRoomObject(parentR.roomID(), true, false);
			if(newR==null)
				return null;
			final TreeMap<String,Room> V=new TreeMap<String,Room>();
			V.put(roomID,newR);
			newR.setArea(this); // done here, then deleted, so loaded mobs know where they are
			this.delProperRoom(newR);
			this.delProperRoomnumber(newR.roomID());
			CMLib.database().DBReadRoomExits(newR.roomID(), newR, false);
			CMLib.database().DBReadContent(newR.roomID(), newR, true);
			newR.clearSky();
			if(newR instanceof GridLocale)
				((GridLocale)newR).clearGrid(null);
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				newR.rawDoors()[d]=null;
			newR.setRoomID(roomID);
			newR.setArea(this); // must be done after the roomid is corrected for adds
			addProperRoom(newR);

			synchronized(CMClass.getSync(("SYNC"+roomID)))
			{
				final Room[] newDoors = newR.rawDoors();
				for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
				{
					final Room dirR=parentR.rawDoors()[d];
					if(dirR!=null)
					{
						final String myRID=dirR.roomID();
						if((myRID!=null)
						&&(myRID.length()>0)
						&&(dirR.getArea()==parentA))
						{
							final String localDirRID=convertToMyArea(myRID);
							final Room localDirR=getProperRoom(localDirRID);
							if(localDirR!=null)
								newDoors[d]=localDirR;
							else
							if(localDirRID==null)
								Log.errOut("StdThinInstance","Error in linked room ID "+parentR.roomID()+", dir="+d);
							else
							{
								newDoors[d]=CMClass.getLocale("ThinRoom");
								newDoors[d].setRoomID(localDirRID);
								newDoors[d].setArea(this);
							}
						}
						else
							newDoors[d]=dirR;
					}
				}
			}
			//for(final Enumeration<MOB> e=newR.inhabitants();e.hasMoreElements();) // is this really necc?
			//	e.nextElement().bringToLife(newR,true);
			newR.startItemRejuv();
			fillInAreaRoom(newR);
			newR.setExpirationDate(System.currentTimeMillis()+WorldMap.ROOM_EXPIRATION_MILLIS);
			final MOB mob=CMClass.getFactoryMOB("the wind",1,newR);
			try
			{
				newR.executeMsg(mob, CMClass.getMsg(mob, newR, CMMsg.MSG_NEWROOM, null));
			}
			finally
			{
				mob.destroy();
			}
			return newR;
		}
		return existingR;
	}

	protected static boolean isInThisArea(final Area childA, final MOB mob)
	{
		if(mob != null)
		{
			final Room R=mob.location();
			if(R!=null)
			{
				final Area A=R.getArea();
				if(R.getArea()==childA)
					return true;
				if(A instanceof Boardable)
				{
					final Room R2=CMLib.map().roomLocation(((Boardable)A).getBoardableItem());
					if((R2!=null)
					&&(R2.getArea()==childA))
						return true;
				}
			}
		}
		return false;
	}

	protected Set<Physical> getProtectedSet(final Area childA, final List<WeakReference<MOB>> registeredMobList)
	{
		final Set<Physical> protectTheseList=new HashSet<Physical>();
		for(final WeakReference<MOB> wmob : registeredMobList)
		{
			final MOB M=wmob.get();
			if(M!=null)
			{
				final Room R=M.location();
				if(CMLib.flags().isInTheGame(M,true)
				&&(R!=null)
				&&(R.getArea()==childA))
					protectTheseList.add(M);
			}
		}
		for(final Enumeration<MOB> m = CMLib.players().players();m.hasMoreElements();)
		{
			final MOB M=m.nextElement();
			if(M!=null)
			{
				final Room R=M.location();
				if((R!=null)
				&&(R.getArea()==childA))
					protectTheseList.add(M);
			}
		}
		for(final Enumeration<Room> r=childA.getFilledCompleteMap();r.hasMoreElements();)
		{
			final Room R=r.nextElement();
			if(R!=null)
			{
				for(final Enumeration<Item> i=R.items();i.hasMoreElements();)
				{
					final Item I=i.nextElement();
					if((I instanceof DeadBody)
					&&(((DeadBody)I).isPlayerCorpse()))
						protectTheseList.add(I);
				}
			}
		}
		for(final Enumeration<Boardable> s = CMLib.map().ships();s.hasMoreElements();)
		{
			final Boardable B=s.nextElement();
			if(B!=null)
			{
				final Item I=B.getBoardableItem();
				if((I!=null)
				&&(I.baseGoldValue()>2)
				&&(I.owner() instanceof Room))
				{
					final Room R=(Room)I.owner();
					if((R!=null)
					&&(R.getArea()==childA)
					&&(B instanceof PrivateProperty)
					&&(((PrivateProperty)B).getOwnerName()!=null)
					&&(((PrivateProperty)B).getOwnerName().length()>0))
					{
						protectTheseList.add(I);
					}
				}
			}
		}
		final List<MOB> addFollowers=new ArrayList<MOB>(0);
		for(final Physical p : protectTheseList)
		{
			if(p instanceof MOB)
			{
				final MOB pmob=(MOB)p;
				final Set<MOB> grp=pmob.getGroupMembers(new HashSet<MOB>());
				for(final MOB M : grp)
				{
					if(pmob!=M)
					{
						final Room R=M.location();
						if((R!=null)
						&&(R.getArea()==childA))
							addFollowers.add(M);
					}
				}
			}
		}
		protectTheseList.addAll(addFollowers);
		return protectTheseList;
	}

	protected static class EmptyFilters implements RFilters
	{
		@Override
		public boolean isFilteredOut(final Room hostR, final Room R, final Exit E, final int dir)
		{
			return false;
		}

		@Override
		public RFilters plus(final RFilter filter)
		{
			return this;
		}

		@Override
		public RFilters minus(final RFilter filter)
		{
			return this;
		}

		@Override
		public RFilters copyOf()
		{
			return this;
		}
	}

	protected static class AllWaterFilters extends EmptyFilters
	{
		@Override
		public boolean isFilteredOut(final Room hostR, final Room R, final Exit E, final int dir)
		{
			return (!(CMLib.flags().isWateryRoom(hostR)||CMLib.flags().isWateryRoom(R)));
		}
	}

	protected static class OutOfAreaFilter implements RFilter
	{
		protected final Area A1;

		public OutOfAreaFilter(final Area A1)
		{
			this.A1=A1;
		}

		@Override
		public boolean isFilteredOut(final Room hostR, final Room R, final Exit E, final int dir)
		{
			return (R.getArea()==A1);
		}
	}

	protected void flushInstance(final AreaInstanceChild child)
	{
		final Area childA=child.A;
		final Set<Physical> protectedList = getProtectedSet(childA, child.mobs);
		childA.setAreaState(State.STOPPED); // kill motion!
		Room returnBoatsToR = null;
		for(final Physical P : protectedList)
		{
			if(P instanceof MOB)
			{
				final MOB wmob=(MOB)P;
				final Room R=wmob.location();
				if((R!=null)
				&&(R.getArea()==childA))
				{
					final Room startRoom=wmob.getStartRoom();
					if(startRoom != null)
					{
						if(R.isInhabitant(wmob))
							startRoom.bringMobHere(wmob, true);
						if(wmob.location()!=startRoom)
							wmob.setLocation(startRoom);
					}
				}
			}
			else
			if(P instanceof Item)
			{
				final Item I = (Item)P;
				Room startRoom = returnBoatsToR;
				Room R=CMLib.map().roomLocation(I);
				if(R!=null)
				{
					if((startRoom == null)
					&&(I instanceof NavigableItem))
					{
						final Room R1=CMLib.tracking().getRadiantRoomTarget(R, new AllWaterFilters(), new OutOfAreaFilter(childA));
						if(R1!=null)
						{
							returnBoatsToR=R1;
							startRoom=R1;
						}
					}
					if(startRoom == null)
					{
						final Room R1=CMLib.tracking().getRadiantRoomTarget(R, new EmptyFilters(), new OutOfAreaFilter(childA));
						if(R1!=null)
							startRoom=R1;
					}
					if(startRoom != null)
					{
						if(R.isHere(I))
							startRoom.moveItemTo(I);
						R=CMLib.map().roomLocation(I);
						if(R!=startRoom)
							I.setOwner(startRoom);
					}
				}
			}
		}
		final MOB mob=CMClass.getFactoryMOB();
		try
		{
			final CMMsg msg=CMClass.getMsg(mob,null,null,CMMsg.MSG_EXPIRE,null);
			msg.setTarget(childA);
			for(final WeakReference<MOB> m : child.mobs)
			{
				final MOB M=m.get();
				if(M!=null)
				{
					msg.setSource(M);
					M.executeMsg(M, msg);
				}
			}
			msg.setSource(mob);
			final LinkedList<Room> propRooms = new LinkedList<Room>();
			for(final Enumeration<Room> r=childA.getFilledProperMap();r.hasMoreElements();)
				propRooms.add(r.nextElement());
			// msgs only, handles saves and stuff, but ignores grid rooms!!
			for(final Iterator<Room> r=propRooms.iterator();r.hasNext();)
			{
				final Room R=r.next();
				try
				{
					try
					{
						R.clearSky();
						msg.setTarget(R);
						R.executeMsg(mob,msg);
						try
						{
							CMLib.map().emptyRoom(R, null, true);
						}
						catch(final Exception e)
						{
							Log.errOut(e);
						}
					}
					catch(final Exception e)
					{
						Log.errOut(e);
					}
					R.destroy(); // destroys the mobs and items.  the Deadly Thing.
				}
				catch(final Exception e)
				{
					Log.errOut(e);
				}
			}
			propRooms.clear();
			CMLib.map().delArea(childA);
			childA.destroy();
		}
		finally
		{
			mob.destroy();
		}
	}

	@Override
	public void destroy()
	{
		super.destroy();
		if(this.doesManageChildAreas())
		{
			final List<AreaInstanceChild> cs = new ArrayList<AreaInstanceChild>(instanceChildren.size());
			synchronized(instanceChildren)
			{
				for(int i=instanceChildren.size()-1;i>=0;i--)
					cs.add(instanceChildren.get(i));
				instanceChildren.clear();
			}
			for(final AreaInstanceChild I : cs)
			{
				final Area A=I.A;
				try
				{
					flushInstance(I);
				}
				catch(final Exception e)
				{
					Log.errOut(e);
				}
				CMLib.map().delArea(A);
				A.destroy();
			}
		}
	}

	protected boolean doesManageChildAreas()
	{
		if(CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD))
			return false;
		return true;
	}

	@Override
	public boolean tick(final Tickable ticking, final int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if((CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD))
		&&(this.childTimeout > 0)
		&&(--this.childTimeout == 0))
		{
			this.resetInstance(null);
			return false;
		}
		if(!doesManageChildAreas())
			return true;
		if((--childCheckDown)<=0)
		{
			childCheckDown=CMProps.getMillisPerMudHour()/CMProps.getTickMillis();
			final List<AreaInstanceChild> flushThese = new ArrayList<AreaInstanceChild>(1);
			synchronized(instanceChildren)
			{
				for(int i=instanceChildren.size()-1;i>=0;i--)
				{
					final Area childA=instanceChildren.get(i).A;
					if(childA.getAreaState() != Area.State.ACTIVE) // this is the one and only criteria -- if its not active, flush it.
					{
						final AreaInstanceChild child = instanceChildren.remove(i);
						flushThese.add(child);
					}
				}
			}
			for(final AreaInstanceChild inst : flushThese)
				flushInstance(inst);
		}
		return true;
	}

	protected boolean resetInstance(Room returnToRoom)
	{
		if(CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD))
		{
			final Area A=this.getSuperArea();
			if(A instanceof StdThinInstance)
			{
				if(returnToRoom == null)
				{
					Room backupR = null;
					for(final Enumeration<Room> r = getProperMap();r.hasMoreElements();)
					{
						final Room R = r.nextElement();
						if(R == null)
							continue;
						for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
						{
							if(R.getExitInDir(d)!=null)
							{
								final Room nR = R.getRoomInDir(d);
								if((nR != null)
								&& (nR.getArea() != this))
								{
									if (CMLib.flags().canAccess(null, nR))
									{
										returnToRoom = R;
										break;
									}
									else
										backupR = R;
								}
							}
						}
						if(returnToRoom == null)
							returnToRoom = backupR;
					}
				}
				final StdThinInstance parentA=(StdThinInstance)A;
				final List<AreaInstanceChild> resetThese = new LinkedList<AreaInstanceChild>();
				synchronized(instanceChildren)
				{
					for(int i=0;i<parentA.instanceChildren.size();i++)
					{
						if(parentA.instanceChildren.get(i).A==this)
						{
							final AreaInstanceChild child = parentA.instanceChildren.remove(i);
							resetThese.add(child);
						}
					}
				}
				if(resetThese.size()>0)
				{
					for(final AreaInstanceChild child : resetThese)
					{
						final List<WeakReference<MOB>> V=child.mobs;
						for(final WeakReference<MOB> wM : V)
						{
							final MOB M=wM.get();
							if((M!=null)
							&&CMLib.flags().isInTheGame(M,true)
							&&(M.location()!=null)
							&&(M.location().getArea()==this))
							{
								Room goR = returnToRoom;
								if(goR == null)
									goR = M.getStartRoom();
								if(goR == null)
									goR = CMLib.map().getRandomRoom();
								if(M.location() != goR)
								{
									goR.bringMobHere(M, true);
									CMLib.commands().postLook(M, true);
								}
							}
						}
						setAreaState(Area.State.PASSIVE);
						flushInstance(child);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost, msg);
		if(CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD))
		{
			if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
			&&(msg.sourceMessage()!=null)
			&&((msg.sourceMajor()&CMMsg.MASK_MAGIC)==0))
			{
				final String said=CMStrings.getSayFromMessage(msg.sourceMessage());
				if("RESET INSTANCE".equalsIgnoreCase(said))
				{
					Room returnToRoom=null;
					final Room thisRoom=msg.source().location();
					if(thisRoom.getArea()==this)
					{
						for(int d=0;d<Directions.NUM_DIRECTIONS();d++)
						{
							final Room R=thisRoom.getRoomInDir(d);
							if((R!=null)
							&&(R.getArea()!=null)
							&&(R.getArea()!=this))
								returnToRoom=R;
						}
					}
					if(returnToRoom==null)
					{
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),null,null,CMMsg.MSG_OK_ACTION,CMMsg.NO_EFFECT,CMMsg.NO_EFFECT, L("You must be at an entrance to reset the area.")));
						return;
					}
					if(this.resetInstance(returnToRoom))
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),CMMsg.MSG_OK_ACTION,L("The instance has been reset.")));
					else
						msg.addTrailerMsg(CMClass.getMsg(msg.source(),CMMsg.MSG_OK_ACTION,L("The instance failed to reset.")));
				}
			}
			else
			if((msg.sourceMinor()==CMMsg.TYP_QUIT)&&(CMLib.hunt().isHere(msg.source(), this)))
			{
				final MOB mob = msg.source();
				CMLib.tracking().forceRecall(mob, true);
			}
		}
	}

	@Override
	protected AreaIStats getAreaIStats()
	{
		if(!CMProps.getBoolVar(CMProps.Bool.MUDSTARTED))
			return emptyStats;
		final Area parentArea=getSuperArea();
		final String areaName = (parentArea==null)?Name():parentArea.Name();
		AreaIStats statData=(AreaIStats)Resources.getResource("STATS_"+areaName.toUpperCase());
		if(statData!=null)
			return statData;
		synchronized(CMClass.getSync(("STATS_"+areaName)))
		{
			if(parentArea==null)
			{
				statData=super.getAreaIStats();
				if(statData==emptyStats)
				{
					final Iterator<AreaInstanceChild> childE=instanceChildren.iterator();
					int ct=0;
					if(childE.hasNext())
					{
						statData=(AreaIStats)CMClass.getCommon("DefaultAreaIStats");
						for(;childE.hasNext();)
						{
							final Area childA = childE.next().A;
							if(childA.isAreaStatsLoaded())
							{
								ct++;
								for(final Area.Stats stat : Area.Stats.values())
									statData.setStat(stat,statData.getStat(stat)+childA.getIStat(stat));
							}
						}
					}
					if(ct==0)
						return emptyStats;
					for(final Area.Stats stat : Area.Stats.values())
						statData.setStat(stat,statData.getStat(stat)/ct);
				}
				Resources.removeResource("HELP_"+areaName.toUpperCase());
				Resources.submitResource("STATS_"+areaName.toUpperCase(),statData);
			}
			else
			{
				return super.getAreaIStats();
			}
		}
		return statData;
	}

	protected boolean doesManageMobLists()
	{
		return CMath.bset(flags(),Area.FLAG_INSTANCE_PARENT);
	}

	protected Area createRedirectArea(final List<MOB> mobs)
	{
		final StdThinInstance newA=(StdThinInstance)this.copyOf();
		newA.setTimeObj((TimeClock)getTimeObj().copyOf());
		newA.properRooms=new STreeMap<String, Room>(new Area.RoomIDComparator());
		newA.derivedAtmo=getAtmosphere();
		newA.derivedClimate=getClimateType();
		newA.derivedTheme=getTheme();
		newA.parentArea=null;
		newA.properRoomIDSet = null;
		newA.metroRoomIDSet = null;
		newA.blurbFlags=new STreeMap<String,String>();
		newA.setName((++instanceCounter)+"_"+Name());
		newA.flags |= Area.FLAG_INSTANCE_CHILD;
		for(final Enumeration<String> e=getProperRoomnumbers().getRoomIDs();e.hasMoreElements();)
			newA.addProperRoomnumber(newA.convertToMyArea(e.nextElement()));
		CMLib.map().addArea(newA);
		newA.setAreaState(Area.State.ACTIVE); // starts ticking
		final List<WeakReference<MOB>> newMobList = new SVector<WeakReference<MOB>>(mobs.size());
		for(final MOB mob : mobs)
			newMobList.add(new WeakReference<MOB>(mob));
		final AreaInstanceChild child = new AreaInstanceChild(newA,newMobList);
		instanceChildren.add(child);
		return newA;
	}

	@Override
	public boolean isStat(final String code)
	{
		if(!super.isStat(code))
		{
			return (code.equalsIgnoreCase("RESET_INSTANCE")
					||code.equalsIgnoreCase("FLUSH_INSTANCE")
					||code.equalsIgnoreCase("TIMEOUT_INSTANCE"));
		}
		return true;
	}

	@Override
	public void setStat(final String code, final String val)
	{
		if(code.equalsIgnoreCase("RESET_INSTANCE")||code.equalsIgnoreCase("FLUSH_INSTANCE"))
		{
			final Room returnToR = (val.length()>0) ? CMLib.map().getRoom(val) : null;
			this.resetInstance(returnToR);
			return;
		}
		else
		if(code.equalsIgnoreCase("TIMEOUT_INSTANCE"))
		{
			childTimeout = CMath.s_int(val);
			return;
		}
		super.setStat(code, val);
	}

	@Override
	public String getStat(final String code)
	{
		if(code == null)
			return "";
		if(code.equalsIgnoreCase("TIMEOUT_INSTANCE"))
			return ""+childTimeout;
		return super.getStat(code);
	}

	private static String[] inst_codes = null;

	@Override
	public String[] getStatCodes()
	{
		if (inst_codes != null)
			return inst_codes;
		inst_codes = CMParms.combine(super.getStatCodes(), new String[] { "RESET_INSTANCE", "FLUSH_INSTANCE", "TIMEOUT_INSTANCE"});
		return inst_codes;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost, msg))
			return false;
		if(!this.doesManageChildAreas())
			return true;
		if(CMath.bset(flags(),Area.FLAG_INSTANCE_PARENT)
		&&(!CMath.bset(flags(),Area.FLAG_INSTANCE_CHILD)))
			setAreaState(Area.State.PASSIVE);
		if((msg.sourceMinor()==CMMsg.TYP_ENTER)
		&&(msg.target() instanceof Room)
		&&(doesManageMobLists())
		&&(isRoom((Room)msg.target()))
		&&((!CMSecurity.isAllowed(msg.source(),(Room)msg.target(),CMSecurity.SecFlag.CMDAREAS))
			||(!msg.source().isAttributeSet(MOB.Attrib.SYSOPMSGS)))
		&&(((msg.source().getStartRoom()==null)||(msg.source().getStartRoom().getArea()!=this))))
		{
			int myDex=-1;
			Area redirectA = null;
			final Set<MOB> grp = msg.source().getGroupMembers(new HashSet<MOB>());
			if(msg.source().isMonster()
			&&(msg.source().riding() instanceof Boardable))
			{
				final List<MOB> mobSet=new LinkedList<MOB>();
				boolean playerFound=false;
				final Area subA=((Boardable)msg.source().riding()).getArea();
				for(final Enumeration<Room> r=subA.getProperMap();r.hasMoreElements();)
				{
					final Room R=r.nextElement();
					if(R!=null)
					{
						for(final Enumeration<MOB> m=R.inhabitants();m.hasMoreElements();)
						{
							final MOB M=m.nextElement();
							if(M!=null)
							{
								mobSet.add(M);
								playerFound = playerFound || (!M.isMonster());
							}
						}
					}
				}
				if(playerFound)
				{
					grp.addAll(mobSet);
				}
			}
			synchronized(instanceChildren)
			{
				for(int i=0;i<instanceChildren.size();i++)
				{
					final List<WeakReference<MOB>> V=instanceChildren.get(i).mobs;
					for (final WeakReference<MOB> weakReference : V)
					{
						if(msg.source() == weakReference.get())
						{
							myDex=i;
							break;
						}
					}
				}
				for(int i=0;i<instanceChildren.size();i++)
				{
					if(i!=myDex)
					{
						final List<WeakReference<MOB>> V=instanceChildren.get(i).mobs;
						for(int v=V.size()-1;v>=0;v--)
						{
							final WeakReference<MOB> wmob=V.get(v);
							if(wmob==null)
								continue;
							final MOB M=wmob.get();
							if(grp.contains(M))
							{
								if(myDex<0)
								{
									myDex=i;
									break;
								}
								else
								if((CMLib.flags().isInTheGame(M,true))
								&&(M.location().getArea()!=instanceChildren.get(i).A))
								{
									V.remove(wmob);
									instanceChildren.get(myDex).mobs.add(new WeakReference<MOB>(M));
								}
							}
						}
					}
				}
				if(myDex>=0)
					redirectA=instanceChildren.get(myDex).A;
			}
			if(myDex<0)
				redirectA = createRedirectArea(new XVector<MOB>(grp));
			if(redirectA instanceof StdThinInstance)
			{
				Room R=redirectA.getRoom(((StdThinInstance)redirectA).convertToMyArea(CMLib.map().getExtendedRoomID((Room)msg.target())));
				int tries=1000;
				while((R==null)&&((--tries)>0)&&(redirectA.numberOfProperIDedRooms()>0))
				{
					R=redirectA.getRandomProperRoom();
					if(R!=null)
					{
						msg.setTarget(R);
						if((!CMLib.flags().canAccess(msg.source(),R))
						||(CMLib.law().getLandTitle(R)!=null)
						||(!R.okMessage(msg.source(), msg)))
							R=null;
					}
				}
				if(R!=null)
					msg.setTarget(R);
			}
		}
		return true;
	}
}
