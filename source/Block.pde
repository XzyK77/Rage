class Block{                                                        //Used as building blocks to generate walls of level

  Vec2f loc;
  int size;
  boolean isVisible;

  Block(Vec3f _v, boolean _isVisible){ 
    loc = new Vec2f(_v.x, _v.y);
    size = int(_v.z);
    isVisible = _isVisible;
  }
  
  void display(Vec2f _offset){ 
    if(getIsInViewPort(_offset)){
      if(isVisible){
        rect(loc.x + _offset.x, loc.y + _offset.y, size, size); 
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
