package com.planet_ink.coffee_mud.Common.interfaces;
import java.util.*;

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
   Copyright 2011-2024 Bo Zimmerman

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
 * A class defining the characteristics of a clan government,
 * and its membership.
 * @author Bo Zimmerman
 */
public interface ClanGovernment extends Modifiable, CMCommon
{
	/**
	 * Gets the iD.
	 *
	 * @see ClanGovernment#getID()
	 *
	 * @return the iD
	 */
	public int getID();

	/**
	 * Sets the iD.
	 *
	 * @see ClanGovernment#getID()
	 *
	 * @param iD the new iD
	 */
	public void setID(int iD);

	/**
	 * Gets the name.
	 *
	 * @see ClanGovernment#setName(String)
	 *
	 * @return the name
	 */
	public String getName();

	/**
	 * Sets the name.
	 *
	 * @see ClanGovernment#getName()
	 *
	 * @param name the new name
	 */
	public void setName(String name);

	/**
	 * Gets the category.
	 *
	 * @see ClanGovernment#setCategory(String)
	 *
	 * @return the category
	 */
	public String getCategory();

	/**
	 * Sets the category.
	 *
	 * @see ClanGovernment#getCategory()
	 *
	 * @param category the new category
	 */
	public void setCategory(String category);

	/**
	 * Gets the auto role.
	 *
	 * @see ClanGovernment#setAutoRole(int)
	 *
	 * @return the auto role
	 */
	public int getAutoRole();

	/**
	 * Sets the auto role.
	 *
	 * @see ClanGovernment#getAutoRole()
	 *
	 * @param autoRole the new auto role
	 */
	public void setAutoRole(int autoRole);

	/**
	 * Gets the accept pos.
	 *
	 * @see ClanGovernment#setAcceptPos(int)
	 *
	 * @return the accept pos
	 */
	public int getAcceptPos();

	/**
	 * Sets the accept pos.
	 *
	 * @see ClanGovernment#getAcceptPos()
	 *
	 * @param acceptPos the new accept pos
	 */
	public void setAcceptPos(int acceptPos);

	/**
	 * Gets the short desc.
	 *
	 * @see ClanGovernment#setShortDesc(String)
	 *
	 * @return the short desc
	 */
	public String getShortDesc();

	/**
	 * Sets the short desc.
	 *
	 * @see ClanGovernment#getShortDesc()
	 *
	 * @param shortDesc the new short desc
	 */
	public void setShortDesc(String shortDesc);

	/**
	 * Gets the long desc.
	 *
	 * @see ClanGovernment#setLongDesc(String)
	 *
	 * @return the long desc
	 */
	public String getLongDesc();

	/**
	 * Sets the long desc.
	 *
	 * @see ClanGovernment#getLongDesc()
	 *
	 * @param longDesc the new long desc
	 */
	public void setLongDesc(String longDesc);

	/**
	 * Gets the required mask str.
	 *
	 * @see ClanGovernment#setRequiredMaskStr(String)
	 *
	 * @return the required mask str
	 */
	public String getRequiredMaskStr();

	/**
	 * Sets the required mask str.
	 *
	 * @see ClanGovernment#getRequiredMaskStr()
	 *
	 * @param requiredMaskStr the new required mask str
	 */
	public void setRequiredMaskStr(String requiredMaskStr);

	/**
	 * Gets the Scriptable parm when joining/creating a clan.
	 *
	 * @see ClanGovernment#setEntryScript(String)
	 *
	 * @return the script for joining/creating a clan
	 */
	public String getEntryScript();

	/**
	 * Sets the Scriptable parm when joining/creating a clan.
	 *
	 * @see ClanGovernment#getEntryScript()
	 *
	 * @param scriptParm the Scriptable parm
	 */
	public void setEntryScript(String scriptParm);

	/**
	 * Gets the Scriptable parm when resigning/exiling a clan.
	 *
	 * @see ClanGovernment#setExitScript(String)
	 *
	 * @return the script for resigning/exiling a clan
	 */
	public String getExitScript();

	/**
	 * Sets the Scriptable parm when resigning/exiling a clan.
	 *
	 * @see ClanGovernment#getExitScript()
	 *
	 * @param scriptParm the Scriptable parm
	 */
	public void setExitScript(String scriptParm);

	/**
	 * Checks if is public.
	 *
	 * @see ClanGovernment#setPublic(boolean)
	 *
	 * @return true, if is public
	 */
	public boolean isPublic();

	/**
	 * Sets the public.
	 *
	 * @see ClanGovernment#isPublic()
	 *
	 * @param isPublic the new public
	 */
	public void setPublic(boolean isPublic);

	/**
	 * Checks if is family only.
	 *
	 * @see ClanGovernment#setFamilyOnly(boolean)
	 *
	 * @return true, if is family only
	 */
	public boolean isFamilyOnly();

	/**
	 * Sets the family only.
	 *
	 * @see ClanGovernment#isFamilyOnly()
	 *
	 * @param isFamilyOnly the new family only
	 */
	public void setFamilyOnly(boolean isFamilyOnly);

	/**
	 * Returns a list of titles granted to players of clans
	 * in this government.  Variables in the titles include:
	 * {@literal @}x1 The Clan Name
	 * {@literal @}x2 The Clan Government Name
	 *
	 * This list is manipulable.
	 *
	 * @return the clan base titles
	 */
	public List<String> getTitleAwards();

	/**
	 * Returns true if this clan is rivalrous with other rivalrous clans,
	 * meaning that pvp is enabled between them, and war can be declared
	 * between them.
	 *
	 * @see ClanGovernment#setRivalrous(boolean)
	 *
	 * @return true or false
	 */
	public boolean isRivalrous();

	/**
	 * Set to true if this clan is rivalrous with other rivalrous clans,
	 * meaning that pvp is enabled between them, and war can be declared
	 *
	 * @see ClanGovernment#isRivalrous()
	 *
	 * @param isRivalrous true or false
	 */
	public void setRivalrous(boolean isRivalrous);

	/**
	 * Gets the override min members.
	 *
	 * @see ClanGovernment#setOverrideMinMembers(Integer)
	 *
	 * @return the override min members
	 */
	public Integer getOverrideMinMembers();

	/**
	 * Sets the override min members.
	 *
	 * @see ClanGovernment#getOverrideMinMembers()
	 *
	 * @param overrideMinMembers the new override min members
	 */
	public void setOverrideMinMembers(Integer overrideMinMembers);

	/**
	 * Checks if is conquest enabled.
	 *
	 * @see ClanGovernment#setConquestEnabled(boolean)
	 *
	 * @return true, if is conquest enabled
	 */
	public boolean isConquestEnabled();

	/**
	 * Sets the conquest enabled.
	 *
	 * @see ClanGovernment#isConquestEnabled()
	 *
	 * @param conquestEnabled the new conquest enabled
	 */
	public void setConquestEnabled(boolean conquestEnabled);

	/**
	 * Checks if is conquest item loyalty.
	 *
	 * @see ClanGovernment#setConquestItemLoyalty(boolean)
	 *
	 * @return true, if is conquest item loyalty
	 */
	public boolean isConquestItemLoyalty();

	/**
	 * Sets the conquest item loyalty.
	 *
	 * @see ClanGovernment#isConquestItemLoyalty()
	 *
	 * @param conquestItemLoyalty the new conquest item loyalty
	 */
	public void setConquestItemLoyalty(boolean conquestItemLoyalty);

	/**
	 * Checks if is conquest by worship.
	 *
	 * @see ClanGovernment#setConquestByWorship(boolean)
	 *
	 * @return true, if is conquest by worship
	 */
	public boolean isConquestByWorship();

	/**
	 * Sets the conquest by worship.
	 *
	 * @see ClanGovernment#isConquestByWorship()
	 *
	 * @param conquestByWorship the new conquest by worship
	 */
	public void setConquestByWorship(boolean conquestByWorship);

	/**
	 * Gets the max vote days.
	 *
	 * @see ClanGovernment#setMaxVoteDays(int)
	 *
	 * @return the max vote days
	 */
	public int getMaxVoteDays();

	/**
	 * Sets the max vote days.
	 *
	 * @see ClanGovernment#getMaxVoteDays()
	 *
	 * @param maxVoteDays the new max vote days
	 */
	public void setMaxVoteDays(int maxVoteDays);

	/**
	 * Gets the vote quorum pct.
	 *
	 * @see ClanGovernment#setVoteQuorumPct(int)
	 *
	 * @return the vote quorum pct
	 */
	public int getVoteQuorumPct();

	/**
	 * Sets the vote quorum pct.
	 *
	 * @see ClanGovernment#getVoteQuorumPct()
	 *
	 * @param voteQuorumPct the new vote quorum pct
	 */
	public void setVoteQuorumPct(int voteQuorumPct);

	/**
	 * Returns a miscellaneous variable setting configuration string
	 * to be applied to new clans created with this government.
	 * The format is VAR1="VALUE" VAR2="VALUE", etc..
	 *
	 * @see ClanGovernment#setMiscVariableSettings(String)
	 *
	 * @return the coded var string
	 */
	public String getMiscVariableSettings();

	/**
	 * Sets a miscellaneous variable setting configuration string
	 * to be applied to new clans created with this government.
	 * The format is VAR1="VALUE" VAR2="VALUE", etc..
	 *
	 * @see ClanGovernment#getMiscVariableSettings()
	 *
	 * @param vars the coded var string
	 */
	public void setMiscVariableSettings(String vars);

	/**
	 * Gets the xp calculation formula.
	 *
	 * @see ClanGovernment#setXpCalculationFormulaStr(String)
	 * @see ClanGovernment#getXPCalculationFormula()
	 *
	 * @return the xp calculation formula
	 */
	public String getXpCalculationFormulaStr();

	/**
	 * Sets the xp calculation formula.
	 *
	 * @see ClanGovernment#setXpCalculationFormulaStr(String)
	 * @see ClanGovernment#getXPCalculationFormula()
	 *
	 * @param xpCalculationFormulaStr the new xp calculation formula
	 */
	public void setXpCalculationFormulaStr(String xpCalculationFormulaStr);

	/**
	 * Returns the compiled xp calculation formula
	 *
	 * @see ClanGovernment#setXpCalculationFormulaStr(String)
	 * @see ClanGovernment#getXpCalculationFormulaStr()
	 *
	 * @return the compiled xp calculation formula
	 */
	public CMath.CompiledFormula getXPCalculationFormula();

	/**
	 * Checks if is default.
	 *
	 * @see ClanGovernment#setDefault(boolean)
	 *
	 * @return true, if is default
	 */
	public boolean isDefault();

	/**
	 * Sets the default.
	 *
	 * @see ClanGovernment#isDefault()
	 *
	 * @param isDefault the new default
	 */
	public void setDefault(boolean isDefault);

	/**
	 * Gets the positions.
	 *
	 * @see ClanGovernment#setPositions(ClanPosition[])
	 *
	 * @return the positions
	 */
	public ClanPosition[] getPositions();

	/**
	 * Sets the positions.
	 *
	 * @see ClanGovernment#getPositions()
	 *
	 * @param positions the new positions
	 */
	public void setPositions(ClanPosition[] positions);

	/**
	 * Gets the auto promote by.
	 *
	 * @see ClanGovernment#setAutoPromoteBy(com.planet_ink.coffee_mud.Common.interfaces.Clan.AutoPromoteFlag)
	 *
	 * @return the auto promote by
	 */
	public Clan.AutoPromoteFlag getAutoPromoteBy();

	/**
	 * Sets the auto promote by.
	 *
	 * @see ClanGovernment#getAutoPromoteBy()
	 *
	 * @param autoPromoteBy the new auto promote by
	 */
	public void setAutoPromoteBy(Clan.AutoPromoteFlag autoPromoteBy);

	/**
	 * Gets the level progression.
	 *
	 * @see ClanGovernment#setLevelProgression(int[])
	 *
	 * @return the level progression
	 */
	public int[] getLevelProgression();

	/**
	 * Sets the level progression.
	 *
	 * @see ClanGovernment#getLevelProgression()
	 *
	 * @param levelProgression the new level progression
	 */
	public void setLevelProgression(int[] levelProgression);

	/**
	 * Gets the help str.
	 *
	 * @return the help str
	 */
	public String getHelpStr();

	/**
	 * Adds the position.
	 *
	 * @see ClanGovernment#delPosition(ClanPosition)
	 * @see ClanGovernment#getPosition(String)
	 * @see ClanGovernment#findPositionRole(Integer)
	 * @see ClanGovernment#findPositionRole(String)
	 *
	 * @return the clan position
	 */
	public ClanPosition addPosition();

	/**
	 * Del position.
	 *
	 * @see ClanGovernment#addPosition()
	 * @see ClanGovernment#getPosition(String)
	 * @see ClanGovernment#findPositionRole(Integer)
	 * @see ClanGovernment#findPositionRole(String)
	 *
	 * @param pos the pos
	 */
	public void delPosition(ClanPosition pos);

	/**
	 * Gets the position.
	 *
	 * @see ClanGovernment#delPosition(ClanPosition)
	 * @see ClanGovernment#addPosition()
	 * @see ClanGovernment#findPositionRole(Integer)
	 * @see ClanGovernment#findPositionRole(String)
	 *
	 * @param pos the pos
	 * @return the position
	 */
	public ClanPosition getPosition(String pos);

	/**
	 * returns clan position by the given role id
	 *
	 * @see ClanGovernment#delPosition(ClanPosition)
	 * @see ClanGovernment#addPosition()
	 * @see ClanGovernment#getPosition(String)
	 * @see ClanGovernment#findPositionRole(String)
	 *
	 * @param roleID the role id to look for
	 * @return the position role id goes to
	 */
	public ClanPosition findPositionRole(Integer roleID);

	/**
	 * returns clan position based on role id, or some part
	 * of its id or name.
	 *
	 * @see ClanGovernment#delPosition(ClanPosition)
	 * @see ClanGovernment#addPosition()
	 * @see ClanGovernment#getPosition(String)
	 * @see ClanGovernment#findPositionRole(Integer)
	 *
	 * @param pos the role id, name, or id
	 * @return the clan position the pos goes to
	 */
	public ClanPosition findPositionRole(String pos);

	/**
	 * Return the list of abilities owned by someone
	 * who is part of a clan of the given level.
	 *
	 * @param mob the mob enabled
	 * @param clan the clan the abilities come from
	 * @param level clan level
	 * @return list of abilities
	 */
	public SearchIDList<Ability> getClanLevelAbilities(MOB mob, Clan clan, Integer level);

	/**
	 * Return the list of effects owned by someone
	 * who is part of a clan of the given level.
	 *
	 * @param mob the mob affected
	 * @param clan the clan the effects come from
	 * @param level clan level
	 * @return list of abilities
	 */
	public ChameleonList<Ability> getClanLevelEffects(MOB mob, Clan clan, Integer level);
}
