class Player{                                          //Defines and draws everything related to the player

  Timer movementTick, animationTick, ragePassiveTick, outBurstTimer;
  Vec2f loc;
  Vec2i spriteTile;
  int size, animationFrame, directionalIndex, rotationAngle, lungeDirection;
  float baseSpeed, speed;
  float rage, maxRage, rageBarAnchor, rageBarMaxWidth;
  float baseRageIncrease;
  boolean isWPressed, isAPressed, isSPressed, isDPressed, isMouseRHPressed;
  boolean isColliding;
  PImage playerSprites, sprite;
  boolean isIncrementingAnimation, isOutBurstTimerStarted, isOutBursted, isLungeAttacking, hasWon;
  
  Player(Vec2f _loc, int _size, String _filePath, float _baseSpeed){
  
    rage = 0;
    lungeDirection = 0;
    maxRage = 100;
    baseRageIncrease = .14;
    rageBarAnchor = width / 4;
    rageBarMaxWidth = width / 1.75;
    isOutBursted = false;
    ragePassiveTick = new Timer(10, true);                                           //Faster timer + slower speed = SMOOTHER.
    outBurstTimer = new Timer(3000, false);
    isOutBurstTimerStarted = false;
    movementTick = new Timer(1, true);                                
    animationTick = new Timer(150, true);
    baseSpeed = _baseSpeed;
    speed = baseSpeed;                     
    directionalIndex = 0;
    rotationAngle = 0;
    isLungeAttacking = false;
    hasWon = false;
    loc = _loc;
    size = _size;
    isColliding = false;
    spriteTile = new Vec2i(0,0);
    playerSprites = loadImage(_filePath);
    sprite = playerSprites.get(100 * spriteTile.x, 100 * spriteTile.y, 100, 100);    //HARD CODED: Size - is different than collision size! replace with Pass-in!
    isIncrementingAnimation = true;
  }
  
  //=============================================================================================================================================================================================================================================//
  void checkTimers(){                                                                  //Checks all timers within player
    
    if(animationTick.isFinished()){                                                    //ANIMATION TIMER: increments till reaching max, then decrements till min, repeat
      
      if(isIncrementingAnimation){ animationFrame++; }
      else{                        animationFrame--; }
      
      if(animationFrame ==  3){ animationFrame = 1; isIncrementingAnimation = false; }
      if(animationFrame == -1){ animationFrame = 1; isIncrementingAnimation = true;  }
    }
    
    if(movementTick.isFinished()){                                                     //MOVEMENT TIMER:  applies speed based on boolean Values within player
    
        if(!(lungeDirection == 0)){//need directional index... work like clock 0 - 7, 0 @ 12 o'clock, 7 @ 10.5
        
          speed = baseSpeed + 15;
      
        }
        
        if(isMouseRHPressed){ speed = baseSpeed + 10; spriteTile.y = 1; }
        else                { speed = baseSpeed;      spriteTile.y = 0; }
      
        if(isWPressed){ directionalIndex += 1; }
        if(isDPressed){ directionalIndex += 2; }
        if(isSPressed){ directionalIndex += 4; }
        if(isAPressed){ directionalIndex += 7; }
      
        switch(directionalIndex){          //Directional Index is mathematical way of doing movement.
          case 0: break;                                                                                              //No Movement
          case 10:                                                                                                                  //Edge Case: UP, LEFT, RIGHT
          case 1: movePlayer(0, -speed); break;                                                                              //UP
          case 3: movePlayer(speed, -speed);break;                              //UP, RIGHT
          case 2: movePlayer(speed, 0); break;                                      //RIGHT 
          case 6: movePlayer(speed, speed);break;                      //DOWN, RIGHT
          case 13:                                                                                                                  //Edge Case: DOWN, LEFT, RIGHT
          case 4: movePlayer(0, speed);break;                                                                              //DOWN
          case 11: movePlayer(-speed, speed);break;                    //DOWN, LEFT
          case 12:                                                                                                                  //Edge Case: UP, DOWN, LEFT
          case 7: movePlayer(-speed, 0);break;                                     //LEFT
          case 8: movePlayer(-speed, -speed); break;                     //UP, LEFT
          case 14: break;                                                                                                           //Edge Case: UP, DOWN, LEFT, RIGHT
          default: println("DEFAULT:: PLAYER:: DIRECTIONALINDEX:: " + directionalIndex + "Movement not Defined! (or somethings up)");
        }
        directionalIndex = 0;
      }
    
    
    if(ragePassiveTick.isFinished()){                                                  //RAGE PASSIVE TIMER: applies naturally occuring rage
    
      rage += baseRageIncrease;
      if( (!(isOutBurstTimerStarted)) && (rage >= maxRage) ){                          //if over max rage, start outburst timer (which is basically just a death timer)
        outBurstTimer.start(); 
        isOutBurstTimerStarted = true;
      }
    }
    
    if(isOutBurstTimerStarted && outBurstTimer.isFinished()){ isOutBursted = true; } 
  }
  
  //=============================================================================================================================================================================================================================================//
  
  void movePlayer(float _x, float _y){
  
    loc.sum(_x,_y);
    
    if(isLungeAttacking){
    
      spriteTile.y = 2;
      switch(lungeDirection){
        case 1: spriteTile.x = 3; rotationAngle = 270; break;
        case 3: spriteTile.x = 3; rotationAngle = 315; break;
        case 2: spriteTile.x = 3; rotationAngle = 0; break;
        case 6: spriteTile.x = 3; rotationAngle = 45; break;
        case 4: spriteTile.x = 0; rotationAngle = 270; break;
        case 11: spriteTile.x = 0; rotationAngle = 315; break;
        case 7: spriteTile.x = 0; rotationAngle = 0; break;
        case 8: spriteTile.x = 0; rotationAngle = 45; break;
        default: println("DEFAULT:: " + directionalIndex + " PLAYER:: isLungeAttacking");
      }
    }
    else{
      
      switch(directionalIndex){
        case 3:  case 2:  case 6:         spriteTile.x = 3; break;
        case 11: case 12: case 7: case 8: spriteTile.x = 0; break;
      }
    }
  }
  
  //=============================================================================================================================================================================================================================================//
  void checkCollisions(Room[] _allRooms, LevelBlocks _levelBlocks){  
    
    for(int i = 0; i < _allRooms.length; i++){                      //for(each rooms)
      for(int j = 0; j < _allRooms[i].allBlocks.length; j++){       //for(each block)
        
        resolveCollisions(_allRooms[i].allBlocks[j]);
      }
    }
    
    for(int i = 0; i < _levelBlocks.rows; i++){                     //for(each row)
      for(int j = 0; j < _levelBlocks.cols; j++){                   //for(each column)
        if(_levelBlocks.blocks[i][j].isVisible){                    //Cut down on calculations by see if isVisible
        
          resolveCollisions(_levelBlocks.blocks[i][j]);
        }
      }
    }
  }
  
  //=============================================================================================================================================================================================================================================//
  void checkOrbs(int _action){  
    
    switch(_action){
      case 0: break;
      case 1: rage = 0; break;
      case 2: rage = 0; baseRageIncrease = 0; outBurstTimer.start(); isOutBurstTimerStarted = true; break;
      default: println("DEFAULT:: PLAYER:: CHECKORBS:: ACTION: " + _action + "Not supported");
    }
  }
  
  //=============================================================================================================================================================================================================================================//
    void resolveCollisions(Block _b){                                     //Even though the blocks are square, easier to do circle on circle collsion and resolution. (couldn't get the circle on square to work properly anyway.)
  
    Tri.a = loc.x - (_b.loc.x + (_b.size / 2));                           //Set tri for distance calculation, based on playerLocation and center of Square
    Tri.b = loc.y - (_b.loc.y + (_b.size / 2));
    float distance = Tri.getC();
    float nonCollisionDistance = ((size / 2) + (_b.size / 2));            //figure out what distance should the two circles be away from each other
    if(distance < nonCollisionDistance){
      
      loc.x += Tri.a / (distance / (nonCollisionDistance - distance));    //Condensed math stuffs: find difference between collision distance, and proper distance, then "pushes" player out of block.
      loc.y += Tri.b / (distance / (nonCollisionDistance - distance));
    }
  }
  
  //=============================================================================================================================================================================================================================================//
  void display(Vec2f _offset){                                                                          //Grabs applicable Sprite, then displays in relation to the Viewport Offset
    
    sprite = playerSprites.get(100 * (spriteTile.x + animationFrame), 100 * spriteTile.y, 100,100);
    
    imageMode(CENTER);
    pushMatrix();
    
    translate(loc.x + _offset.x, loc.y + _offset.y + 5);
    rotate(rotationAngle * TWO_PI / 360);
    image(sprite,0,0, 100, 100);
    
    popMatrix();
    imageMode(CORNER);
    
  }
  
  //=============================================================================================================================================================================================================================================//
  void displayHUD(){                                                    //Shows rageBar so player knows how close to outBurst they are
    
    fill(255,255,255);                                                  //White rect
    rect(rageBarAnchor, 10, rageBarMaxWidth, 50);
    
    fill(255,0,0);                                                      //red rect
    if(isOutBurstTimerStarted){ rect(rageBarAnchor, 10, rageBarMaxWidth, 50); }
    else{                       rect(rageBarAnchor, 10, (rage * rageBarMaxWidth) / maxRage, 50); }
    
    fill(255,255,255);                                                  //reset rect color
  }
  
  //=============================================================================================================================================================================================================================================//
  void pressed(){                                              //Pressed and releases functions: sets boolean values to streamline input from keys
  
    if(!(isLungeAttacking)){
      if(key == 'w'){ isWPressed = true; }
      if(key == 'a'){ isAPressed = true; }
      if(key == 's'){ isSPressed = true; }
      if(key == 'd'){ isDPressed = true; }
    }
  }
  
  void released(){
  
    if(key == 'w'){ isWPressed = false; }
    if(key == 'a'){ isAPressed = false; }
    if(key == 's'){ isSPressed = false; }
    if(key == 'd'){ isDPressed = false; }
  }
  
  void mousePush(){
  
    if(!(isLungeAttacking)){
      if(mouseButton == RIGHT){ isMouseRHPressed = true; }
      if(mouseButton == LEFT){ isLungeAttacking = true; }
    }
  }
  
  void mouseNotPush(){
  
    if(mouseButton == RIGHT){ isMouseRHPressed = false; }
    if(mouseButton == LEFT){ isLungeAttacking = false;}
  }
}
