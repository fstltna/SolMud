package com.planet_ink.coffee_mud.Commands;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.CMath.CompiledFormula;
import com.planet_ink.coffee_mud.core.CMath.CompiledOperation;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.core.interfaces.BoundedObject.BoundedCube;
import com.planet_ink.coffee_mud.core.exceptions.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.*;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.ColorLibrary.Color;
import com.planet_ink.coffee_mud.Libraries.interfaces.PlayerLibrary.PlayerCode;
import com.planet_ink.coffee_mud.Locales.interfaces.*;
import com.planet_ink.coffee_mud.MOBS.interfaces.*;
import com.planet_ink.coffee_mud.Races.interfaces.*;
import com.planet_ink.coffee_mud.WebMacros.interfaces.*;
import com.planet_ink.coffee_web.http.HTTPMethod;
import com.planet_ink.coffee_web.http.MultiPartData;
import com.planet_ink.coffee_web.interfaces.HTTPRequest;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/*
* <p>Portions Copyright (c) 2003 Jeremy Vyska</p>
* <p>Portions Copyright (c) 2004-2024 Bo Zimmerman</p>

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
public class Test extends StdCommand
{
	public Test()
	{
	}

	private final String[]	access	= I(new String[] { "TEST" });

	@Override
	public String[] getAccessWords()
	{
		return access;
	}

	public static final String[]	spells			= { "Spell_Blur", "Spell_ResistMagicMissiles" };
	public static String			semiSpellList	= null;

	final Comparator<Object> comp=new Comparator<Object>()
	{
		@Override
		public int compare(final Object o1, final Object o2)
		{
			if(o1 == null)
				return (o2==null)?0:-1;
			if(o2==null)
				return 1;
			if(o1 instanceof Ability)
			{
				if(!(o2 instanceof Ability))
					return -1;
				if((((CMObject)o1).ID().equals(((CMObject)o2).ID()))
				&&(((Ability)o1).text().equals(((Ability)o2).text())))
					return 0;
			}
			else
			if(o1 instanceof Behavior)
			{
				if(!(o2 instanceof Behavior))
					return -1;
				if((((CMObject)o1).ID().equals(((CMObject)o2).ID()))
				&&(((Behavior)o1).getParms().equals(((Behavior)o2).getParms())))
					return 0;
			}
			else
			if(o1 instanceof CMObject)
			{
				if(!(o2 instanceof CMObject))
					return -1;
				if(((CMObject)o1).ID().compareTo(((CMObject)o2).ID())==0)
					return 0;
			}
			else
			if(o1.equals(o2))
				return 0;
			return -1;
		}
	};

	public static String semiSpellList()
	{
		if(semiSpellList!=null)
			return semiSpellList;
		final StringBuffer str=new StringBuffer("");
		for (final String spell : spells)
			str.append(spell+";");
		semiSpellList=str.toString();
		return semiSpellList;
	}

	public static final String[] maliciousspells={"Spell_Blindness","Spell_Mute"};
	public static String maliciousSemiSpellList=null;

	public static String maliciousSemiSpellList()
	{
		if(maliciousSemiSpellList!=null)
			return maliciousSemiSpellList;
		final StringBuffer str=new StringBuffer("");
		for (final String maliciousspell : maliciousspells)
			str.append(maliciousspell+";");
		maliciousSemiSpellList=str.toString();
		return maliciousSemiSpellList;
	}

	public boolean isAllAdjusted(final MOB mob)
	{
		if(mob.phyStats().ability()<10)
			return false;
		if(mob.charStats().getStat(CharStats.STAT_GENDER)!='F')
			return false;
		if(!mob.charStats().getCurrentClass().ID().equals("Fighter"))
			return false;
		if(mob.charStats().getStat(CharStats.STAT_CHARISMA)<18)
			return false;
		if(mob.maxState().getMana()<1000)
			return false;
		return true;
	}

	public boolean isAnyAdjusted(final MOB mob)
	{
		if(mob.phyStats().ability()>=10)
			return true;
		if(mob.charStats().getStat(CharStats.STAT_GENDER)=='F')
			return true;
		if(mob.charStats().getCurrentClass().ID().equals("Fighter"))
			return true;
		if(mob.charStats().getStat(CharStats.STAT_CHARISMA)>=18)
			return true;
		if(mob.maxState().getMana()>=1000)
			return true;
		return false;
	}

	public void giveAbility(final Physical P, final Ability A)
	{
		final Ability A2=((Ability)A.copyOf());
		A2.setMiscText(A.text());
		P.addNonUninvokableEffect(A2);
	}

	private Map<String,List<String>> parseHeaders(final String header)
	{
		final Map<String,List<String>> headers = new HashMap<String,List<String>>();
		for(final String rawHeader : header.split("\r\n"))
		{
			final int x=rawHeader.indexOf(':');
			final String headerKey=rawHeader.substring(0,x).trim().toUpperCase();
			final List<String> headerVals = new ArrayList<String>();
			for(final String s : Arrays.asList(CMLib.coffeeFilter().colorOnlyFilter(rawHeader.substring(x+1),null).trim().split(";")))
				headerVals.add(s.trim());
			headers.put(headerKey, headerVals);
		}
		return headers;
	}

	private String decodeQuotedPrintable(final String str)
	{
		final StringBuilder nstr=new StringBuilder("");
		for(int c=0;c<str.length()-2;c++)
		{
			final char ch=str.charAt(c);
			if(ch=='=')
			{
				final char ch1=Character.toUpperCase(str.charAt(++c));
				final char ch2=Character.toUpperCase(str.charAt(++c));
				final int hex1="0123456789ABCDEF".indexOf(ch1);
				final int hex2="0123456789ABCDEF".indexOf(ch2);
				if((hex1>=0)&&(hex2>=0))
					nstr.append((char)((hex1*16)+hex2));
			}
			else
				nstr.append(ch);
		}
		return nstr.toString();
	}

	private String stripHtmlHeaders(String html)
	{
		if(html.trim().startsWith("&lt;HTML")
		||html.trim().startsWith("&lt;html"))
		{
			html=CMStrings.replaceAll(html, "&lt;", "<");
			html=CMStrings.replaceAll(html, "&gt;", ">");
			html=CMStrings.replaceAll(html, "&quot;", "\"");
			html=CMStrings.replaceAll(html, "&#39;", "'");
		}
		if(html.trim().startsWith("<html")
			||html.trim().startsWith("<HTML"))
		{
			int x=html.lastIndexOf("</body>");
			if(x<0)
				x=html.lastIndexOf("</BODY>");
			if(x>0)
				html=html.substring(0,x);
			x=html.indexOf("<body");
			if(x<0)
				x=html.indexOf("<BODY");
			if(x>0)
			{
				x=html.indexOf(">",x+4);
				html=html.substring(x+1);
			}
		}
		return html;
	}

	@SuppressWarnings("rawtypes")
	public void compareObjectsAndReport(final String rep, final MOB mob, final String var, final Object val1, final Object val2)
	{
		if(val1 == null)
			mob.tell(rep+"-NFAIL: "+var+"="+val1);
		else
		if(val1 instanceof Object[])
		{
			if(!Arrays.deepEquals((Object[])val1, (Object[])val2))
			{
				mob.tell(rep+"-FAIL: "+var+"="+val1);
				mob.tell(rep+"-WAS : "+var+"="+val2);
			}
		}
		else
		if(val1 instanceof List)
		{
			final List l=(List)val1;
			if((l.size()==0)||(((List)val2).size()!=l.size()))
				mob.tell(rep+"-FAIL: "+var+"="+l.size()+"!="+((List)val2).size());
			else
			{
				for(final Object o1 : l)
				{
					boolean found=false;
					for(final Object o2 : ((List)val2))
					{
						if(o1 instanceof Triad)
						{
							final Triad t1=(Triad)o1;
							final Triad t2=(Triad)o2;
							if((comp.compare(t1.second, t2.second)==0)
							&&((comp.compare(t1.third, t2.third)==0)))
								found=true;
						}
						else
						if(o1 instanceof Pair)
						{
							final Pair t1=(Pair)o1;
							final Pair t2=(Pair)o2;
							if((comp.compare(t1.second, t2.second)==0)
							&&((comp.compare(t1.first, t2.first)==0)))
								found=true;
						}
						else
						if(comp.compare(o1, o2)==0)
							found=true;
					}
					if(!found)
					{
						mob.tell(rep+"-FAIL: "+var+"~="+o1);
						break;
					}
				}
			}
		}
		else
		if(!val1.equals(val2))
		{
			mob.tell(rep+"-FAIL: "+var+"="+val1);
			mob.tell(rep+"-WAS : "+var+"="+val2);
		}
	}

	public String spaceMoveError(final String pos, final SpaceShip o, final double[] dir,
								 final double speedDiff, final double speed,
								 final double distDiff, final double traveledDistance, final double distanceTravelled,
								 final long[] oldCoords)
	{
		String complaint="";
		if((!Arrays.equals(dir, o.direction())))
			complaint += " angles: "+
						Math.round(Math.toDegrees(dir[0]))+"mk"+Math.round(Math.toDegrees(dir[1]))
					  +".vs."+Math.round(Math.toDegrees(o.direction()[0]))+"mk"+Math.round(Math.toDegrees(o.direction()[1]));
		if(speedDiff > 1)
			complaint +=  "speed: "+o.speed()+".vs."+speed;
		if(distDiff > 1)
		{
			complaint += "dist: "+traveledDistance+".vs"+distanceTravelled;
			complaint += ", coords: "+CMParms.toListString(oldCoords)+".vs"+CMParms.toListString(o.coordinates());
		}
		return "Error: Space move "+pos+", test failed: "+complaint;
	}

	public String copyYahooGroupMsg(final MOB mob, int lastMsgNum) throws Exception
	{
		long numTimes = 9999999;
		java.io.File dir;
		if(java.io.File.separatorChar=='\\')
			dir=new java.io.File("Z:\\_COFEHAS\\Misc\\yahoo-group");
		else
			dir=new java.io.File("/arc/_COFEHAS/Misc/yahoo-group");
		int numTotal=0;
		{
			final int baseTotal=dir.listFiles().length;
			numTotal = baseTotal;
			java.io.File F=new File(dir,""+numTotal+".json");
			while(!F.exists())
			{
				int diff=(numTotal/100);
				if(diff == 0)
					diff = 1;
				numTotal = numTotal-diff;
				F=new File(dir,""+numTotal+".json");
			}
			while(F.exists())
			{
				numTotal++;
				F=new File(dir,""+numTotal+".json");
				if(!F.exists())
				{
					for(int i=0;i<100;i++)
					{
						F=new File(dir,""+(i+numTotal)+".json");
						if(F.exists())
						{
							numTotal += i;
							break;
						}
					}
				}
			}
			F=new File(dir,""+numTotal+".json");
			while(!F.exists())
			{
				numTotal--;
				F=new File(dir,""+numTotal+".json");
			}
			mob.tell(numTotal+": highest mail file found.");
		}
		while ((--numTimes) >= 0)
		{
			lastMsgNum++;
			if (lastMsgNum > numTotal)
			{
				lastMsgNum = numTotal;
				return lastMsgNum + "of " + numTotal + " messages already processed";
			}
			final java.io.File F=new File(dir,""+lastMsgNum+".json");
			if(!F.exists())
				continue;
			final java.io.BufferedInputStream bin=new java.io.BufferedInputStream(new java.io.FileInputStream(F));
			final StringBuilder msgBuild = new StringBuilder("");
			for(int i=0;i<F.length();i++)
				msgBuild.append((char)bin.read());
			bin.close();
			final String msgPage = msgBuild.toString();
			final MiniJSON json=new MiniJSON();
			final MiniJSON.JSONObject msgObj = json.parseObject(msgPage).getCheckedJSONObject("ygData");

			String subject = CMLib.coffeeFilter().colorOnlyFilter(msgObj.getCheckedString("subject"),null);
			final long dateLong = CMath.s_long(msgObj.getCheckedString("postDate")) * 1000L;
			String author;
			if(msgObj.containsKey("profile"))
				author = msgObj.getCheckedString("profile");
			else
			if(msgObj.containsKey("authorName"))
				author = msgObj.getCheckedString("authorName");
			else
				author = "Unknown";
			if(author.trim().length()==0)
				author = "Unknown";
			String theMessage=msgObj.getCheckedString("rawEmail");
			final int headerEnd=theMessage.indexOf("\r\n\r\n");
			if(headerEnd<0)
				return "Failed: to find header in msg:" + lastMsgNum;
			Map<String,List<String>> headers = this.parseHeaders(theMessage.substring(0,headerEnd+4));
			if(!headers.containsKey("CONTENT-TYPE"))
				return "Failed: to find content-type in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
			String contentType=headers.get("CONTENT-TYPE").get(0);
			theMessage = theMessage.substring(headerEnd+4);
			if (theMessage.trim().length() == 0)
			{
				if(lastMsgNum == 18208)
					continue;
				return "Failed: to find lengthy msg in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
			}
			if(contentType.equalsIgnoreCase("multipart/mixed")||contentType.equalsIgnoreCase("multipart/related"))
			{
				String multiBoundary=null;
				final List<String> bounds = headers.get("CONTENT-TYPE");
				for(String s : bounds)
				{
					s=s.trim();
					if(s.toLowerCase().startsWith("boundary="))
					{
						multiBoundary=s.substring(9);
						if(multiBoundary.startsWith("\"") && multiBoundary.endsWith("\""))
							multiBoundary=multiBoundary.substring(1,multiBoundary.length()-1).trim();
					}
				}
				if(multiBoundary == null)
					return "Failed: missing multi-part-boundary in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
				boolean kaplah=false;
				for(String msgChoice : theMessage.split("--"+multiBoundary))
				{
					msgChoice=msgChoice.trim();
					if(msgChoice.length()==0)
						continue;
					if(msgChoice.startsWith("--"))
						break;
					final int innerHeaderDex=msgChoice.indexOf("\r\n\r\n");
					if(innerHeaderDex<0)
						return "Failed: missing innerHeaderDex in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
					final Map<String,List<String>> innerHeaders = this.parseHeaders(msgChoice.substring(0,innerHeaderDex+4));
					if(!innerHeaders.containsKey("CONTENT-TYPE"))
						return "Failed: to find content-type in inner header in :" + lastMsgNum + "/message/" + lastMsgNum;
					final String innerContentType=innerHeaders.get("CONTENT-TYPE").get(0);
					if(innerContentType.equalsIgnoreCase("multipart/alternative")
					||innerContentType.equalsIgnoreCase("text/plain")
					||innerContentType.equalsIgnoreCase("text/html"))
					{
						contentType=innerContentType;
						headers=innerHeaders;
						theMessage=msgChoice.substring(innerHeaderDex+4);
						kaplah=true;
						break;
					}
				}
				if(!kaplah)
					return "Failed: to find acceptable inner part in :" + lastMsgNum + "/message/" + lastMsgNum;
			}
			if(contentType.equalsIgnoreCase("multipart/alternative"))
			{
				String multiBoundary=null;
				final List<String> bounds = headers.get("CONTENT-TYPE");
				for(String s : bounds)
				{
					s=s.trim();
					if(s.toLowerCase().startsWith("boundary="))
					{
						multiBoundary=s.substring(9);
						if(multiBoundary.startsWith("\"") && multiBoundary.endsWith("\""))
							multiBoundary=multiBoundary.substring(1,multiBoundary.length()-1).trim();
					}
				}
				if(multiBoundary == null)
					return "Failed: missing multi-boundary in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
				boolean kaplah=false;
				for(String msgChoice : theMessage.split("--"+multiBoundary))
				{
					msgChoice=msgChoice.trim();
					if(msgChoice.length()==0)
						continue;
					if(msgChoice.startsWith("--"))
						break;
					final int innerHeaderDex=msgChoice.indexOf("\r\n\r\n");
					if(innerHeaderDex<0)
						return "Failed: missing innerHeaderDex in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
					final Map<String,List<String>> innerHeaders = this.parseHeaders(msgChoice.substring(0,innerHeaderDex+4));
					if(!innerHeaders.containsKey("CONTENT-TYPE"))
						return "Failed: to find content-type in inner header in :" + lastMsgNum + "/message/" + lastMsgNum;
					String encoding="7bit";
					if(innerHeaders.containsKey("CONTENT-TRANSFER-ENCODING"))
						encoding=innerHeaders.get("CONTENT-TRANSFER-ENCODING").get(0);
					//return "Failed: to find content-transfer-encoding in inner header in :" + lastMsgNum + "/message/" + lastMsgNum;
					msgChoice=msgChoice.substring(innerHeaderDex+4).trim();
					final String innerContentType=innerHeaders.get("CONTENT-TYPE").get(0);
					if(innerContentType.equalsIgnoreCase("text/plain")
					||innerContentType.equalsIgnoreCase("text/html"))
					{
						if(encoding.equalsIgnoreCase("base64"))
						{
							if(msgChoice.endsWith("\n(Message over 64 KB, truncated)"))
								msgChoice=msgChoice.substring(0,msgChoice.indexOf("\n(Message over 64 KB, truncated)"));
							theMessage=new String(B64Encoder.B64decode(msgChoice));
						}
						else
						if(encoding.equalsIgnoreCase("quoted-printable"))
							theMessage=decodeQuotedPrintable(msgChoice);
						else
						if((encoding.equalsIgnoreCase("7bit")) || (encoding.equalsIgnoreCase("8bit")))
							theMessage=msgChoice;
						else
							return "Failed: Invalid encoding '"+encoding+"' in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
						kaplah=true;
						if(innerContentType.equalsIgnoreCase("text/html"))
						{
							theMessage=stripHtmlHeaders(theMessage);
							//break; //kaplah
						}
						else
						if(innerContentType.equalsIgnoreCase("text/plain"))
						{
							theMessage=CMStrings.replaceAll(theMessage, "\n", "<BR>");
							break; //kaplah
						}
					}
				}
				if(!kaplah)
					return "Failed: to find acceptable inner message in :" + lastMsgNum + "/message/" + lastMsgNum;
			}
			else
			if(contentType.equalsIgnoreCase("text/plain"))
			{
				String encoding="7bit";
				if(headers.containsKey("CONTENT-TRANSFER-ENCODING"))
					encoding=headers.get("CONTENT-TRANSFER-ENCODING").get(0);
				if(encoding.equalsIgnoreCase("base64"))
				{
					if(theMessage.endsWith("\n(Message over 64 KB, truncated)"))
						theMessage=theMessage.substring(0,theMessage.indexOf("\n(Message over 64 KB, truncated)"));
					theMessage=new String(B64Encoder.B64decode(theMessage));
				}
				else
				if(encoding.equalsIgnoreCase("quoted-printable"))
					theMessage=decodeQuotedPrintable(theMessage);
				else
				if((!encoding.equalsIgnoreCase("7bit")) && (!encoding.equalsIgnoreCase("8bit")))
					return "Failed: Invalid encoding '"+encoding+"' in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
				theMessage=CMStrings.replaceAll(theMessage, "\n", "<BR>");
			}
			else
			if(contentType.equalsIgnoreCase("text/html"))
			{
				String encoding="7bit";
				if(headers.containsKey("CONTENT-TRANSFER-ENCODING"))
					encoding=headers.get("CONTENT-TRANSFER-ENCODING").get(0);
				if(encoding.equalsIgnoreCase("base64"))
				{
					if(theMessage.endsWith("\n(Message over 64 KB, truncated)"))
						theMessage=theMessage.substring(0,theMessage.indexOf("\n(Message over 64 KB, truncated)"));
					theMessage=new String(B64Encoder.B64decode(theMessage));
				}
				else
				if(encoding.equalsIgnoreCase("quoted-printable"))
					theMessage=decodeQuotedPrintable(theMessage);
				else
				if((!encoding.equalsIgnoreCase("7bit")) && (!encoding.equalsIgnoreCase("8bit")))
					return "Failed: Invalid encoding '"+encoding+"' in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;
				theMessage=stripHtmlHeaders(theMessage);
			}
			else
				return "Failed: Invalid content-type '"+contentType+"' in lastMsgNum:" + lastMsgNum + "/message/" + lastMsgNum;

			theMessage = CMStrings.replaceAll(theMessage, "&#39;", "`");
			theMessage = CMStrings.replaceAll(theMessage, "'", "`");
			theMessage = CMStrings.replaceAll(theMessage, "@", "&#64;");
			final JournalsLibrary.ForumJournal forum = CMLib.journals().getForumJournal("Support");
			if (forum == null)
				return "Failed: bad forum given";
			String email="";
			if(msgObj.containsKey("from"))
			{
				email=CMLib.coffeeFilter().colorOnlyFilter(msgObj.getCheckedString("from").trim(), null);
				final int dex=email.lastIndexOf('<');
				if(dex<0)
					email="";
				else
				if(email.endsWith(">"))
					email=email.substring(dex+1,email.length()-1);
				else
					email="";
			}
			if (email.indexOf('@') >= 0)
			{
				final MOB aM = CMLib.players().getLoadPlayerByEmail(email);
				if (aM != null)
					author = aM.Name();
				else
				if (CMProps.isUsingAccountSystem())
				{
					final PlayerAccount A = CMLib.players().getLoadAccountByEmail(email);
					if (A != null)
						author = A.getAccountName();
				}
				else
				if(!CMLib.players().playerExistsAllHosts(author))
					author = "_" + author;
			}
			else
			if(!CMLib.players().playerExistsAllHosts(author))
				author = "_" + author;

			String parent = "";
			if (subject.toLowerCase().startsWith("[coffeemud]"))
				subject = subject.substring(11).trim();
			if (subject.toUpperCase().startsWith("RE:"))
			{
				String subj = subject;
				while (subj.toUpperCase().startsWith("RE:") || subj.toLowerCase().startsWith("[coffeemud]"))
				{
					if (subj.toUpperCase().startsWith("RE:"))
						subj = subj.substring(3).trim();
					if (subj.toLowerCase().startsWith("[coffeemud]"))
						subj = subj.substring(11).trim();
				}

				final List<JournalEntry> journalEntries = CMLib.database().DBSearchAllJournalEntries(forum.NAME(), subj);
				if ((journalEntries != null) && (journalEntries.size() > 0))
				{
					JournalEntry WIN = null;
					for (final JournalEntry J : journalEntries)
					{
						if (J.subj().trim().equals(subj))
							WIN = J;
					}
					if (WIN == null)
					{
						for (final JournalEntry J : journalEntries)
						{
							if (J.subj().trim().equalsIgnoreCase(subj))
								WIN = J;
						}
					}
					if (WIN == null)
					{
						for (final JournalEntry J : journalEntries)
						{
							if (J.subj().trim().indexOf(subj) >= 0)
								WIN = J;
						}
					}
					if (WIN == null)
					{
						for (final JournalEntry J : journalEntries)
						{
							if (J.subj().toLowerCase().trim().indexOf(subj.toLowerCase()) >= 0)
								WIN = J;
						}
					}

					if (WIN != null)
						parent = WIN.key();
				}
				if (parent.length() == 0)
					subject = subj;
			}
			final JournalEntry msg = (JournalEntry)CMClass.getCommon("DefaultJournalEntry");
			msg.from (author);
			msg.subj (CMLib.webMacroFilter().clearWebMacros(subject));
			msg.msg (CMLib.webMacroFilter().clearWebMacros(theMessage));
			msg.dateStr(""+dateLong);
			msg.update (dateLong);
			msg.parent (parent);
			msg.msgIcon ("");
			msg.data ("");
			msg.to ("ALL");
			// check for dups
			final List<JournalEntry> chckEntries = CMLib.database().DBReadJournalMsgsNewerThan(forum.NAME(), "ALL", msg.date() - 1);
			boolean dup=false;
			for (final JournalEntry entry : chckEntries)
			{
				if ((entry.date() == msg.date())
				&& (entry.from().equals(msg.from()))
				&& (entry.subj().equals(msg.subj()))
				&& (entry.parent().equals(msg.parent())))
				{
					dup=true;
					break;
				}
			}
			if(dup)
			{
				if(mob != null)
				{
					mob.tell("Message "+lastMsgNum+" was a dup!");
					continue;
				}
				else
					return "Msg#" + lastMsgNum + " was a dup!";
			}
			CMLib.database().DBWriteJournal(forum.NAME(), msg);
			if (parent.length() > 0)
				CMLib.database().DBTouchJournalMessage(parent, msg.date());
			CMLib.journals().clearJournalSummaryStats(forum);
			if(mob != null)
				mob.tell("Message "+lastMsgNum+" posted.");
		}
		return "Post " + lastMsgNum + " submitted.";
	}

	public boolean testResistance(final MOB mob)
	{
		final Item I=CMClass.getWeapon("Dagger");
		mob.curState().setHitPoints(mob.maxState().getHitPoints());
		int curHitPoints=mob.curState().getHitPoints();
		CMLib.combat().postDamage(mob,mob,I,5,CMMsg.MSG_WEAPONATTACK,Weapon.TYPE_PIERCING,"<S-NAME> <DAMAGE> <T-NAME>.");
		if(mob.curState().getHitPoints()<curHitPoints-3)
			return false;
		curHitPoints=mob.curState().getHitPoints();
		CMLib.factions().setAlignmentOldRange(mob,0);
		final Ability A=CMClass.getAbility("Prayer_DispelEvil");
		A.invoke(mob,mob,true,1);
		if(mob.curState().getHitPoints()<curHitPoints)
			return false;
		curHitPoints=mob.curState().getHitPoints();
		if(mob.charStats().getSave(CharStats.STAT_SAVE_ACID)<30)
			return false;
		return true;
	}

	public Item[] giveTo(final Item I, final Ability A, final MOB mob1, final MOB mob2, final int code)
	{
		final Item[] IS=new Item[2];
		final Item I1=(Item)I.copyOf();
		if(A!=null)
			giveAbility(I1,A);
		if(code<2)
		{
			mob1.addItem(I1);
			if(code==1)
				I1.wearEvenIfImpossible(mob1);
		}
		else
		{
			mob1.location().addItem(I1);
			if((I1 instanceof Rideable)&&(code==2))
				mob1.setRiding((Rideable)I1);
		}

		IS[0]=I1;

		final Item I2=(Item)I.copyOf();
		if(A!=null)
			giveAbility(I2,A);
		if(mob2!=null)
		{
			if(code<2)
			{
				mob2.addItem(I2);
				if(code==1)
					I2.wearEvenIfImpossible(mob2);
			}
			else
			{
				mob2.location().addItem(I2);
				if((I2 instanceof Rideable)&&(code==2))
					mob2.setRiding((Rideable)I2);
			}
		}
		IS[1]=I2;
		mob1.location().recoverRoomStats();
		return IS;
	}

	public boolean spellCheck(final String[] spells, final MOB mob)
	{
		for (final String spell : spells)
		{
			if(mob.fetchAbility(spell)==null)
				return false;
		}
		return true;
	}

	public boolean effectCheck(final String[] spells, final MOB mob)
	{
		for (final String spell : spells)
		{
			if(mob.fetchEffect(spell)==null)
				return false;
		}
		return true;
	}

	public void reset(final MOB[] mobs,final MOB[] backups, final Room R, final Item[] IS,final Room R2)
	{
		R2.delAllEffects(true);
		if(IS!=null)
		{
			if(IS[0]!=null)
				IS[0].destroy();
			if(IS[1]!=null)
				IS[1].destroy();
		}
		if(mobs[0]!=null)
			mobs[0].destroy();
		if(mobs[1]!=null)
			mobs[1].destroy();
		R.recoverRoomStats();
		mobs[0]=CMClass.getMOB("StdMOB");
		mobs[0].baseCharStats().setMyRace(CMClass.getRace("Dwarf"));
		mobs[0].setName(L("A Dwarf"));
		mobs[0].baseCharStats().setCurrentClass(CMClass.getCharClass("Gaian"));
		mobs[0].baseCharStats().setCurrentClassLevel(30);
		mobs[0].basePhyStats().setLevel(30);
		mobs[0].recoverCharStats();
		mobs[0].recoverPhyStats();
		backups[0]=(MOB)mobs[0].copyOf();
		mobs[1]=CMClass.getMOB("StdMOB");
		mobs[1].setName(L("A Human"));
		mobs[1].baseCharStats().setMyRace(CMClass.getRace("Human"));
		mobs[1].baseCharStats().setCurrentClass(CMClass.getCharClass("Druid"));
		mobs[1].baseCharStats().setCurrentClassLevel(30);
		mobs[1].basePhyStats().setLevel(30);
		mobs[1].recoverCharStats();
		mobs[1].recoverPhyStats();
		backups[0]=(MOB)mobs[1].copyOf();

		mobs[0].bringToLife(R,true);
		mobs[1].bringToLife(R,true);
	}

	public int[] recoverMath(final int level, final int con, final int inte, final int wis, final int str, final boolean isHungry, final boolean isThirsty, final boolean isFatigued, final boolean isSleeping, final boolean isSittingOrRiding,final boolean isFlying,final boolean isSwimming)
	{
		/*	# @x1=stat(con/str/int-wis), @x2=level, @x3=hungry?1:0, @x4=thirsty?1:0, @x5=fatigued?0:1 # @x6=asleep?1:0, @x7=sitorride?1:0, @x8=flying?0:1, @x9=swimming?0:1 */
		final CompiledFormula stateHitPointRecoverFormula = CMath.compileMathExpression("5+(((@x1 - (@xx*@x3/2.0) - (@xx*@x4/2.0))*@x2/9.0) + (@xx*@x6*.5) + (@xx/4.0*@x7) - (@xx/2.0*@x9))");
		final CompiledFormula stateManaRecoverFormula = CMath.compileMathExpression("25+(((@x1 - (@xx*@x3/2.0) - (@xx*@x4/2.0) - (@xx*@x5/2.0))*@x2/50.0) + (@xx*@x6*.5) + (@xx/4.0*@x7) - (@xx/2.0*@x9))");
		final CompiledFormula stateMovesRecoverFormula = CMath.compileMathExpression("25+(((@x1 - (@xx*@x3/2.0) - (@xx*@x4/2.0) - (@xx*@x5/2.0))*@x2/10.0) + (@xx*@x6*.5) + (@xx/4.0*@x7) + (@xx/4.0*@x8) - (@xx/2.0*@x9))");
		final double[] vals=new double[]{
			con,
			level,
			isHungry?1.0:0.0,
			isThirsty?1.0:0.0,
			isFatigued?1.0:0.0,
			isSleeping?1.0:0.0,
			isSittingOrRiding?1.0:0.0,
			isFlying?1.0:0.0,
			isSwimming?1.0:0.0
		};
		final int[] v=new int[3];
		v[0]= (int)Math.round(CMath.parseMathExpression(stateHitPointRecoverFormula, vals, 0.0));

		vals[0]=((inte+wis));
		v[1]= (int)Math.round(CMath.parseMathExpression(stateManaRecoverFormula, vals, 0.0));

		vals[0]=str;
		v[2]= (int)Math.round(CMath.parseMathExpression(stateMovesRecoverFormula, vals, 0.0));
		return v;
	}

	@Override
	public boolean execute(final MOB mob, final List<String> commands, final int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()>1)
		{
			final String what=commands.get(1).toUpperCase().trim();
			//String rest=CMParms.combine(commands,2);
			if(what.equalsIgnoreCase("levelxptest"))
			{
				for(int i=0;i<100;i++)
					CMLib.leveler().getLevelExperience(mob, CMLib.dice().roll(1,100,0));
				final MOB M=CMClass.getMOB("StdMOB");
				M.setExperience(0);
				for(int i=1;i<100;i++)
				{
					M.basePhyStats().setLevel(i);
					M.phyStats().setLevel(i);
					M.baseCharStats().setClassLevel(M.baseCharStats().getCurrentClass(),i);
					M.charStats().setClassLevel(M.baseCharStats().getCurrentClass(),i);
					final int level=M.basePhyStats().level();
					int xp=0;
					final String s=i+") "+M.getExperience()+"/"+M.getExpNextLevel()+"/"+M.getExpNeededLevel()+": ";
					while(level==M.basePhyStats().level())
					{
						xp+=10;
						CMLib.leveler().gainExperience(M,"TEST:",null,"",10, true);
					}
					mob.tell(s+xp);
				}
			}
			else
			if(what.equalsIgnoreCase("yahoo"))
			{
				try
				{
					//final int rememberMe=18201;
					final String rest=CMParms.combine(commands,2);
					if(CMath.isInteger(rest))
						mob.tell(copyYahooGroupMsg(mob,CMath.s_int(rest)));
					else
						mob.tell("18201 was a nice year.");
					return true;
				}
				catch(final Exception e)
				{
					e.printStackTrace();
					Log.errOut(e);
					mob.tell(e.getMessage());
				}
			}
			if(what.equalsIgnoreCase("levelcharts"))
			{
				final StringBuffer str=new StringBuffer("");
				for(int i=0;i<110;i++)
					str.append(i+"="+CMLib.leveler().getLevelExperience(mob, i)+"\r");
				mob.tell(str.toString());
			}
			else
			if(what.equalsIgnoreCase("ratspercolator"))
			{
				final Command C=CMClass.getCommand("Generate");
				if((commands.size()<3)||(!CMath.isInteger(commands.get(2))))
				{
					mob.tell("You need an number of iterations first, I'm afraid");
					return false;
				}
				final int iterations=CMath.s_int(commands.remove(2));
				final String theRest=CMParms.combine(commands,2).toUpperCase();
				commands.set(0, "GENERATE");
				commands.remove(1);
				final String areaName = CMParms.getParmStr(theRest, "AREANAME", "");
				if(areaName.length()==0)
				{
					mob.tell("You need an area name, I'm afraid");
					return false;
				}

				for(int i=0;i<iterations;i++)
				{
					mob.tell(L("Generate #@x1: Working...",""+i));
					final XVector<String> cmds2=new XVector<String>(commands);
					C.execute(mob, cmds2, metaFlags);

					final Area A=CMLib.map().getArea(areaName);
					if(A==null)
					{
						mob.tell("Fail!");
						break;
					}
					CMLib.map().obliterateMapArea(A);
					mob.tell(L("Generate #@x1: Complete!",""+i));
				}
			}
			else
			if(what.equalsIgnoreCase("timsdeconstruction"))
			{
				mob.tell(L("Checking..."));
				final String theRest=CMParms.combine(commands,2).toUpperCase();
				for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if(A instanceof ItemCraftor)
					{
						final ItemCraftor I=(ItemCraftor)A;
						if((theRest.length()==0)||(I.ID().toUpperCase().indexOf(theRest)>=0))
						{
							final List<ItemCraftor.CraftedItem> set=I.craftAllItemSets(false);
							for(final ItemCraftor.CraftedItem KP : set)
							{
								if((KP.item instanceof Armor)||(KP.item instanceof Weapon))
								{
									final int newLevel=CMLib.itemBuilder().timsLevelCalculator(KP.item);
									if((newLevel < Math.round(KP.item.basePhyStats().level() * .7))
									||(newLevel > Math.round(KP.item.basePhyStats().level() * 1.3)))
										mob.tell(KP.item.name()+": "+KP.item.basePhyStats().level()+"!="+newLevel);
								}
							}
						}
					}
				}
			}
			else
			if(what.equalsIgnoreCase("recover"))
			{
				for(final int level : new int[]{1,10,50,90})
				{
					int hp;
					int mana;
					int move;
					int stat;
					switch(level)
					{
					default:
					case 1:
						hp=20;
						mana=100;
						move=100;
						stat=15;
						break;
					case 10:
						hp=120;
						mana=150;
						move=190;
						stat=17;
						break;
					case 50:
						hp=500;
						mana=420;
						move=520;
						stat=18;
						break;
					case 90:
						hp=1200;
						mana=700;
						move=1000;
						stat=18;
						break;
					}
					final StringBuilder str=new StringBuilder("level: "+level+"\n\r");
					int[] resp;
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/false,/*Sleep*/false,/*Sit*/false,/*Fly*/false,/*Swim*/false);
					str.append(L("standing: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/true,/*Thirs*/false,/*Fatig*/false,/*Sleep*/false,/*Sit*/false,/*Fly*/false,/*Swim*/false);
					str.append(L("hungry: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/true,/*Fatig*/false,/*Sleep*/false,/*Sit*/false,/*Fly*/false,/*Swim*/false);
					str.append(L("thirsty: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/true,/*Sleep*/false,/*Sit*/false,/*Fly*/false,/*Swim*/false);
					str.append(L("fatigued: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/false,/*Sleep*/true,/*Sit*/false,/*Fly*/false,/*Swim*/false);
					str.append(L("sleep: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/false,/*Sleep*/false,/*Sit*/true,/*Fly*/false,/*Swim*/false);
					str.append(L("sitting: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/false,/*Sleep*/false,/*Sit*/false,/*Fly*/true,/*Swim*/false);
					str.append(L("flying: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					resp=recoverMath(level,stat,stat,stat-4,stat,/*Hun*/false,/*Thirs*/false,/*Fatig*/false,/*Sleep*/false,/*Sit*/false,/*Fly*/false,/*Swim*/true);
					str.append(L("swimming: hpticks=@x1,  manaticks=@x2,  moveticks=@x3\n\r",""+(hp/resp[0]),""+(mana/resp[1]),""+(move/resp[2])));
					str.append("\n\r");
					mob.tell(str.toString());
				}
			}
			else
			if(what.equalsIgnoreCase("deconstruction"))
			{
				mob.tell(L("Building item sets..."));
				final Hashtable<ItemCraftor,List<ItemCraftor.CraftedItem>> allSets=new Hashtable<ItemCraftor,List<ItemCraftor.CraftedItem>>();
				for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
				{
					final Ability A=a.nextElement();
					if(A instanceof ItemCraftor)
					{
						final ItemCraftor I=(ItemCraftor)A;
						allSets.put(I,I.craftAllItemSets(false));
					}
				}
				mob.tell(L("Sorting..."));
				for(final ItemCraftor I : allSets.keySet())
				{
					final List<ItemCraftor.CraftedItem> allItems=allSets.get(I);
					for(final ItemCraftor.CraftedItem P : allItems)
					{
						if(P.item.material()!=RawMaterial.RESOURCE_WHITE_GOLD)
						for(final ItemCraftor oI : allSets.keySet())
						{
							if(oI.supportsDeconstruction())
							{
								if(!oI.mayICraft(P.item))
								{
									if(oI==I)
									{
										Log.sysOut("INFO",P.item.name()+" can't even be built by "+oI.ID());
									}
								}
								else
								{
									if(oI!=I)
										Log.sysOut("INFO",P.item.name()+", owned by "+I.ID()+" can also be built by "+oI.ID());
								}
							}
						}
					}
				}
			}
			else
			if(what.equalsIgnoreCase("statcreationspeed"))
			{
				int times=CMath.s_int(CMParms.combine(commands,2));
				if(times<=0)
					times=9999999;
				mob.tell(L("times=@x1",""+times));
				Object newStats=null;
				long time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					newStats=mob.basePhyStats().copyOf();
				mob.tell(L("PhyStats CopyOf took :@x1",""+(System.currentTimeMillis()-time)));
				time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					mob.basePhyStats().copyInto((PhyStats)newStats);
				mob.tell(L("PhyStats CopyInto took :@x1",""+(System.currentTimeMillis()-time)));

				time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					newStats=mob.baseCharStats().copyOf();
				mob.tell(L("CharStats CopyOf took :@x1",""+(System.currentTimeMillis()-time)));
				time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					mob.baseCharStats().copyInto((CharStats)newStats);
				mob.tell(L("CharStats CopyInto took :@x1",""+(System.currentTimeMillis()-time)));

				time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					newStats=mob.maxState().copyOf();
				mob.tell(L("CharState CopyOf took :@x1",""+(System.currentTimeMillis()-time)));
				time=System.currentTimeMillis();
				for(int i=0;i<times;i++)
					mob.maxState().copyInto((CharState)newStats);
				mob.tell(L("CharState CopyInto took :@x1",""+(System.currentTimeMillis()-time)));
			}
			else
			if(what.equalsIgnoreCase("randomroompick"))
			{
				final int num=CMath.s_int(CMParms.combine(commands,2));
				int numNull=0;
				for(int i=0;i<num;i++)
				{
					if(mob.location().getArea().getRandomProperRoom()==null)
						numNull++;
				}
				mob.tell(L("Picked @x1/@x2 rooms in this area.",""+(num-numNull),""+num));
			}
			else
			if(what.equalsIgnoreCase("randomnames"))
			{
				final int num=CMath.s_int(CMParms.combine(commands,2));
				final StringBuilder str=new StringBuilder("");
				for(int i=0;i<num;i++)
					str.append(CMLib.login().generateRandomName(3, 8)).append(", ");
				if(mob.session()!=null)
					mob.session().rawPrint(str.toString()+"\n");
			}
			else
			if(what.equalsIgnoreCase("edrecipe"))
			{
				final boolean save = CMParms.combine(commands,2).equalsIgnoreCase("save");
				for(final Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
				{
					final Ability A=e.nextElement();
					if(A instanceof RecipeDriven)
					{
						final RecipeDriven iA=(RecipeDriven)A;
						if(iA.getRecipeFormat().length()>0)
						{
							try
							{
								CMLib.ableParms().testRecipeParsing(iA.getRecipeFilename(),iA.getRecipeFormat(),save);
							}
							catch(final CMException e2)
							{
								mob.tell(L("Recipe parse exception @x1",e2.getMessage()));
							}
						}
					}
				}
			}
			else
			if(what.equalsIgnoreCase("scriptable"))
			{
				final Area A=CMClass.getAreaType("StdArea");
				A.setName(L("UNKNOWNAREA"));
				final Room R=CMClass.getLocale("WoodRoom");
				R.setRoomID("UNKNOWN1");
				R.setArea(A);
				final MOB M=CMClass.getMOB("GenShopkeeper");
				M.setName(L("Shoppy"));
				final ShopKeeper SK=(ShopKeeper)M;
				Item I=CMClass.getWeapon("Dagger");
				SK.getShop().addStoreInventory(I,10,5);
				I=CMClass.getWeapon("Shortsword");
				SK.getShop().addStoreInventory(I,10,5);
				SK.setInvResetRate(999999999);
				final Room R2=CMClass.getLocale("WoodRoom");
				R2.setRoomID("UNKNOWN2");
				R2.setArea(A);
				R.rawDoors()[Directions.NORTH]=R2;
				R2.rawDoors()[Directions.SOUTH]=R;
				R.setRawExit(Directions.NORTH,CMClass.getExit("Open"));
				R2.setRawExit(Directions.SOUTH,CMClass.getExit("Open"));
				final Behavior B=CMClass.getBehavior("Scriptable");
				B.setParms("LOAD=progs/scriptableTest.script");
				M.addBehavior(B);
				M.text();
				M.bringToLife(R,true);
				M.setStartRoom(null);
				final ScriptingEngine S=(ScriptingEngine)M.fetchBehavior("Scriptable");
				for(int i=0;i<1000;i++)
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (final Exception e)
					{
					}
					if(S.getVar("Shoppy","ERRORS").length()>0)
						break;
				}
				mob.tell(L("Successes: @x1",S.getVar("Shoppy","SUCCESS")));
				mob.tell(L("\n\rUntested: @x1",S.getVar("Shoppy","UNTESTED")));
				mob.tell(L("\n\rErrors: @x1",S.getVar("Shoppy","ERRORS")));
				M.destroy();
				R2.destroy();
				R.destroy();
				A.destroy();
				Resources.removeResource("PARSEDPRG: LOAD=progs/scriptableTest.script");
			}
			else
			if(what.equalsIgnoreCase("mudhourstil"))
			{
				final String startDate=CMParms.combine(commands,2);
				final int x=startDate.indexOf('-');
				final int mudmonth=CMath.s_int(startDate.substring(0,x));
				final int mudday=CMath.s_int(startDate.substring(x+1));
				final TimeClock C=mob.location().getArea().getTimeObj();
				final TimeClock NOW=mob.location().getArea().getTimeObj();
				C.setMonth(mudmonth);
				C.setDayOfMonth(mudday);
				C.setHourOfDay(0);
				if((mudmonth<NOW.getMonth())
				||((mudmonth==NOW.getMonth())&&(mudday<NOW.getDayOfMonth())))
					C.setYear(NOW.getYear()+1);
				else
					C.setYear(NOW.getYear());
				final long millidiff=C.deriveMillisAfter(NOW);
				mob.tell(L("MilliDiff=@x1",""+millidiff));
				final long time = System.currentTimeMillis() + millidiff;
				final TimeClock C2=(TimeClock)NOW.copyOf();
				C2.deriveClock(time);
				mob.tell(L("Backport=@x1",""+C2.toTimePeriodCodeString()));
				return true;
			}
			else
			if(what.equalsIgnoreCase("playeredit"))
			{
				MOB M=CMLib.players().getLoadPlayer("Testplayeredit");
				if(M!=null)
					CMLib.players().obliteratePlayer(M, true, true);
				M=CMClass.getMOB("StdMOB");
				M.setName("Testplayeredit");
				M.setPlayerStats((PlayerStats)CMClass.getCommon("DefaultPlayerStats"));
				M.setBaseCharStats((CharStats)CMClass.getCommon("DefaultCharStats"));
				CMLib.database().DBCreateCharacter(M);
				CMLib.database().DBUpdatePlayerPlayerStats(M);
				M=CMLib.players().getLoadPlayer("Testplayeredit");
				// first test the normal stuff
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					switch(c)
					{
					case ABLES:
					{
						Ability A=CMClass.getAbility("Spell_Web");
						A.setMiscText("web");
						A.setProficiency(54);
						M.addAbility(A);
						A=CMClass.getAbility("Spell_Fireball");
						A.setMiscText("fire");
						A.setProficiency(94);
						M.addAbility(A);
						A=CMClass.getAbility("Spell_GustOfWind");
						A.setMiscText("wind");
						A.setProficiency(24);
						M.addAbility(A);
						break;
					}
					case ACCOUNT:
						break;
					case AFFBEHAV:
						Behavior B=CMClass.getBehavior("Mobile");
						B.setParms("1");
						M.addBehavior(B);
						B=CMClass.getBehavior("Mime");
						B.setParms("2");
						M.addBehavior(B);
						B=CMClass.getBehavior("Emoter");
						B.setParms("3");
						M.addBehavior(B);
						B=CMClass.getBehavior("CorpseEater");
						B.setParms("4");
						M.addBehavior(B);
						Ability A=CMClass.getAbility("Prop_AbilityImmunity");
						A.setMiscText("web");
						M.addNonUninvokableEffect(A);
						A=CMClass.getAbility("Prop_SafePet");
						A.setMiscText("inv");
						M.addNonUninvokableEffect(A);
						A=CMClass.getAbility("Prop_Smell");
						A.setMiscText("smell");
						M.addNonUninvokableEffect(A);
						break;
					case AGE:
						M.setAgeMinutes(123456);
						break;
					case ALIGNMENT:
						M.addFaction(CMLib.factions().getAlignmentID(), 2345);
						break;
					case ARMOR:
						M.basePhyStats().setArmor(404);
						break;
					case ATTACK:
						M.basePhyStats().setAttackAdjustment(111);
						break;
					case CHANNELMASK:
						M.playerStats().setChannelMask(11);
						break;
					case CHARCLASS:
						M.baseCharStats().setCurrentClass(CMClass.getCharClass("Apprentice"));
						M.baseCharStats().setCurrentClassLevel(1);
						M.baseCharStats().setCurrentClass(CMClass.getCharClass("Fighter"));
						M.baseCharStats().setCurrentClassLevel(3);
						M.basePhyStats().setLevel(4);
						break;
					case CLANS:
						M.setClan(CMLib.clans().clans().nextElement().clanID(), 1);
						break;
					case COLOR:
						M.playerStats().setColorStr(CMLib.database().DBReadPlayerValue("Zac", c).toString());
						break;
					case DAMAGE:
						M.basePhyStats().setDamage(99);
						break;
					case DEITY:
						M.baseCharStats().setWorshipCharID(CMLib.map().deities().nextElement().Name());
						break;
					case DESCRIPTION:
						M.setDescription("My Desc");
						break;
					case EMAIL:
						M.playerStats().setEmail("my@email.com");
						break;
					case EXPERIENCE:
						M.setExperience(4321);
						break;
					case EXPERS:
					{
						for(int i=0;i<10;i++)
						{
							final int r=CMLib.dice().roll(1, CMLib.expertises().numExpertises(), -1);
							final Enumeration<ExpertiseLibrary.ExpertiseDefinition> defs=CMLib.expertises().definitions();
							for(int x=0;x<r;x++)
								defs.nextElement();
							M.addExpertise(defs.nextElement().ID());
						}
						break;
					}
					case FACTIONS:
					{
						final Enumeration<Faction> fs = CMLib.factions().factions();
						M.addFaction(fs.nextElement().factionID(), 10);
						M.addFaction(fs.nextElement().factionID(), 100);
						M.addFaction(fs.nextElement().factionID(), 1000);
						break;
					}
					case HEIGHT:
						M.basePhyStats().setHeight(121);
						break;
					case HITPOINTS:
						M.baseState().setHitPoints(10);
						break;
					case INVENTORY:
					{
						Item I=CMClass.getArmor("GenShirt");
						I.basePhyStats().setLevel(1);
						I.text();
						M.addItem(I);
						I=CMClass.getWeapon("GenWeapon");
						I.basePhyStats().setLevel(1);
						M.addItem(I);
						I.text();
						I=CMClass.getArmor("GenArmor");
						I.basePhyStats().setLevel(1);
						M.addItem(I);
						I.text();
						I=CMClass.getArmor("GenShirt");
						I.basePhyStats().setLevel(1);
						M.addItem(I);
						I.text();
						I=CMClass.getWeapon("GenWeapon");
						I.basePhyStats().setLevel(1);
						M.addItem(I);
						I.text();
						break;
					}
					case LASTDATE:
						M.playerStats().setLastDateTime(123123123);
						break;
					case LASTIP:
						M.playerStats().setLastIP("10.10.10.1");
						break;
					case LEIGE:
						M.setLiegeID("Zac");
						break;
					case LEVEL:
						M.basePhyStats().setLevel(4);
						break;
					case LOCATION:
						M.setLocation(CMLib.map().getRandomRoom());
						break;
					case MANA:
						M.baseState().setMana(101);
						break;
					case MATTRIB:
						M.setAttributesBitmap(543);
						break;
					case MONEY:
						CMLib.beanCounter().addMoney(M, 1000);
						break;
					case MOVES:
						M.baseState().setMovement(110);
						break;
					case NAME:
						break;
					case PASSWORD:
						M.playerStats().setPassword("newpass");
						break;
					case PRACTICES:
						M.setPractices(77);
						break;
					case QUESTPOINTS:
						M.setQuestPoint(88);
						break;
					case RACE:
						M.baseCharStats().setMyRace(CMClass.getRace("Toadstool"));
						break;
					case STARTROOM:
						M.setStartRoom(CMLib.map().getRandomRoom());
						break;
					case TATTS:
						M.addTattoo("TATTEE1");
						M.addTattoo("TATTEE2");
						M.addTattoo("TATTEE3");
						M.addTattoo("TATTEE4");
						M.addTattoo("TATTEE5");
						break;
					case TRAINS:
						M.setTrains(99);
						break;
					case WEIGHT:
						M.basePhyStats().setWeight(927);
						break;
					case WIMP:
						M.setWimpHitPoint(123);
						break;
					default:
						break;
					}
				}
				CMLib.players().savePlayers();
				final Object[] saved = new Object[PlayerLibrary.PlayerCode.values().length];
				final Object[] saved2 = new Object[PlayerLibrary.PlayerCode.values().length];
				int s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					saved[s++]=val;
					if(val == null)
						mob.tell("PREFAIL: "+c.name()+"="+val);
					//mob.tell(c.name()+"="+val);
				}
				CMLib.players().unloadOfflinePlayer(M);
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					this.compareObjectsAndReport("0",mob, c.name(), val, saved[s]);
					s++;
				}
				M=CMLib.players().getLoadPlayer("Testplayeredit");
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					saved[s++]=val; // now they have db ids!
					if(val == null)
						mob.tell("2PREFAIL: "+c.name()+"="+val);
					//mob.tell(c.name()+"="+val);
				}
				// test writing to a mob in ram
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object oldValue = CMLib.players().getPlayerValue("Testplayeredit", c);
					Object newValue = null;
					switch(c)
					{
					case ABLES:
					{
						@SuppressWarnings("unchecked")
						final List<Ability> l=(List<Ability>)oldValue;
						l.remove(1);
						final Ability A=CMClass.getAbility("Spell_Blink");
						A.setMiscText("eye");
						A.setProficiency(22);
						l.add(A);
						newValue = new XVector<Object>(l);
						break;
					}
					case ACCOUNT:
						break;
					case AFFBEHAV:
					{
						@SuppressWarnings("unchecked")
						final List<CMObject> l=(List<CMObject>)oldValue;
						l.remove(1);
						final Ability A=CMClass.getAbility("Skill_Stonecunning");
						A.setMiscText("eye");
						A.makeNonUninvokable();
						l.add(A);
						newValue = new XVector<CMObject>(l);
						break;
					}
					case ALIGNMENT: case ARMOR: case ATTACK: case CHANNELMASK:
					case DAMAGE: case EXPERIENCE: case HEIGHT: case HITPOINTS:
					case MANA: case MATTRIB: case MOVES: case PRACTICES: case QUESTPOINTS:
					case TRAINS: case WEIGHT: case WIMP:
						newValue=Integer.valueOf(((Integer)oldValue).intValue()-1);
						break;
					case CHARCLASS:
						newValue = CMClass.getCharClass("Apprentice");
						break;
					case CLANS:
					{
						@SuppressWarnings("unchecked")
						final List<Pair<Clan,Integer>> l=(List<Pair<Clan,Integer>>)oldValue;
						l.remove(0);
						final Enumeration<Clan> e=CMLib.clans().clans();
						e.nextElement();
						final Clan C=e.nextElement();
						l.add(new Pair<Clan,Integer>(C, Integer.valueOf(2)));
						newValue = new XVector<Object>(l);
						break;
					}
					case COLOR:
					case DEITY:
						newValue="";
						break;
					case DESCRIPTION:
					case EMAIL:
						newValue=oldValue.toString()+"2";
						break;
					case EXPERS:
					{
						@SuppressWarnings("unchecked")
						final List<String> l=(List<String>)oldValue;
						l.remove(1);
						for(int i=0;i<3;i++)
						{
							final int r=CMLib.dice().roll(1, CMLib.expertises().numExpertises(), -1);
							final Enumeration<ExpertiseLibrary.ExpertiseDefinition> defs=CMLib.expertises().definitions();
							for(int x=0;x<r;x++)
								defs.nextElement();
							final String new1=defs.nextElement().ID();
							boolean found=false;
							for(int d=0;d<l.size();d++)
								if(l.get(d).startsWith(new1.substring(0,new1.length()-2)))
									found=true;
							if(!found)
								l.add(new1);
						}
						newValue = new XVector<Object>(l);
						break;
					}
					case FACTIONS:
					{
						@SuppressWarnings("unchecked")
						final List<Pair<String,Integer>> l=(List<Pair<String,Integer>>)oldValue;
						l.remove(1);
						final Enumeration<Faction> fs = CMLib.factions().factions();
						for(int i=0;i<3;i++)
							fs.nextElement();
						l.add(new Pair<String,Integer>(fs.nextElement().factionID(),Integer.valueOf(9999)));
						newValue = new XVector<Object>(l);
						break;
					}
					case INVENTORY:
					{
						@SuppressWarnings("unchecked")
						final List<Triad<String,String,String>> l=(List<Triad<String,String,String>>)oldValue;
						l.remove(0); // remove a shirt
						Item I=CMClass.getItem("GenBow");
						I.basePhyStats().setLevel(1);
						I.text();
						l.add(new Triad<String,String,String>(I.databaseID(),I.ID(),I.text()));
						I=CMClass.getItem("GenWeapon");
						I.basePhyStats().setLevel(1);
						I.text();
						l.add(new Triad<String,String,String>(I.databaseID(),I.ID(),I.text()));
						newValue = new XVector<Object>(l);
						break;
					}
					case LASTDATE: case AGE:
						newValue=Long.valueOf(((Long)oldValue).longValue()+1);
						break;
					case LASTIP:
						newValue="10.10.10.2";
						break;
					case LEIGE:
						newValue="Fred";
						break;
					case LOCATION:
					case STARTROOM:
						newValue=CMLib.map().getExtendedRoomID(CMLib.map().getRandomRoom());
						break;
					case MONEY:
					{
						final XVector<Coins> nv = new XVector<Coins>();
						@SuppressWarnings("unchecked")
						final List<Coins> ov = (List<Coins>)oldValue;
						for(final Coins C : ov)
						{
							final Coins C2=(Coins)C.copyOf();
							C2.setDatabaseID(C.databaseID());
							C2.setNumberOfCoins(C2.getNumberOfCoins()+1);
							nv.add(C2);
						}
						newValue=nv;
						break;
					}
					case NAME: case PASSWORD:
						break;
					case LEVEL:
						newValue=Integer.valueOf(5);
						break;
					case RACE:
						newValue=CMClass.getRace("Unique");
						break;
					case TATTS:
					{
						@SuppressWarnings("unchecked")
						final List<Tattoo> l=(List<Tattoo>)oldValue;
						final Tattoo t = l.remove(2);
						t.set("TATTEE6");
						l.add(t);
						newValue = new XVector<Object>(l);
						break;
					}
					default:
						break;
					}
					saved2[s]=newValue; // new clean value
					if(newValue != null)
						CMLib.players().setPlayerValue("Testplayeredit", c, newValue);
					else
						saved2[s]=oldValue;
					s++;
				}
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					compareObjectsAndReport("1",mob, c.name(), val, saved2[s]);
					s++;
				}
				CMLib.players().savePlayers();
				CMLib.players().unloadOfflinePlayer(M); // now the player is offline again
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					compareObjectsAndReport("2",mob, c.name(), val, saved2[s]);
					s++;
				}
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
					CMLib.players().setPlayerValue("Testplayeredit", c, saved[s++]);
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					compareObjectsAndReport("3",mob, c.name(), val, saved[s]);
					s++;
				}
				M=CMLib.players().getLoadPlayer("Testplayeredit");
				s=0;
				for(final PlayerLibrary.PlayerCode c : PlayerLibrary.PlayerCode.values())
				{
					final Object val=CMLib.players().getPlayerValue("Testplayeredit", c);
					compareObjectsAndReport("4",mob, c.name(), val, saved[s]);
					s++;
				}
			}
			else
			if(what.equalsIgnoreCase("horsedraggers"))
			{
				final MOB M=CMClass.getMOB("GenMOB");
				M.setName(L("MrRider"));
				M.setDisplayText(L("MrRider is here"));
				final Behavior B=CMClass.getBehavior("Mobile");
				B.setParms("min=1 max=1 chance=99 wander");
				M.addBehavior(B);
				M.bringToLife(mob.location(),true);
				final MOB M2=CMClass.getMOB("GenRideable");
				M2.setName(L("a pack horse"));
				M2.setDisplayText(L("a pack horse is here"));
				M2.bringToLife(mob.location(),true);
				M.setRiding((Rideable)M2);
				final Behavior B2=CMClass.getBehavior("Scriptable");
				B2.setParms("RAND_PROG 100;IF !ISHERE(nondescript);MPECHO LOST MY CONTAINER $d $D!; GOSSIP LOST MY CONTAINER! $d $D; MPPURGE $i;ENDIF;~;");
				M2.addBehavior(B2);
				final Item I=CMClass.getBasicItem("LockableContainer");
				mob.location().addItem(I,ItemPossessor.Expire.Player_Drop);
				I.setRiding((Rideable)M2);
			}

			Ability A2=null;
			Item I=null;
			CMMsg msg=null;
			Command C=null;
			Item IS[]=new Item[2];
			final Room R=mob.location();
			final Room upRoom=R.rawDoors()[Directions.UP];
			final Exit upExit=R.getRawExit(Directions.UP);
			final Room R2=CMClass.getLocale("StoneRoom");
			R2.setArea(R.getArea());
			R.setRawExit(Directions.UP,CMClass.getExit("Open"));
			R2.setRawExit(Directions.DOWN,CMClass.getExit("Open"));
			R.rawDoors()[Directions.UP]=R2;
			R2.rawDoors()[Directions.DOWN]=R;
			final MOB[] mobs=new MOB[2];
			final MOB[] backups=new MOB[2];
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HaveEnabler"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HaveEnabler = CMClass.getAbility("Prop_HaveEnabler");
				HaveEnabler.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", HaveEnabler.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveEnabler, mobs[0], null, 0);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS[0].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HaveEnabler.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", HaveEnabler.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveEnabler, mobs[0], mobs[1], 0);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.moveItemTo(IS[1], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HaveSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HaveSpellCast = CMClass.getAbility("Prop_HaveSpellCast");
				HaveSpellCast.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", HaveSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveSpellCast, mobs[0], null, 0);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS[0].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HaveSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", HaveSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveSpellCast, mobs[0], mobs[1], 0);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.moveItemTo(IS[1], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HaveSpellCast.setMiscText(semiSpellList() + "MASK=-Human");
				mob.tell(L("Test:"+what+"-3: @x1", HaveSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveSpellCast, mobs[0], mobs[1], 0);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.moveItemTo(IS[1], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_WearEnabler"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability WearEnabler = CMClass.getAbility("Prop_WearEnabler");
				WearEnabler.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", WearEnabler.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearEnabler, mobs[0], null, 1);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS[0].unWear();
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				WearEnabler.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", WearEnabler.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearEnabler, mobs[0], mobs[1], 1);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_WearSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability WearSpellCast = CMClass.getAbility("Prop_WearSpellCast");
				WearSpellCast.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", WearSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearSpellCast, mobs[0], null, 1);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS[0].unWear();
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				WearSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", WearSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearSpellCast, mobs[0], mobs[1], 1);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				WearSpellCast.setMiscText(semiSpellList() + "MASK=-Human");
				mob.tell(L("Test:"+what+"-3: @x1", WearSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearSpellCast, mobs[0], mobs[1], 1);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_RideEnabler"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability RideEnabler = CMClass.getAbility("Prop_RideEnabler");
				RideEnabler.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", RideEnabler.accountForYourself()));
				IS = giveTo(CMClass.getItem("Boat"), RideEnabler, mobs[0], null, 2);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				mobs[0].setRiding(null);
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				RideEnabler.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", RideEnabler.accountForYourself()));
				IS = giveTo(CMClass.getItem("Boat"), RideEnabler, mobs[0], mobs[1], 2);
				if (!spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				mobs[0].setRiding(null);
				mobs[1].setRiding(null);
				R.recoverRoomStats();
				if (spellCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (spellCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_RideSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability RideSpellCast = CMClass.getAbility("Prop_RideSpellCast");
				RideSpellCast.setMiscText(semiSpellList());
				// mob.tell(L("Test:"+what+"-1: @x1",RideSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getItem("Boat"), RideSpellCast, mobs[0], null, 2);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				mobs[0].setRiding(null);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				RideSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", RideSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getItem("Boat"), RideSpellCast, mobs[0], mobs[1], 2);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				mobs[0].setRiding(null);
				mobs[1].setRiding(null);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				RideSpellCast.setMiscText(semiSpellList() + "MASK=-Human");
				mob.tell(L("Test:"+what+"-3: @x1", RideSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getItem("Boat"), RideSpellCast, mobs[0], mobs[1], 2);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				mobs[0].setRiding(null);
				mobs[1].setRiding(null);
				R.recoverRoomStats();
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HereSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HereSpellCast = CMClass.getAbility("Prop_HereSpellCast");
				HereSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", HereSpellCast.accountForYourself()));
				A2 = ((Ability) HereSpellCast.copyOf());
				A2.setMiscText((HereSpellCast).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HereSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", HereSpellCast.accountForYourself()));
				A2 = ((Ability) HereSpellCast.copyOf());
				A2.setMiscText((HereSpellCast).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				CMLib.tracking().walk(mobs[1], Directions.UP, false, false);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				CMLib.tracking().walk(mobs[1], Directions.DOWN, false, false);
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HereSpellCast.setMiscText(semiSpellList() + "MASK=-Human");
				mob.tell(L("Test:"+what+"-3: @x1", HereSpellCast.accountForYourself()));
				A2 = ((Ability) HereSpellCast.copyOf());
				A2.setMiscText((HereSpellCast).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				CMLib.tracking().walk(mobs[1], Directions.UP, false, false);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				CMLib.tracking().walk(mobs[1], Directions.DOWN, false, false);
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_SpellAdder"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability SpellAdder = CMClass.getAbility("Prop_SpellAdder");
				SpellAdder.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", SpellAdder.accountForYourself()));
				R2.addNonUninvokableEffect(SpellAdder);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				if (effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_UseSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability UseSpellCast = CMClass.getAbility("Prop_UseSpellCast"); // put
																						// IN
				UseSpellCast.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", UseSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getItem("SmallSack"), UseSpellCast, mobs[0], null, 0);
				I = CMClass.getItem("StdFood");
				mobs[0].addItem(I);
				C = CMClass.getCommand("Put");
				C.execute(mobs[0], new XVector<String>("Put", "Food", "Sack"), metaFlags);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				R.recoverRoomStats();

				reset(mobs, backups, R, IS, R2);
				UseSpellCast.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", UseSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getItem("SmallSack"), UseSpellCast, mobs[0], mobs[1], 0);
				I = CMClass.getItem("StdFood");
				mobs[0].addItem(I);
				C = CMClass.getCommand("Put");
				C.execute(mobs[0], new XVector<String>("Put", "Food", "Sack"), metaFlags);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				I = CMClass.getItem("StdFood");
				mobs[1].addItem(I);
				C = CMClass.getCommand("Put");
				C.execute(mobs[1], new XVector<String>("Put", "Food", "Sack"), metaFlags);
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				R.recoverRoomStats();
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_UseSpellCast2"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability UseSpellCast2 = CMClass.getAbility("Prop_UseSpellCast2"); // EAT
				UseSpellCast2.setMiscText(semiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", UseSpellCast2.accountForYourself()));
				IS = giveTo(CMClass.getItem("StdFood"), UseSpellCast2, mobs[0], null, 0);
				C = CMClass.getCommand("Eat");
				C.execute(mobs[0], new XVector<String>("Eat", "ALL"), metaFlags);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				R.recoverRoomStats();

				reset(mobs, backups, R, IS, R2);
				UseSpellCast2.setMiscText(semiSpellList() + "MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", UseSpellCast2.accountForYourself()));
				IS = giveTo(CMClass.getItem("StdFood"), UseSpellCast2, mobs[0], mobs[1], 0);
				C = CMClass.getCommand("Eat");
				C.execute(mobs[0], new XVector<String>("Eat", "ALL"), metaFlags);
				if (!effectCheck(spells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				C = CMClass.getCommand("Eat");
				C.execute(mobs[1], new XVector<String>("Eat", "ALL"), metaFlags);
				if (effectCheck(spells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				R.recoverRoomStats();
			}
			if(what.equalsIgnoreCase("metaflags")||what.equalsIgnoreCase("all"))
			{
				final StringBuffer str=new StringBuffer("");
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_AS))
					str.append(L(" AS "));
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_FORCED))
					str.append(L(" FORCED "));
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_MPFORCED))
					str.append(L(" MPFORCED "));
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_ORDER))
					str.append(L(" ORDERED "));
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_POSSESSED))
					str.append(L(" POSSESSED "));
				if(CMath.bset(metaFlags,MUDCmdProcessor.METAFLAG_SNOOPED))
					str.append(L(" SNOOPED "));
				mob.tell(str.toString());
			}
			if(what.equalsIgnoreCase("cmparms")||what.equalsIgnoreCase("all"))
			{
				List<String> V = CMParms.parseAny("blah~BLAH~BLAH!", '~', true);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~", 0).equals("blah~BLAH~BLAH!~"))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				V = CMParms.parseAny("blah~~", '~', true);
				if (V.size() != 1)
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (!V.get(0).equals("blah"))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}

				V = CMParms.parseAny("blah~~", '~', false);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~", 0).equals("blah~~~"))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				V = CMParms.parseAny("blah~~BLAH~~BLAH!", "~~", true);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~~", 0).equals("blah~~BLAH~~BLAH!~~"))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}

				V = CMParms.parseAny("blah~~~~", "~~", true);
				if (V.size() != 1)
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (!V.get(0).equals("blah"))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}

				V = CMParms.parseAny("blah~~~~", "~~", false);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-11"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~~", 0).equals("blah~~~~~~"))
				{
					mob.tell(L("Error:"+what+"-12"));
					return false;
				}

				V = CMParms.parseSentences("blah. blahblah. poo");
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-13"));
					return false;
				}
				if (!V.get(0).equals("blah."))
				{
					mob.tell(L("Error:"+what+"-14:@x1", V.get(0)));
					return false;
				}
				if (!V.get(1).equals("blahblah."))
				{
					mob.tell(L("Error:"+what+"-15:@x1", V.get(1)));
					return false;
				}
				if (!V.get(2).equals("poo"))
				{
					mob.tell(L("Error:"+what+"-16:@x1", V.get(2)));
					return false;
				}

				V = CMParms.parseAny("blah~BLAH~BLAH!~", '~', true);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-17"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~", 0).equals("blah~BLAH~BLAH!~"))
				{
					mob.tell(L("Error:"+what+"-18"));
					return false;
				}

				V = CMParms.parseAny("blah~~BLAH~~BLAH!~~", "~~", true);
				if (V.size() != 3)
				{
					mob.tell(L("Error:"+what+"-19"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~~", 0).equals("blah~~BLAH~~BLAH!~~"))
				{
					mob.tell(L("Error:"+what+"-20"));
					return false;
				}

				V = CMParms.parseAny("blah~BLAH~BLAH!~", '~', false);
				if (V.size() != 4)
				{
					mob.tell(L("Error:"+what+"-21"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~", 0).equals("blah~BLAH~BLAH!~~"))
				{
					mob.tell(L("Error:"+what+"-22"));
					return false;
				}

				V = CMParms.parseAny("blah~~BLAH~~BLAH!~~", "~~", false);
				if (V.size() != 4)
				{
					mob.tell(L("Error:"+what+"-23"));
					return false;
				}
				if (!CMParms.combineWithX(V, "~~", 0).equals("blah~~BLAH~~BLAH!~~~~"))
				{
					mob.tell(L("Error:"+what+"-24"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_FightSpellCast"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability FightSpellCast = CMClass.getAbility("Prop_FightSpellCast");
				FightSpellCast.setMiscText(maliciousSemiSpellList());
				mob.tell(L("Test:"+what+"-1: @x1", FightSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), FightSpellCast, mobs[0], null, 1);
				if (effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (effectCheck(maliciousspells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				for (int i = 0; i < 100; i++)
				{
					mobs[1].curState().setHitPoints(1000);
					mobs[0].curState().setHitPoints(1000);
					CMLib.combat().postAttack(mobs[0], mobs[1], mobs[0].fetchWieldedItem());
					if (effectCheck(maliciousspells, mobs[1]))
						break;
				}
				if (!effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				R.recoverRoomStats();

				reset(mobs, backups, R, IS, R2);
				FightSpellCast.setMiscText(maliciousSemiSpellList() + "MASK=-RACE +Human");
				mob.tell(L("Test:"+what+"-2: @x1", FightSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), FightSpellCast, mobs[1], null, 1);
				if (effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (effectCheck(maliciousspells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				for (int i = 0; i < 100; i++)
				{
					mobs[1].curState().setHitPoints(1000);
					mobs[0].curState().setHitPoints(1000);
					CMLib.combat().postAttack(mobs[1], mobs[0], mobs[1].fetchWieldedItem());
					if (effectCheck(maliciousspells, mobs[1]))
						break;
				}
				if (effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				R.recoverRoomStats();

				reset(mobs, backups, R, IS, R2);
				FightSpellCast.setMiscText(maliciousSemiSpellList() + "MASK=-RACE +Human");
				mob.tell(L("Test:"+what+"-3: @x1", FightSpellCast.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), FightSpellCast, mobs[0], null, 1);
				if (effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (effectCheck(maliciousspells, mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				for (int i = 0; i < 100; i++)
				{
					mobs[1].curState().setHitPoints(1000);
					mobs[0].curState().setHitPoints(1000);
					CMLib.combat().postAttack(mobs[0], mobs[1], mobs[0].fetchWieldedItem());
					if (effectCheck(maliciousspells, mobs[1]))
						break;
				}
				if (!effectCheck(maliciousspells, mobs[1]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				R.recoverRoomStats();
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("all_zappers"))
			||(what.equalsIgnoreCase("Prop_HaveZapper"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HaveZapper = CMClass.getAbility("Prop_HaveZapper");
				HaveZapper.setMiscText("-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", HaveZapper.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), HaveZapper, mobs[0], mobs[1], 2);
				CMLib.commands().postGet(mobs[0], null, IS[0], false);
				CMLib.commands().postGet(mobs[1], null, IS[1], false);
				if (!mobs[0].isMine(IS[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (mobs[1].isMine(IS[1]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
			}

			//if(what.equalsIgnoreCase("rsql")) CMLib.percolator().testMQLParsing();

			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("all_zappers"))
			||(what.equalsIgnoreCase("Prop_RideZapper"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs,backups,R,IS,R2);
				final Ability RideZapper=CMClass.getAbility("Prop_RideZapper");
				RideZapper.setMiscText("-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1",RideZapper.accountForYourself()));
				IS=giveTo(CMClass.getItem("Boat"),RideZapper,mobs[0],mobs[1],3);
				msg=CMClass.getMsg(mobs[0],IS[0],null,CMMsg.MSG_MOUNT,L("<S-NAME> mount(s) <T-NAMESELF>."));
				if(R.okMessage(mobs[0],msg))
					R.send(mobs[0],msg);
				msg=CMClass.getMsg(mobs[1],IS[1],null,CMMsg.MSG_MOUNT,L("<S-NAME> mount(s) <T-NAMESELF>."));
				if(R.okMessage(mobs[1],msg))
					R.send(mobs[1],msg);
				if (mobs[0].riding() != IS[0])
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (mobs[1].riding() == IS[1])
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("all_zappers"))
			||(what.equalsIgnoreCase("Prop_WearZapper"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability WearZapper = CMClass.getAbility("Prop_WearZapper");
				WearZapper.setMiscText("-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", WearZapper.accountForYourself()));
				IS = giveTo(CMClass.getWeapon("Sword"), WearZapper, mobs[0], mobs[1], 0);
				msg = CMClass.getMsg(mobs[0], IS[0], null, CMMsg.MSG_WIELD, L("<S-NAME> wield(s) <T-NAMESELF>."));
				if (R.okMessage(mobs[0], msg))
					R.send(mobs[0], msg);
				msg = CMClass.getMsg(mobs[1], IS[1], null, CMMsg.MSG_WIELD, L("<S-NAME> wield(s) <T-NAMESELF>."));
				if (R.okMessage(mobs[1], msg))
					R.send(mobs[1], msg);
				if (IS[0].amWearingAt(Wearable.IN_INVENTORY))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (!IS[1].amWearingAt(Wearable.IN_INVENTORY))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_Resistance"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability Resistance = CMClass.getAbility("Prop_Resistance");
				Resistance.setMiscText("pierce 100% holy 100% acid 30%");
				mob.tell(L("Test:"+what+"-1: @x1", Resistance.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				giveAbility(mobs[0], Resistance);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				Resistance.setMiscText("pierce 100% holy 100% acid 30% MASK=-RACE +DWARF");
				mob.tell(L("Test:"+what+"-2: @x1", Resistance.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				giveAbility(mobs[0], Resistance);
				giveAbility(mobs[1], Resistance);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
			}
			if(what.equalsIgnoreCase("raceweaps"))
			{
				final StringBuffer buf=new StringBuffer("");
				for(final Enumeration<Race> r=CMClass.races();r.hasMoreElements();)
				{
					final Race R1=r.nextElement();
					if((R1!=null)&&(!R1.isGeneric()))
					{
						buf.append(CMStrings.padRight(R1.ID(), 13)).append(" : ");
						buf.append(R1.getNaturalWeapons().length).append(": ");
						for(int i=0;i<R1.getNaturalWeapons().length && i<2;i++)
							buf.append(R1.getNaturalWeapons()[i].name()).append(": ");
						buf.append("\r\n");
					}
				}
				mob.tell(buf.toString());
			}
			if(what.equalsIgnoreCase("racelangs"))
			{
				for(final Enumeration<Race> r=CMClass.races();r.hasMoreElements();)
				{
					final Race R1=r.nextElement();
					if(R1!=null)
					{
						final StringBuffer buf=new StringBuffer("");
						buf.append(CMStrings.padRight(R1.ID(), 13)).append(" : ");
						for(final Quad<String,Integer,Integer,Boolean> Q : R1.culturalAbilities())
						{
							final Ability A=CMClass.getAbilityPrototype(Q.first);
							if(A instanceof Language)
								buf.append("C:").append(A.ID()).append(" ");
						}
						for(final Ability A : R1.racialAbilities(null))
						{
							if(A instanceof Language)
								buf.append("R:").append(A.ID()).append(" ");
						}
						for(final Ability A : R1.racialEffects(null))
						{
							if(A instanceof Language)
								buf.append("E:").append(A.ID()).append(" ");
						}
						mob.tell(buf.toString());
					}
				}
			}

			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HaveResister"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HaveResister = CMClass.getAbility("Prop_HaveResister");
				HaveResister.setMiscText("pierce 100% holy 100% acid 30%");
				mob.tell(L("Test:"+what+"-1: @x1", HaveResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), HaveResister, mobs[0], null, 0);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				IS[0].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HaveResister.setMiscText("pierce 100% holy 100% acid 30% MASK=-RACE +DWARF");
				mob.tell(L("Test:"+what+"-2: @x1", HaveResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), HaveResister, mobs[0], mobs[1], 0);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.moveItemTo(IS[1], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_WearResister"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability WearResister = CMClass.getAbility("Prop_WearResister");
				WearResister.setMiscText("pierce 100% holy 100% acid 30%");
				mob.tell(L("Test:"+what+"-1: @x1", WearResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getWeapon("Sword"), WearResister, mobs[0], null, 1);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				IS[0].unWear();
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				WearResister.setMiscText("pierce 100% holy 100% acid 30% MASK=-RACE +DWARF");
				mob.tell(L("Test:"+what+"-2: @x1", WearResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getWeapon("Sword"), WearResister, mobs[0], mobs[1], 1);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_RideResister"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability RideResister = CMClass.getAbility("Prop_RideResister");
				RideResister.setMiscText("pierce 100% holy 100% acid 30%");
				mob.tell(L("Test:"+what+"-1: @x1", RideResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getItem("Boat"), RideResister, mobs[0], null, 2);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				mobs[0].setRiding(null);
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				RideResister.setMiscText("pierce 100% holy 100% acid 30% MASK=-RACE +DWARF");
				mob.tell(L("Test:"+what+"-2: @x1", RideResister.accountForYourself()));
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getItem("Boat"), RideResister, mobs[0], mobs[1], 2);
				R.recoverRoomStats();
				if (!testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				mobs[0].setRiding(null);
				mobs[1].setRiding(null);
				R.recoverRoomStats();
				if (testResistance(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (testResistance(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HaveAdjuster"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HaveAdjuster = CMClass.getAbility("Prop_HaveAdjuster");
				HaveAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000");
				mob.tell(L("Test:"+what+"-1: @x1", HaveAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), HaveAdjuster, mobs[0], null, 0);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				IS[0].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				HaveAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000 MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", HaveAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), HaveAdjuster, mobs[0], mobs[1], 0);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.moveItemTo(IS[0], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.moveItemTo(IS[1], ItemPossessor.Expire.Never, ItemPossessor.Move.Followers);
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_WearAdjuster"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability WearAdjuster = CMClass.getAbility("Prop_WearAdjuster");
				WearAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000");
				mob.tell(L("Test:"+what+"-1: @x1", WearAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), WearAdjuster, mobs[0], null, 1);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				IS[0].unWear();
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				WearAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000 MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", WearAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getItem("SmallSack"), WearAdjuster, mobs[0], mobs[1], 1);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				IS[0].unWear();
				IS[1].unWear();
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("clock_conversion"))
			||what.equalsIgnoreCase("all"))
			{
				for(int h=1;h<65536;h*=2)
				{
					final TimeClock NOW = (TimeClock)CMLib.time().globalClock().copyOf();
					final TimeClock C1 = (TimeClock)NOW.copyOf();
					final TimeClock C2 = (TimeClock)NOW.copyOf();
					C1.tickTock(h);
					final long millis = System.currentTimeMillis()+C2.deriveMillisAfter(NOW);
					final TimeClock C3 = C1.deriveClock(millis);
					if(!C1.isEqual(C3))
						mob.tell("Error: "+C1.getShortestTimeDescription()+"!="+C3.getShortestTimeDescription());
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_RideAdjuster"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability RideAdjuster = CMClass.getAbility("Prop_RideAdjuster");
				RideAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000");
				mob.tell(L("Test:"+what+"-1: @x1", RideAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				IS = giveTo(CMClass.getItem("Boat"), RideAdjuster, mobs[0], null, 2);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				mobs[0].setRiding(null);
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				RideAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000 MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-1: @x1", RideAdjuster.accountForYourself()));
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				IS = giveTo(CMClass.getItem("Boat"), RideAdjuster, mobs[0], mobs[1], 2);
				R.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				mobs[0].setRiding(null);
				mobs[1].setRiding(null);
				R.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}

			if(what.equalsIgnoreCase("numwords")||what.equalsIgnoreCase("all"))
			{
				final double[] nums = new double[] {
					8, 12, 38, 87, 100, 112, 356, 34000, 45721,
					100000, 871231, 1000000, 12000231, 23982100,
					54.3223
				};
				for(final double n : nums)
					mob.tell(n +" = " + CMLib.english().makeNumberWords(n, 9));
			}

			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_HereAdjuster"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability HereAdjuster = CMClass.getAbility("Prop_HereAdjuster");
				HereAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000");
				mob.tell(L("Test:"+what+"-1: @x1", HereAdjuster.accountForYourself()));
				A2 = ((Ability) HereAdjuster.copyOf());
				A2.setMiscText((HereAdjuster).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-0"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				R2.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				R2.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HereAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000 MASK=-RACE +Dwarf");
				mob.tell(L("Test:"+what+"-2: @x1", HereAdjuster.accountForYourself()));
				A2 = ((Ability) HereAdjuster.copyOf());
				A2.setMiscText((HereAdjuster).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				CMLib.tracking().walk(mobs[1], Directions.UP, false, false);
				R2.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				CMLib.tracking().walk(mobs[1], Directions.DOWN, false, false);
				R2.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}

				reset(mobs, backups, R, IS, R2);
				HereAdjuster.setMiscText("abi+10 gen=F class=Fighter cha+10 man+1000 MASK=-Human");
				mob.tell(L("Test:"+what+"-3: @x1", HereAdjuster.accountForYourself()));
				A2 = ((Ability) HereAdjuster.copyOf());
				A2.setMiscText((HereAdjuster).text());
				R2.addNonUninvokableEffect(A2);
				R2.recoverRoomStats();
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				CMLib.tracking().walk(mobs[1], Directions.UP, false, false);
				R2.recoverRoomStats();
				if (!isAllAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				CMLib.tracking().walk(mobs[0], Directions.DOWN, false, false);
				CMLib.tracking().walk(mobs[1], Directions.DOWN, false, false);
				R2.recoverRoomStats();
				if (isAnyAdjusted(mobs[0]))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
				if (isAnyAdjusted(mobs[1]))
				{
					mob.tell(L("Error:"+what+"-10"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqAlignments"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability reqA = CMClass.getAbility("Prop_ReqAlignments");
				reqA.setMiscText("NOFOL -EVIL");
				R2.addNonUninvokableEffect(reqA);

				CMLib.factions().setAlignment(mobs[0], Faction.Align.EVIL);

				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (mobs[0].location() == R2)
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}

				CMLib.factions().setAlignment(mobs[0], Faction.Align.NEUTRAL);
				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (mobs[0].location() != R2)
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				R.bringMobHere(mobs[0], false);

				reqA.setMiscText("NOFOL -ALL +EVIL");
				CMLib.factions().setAlignment(mobs[0], Faction.Align.NEUTRAL);

				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (mobs[0].location() == R2)
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}

				CMLib.factions().setAlignment(mobs[0], Faction.Align.EVIL);

				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (mobs[0].location() != R2)
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqCapacity"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs, backups, R, IS, R2);
				final Ability reqA = CMClass.getAbility("Prop_ReqCapacity");
				reqA.setMiscText("people=1 weight=100 items=1");
				R2.addNonUninvokableEffect(reqA);
				IS = giveTo(CMClass.getWeapon("Sword"), null, mobs[0], mobs[1], 0);

				CMLib.tracking().walk(mobs[0], Directions.UP, false, false);
				if (mobs[0].location() != R2)
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}

				CMLib.tracking().walk(mobs[1], Directions.UP, false, false);
				if (mobs[1].location() == R2)
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}

				mobs[0].moveItemTo(IS[0]);
				mobs[0].moveItemTo(IS[1]);

				msg = CMClass.getMsg(mobs[0], IS[0], null, CMMsg.MSG_DROP, null);
				if (!R2.okMessage(mobs[0], msg))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				R2.send(mobs[0], msg);

				msg = CMClass.getMsg(mobs[0], IS[1], null, CMMsg.MSG_DROP, null);
				if (R2.okMessage(mobs[0], msg))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqClasses")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqEntry")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqHeight")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqLevels")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqNoMOB")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqPKill")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqRaces")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqStat")))
			{

			}
			if((what.equalsIgnoreCase("all_properties"))
			||(what.equalsIgnoreCase("Prop_ReqTattoo")))
			{

			}
			if((what.equalsIgnoreCase("all_masks"))
			||(what.equalsIgnoreCase("ZAPPER_ANYCLASSLEVEL"))
			||what.equalsIgnoreCase("all"))
			{
				reset(mobs,backups,R,IS,R2);
				final String mask1="-ANYCLASSLEVEL +Gaian +>=30 +Druid +<10";
				final String mask2="+ANYCLASSLEVEL -Gaian ->=30 -Druid -<10";
				final MaskingLibrary.CompiledZMask cmask1 = CMLib.masking().maskCompile(mask1);
				mob.tell(L("Test:"+what+"-1: @x1",CMLib.masking().maskDesc(mask1)));
				if (!CMLib.masking().maskCheck(mask1, mobs[0], true))
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (!CMLib.masking().maskCheck(cmask1, mobs[0], true))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				if (CMLib.masking().maskCheck(mask1, mobs[1], true))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (CMLib.masking().maskCheck(cmask1, mobs[1], true))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				final MaskingLibrary.CompiledZMask cmask2 = CMLib.masking().maskCompile(mask2);
				mob.tell(L("Test:"+what+"-2: @x1", CMLib.masking().maskDesc(mask2)));
				if (CMLib.masking().maskCheck(mask2, mobs[0], true))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (CMLib.masking().maskCheck(cmask2, mobs[0], true))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (!CMLib.masking().maskCheck(mask2, mobs[1], true))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (!CMLib.masking().maskCheck(cmask2, mobs[1], true))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all")||what.equalsIgnoreCase("rtree"))&&(mob.session()!=null))
			{
				final long t1=System.currentTimeMillis();
				final RTree<BoundedObject> tree=new RTree<BoundedObject>();
				final List<BoundedObject> origSet=new Vector<BoundedObject>();
				final List<long[]> samples=new Vector<long[]>();
				final Random r=new Random(System.currentTimeMillis());
				for(int g=0;g<15;g++)
				{
					long gcenterX=r.nextLong();
					if(gcenterX<0)
						gcenterX=gcenterX*-1;
					long gcenterY=r.nextLong();
					if(gcenterY<0)
						gcenterY=gcenterY*-1;
					long gcenterZ=r.nextLong();
					if(gcenterZ<0)
						gcenterZ=gcenterZ*-1;
					final long grp=g;
					for(int i=0;i<100;i++)
					{
						final int dist=r.nextInt(0x0fff);
						final BoundedObject.BoundedCube cube=new BoundedObject.BoundedCube(gcenterX-dist,gcenterX+dist,gcenterY-dist,gcenterY+dist,gcenterZ-dist,gcenterZ+dist);
						final int num=i;
						final BoundedObject obj=new BoundedObject()
						{
							@Override
							public BoundedCube getBounds()
							{
								return cube;
							}

							@Override
							public String toString()
							{
								return "g" + grp + "#" + num;
							}
						};
						origSet.add(obj);
					}
					samples.add(new long[]{gcenterX,gcenterY,gcenterZ});
				}
				final List<BoundedObject> setToAdd=new Vector<BoundedObject>(origSet.size());
				final List<BoundedObject> randomSet=new Vector<BoundedObject>(origSet.size());
				setToAdd.addAll(origSet);
				while(setToAdd.size()>0)
				{
					final BoundedObject O=setToAdd.remove(r.nextInt(setToAdd.size()));
					if((tree.contains(O))||(tree.leafSearch(O)))
					{
						mob.tell(L("Error:"+what+"-0"));
						return false;
					}
					tree.insert(O);
					if ((!tree.contains(O)) || (!tree.leafSearch(O)))
					{
						mob.tell(L("Error:"+what+"-0.1"));
						return false;
					}
					randomSet.add(O);
				}
				long totalSize=0;
				for(int gnum=0; gnum<samples.size();gnum++)
				{
					setToAdd.clear();
					final long[] pt=samples.get(gnum);
					tree.query(setToAdd,pt[0],pt[1],pt[2]);
					totalSize+=setToAdd.size();
				}
				mob.tell(L("Average set size=@x1, time=@x2, count=@x3",""+(totalSize/samples.size()),""+((System.currentTimeMillis()-t1)),""+tree.count()));
				for(final BoundedObject O : origSet)
				{
					if((!tree.contains(O))||(!tree.leafSearch(O)))
					{
						mob.tell(L("Error:"+what+"-0.2"));
						return false;
					}
				}
				for(int gnum=0; gnum<samples.size();gnum++)
				{
					setToAdd.clear();
					final long[] pt=samples.get(gnum);
					tree.query(setToAdd,pt[0],pt[1],pt[2]);
					if(setToAdd.size()!=100)
					{
						mob.tell(L("Error:"+what+"-1"));
						return false;
					}
					for (int i = 0; i < setToAdd.size(); i++)
					{
						if (!setToAdd.get(i).toString().startsWith("g" + gnum + "#"))
						{
							mob.tell(L("Error:"+what+"-1.1"));
							return false;
						}
					}
				}
				for(int gnum=0; gnum<samples.size();gnum++)
				{
					setToAdd.clear();
					final long[] pt=samples.get(gnum);
					tree.query(setToAdd,pt[0],pt[1],pt[2]);
					for(int i2=0;i2<setToAdd.size();i2++)
					{
						final BoundedObject O2=setToAdd.get(i2);
						if((!tree.contains(O2))||(!tree.leafSearch(O2)))
						{
							mob.tell(L("Error:"+what+"-1.99#@x1/@x2/@x3", "" + gnum, "" + i2, "" + setToAdd.size()));
							return false;
						}
					}
					for(int i2=0;i2<setToAdd.size();i2++) // remove dups
					{
						final BoundedObject O2=setToAdd.get(i2);
						for(int i3=setToAdd.size()-1;i3>i2;i3--) // remove dups
						{
							if(setToAdd.get(i3)==O2)
							{
								setToAdd.remove(i3);
							}
						}
					}
					for(int i=0;i<setToAdd.size();i++)
					{
						final int ct=tree.count();
						final BoundedObject O=setToAdd.get(i);
						if((!tree.contains(O))&&(!tree.leafSearch(O)))
						{
							mob.tell(L("Error:"+what+"-2#@x1/@x2", "" + gnum, "" + i));
							return false;
						}
						if (!tree.remove(O))
						{
							mob.tell(L("Error:"+what+"-3#@x1/@x2", "" + gnum, "" + i));
							return false;
						}
						for (int i2 = i + 1; i2 < setToAdd.size(); i2++)
						{
							final BoundedObject O2 = setToAdd.get(i2);
							if ((!tree.contains(O2)) && (!tree.leafSearch(O2)))
							{
								mob.tell(L("Error:"+what+"-3.2#@x1/@x2/@x3/@x4", "" + gnum, "" + i, "" + i2, "" + setToAdd.size()));
								return false;
							}
						}
						if (tree.contains(O))
						{
							mob.tell(L("Error:"+what+"-4#@x1/@x2", "" + gnum, "" + i));
							return false;
						}
						final List<BoundedObject> dblCheck = new Vector<BoundedObject>(setToAdd.size() - i);
						tree.query(dblCheck, pt[0], pt[1], pt[2]);
						if (dblCheck.contains(O))
						{
							mob.tell(L("Error:"+what+"-5#@x1/@x2", "" + gnum, "" + i));
							return false;
						}
						if (tree.leafSearch(O))
						{
							mob.tell(L("Error:"+what+"-6#@x1/@x2", "" + gnum, "" + i));
							return false;
						}
						if (tree.count() != ct - 1)
						{
							mob.tell(L("Error:"+what+"- 1"));
							return false;
						}
					}
				}

			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("dumpMobBitmaps")))
			{
				mob.tell(CMStrings.padRight(L("Attribute"), 15)+CMStrings.padRight(L("Value"), 11)+CMStrings.padRight(L("Reversed"), 11));
				for(final MOB.Attrib A : MOB.Attrib.values())
					mob.tell(CMStrings.padRight(A.name(), 15)+CMStrings.padRight(A.getBitCode()+"", 11)+CMStrings.padRight(A.isAutoReversed()+"", 11));
			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("parseAny")))
			{
				final String t1="";
				final String t2="]blah";
				final String t3="]blah]";
				final String t4="boo]blah]";
				final String t5="boo]blah]poo";
				if (CMParms.parseAny(t1, "]", true).size() != 0)
				{
					mob.tell(L("Error:"+what+"-0"));
					return false;
				}
				if (CMParms.parseAny(t1, "]", false).size() != 0)
				{
					mob.tell(L("Error:"+what+"-1"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t2, "]", true).toArray(), new Object[] { "blah" }))
				{
					mob.tell(L("Error:"+what+"-2"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t2, "]", false).toArray(), new Object[] { "", "blah" }))
				{
					mob.tell(L("Error:"+what+"-3"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t3, "]", true).toArray(), new Object[] { "blah" }))
				{
					mob.tell(L("Error:"+what+"-4"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t3, "]", false).toArray(), new Object[] { "", "blah", "" }))
				{
					mob.tell(L("Error:"+what+"-5"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t4, "]", true).toArray(), new Object[] { "boo", "blah" }))
				{
					mob.tell(L("Error:"+what+"-6"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t4, "]", false).toArray(), new Object[] { "boo", "blah", "" }))
				{
					mob.tell(L("Error:"+what+"-7"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t5, "]", true).toArray(), new Object[] { "boo", "blah", "poo" }))
				{
					mob.tell(L("Error:"+what+"-8"));
					return false;
				}
				if (!Arrays.deepEquals(CMParms.parseAny(t5, "]", false).toArray(), new Object[] { "boo", "blah", "poo" }))
				{
					mob.tell(L("Error:"+what+"-9"));
					return false;
				}
			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("escapefilterbug")))
			{
				String str=L("@x1@x2^<CHANNEL \"TEST\"^>You TEST 'message'^</CHANNEL^>^N^.",Color.GREY.getANSICode(),Color.BGGREEN.getANSICode());
				str=CMLib.coffeeFilter().fullOutFilter(mob.session(), mob, mob, null, null, str, false);
				str=CMLib.coffeeFilter().fullOutFilter(mob.session(), mob, mob, null, null, str, false);
				str=CMLib.coffeeFilter().fullOutFilter(mob.session(), mob, mob, null, null, str, false);
				mob.tell(str);
			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("racemixing")))
			{
				if((commands.size()<4)||what.equalsIgnoreCase("all"))
				{
					final String mixRace = "Troll";
					final Race firstR=CMClass.getRace(mixRace);
					if(firstR!=null)
					{
						final Race secondR=CMClass.getRace("Human");
						final Race R1=CMLib.utensils().getMixedRace(firstR.ID(),secondR.ID(), false);
						if(R1!=null)
						{
							// well, it didn't crash
							mob.tell(R1.name()+" generated");
						}
					}
				}
				else
				{
					final String mixRace1 = commands.get(2);
					final String mixRace2 = commands.get(3);
					final Race firstR=CMClass.getRace(mixRace1);
					if(firstR!=null)
					{
						final Race secondR=CMClass.getRace(mixRace2);
						if(secondR!=null)
						{
							final Race R1=CMLib.utensils().getMixedRace(firstR.ID(),secondR.ID(), false);
							if(R1!=null)
							{
								// well, it didn't crash
								mob.tell(R1.name()+" generated");
							}
						}
					}
				}
			}


			if(what.equalsIgnoreCase("spacesectorsmap")||what.equalsIgnoreCase("all"))
			{
				final Map<String,BoundedCube> sectors = CMLib.space().getSectorMap();
				for(final String key : sectors.keySet())
					mob.tell(key+": "+sectors.get(key).toString());
			}
			if(what.equalsIgnoreCase("spaceinterception")||what.equalsIgnoreCase("all"))
			{
				final Random rand=new Random(System.currentTimeMillis());
				final GalacticMap space=CMLib.space();
				int passed=0;
				final int numTests=100;
				final int maxTicks=10000;
				for(int tests=0;tests<numTests;tests++)
				{
					final SpaceObject obj1 = (SpaceObject)CMClass.getItem("StdSpaceBody");
					obj1.setRadius(100);
					final SpaceObject obj2 = (SpaceObject)CMClass.getItem("StdSpaceBody");
					obj2.setRadius(100);
					for(int i=0;i<3;i++)
					{
						obj1.coordinates()[i]=rand.nextInt(50000);
						obj2.coordinates()[i]=rand.nextInt(50000);
					}
					obj2.direction()[0]=Math.abs(rand.nextDouble() * Math.PI * 2.0);
					obj2.direction()[1]=Math.abs(rand.nextDouble() * Math.PI);
					obj2.setSpeed(300+rand.nextInt(300));
					final Pair<double[], Long> pair = space.calculateIntercept(obj1, obj2, 1000, maxTicks);
					if(pair == null)
					{
						mob.tell("FAILED! #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
								+": "+CMLib.english().directionDescShort(obj2.direction())
								+": "+obj2.speed());
					}
					else
					{
						final SpaceObject orig2=(SpaceObject)obj2.copyOf();
						obj1.setDirection(pair.first);
						obj1.setSpeed(pair.second.longValue());
						if(!space.canMaybeIntercept(obj1, obj2, maxTicks, pair.second.longValue()))
						{
							mob.tell("Stupid #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
									+": "+CMLib.english().directionDescShort(obj2.direction())
									+": "+obj2.speed());
							continue;
						}
						int atti=0;
						final long radius = obj1.radius()+obj2.radius();
						final double orig = space.getDistanceFrom(obj1, obj2);
						for(;atti<maxTicks;atti++)
						{
							final long[] oldCoords1=obj1.coordinates().clone();
							obj1.setCoords(space.moveSpaceObject(oldCoords1, obj1.direction(), Math.round(obj1.speed())));
							final double x=space.getMinDistanceFrom(oldCoords1, obj1.coordinates(), obj2.coordinates());
							if(x<radius)
								break;
							obj2.setCoords(space.moveSpaceObject(obj2.coordinates(), obj2.direction(), Math.round(obj2.speed())));
						}
						if(atti>=maxTicks)
						{
							mob.tell("Failed #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
									+": "+CMLib.english().directionDescShort(obj2.direction())
									+": "+obj2.speed());
						}
						else
							passed++;
					}
				}
				mob.tell("Passed "+passed+"/"+numTests);
			}
			if(what.equalsIgnoreCase("spaceinterception2")||what.equalsIgnoreCase("all"))
			{
				final Random rand=new Random(System.currentTimeMillis());
				final GalacticMap space=CMLib.space();
				int passed=0;
				final int numTests=100;
				final int maxTicks=10000;
				for(int tests=0;tests<numTests;tests++)
				{
					final SpaceObject obj1 = (SpaceObject)CMClass.getItem("StdSmartTorpedo");
					obj1.setRadius(100);
					((Item)obj1).basePhyStats().setSpeed(CMath.div(1000L, SpaceObject.VELOCITY_LIGHT));
					((Item)obj1).phyStats().setSpeed(CMath.div(100000L, SpaceObject.VELOCITY_LIGHT));
					final SpaceObject obj2 = (SpaceObject)CMClass.getItem("StdSpaceBody");
					obj2.setRadius(100);
					obj1.setKnownTarget(obj2);
					final long radius=obj1.radius()+obj2.radius();
					for(int i=0;i<3;i++)
					{
						obj1.coordinates()[i]=Math.abs(rand.nextInt(50000));
						obj2.coordinates()[i]=Math.abs(rand.nextInt(50000));
					}
					obj2.direction()[0]=Math.abs(rand.nextDouble() * Math.PI * 2);
					obj2.direction()[1]=Math.abs(rand.nextDouble() * Math.PI);
					obj2.setSpeed(300+rand.nextInt(300));
					final Pair<double[], Long> pair = space.calculateIntercept(obj1, obj2, 1000, maxTicks);
					if(pair == null)
					{
						mob.tell("Failed #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
								+": "+CMLib.english().directionDescShort(obj2.direction())
								+": "+obj2.speed());
					}
					else
					{
						final SpaceObject orig2=(SpaceObject)obj2.copyOf();
						obj1.setDirection(pair.first);
						obj1.setSpeed(pair.second.longValue());
						if(!space.canMaybeIntercept(obj1, obj2, maxTicks, pair.second.longValue()))
						{
							mob.tell("Stupid #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
									+": "+CMLib.english().directionDescShort(obj2.direction())
									+": "+obj2.speed());
							continue;
						}
						int atti=0;
						for(;atti<maxTicks;atti++)
						{
							CMLib.space().addObjectToSpace(obj1);
							try
							{
								for(int x=0;x<4;x++)
									obj1.tick(obj1, Tickable.TICKID_BALLISTICK);
							}
							finally
							{
								CMLib.space().delObjectInSpace(obj1);
							}

							final long[] oldCoords1=obj1.coordinates().clone();
							obj1.setCoords(space.moveSpaceObject(oldCoords1, obj1.direction(), Math.round(obj1.speed())));
							final double x=space.getMinDistanceFrom(oldCoords1, obj1.coordinates(), obj2.coordinates());
							if(x<radius)
								break;
							obj2.setCoords(space.moveSpaceObject(obj2.coordinates(), obj2.direction(), Math.round(obj2.speed())));
							space.changeDirection(obj2.direction(), (Math.abs(rand.nextDouble()*.1)),0);
							if(rand.nextBoolean() && (obj2.speed()<500))
								obj2.setSpeed(obj2.speed()+25);
						}
						if(atti>=maxTicks)
						{
							mob.tell("Failed #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
									+": "+CMLib.english().directionDescShort(obj2.direction())
									+": "+obj2.speed());
						}
						else
						{
							//mob.tell("Passed #"+tests+": "+CMLib.english().coordDescShort(obj2.coordinates())
							//		+": "+CMLib.english().directionDescShort(obj2.direction())
							//		+": "+obj2.speed());
							passed++;
						}
					}
				}
				mob.tell("Passed "+passed+"/"+numTests);
			}

			if(what.equalsIgnoreCase("spacecollisions")||what.equalsIgnoreCase("all"))
			{
				// {ship coord}, {speed}, {deg.diff,r},{target},{hit=1}
				final long[][][] tests={
					{{0,0,0},{1000},{},{500,0,0},{1}}, // 0
					{{0,0,0},{1000},{},{0,500,0},{1}},
					{{0,0,0},{1000},{},{0,0,500},{1}},
					{{0,0,0},{1000},{},{-500,0,0},{1}},
					{{0,0,0},{1000},{},{0,-500,0},{1}},
					{{0,0,0},{1000},{},{0,0,-500},{1}},
					{{0,0,0},{1000},{},{500,0,500},{1}},
					{{0,0,0},{1000},{},{0,500,500},{1}},
					{{0,0,0},{1000},{},{500,0,500},{1}},
					{{0,0,0},{1000},{},{-500,0,500},{1}},
					{{0,0,0},{1000},{},{0,-500,500},{1}},
					{{0,0,0},{1000},{},{500,0,-500},{1}}, //11

					{{0,0,0},{480},{},{500,0,0},{0}}, // 12
					{{0,0,0},{480},{},{0,500,0},{0}},
					{{0,0,0},{480},{},{0,0,500},{0}},
					{{0,0,0},{480},{},{-500,0,0},{0}},
					{{0,0,0},{480},{},{0,-500,0},{0}},
					{{0,0,0},{480},{},{0,0,-500},{0}},
					{{0,0,0},{480},{},{500,0,500},{0}},
					{{0,0,0},{480},{},{0,500,500},{0}},
					{{0,0,0},{480},{},{500,0,500},{0}},
					{{0,0,0},{480},{},{-500,0,500},{0}},
					{{0,0,0},{480},{},{0,-500,500},{0}},
					{{0,0,0},{480},{},{500,0,-500},{0}}, // 23

					{{0,0,0},{495},{},{500,0,0},{1}}, // 24
					{{0,0,0},{495},{},{0,500,0},{1}},
					{{0,0,0},{495},{},{0,0,500},{1}},
					{{0,0,0},{495},{},{-500,0,0},{1}},
					{{0,0,0},{495},{},{0,-500,0},{1}},
					{{0,0,0},{495},{},{0,0,-500},{1}},
					{{0,0,0},{700},{},{500,0,500},{1}},
					{{0,0,0},{700},{},{0,500,500},{1}},
					{{0,0,0},{700},{},{500,0,500},{1}},
					{{0,0,0},{700},{},{-500,0,500},{1}},
					{{0,0,0},{700},{},{0,-500,500},{1}},
					{{0,0,0},{700},{},{500,0,-500},{1}}, // 35

					{{0,0,0},{10000},{1,0},{5000,0,0},{0}}, // 36
					{{0,0,0},{10000},{0,1},{0,5000,0},{0}},
					{{0,0,0},{10000},{0,1},{0,0,5000},{0}},
					{{0,0,0},{10000},{1,0},{-5000,0,0},{0}},
					{{0,0,0},{10000},{0,1},{0,-5000,0},{0}},
					{{0,0,0},{10000},{0,1},{0,0,-5000},{0}},
					{{0,0,0},{10000},{1,1},{5000,0,5000},{0}},
					{{0,0,0},{10000},{0,1},{0,5000,5000},{0}},
					{{0,0,0},{10000},{1,0},{5000,0,5000},{0}},
					{{0,0,0},{10000},{1,0},{-5000,0,5000},{0}},
					{{0,0,0},{10000},{1,1},{0,-5000,5000},{0}},
					{{0,0,0},{10000},{1,0},{5000,0,-5000},{0}}, // 47

					{{0,0,0},{10000},{1,0},{5000,0,0},{0}}, // 48
					{{0,0,0},{10000},{0,1},{0,5000,0},{0}},
					{{0,0,0},{10000},{0,1},{0,0,5000},{0}},
					{{0,0,0},{10000},{1,0},{-5000,0,0},{0}},
					{{0,0,0},{10000},{0,1},{0,-5000,0},{0}},
					{{0,0,0},{10000},{0,1},{0,0,-5000},{0}},
					{{0,0,0},{10000},{1,1},{5000,0,5000},{0}},
					{{0,0,0},{10000},{0,1},{0,5000,5000},{0}},
					{{0,0,0},{10000},{1,0},{5000,0,5000},{0}},
					{{0,0,0},{10000},{1,0},{-5000,0,5000},{0}},
					{{0,0,0},{10000},{1,1},{0,-5000,5000},{0}},
					{{0,0,0},{10000},{1,0},{5000,0,-5000},{0}}, // 59

					{{0,0,0},{10000},{179,0},{5000,0,0},{0}}, // 60
					{{0,0,0},{10000},{0,89},{0,5000,0},{0}},
					{{0,0,0},{10000},{0,89},{0,0,5000},{0}},
					{{0,0,0},{10000},{179,0},{-5000,0,0},{0}},
					{{0,0,0},{10000},{0,89},{0,-5000,0},{0}},
					{{0,0,0},{10000},{0,89},{0,0,-5000},{0}},
					{{0,0,0},{10000},{179,89},{5000,0,5000},{0}},
					{{0,0,0},{10000},{0,89},{0,5000,5000},{0}},
					{{0,0,0},{10000},{179,0},{5000,0,5000},{0}},
					{{0,0,0},{10000},{179,0},{-5000,0,5000},{0}},
					{{0,0,0},{10000},{179,89},{0,-5000,5000},{0}},
					{{0,0,0},{10000},{179,0},{5000,0,-5000},{0}}, // 71

					{{0,0,0},{42},{179,1},{620000,0,0},{0}}, // 72
					{{9735, -1357, 707161},{29979245},{5,0},{2000, 1000, 0},{0}}, // 73
					{{9735, -1357, 707161},{29979245},{0,0},{20000, 10000, 5030},{1}}, // 74
				};
				final long[] lt1= new long[]{5, 2, 1};
				final long[] lt2= new long[]{3, 1, -1};
				final long[] lt3= new long[]{0, 2, 3};
				if(CMLib.space().getMinDistanceFrom(lt1,lt2,lt3)<1)
				{
					mob.tell(L("Error: Straight line test failed: "+CMLib.space().getMinDistanceFrom(lt1,lt2,lt3)));
					return false;
				}
				final long[] ld1 = new long[] {175, 193, 117};
				final long[] ld2 = new long[] {197, 218, 134};
				final long[] ld3 = new long[] {0, 0, 0};
				if(CMLib.space().getMinDistanceFrom(ld1,ld2,ld3)<285)
				{
					mob.tell(L("Error: Short line test failed: "+CMLib.space().getMinDistanceFrom(ld1,ld2,ld3)));
					return false;
				}

				final long[] l1= new long[]{3515255, 3877051, -239069815};
				final long[] l2= new long[]{3953445, 4361852, -269041937};
				final long[] l3= new long[]{9734, -1358, 707222};
				if(CMLib.space().getMinDistanceFrom(l1,l2,l3)<239834022)
				{
					mob.tell(L("Error: Straight line test failed: "+CMLib.space().getMinDistanceFrom(l1,l2,l3)));
					return false;
				}
				for(int li=0;li<tests.length;li++)
				{
					final long[][] l=tests[li];
					// l->r
					{
						final long[] shipCoord1 = l[0];
						final long speed = l[1][0];
						final long[] targetCoord=l[3];
						final double[] dir=CMLib.space().getDirection(shipCoord1, targetCoord);
						if(l[2].length>0)
						{
							CMLib.space().changeDirection(dir, Math.toRadians(l[2][0]), 0);
						}
						if(l[2].length==2)
						{
							CMLib.space().changeDirection(dir, Math.toRadians(l[2][1]), 0);
						}
						//Log.debugOut(dir[0]+","+dir[1]);
						final boolean expectHit=l[4][0]>0;
						final long[] shipCoord2=CMLib.space().moveSpaceObject(shipCoord1, dir, speed);
						final double swish=CMLib.space().getMinDistanceFrom(shipCoord1, shipCoord2, targetCoord);
						if(expectHit != (swish < 10))
						{
							mob.tell(L("Error:"+expectHit+"!="+li+"A: minDist="+swish+"/"+(CMLib.space().getDistanceFrom(shipCoord1, targetCoord))));
							Log.debugOut(li+"A) orig coords="+shipCoord1[0]+","+shipCoord1[1]+","+shipCoord1[2]);
							Log.debugOut(li+"A) target coords="+targetCoord[0]+","+targetCoord[1]+","+targetCoord[2]);
							Log.debugOut(li+"A) original direction to target="+dir[0]+","+dir[1]);
							Log.debugOut(li+"A) adjusted direction to target="+dir[0]+","+dir[1]);
							Log.debugOut(li+"A) moved coords="+shipCoord2[0]+","+shipCoord2[1]+","+shipCoord2[2]);
							Log.debugOut(li+"A) original distance="+CMLib.space().getDistanceFrom(shipCoord1, targetCoord));
							Log.debugOut(li+"A) current distance="+CMLib.space().getDistanceFrom(shipCoord2, targetCoord));
							Log.debugOut(li+"A) switsh="+swish);
							//return false;
						}
					}
					// r->l
					{
						final long[] shipCoord1 = l[3];
						final long speed = l[1][0];
						final long[] targetCoord=l[0];
						final double[] dir=CMLib.space().getDirection(shipCoord1, targetCoord);
						if(l[2].length>0)
						{
							CMLib.space().changeDirection(dir, Math.toRadians(l[2][0]), 0);
						}
						if(l[2].length==2)
						{
							CMLib.space().changeDirection(dir, Math.toRadians(l[2][1]), 0);
						}
						//Log.debugOut(dir[0]+","+dir[1]);
						final boolean expectHit=l[4][0]>0;
						final long[] shipCoord2=CMLib.space().moveSpaceObject(shipCoord1, dir, speed);
						final double swish=CMLib.space().getMinDistanceFrom(shipCoord1, shipCoord2, targetCoord);
						if(expectHit != (swish < 10))
						{
							mob.tell(L("Error:"+expectHit+"!="+li+"B: minDist="+swish+"/"+(CMLib.space().getDistanceFrom(shipCoord1, targetCoord))));
							Log.debugOut(li+"B) orig coords="+shipCoord1[0]+","+shipCoord1[1]+","+shipCoord1[2]);
							Log.debugOut(li+"B) target coords="+targetCoord[0]+","+targetCoord[1]+","+targetCoord[2]);
							Log.debugOut(li+"B) original direction to target="+dir[0]+","+dir[1]);
							Log.debugOut(li+"B) adjusted direction to target="+dir[0]+","+dir[1]);
							Log.debugOut(li+"B) moved coords="+shipCoord2[0]+","+shipCoord2[1]+","+shipCoord2[2]);
							Log.debugOut(li+"B) switsh="+swish);
							//return false;
						}
					}
				}
			}

			if(what.equalsIgnoreCase("spacesectors"))
			{
				final long[] coordinates = new long[3];
				for(long x = -SpaceObject.Distance.GalaxyRadius.dm; x<= SpaceObject.Distance.GalaxyRadius.dm; x+= (SpaceObject.Distance.GalaxyRadius.dm / 88))
				{
					coordinates[0] = x;
					final long [] in = CMLib.space().getInSectorCoords(coordinates);
					mob.tell(CMLib.space().getSectorName(coordinates) + ": "+in[0]+","+in[1]+","+in[2]);
				}
				for(long x = -SpaceObject.Distance.GalaxyRadius.dm; x<= SpaceObject.Distance.GalaxyRadius.dm; x+= (SpaceObject.Distance.GalaxyRadius.dm / 88))
				{
					coordinates[1] = x;
					final long [] in = CMLib.space().getInSectorCoords(coordinates);
					mob.tell(CMLib.space().getSectorName(coordinates) + ": "+in[0]+","+in[1]+","+in[2]);
				}
				for(long x = -SpaceObject.Distance.GalaxyRadius.dm; x<= SpaceObject.Distance.GalaxyRadius.dm; x+= (SpaceObject.Distance.GalaxyRadius.dm / 88))
				{
					coordinates[2] = x;
					final long [] in = CMLib.space().getInSectorCoords(coordinates);
					mob.tell(CMLib.space().getSectorName(coordinates) + ": "+in[0]+","+in[1]+","+in[2]);
				}
			}
			if(what.equalsIgnoreCase("spacemovereport1"))
			{
				//List<double[]> results = new ArrayList<double[]>();
				for(double dir0 = 0; dir0 <=Math.PI*2; dir0 += (Math.PI/12.0))
				{
					for(double dir1 = 0; dir1 <(Math.PI+(Math.PI/12)); dir1 += (Math.PI/12.0))
					{
						for(double adir0 = 0; adir0 <=Math.PI*2; adir0 += (Math.PI/12.0))
						{
							for(double adir1 = 0; adir1 <(Math.PI+(Math.PI/12)); adir1 += (Math.PI/12.0))
							{
								if(dir1 > Math.PI)
									dir1=Math.PI;
								if(adir1 > Math.PI)
									adir1=Math.PI;
								final double[] curDir = new double[] {dir0, dir1};
								final double[] accelDir = new double[] {adir0, adir1};
								//double curSpeed = 1000;
								//long newAcceleration = 200;
								//int steps = 0;
								final double totDirDiff = CMLib.space().getAngleDelta(curDir, accelDir);
								//System.out.print("Interesting: ");
								Log.debugOut("Andgle diff between "+Math.round(Math.toDegrees(curDir[0]))+"mk"+Math.round(Math.toDegrees(curDir[1]))
								+"   and   "+Math.round(Math.toDegrees(accelDir[0]))+"mk"+Math.round(Math.toDegrees(accelDir[1]))
								+"       is: "+Math.round(Math.toDegrees(totDirDiff)));
								//double halfPI = Math.PI/2.0;
								/*
								while(!Arrays.equals(curDir, accelDir))
								{
									double oldCurSpeed = curSpeed;
									double curDirDiff = CMLib.space().getAngleDelta(curDir, accelDir);
									double[] oldCurDir=new double[]{curDir[0],curDir[1]};
									curSpeed = CMLib.space().moveSpaceObject(curDir,curSpeed,accelDir, newAcceleration);
									double newDirDiff = CMLib.space().getAngleDelta(curDir, accelDir);
									if((curDirDiff > halfPI)
									&&(newDirDiff > halfPI))
									{
										if(curSpeed > oldCurSpeed)
										{
											Log.debugOut("Step "+steps+" of "+
													Math.round(Math.toDegrees(oldCurDir[0]))+"@"+Math.round(Math.toDegrees(oldCurDir[1]))
													+" -> "
													+Math.round(Math.toDegrees(accelDir[0]))+"@"+Math.round(Math.toDegrees(accelDir[1]))
													+" (angle Diff "+curDirDiff+") went from speed "+oldCurSpeed+" to "+curSpeed);
											//CMLib.space().moveSpaceObject(oldCurDir,oldCurSpeed,accelDir, newAcceleration);
											//curDirDiff = CMLib.space().getAngleDelta(oldCurDir, accelDir);
										}
									}
									else
									if((curDirDiff < halfPI)
									&&(newDirDiff < halfPI))
									{
										if(curSpeed < oldCurSpeed)
										{
											Log.debugOut("Step "+steps+" of "+
													Math.round(Math.toDegrees(oldCurDir[0]))+"@"+Math.round(Math.toDegrees(oldCurDir[1]))
													+" -> "
													+Math.round(Math.toDegrees(accelDir[0]))+"@"+Math.round(Math.toDegrees(accelDir[1]))
													+" (angle Diff "+curDirDiff+") went from speed "+oldCurSpeed+" to "+curSpeed);
										}
									}
									steps++;
								}
								// Test Ideas
								// test whether smaller angle diffs result in fewer steps.
								Log.debugOut(Math.round(Math.toDegrees(totDirDiff))+", ="+steps+"                      fspeed="+curSpeed);
								results.add(new double[]{Math.round(Math.toDegrees(totDirDiff)),steps});
								*/
							}
						}
					}
				}
			}
			if(what.equalsIgnoreCase("spacemovereport2"))
			{
				for(double dir0 = 0; dir0 <=Math.PI*2; dir0 += (Math.PI/12.0))
				{
					if(dir0 == 0)
						dir0 = 0.0001;
					if(dir0 == (2*Math.PI))
						dir0 = (2*Math.PI)-0.0001;
					for(double dir1 = 0; dir1 <(Math.PI+(Math.PI/12)); dir1 += (Math.PI/12.0))
					{
						if(dir1 == 0)
							dir1 = 0.0001;
						if(dir1 == (Math.PI))
							dir1 = (Math.PI)-0.0001;
						for(double adir0 = 0; adir0 <=Math.PI*2; adir0 += (Math.PI/12.0))
						{
							if(adir0 == 0)
								adir0 = 0.0001;
							if(adir0 == (2*Math.PI))
								adir0 = (2*Math.PI)-0.0001;
							for(double adir1 = 0; adir1 <(Math.PI+(Math.PI/12)); adir1 += (Math.PI/12.0))
							{
								if(adir1 == 0)
									adir1 = 0.0001;
								if(adir1 == (Math.PI))
									adir1 = (Math.PI)-0.0001;
								if(dir1 > Math.PI)
									dir1=Math.PI;
								if(adir1 > Math.PI)
									adir1=Math.PI;
								final double[] angle1 = new double[] {dir0, dir1};
								final double[] angle2 = new double[] {adir0, adir1};
								final double[] mid = CMLib.space().getMiddleAngle(angle1, angle2);
								Log.debugOut("Middle angle between "+Math.round(Math.toDegrees(angle1[0]))+"mk"+Math.round(Math.toDegrees(angle1[1]))
								+"   and   "+Math.round(Math.toDegrees(angle2[0]))+"mk"+Math.round(Math.toDegrees(angle2[1]))
								+"       is: "+Math.round(Math.toDegrees(mid[0]))+"mk"+Math.round(Math.toDegrees(mid[1])));
							}
						}
					}
				}
			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("spaceangles")))
			{
				final int[][][] diffsets = new int[][][] {
					{ {90,90}, {80,90},  {10} },
					{ {90,90}, {180,90},  {90} },
					{ {0,90}, {80,90},  {80} },
					{ {90,10}, {90,100},  {90} },
				};
				final int[][][] opps = new int[][][] {
					{ {180,0},  	 {180,180}, {360,0}, {0,0}, {0,180} },
					{ {180,5},  	 {0,175}, {360,175} },
					{ {90,15},  	 {270,165} },
					{ {15,90},  	 {195,90} }
				};
				final int[][][] offsets = new int[][][] {
					{ {90,45},{85,45},   {95,45} },
				};
				final int[][][] midsets = new int[][][] {
					{ {90,45},{80,45},   {85,45} },
				};
				boolean success=true;
				for(int i=0;i<diffsets.length;i++)
				{
					final int[] anglei1 = diffsets[i][0];
					final double[] angle1 = new double[] {Math.toRadians(anglei1[0]), Math.toRadians(anglei1[1])};
					final int[] anglei2 = diffsets[i][1];
					final double[] angle2 = new double[] {Math.toRadians(anglei2[0]), Math.toRadians(anglei2[1])};
					final double diff = CMLib.space().getAngleDelta(angle1, angle2);
					final int[] an = diffsets[i][2];
					final double and = Math.toRadians(an[0]);
					final boolean found = Math.abs(diff - and) < 0.001;
					success = success && found;
					if(!found)
						mob.tell("DIFF Test #"+(i+1)+" failed: "+Math.toDegrees(diff));
					else
						mob.tell("DIFF Test #"+(i+1)+": "+
								CMLib.english().directionDescShort(angle1)+"-"+CMLib.english().directionDescShort(angle2)
									+"="+Math.toDegrees(diff));
				}
				for(int i=0;i<opps.length;i++)
				{
					final int[] anglei = opps[i][0];
					final double[] angle = new double[] {Math.toRadians(anglei[0]), Math.toRadians(anglei[1])};
					final double[] op = CMLib.space().getOppositeDir(angle);
					boolean found=false;
					for(int x=1;x<opps[i].length;x++)
					{
						final int[] compi = opps[i][x];
						final double[] comp = new double[] {Math.toRadians(compi[0]), Math.toRadians(compi[1])};
						if((Math.abs(comp[0]-op[0])<0.001)&&(Math.abs(comp[1]-op[1])<0.001))
							found=true;
					}
					success = success && found;
					if(!found)
						mob.tell("OP Test #"+(i+1)+" failed: "+Math.toDegrees(op[0])+","+Math.toDegrees(op[1]));
				}
				for(int i=0;i<offsets.length;i++)
				{
					final int[] anglei1 = offsets[i][0];
					final double[] angle1 = new double[] {Math.toRadians(anglei1[0]), Math.toRadians(anglei1[1])};
					final int[] anglei2 = offsets[i][1];
					final double[] angle2 = new double[] {Math.toRadians(anglei2[0]), Math.toRadians(anglei2[1])};
					final double[] off = CMLib.space().getOffsetAngle(angle1, angle2);
					final int[] an = offsets[i][2];
					final double[] and = new double[] {Math.toRadians(an[0]), Math.toRadians(an[1])};
					final boolean found = (Math.abs(off[0] - and[0])<0.001) && (Math.abs(off[1] - and[1])<0.001);
					success = success && found;
					if(!found)
						mob.tell("OF Test #"+(i+1)+" failed: "+Math.toDegrees(off[0])+","+Math.toDegrees(off[1]));
				}
				for(int i=0;i<midsets.length;i++)
				{
					final int[] anglei1 = midsets[i][0];
					final double[] angle1 = new double[] {Math.toRadians(anglei1[0]), Math.toRadians(anglei1[1])};
					final int[] anglei2 = midsets[i][1];
					final double[] angle2 = new double[] {Math.toRadians(anglei2[0]), Math.toRadians(anglei2[1])};
					final double[] mid = CMLib.space().getMiddleAngle(angle1, angle2);
					final int[] an = midsets[i][2];
					final double[] and = new double[] {Math.toRadians(an[0]), Math.toRadians(an[1])};
					final boolean found = (Math.abs(mid[0] - and[0])<0.001) && (Math.abs(mid[1] - and[1])<0.001);
					success = success && found;
					if(!found)
						mob.tell("MID Test #"+(i+1)+" failed: "+Math.toDegrees(mid[0])+","+Math.toDegrees(mid[1]));
				}
				if(!success)
					return false;
			}

			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("spacemove")||what.equalsIgnoreCase("spacemoves")))
			{
				final SpaceShip o = (SpaceShip)CMClass.getItem("GenSpaceShip");
				// timtest1
				{
					// pitches 45 & 135
					final long[] startCoords = new long[] {0,0,0};
					o.setCoords(Arrays.copyOf(startCoords, 3));
					o.setDirection(new double[] {Math.PI/8.0, Math.PI*3.0/8.0});
					o.setSpeed(100.0);
					o.setFacing(o.direction());
					CMLib.space().moveSpaceObject(o);
					final long[] midCoord = Arrays.copyOf(o.coordinates(), 3);
					//mob.tell(CMParms.toListString(o.coordinates())+", dist="+CMLib.space().getDistanceFrom(startCoords, midCoord)+"");
					o.setDirection(new double[] {Math.PI/8.0, Math.PI*5.0/8.0});
					o.setSpeed(100.0);
					o.setFacing(o.direction());
					CMLib.space().moveSpaceObject(o);
					//mob.tell(CMParms.toListString(o.coordinates())+", dist="+CMLib.space().getDistanceFrom(midCoord, o.coordinates())+"");
					if(!Arrays.equals(o.coordinates(), new long[] {170,70,0}))
					{
						mob.tell("Error space move: TT1: "+CMParms.toListString(o.coordinates())+", dist="+CMLib.space().getDistanceFrom(midCoord, o.coordinates())+"");
						return false;
					}
				}
				final int accelMoves = 110;
				final int slowMoves = accelMoves - 1;
				final double accel = 3.0;
				for(int i=0;i<100;i++)
				{
					final double[] dir = new double[] { Math.random() * Math.PI*2, Math.random() * Math.PI};
					o.setSpeed(0.0);
					o.setDirection(dir);
					o.setFacing(dir);
					final double[] opDir = CMLib.space().getOppositeDir(dir);
					final double[] opOpDir = CMLib.space().getOppositeDir(opDir);
					if(CMLib.space().getAngleDelta(dir, opOpDir)>0.01)
					{
						final String angles = Math.round(Math.toDegrees(dir[0]))+"mk"+Math.round(Math.toDegrees(dir[1]))
						+".vs."+Math.round(Math.toDegrees(opOpDir[0]))+"mk"+Math.round(Math.toDegrees(opOpDir[1]));
						mob.tell(L("Error: Space move OpDir Fail: "+angles));
						return false;
					}
					double predictedMidDistance=0;
					for(int a=1;a<=accelMoves;a++)
						predictedMidDistance += (accel * a);
					final double predictedMidSpeed = accelMoves * accel;
					double predictedDistance = predictedMidDistance;
					for(int a=accelMoves;a>0;a--)
						predictedDistance += (accel * a);

					final long[] startCoords = Arrays.copyOf(o.coordinates(),3);
					double speed=0.0;
					double distanceTravelled = 0.0;
					for(int a=0;a<accelMoves;a++)
					{
						CMLib.space().accelSpaceObject(o,dir,accel);
						speed += accel;
						final long[] oldCoords = Arrays.copyOf(o.coordinates(), 3);
						CMLib.space().moveSpaceObject(o);
						distanceTravelled+=speed;
						final double traveledDistance = CMLib.space().getDistanceFrom(startCoords, o.coordinates());
						final double distDiff = Math.abs(traveledDistance - distanceTravelled);
						final double speedDiff = Math.abs(o.speed() - speed);
						if((speedDiff>1)||(!Arrays.equals(dir, o.direction()))||(distDiff > 2))
						{
							final String s = spaceMoveError("mid-mid"+i+"."+a, o, dir, speedDiff, speed, distDiff, traveledDistance, distanceTravelled, oldCoords);
							mob.tell(s);
							return false;
						}
						if(Math.abs(traveledDistance - distanceTravelled)>1)
						{
							mob.tell(""+Math.abs(traveledDistance-distanceTravelled));
						}
						distanceTravelled=traveledDistance;
					}
					final double midTraveledDistance = CMLib.space().getDistanceFrom(startCoords, o.coordinates());
					final double midDistDiff = Math.abs(midTraveledDistance - predictedMidDistance);
					final double midSpeedDiff = Math.abs(o.speed() - predictedMidSpeed);
					if((midSpeedDiff>accel+1)||(!Arrays.equals(dir, o.direction()))||(midDistDiff > accel))
					{
						final String s = spaceMoveError("mid-"+i, o, dir, midSpeedDiff, predictedMidSpeed, midDistDiff, midTraveledDistance, predictedMidDistance, o.coordinates());
						mob.tell(s);
						return false;
					}
					for(int a=0;a<slowMoves;a++)
					{
						CMLib.space().accelSpaceObject(o,opDir,accel);
						speed -= accel;
						final long[] oldCoords = Arrays.copyOf(o.coordinates(), 3);
						final double otraveledDistance = CMLib.space().getDistanceFrom(startCoords, o.coordinates());
						CMLib.space().moveSpaceObject(o);
						distanceTravelled+=speed;
						final double traveledDistance = CMLib.space().getDistanceFrom(startCoords, o.coordinates());
						final double distDiff = Math.abs(traveledDistance - distanceTravelled);
						final double speedDiff = Math.abs(o.speed() - speed);
						if((speedDiff>1)||(!Arrays.equals(dir, o.direction()))||(distDiff > accel))
						{
							final String s = spaceMoveError("mid-end"+i+"."+a, o, dir, speedDiff, speed, distDiff, traveledDistance, distanceTravelled, oldCoords);
							distanceTravelled-=speed;
							mob.tell(s+"\n\rdist:"+otraveledDistance+"+"+speed+"="+traveledDistance);
							CMLib.space().moveSpaceObject(o);
							return false;
						}
						distanceTravelled=traveledDistance;
					}
					final double traveledDistance = CMLib.space().getDistanceFrom(startCoords, o.coordinates());
					final double distDiff = Math.abs(traveledDistance - predictedDistance);
					final double speedDiff = Math.abs(o.speed() - accel);
					if((speedDiff>accel+1)
					||(!Arrays.equals(dir, o.direction()))
					||(distDiff > slowMoves))
					{
						final String s = spaceMoveError("test "+i, o, dir, speedDiff, speed, distDiff, traveledDistance, predictedDistance, startCoords);
						mob.tell(s);
						return false;
					}
				}
			}

			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("spaceturn")||what.equalsIgnoreCase("spaceturns")))
			{
				final double[][][] tests = new double[][][] {
					{ {1000.0}, {Math.PI, Math.PI-.2}, {Math.PI/2.0, Math.PI-.2} },
					{ {1000.0}, {Math.PI, Math.PI/2.0}, {Math.PI/2.0, Math.PI/2.0} },
					{ {1000.0}, {Math.PI/2.0, Math.PI/2.0}, {Math.PI+Math.PI/2.0, Math.PI/2.0}},
					{ {100.0}, {Math.PI, Math.PI-.2}, {Math.PI/2.0, Math.PI-.2} },
					{ {100.0}, {Math.PI, Math.PI/2.0}, {Math.PI/2.0, Math.PI/2.0} },
					{ {100.0}, {Math.PI/2.0, Math.PI/2.0}, {Math.PI+Math.PI/2.0, Math.PI/2.0}},
				};
				final SpaceShip o = (SpaceShip)CMClass.getItem("GenSpaceShip");
				for(int t=0;t<tests.length;t++)
				{
					final double[][] test = tests[t];
					final double speed = test[0][0];
					final double[] startDir = test[1];
					final double[] accelDir = test[2];
					final long[] startCoords = new long[] {0,0,0};
					o.setCoords(Arrays.copyOf(startCoords, 3));
					o.setDirection(startDir);
					o.setSpeed(speed);
					o.setFacing(accelDir);
					int i=0;
					for(i=0;i<1000;i++)
					{
						CMLib.space().accelSpaceObject(o,accelDir,3.0);
						final double d = CMLib.space().getAngleDelta(o.direction(), accelDir);
						/*
						mob.tell(Math.toDegrees(
								 o.direction()[0])+","+Math.toDegrees(o.direction()[1])
								+"  -->  "
								+Math.toDegrees(accelDir[0])+","+Math.toDegrees(accelDir[1])
								+" === "+d);
						*/
						if(d<0.1)
							break;
					}
					if((test.length>3)&&(i!=test[3][0]))
					{
						mob.tell("Error: Space turn test "+t+", test failed: "+i+"!="+test[2][0]);
						return false;
					}
					else
						mob.tell("Info: Space turn test "+t+", test result: "+i);
				}
			}

			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("notrandom")))
			{
				mob.tell(""+CMath.NotRandomHigh.nextInt());
				mob.tell(""+CMath.NotRandomHigh.nextInt(10));
				mob.tell(""+CMath.NotRandomHigh.nextLong());
				mob.tell(""+CMath.NotRandomHigh.nextDouble());
			}

			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("cmuniqsortsvec")))
			{
				final String[] tests=new String[]{
					"Elvish",
					"Fighter_FastSlinging",
					"Common",
					"Proficiency_Sling",
					"Skill_Befriend",
					"Skill_Haggle",
					"Skill_Recall",
					"Skill_Write",
					"Song_Detection",
					"Song_Nothing",
					"Specialization_EdgedWeapon",
					"SignLanguage",
					"Song_Seeing",
					"Specialization_EdgedWeapon",
					"FireBuilding",
					"Song_Valor",
					"Specialization_EdgedWeapon",
					"Fighter_FastSlinging",
					"FireBuilding",
					"Proficiency_Sling",
					"FireBuilding",
					"Song_Charm",
					"Fighter_FastSlinging",
					"FireBuilding",
					"Proficiency_Sling",
					"Specialization_Sword",
					"Butchering",
					"Skill_Befriend",
					"Skill_Haggle",
					"Song_Armor",
					"Song_Babble",
					"Song_Charm",
					"Song_Seeing",
					"FireBuilding",
					"Play_Break",
					"Play_Tempo",
					"Skill_Befriend",
					"Skill_Recall",
					"Skill_Write",
					"Song_Nothing",
					"Specialization_Ranged",
					"Fighter_FastSlinging",
				};
				for(int y=0;y<100;y++)
				for(int x=0;x<100;x++)
				{
					final java.util.concurrent.atomic.AtomicInteger counter=new java.util.concurrent.atomic.AtomicInteger(0);
					final CMUniqSortSVec<Ability> vec = new CMUniqSortSVec<Ability>();
					final int delayType = x/30;
					for(int i=0;i<tests.length;i++)
					{
						final Ability A1=CMClass.getAbility(tests[i]);
						if(delayType == 0)
						{
							final Ability A=A1;
							if(vec.find(A.ID())==null)
								vec.addElement(A);
							counter.incrementAndGet();
						}
						else
						{
							CMLib.threads().executeRunnable(new Runnable()
							{
								final Ability A=A1;
								@Override
								public void run()
								{
									if(delayType == 2)
										CMLib.s_sleep(CMLib.dice().roll(1, 10, -1));
									if(vec.find(A.ID())==null)
										vec.addElement(A);
									counter.incrementAndGet();
								}
							});
						}
					}
					while(counter.get() < tests.length)
						CMLib.s_sleep(10);
					final Set<String> found=new TreeSet<String>();
					for(int i=0;i<vec.size();i++)
						if(found.contains(vec.get(i).ID()))
						{
							mob.tell(L("Error:"+what+"-"+i+"("+vec.get(i).ID()+")"));
							return false;
						}
						else
						{
							found.add(vec.get(i).ID());
						}
					if(vec.size() != found.size())
					{
						mob.tell(L("Error:"+what+"-"));
						return false;
					}
				}
				mob.tell(L("Dun"));
			}
			if((what.equalsIgnoreCase("all"))
			||(what.equalsIgnoreCase("clans")))
			{
				reset(mobs,backups,R,IS,R2);
				mobs[0].setPlayerStats((PlayerStats)CMClass.getCommon("DefaultPlayerStats"));
				mobs[1].setPlayerStats((PlayerStats)CMClass.getCommon("DefaultPlayerStats"));
				final Session S1=(Session)CMClass.getCommon("FakeSession");
				final Session S2=(Session)CMClass.getCommon("FakeSession");
				S1.initializeSession(null,Thread.currentThread().getThreadGroup().getName(), "MEMORY");
				S2.initializeSession(null,Thread.currentThread().getThreadGroup().getName(), "MEMORY");
				mobs[0].setSession(S1);
				mobs[1].setSession(S2);
				try
				{
					S1.getPreviousCMD().add("Y");
					S1.getPreviousCMD().add("TESTCLAN");
					S1.getPreviousCMD().add("Y");

				}
				finally
				{
					mobs[0].setSession(null);
					mobs[1].setSession(null);
					mobs[0].setPlayerStats(null);
					mobs[1].setPlayerStats(null);
				}
			}
			if(what.equalsIgnoreCase("matches"))
			{
				final Session S=mob.session();
				String word="x";
				while((word.length()>0)
				&&(S!=null)
				&&(!S.isStopped()))
				{
					word = S.prompt("Enter a word: ");
					if(word.trim().length()==0)
						break;
					String match="x";
					while((match.length()>0)
					&&(S!=null)
					&&(!S.isStopped()))
					{
						match = S.prompt("Enter a matcher: ");
						if(match.trim().length()==0)
							break;
						S.println("match="+CMStrings.matches(word, match, false));
					}
				}
			}
			if(what.equalsIgnoreCase("citycheck"))
			{
				for(final Enumeration<Area> a=CMLib.map().areas();a.hasMoreElements();)
				{
					final Area A=a.nextElement();
					final boolean newCheck;
					if(A.getAreaIStats()[Area.Stats.COUNTABLE_ROOMS.ordinal()]>0)
						newCheck = CMLib.law().isACity(A);
					else
						newCheck=false;
					if(newCheck)
					mob.tell(newCheck+"("+(CMath.div(A.getAreaIStats()[Area.Stats.CITY_ROOMS.ordinal()],A.getAreaIStats()[Area.Stats.COUNTABLE_ROOMS.ordinal()]))+") : "+A.Name());
				}
			}
			if(what.equalsIgnoreCase("timsvalue"))
			{
				final ArrayList<ItemCraftor> V=new ArrayList<ItemCraftor>();
				final Vector<ItemCraftor> craftingSkills=new Vector<ItemCraftor>();
				for(final Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
				{
					final Ability A=e.nextElement();
					if(A instanceof ItemCraftor)
						V.add((ItemCraftor)A.copyOf());
				}
				while(V.size()>0)
				{
					int lowest=Integer.MAX_VALUE;
					ItemCraftor lowestA=null;
					for(int i=0;i<V.size();i++)
					{
						final ItemCraftor A=V.get(i);
						final int ii=CMLib.ableMapper().lowestQualifyingLevel(A.ID());
						if(ii<lowest)
						{
							lowest=ii;
							lowestA=A;
						}
					}
					if(lowestA==null)
						lowestA=V.get(0);
					if(lowestA!=null)
					{
						V.remove(lowestA);
						craftingSkills.add(lowestA);
					}
					else
						break;
				}
				for(final ItemCraftor cA : craftingSkills)
				{
					if(cA.ID().toLowerCase().indexOf("jewel")>=0)
						continue;
					final List<Integer> rscs = cA.myResources();
					if(rscs.size()==0)
						continue;
					final Map<Integer,int[]> mats = new TreeMap<Integer,int[]>();
					for(final Integer mI : rscs)
					{
						final Integer matI=Integer.valueOf(mI.intValue()&RawMaterial.MATERIAL_MASK);
						if(!mats.containsKey(matI))
							mats.put(matI, new int[] {0});
						mats.get(matI)[0]++;
					}
					int mostCommonMat=-1;
					int mostCommonIs=0;
					for(final Integer matI : mats.keySet())
						if(mats.get(matI)[0]>mostCommonIs)
						{
							mostCommonMat=matI.intValue();
							mostCommonIs = mats.get(matI)[0];
						}
					int mostCommonRsc = -1;
					switch(mostCommonMat)
					{
					case RawMaterial.MATERIAL_PAPER:
						mostCommonRsc=RawMaterial.RESOURCE_PAPER;
						break;
					case RawMaterial.MATERIAL_CLOTH:
						mostCommonRsc=RawMaterial.RESOURCE_COTTON;
						break;
					case RawMaterial.MATERIAL_ROCK:
						mostCommonRsc=RawMaterial.RESOURCE_STONE;
						break;
					case RawMaterial.MATERIAL_PRECIOUS:
						mostCommonRsc=RawMaterial.RESOURCE_GEM;
						break;
					case RawMaterial.MATERIAL_SYNTHETIC:
						mostCommonRsc=RawMaterial.RESOURCE_PLASTIC;
						break;
					case RawMaterial.MATERIAL_GLASS:
						mostCommonRsc=RawMaterial.RESOURCE_GLASS;
						break;
					case RawMaterial.MATERIAL_FLESH:
						mostCommonRsc=RawMaterial.RESOURCE_BEEF;
						break;
					case RawMaterial.MATERIAL_LEATHER:
						mostCommonRsc=RawMaterial.RESOURCE_LEATHER;
						break;
					case RawMaterial.MATERIAL_METAL:
					case RawMaterial.MATERIAL_MITHRIL:
						mostCommonRsc=RawMaterial.RESOURCE_IRON;
						break;
					case RawMaterial.MATERIAL_WOODEN:
						mostCommonRsc=RawMaterial.RESOURCE_OAK;
						break;
					default:
						mostCommonRsc=RawMaterial.RESOURCE_OAK;
						break;
					}
					final List<ItemCraftor.CraftedItem> l=cA.craftAllItemSets(mostCommonRsc,false);
					if(V!=null)
					{
						final String fileName = "skills/"+cA.getRecipeFilename();
						final StringBuffer recipeFile = Resources.getFileResource(fileName, true);
						boolean didSomething = false;
						for(final ItemCraftor.CraftedItem L: l)
						{
							I = L.item;
							if(I.baseGoldValue()<=0)
								continue;
							if(I instanceof FalseLimb)
								continue;
							final String rscName = RawMaterial.CODES.NAME(I.material()).toLowerCase();
							final int oldValue = I.baseGoldValue();
							I.setBaseValue(CMLib.itemBuilder().calculateBaseValue(I));
							boolean modified = false;
							if(CMLib.itemBuilder().calculateBaseValue(I)*3<oldValue)
							{
								mob.tell(I.name()+" is "+oldValue+" which is > 3 * "+I.baseGoldValue());
							}

							if(oldValue > I.baseGoldValue() *2)
							{
								String findName = CMLib.english().removeArticleLead(
										CMStrings.replaceAll(I.name(), rscName+" ", "% "));
								if(findName.startsWith("designer "))
									findName=findName.substring(9);
								final int x = recipeFile.toString().toLowerCase().indexOf(findName.toLowerCase());
								if(x >= 0)
								{
									final int eol = recipeFile.indexOf("\n",x+1);
									final String lineStr = recipeFile.substring(x,eol);
									final String srchStr = ""+oldValue;
									final int valDex = lineStr.indexOf("\t"+srchStr+"\t");
									if(valDex > 0)
									{
										recipeFile.replace(x+valDex+1, x+valDex+srchStr.length()+1, ""+I.baseGoldValue());
										didSomething=true;
										modified = true;
									}

								}
								if(modified)
									mob.tell(I.name()+" value is "+oldValue+" but fixed to "+I.baseGoldValue()+": "+cA.ID()+": "+rscName);
								else
									mob.tell(findName+" ("+x+") value is "+oldValue+" but SHOULD be "+I.baseGoldValue()+": "+cA.ID()+": "+rscName);
							}
						}
						if(didSomething)
						{
							final StringBuffer finalData = new StringBuffer(
								CMStrings.replaceAll(recipeFile.toString(), "\n\r", "\n")
							);
							Resources.updateFileResource(fileName, finalData);
						}
					}
				}
			}
			reset(mobs,backups,R,IS,R2);
			CMLib.map().emptyRoom(R2,null,true);
			R2.destroy();
			R.rawDoors()[Directions.UP]=upRoom;
			R.setRawExit(Directions.UP,upExit);
			mobs[0].destroy();
			mobs[1].destroy();
			R.recoverRoomStats();
			mob.tell(L("Test(s) passed or completed."));
		}
		else
			mob.tell(L("Test what?"));
		return false;
	}

	@Override
	public boolean canBeOrdered()
	{
		return false;
	}

	@Override
	public boolean securityCheck(final MOB mob)
	{
		return CMSecurity.isASysOp(mob);
	}
}
