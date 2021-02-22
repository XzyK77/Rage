import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Rage_Main extends PApplet {

Level level;
Player player;
ViewPort viewPort;

public void settings(){ fullScreen(); }                                                    //Better instead of size?

public void setup(){

  frameRate(60);
  noCursor();
  
  byte[] segmentSizes = {3,4,5};                                                    //Only used for sizes
  Vec3i circleSizes = new Vec3i(3000, 2250, 1500);
  int blockSizes = 30;
  int roomGap = 500;
  int hallSize = 100;
  
  level = new Level(segmentSizes, circleSizes, blockSizes, roomGap, hallSize);      //Based on segment sizes and circle sizes
  
  int playerSize = 60;
  
  player = new Player(level.map.rooms[0].loc, playerSize, color(100,100,100));               //Start Location, size, spritesheet, tint
  viewPort = new ViewPort(new Vec2f(width / 2, height / 2));                        //screenAnchorLoc

}

public void draw(){

  player.tick();                                                                    //Analyses player state and applies time based functions
  Vec2f viewOffset = viewPort.getOffset(player.loc);                                //Grab offset for passing into display functions
  
  background(255,255,255);                                                          //Start with white Background
  level.display(viewOffset);
  player.display(viewOffset);

}

public void keyPressed(){ player.pressed(); }
public void keyReleased(){ player.released(); }
class Block{                                                        //Used as building blocks to generate walls of level

  Vec2f loc;
  int size;
  boolean isVisible;

  Block(Vec3f _v, boolean _isVisible){ 
    loc = new Vec2f(_v.x, _v.y);
    size = PApplet.parseInt(_v.z);
    isVisible = _isVisible;
  }
  
  public void display(Vec2f _offset){ 
    if(getIsInViewPort(_offset)){
      if(isVisible){
        rect(loc.x + _offset.x, loc.y + _offset.y, size, size); 
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
class Level{                                                                                            //Contains randomized room locations and hallways for general level layout

  LevelMap map;                                                                                         //Referes to entire scope of rooms
  LevelBlocks blocks;                                                                                   //Blocks that make up the walls of the level
  Vec4f levelSize;                                                                                      //Represents outer bounds of level
  
  Level(byte[] _segSizes, Vec3i _circleSizes, int _blockSize, int _roomGap, int _hallSize){             //Creates randomly generated level
    
    map = new LevelMap(_segSizes, _circleSizes, _roomGap, _hallSize);                                   //Generates rooms and hallways
    
    int lgCircleSize = _circleSizes.x;                                                                  //Clarification code: shows that the large circle is located at x within Vec3i
    
    Vec3f s = new Vec3f(map.rooms[0].loc.x, map.rooms[0].loc.y, map.rooms[0].size);                     //starts at first room circle, helps with calls and reference size
    levelSize = new Vec4f(s.x - (s.z / 2), s.y - (s.z / 2), s.x + (s.z / 2), s.y +  (s.z / 2));         //Is the Start location = to first Room
    
    for(int i = 0; i < map.rooms.length; i++){                                                          //Calculates bounds based on all Rooms
      
      Vec2f loc = new Vec2f(map.rooms[i].loc.x, map.rooms[i].loc.y);
      if(loc.x < levelSize.x){ levelSize.x = loc.x; }                                                   //Min X
      if(loc.y < levelSize.y){ levelSize.y = loc.y; }                                                   //Min Y
      if(loc.x > levelSize.w){ levelSize.w = loc.x; }                                                   //Max X
      if(loc.y > levelSize.h){ levelSize.h = loc.y; }                                                   //Max Y
    }
    
    levelSize.w -= levelSize.x;                                                                         //width = difference between max X and min X
    levelSize.h -= levelSize.y;                                                                         //height = difference between max Y and min Y
    
    levelSize =  new Vec4f(levelSize.x - lgCircleSize, levelSize.y - lgCircleSize, levelSize.w + (lgCircleSize * 2), levelSize.h + + (lgCircleSize * 2));  //Adds padding to the sides 
    
    blocks = new LevelBlocks(levelSize, _blockSize, map.allRooms, _hallSize, new Vec2f(levelSize.x, levelSize.y));  //Creates the blocks that form the barriers to the level
  }
  
  public void display(Vec2f _offset){
    
    blocks.display(_offset);
    //map.display(_offset); 
    //blocks.display(_offset);
  }
}
class LevelBlocks{

  Block[][] blocks;                                                             //2d array for all blocks
  int rows, cols;
  
  LevelBlocks(Vec4f _levelBounds, int _size, Room[] _allRooms, int _hallSize, Vec2f _levelLocation){
    
    float blockX = 0, blockY = 0;
    
    rows = PApplet.parseInt(_levelBounds.w) / _size;                                        //generate size of two dimensional array
    cols = PApplet.parseInt(_levelBounds.h) / _size;
    
    blocks = new Block[rows][cols];
    
    for(int i = 0; i < rows; i++){                                            //for(each row){ just grab cols... }
      for(int j = 0; j < cols; j++){                                          //for(each col){ just grab segment... }
        boolean isBlockVisible = true;
        for(int k = 0; k < _allRooms.length; k++){                            //grab room once, do calcs here
                                                                               
            Room room = _allRooms[k];                                         //term simplification AND block is not created
            
            blockX = _levelLocation.x + (_size * i);
            blockY = _levelLocation.y + (_size * j);
            
            float a = blockX - room.loc.x;                                    //calculate distance to room
            float b = blockY - room.loc.y;
            float distance = sqrt((a * a) + (b * b));
            
            if(room.size == _hallSize){
              if(distance < room.size){
                blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), false); 
                isBlockVisible = false;
                break;
              }
            }
            else if(distance < room.size / 2){ 
              blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), false); 
              isBlockVisible = false;
              break; 
            }
        }
        if(isBlockVisible){ blocks[i][j] = new Block(new Vec3f(blockX, blockY, PApplet.parseFloat(_size)), true); }
      }
    }
  }
  
  public void display(Vec2f _offset){
    for( int i = 0; i < rows; i++){
      for( int j = 0; j < cols; j++){
          blocks[i][j].display(_offset);
      }
    }
  }
  
}
class LevelMap{                                                                                 //Contains all Segments of level Generation

  Room[] rooms;
  Room[] allRooms;
  
  LevelMap(byte[] _segmentSizes, Vec3i _roomSizeSet, int _roomGap, int _hallSize){              //Will be used to make locations and room sizes. -> int[]_roomSizes, and Vec2f _locs
  
    byte totalSize = 0;                                                                         //Determine total size
    for(int i = 0; i < _segmentSizes.length; i++){ totalSize += _segmentSizes[i]; }
    rooms = new Room[totalSize];
    
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
      rooms[i] = new Room(new Vec2f(roomCandidate.x, roomCandidate.y), roomSize, PApplet.parseByte(0), color(255,255,255));
    }
    
    int totalHallRooms = 0;                                                                  //create Corridors
    for(int i = 0; i < rooms.length - 1; i++){                                               //Need total num of rooms based on distance between rooms for array initialization
      
      float a = rooms[i].loc.x - rooms[i + 1].loc.x;                                         //find distance
      float b = rooms[i].loc.y - rooms[i + 1].loc.y;
      float distance = sqrt((a * a) + (b * b));
      totalHallRooms += PApplet.parseInt(distance / _hallSize);
    }
    
    allRooms = new Room[totalHallRooms + totalSize];
    int allRoomsIndex = rooms.length;                                                        //Start at rooms.length b/c we load rooms into allRooms first
    
    for(int i = 0; i < rooms.length; i++){ allRooms[i] = rooms[i]; }                         //Load rooms into allRooms
    
    for(int i = 0; i < rooms.length - 1; i++){
      
      float a = rooms[i].loc.x - rooms[i + 1].loc.x;
      float b = rooms[i].loc.y - rooms[i + 1].loc.y;
      float distance = sqrt((a * a) + (b * b));
      int numHallRooms = PApplet.parseInt(distance / _hallSize);
      float subA = a / numHallRooms;
      float subB = b / numHallRooms;
      
      for(int j = 0; j < numHallRooms; j++){
      
        allRooms[allRoomsIndex] = new Room(new Vec2f(rooms[i].loc.x - (subA * j), rooms[i].loc.y - (subB * j)), _hallSize, PApplet.parseByte(0), color(255,255,255));
        allRoomsIndex++;
      
      }
    }
  }
  /*==========================================================================================================================================*/
  
  public Vec2f generateRoomLocation(float _thisRoomSize, Vec3f _lastRoom, int _gap){                              //Generates Location of new Room
    
    float distance = (_lastRoom.z / 2) + (_thisRoomSize / 2) + _gap;
    float x = random(-distance, distance);
    float y = sqrt((distance * distance) - (x * x));
    int randomY = PApplet.parseInt(random(2));
    if(randomY == 0){ y = -y; }
    
    return new Vec2f(_lastRoom.x + x, _lastRoom.y + y);
  }
  /*===========================================================================================================================================*/
  
  public boolean isInsideOtherRooms(Vec3f _candidate, int _roomIndex){
  
    if(_roomIndex == 0){ return false; }
    
    for(byte i = 0; i < _roomIndex; i++){
      float a = _candidate.y - rooms[i].loc.y;
      float b = _candidate.x - rooms[i].loc.x;
      float distance = sqrt((a * a) + (b * b));
      
      if(distance < ((rooms[i].size / 2) + (_candidate.z / 2))){ return true; }      //Add gap if shit hits the fan with rooms melding into each other.
    }
    return false;
  }
  /*==========================================================================================================================================*/
  
  public void display(Vec2f _offset){ for(int i = 1; i < allRooms.length; i++){ allRooms[i].display(_offset); } }
}
class Player{

  Timer tick;
  Vec2f loc;
  int size;
  float speed;
  int c;
  boolean isWPressed, isAPressed, isSPressed, isDPressed;
  
  Player(Vec2f _loc, int _size, int _c){
  
    tick = new Timer(1);                        //Faster timer, slower speed = SMOOTHER.
    tick.start();
    isDPressed = false;
    speed = 10;                                   //HARD CODED: replace with Pass-in
    loc = _loc;
    size = _size;
    c = _c;
  }
  
  public void tick(){
  
    if(tick.isFinished()){
    tick.start();
    
      if(isWPressed){loc.y -= speed; }
      if(isAPressed){loc.x -= speed; }
      if(isSPressed){loc.y += speed; }
      if(isDPressed){loc.x += speed; }
    }
  }
  
  public void display(Vec2f _offset){
  
    fill(c);
    ellipse(loc.x + _offset.x, loc.y + _offset.y, size,size);
    fill(255,255,255);
  }
  
  public void pressed(){
  
    if(key == 'w'){ isWPressed = true; }
    if(key == 'a'){ isAPressed = true; }
    if(key == 's'){ isSPressed = true; }
    if(key == 'd'){ isDPressed = true; }
  }
  
  public void released(){
  
    if(key == 'w'){ isWPressed = false; }
    if(key == 'a'){ isAPressed = false; }
    if(key == 's'){ isSPressed = false; }
    if(key == 'd'){ isDPressed = false; }
  }
}
class Room{

  Vec2f loc;
  float size;
  int c;
  Block[] allBlocks;
  
  Room(){ this(new Vec2f(0.0f,0.0f), 10, PApplet.parseByte(0), color(255,255,255)); }
  
  Room(Vec2f _loc, float _roomSize, byte _roomType, int _c){
  
    loc = _loc;
    size = _roomSize;
    c = _c; 
    allBlocks = new Block[1];
    
    switch(_roomType){
    
      case 0: break;    //Empty
      case 1: drawRoom1(); break;    
      default:  println("DEFAULT:: CLASS:: ROOM:: TYPE");
    
    }
    
  }
  
  public void display(Vec2f _offset){
    
    ellipse(loc.x + _offset.x, loc.y + _offset.y, size, size);
    
  }
  
  public void drawRoom1(){
    
    //Ellipse() size, size = width, height so basic drawing area = -1500, 1500 x and y
    Vec2f[] pts = {new Vec2f(-750, 1000), new Vec2f(-500, 100), new Vec2f(500, 100), new Vec2f(750,1000), new Vec2f(1000,1000), new Vec2f(100,-750), new Vec2f(100, -1250), new Vec2f(-100, -1250), new Vec2f(-100,750)};
    
    for(int i = 0; i < pts.length - 1; i++){
    
      //find distance between this point and next point
      
      float a = pts[i].x - pts[i + 1].x;
      float b = pts[i].y - pts[i + 1].y;
      float distance = sqrt((a * a) + (b * b));
      //numofblocks = distance / 30 (blocksize)
      //remainder = distance % 30 (blocksize)
      //numofaddagetoblock = remainder / numofBlocks
      //newblocksize = blocksize + numofaddagetoblock
      //numofblocks = distance / newblocksize
      
      //re initialize array
        //Block[] blocks = new Block[numofblocks];
      
      //create array
      
      
      //after calculations: merge array
        //Block[] temp = newBlock[allBlocks.length + blocks.length];
      
      //2 for loops putting both arraays into it
      //re-assign allBlocks from temp array
      
        //for(int j = 0; j < numofblocks; i++){
      
        
      
      //}
    }
    
    
  
  }

}
class Timer{                                                    /* Millis() returns how many milliseconds the program has been running.  There are 1000 Milliseconds in a second*/

  int timerStart;                                               //When Start
  int timerDuration;                                            //How long timer is
  
  Timer(int _timerDuration){ timerDuration = _timerDuration; }  //constructor holds amount of time
  
  public void start(){ timerStart = millis(); }                        //Timestamp of how many milliseconds have gone by.

  public boolean isFinished(){                                         //Checks to see if enough Millis have gone by
  
    int passedTime = millis() - timerStart;  
    
    if(passedTime > timerDuration) { return true; } 
    else { return false; }
    
  }
}
class Vec2f{

  float x, y;
  
  Vec2f(float _x, float _y){ x = _x; y = _y; }

}

class Vec3f{

  float x, y, z;
  
  Vec3f(Vec2f _loc, float _z){ this(_loc.x, _loc.y, _z); }
  
  Vec3f(float _x, float _y, float _z){ x = _x; y = _y; z = _z; }

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
