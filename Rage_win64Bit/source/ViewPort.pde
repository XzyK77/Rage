class ViewPort{

  Vec2f screenAnchorLoc, offset;
  
  ViewPort(Vec2f _screenAnchorLoc){
  
    screenAnchorLoc = _screenAnchorLoc;
    offset = new Vec2f(0.0,0.0);
  }
  
  Vec2f getOffset(Vec2f _centeringLoc){
  
    offset.x = screenAnchorLoc.x - _centeringLoc.x;
    offset.y = screenAnchorLoc.y - _centeringLoc.y;
    return offset;
  }
}
