package com.planet_ink.coffee_mud.Libraries;
import com.planet_ink.coffee_mud.Libraries.interfaces.*;
import com.planet_ink.coffee_mud.core.CMath;
import com.planet_ink.coffee_mud.core.CMProps;

import java.util.*;

/*
   Copyright 2001-2025 Bo Zimmerman

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
public class Dice extends StdLibrary implements DiceLibrary
{
	@Override
	public String ID()
	{
		return "Dice";
	}

	private Random randomizer = null;
	protected CMath.CompiledFormula  baseNpcHitpointsFormula = null;

	@Override
	public synchronized Random getRandomizer()
	{
		if(randomizer == null)
			randomizer = new Random(System.currentTimeMillis());
		return randomizer;
	}

	public Dice()
	{
		super();
		baseNpcHitpointsFormula=CMath.compileMathExpression(CMProps.getVar(CMProps.Str.FORMULA_NPCHITPOINTS));
		randomizer = new Random(System.currentTimeMillis());
	}

	@Override
	public boolean activate()
	{
		baseNpcHitpointsFormula = CMath.compileMathExpression(CMProps.getVar(CMProps.Str.FORMULA_NPCHITPOINTS));
		return super.activate();
	}

	@Override
	public void propertiesLoaded()
	{
		activate();
	}

	@Override
	public boolean normalizeAndRollLess(final int score)
	{
		return (rollPercentage()<normalizeBy5(score));
	}

	@Override
	public int normalizeBy5(final int score)
	{
		if(score>95)
			return 95;
		else
		if(score<5)
			return 5;
		return score;
	}

	@Override
	public int rollHP(final int level, int code)
	{
		if(code<=0)
			code=CMProps.getMobHPBase();
		// new old style
		if(code<32768)
			return (int)Math.round(CMath.parseMathExpression(baseNpcHitpointsFormula, new double[]{level,code,0,0,0,0,0,0,0,0,0},0.0));

		// old old style
		//	return 10 +(int)Math.round(CMath.mul(level*level,0.85)) +(roll(level,code,0)*mul);

		// new style
		final int r=code>>23;
		final int d=(code-(r<<23))>>15;
		final int p=(((code-(r<<23))-(d<<15)));
		return roll(r,d,p);
	}

	@Override
	public void scramble(final List<?> objs)
	{
		Collections.shuffle(objs, randomizer);
	}

	@Override
	public void scramble(final int[] objs)
	{
		if(objs.length<2)
			return;
		final int sz=objs.length;
		for(int i=0;i<sz;i++)
		{
			final int k=i+randomizer.nextInt(sz-i);
			final int o=objs[k];
			objs[k]=objs[i];
			objs[i]=o;
		}
	}

	@Override
	public void scramble(final Object[] objs)
	{
		if(objs.length<2)
			return;
		final int sz=objs.length;
		for(int i=0;i<sz;i++)
		{
			final int k=i+randomizer.nextInt(sz-i);
			final Object o=objs[k];
			objs[k]=objs[i];
			objs[i]=o;
		}
	}

	@Override
	public long plusOrMinus(final long range)
	{
		final long l=randomizer.nextLong() % range;
		return randomizer.nextBoolean()?l:-l;
	}

	@Override
	public int plusOrMinus(final int range)
	{
		final int l=randomizer.nextInt() % range;
		return randomizer.nextBoolean()?l:-l;
	}

	@Override
	public double plusOrMinus(final double range)
	{
		final double l=randomizer.nextDouble() * range;
		return randomizer.nextBoolean()?l:-l;
	}

	@Override
	public float plusOrMinus(final float range)
	{
		final float l=randomizer.nextFloat() * range;
		return randomizer.nextBoolean()?l:-l;
	}

	@Override
	public int rollInRange(final int min, final int max)
	{
		if(max<=min)
			return min;
		final int l= randomizer.nextInt();
		if(l < 0)
			return min + ( -l % ((max-min)+1));
		else
			return min + (l % ((max-min)+1));
	}

	@Override
	public long rollInRange(final long min, final long max)
	{
		if(max<=min)
			return min;
		final long l= randomizer.nextLong();
		if(l < 0)
			return min + ( -l % ((max-min)+1));
		else
			return min + (l % ((max-min)+1));
	}

	@Override
	public Object doublePick(final Object[][] set)
	{
		if(set.length==0)
			return null;
		final Object[] sset = set[randomizer.nextInt(set.length)];
		if(sset.length==0)
			return null;
		return sset[randomizer.nextInt(sset.length)];
	}

	@Override
	public Object pick(final Object[] set, final Object not)
	{
		if(set.length==1)
		{
			if(set[0].equals( not ))
				return null;
		}
		else
		if(set.length==2)
		{
			if(set[0].equals( not ))
				return set[1];
			if(set[1].equals( not ))
				return set[0];
		}
		final List<Object> newList = new ArrayList<Object>(set.length);
		for(final Object O : set)
			newList.add(O);
		newList.remove( not );
		return pick(newList.toArray(new Object[0]));
	}

	@Override
	public Object pick(final Object[] set)
	{
		if(set.length==0)
			return null;
		return set[randomizer.nextInt(set.length)];
	}

	@Override
	public int pick(final int[] set, final int not)
	{

		boolean found=false;
		for(int i=0;i<set.length;i++)
		{
			if(set[i]==not)
			{
				found=true;
				break;
			}
		}
		if(found)
		{
			final int[] newSet = new int[set.length];
			int numGood = 0;
			for(final int x : set)
			{
				if(x != not)
					newSet[numGood++] = x;
			}
			return pick(Arrays.copyOf( newSet, numGood ));
		}
		return pick(set);
	}

	@Override
	public int rollNormalDistribution(final int number, final int die, final int modifier)
	{
		if(number<=0)
			return modifier;
		int total=0;
		double subtotal;
		for(int i=0;i<number;i++)
		{
			subtotal=randomizer.nextDouble() * die;
			subtotal += randomizer.nextDouble() * die;
			subtotal += randomizer.nextDouble() * die;
			total += 1 + (int)Math.round(Math.floor(subtotal / 2.9999));
		}
		return total + modifier;
	}

	@Override
	public int rollLowBiased(final int number, final int die, final int modifier)
	{
		if(number<=0)
			return modifier;
		int total=0;
		for(int i=0;i<number;i++)
		{
			double gauss=Math.abs(randomizer.nextGaussian() * die);
			gauss=gauss*0.5;
			while((gauss < 1.0) || (gauss > (die+0.9999999999)))
				gauss=Math.abs(randomizer.nextGaussian() * die);
			total+=(int)Math.round(Math.floor(gauss));
		}
		return total + modifier;
	}

	@Override
	public int pick(final int[] set)
	{
		if(set.length==0)
			return -1;
		return set[randomizer.nextInt(set.length)];
	}

	@Override
	public Object pick(final List<? extends Object> set)
	{
		if(set.size()==0)
			return null;
		return set.get(randomizer.nextInt(set.size()));
	}

	@Override
	public int getHPCode(String str)
	{
		int i=str.indexOf('d');
		if(i<0)
			return CMProps.getMobHPBase();
		final int roll=CMath.s_int(str.substring(0,i).trim());
		str=str.substring(i+1).trim();

		i=str.indexOf('+');
		int dice=0;
		int plus=0;
		if(i<0)
		{
			i=str.indexOf('-');
			if(i<0)
				dice=CMath.s_int(str.trim());
			else
			{
				dice=CMath.s_int(str.substring(0,i).trim());
				plus=CMath.s_int(str.substring(i));
			}
		}
		else
		{
			dice=CMath.s_int(str.substring(0,i).trim());
			plus=CMath.s_int(str.substring(i+1));
		}
		return getHPCode(roll,dice,plus);
	}

	@Override
	public int getHPCode(int roll, int dice, int plus)
	{
		if(roll<=0)
			roll=1;
		if(dice<=0)
			dice=0;

		if(roll>255)
		{
			final int diff=roll-255;
			roll=255;
			plus+=(diff*dice)/2;
		}
		if(dice>255)
		{
			final int diff=dice-255;
			dice=255;
			plus+=(diff*roll)/2;
		}
		int mul=1;
		if(plus<0)
		{
			plus=plus*-1;
			mul=-1;
		}
		if(plus>32768)
			plus=32768;
		return 	(plus+(dice<<15)+(roll<<(23)))*mul;
	}

	@Override
	public int[] getHPBreakup(final int level, int code)
	{
		int mul=1;
		if(code<0)
		{
			code=code*-1;
			mul=-1;
		}
		final int stuff[]=new int[3];
		// old style
		if(code<32768)
		{
			stuff[0]=level;
			stuff[1]=(code*mul);
			stuff[2]=(int)Math.round(CMath.mul(level*level,0.85));
		}
		else
		{
			// new style
			final int r=code>>23;
			final int d=(code-(r<<23))>>15;
			final int p=(((code-(r<<23))-(d<<15)))*mul;
			stuff[0]=r;
			stuff[1]=d;
			stuff[2]=p;
		}
		return stuff;
	}

	@Override
	public int roll(final int number, final int die, final int modifier)
	{
		if(die<=0)
			return modifier;
		int total=0;
		for(int i=0;i<number;i++)
			total+=randomizer.nextInt(die)+1;
		return total + modifier;
	}

	@Override
	public int rollPercentage()
	{
		return (Math.abs(randomizer.nextInt() % 100)) + 1;
	}

}
