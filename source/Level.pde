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
  
  void display(Vec2f _offset){
    
    blocks.display(_offset);
    //map.display(_offset); 
    //blocks.display(_offset);
  }
}
