package com.planet_ink.coffee_mud.Abilities.Languages;
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
public class Goblinese extends StdLanguage
{
	@Override
	public String ID()
	{
		return "Goblinese";
	}

	private final static String localizedName = CMLib.lang().L("Goblinese");

	@Override
	public String name()
	{
		return localizedName;
	}

	@Override
	public String getTranslationVerb()
	{
		return "squeek(s)";
	}

	public static List<String[]> wordLists=null;
	public Goblinese()
	{
		super();
	}

	@Override
	public List<String[]> translationLists(final String language)
	{
		if(wordLists==null)
		{
			final String[] one={"i","klpt","ih","g"};
			final String[] two={"te","il","ag","go"};
			final String[] three={"nik","rem","tit","nip","pop","pon","ipi","wip","pec"};
			final String[] four={"perp","merp","nerp","pein","noog","gobo","koer","werp","terp","tert","grlt","Jrl","gran","kert"};
			final String[] five={"whamb","thwam","nipgo","pungo","upoin","krepe","tungo","pongo","twang","hrgap","splt","krnch","baam","poww"};
			final String[] six={"tawthak","krsplt","palpep","poopoo","dungdung","owwie","greepnak","tengak","grnoc","pisspiss","phlyyytt","plllb","hrangnok","ticktick","nurang"};
			wordLists=new Vector<String[]>();
			wordLists.add(one);
			wordLists.add(two);
			wordLists.add(three);
			wordLists.add(four);
			wordLists.add(five);
			wordLists.add(six);
		}
		return wordLists;
	}

	private static final Map<String,String> exactWords=new TreeMap<String,String>();

	@Override
	public Map<String, String> translationHash(final String language)
	{
		if((exactWords!=null)&&(exactWords.size()>0))
			return exactWords;
		exactWords.put("0","di");
		exactWords.put("A","ah");
		exactWords.put("ABLE","gafnahkk");
		exactWords.put("ABOUT","bah");
		exactWords.put("ABOVE","hat");
		exactWords.put("ACCORDING","gi");
		exactWords.put("AGAIN","vi");
		exactWords.put("AGREE","yoess");
		exactWords.put("ALIVE","gongki");
		exactWords.put("ALL","jay");
		exactWords.put("ALRIGHT","yoess");
		exactWords.put("RIGHT","yoess");
		exactWords.put("ALLIANCE","zmat'him");
		exactWords.put("ALLY","zmat");
		exactWords.put("ALONGSIDE","kho");
		exactWords.put("ALSO","dok");
		exactWords.put("AM","ko");
		exactWords.put("AND","tw");
		exactWords.put("ANIMAL","achi");
		exactWords.put("ANYONE","chay");
		exactWords.put("ANYTHING","chez");
		exactWords.put("APPRENTICE","podri");
		exactWords.put("ARE","ko");
		exactWords.put("AREA","rrey");
		exactWords.put("AS","gon");
		exactWords.put("ASK","banrahk");
		exactWords.put("ASSIST","pod");
		exactWords.put("ASSISTANCE","podum");
		exactWords.put("ASSISTANT","podri");
		exactWords.put("AT","hm");
		exactWords.put("BACK","let");
		exactWords.put("BAD","neo");
		exactWords.put("BATTLE","znavo");
		exactWords.put("BAY","relah");
		exactWords.put("BE","kok");
		exactWords.put("BEAST","achi");
		exactWords.put("BECAUSE","ckhw");
		exactWords.put("BECOME","kok");
		exactWords.put("BEFRIEND","zmat");
		exactWords.put("BEGIN","plint");
		exactWords.put("BEGINNING","plin");
		exactWords.put("BEHIND","let");
		exactWords.put("BELOW","get");
		exactWords.put("BENEATH","get");
		exactWords.put("BIG","loakh");
		exactWords.put("BLACK","eske");
		exactWords.put("BLEED","gurr");
		exactWords.put("BLOOD","gurrde");
		exactWords.put("BLUE","meh");
		exactWords.put("BOTTOM","get");
		exactWords.put("BRIGHT","epli");
		exactWords.put("BROTHER","kokihn");
		exactWords.put("BROWN","ikm");
		exactWords.put("BUT","vw");
		exactWords.put("BY","gi");
		exactWords.put("BYE","dzo");
		exactWords.put("CAN","gafnahkk");
		exactWords.put("CAST","uvoj");
		exactWords.put("CHIEF","kore");
		exactWords.put("COME","ling");
		exactWords.put("RETURN","umkloer");
		exactWords.put("COMMON","mantan");
		exactWords.put("TONGUE","mantan");
		exactWords.put("COMPLICATED","efichi");
		exactWords.put("CONCEDE","yoess");
		exactWords.put("CONCEPT","ezo");
		exactWords.put("CURE","vuor");
		exactWords.put("CURIOUS","blomgreh");
		exactWords.put("DARK","eske");
		exactWords.put("DARKNESS","eske");
		exactWords.put("DAUGHTER","getrihn");
		exactWords.put("DAY","hn");
		exactWords.put("DEAD","kurri");
		exactWords.put("DEATH","kurrte");
		exactWords.put("DEEP","legin");
		exactWords.put("DIE","kurr");
		exactWords.put("DIFFICULT","efichi");
		exactWords.put("DIRECT","noreyg");
		exactWords.put("DIRT","shikm");
		exactWords.put("DISCOVER","kurflig");
		exactWords.put("DO","uft");
		exactWords.put("DWARF","dahrfw");
		exactWords.put("EASY","imshi");
		exactWords.put("EAT","chip");
		exactWords.put("ELF","elfw");
		exactWords.put("END","skeh");
		exactWords.put("ENEMY","neot'h");
		exactWords.put("ENJOY","gyur");
		exactWords.put("ENTER","ling");
		exactWords.put("ENTRANCE","lin");
		exactWords.put("ENTRUST","obranyuj");
		exactWords.put("EVERYONE","jay");
		exactWords.put("EVERYTHING","jez");
		exactWords.put("EXIT","keh");
		exactWords.put("FAERY","feyri");
		exactWords.put("FAIL","poep");
		exactWords.put("FAR","eysah");
		exactWords.put("FAT","nerri");
		exactWords.put("FATHER","hatkihn");
		exactWords.put("FAVOR","rekyom");
		exactWords.put("FEATHER","pwm");
		exactWords.put("FEMALE","rihn");
		exactWords.put("FIGHT","znav");
		exactWords.put("FIND","kurflig");
		exactWords.put("FINISH","sket");
		exactWords.put("FIRE","ikh");
		exactWords.put("FIX","yokok");
		exactWords.put("FOLLOW","podr");
		exactWords.put("FOLLOWER","podri");
		exactWords.put("FOOD","chipo");
		exactWords.put("FOR","cho");
		exactWords.put("FORGET","nrvidoj");
		exactWords.put("FORWARD","lit");
		exactWords.put("FREE","jihf");
		exactWords.put("FREEDOM","jihfo");
		exactWords.put("FRIEND","zmat'h");
		exactWords.put("FRIENDSHIP","zmat'him");
		exactWords.put("FROM","che");
		exactWords.put("FRONT","lit");
		exactWords.put("FULFILL","ghrakh");
		exactWords.put("FULFILLED","ghrakh");
		exactWords.put("FULL","meyrlin");
		exactWords.put("GET","tuhg");
		exactWords.put("GIFT","tuh");
		exactWords.put("GIVE","tuhv");
		exactWords.put("GO","loer");
		exactWords.put("GOBLIN","goblin");
		exactWords.put("GOBLINESE","goblintan");
		exactWords.put("GOING","po");
		exactWords.put("GOOD","yoe");
		exactWords.put("EVENING","hayke");
		exactWords.put("MORNING","hayli");
		exactWords.put("MIDNIGHT","dzoke");
		exactWords.put("GOODBYE","dzojo");
		exactWords.put("GRAY","pliske");
		exactWords.put("GREEN","epo");
		exactWords.put("GREMLIN","grihm");
		exactWords.put("GROUND","shah");
		exactWords.put("GUARD","ckhut");
		exactWords.put("GUARDIAN","ckhuttro");
		exactWords.put("GUIDE","norey");
		exactWords.put("HAPPY","ashi");
		exactWords.put("HARD","efichi");
		exactWords.put("HAS","ko");
		exactWords.put("HAVE","uk");
		exactWords.put("HE","kah");
		exactWords.put("HEAL","vuor");
		exactWords.put("HELLO","hay");
		exactWords.put("PLEASED","hayay");
		exactWords.put("MEET","hayay");
		exactWords.put("HELP","pod");
		exactWords.put("HELPER","podri");
		exactWords.put("HER","rah");
		exactWords.put("HERE","me");
		exactWords.put("HEY","hay");
		exactWords.put("HI","hay");
		exactWords.put("HIM","kah");
		exactWords.put("HIS","kahif");
		exactWords.put("HOBGOBLIN","hoblin");
		exactWords.put("HOW","fw");
		exactWords.put("HOWEVER","vw");
		exactWords.put("HUMAN","man");
		exactWords.put("SPEECH","mantan");
		exactWords.put("HUSBAND","kihnkahn");
		exactWords.put("I","ngah");
		exactWords.put("APOLOGIZE","jahsht");
		exactWords.put("SORRY","jahsht");
		exactWords.put("IF","lun");
		exactWords.put("IMP","ihm");
		exactWords.put("IN","wm");
		exactWords.put("INSTRUCT","tuhdoch");
		exactWords.put("INSTRUCTOR","tuhdoche");
		exactWords.put("INTRUDE","jrach");
		exactWords.put("INTRUDER","jrachw");
		exactWords.put("IS","ko");
		exactWords.put("IT","ez");
		exactWords.put("ITS","ezif");
		exactWords.put("JESTER","speyah");
		exactWords.put("JOKESTER","speyah");
		exactWords.put("KNOW","doj");
		exactWords.put("KOBOLD","achilin");
		exactWords.put("LAKE","relah");
		exactWords.put("LANGUAGE","tan");
		exactWords.put("LARGE","loakh");
		exactWords.put("LEAD","noreyg");
		exactWords.put("LEADER","kore");
		exactWords.put("LEARN","port");
		exactWords.put("LEARNER","port'h");
		exactWords.put("LEAVE","keng");
		exactWords.put("LESS","get");
		exactWords.put("LIFE","gongko");
		exactWords.put("LIGHT","epli");
		exactWords.put("LIKE","gon");
		exactWords.put("LIQUID","leyfah");
		exactWords.put("LIVE","gongk");
		exactWords.put("LIVING","gongki");
		exactWords.put("LONG","loakh");
		exactWords.put("AGO","eysah");
		exactWords.put("LOOK","jok");
		exactWords.put("LOOKS","turflig");
		exactWords.put("LOSE","kurfeckh");
		exactWords.put("LOTS","baso");
		exactWords.put("LOVE","gyurak");
		exactWords.put("MAGIC","uvojn");
		exactWords.put("MAGICAL","uvoji");
		exactWords.put("MAKE","neot");
		exactWords.put("MALE","kihn");
		exactWords.put("MARK","shtihk");
		exactWords.put("MASTER","norey");
		exactWords.put("ME","ngah");
		exactWords.put("MINE","ngahif");
		exactWords.put("MINERAL","tikm");
		exactWords.put("MISPLACE","kurfeckh");
		exactWords.put("MOON","meyre");
		exactWords.put("MORE","hat");
		exactWords.put("MOTHER","hatrihn");
		exactWords.put("MOUNTAIN","ikrro");
		exactWords.put("MUCH","baso");
		exactWords.put("MUD","rekm");
		exactWords.put("MUST","ckhiz");
		exactWords.put("MY","ngahif");
		exactWords.put("MYSTICAL","uvoji");
		exactWords.put("NEED","ckhiz");
		exactWords.put("NIGHT","meske");
		exactWords.put("NO","nah");
		exactWords.put("NOBODY","di");
		exactWords.put("NOONE","di");
		exactWords.put("NOSY","blomgreh");
		exactWords.put("NOT","nr");
		exactWords.put("NOTHING","di");
		exactWords.put("NOW","koy");
		exactWords.put("NUMBER","doh");
		exactWords.put("OBJECT","ezo");
		exactWords.put("OCEAN","relahle");
		exactWords.put("CARES","nyah");
		exactWords.put("OF","o");
		exactWords.put("OGRE","ogrh");
		exactWords.put("OKAY","yoess");
		exactWords.put("ON","am");
		exactWords.put("ONCE","vi");
		exactWords.put("BILLION","trw");
		exactWords.put("HUNDRED","toy");
		exactWords.put("MILLION","tri");
		exactWords.put("THOUSAND","tro");
		exactWords.put("OR","lw");
		exactWords.put("ORANGE","igh");
		exactWords.put("ORC","orkh");
		exactWords.put("OUR","jadif");
		exactWords.put("OVER","bah");
		exactWords.put("OWN","uk");
		exactWords.put("PLACE","rrey");
		exactWords.put("PLANT","ngepo");
		exactWords.put("PLEASE","tiki");
		exactWords.put("PLUME","pwm");
		exactWords.put("POND","relah");
		exactWords.put("POSSESS","uk");
		exactWords.put("POSSESSION","uktuh");
		exactWords.put("PRACTICE","gihspw");
		exactWords.put("PRESENT","tuh");
		exactWords.put("PRIZE","tyesui");
		exactWords.put("PROMISE","obranyu");
		exactWords.put("PROTECT","ckhut");
		exactWords.put("PURPLE","mede");
		exactWords.put("QUESTION","banrakh");
		exactWords.put("RECIEVE","tuhg");
		exactWords.put("RED","urde");
		exactWords.put("RELATE","gong");
		exactWords.put("RELATED","gong");
		exactWords.put("RELATION","gongah");
		exactWords.put("RELATIVE","gongah");
		exactWords.put("REMEMBER","vidoj");
		exactWords.put("REPAIR","yokok");
		exactWords.put("REPLACE","jepwov");
		exactWords.put("REPLACEMENT","jepwovw");
		exactWords.put("REQUEST","banrahk");
		exactWords.put("RESCUE","pod");
		exactWords.put("RESCUER","podri");
		exactWords.put("REWARD","tyesui");
		exactWords.put("RIVER","reloh");
		exactWords.put("ROCK","tikm");
		exactWords.put("RUN","reloh");
		exactWords.put("RUNNING","reloh");
		exactWords.put("SACRED","urpfah");
		exactWords.put("SAD","eckhi");
		exactWords.put("SAND","shikm");
		exactWords.put("SAVE","pod");
		exactWords.put("SEA","relahle");
		exactWords.put("SEARCH","turflig");
		exactWords.put("SECRET","urpfah");
		exactWords.put("SEE","jok");
		exactWords.put("SERIOUS","legin");
		exactWords.put("SHALL","po");
		exactWords.put("SHE","rah");
		exactWords.put("SHORT","loen");
		exactWords.put("SIDE","bit");
		exactWords.put("SIMPLE","imshi");
		exactWords.put("SIMULTANEOUSLY","kahnz");
		exactWords.put("SISTER","korihn");
		exactWords.put("SKINNY","zifti");
		exactWords.put("SKY","mey");
		exactWords.put("SLAVE","zruhg");
		exactWords.put("SMALL","loen");
		exactWords.put("SO","nw");
		exactWords.put("SOME","choy");
		exactWords.put("SOMEONE","chay");
		exactWords.put("SOMETHING","chez");
		exactWords.put("SON","getkihn");
		exactWords.put("SPEAK","tanz");
		exactWords.put("SPRING","reloh");
		exactWords.put("START","plint");
		exactWords.put("STREAK","shtihk");
		exactWords.put("STREAM","reloh");
		exactWords.put("STRIPE","shtihk");
		exactWords.put("STUDENT","port'h");
		exactWords.put("SUBSTITUTE","jepwov");
		exactWords.put("SUCCEED","poahp");
		exactWords.put("SUN","meykh");
		exactWords.put("SUNSET","meykeh");
		exactWords.put("SURISE","meykin");
		exactWords.put("SWAMP","rehao");
		exactWords.put("TAKE","tuhg");
		exactWords.put("TALK","tanz");
		exactWords.put("TALL","ngeyn");
		exactWords.put("TASK","rekyom");
		exactWords.put("TEACH","tuhdoch");
		exactWords.put("TEACHER","tuhdoche");
		exactWords.put("TEASE","speyak");
		exactWords.put("TELL","danz");
		exactWords.put("THANK","chiwkki");
		exactWords.put("THANKS","chiwkki");
		exactWords.put("THAT","ez");
		exactWords.put("THE","za");
		exactWords.put("EIGHT","wihn");
		exactWords.put("8","wihn");
		exactWords.put("FIVE","ye");
		exactWords.put("5","ye");
		exactWords.put("FOUR","hi");
		exactWords.put("4","hi");
		exactWords.put("NINE","yw");
		exactWords.put("9","yw");
		exactWords.put("ONE","o");
		exactWords.put("FIRST","oze");
		exactWords.put("SECOND","hoze");
		exactWords.put("THIRD","hoyze");
		exactWords.put("FOURTH","hize");
		exactWords.put("FIFTH","yeze");
		exactWords.put("SIXTH","yize");
		exactWords.put("SEVENTH","wize");
		exactWords.put("EIGHTH","wihnze");
		exactWords.put("NINETH","ywze");
		exactWords.put("TENTH","toze");
		exactWords.put("1","o");
		exactWords.put("SEVEN","wi");
		exactWords.put("7","wi");
		exactWords.put("SIX","yi");
		exactWords.put("6","yi");
		exactWords.put("TEN","to");
		exactWords.put("10","to");
		exactWords.put("THREE","hoy");
		exactWords.put("3","hoy");
		exactWords.put("TWO","ho");
		exactWords.put("2","ho");
		exactWords.put("THEIR","jedif");
		exactWords.put("THEM","jed");
		exactWords.put("THEN","noy");
		exactWords.put("THERE","mi");
		exactWords.put("THEREFORE","nw");
		exactWords.put("THESE","ne");
		exactWords.put("THEY","jed");
		exactWords.put("THIN","zifti");
		exactWords.put("THING","ezo");
		exactWords.put("THIS","e");
		exactWords.put("THOSE","ni");
		exactWords.put("TIME","oy");
		exactWords.put("TO","cho");
		exactWords.put("TODAY","koyn");
		exactWords.put("TOGETHER","kahnz");
		exactWords.put("TOMORROW","poyn");
		exactWords.put("TOO","dok");
		exactWords.put("TOP","hat");
		exactWords.put("TREAT","vuor");
		exactWords.put("TREE","ngeyn");
		exactWords.put("TRESPASS","jrach");
		exactWords.put("TRESPASSER","jrachw");
		exactWords.put("TROLL","trolo");
		exactWords.put("UNCOMPLICATED","imshi");
		exactWords.put("UNDER","get");
		exactWords.put("UNLESS","vw");
		exactWords.put("US","jad");
		exactWords.put("USE","ghrakh");
		exactWords.put("USED","ghrakh");
		exactWords.put("VERY","baso");
		exactWords.put("VIOLENT","znavo");
		exactWords.put("VIOLENCE","znavo");
		exactWords.put("VITALITIY","gongko");
		exactWords.put("WANT","kiz");
		exactWords.put("WAR","znavo");
		exactWords.put("WAS","no");
		exactWords.put("WATCH","jok");
		exactWords.put("WATER","reh");
		exactWords.put("WE","jad");
		exactWords.put("WELCOME","chiwkkshah");
		exactWords.put("WERE","no");
		exactWords.put("WHAT","fe");
		exactWords.put("WHATEVER","nyah");
		exactWords.put("WHEN","foy");
		exactWords.put("WHERE","fey");
		exactWords.put("WHICH","yr");
		exactWords.put("WHITE","epli");
		exactWords.put("WHO","fah");
		exactWords.put("WHY","fi");
		exactWords.put("WIDE","nerri");
		exactWords.put("WIFE","rihnkahn");
		exactWords.put("WILL","po");
		exactWords.put("WIN","poahp");
		exactWords.put("WITH","kho");
		exactWords.put("WITHOUT","konr");
		exactWords.put("WORK","zruhg");
		exactWords.put("YELLOW","ekh");
		exactWords.put("YES","yah");
		exactWords.put("YESTERDAY","noyn");
		exactWords.put("YET","vw");
		exactWords.put("YOU","so");
		exactWords.put("YALL","joth");
		exactWords.put("Y`ALL","joth");
		exactWords.put("YOUR","soif");
		return exactWords;
	}
}
