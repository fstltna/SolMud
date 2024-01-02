package com.planet_ink.coffee_mud.core.interfaces;

/*
   Copyright 2022-2024 Bo Zimmerman

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
 * An object that can expire, used almost everywhere
 * @author Bo Zimmerman
 *
 */
public interface Expireable
{
	/**
	 * If this object expires, it should have a timestamp saying when it expires, in real time.
	 * When it expires, a MSG_EXPIRE message will be sent to it.
	 * @see com.planet_ink.coffee_mud.core.interfaces.Environmental#setExpirationDate(long)
	 * @return the time stamp when this thing expires
	 */
	public long expirationDate();

	/**
	 * If this object expires, it should have a timestamp saying when it expires, in real time.
	 * When it expires, a MSG_EXPIRE message will be sent to it.
	 * @see com.planet_ink.coffee_mud.core.interfaces.Environmental#expirationDate()
	 * @param dateTime the time stamp when this thing expires
	 */
	public void setExpirationDate(long dateTime);
}
