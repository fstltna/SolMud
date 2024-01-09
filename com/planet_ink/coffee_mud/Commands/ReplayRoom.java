package com.planet_ink.coffee_mud.Commands;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;
import com.planet_ink.coffee_mud.Locales.interfaces.Room;
import com.planet_ink.coffee_mud.Locales.interfaces.RoomHistoryEntry;
import com.planet_ink.coffee_mud.MOBS.interfaces.MOB;
import com.planet_ink.coffee_mud.core.CMLib;
import com.planet_ink.coffee_mud.core.CMParms;
import com.planet_ink.coffee_mud.core.CMath;

public class ReplayRoom extends StdCommand {
	public ReplayRoom() {
	}

	private String[] access = { "REPLAYROOM" };

	@Override
	public String[] getAccessWords() {
		return access;
	}

	@Override
	public boolean execute(final MOB mob, final List<String> commands, final int metaFlags) throws java.io.IOException {
		if ((!mob.isMonster()) && (commands.size() > 1) && (CMath.isNumber(commands.get(1)))
				&& (mob.playerStats() != null)) {

			boolean oldFlag = false;
			boolean showDetails = false;
			boolean showOOC = true;
			boolean showIC = true;

			for (int idx = commands.size() - 1; idx > 0; idx--) {
				String command = commands.get(idx);
				if (command.equalsIgnoreCase("DETAILS")) {
					showDetails = true;
					commands.remove(idx);
				}
			}


			Room room = mob.location();
			if ((room != null) && (commands.size() > 0)) {
				if (!CMath.isInteger(CMParms.combine(commands, 1))){
					if (commands.get(1).equalsIgnoreCase("RESET")) {
						room.clearRoomHistory();
						mob.tell("Room history cleared.");
						return true;
					}
				}

				int num = CMath.s_int(CMParms.combine(commands, 1));
				if ((num > 50))
					num = 50;
				List<RoomHistoryEntry> V = room.getRoomHistory(mob);
				if (V.size() == 0) {
					mob.tell("No activity in this room.");
					return true;
				}

				List<RoomHistoryEntry> messages = new ArrayList<RoomHistoryEntry>();

				for (int v = V.size() - 1; v >= 0; v--) {
					RoomHistoryEntry entry = V.get(v);

					if (mob == entry.message.source()) {
						messages.add(0, entry);
					} else if (mob == entry.message.target()) {
						messages.add(0, entry);
					} else {
						messages.add(0, entry);
					}

					if (messages.size() >= num)
						break;
				}
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
//				DateFormat todaydf = new SimpleDateFormat("HH:mm:ss");
				for (RoomHistoryEntry entry : messages) {
					CMMsg msg = entry.message;

					long elapsedTime = System.currentTimeMillis() - entry.dtWhen.getTime();

					String timeAgo; // = "^.^N ("+CMLib.time().date2SmartEllapsedTime(elapsedTime,true)+" ago)";
					if (showDetails) {
						final String originator = "^.^N[" + df.format(entry.dtWhen.getTime()) + " "
								+ msg.source().Name() + "]";
						mob.tell(originator);
						timeAgo = "";
					} else {
						timeAgo = "^.^N (" + CMLib.time().date2SmartEllapsedTime(elapsedTime, true) + " ago)";
					}
				}

			} else {
				mob.tell("Replayroom what?");
				return false;
			}
		}
		return false;
	}
	//tried to un-deprecate shit
	boolean isBeforeToday(Date d) {
		Date today = new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		return d.before(today);
	}

	boolean isAfterToday(Date d) {
		Date today = new Date();
		today.setHours(0);
		today.setMinutes(0);
		today.setSeconds(0);
		return d.after(today);
	}

	@Override
	public boolean canBeOrdered() {
		return false;
	}
}
