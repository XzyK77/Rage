class LevelBlocks{

  Block[][] blocks;                                                             //2d array for all blocks
  int rows, cols;
  
  LevelBlocks(Vec4f _levelBounds, int _size, Room[] _allRooms, int _hallSize, Vec2f _levelLocation){
    
    float blockX = 0, blockY = 0;
    
    rows = int(_levelBounds.w) / _size;                                        //generate size of two dimensional array
    cols = int(_levelBounds.h) / _size;
    
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
                blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), false); 
                isBlockVisible = false;
                break;
              }
            }
            else if(distance < room.size / 2){ 
              blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), false); 
              isBlockVisible = false;
              break; 
            }
        }
        if(isBlockVisible){ blocks[i][j] = new Block(new Vec3f(blockX, blockY, float(_size)), true); }
      }
    }
  }
  
  void display(Vec2f _offset){
    for( int i = 0; i < rows; i++){
      for( int j = 0; j < cols; j++){
          blocks[i][j].display(_offset);
      }
    }
  }
  
}
