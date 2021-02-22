class Player{

  Timer tick;
  Vec2f loc;
  int size;
  float speed;
  color c;
  boolean isWPressed, isAPressed, isSPressed, isDPressed;
  
  Player(Vec2f _loc, int _size, color _c){
  
    tick = new Timer(1);                        //Faster timer, slower speed = SMOOTHER.
    tick.start();
    isDPressed = false;
    speed = 10;                                   //HARD CODED: replace with Pass-in
    loc = _loc;
    size = _size;
    c = _c;
  }
  
  void tick(){
  
    if(tick.isFinished()){
    tick.start();
    
      if(isWPressed){loc.y -= speed; }
      if(isAPressed){loc.x -= speed; }
      if(isSPressed){loc.y += speed; }
      if(isDPressed){loc.x += speed; }
    }
  }
  
  void display(Vec2f _offset){
  
    fill(c);
    ellipse(loc.x + _offset.x, loc.y + _offset.y, size,size);
    fill(255,255,255);
  }
  
  void pressed(){
  
    if(key == 'w'){ isWPressed = true; }
    if(key == 'a'){ isAPressed = true; }
    if(key == 's'){ isSPressed = true; }
    if(key == 'd'){ isDPressed = true; }
  }
  
  void released(){
  
    if(key == 'w'){ isWPressed = false; }
    if(key == 'a'){ isAPressed = false; }
    if(key == 's'){ isSPressed = false; }
    if(key == 'd'){ isDPressed = false; }
  }
}
