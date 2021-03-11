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
  
  void checkTimer(){
  
    if(animationTick.isFinished()){ spriteTileX++; println(spriteTileX);}
    if(spriteTileX == 4){ spriteTileX = 0; }
  
  }
  
  boolean checkCollision(Player _player){
  
    
    if((Tri.getC(_player.loc.x - loc.x, _player.loc.y - loc.y) <= ((_player.size / 2) + size / 2)) && (isVisible)){ isVisible = false; return true; }//set to invisible and return true
    return false;
  
  }
  
  void display(Vec2f _offset){
  
    sprite = orbSprite.get(size * spriteTileX, 0, size, size); //gtg
    
    if(isVisible){
        image(sprite, loc.x + _offset.x, loc.y + _offset.y, size, size);
    }
  }
  
}
