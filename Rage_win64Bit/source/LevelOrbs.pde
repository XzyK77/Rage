class LevelOrbs{                                                                        //Object used to decrease Player's rage amount
  
  Orb[] orbs;

  LevelOrbs(Room[] _rooms, Vec2f _offset, int _size, PImage _spriteFilePath, boolean _isVisible){
    
    orbs = new Orb[_rooms.length];                                                      
  
    for(int i = 0; i < orbs.length; i++){                                                //Creates one orb per room at that rooms location with the given offset
    
      orbs[i] = new Orb(new Vec2f(_rooms[i].loc.x + _offset.x, _rooms[i].loc.y + _offset.y), _size, _spriteFilePath, _isVisible);
    }
  }
  
  void checkTimers(){ for(int i = 0; i < orbs.length; i++){ orbs[i].checkTimer(); } }    //Increments the animation frames of each orb
  
  int checkCollision(Player _player){
  
    for(int i = 0; i < orbs.length; i++){
      
      if(orbs[i].checkCollision(_player)){ 
        
        if(i == orbs.length - 1){ return 2; }                                            //2 = orb at end of level
        return 1;                                                                        //1 = all other orbs
      }
    }
    return 0;                                                                            //no Collision
  }
  
  void display(Vec2f _offset){
    for (int i = 0; i < orbs.length; i++){ orbs[i].display( _offset); }
  }
}
