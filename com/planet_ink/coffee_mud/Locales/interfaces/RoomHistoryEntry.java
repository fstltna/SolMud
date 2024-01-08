package com.planet_ink.coffee_mud.Locales.interfaces;
import java.util.Date;
import com.planet_ink.coffee_mud.Common.interfaces.CMMsg;

public class RoomHistoryEntry {
    public CMMsg message = null;
    public Date dtWhen = new Date();

    public RoomHistoryEntry(CMMsg msg){
        message = msg; 
        dtWhen = new Date(System.currentTimeMillis());
    }
    
    @Override
    public String toString(){
        return dtWhen.toString() + " " + message.source().Name();
    }
}