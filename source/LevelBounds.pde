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
