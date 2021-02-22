Level level;
Player player;
ViewPort viewPort;

void settings(){ fullScreen(); }                                                    //Better instead of size?

void setup(){

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

void draw(){

  player.tick();                                                                    //Analyses player state and applies time based functions
  Vec2f viewOffset = viewPort.getOffset(player.loc);                                //Grab offset for passing into display functions
  
  background(255,255,255);                                                          //Start with white Background
  level.display(viewOffset);
  player.display(viewOffset);

}

void keyPressed(){ player.pressed(); }
void keyReleased(){ player.released(); }
