package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_web.http.HTTPException;
import com.planet_ink.coffee_web.http.HTTPHeader;
import com.planet_ink.coffee_web.http.HTTPStatus;
import com.planet_ink.coffee_web.interfaces.*;
import com.planet_ink.coffee_web.util.CWDataBuffers;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.CMSecurity.DisFlag;
import com.planet_ink.coffee_mud.core.CoffeeIOPipe.CoffeeIOPipes;
import com.planet_ink.coffee_mud.core.CoffeeIOPipe.CoffeePipeSocket;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.DatabaseEngine.PlayerData;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.Session.SessionStatus;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import com.planet_ink.coffee_mud.WebMacros.WebSock.WSPType;

import java.io.*;
import java.lang.ref.Reference;
import java.util.*;

/*
   Copyright 2025-2025 Bo Zimmerman

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
public class WebSockJSON extends WebSock
{
	@Override
	public String ID()
	{
		return "WebSockJSON";
	}

	@Override
	public String name()
	{
		return "WebSockJSON";
	}

	public WebSockJSON()
	{
		super();
	}

	public WebSockJSON(final HTTPRequest httpReq) throws IOException
	{
		super(httpReq);
	}

	protected volatile boolean	sessionInit	= false;
	protected boolean linefeeds = true;
	protected boolean ascii = false;
	protected boolean telnet = true;

	private enum WSConfig
	{
		LINEFEEDS,
		ASCIIONLY,
		TELNET,
		MXP,
		MSP,
		MSDP,
		GMCP
	}

	private enum WJSONType
	{
		CONNECT,
		INPUT,
		LOGIN,
		TEXT,
		ERROR,
		LOGOUT,
		CONFIG
	}

	@Override
	protected Pair<byte[],WSPType> processPolledBytes(byte[] data)
	{
		if(!telnet)
			data = CMLib.protocol().stripTelnet(data);
		if(ascii)
		{
			data = CMLib.protocol().stripNonASCII(
					CMLib.protocol().stripANSI(data));
		}
		if(!linefeeds)
			data = CMLib.protocol().stripLF(data);
		final MiniJSON.JSONObject json = new MiniJSON.JSONObject();
		json.put("type", WJSONType.TEXT.name().toLowerCase());
		json.put("text", data);
		return new Pair<byte[],WSPType>(json.toString().getBytes(), WSPType.TEXT);
	}

	protected void sendSimplePacket(final MiniJSON.JSONObject json)
	{
		try
		{
			super.sendPacket(json.toString().getBytes(), WSPType.TEXT);
		}
		catch (final IOException e)
		{
		}
	}

	protected void sendSimplePacket(final String type, final String data)
	{
		final MiniJSON.JSONObject json = new MiniJSON.JSONObject();
		json.put("type", type);
		if((data != null) && (data.length() > 0))
		{
			for(final String pair : CMParms.parseCommas(data, true))
			{
				final int x = pair.indexOf('=');
				if(x > 0)
					json.put(pair.substring(0,x),pair.substring(x+1));
			}
		}
		sendSimplePacket(json);
	}

	protected Object getConfig(final WSConfig conf)
	{
		switch(conf)
		{
		case ASCIIONLY:
			return Boolean.valueOf(ascii);
		case LINEFEEDS:
			return Boolean.valueOf(linefeeds);
		case MSP:
			if((sess == null)||(sess[0] == null))
				return MiniJSON.NULL;
			return Boolean.valueOf(sess[0].getClientTelnetMode(Session.TELNET_MSP));
		case MSDP:
			if((sess == null)||(sess[0] == null))
				return MiniJSON.NULL;
			return Boolean.valueOf(sess[0].getClientTelnetMode(Session.TELNET_MSDP));
		case MXP:
			if((sess == null)||(sess[0] == null))
				return MiniJSON.NULL;
			return Boolean.valueOf(sess[0].getClientTelnetMode(Session.TELNET_MXP));
		case GMCP:
			if((sess == null)||(sess[0] == null))
				return MiniJSON.NULL;
			return Boolean.valueOf(sess[0].getClientTelnetMode(Session.TELNET_GMCP));
		case TELNET:
			return Boolean.valueOf(telnet);
		}
		return MiniJSON.NULL;
	}

	protected void setConfig(final WSConfig conf, final Object value)
	{
		switch(conf)
		{
		case ASCIIONLY:
			ascii = (value == null) ? ascii : CMath.s_bool(value.toString());
			break;
		case GMCP:
			if((sess != null) && (sess[0] != null))
			{
				final boolean onoff = CMath.s_bool(value.toString());
				sess[0].setClientTelnetMode(Session.TELNET_GMCP, onoff);
				sess[0].setServerTelnetMode(Session.TELNET_GMCP, onoff);
			}
			break;
		case LINEFEEDS:
			linefeeds = (value == null) ? linefeeds : CMath.s_bool(value.toString());
			break;
		case MSDP:
			if((sess != null) && (sess[0] != null))
			{
				final boolean onoff = CMath.s_bool(value.toString());
				sess[0].setClientTelnetMode(Session.TELNET_MSDP, onoff);
				sess[0].setServerTelnetMode(Session.TELNET_MSDP, onoff);
			}
			break;
		case MSP:
			if((sess != null) && (sess[0] != null))
			{
				final boolean onoff = CMath.s_bool(value.toString());
				sess[0].setClientTelnetMode(Session.TELNET_MSP, onoff);
				sess[0].setServerTelnetMode(Session.TELNET_MSP, onoff);
			}
			break;
		case MXP:
			if((sess != null) && (sess[0] != null))
			{
				final boolean onoff = CMath.s_bool(value.toString());
				sess[0].setClientTelnetMode(Session.TELNET_MXP, onoff);
				sess[0].setServerTelnetMode(Session.TELNET_MXP, onoff);
			}
			break;
		case TELNET:
			telnet = (value == null) ? telnet : CMath.s_bool(value.toString());
			break;
		}
	}

	@Override
	protected void poll()
	{
		synchronized(sess)
		{
			if(!sessionInit)
			{
				sessionInit = (sess[0] != null);
				if(sessionInit)
					sendSimplePacket(WJSONType.CONNECT.name().toLowerCase(),"text=");
			}
		}
		super.poll();
	}

	private void config(final MiniJSON.JSONObject obj) throws IOException, MiniJSON.MJSONException
	{
		if(obj.containsKey("key"))
			throw new MiniJSON.MJSONException("Invalid JSON: Missing key.");
		final String key = obj.getCheckedString("key");
		final WSConfig conf = (WSConfig)CMath.s_valueOf(WSConfig.class, key.toUpperCase().trim());
		if(conf == null)
			throw new MiniJSON.MJSONException("Invalid JSON: Invalid key.");
		if(!obj.containsKey("value"))
		{
			final MiniJSON.JSONObject json = new MiniJSON.JSONObject();
			json.put("type", WJSONType.CONFIG.name().toLowerCase());
			json.put("key", conf.name());
			json.put("value", getConfig(conf));
			sendSimplePacket(json);
		}
		else
		{
			this.setConfig(conf, obj.get("value"));
			obj.remove("value");
			config(obj); // respond with the already set value
		}
	}

	private void logout(final MiniJSON.JSONObject obj) throws IOException, MiniJSON.MJSONException
	{
		final Session sess = this.sess[0];
		if(sess==null)
			throw new MiniJSON.MJSONException("Not yet connected.");
		if(sess.mob()==null)
			throw new MiniJSON.MJSONException("Not logged in.");
		final CMMsg msg=CMClass.getMsg(sess.mob(),null,CMMsg.MSG_QUIT,null);
		final Room R=sess.mob().location();
		if(R != null)
		{
			if(R.okMessage(sess.mob(),msg))
			{
				CMLib.map().sendGlobalMessage(sess.mob(),CMMsg.TYP_QUIT, msg);
				sess.logout(true); // this should call prelogout and later loginlogoutthread to cause msg SEND
				CMLib.commands().monitorGlobalMessage(R, msg);
			}
		}
		sess.setMob(null);
		sess.setAccount(null);
	}
	private void login(final MiniJSON.JSONObject obj) throws IOException, MiniJSON.MJSONException
	{
		final Session sess = this.sess[0];
		if(sess==null)
			throw new MiniJSON.MJSONException("Not yet connected.");
		if(sess.mob()!=null)
			throw new MiniJSON.MJSONException("Already logged in.");
		final String name = obj.getCheckedString("name");
		final String pw = obj.getCheckedString("password");
		if(!CMLib.players().playerExists(name))
			throw new MiniJSON.MJSONException("Login failed.");
		MOB target=CMLib.players().getPlayer(name);
		if(target != null)
		{
			if(target.playerStats()==null)
				throw new MiniJSON.MJSONException("Login failed.");
			if(CMLib.flags().isInTheGame(target, true))
				throw new MiniJSON.MJSONException("Already logged in.");
		}
		if(!sess.autoLogin(name, pw))
			throw new MiniJSON.MJSONException("Login failed.");
		target=CMLib.players().getPlayer(name);
		if(target == null)
			throw new MiniJSON.MJSONException("Login failed.");
		sendSimplePacket(WJSONType.CONNECT.name().toLowerCase(),"text=,username="+target.Name());
	}

	private void input(final MiniJSON.JSONObject obj) throws IOException, MiniJSON.MJSONException
	{
		if (obj.containsKey("data"))
		{
			final Object o = obj.get("data");
			if (o instanceof String)
			{
				if (mudOut != null)
					mudOut.write(((String)o).getBytes(CMProps.getVar(CMProps.Str.CHARSETINPUT)));
			}
			else
			if (o instanceof Object[])
			{
				final Object[] inputs = (Object[])o;
				final List<String> inputV = new ArrayList<String>();
				for(final Object o2 : inputs)
				{
					if(o2 instanceof String)
						inputV.add((String)o2);
				}
				final String finalInput = CMParms.combineQuoted(inputV,  0).trim() + "\n";
				if (mudOut != null)
					mudOut.write(finalInput.getBytes(CMProps.getVar(CMProps.Str.CHARSETINPUT)));
			}
		}
	}

	@Override
	protected Pair<byte[], WSPType> processTextInput(final String input) throws IOException
	{
		if(mudOut == null)
			return null;
		try
		{
			final MiniJSON.JSONObject obj = new MiniJSON().parseObject(input);
			if(!obj.containsKey("type"))
				throw new MiniJSON.MJSONException("Invalid JSON: Missing TYPE.");
			final String typeStr = obj.getCheckedString("type").toUpperCase();
			final WJSONType type = (WJSONType)CMath.s_valueOf(WJSONType.class,typeStr);
			if(type == null)
				throw new MiniJSON.MJSONException("Invalid JSON: Unknown TYPE '"+typeStr+"'.");
			switch(type)
			{
			case CONNECT:
				throw new MiniJSON.MJSONException("CONNECT is a server-only command.");
			case TEXT:
				throw new MiniJSON.MJSONException("TEXT is a server-only command.");
			case INPUT:
				input(obj);
				break;
			case LOGIN:
				login(obj);
				break;
			case LOGOUT:
				logout(obj);
				break;
			case ERROR:
				throw new MiniJSON.MJSONException("ERROR is a server-only command.");
			case CONFIG:
				config(obj);
				break;
			}
		}
		catch(final MiniJSON.MJSONException x)
		{
			final MiniJSON.JSONObject json = new MiniJSON.JSONObject();
			json.put("type", WJSONType.ERROR.name().toLowerCase());
			json.put("text", x.getMessage().getBytes());
			return new Pair<byte[],WSPType>(json.toString().getBytes(), WSPType.TEXT);
			// ignore
		}
		return null;
	}

	@Override
	protected Pair<byte[], WSPType> processBinaryInput(final byte[] input) throws IOException
	{
		// ignore
		final MiniJSON.JSONObject json = new MiniJSON.JSONObject();
		json.put("type", WJSONType.ERROR.name().toLowerCase());
		json.put("text", "Binary input not accepted.  Send valid JSON");
		return new Pair<byte[],WSPType>(json.toString().getBytes(), WSPType.TEXT);
	}

}
