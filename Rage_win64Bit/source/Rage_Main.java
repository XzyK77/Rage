import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.sound.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Rage_Main extends PApplet {



LevelMap levelMap;
LevelBounds levelBounds;
LevelBlocks levelBlocks;
LevelOrbs levelOrbs;
Player player;
ViewPort viewPort;
SoundFile bgSong;

public void settings(){ fullScreen(); }                                                         //Better instead of size!

public void setup(){

  frameRate(60);                                                                         //Gotta have 60 frames!!!
  noCursor();                                                                            //Go to your room!  Dont come out till we are done!
  
  PImage sprite = loadImage("Block01.png");                                              //Where are the Sprites?
  Vec3i circleSizes = new Vec3i(3000, 2250, 1500);                                       //Level cycles through three room sizes when generating rooms
  int numRooms = 12;                                                                     //How long should the level be?
  int roomGap = 500;                                                                     //How far away should each room be from the last room?
  int hallSize = 100;                                                                    //How wide should the gaps be?
  int blockSize = 30;                                                                    //How big are the walls?
  
  levelMap = new LevelMap(numRooms, circleSizes, roomGap, hallSize, blockSize, sprite);  //Generates rooms and hallways in arbitrary space, no bounds restrict map generation
  
  int lgCircleSize = circleSizes.x;                                                      //Clarification Code: only need the largest circle size when deciding the bounds for the map
  
  levelBounds = new LevelBounds(levelMap, lgCircleSize);                                 //Based on segment sizes and circle sizes
  
  levelBlocks = new LevelBlocks(levelBounds.levelSize, blockSize, levelMap.allRooms, hallSize, new Vec2f(levelBounds.levelSize.x, levelBounds.levelSize.y), sprite);  //Creates the blocks tha, _blockSpriteFilePatht,  _blockSpriteFilePath form the barriers to the level
  
  PImage orbSprites = loadImage("Orb.png");                                              //Where are the Sprites?
  Vec2f orbOffset = new Vec2f(0,-50);                                                    //Orb locations are based on the centers of the room, moves all levelOrbs
  boolean orbVisibility = true;                                                          //Should they be Visible?
  int orbSize = 24;                                                                      //How big are the sprites for the orbs?
  
  levelOrbs = new LevelOrbs(levelMap.rooms, orbOffset, orbSize, orbSprites, orbVisibility);    //Places orbs based on rooms locations with overall offset.
  
  String playerSpriteFilePath = "Player_SpriteSheet.png";
  Vec2f playerStartLoc = new Vec2f(levelMap.rooms[0].loc.x - 400, levelMap.rooms[0].loc.y + 600);
  int playerSize = 60;
  float playerBaseSpeed = 5;
  
  player = new Player(playerStartLoc, playerSize, playerSpriteFilePath, playerBaseSpeed);    //Start Location, size, spritesheet, tint, spriteFilePath
  
  viewPort = new ViewPort(new Vec2f(width / 2, height / 2));                        //screenAnchorLoc
  
  String soundBGFilePath = "Final_Boss[extended]_audio.wav";
  bgSong = new SoundFile(this, soundBGFilePath);
  bgSong.amp(.1f);
  bgSong.play();

}

public void draw(){

  player.checkTimers();
  levelOrbs.checkTimers();
  
  if(player.isOutBursted){ bgSong.stop(); exit(); }
  
  Vec2f viewOffset = viewPort.getOffset(player.loc);                                //Grab offset for passing into display functions
  
  background(255,255,255);                                                          //Start with white Background
  player.checkCollisions(levelMap.allRooms, levelBlocks);                           //
  
  player.checkOrbs(levelOrbs.checkCollision(player));                               //Simultaneously makes orbs invisible and alter Rage meter of player on Collision
  
  levelMap.display(viewOffset);                                                     //Display Calls (easier way?)
  levelBlocks.display(viewOffset);
  levelOrbs.display(viewOffset);
  player.display(viewOffset);
  player.displayHUD();

}

public void keyPressed(){ player.pressed(); }
public void keyReleased(){ player.released(); }
public void mousePressed(){ player.mousePush(); }
public void mouseReleased(){ player.mouseNotPush(); }                                        //  :P
class Block{                                                        //Used as building blocks to generate walls of level

  Vec2f loc;
  int size;
  boolean isVisible;
  String blockSpriteFilePath;
  PImage sprite;

  Block(Vec3f _v, boolean _isVisible, PImage _sprite){ 
    loc = new Vec2f(_v.x, _v.y);
    size = PApplet.parseInt(_v.z);
    isVisible = _isVisible;
    sprite = _sprite;
  }
  
  public void displayWithResize(Vec2f _offset, boolean _isResized){
    if(getIsInViewPort(_offset)){
      if(isVisible){
        if(_isResized){ image(sprite, loc.x + _offset.x, loc.y + _offset.y, size, size);}
        else{ image(sprite, loc.x + _offset.x, loc.y + _offset.y); } 
      }
    }
  }
  
  public boolean getIsInViewPort(Vec2f _offset){
  
    if(((loc.x + _offset.x) > - size) && ((loc.x + _offset.x) < width) && ((loc.y + _offset.y) > - size) && ((loc.y + _offset.y) < height)){
      return true;
    }
    return false;
  }
}
class LevelBlocks{                                                                        //Generates all blocks that form the level's walls

  Block[][] blocks;                                                                       //2d array for all blocks
  int rows, cols;
  
  LevelBlocks(Vec4f _levelBounds, int _size, Room[] _allRooms, int _hallSize, Vec2f _levelLocation, PImage _sprite){
    
    float blockX = 0, blockY = 0;
    
    rows = PApplet.parseInt(_levelBounds.w) / _size;                                                    //generate size of two dimensional array
    cols = PApplet.parseInt(_levelBounds.h) / _size;
    
    blocks = new Block[rows][cols];
    
    for(int i = 0; i < rows; i++){                                                         //for(each row){ just grab cols... }
      for(int j = 0; j < cols; j++){                                                       //for(each col){ just grab segment... }
        boolean isBlockVisible = true;
        for(int k = 0; k < _allRooms.length; k++){                                         //grab room once, do calcs here
                                                                               
            Room room = _allRooms[k];                                                      //term simplification AND block is not created yet
            
            blockX = _levelLocation.x + (_size * i);                                       //grab block's projected x and y location
            blockY = _levelLocation.y + (_size * j);
            
            float distance = Tri.getC(blockX - room.loc.x, blockY - room.loc.y);           //calculate distance to room's center
            
            if(room.size == _hallSize){                                                    //blocks having a hard time becoming invisible with the smaller circles, so they are checked differently
              if(distance < room.size){                                                    //if within full diameter, create block, but make invisible
                blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), false, _sprite);  //-> creating the block and making it invisible avoids NullPointers and allows display() to later ignore the draw call.
                isBlockVisible = false;
                break;
              }
            }
            else if(distance < room.size / 2){                                             //if dealing with the regular circles, do calculations as normal
              blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), false, _sprite); 
              isBlockVisible = false;
              break; 
            }
        }
        if(isBlockVisible){ blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), true, _sprite); }
      }
    }
  }
  
  public void display(Vec2f _offset){
    for( int i = 0; i < rows; i++){
      for( int j = 0; j < cols; j++){
          blocks[i][j].displayWithResize(_offset, false);
      }
    }
  }
  
}
class LevelBounds{                                                                                            //Contains randomized room locations and hallways for general level layout

  Vec4f levelSize;                                                                                      //Represents outer bounds of level
  
  LevelBounds(LevelMap _map, int _lgCircleSize){               //Creates randomly generated level                                                    
    
    Vec3f s = new Vec3f(_map.rooms[0].loc.x, _map.rooms[0].loc.y, _map.rooms[0].size);                  //starts at first room circle, helps with calls and reference size
    levelSize = new Vec4f(s.x - (s.z / 2), s.y - (s.z / 2), s.x + (s.z / 2), s.y +  (s.z / 2));         //Is the Start location = to first Room
    
    for(int i = 0; i < _map.rooms.length; i++){                                                         //Calculates bounds based on all Rooms
      
      Vec2f loc = new Vec2f(_map.rooms[i].loc.x, _map.rooms[i].loc.y);
      if(loc.x < levelSize.x){ levelSize.x = loc.x; }                                                   //Min X
      if(loc.y < levelSize.y){ levelSize.y = loc.y; }                                                   //Min Y
      if(loc.x > levelSize.w){ levelSize.w = loc.x; }                                                   //Max X
      if(loc.y > levelSize.h){ levelSize.h = loc.y; }                                                   //Max Y
    }
    
    levelSize.w -= levelSize.x;                                                                         //width = difference between max X and min X
    levelSize.h -= levelSize.y;                                                                         //height = difference between max Y and min Y
    
    levelSize =  new Vec4f(levelSize.x - _lgCircleSize, levelSize.y - _lgCircleSize, levelSize.w + (_lgCircleSize * 2), levelSize.h + + (_lgCircleSize * 2));  //Adds padding to the sides 
  }
}
class LevelMap{                                                                                 //Contains all Segments of level Generation

  Room[] rooms;
  Room[] allRooms;
  
  LevelMap(int _numRooms, Vec3i _roomSizeSet, int _roomGap, int _hallSize, int _blockSize, PImage _sprite){              //Will be used to make locations and room sizes. -> int[]_roomSizes, and Vec2f _locs
  
    rooms = new Room[_numRooms];
    
    Vec3f roomCandidate = new Vec3f(0.0f,0.0f,0.0f);
    byte roomSizeSetIndex = 0;                                                                  //Keeps track of what roomSizeSet to set roomSize when applying it to roomCandidate
    float roomSize = 0;                                                                         //actual roomSize according to roomSizeSet
    
    for(byte i = 0; i < rooms.length; i++){
      
      switch(roomSizeSetIndex){
      
        case 0: roomSize = _roomSizeSet.x; break;
        case 1: roomSize = _roomSizeSet.y; break;
        case 2: roomSize = _roomSizeSet.z; roomSizeSetIndex = -1; break;
        default:  println("CONSTRUCTOR FAIL:: RoomSegment():: DEFAULT:: roomIndex VALUE = " + roomSizeSetIndex);
      }    //save RoomSize for later...
      roomSizeSetIndex++;
      
      boolean isLocationValid = false;           //If declaring variables like "boolean something = false" within a for loop, will that stay in memory till the entire for loop is complete? or dump for each itteration?
      int gap = _roomGap;                        //Gap is re-initialized to original size
      
      while(!(isLocationValid)){
        
        gap += 20;
        if( i - 1 == -1 ){ roomCandidate = new Vec3f(generateRoomLocation(roomSize, new Vec3f(width / 2, height / 2,                  roomSize), gap), roomSize); }      
        else{              roomCandidate = new Vec3f(generateRoomLocation(roomSize, new Vec3f(rooms[i - 1].loc.x, rooms[i - 1].loc.y, roomSize), gap), roomSize); }
        
        isLocationValid = (!(isInsideOtherRooms(roomCandidate, i)));
      }
      byte roomType = 0;
      if(i == 0) {roomType = 1; println("Start Room Set");}
      else if(i == rooms.length - 1){roomType = 2; println("End Room Set");}
      else{roomType = PApplet.parseByte(random(3, 6)); println("Random Room Type = " + roomType);}
      rooms[i] = new Room(new Vec2f(roomCandidate.x, roomCandidate.y), roomSize, roomType, color(255,255,255), _blockSize, _sprite);
    }
    
    int totalHallRooms = 0;                                                                  //create Corridors
    for(int i = 0; i < rooms.length - 1; i++){                                               //Need total num of rooms based on distance between rooms for array initialization

      totalHallRooms += PApplet.parseInt(Tri.getC(rooms[i].loc.x - rooms[i + 1].loc.x, rooms[i].loc.y - rooms[i + 1].loc.y) / _hallSize);  // = int(distance / hallSize)
    }
    
    allRooms = new Room[totalHallRooms + _numRooms];
    int allRoomsIndex = rooms.length;                                                        //Start at rooms.length b/c we load rooms into allRooms first
    
    for(int i = 0; i < rooms.length; i++){ allRooms[i] = rooms[i]; }                         //Load rooms into allRooms
    
    for(int i = 0; i < rooms.length - 1; i++){
      
      Tri.a = rooms[i].loc.x - rooms[i + 1].loc.x;
      Tri.b = rooms[i].loc.y - rooms[i + 1].loc.y;
      int numHallRooms = PApplet.parseInt(Tri.getC() / _hallSize);
      float subA = Tri.a / numHallRooms;
      float subB = Tri.b / numHallRooms;
      
      for(int j = 0; j < numHallRooms; j++){
      
        allRooms[allRoomsIndex] = new Room(new Vec2f(rooms[i].loc.x - (subA * j), rooms[i].loc.y - (subB * j)), _hallSize, PApplet.parseByte(0), color(255,255,255), _blockSize, _sprite);
        allRoomsIndex++;
      
      }
    }
  }
  /*==========================================================================================================================================*/
  
  public Vec2f generateRoomLocation(float _thisRoomSize, Vec3f _lastRoom, int _gap){                              //Generates Location of new Room
    
    float distance = (_lastRoom.z / 2) + (_thisRoomSize / 2) + _gap;
    float x = random(-distance, distance);
    float y = sqrt((distance * distance) - (x * x));                                                       //No method in Tri, and only done once, so just keep here.
    int randomY = PApplet.parseInt(random(2));
    if(randomY == 0){ y = -y; }
    
    return new Vec2f(_lastRoom.x + x, _lastRoom.y + y);
  }
  /*===========================================================================================================================================*/
  
  public boolean isInsideOtherRooms(Vec3f _candidate, int _roomIndex){
  
    if(_roomIndex == 0){ return false; }
    
    for(byte i = 0; i < _roomIndex; i++){
      if(Tri.getC(_candidate.y - rooms[i].loc.y, _candidate.x - rooms[i].loc.x) < ((rooms[i].size / 2) + (_candidate.z / 2))){ return true; }      //if(distance < thisRoomSize + candidate's roomSize)
    }
    return false;
  }
  /*==========================================================================================================================================*/
  
  public void display(Vec2f _offset){ for(int i = 0; i < allRooms.length; i++){ allRooms[i].display(_offset); } }
}
class LevelOrbs{                                                                        //Object used to decrease Player's rage amount
  
  Orb[] orbs;

  LevelOrbs(Room[] _rooms, Vec2f _offset, int _size, PImage _spriteFilePath, boolean _isVisible){
    
    orbs = new Orb[_rooms.length];                                                      
  
    for(int i = 0; i < orbs.length; i++){                                                //Creates one orb per room at that rooms location with the given offset
    
      orbs[i] = new Orb(new Vec2f(_rooms[i].loc.x + _offset.x, _rooms[i].loc.y + _offset.y), _size, _spriteFilePath, _isVisible);
    }
  }
  
  public void checkTimers(){ for(int i = 0; i < orbs.length; i++){ orbs[i].checkTimer(); } }    //Increments the animation frames of each orb
  
  public int checkCollision(Player _player){
  
    for(int i = 0; i < orbs.length; i++){
      
      if(orbs[i].checkCollision(_player)){ 
        
        if(i == orbs.length - 1){ return 2; }                                            //2 = orb at end of level
        return 1;                                                                        //1 = all other orbs
      }
    }
    return 0;                                                                            //no Collision
  }
  
  public void display(Vec2f _offset){
    for (int i = 0; i < orbs.length; i++){ orbs[i].display( _offset); }
  }
}
class Orb{                                                                    

  Vec2f loc;
  int size, spriteTileX;
  PImage orbSprite, sprite;
  boolean isVisible;
  Timer animationTick;
  
  Orb(Vec2f _loc, int _size, PImage _orbSprite, boolean _isVisible){
  
    loc = _loc;
    size = _size;
    orbSprite = _orbSprite;
    spriteTileX = 0;
    sprite = orbSprite.get(size * spriteTileX, 0 ,size,size);
    animationTick = new Timer(250, true);
    isVisible = _isVisible;
  
  }
  
  public void checkTimer(){
  
    if(animationTick.isFinished()){ spriteTileX++; println(spriteTileX);}
    if(spriteTileX == 4){ spriteTileX = 0; }
  
  }
  
  public boolean checkCollision(Player _player){
  
    
    if((Tri.getC(_player.loc.x - loc.x, _player.loc.y - loc.y) <= ((_player.size / 2) + size / 2)) && (isVisible)){ isVisible = false; return true; }//set to invisible and return true
    return false;
  
  }
  
  public void display(Vec2f _offset){
  
    sprite = orbSprite.get(size * spriteTileX, 0, size, size); //gtg
    
    if(isVisible){
        image(sprite, loc.x + _offset.x, loc.y + _offset.y, size, size);
    }
  }
  
}
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
    baseRageIncrease = .14f;
    rageBarAnchor = width / 4;
    rageBarMaxWidth = width / 1.75f;
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
  public void checkTimers(){                                                                  //Checks all timers within player
    
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
  
  public void movePlayer(float _x, float _y){
  
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
  public void checkCollisions(Room[] _allRooms, LevelBlocks _levelBlocks){  
    
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
  public void checkOrbs(int _action){  
    
    switch(_action){
      case 0: break;
      case 1: rage = 0; break;
      case 2: rage = 0; baseRageIncrease = 0; outBurstTimer.start(); isOutBurstTimerStarted = true; break;
      default: println("DEFAULT:: PLAYER:: CHECKORBS:: ACTION: " + _action + "Not supported");
    }
  }
  
  //=============================================================================================================================================================================================================================================//
    public void resolveCollisions(Block _b){                                     //Even though the blocks are square, easier to do circle on circle collsion and resolution. (couldn't get the circle on square to work properly anyway.)
  
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
  public void display(Vec2f _offset){                                                                          //Grabs applicable Sprite, then displays in relation to the Viewport Offset
    
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
  public void displayHUD(){                                                    //Shows rageBar so player knows how close to outBurst they are
    
    fill(255,255,255);                                                  //White rect
    rect(rageBarAnchor, 10, rageBarMaxWidth, 50);
    
    fill(255,0,0);                                                      //red rect
    if(isOutBurstTimerStarted){ rect(rageBarAnchor, 10, rageBarMaxWidth, 50); }
    else{                       rect(rageBarAnchor, 10, (rage * rageBarMaxWidth) / maxRage, 50); }
    
    fill(255,255,255);                                                  //reset rect color
  }
  
  //=============================================================================================================================================================================================================================================//
  public void pressed(){                                              //Pressed and releases functions: sets boolean values to streamline input from keys
  
    if(!(isLungeAttacking)){
      if(key == 'w'){ isWPressed = true; }
      if(key == 'a'){ isAPressed = true; }
      if(key == 's'){ isSPressed = true; }
      if(key == 'd'){ isDPressed = true; }
    }
  }
  
  public void released(){
  
    if(key == 'w'){ isWPressed = false; }
    if(key == 'a'){ isAPressed = false; }
    if(key == 's'){ isSPressed = false; }
    if(key == 'd'){ isDPressed = false; }
  }
  
  public void mousePush(){
  
    if(!(isLungeAttacking)){
      if(mouseButton == RIGHT){ isMouseRHPressed = true; }
      if(mouseButton == LEFT){ isLungeAttacking = true; }
    }
  }
  
  public void mouseNotPush(){
  
    if(mouseButton == RIGHT){ isMouseRHPressed = false; }
    if(mouseButton == LEFT){ isLungeAttacking = false;}
  }
}
static class Tri{                                                          //Used statically for saved space as well as versatility

  static float a, b, c;                                                    //can be set and used with getC() with no argument if values are to be retained for other calculations.

  public static float getC(){ return sqrt((a * a) + (b * b)); }                   //Use if Tri.a and Tri.b are already set and want c calculated from them.  Good for retaining a and b within calculations area for other uses.
  public static float getC(float a, float b){ return sqrt((a * a) + (b * b)); }   //Use if no setting to Tri is desired, mor of a one off

}
class Room{

  Vec2f loc;
  float size;
  int c;
  byte roomType;
  int blockSize;
  Block[] allBlocks;
  float[] xPts, yPts;
  
  Room(){ this(new Vec2f(0.0f,0.0f), 10, PApplet.parseByte(0), color(255,255,255), 5, loadImage("Block01.png")); }
  
  Room(Vec2f _loc, float _roomSize, byte _roomType, int _c, int _blockSize, PImage _sprite){
  
    loc = _loc;
    size = _roomSize;
    blockSize = _blockSize;
    c = _c; 
    roomType = _roomType;
    allBlocks = new Block[0];
    xPts = new float[]{0,0,0};
    yPts = new float[]{0,0,0};
    
    switch(_roomType){
     
      case 0: break;                                      //Empty
      case 1:                                             //Start
        xPts = new float[]{ -1000, -1250, -1250,  1250};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{   800,   800,  1250,  1250};
        break;
      case 2:                                             //End
        xPts = new float[]{ -1200,  1100, 1100, 600, 600, 1150,  1150,  1250};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{  1200,  1200,    0,   0, -60,  -60,  1200,  1250};
        break;
      case 3:                                             //Half - A
        xPts = new float[]{ -750, -250, 100,  600, 1250,  350,   100,  -100, -900};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ 1000,  100, 100, 1000, 1000, -750, -1250, -1250, -300};
        break; 
      case 4:                                             //Arena Square
        xPts = new float[]{ -1250, -1250, 1250,  1250,  -900};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ -1250,  1250, 1250, -1250, -1250};
        break;
      case 5:                                             //Room G
        xPts = new float[]{  1250,  1100,     0, -1100, -1250, -1600, -1250, -1100,    0, 1100, 1250, 1600,  1250, -450, -800, -800, -200, 400, 400, 0};      //BOTH OF THESE NEED TO BE THE SAME SIZE
        yPts = new float[]{ -1250, -1400, -1600, -1400, -1250,     0,  1250,  1400, 1600, 1400, 1250,    0,  -800, -900, -700,  700, 1000, 900, 500, 0};
        break;
      default:  println("DEFAULT:: CLASS:: ROOM:: TYPE");
    }
    setAllBlocks(xPts, yPts, _sprite);
  }
  
  public void display(Vec2f _offset){
    
    //ellipse(loc.x + _offset.x, loc.y + _offset.y, size, size);                        //Can display size of room with this.
    for(int i = 0; i < allBlocks.length; i++){ allBlocks[i].displayWithResize(_offset, true); }
    
  }
  
  public void setAllBlocks(float[] xPts, float[] yPts, PImage _sprite){                        //Uses an array of points to place blocks along those points.  Will scale points to fit into circle's size.
    
    float biggestX = 0;                                                                 //Need to scale according to room size
    int xIndex = 0;
    float biggestY = 0;
    int yIndex = 0;
    int lgValIndex = 0;
    
    for(int i = 0; i < xPts.length; i++){                                               //loops only call xPts as both xPts and yPts are the same sizes
      
      float xCandidate = sqrt(xPts[i] * xPts[i]);                                       //checks x point for biggest size
      if(xCandidate >= biggestX){ xIndex = i; biggestX = xCandidate;}
      float bigYCandidate = sqrt(yPts[i] * yPts[i]);                                    //checks y point for biggest size
      if(bigYCandidate >= biggestY){ yIndex = i; biggestY = bigYCandidate; }
    }
    
    if(biggestX > biggestY){ lgValIndex = xIndex; }
    else{ lgValIndex = yIndex; }                                                        //Only thing we are looking for is the index with the largest value to base scaleAdjustment.
    
    float scaleAdjustment = (size / 3) / Tri.getC(xPts[lgValIndex], yPts[lgValIndex]);  //Finds scale adjustment for use with all points based on modified size and actual distance of farthest point
    
    for(int i = 0; i < xPts.length; i++){                                               //Adjusts all values with the scaleAdjustment
    
      xPts[i] = xPts[i] * scaleAdjustment; 
      yPts[i] = yPts[i] * scaleAdjustment; 
    }     
    
    for(int i = 0; i < xPts.length - 1; i++){
      
      Tri.a = xPts[i] - xPts[i + 1];                                                    //Find distance between this point and next point
      Tri.b = yPts[i] - yPts[i + 1];
      float distance = Tri.getC();
      float numOfBlocks = distance / blockSize;                                         //will result in number with remainder, need to divvy out remaider over rest of blocks
      float newBlockSize = blockSize + ((distance % blockSize) / numOfBlocks);          //Distributes remainder of block space into all blocks
      
      numOfBlocks = PApplet.parseInt(distance / newBlockSize);
      float subA = Tri.a / numOfBlocks;
      float subB = Tri.b / numOfBlocks;
      
      Block[] segmentBlocks = new Block[PApplet.parseInt(numOfBlocks)];                              //initialize this segment for block generation
        
      for(int j = 0; j < PApplet.parseInt(numOfBlocks); j++){ segmentBlocks[j] = new Block(new Vec3f(loc.x + xPts[i] - (j * subA), loc.y + yPts[i] - (j * subB), newBlockSize), true, _sprite); }  //generate blocks on line
      
      Block[] temp = new Block[allBlocks.length + segmentBlocks.length];                                                                                                     //Merge array with allBlocks
       
      for(int j = 0; j < allBlocks.length; j++){ temp[j] = allBlocks[j]; }                                                                                                   //copy allBlocks into new array
      for(int j = allBlocks.length; j < allBlocks.length + segmentBlocks.length; j++){ temp[j] = segmentBlocks[j - allBlocks.length]; }                                      //Add this segment to new array
      
      allBlocks = new Block[temp.length];                                                                                                                                    //re-initialize allBlocks to correct size
      for(int j = 0; j < temp.length; j++){ allBlocks[j] = temp[j]; }                                                                                                        //Set allBlocks to new array
    }
  }
}
class Timer{                                                             /* Millis() returns how many milliseconds the program has been running.  There are 1000 Milliseconds in a second*/

  int timerStart;                                                        //When Start
  int timerDuration;                                                     //How long timer is
  boolean isRecurring;
  
  Timer(int _timerDuration, boolean _isRecurring){                       //constructor holds amount of time, starts timer as well
    isRecurring = _isRecurring;
    timerDuration = _timerDuration; 
    if(isRecurring){ start(); }                                          //Recurring Timer auto-starts
  }  
  
  public void start(){ timerStart = millis(); }                                 //Timestamp of how many milliseconds have gone by.

  public boolean isFinished(){                                                  //Checks to see if enough Millis have gone by
  
    int passedTime = millis() - timerStart;  
    
    if(passedTime > timerDuration) { 
      if(isRecurring){ start(); }                                        //Recurring Timer auto-restarts itself
      return true; 
    } 
    else { return false; }
    
  }
}
class Vec2i{

  int x, y;
  Vec2i(int _x, int _y){ x = _x; y = _y; }

}

class Vec2f{

  float x, y;
  Vec2f(float _x, float _y){ x = _x; y = _y; }
  
  public void sum(float _x, float _y){ x += _x; y += _y; }

}

class Vec3f{

  float x, y, z;
  Vec3f(float _x, float _y, float _z){ x = _x; y = _y; z = _z; }
  
  Vec3f(Vec2f _loc, float _z){ this(_loc.x, _loc.y, _z); }
  
}

class Vec3i{

  int x, y, z;
  Vec3i(int _x, int _y, int _z){ x = _x; y = _y; z = _z; }

}

class Vec4f{
  
  float x, y, w, h;
  Vec4f(float _x, float _y, float _w, float _h ){ x = _x; y = _y; w = _w; h = _h;}
  
}
class ViewPort{

  Vec2f screenAnchorLoc, offset;
  
  ViewPort(Vec2f _screenAnchorLoc){
  
    screenAnchorLoc = _screenAnchorLoc;
    offset = new Vec2f(0.0f,0.0f);
  }
  
  public Vec2f getOffset(Vec2f _centeringLoc){
  
    offset.x = screenAnchorLoc.x - _centeringLoc.x;
    offset.y = screenAnchorLoc.y - _centeringLoc.y;
    return offset;
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Rage_Main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
