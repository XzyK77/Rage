class Timer{                                                             /* Millis() returns how many milliseconds the program has been running.  There are 1000 Milliseconds in a second*/

  int timerStart;                                                        //When Start
  int timerDuration;                                                     //How long timer is
  boolean isRecurring;
  
  Timer(int _timerDuration, boolean _isRecurring){                       //constructor holds amount of time, starts timer as well
    isRecurring = _isRecurring;
    timerDuration = _timerDuration; 
    if(isRecurring){ start(); }                                          //Recurring Timer auto-starts
  }  
  
  void start(){ timerStart = millis(); }                                 //Timestamp of how many milliseconds have gone by.

  boolean isFinished(){                                                  //Checks to see if enough Millis have gone by
  
    int passedTime = millis() - timerStart;  
    
    if(passedTime > timerDuration) { 
      if(isRecurring){ start(); }                                        //Recurring Timer auto-restarts itself
      return true; 
    } 
    else { return false; }
    
  }
}
