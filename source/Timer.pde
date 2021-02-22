class Timer{                                                    /* Millis() returns how many milliseconds the program has been running.  There are 1000 Milliseconds in a second*/

  int timerStart;                                               //When Start
  int timerDuration;                                            //How long timer is
  
  Timer(int _timerDuration){ timerDuration = _timerDuration; }  //constructor holds amount of time
  
  void start(){ timerStart = millis(); }                        //Timestamp of how many milliseconds have gone by.

  boolean isFinished(){                                         //Checks to see if enough Millis have gone by
  
    int passedTime = millis() - timerStart;  
    
    if(passedTime > timerDuration) { return true; } 
    else { return false; }
    
  }
}
