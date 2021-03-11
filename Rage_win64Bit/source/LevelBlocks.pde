class LevelBlocks{                                                                        //Generates all blocks that form the level's walls

  Block[][] blocks;                                                                       //2d array for all blocks
  int rows, cols;
  
  LevelBlocks(Vec4f _levelBounds, int _size, Room[] _allRooms, int _hallSize, Vec2f _levelLocation, PImage _sprite){
    
    float blockX = 0, blockY = 0;
    
    rows = int(_levelBounds.w) / _size;                                                    //generate size of two dimensional array
    cols = int(_levelBounds.h) / _size;
    
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
                blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), false, _sprite);  //-> creating the block and making it invisible avoids NullPointers and allows display() to later ignore the draw call.
                isBlockVisible = false;
                break;
              }
            }
            else if(distance < room.size / 2){                                             //if dealing with the regular circles, do calculations as normal
              blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), false, _sprite); 
              isBlockVisible = false;
              break; 
            }
        }
        if(isBlockVisible){ blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), true, _sprite); }
      }
    }
  }
  
  void display(Vec2f _offset){
    for( int i = 0; i < rows; i++){
      for( int j = 0; j < cols; j++){
          blocks[i][j].displayWithResize(_offset, false);
      }
    }
  }
  
}
