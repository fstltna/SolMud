package com.planet_ink.coffee_mud.Libraries.interfaces;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.core.exceptions.CMException;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.ShipDirectional.ShipDir;
import com.planet_ink.coffee_mud.Items.interfaces.Technical.TechCommand;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;

import java.util.*;
/*
   Copyright 2012-2025 Bo Zimmerman

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
 * The Tech library mainly deals with technical items, and making them work.
 * It registers groups of items into circuits according to a tech 'key',
 * and then uses all the items in the circuit to trigger electricity
 * generation and distribution.
 *
 * It also manages the list of tech manufacturers and their stats, as
 * well as the Global Tech Level.
 *
 * @author Bo Zimmerman
 *
 */
public interface TechLibrary extends CMLibrary
{
	/**
	 * Returns the appropriate electronics key for this electronic item,
	 * area, or room.  It represents the electronical currents that tie
	 * technology together.
	 *
	 * @param o the object to inspect
	 * @return the key to use, or null if none can be found
	 */
	public String getElectronicsKey(final CMObject o);

	/**
	 * Unregisters an electronic component that belonged
	 * in a complex circuitry, like a panel or a
	 * generator.
	 *
	 * @param E the electronic component to unregister
	 * @param oldKey the last key registered to this device
	 */
	public void unregisterElectronics(Electronics E, String oldKey);

	/**
	 * Unregisters all electronic components that belonged
	 * in a complex circuitry, like a panel or a
	 * generator, of the given key.
	 *
	 * @param oldKey the last key registered to this device
	 */
	public void unregisterAllElectronics(final String oldKey);

	/**
	 * Registers an electronic component that belongs
	 * in a complex circuitry, like a panel or a
	 * generator
	 *
	 * @param E the electronic component to register
	 * @param oldKey the last key registered to this device
	 * @return the new key assigned to this item (or old key)
	 */
	public String registerElectrics(Electronics E, String oldKey);

	/**
	 * Registers an electronic component that belongs
	 * in a complex circuitry, like a panel or a
	 * generator.  Also allows you to register for various
	 * TechCommand activations in the circuit.
	 *
	 * @param E the electronic component to register
	 * @param oldKey the last key registered to this device
	 * @param listenFors TechCommands to listen for, or null
	 * @return the new key assigned to this item (or old key)
	 */
	public String registerElectrics(final Electronics E, final String oldKey, final TechCommand[] listenFors);

	/**
	 * Returns whether the currents at the given key are still
	 * active.  Some currents go inactive when players leave
	 * the game, or their areas may be suspended by the system.
	 *
	 * @param key the current key
	 * @return true if the area is active
	 */
	public boolean isCurrentActive(final String key);

	/**
	 * Returns a new exclusive list of all the registered electronics
	 * keys being processed.
	 * @return the list of keys
	 */
	public List<String> getMakeRegisteredKeys();

	/**
	 * For the given key, return an eclusive list of all the electronics
	 * that belong to that key.
	 *
	 * @param key the key to return electronics for
	 * @return the list of electronic object of that key
	 */
	public List<Electronics> getMakeRegisteredElectronics(String key);

	/**
	 * For objects connected ONLY via their electronics, this will allow
	 * tech messages to be transferred between them.
	 * This is the previewer.
	 *
	 * @see TechLibrary#executeMsg(String, TechCommand, Environmental, CMMsg)
	 *
	 * @param oldKey the electronics key
	 * @param command the tech command in question
	 * @param myHost message host
	 * @param msg the message to send
	 * @return true if the message may be sent
	 */
	public boolean okMessage(final String oldKey, final TechCommand command, final Environmental myHost, final CMMsg msg);

	/**
	 * For objects connected ONLY via their electronics, this will allow
	 * tech messages to be transferred between them.
	 * This method sends the messsage and executes it.
	 *
	 * @see TechLibrary#okMessage(String, TechCommand, Environmental, CMMsg)
	 *
	 * @param oldKey the electronics key
	 * @param command the tech command in question
	 * @param myHost message host
	 * @param msg the message to send
	 */
	public void executeMsg(final String oldKey, final TechCommand command, final Environmental myHost, final CMMsg msg);

	/**
	 * Certain Key Systems may automatically force batteries in their circuit
	 * to activate in order to provide that system with power. This will make
	 * that attempt.
	 *
	 * @param E the key device to seek power
	 * @param key this devices key
	 * @return true if an attempt to give power was made, false otherwise.
	 */
	public boolean seekBatteryPower(final ElecPanel E, final String key);

	/**
	 * Retrieves the default manufacturer for new products
	 * @return the default manufacturer for new products
	 */
	public Manufacturer getDefaultManufacturer();

	/**
	 * Adds and saves a new manufacturer to the list.
	 * @param manufacturer the one to add
	 */
	public void addManufacturer(Manufacturer manufacturer);

	/**
	 * Removes a new manufacturer from the list.
	 * @param manufacturer the one to remove
	 */
	public void delManufacturer(Manufacturer manufacturer);

	/**
	 * Updates a new manufacturer in the list.
	 * @param manufacturer the one to update
	 */
	public void updateManufacturer(Manufacturer manufacturer);

	/**
	 * Retrieves the manufacturer of the given name, or null
	 * if it is not found.  Can not handle RANDOM!!
	 *
	 * @param name the manufacturer to fetch
	 * @return the manufacturer found, or null
	 */
	public Manufacturer getManufacturer(String name);

	/**
	 * Retrieves the manufacturer of the given name, or null
	 * if it is not found.  If random is sent, it will find
	 * a random manufacturer for the given item.
	 *
	 * @param E for random manufacturers, the item to check
	 * @param name the manufacturer to fetch
	 * @return the manufacturer found, or null
	 */
	public Manufacturer getManufacturerOf(Technical E, String name);

	/**
	 * Returns an iterator of manufacturers
	 * @return the set to return
	 */
	public Iterator<Manufacturer> manufacterers();

	/**
	 * Return all the rooms containing computers in the
	 * given circuit key.
	 *
	 * @param key the circuit key
	 * @return an iterator of rooms
	 */
	public Iterator<Room> getComputerRooms(String key);
	/**
	 * Return all the computers in the given circuit key.
	 *
	 * @param key the circuit key
	 * @return an iterator of computers
	 */
	public Iterator<Computer> getComputers(String key);

	/**
	 * Returns a random gaussian-distributed tech level from the
	 * current low tech level bound to bound+10.
	 * @return a random valid tech level;
	 */
	public int getRandomGlobalTechLevel();

	/**
	 * Returns the global tech level
	 * @return the global tech level
	 */
	public int getGlobalTechLevel();

	/**
	 * "Fixes" the tech level of the given item by making sure
	 * it's manufacturer is non-random, and by assigning a random
	 * tech level within the valid range, and modifying its name
	 * and displaytext to reflect the new tech level
	 *
	 * @param I An electronics item that needs fixing
	 * @param newTechLevel the new tech level
	 */
	public void fixItemTechLevel(Technical I, int newTechLevel);

}
