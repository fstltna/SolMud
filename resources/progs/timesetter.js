function setInGameDate() {
  var currentDate = new Date();
  var currentYear = currentDate.getFullYear() + 500;
  var currentMonth = currentDate.getMonth(); // no need to add 1
  var currentDay = currentDate.getDate();
  var currentHour = currentDate.getHours(); // get the current hour

  mud.setDay(currentDay);
  mud.setMonth(currentMonth);
  mud.setYear(currentYear);
  mud.setTime(currentHour, 0); // set the time to the current hour and 0 minutes
  mud.setSaveRequired(true);
  mob().tell("In-game date set to " + (currentMonth+1) + "/" + currentDay + "/" + currentYear + " " + currentHour + ":00.");
}