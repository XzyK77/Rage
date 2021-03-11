import processing.sound.*;

LevelMap levelMap;
LevelBounds levelBounds;
LevelBlocks levelBlocks;
LevelOrbs levelOrbs;
Player player;
ViewPort viewPort;
SoundFile bgSong;

void settings(){ fullScreen(); }                                                         //Better instead of size!

void setup(){

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
  bgSong.amp(.1);
  bgSong.play();

}

void draw(){

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

void keyPressed(){ player.pressed(); }
void keyReleased(){ player.released(); }
void mousePressed(){ player.mousePush(); }
void mouseReleased(){ player.mouseNotPush(); }                                        //  :P
