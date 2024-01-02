package com.planet_ink.coffee_mud.Items.interfaces;

import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.core.exceptions.*;
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
import com.planet_ink.coffee_mud.Libraries.interfaces.*;

import java.util.*;

/*
   Copyright 2016-2024 Bo Zimmerman

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
/**
 * A Sailing Ship or Caravan, which is an object that's boardable, rooms are attached
 * to it so you can get in, and its an item that can appear in rooms, have
 * speed and direction in real rooms, and can be in combat, etc.
 * @author Bo Zimmerman
 *
 */
public interface NavigableItem extends Boardable, Item, Rideable, SiegableItem
{
	public static final int COURSE_STEER_MASK = 256;

	/**
	 * Returns which direction the ship is currently facing.
	 * @return the direction the ship is facing.
	 */
	public int getDirectionFacing();

	/**
	 * Sets which direction the ship is currently facing.
	 * @param dir the direction the ship is facing.
	 */
	public void setDirectionFacing(int dir);

	/**
	 * Returns whether the anchor is down, thus holding the ship in place.
	 * @return true if the anchor is down, holding the ship in place.
	 */
	public boolean isAnchorDown();

	/**
	 * Returns this ships max speed, typically &gt;= 1
	 * @return this ships max speed, typically &gt;= 1
	 */
	public int getMaxSpeed();
	/**
	 * Sets whether the anchor is down, thus holding the ship in place.
	 * @param truefalse true if the anchor is down, false if the anchor is up
	 */
	public void setAnchorDown(boolean truefalse);

	/**
	 * Returns the future course of this ship.  A stop-course direction is
	 * always -1, so it is typically the last entry.  Otherwise, each entry
	 * is a compass direction, possibly masked by COURSE_STEER_MASK in order
	 * to specify that it is a TURN ONLY.  Directions not marked as turns
	 * are automatic movements in that direction.
	 * @see NavigableItem#setCurrentCourse(List)
	 * @return the future course of this ship.
	 */
	public List<Integer> getCurrentCourse();

	/**
	 * Sets the future course of this ship.  A stop-course direction is
	 * always -1, so it is typically the last entry.  Otherwise, each entry
	 * is a compass direction, possibly masked by COURSE_STEER_MASK in order
	 * to specify that it is a TURN ONLY.  Directions not marked as turns
	 * are automatic movements in that direction.
	 * @see NavigableItem#getCurrentCourse()
	 * @param course the new course to set.
	 */
	public void setCurrentCourse(List<Integer> course);

	/**
	 * The type of navigable object this is.
	 * @see Rideable.Basis
	 * @return the Basis* enum describing how this is navigated
	 */
	public Basis navBasis();

	/**
	 * Returns whether the given mob is allowed to steer the
	 * craft from the given internal room.
	 *
	 * @param mob the mob in the navigable area
	 * @param R the room in the navigable area
	 * @return true if steering can happen
	 */
	public boolean canSteer(final MOB mob, final Room R);

	/**
	 * Forces a normal navigation move from the ships
	 * current location to the location in the given
	 * direction.  This will also do tactical moves.
	 *
	 * @param direction the direction to move
	 * @return true if it worked, false otherwise
	 */
	public boolean navigate(final int direction);
}
