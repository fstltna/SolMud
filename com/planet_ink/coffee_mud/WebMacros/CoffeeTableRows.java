package com.planet_ink.coffee_mud.WebMacros;

import com.planet_ink.coffee_web.interfaces.*;
import com.planet_ink.coffee_mud.core.interfaces.*;
import com.planet_ink.coffee_mud.core.*;
import com.planet_ink.coffee_mud.core.collections.*;
import com.planet_ink.coffee_mud.Abilities.interfaces.*;
import com.planet_ink.coffee_mud.Areas.interfaces.*;
import com.planet_ink.coffee_mud.Behaviors.interfaces.*;
import com.planet_ink.coffee_mud.CharClasses.interfaces.*;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.Commands.interfaces.Command;
import com.planet_ink.coffee_mud.Common.interfaces.*;
import com.planet_ink.coffee_mud.Exits.interfaces.*;
import com.planet_ink.coffee_mud.Items.interfaces.*;
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
public class CoffeeTableRows extends StdWebMacro
{
	@Override
	public String name()
	{
		return "CoffeeTableRows";
	}

	//HEADER, FOOTER, DATERANGE, DATESTART, DATEEND, LEVELSUP, DIVORCES, BIRTHS, MARRIAGES, PURGES, CLASSCHANGES,
	//PKDEATHS, DEATHS, NEWPLAYERS, TOTALHOURS, AVERAGETICKS, AVERAGEONLINE, MOSTONLINE, AVERAGEPONLINE, MOSTPONLINE, LOGINS,
	@Override
	public String runMacro(final HTTPRequest httpReq, String parm, final HTTPResponse httpResp)
	{
		if(parm.length()==0)
		{
			parm="DATERANGE&LOGINS&MOSTONLINE&AVERAGEONLINE&MOSTPONLINE&AVERAGEPONLINE&TOTALHOURS"
				+ "&NEWPLAYERS&DEATHS&PKDEATHS&CLASSCHANGES&PURGES&MARRIAGES&BIRTHS&DIVORCES";
		}
		final java.util.Map<String,String> parms=parseParms(parm);
		final PairSVector<String,String> orderedParms=parseOrderedParms(parm,false);
		String header=parms.get("HEADER");
		if(header==null)
			header="";
		String footer=parms.get("FOOTER");
		if(footer==null)
			footer="";
		int scale=CMath.s_int(httpReq.getUrlParameter("SCALE"));
		if(scale<=0)
			scale=1;
		int days=CMath.s_int(httpReq.getUrlParameter("DAYS"));
		days=days*scale;
		if(days<=0)
			days=0;
		String code=httpReq.getUrlParameter("CODE");
		if((code==null)||(code.length()==0))
			code="*";

		final Calendar ENDQ=Calendar.getInstance();
		ENDQ.add(Calendar.DATE,-days);
		ENDQ.set(Calendar.HOUR_OF_DAY,23);
		ENDQ.set(Calendar.MINUTE,59);
		ENDQ.set(Calendar.SECOND,59);
		ENDQ.set(Calendar.MILLISECOND,000);
		CMLib.coffeeTables().update();
		final List<CoffeeTableRow> V=CMLib.coffeeTables().readRawStats(ENDQ.getTimeInMillis()-1,0);
		if(V.size()==0)
		{
			return "";
		}
		final StringBuffer table=new StringBuffer("");
		final Calendar C=Calendar.getInstance();
		C.set(Calendar.HOUR_OF_DAY,23);
		C.set(Calendar.MINUTE,59);
		C.set(Calendar.SECOND,59);
		C.set(Calendar.MILLISECOND,999);
		long curTime=C.getTimeInMillis();
		long lastCur=0;
		String colspan="";
		if(parms.containsKey("SOCUSE"))
		{
			final List<String> classes = new ArrayList<String>(1);
			if(code.startsWith("S"))
			{
				final Social S=CMLib.socials().fetchSocial(code.substring(1),true);
				classes.add((S==null)?null:S.baseName());
			}
			if(classes.size()==0)
				classes.addAll(CMLib.socials().getSocialsBaseList());

			final long[][] totals=new long[classes.size()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new LinkedList<CoffeeTableRow>();
				if(V.size()==1)
				{
					final CoffeeTableRow T=V.get(0);
					set.add(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				for(final CoffeeTableRow T : set)
				{
					for(int x=0;x<classes.size();x++)
						T.totalUp("S"+classes.get(x),totals[x]);
				}
				if(scale==0)
					break;
			}
			int x=-1;
			String S=null;
			while(x<classes.size())
			{
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if(key.equalsIgnoreCase("NEXTSOCID"))
					{
						x++;
						if(x>=classes.size())
							S=null;
						else
						{
							S=classes.get(x);
							table.append("<TD"+colspan+">"+header+S+footer+"</TD>");
						}
					}
					else
					if(key.equalsIgnoreCase("SOCUSE"))
					{
						if(S!=null)
							table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_SOCUSE]+footer+"</TD>");
					}
				}
				table.append("</TR>");
			}
		}
		else
		if(parms.containsKey("CMDUSE"))
		{
			final List<Command> classes = new ArrayList<Command>(1);
			if(code.startsWith("M"))
				classes.add(CMClass.getCommand(code.substring(1)));
			if(classes.size()==0)
			{
				for(final Enumeration<Command> s= CMClass.commands();s.hasMoreElements();)
					classes.add(s.nextElement());
			}

			final long[][] totals=new long[classes.size()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new LinkedList<CoffeeTableRow>();
				if(V.size()==1)
				{
					final CoffeeTableRow T=V.get(0);
					set.add(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				for(final CoffeeTableRow T : set)
				{
					for(int x=0;x<classes.size();x++)
						T.totalUp("M"+classes.get(x).ID(),totals[x]);
				}
				if(scale==0)
					break;
			}
			int x=-1;
			Command S=null;
			while(x<classes.size())
			{
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if(key.equalsIgnoreCase("NEXTCMDID"))
					{
						x++;
						if(x>=classes.size())
							S=null;
						else
						{
							S=classes.get(x);
							table.append("<TD"+colspan+">"+header+S.ID()+footer+"</TD>");
						}
					}
					else
					if(key.equalsIgnoreCase("CMDUSE"))
					{
						if(S!=null)
							table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_CMDUSE]+footer+"</TD>");
					}
				}
				table.append("</TR>");
			}
		}
		else
		if(parms.containsKey("SKILLUSE"))
		{
			final List<CharClass> classes = new ArrayList<CharClass>(1);
			if(code.startsWith("C"))
				classes.add(CMClass.getCharClass(code.substring(1)));
			else
			if(code.startsWith("B"))
			{
				final String codeSub1=code.substring(1);
				for(final Enumeration<CharClass> c=CMClass.charClasses();c.hasMoreElements();)
				{
					final CharClass C1=c.nextElement();
					if(C1.baseClass().equalsIgnoreCase(codeSub1))
						classes.add(C1);
				}
			}
			if(classes.size()==0)
				classes.add(null);

			final List<Ability> allSkills=new ArrayList<Ability>();
			int onlyAbilityTypes=-1;
			int onlyAbilityDomains=-1;
			final String typeName=parms.get("ABLETYPE");
			if(typeName!=null)
			{
				onlyAbilityTypes=CMParms.indexOf(Ability.ACODE.DESCS, typeName.toUpperCase().trim());
				if(onlyAbilityTypes<0)
					onlyAbilityTypes=CMParms.indexOf(Ability.ACODE.DESCS_, typeName.toUpperCase().trim());
			}
			final String domainName=parms.get("ABLEDOMAIN");
			if(domainName!=null)
			{
				final int domainIndex=CMParms.indexOf(Ability.DOMAIN.DESCS, domainName.toUpperCase().trim());
				if(domainIndex>=0)
					onlyAbilityDomains=domainIndex<<5;
			}
			for(final CharClass charC : classes)
			{
				for(final Enumeration<Ability> e=CMClass.abilities();e.hasMoreElements();)
				{
					final Ability A=e.nextElement();
					if(((charC==null)
						||((CMLib.ableMapper().getQualifyingLevel(charC.ID(),true,A.ID())>=0)&&(!allSkills.contains(A))))
					&&((onlyAbilityTypes<0)||((A.classificationCode()&Ability.ALL_ACODES)==onlyAbilityTypes))
					&&((onlyAbilityDomains<0)||((A.classificationCode()&Ability.ALL_DOMAINS)==onlyAbilityDomains)))
						allSkills.add(A);
				}
			}
			final long[][] totals=new long[allSkills.size()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new LinkedList<CoffeeTableRow>();
				if(V.size()==1)
				{
					final CoffeeTableRow T=V.get(0);
					set.add(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				for(final CoffeeTableRow T : set)
				{
					for(int x=0;x<allSkills.size();x++)
						T.totalUp("A"+allSkills.get(x).ID().toUpperCase(),totals[x]);
				}
				if(scale==0)
					break;
			}
			int x=-1;
			Ability A=null;
			while(x<allSkills.size())
			{
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if(key.equalsIgnoreCase("NEXTSKILLID"))
					{
						x++;
						if(x>=allSkills.size())
							A=null;
						else
						{
							A=allSkills.get(x);
							table.append("<TD"+colspan+">"+header+A.ID()+footer+"</TD>");
						}
					}
					else
					if(key.equalsIgnoreCase("SKILLUSE"))
					{
						if(A!=null)
							table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_SKILLUSE]+footer+"</TD>");
					}
				}
				table.append("</TR>");
			}
		}
		else
		if(parms.containsKey("QUESTNAME")||parms.containsKey("QUESTRPT"))
		{
			final List<Quest> sortedQuests=new XVector<Quest>(CMLib.quests().enumQuests());
			Collections.sort(sortedQuests,new Comparator<Quest>()
			{
				@Override
				public int compare(final Quest o1, final Quest o2)
				{
					return o1.name().toLowerCase().compareTo(o2.name().toLowerCase());
				}
			});
			final long[][] totals=new long[sortedQuests.size()][CoffeeTableRow.STAT_TOTAL];
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-(scale));
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new ArrayList<CoffeeTableRow>();
				if(V.size()==1)
				{
					final CoffeeTableRow T=V.get(0);
					set.add(T);
					V.remove(0);
				}
				else
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				if(set.size()==0)
				{
					set.addAll(V);
					V.clear();
				}
				for(int s=0;s<set.size();s++)
				{
					final CoffeeTableRow T=set.get(s);
					for(int x=0;x<sortedQuests.size();x++)
						T.totalUp("U"+T.tagFix(sortedQuests.get(x).name()),totals[x]);
				}
				if(scale==0)
					break;
			}
			for(int x=0;x<sortedQuests.size();x++)
			{
				final Quest Q=sortedQuests.get(x);
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if(key.equalsIgnoreCase("QUESTNAME"))
						table.append("<TD"+colspan+">"+header+Q.name()+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("DATERANGE"))
						table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+" - "+CMLib.time().date2DateString(lastCur-1)+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("DATESTART"))
						table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(curTime+1)+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("DATEEND"))
						table.append("<TD"+colspan+">"+header+CMLib.time().date2DateString(lastCur)+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("FAILEDSTART"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTFAILEDSTART]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("TIMESTART"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTTIMESTART]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("TIMESTOP"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTTIMESTOP]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("STOP"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTSTOP]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("ACCEPTED"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTACCEPTED]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("FAILED"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTFAILED]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("SUCCESS"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTSUCCESS]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("DROPPED"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTDROPPED]+footer+"</TD>");
					else
					if(key.equalsIgnoreCase("STARTATTEMPT"))
						table.append("<TD"+colspan+">"+header+totals[x][CoffeeTableRow.STAT_QUESTSTARTATTEMPT]+footer+"</TD>");
				}
				table.append("</TR>");
			}
		}
		else
		if(parms.containsKey("AREANAME")||parms.containsKey("AREARPT"))
		{
			final List<CoffeeTableRow> set=new ArrayList<CoffeeTableRow>();
			for(int v=V.size()-1;v>=0;v--)
			{
				final CoffeeTableRow T=V.get(v);
				if((T.startTime()>ENDQ.getTimeInMillis())&&((T.startTime()+TimeManager.MILI_DAY)<=curTime))
				{
					set.add(T);
					V.remove(v);
				}
			}
			for(final Enumeration<Area> a=CMLib.map().areas();a.hasMoreElements();)
			{
				final Area A=a.nextElement();
				if((!CMath.bset(A.flags(),Area.FLAG_INSTANCE_CHILD))&&(!(A instanceof SpaceObject)))
				{
					code = "X"+A.Name().toUpperCase().replace(' ','_');
					final long[] totals=new long[CoffeeTableRow.STAT_TOTAL];
					long highestCOnline=0;
					long numberCOnlineTotal=0;
					long highestPOnline=0;
					long numberPOnlineTotal=0;
					long numberOnlineCounter=0;
					for(int s=0;s<set.size();s++)
					{
						final CoffeeTableRow T=set.get(s);
						T.totalUp(code,totals);
						if(T.highestCharsOnline()>highestCOnline)
							highestCOnline=T.highestCharsOnline();
						numberCOnlineTotal+=T.numberCharsOnlineTotal();
						if(T.highestOnline()>highestPOnline)
							highestPOnline=T.highestOnline();
						numberPOnlineTotal+=T.numberOnlineTotal();
						numberOnlineCounter+=T.numberOnlineCounter();
					}
					final long minsOnline=(totals[CoffeeTableRow.STAT_TICKSONLINE]*CMProps.getTickMillis())/(1000*60);
					double avgOnline=(numberOnlineCounter>0)?CMath.div(numberCOnlineTotal,numberOnlineCounter):0.0;
					avgOnline=CMath.div(Math.round(avgOnline*10.0),10.0);
					double avgPOnline=(numberOnlineCounter>0)?CMath.div(numberPOnlineTotal,numberOnlineCounter):0.0;
					avgPOnline=CMath.div(Math.round(avgPOnline*10.0),10.0);
					table.append("<TR>");
					for(int i=0;i<orderedParms.size();i++)
					{
						final String key=orderedParms.getFirst(i);
						if(key.equals("COLSPAN"))
							colspan=" COLSPAN="+orderedParms.getSecond(i);
						else
						if(key.equals("AREANAME"))
							table.append("<TD" + colspan + ">" + header + A.Name() + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("DATERANGE"))
							table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + " - " + CMLib.time().date2DateString(lastCur - 1) + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("DATESTART"))
							table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("DATEEND"))
							table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(lastCur) + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("LOGINS"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_LOGINS] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("MOSTONLINE"))
							table.append("<TD" + colspan + ">" + header + highestCOnline + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("AVERAGEONLINE"))
							table.append("<TD" + colspan + ">" + header + avgOnline + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("MOSTPONLINE"))
							table.append("<TD" + colspan + ">" + header + highestPOnline + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("AVERAGEPONLINE"))
							table.append("<TD" + colspan + ">" + header + avgPOnline + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("AVERAGETICKS"))
							table.append("<TD" + colspan + ">" + header + ((totals[CoffeeTableRow.STAT_LOGINS] > 0) ? (minsOnline / totals[CoffeeTableRow.STAT_LOGINS]) : 0) + "</TD>");
						else
						if (key.equalsIgnoreCase("TOTALHOURS"))
							table.append("<TD" + colspan + ">" + header + minsOnline + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("NEWPLAYERS"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_NEWPLAYERS] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("DEATHS"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_DEATHS] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("PKDEATHS"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_PKDEATHS] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("CLASSCHANGES"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_CLASSCHANGE] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("PURGES"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_PURGES] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("MARRIAGES"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_MARRIAGES] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("BIRTHS"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_BIRTHS] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("DIVORCES"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_DIVORCES] + footer + "</TD>");
						else
						if (key.equalsIgnoreCase("LEVELSUP"))
							table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_LEVELSGAINED] + footer + "</TD>");
					}
				}
				table.append("</TR>");
				if(scale==0)
					break;
			}
		}
		else
		if(parms.containsKey("CRIMERPT"))
		{
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-scale);
				curTime=C2.getTimeInMillis();
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new LinkedList<CoffeeTableRow>();
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				final long[] totals=new long[CoffeeTableRow.STAT_TOTAL];
				for(final CoffeeTableRow T :set)
					T.totalUp(code,totals);
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if (key.equalsIgnoreCase("DATERANGE"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + " - " + CMLib.time().date2DateString(lastCur - 1) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DATESTART"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DATEEND"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(lastCur) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("WARRANTS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_WARRANTS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("ARRESTS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_ARRESTS] + "</TD>");
					else
					if (key.equalsIgnoreCase("PAROLES"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_PAROLES] + "</TD>");
					else
					if (key.equalsIgnoreCase("JAILINGS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_JAILINGS] + "</TD>");
					else
					if (key.equalsIgnoreCase("EXECUTIONS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_EXECUTIONS] + "</TD>");
				}
				table.append("</TR>");
				if(scale==0)
					break;
			}
		}
		else
		{
			while((V.size()>0)&&(curTime>(ENDQ.getTimeInMillis())))
			{
				lastCur=curTime;
				final Calendar C2=Calendar.getInstance();
				C2.setTimeInMillis(curTime);
				C2.add(Calendar.DATE,-scale);
				curTime=C2.getTimeInMillis();
				C2.set(Calendar.HOUR_OF_DAY,23);
				C2.set(Calendar.MINUTE,59);
				C2.set(Calendar.SECOND,59);
				C2.set(Calendar.MILLISECOND,999);
				curTime=C2.getTimeInMillis();
				final List<CoffeeTableRow> set=new LinkedList<CoffeeTableRow>();
				for(int v=V.size()-1;v>=0;v--)
				{
					final CoffeeTableRow T=V.get(v);
					if((T.startTime()>curTime)&&(T.endTime()<=lastCur))
					{
						set.add(T);
						V.remove(v);
					}
				}
				final long[] totals=new long[CoffeeTableRow.STAT_TOTAL];
				long highestCOnline=0;
				long numberCOnlineTotal=0;
				long highestPOnline=0;
				long numberPOnlineTotal=0;
				long numberOnlineCounter=0;
				for(final CoffeeTableRow T :set)
				{
					T.totalUp(code,totals);
					if(T.highestCharsOnline()>highestCOnline)
						highestCOnline=T.highestCharsOnline();
					numberCOnlineTotal+=T.numberCharsOnlineTotal();
					if(T.highestOnline()>highestPOnline)
						highestPOnline=T.highestOnline();
					numberPOnlineTotal+=T.numberOnlineTotal();
					numberOnlineCounter+=T.numberOnlineCounter();
				}
				final long minsOnline=(totals[CoffeeTableRow.STAT_TICKSONLINE]*CMProps.getTickMillis())/(1000*60);
				double avgOnline=(numberOnlineCounter>0)?CMath.div(numberCOnlineTotal,numberOnlineCounter):0.0;
				avgOnline=CMath.div(Math.round(avgOnline*10.0),10.0);
				double avgPOnline=(numberOnlineCounter>0)?CMath.div(numberPOnlineTotal,numberOnlineCounter):0.0;
				avgPOnline=CMath.div(Math.round(avgPOnline*10.0),10.0);
				table.append("<TR>");
				for(int i=0;i<orderedParms.size();i++)
				{
					final String key=orderedParms.getFirst(i);
					if(key.equals("COLSPAN"))
						colspan=" COLSPAN="+orderedParms.getSecond(i);
					else
					if (key.equalsIgnoreCase("DATERANGE"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + " - " + CMLib.time().date2DateString(lastCur - 1) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DATESTART"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(curTime + 1) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DATEEND"))
						table.append("<TD" + colspan + ">" + header + CMLib.time().date2DateString(lastCur) + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("LOGINS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_LOGINS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("MOSTONLINE"))
						table.append("<TD" + colspan + ">" + header + highestCOnline + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("AVERAGEONLINE"))
						table.append("<TD" + colspan + ">" + header + avgOnline + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("MOSTPONLINE"))
						table.append("<TD" + colspan + ">" + header + highestPOnline + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("AVERAGEPONLINE"))
						table.append("<TD" + colspan + ">" + header + avgPOnline + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("AVERAGETICKS"))
						table.append("<TD" + colspan + ">" + header + ((totals[CoffeeTableRow.STAT_LOGINS] > 0) ? (minsOnline / totals[CoffeeTableRow.STAT_LOGINS]) : 0) + "</TD>");
					else
					if (key.equalsIgnoreCase("TOTALHOURS"))
						table.append("<TD" + colspan + ">" + header + minsOnline + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("NEWPLAYERS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_NEWPLAYERS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DEATHS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_DEATHS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("PKDEATHS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_PKDEATHS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("CLASSCHANGES"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_CLASSCHANGE] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("PURGES"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_PURGES] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("MARRIAGES"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_MARRIAGES] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("BIRTHS"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_BIRTHS] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("DIVORCES"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_DIVORCES] + footer + "</TD>");
					else
					if (key.equalsIgnoreCase("LEVELSUP"))
						table.append("<TD" + colspan + ">" + header + totals[CoffeeTableRow.STAT_LEVELSGAINED] + footer + "</TD>");
				}
				table.append("</TR>");
				if(scale==0)
					break;
			}
		}
		return clearWebMacros(table);
	}
}
