package com.planet_ink.coffee_mud.Items.interfaces;
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
/**
 * Electronics are items that can be turned on and off before their
 * interesting behavior or ability is available, which requires "power"
 * to be operated, and has some capacitance for power that keeps it
 * running for some dramatic amount of time while it is no longer
 * receiving new power.  Electronics have manufacturers who make them,
 * and can be of many different sorts.
 * @author Bo Zimmerman
 *
 */
public interface Electronics extends Item, Technical
{
	/**
	 * Gets the maximum amount of capacitance supported by
	 * this electrical item.  This is the maximum power the item
	 * can store up and utilize before it shuts off. Only the
	 * item itself knows how much it wants to use at any particular
	 * time.
	 * @see Electronics#setPowerCapacity(long)
	 * @return the maximum amount of stored power capacity
	 */
	public long powerCapacity();

	/**
	 * Sets the maximum amount of capacitance supported by
	 * this electrical item.  This is the maximum power the item
	 * can store up and utilize before it shuts off. Only the
	 * item itself knows how much it wants to use at any particular
	 * time.
	 * @see Electronics#powerCapacity()
	 * @param capacity the maximum amount of stored power capacity
	 */
	public void setPowerCapacity(long capacity);

	/**
	 * Gets the amount of power capacitance remaining in this
	 * electrical item.  The item will continue to use this
	 * power until it doesn't have enough to operate, and
	 * then turn off.
	 * @see Electronics#setPowerRemaining(long)
	 * @return the amount of power capacitance remaining
	 */
	public long powerRemaining();

	/**
	 * Sets the amount of power capacitance remaining in this
	 * electrical item.  The item will continue to use this
	 * power until it doesn't have enough to operate, and
	 * then turn off.
	 * @see Electronics#powerRemaining()
	 * @param remaining the amount of power capacitance remaining
	 */
	public void setPowerRemaining(long remaining);

	/**
	 * Gets the amount of power capacitance to allow this
	 * electrical item to charge up to.  It should always
	 * be less than powerCapacity.
	 *
	 * @see Electronics#setPowerTarget(long)
	 * @return the amount of power capacitance max to draw
	 */
	public long powerTarget();

	/**
	 * Sets the amount of power capacitance to allow this
	 * electrical item to charge up to.  It should always
	 * be less than powerCapacity.
	 *
	 * @see Electronics#powerTarget()
	 * @param remaining the amount of power capacitance remaining
	 */
	public void setPowerTarget(long remaining);

	/**
	 * Returns the immediate power needs of this electrical item.
	 * Typically powerCapacity - powerAvailable
	 * @see Electronics#powerRemaining()
	 * @see Electronics#powerCapacity()
	 * @return the amount of power this item can still absorb
	 */
	public int powerNeeds();

	/**
	 * Gets whether this electrical item is "turned on".
	 * An activated item can do the stuff it is supposed to,
	 * but off it cannot.
	 * @see Electronics#activate(boolean)
	 * @return whether this electrical item is "turned on"
	 */
	public boolean activated();

	/**
	 * Sets whether this electrical item is "turned on".
	 * An activated item can do the stuff it is supposed to,
	 * but off it cannot.
	 * @see Electronics#activated()
	 * @param truefalse true to activate, false to deactivate
	 */
	public void activate(boolean truefalse);
}
