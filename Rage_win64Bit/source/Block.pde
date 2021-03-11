class Block{                                                        //Used as building blocks to generate walls of level

  Vec2f loc;
  int size;
  boolean isVisible;
  String blockSpriteFilePath;
  PImage sprite;

  Block(Vec3f _v, boolean _isVisible, PImage _sprite){ 
    loc = new Vec2f(_v.x, _v.y);
    size = int(_v.z);
    isVisible = _isVisible;
    sprite = _sprite;
  }
  
  void displayWithResize(Vec2f _offset, boolean _isResized){
    if(getIsInViewPort(_offset)){
      if(isVisible){
        if(_isResized){ image(sprite, loc.x + _offset.x, loc.y + _offset.y, size, size);}
        else{ image(sprite, loc.x + _offset.x, loc.y + _offset.y); } 
      }
    }
  }
  
  boolean getIsInViewPort(Vec2f _offset){
  
    if(((loc.x + _offset.x) > - size) && ((loc.x + _offset.x) < width) && ((loc.y + _offset.y) > - size) && ((loc.y + _offset.y) < height)){
      return true;
    }
    return false;
  }
}
